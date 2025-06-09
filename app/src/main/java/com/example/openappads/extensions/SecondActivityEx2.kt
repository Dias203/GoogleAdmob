package com.example.openappads.extensions

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.admob.interstitial.InterstitialAdmobListener
import com.example.openappads.screens.MainActivity
import com.example.openappads.screens.SecondActivity
import com.example.openappads.utils.CountDownTimer


fun SecondActivity.setOnClick() {
    binding.icBack.setOnClickListener {
        val intentAd = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        showInterstitialAd(intentAd)
    }
}

fun SecondActivity.listenerInterstitialAd(intent: Intent){
    interstitialAd.listener = object : InterstitialAdmobListener {
        override fun onAdDismiss() {
            interstitialAd.preloadInterstitialAd()
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

        override fun onShowed() {
            TODO("Not yet implemented")
        }
    }
}

fun SecondActivity.showInterstitialAd(intent: Intent) {
    setLoadingState(true)
    isAdRequest = true
    admobOpenAppManager.locked()
    listenerInterstitialAd(intent)

    if (interstitialAd.isAdReady()) {
        // TH1: Ad loaded → timeout 1s rồi show
        ECOLog.showLog("TRƯỜNG HỢP 1")
        startTimeout(1) {
            interstitialAd.showAd(this)
            isAdRequest = false
            showToast("Hiển thị thành công khi loaded timeout 1s")
            hideIfNotShowing()
        }
    } else if (interstitialAd.isLoading()) {
        admobOpenAppManager.locked()
        ECOLog.showLog("TRƯỜNG HỢP 2")
        // TH2: đang loading → timeout 3s, nếu kịp thì show
        startTimeout(3) {
            if (interstitialAd.isAdReady()) {
                admobOpenAppManager.locked()
                ECOLog.showLog( "TH2 THÀNH CÔNG " + interstitialAd.isAdReady())
                showToast("Hiển thị thành công khi loading timeout 3s")
                interstitialAd.showAd(this)
                isAdRequest = false
            } else {
                ECOLog.showLog( "TH2 THẤT BẠI " + interstitialAd.isAdReady())
                showToast("Tải quảng cáo thất bại")
                hideIfNotShowing()
            }
        }
    } else {
        // TH3: fail → tiếp tục
        ECOLog.showLog("TRƯỜNG HỢP 2")
        showToast("Không thể tải quảng cáo!")
        startActivity(intent)
        hideIfNotShowing()
    }
}

fun SecondActivity.startTimeout(timeoutSec: Int, onComplete: () -> Unit) {
    countDownTimer.setProcessTimeSecond(timeoutSec)
    countDownTimer.startJob(object : CountDownTimer.UpdateProgress {
        override fun onUpdateProgress(count: Int) {
            if (countDownTimer.isProgressMax()) {
                onComplete()
            }
        }
    })
}

fun SecondActivity.hideIfNotShowing() {
    setLoadingState(false)
    countDownTimer.stopJob()
}

private fun SecondActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun SecondActivity.loadAdMob() {
    bannerAd.load(binding.adBannerContainer, true)
    interstitialAd.preloadInterstitialAd()
    nativeAd.loadNativeAd(binding.adFrame)
}

fun SecondActivity.progressUpdatedInterstitial(){
    if (countDownTimer.isProgressMax()) {
        if (interstitialAd.isAdReady()) {
            interstitialAd.showAd(this)
            isAdRequest = false
        } else {
            ECOLog.showLog("Quảng cáo chưa sẵn sàng!")
            countDownTimer.stopJob()
        }
        return
    }
    if (interstitialAd.isAdReady()) {
        countDownTimer.stopJob()
        interstitialAd.showAd(this)
        isAdRequest= false
    }
}


fun SecondActivity.onActivityDestroyed() {
    countDownTimer.destroy()
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