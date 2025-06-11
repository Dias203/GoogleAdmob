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
import com.example.openappads.utils.CountDownTimer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


fun SecondActivity.setOnClick() {
    binding.icBack.setOnClickListener {
        admobOpenAppManager.locked()
        interstitialAd.preloadInterstitialAd()
        if(!interstitialAd.finishCoolOffTime()) {
            openMainActivity()
        } else {
            setLoadingState(true)
            registerListenerInterstitial()
            showAdWithTimeout(6, interstitialAd) {
                setLoadingState(false)
                if (interstitialAd.isAdReady()) {
                    interstitialAd.showAd(this)
                } else {
                    openMainActivity()
                }
            }
        }
    }
}

fun SecondActivity.openMainActivity(){
    val intentAd = Intent(this, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
    startActivity(intentAd)
}

fun SecondActivity.registerListenerInterstitial(){
    interstitialAd.listener = object : InterstitialAdmobListener {
        override fun onAdDismiss() {
            interstitialAd.preloadInterstitialAd()
            openMainActivity()
            setLoadingState(false)
            admobOpenAppManager.unlock()
        }

        override fun onAdLoaded() {}
        override fun onFailedAdLoad(error: String) {
            openMainActivity()
            setLoadingState(false)
        }

        override fun onFailedToShow(error: String) {
            openMainActivity()            }

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
    nativeAd.loadNativeAd(binding.adFrame)
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


fun SecondActivity.showAdWithTimeout(
    seconds: Int,
    condition: Any,
    onComplete: () -> Unit
) {
    ECOLog.showLog("Come here")
    var progress = 0
    val delayTime = ((seconds * 1000) / 100).toLong()
    var isCompleted = false
    var job: Job? = null

    job = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            ECOLog.showLog("Come here - 2")

            while (isActive && progress <= 100 && !isCompleted) {
                ECOLog.showLog("Come here - 3, progress: $progress")
                val isReady = when (condition) {
                    is AdmobReward -> condition.isAdReady() || condition.isAdError()
                    is AdmobRewardInterstitial -> condition.isAdReady() || condition.isError()
                    is AdmobInterstitial -> condition.isAdReady() || condition.isError()
                    else -> false
                }
                // Loaded -> timeout 1s
                if (isReady && progress >= 20) {
                    ECOLog.showLog("Come here - 4: Ad ready, showing ad")
                    isCompleted = true
                    onComplete()
                    break
                }

                // Loading -> timeout 3s
                if (!isReady && progress >= 50) {
                    ECOLog.showLog("Come here - Loading timeout 3s, continue flow")
                    isCompleted = true
                    onComplete()
                    break
                }

                delay(delayTime)
                progress++
            }

            if (!isCompleted) {
                ECOLog.showLog("Come here - 5: Total timeout, continue flow")
                onComplete()
            }
        }
        job?.cancel()
    }
}