package com.example.openappads.extensions

import android.content.Intent
import android.graphics.Color
import android.view.View
import com.example.openappads.admob.interstitial.InterstitialAdmobListener
import com.example.openappads.screens.MainActivity
import com.example.openappads.screens.SecondActivity


fun SecondActivity.setOnClick() {
    binding.icBack.setOnClickListener {
        setLoadingState(true)
        admobOpenAppManager.locked()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        interstitialAd.listener = object : InterstitialAdmobListener {
            override fun onAdDismiss() {
                startActivity(intent)
                setLoadingState(false)
                admobOpenAppManager.unlock()
            }

            override fun onAdLoaded() {}
            override fun onFailedAdLoad(error: String) {
                startActivity(intent)
                setLoadingState(false)
            }

            override fun onFailedToShow(error: String) {
                startActivity(intent)            }
        }
        interstitialAd.showInterstitialAd(this)
    }
}

fun SecondActivity.loadAdMob() {
    bannerAd.load(binding.adBannerContainer, true)
    countDownTimer.setProcessTimeSecond(3)
    interstitialAd.preloadInterstitialAd()
    nativeAd.loadNativeAd(binding.adFrame)
}




fun SecondActivity.onActivityPaused() {
    interstitialAd.setActivityPaused(true)
}

fun SecondActivity.onActivityResumed() {
    if(!interstitialAd.setActivityPaused() /*&& interstitialAd.hasPendingShowRequest()*/ && interstitialAd.isAdReady()) {
        interstitialAd.showInterstitialAd(this)
    }
}

fun SecondActivity.onActivityDestroyed() {
    // Cleanup để tránh memory leak
    interstitialAd.cleanup()
    nativeAd.onDestroy()
    bannerAd.onDestroy()
}


fun SecondActivity.setLoadingState(isLoading: Boolean) {
    binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    binding.bgSecond.setBackgroundColor(
        if (isLoading) Color.parseColor("#8C8B8B") else Color.WHITE
    )
}