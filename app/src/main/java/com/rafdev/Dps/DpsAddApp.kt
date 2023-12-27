package com.rafdev.Dps

import android.app.Application
import com.google.android.gms.ads.MobileAds


class DpsAddApp: Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}