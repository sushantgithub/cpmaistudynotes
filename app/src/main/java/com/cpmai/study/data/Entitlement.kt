package com.cpmai.study.data

import java.security.MessageDigest

object Entitlement {
    val freeTopicIds = setOf("core", "p1")
    const val priceLabel = "₹499"
    const val productName = "Full version"

    fun topicAllowed(topicId: String?, unlocked: Boolean): Boolean {
        if (unlocked) return true
        if (topicId.isNullOrBlank() || topicId == "daily") return unlocked
        return topicId in freeTopicIds
    }
}

object LicenseKeys {
    private const val SECRET = "cpmai-prep-unofficial-2026-key"

    fun normalize(raw: String): String =
        raw.trim().uppercase().replace(" ", "").replace("_", "-")

    fun isValid(raw: String): Boolean {
        val code = normalize(raw)
        val parts = code.split("-")
        if (parts.size != 3 || parts[0] != "PREP") return false
        val body = parts[1]
        val check = parts[2]
        if (body.length != 6 || check.length != 4) return false
        if (!body.all { it in "0123456789ABCDEF" }) return false
        return checksum(body) == check
    }

    fun checksum(body: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hex = md.digest((body.uppercase() + SECRET).toByteArray())
            .joinToString("") { "%02x".format(it) }
        return hex.substring(0, 4).uppercase()
    }
}
