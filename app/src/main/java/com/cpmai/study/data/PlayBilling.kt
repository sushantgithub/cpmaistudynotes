package com.cpmai.study.data

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.cpmai.study.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayBilling(
    context: Context,
    private val progress: ProgressStore
) : PurchasesUpdatedListener {

    companion object {
        const val PRODUCT_ID = "full_unlock"
    }

    private val _priceLabel = MutableStateFlow(Entitlement.priceLabel)
    val priceLabel = _priceLabel.asStateFlow()
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()
    private val _ready = MutableStateFlow(false)
    val ready = _ready.asStateFlow()

    private var productDetails: com.android.billingclient.api.ProductDetails? = null

    private val client: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    fun start() {
        if (!BuildConfig.USE_PLAY_BILLING) return
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    _ready.value = true
                    queryProduct()
                    restore()
                } else {
                    _message.value = "Google Play Billing is not available on this install."
                }
            }

            override fun onBillingServiceDisconnected() {
                _ready.value = false
            }
        })
    }

    fun restore() {
        if (!BuildConfig.USE_PLAY_BILLING) return
        if (!client.isReady) return
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        client.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { handlePurchase(it) }
            }
        }
    }

    fun buy(activity: Activity) {
        if (!BuildConfig.USE_PLAY_BILLING) return
        val details = productDetails
        if (details == null) {
            _message.value = "Create in-app product \"$PRODUCT_ID\" in Play Console, then try again from a Play Store install."
            queryProduct()
            return
        }
        val flow = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .build()
                )
            )
            .build()
        client.launchBillingFlow(activity, flow)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> purchases?.forEach { handlePurchase(it) }
            BillingClient.BillingResponseCode.USER_CANCELED -> _message.value = "Purchase canceled."
            else -> _message.value = result.debugMessage.ifBlank { "Purchase could not be completed." }
        }
    }

    private fun queryProduct() {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PRODUCT_ID)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()
        client.queryProductDetailsAsync(params) { result, list ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryProductDetailsAsync
            val details = list.firstOrNull() ?: return@queryProductDetailsAsync
            productDetails = details
            val formatted = details.oneTimePurchaseOfferDetails?.formattedPrice
            if (!formatted.isNullOrBlank()) {
                _priceLabel.value = formatted
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (!purchase.products.contains(PRODUCT_ID)) return
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (!purchase.isAcknowledged) {
            val ack = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            client.acknowledgePurchase(ack) { }
        }
        progress.unlockFull()
        _message.value = "Unlocked. Thank you."
    }
}
