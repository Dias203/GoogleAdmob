package com.example.openappads.extensions

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.admob.banner.BannerListener
import com.example.openappads.admob.reward.RewardListener
import com.example.openappads.admob.reward_interstitial.RewardInterstitialAdmobListener
import com.example.openappads.screens.MainActivity
import com.example.openappads.screens.SecondActivity
import com.example.openappads.utils.CountDownTimer

fun MainActivity.setOnClick() {
    binding.apply {

        admobRewardVideoButton.setOnClickListener { handleRewardVideoClick() }
        openSecondActivity.setOnClickListener { handleOpenSecondActivityClick() }
    }
}



private fun MainActivity.handleRewardVideoClick() {
    showDialogReward()
}

fun MainActivity.showRewardAd(){
    setLoadingState(true)
    admobOpenAppManager.locked()
    listenerRewardAd()

    isAdRequest = true
    setLoadingState(true)
    admobOpenAppManager.locked()

    if (rewardAd.isAdReady()) {
        //admobOpenAppManager.locked()
        // TH1: Ad loaded → timeout 1s rồi show
        ECOLog.showLog("Truong hop 1")
        startTimeout(1) {
            rewardAd.showAd(this)
            isAdRequest = false
            showToast("Hiển thị thành công khi loaded timeout 1s")
            hideIfNotShowing()
        }
    } else if (rewardAd.isLoading()) {
        admobOpenAppManager.locked()
        ECOLog.showLog("Truong hop 2")
        // TH2: đang loading → timeout 3s, nếu kịp thì show
        startTimeout(3) {
            if (rewardAd.isAdReady()) {
                admobOpenAppManager.locked()
                ECOLog.showLog( "TH2 THANH CONG " + rewardAd.isAdReady())
                showToast("Hiển thị thành công khi loading timeout 3s")
                rewardAd.showAd(this)
                isAdRequest = false
            } else {
                ECOLog.showLog( "TH2 THAT BAI " + rewardAd.isAdReady())
                showToast("Tải quảng cáo thất bại")
                hideIfNotShowing()
            }
        }
    } else {
        // TH3: fail → tiếp tục
        showToast("Không thể tải quảng cáo!")
        hideIfNotShowing()
    }
}

private fun MainActivity.listenerRewardAd() {
    rewardAd.listener = object : RewardListener {
        override fun onAdLoaded() {}

        override fun onAdFailedToLoad(error: String) {
            setLoadingState(false)
            showToast("Không thể tải quảng cáo!")
            admobOpenAppManager.unlock()
        }

        override fun onAdDismiss() {
            setLoadingState(false)
            showToast("Mở khóa thành công!")
            rewardAd.preloadRewardAd()
            admobOpenAppManager.unlock()
        }

        override fun onFailedToShow(error: String) {
            setLoadingState(false)
            showToast("Không thể hiển thị quảng cáo!")
            admobOpenAppManager.unlock()
        }

        override fun onShowed() {
            TODO("Not yet implemented")
        }
    }
}

fun MainActivity.hideIfNotShowing() {
    setLoadingState(false)
    countDownTimer.stopJob()
}

fun MainActivity.showDialogReward() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Mở khóa bộ lọc ảnh")
    builder.setMessage("Bạn có muốn xem quảng cáo để nhận một lượt mở khóa bộ lọc ảnh?")

    builder.setPositiveButton("Xem AD") { _, _ ->
        showRewardAd()
    }

    builder.setNegativeButton("Hủy") { dialog, _ ->
        dialog.dismiss()
    }

    val dialog = builder.create()
    dialog.show()
}

private fun MainActivity.handleOpenSecondActivityClick() {
    val intentAd = Intent(this, SecondActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
    showRewardInterstitial(intentAd)
}

fun MainActivity.showRewardInterstitial(intent: Intent){
    isAdRequest = true
    setLoadingState(true)
    admobOpenAppManager.locked()
    rewardInterstitialAd.listener = object : RewardInterstitialAdmobListener {
        override fun onAdDismiss() {
            startActivity(intent)
            setLoadingState(false)
            rewardInterstitialAd.preloadRewardIntersAd()
            admobOpenAppManager.unlock()
        }

        override fun onAdLoaded() {}

        override fun onFailedAdLoad(error: String) {
            startActivity(intent)
            setLoadingState(false)
            admobOpenAppManager.unlock()
        }

        override fun onFailedToShow(error: String) {
            startActivity(intent)
            setLoadingState(false)
            admobOpenAppManager.unlock()
        }

        override fun onShowed() {
            TODO("Not yet implemented")
        }
    }

    if (rewardInterstitialAd.isAdReady()) {
        // TH1: Ad loaded → timeout 1s rồi show
        ECOLog.showLog("Truong hop 1")
        startTimeout(1) {
            rewardInterstitialAd.showAd(this)
            isAdRequest = false
            showToast("Hiển thị thành công khi loaded timeout 1s")
            hideIfNotShowing()
        }
    } else if (rewardInterstitialAd.isLoading()) {
        admobOpenAppManager.locked()
        ECOLog.showLog("Truong hop 2")
        // TH2: đang loading → timeout 3s, nếu kịp thì show
        startTimeout(3) {
            if (rewardInterstitialAd.isAdReady()) {
                admobOpenAppManager.locked()
                ECOLog.showLog( "TH2 THANH CONG " + rewardInterstitialAd.isAdReady())
                showToast("Hiển thị thành công khi loading timeout 3s")
                rewardInterstitialAd.showAd(this)
                isAdRequest = false
            } else {
                ECOLog.showLog( "TH2 THAT BAI " + rewardInterstitialAd.isAdReady())
                showToast("Tải quảng cáo thất bại")
                hideIfNotShowing()
                startActivity(intent)
            }
        }
    } else {
        // TH3: fail → tiếp tục
        showToast("Không thể tải quảng cáo!")
        hideIfNotShowing()
        startActivity(intent)
    }
}

fun MainActivity.loadAdMob() {
    binding.adBannerContainer.post {
        bannerAd.loadInlineBanner(
            binding.adBannerContainer,
            binding.adBannerContainer.width,
            binding.adBannerContainer.height
        )
        bannerAd.listener = object : BannerListener {
            override fun onLoad() {
                binding.loadingProgressBarBanner.visibility = View.GONE
            }

            override fun onLoadFail(error: String) {
                TODO("Not yet implemented")
            }

        }
    }
    // Preload reward ad
    rewardAd.preloadRewardAd()
    rewardInterstitialAd.preloadRewardIntersAd()
}

fun MainActivity.startTimeout(timeoutSec: Int, onComplete: () -> Unit) {
    countDownTimer.setProcessTimeSecond(timeoutSec)
    countDownTimer.startJob(object : CountDownTimer.UpdateProgress {
        override fun onUpdateProgress(count: Int) {
            if (countDownTimer.isProgressMax()) {
                onComplete()
            }
        }
    })
}


fun MainActivity.progressUpdatedReward(){
    if (countDownTimer.isProgressMax()) {
        if (rewardAd.isAdReady()) {
            rewardAd.showAd(this)
            isAdRequest = false
        } else {
            ECOLog.showLog("Quảng cáo chưa sẵn sàng!")
            countDownTimer.stopJob()
        }
        return
    }
    if (rewardAd.isAdReady()) {
        countDownTimer.stopJob()
        rewardAd.showAd(this)
        isAdRequest= false
    }
}

fun MainActivity.progressUpdatedRewardInterstitial(){
    if (countDownTimer.isProgressMax()) {
        if (rewardInterstitialAd.isAdReady()) {
            rewardInterstitialAd.showAd(this)
            isAdRequest = false
        } else {
            ECOLog.showLog("Quảng cáo chưa sẵn sàng!")
            countDownTimer.stopJob()
        }
        return
    }
    if (rewardInterstitialAd.isAdReady()) {
        countDownTimer.stopJob()
        rewardAd.showAd(this)
        isAdRequest= false
    }
}


fun MainActivity.onActivityDestroyed() {
    rewardAd.destroyAd()
    rewardInterstitialAd.destroyAd()
    bannerAd.onDestroy()
}

private fun MainActivity.setLoadingState(isLoading: Boolean) {

    binding.apply {
        loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        bgMain.setBackgroundColor(
            if (isLoading) Color.parseColor("#8C8B8B") else Color.WHITE
        )
        admobRewardVideoButton.isEnabled = !isLoading
        openSecondActivity.isEnabled = !isLoading
    }
}

private fun MainActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
