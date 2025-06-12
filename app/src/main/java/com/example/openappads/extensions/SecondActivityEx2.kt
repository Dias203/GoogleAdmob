package com.example.openappads.extensions

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.admob.interstitial.AdmobInterstitial
import com.example.openappads.admob.interstitial.InterstitialAdmobListener
import com.example.openappads.admob.reward.AdmobReward
import com.example.openappads.admob.reward_interstitial.AdmobRewardInterstitial
import com.example.openappads.screens.MainActivity
import com.example.openappads.screens.SecondActivity
import com.example.openappads.utils.CoolOffTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


fun SecondActivity.setOnClick() {
    binding.icBack.setOnClickListener {
        admobOpenAppManager.locked()
        interstitialAd.preloadInterstitialAd()
        if (!interstitialAd.finishCoolOffTime()) {
            openMainActivity()
        } else {
            //setLoadingState(true)
            dialogAdsFullScreen.showDialog()
            registerListenerInterstitial()
            showAdWithTimeout(6, interstitialAd) {
                //setLoadingState(false)
                dialogAdsFullScreen.hideDialog()
                if (interstitialAd.isAdReady()) {
                    interstitialAd.showAd(this)
                } else {
                    openMainActivity()
                }
            }
        }
    }
}

fun SecondActivity.openMainActivity() {
    val intentAd = Intent(this, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
    startActivity(intentAd)
}

fun SecondActivity.registerListenerInterstitial() {
    interstitialAd.listener = object : InterstitialAdmobListener {
        override fun onAdDismiss() {
            interstitialAd.preloadInterstitialAd()
            openMainActivity()
            dialogAdsFullScreen.hideDialog()
            admobOpenAppManager.unlock()
        }

        override fun onAdLoaded() {}
        override fun onFailedAdLoad(error: String) {
            openMainActivity()
            dialogAdsFullScreen.hideDialog()
        }

        override fun onFailedToShow(error: String) {
            openMainActivity()
        }

        override fun onShowed() {
            TODO("Not yet implemented")
        }
    }
}

private fun SecondActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun SecondActivity.loadAdMob() {
    bannerAd.load(binding.adBannerContainer, true)
    interstitialAd.preloadInterstitialAd()
    nativeAd.loadNativeAd { ad ->
        binding.adFrame.loaded(ad)
    }
}

fun SecondActivity.onActivityDestroyed() {
    interstitialAd.destroyAd()
    nativeAd.onDestroy()
    bannerAd.onDestroy()
}


fun SecondActivity.setLoadingState(isLoading: Boolean) {
    binding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    binding.bgSecond.setBackgroundColor(
        if (isLoading) Color.parseColor("#8C8B8B") else Color.WHITE
    )
}