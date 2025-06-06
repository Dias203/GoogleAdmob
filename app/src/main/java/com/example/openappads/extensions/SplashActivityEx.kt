package com.example.openappads.extensions

import android.content.Intent
import com.example.openappads.admob.openapp.OpenAppAdListener
import com.example.openappads.constants.admob.ADS_OPEN_APP_UNIT_ID_SPLASH
import com.example.openappads.screens.MainActivity
import com.example.openappads.screens.SplashActivity

fun SplashActivity.loadAppOpenAdSplash(){
    admobAppOpen.listener = object : OpenAppAdListener{
        override fun onAdLoaded() {}

        override fun onFailedAdLoad(errorMessage: String) {}

        override fun onAdDismiss() {}

    }
    admobAppOpen.setAdUnitId(ADS_OPEN_APP_UNIT_ID_SPLASH)
    admobAppOpen.loadAd()
}

fun SplashActivity.startMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
    coroutineCountDown.destroy()
}