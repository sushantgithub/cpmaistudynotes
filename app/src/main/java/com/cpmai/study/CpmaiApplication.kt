package com.cpmai.study

import android.app.Application
import com.cpmai.study.data.ContentRepository
import com.cpmai.study.data.PlayBilling
import com.cpmai.study.data.ProgressStore

class CpmaiApplication : Application() {
    lateinit var repository: ContentRepository
        private set
    lateinit var progress: ProgressStore
        private set
    lateinit var billing: PlayBilling
        private set

    override fun onCreate() {
        super.onCreate()
        repository = ContentRepository(this)
        progress = ProgressStore(this)
        billing = PlayBilling(this, progress)
        billing.start()
    }
}
