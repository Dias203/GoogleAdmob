package com.example.openappads.extensions

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.admob.banner.BannerListener
import com.example.openappads.admob.interstitial.AdmobInterstitial
import com.example.openappads.admob.reward.AdmobReward
import com.example.openappads.admob.reward.RewardListener
import com.example.openappads.admob.reward_interstitial.AdmobRewardInterstitial
import com.example.openappads.admob.reward_interstitial.RewardInterstitialAdmobListener
import com.example.openappads.screens.MainActivity
import com.example.openappads.screens.SecondActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun MainActivity.setOnClick() {
    binding.apply {
        admobRewardVideoButton.setOnClickListener {
            handleRewardVideoClick()
        }

        openSecondActivity.setOnClickListener {
            rewardInterstitialAd.preloadRewardIntersAd()
            admobOpenAppManager.locked()
            if(!rewardInterstitialAd.finishCoolOffTime()){
                openSecondActivity()
            }
            else {
                setLoadingState(true)
                registerListenerRewardInters()
                showAdWithTimeout(6, rewardInterstitialAd) {
                    setLoadingState(false)
                    if (rewardInterstitialAd.isAdReady()) {
                        rewardInterstitialAd.showAd(this@setOnClick)
                    } else {
                        openSecondActivity()
                        admobOpenAppManager.unlock()
                    }
                }
            }
        }
    }
}

fun MainActivity.registerListenerRewardInters() {
    rewardInterstitialAd.listener = object : RewardInterstitialAdmobListener {
        override fun onAdLoaded() {
            ECOLog.showLog("Ad loaded successfully")
        }

        override fun onFailedAdLoad(error: String) {
            ECOLog.showLog("Ad load failed: $error")
        }

        override fun onShowFullScreen(isDismiss: Boolean) {
            setLoadingState(false)
            openSecondActivity()
            rewardInterstitialAd.preloadRewardIntersAd()
            admobOpenAppManager.unlock()
        }
    }
}

private fun MainActivity.handleRewardVideoClick() {
    showDialogReward()
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


fun MainActivity.showRewardAd() {
    admobOpenAppManager.locked()
    setLoadingState(true)
    rewardAd.listener = object : RewardListener {
        override fun onAdLoaded() {}
        override fun onAdFailedToLoad(error: String) {}

        override fun onShowFullScreen(isDismiss: Boolean) {
            if(isDismiss) {
                setLoadingState(false)
                showToast("Mở khóa thành công!")
                rewardAd.preloadRewardAd()
                admobOpenAppManager.unlock()
            }
            else {
                setLoadingState(false)
                showToast("Không thể tải quảng cáo!")
                admobOpenAppManager.unlock()
            }
        }
    }
    showAdWithTimeout(6, rewardAd) {
        setLoadingState(false)
        if(rewardAd.isAdReady()) {
            rewardAd.showAd(this@showRewardAd)
        }
        else {
            showToast("Lỗi quảng cáo")
            admobOpenAppManager.unlock()
        }
    }
}

private fun MainActivity.openSecondActivity() {
    val intentAd = Intent(this, SecondActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
    startActivity(intentAd)
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

    rewardAd.preloadRewardAd()
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


//fun MainActivity.showAdWithTimeout(
//    seconds: Int,
//    condition: Any,
//    onComplete: () -> Unit
//) {
//    ECOLog.showLog("Come here")
//    var progress = 0
//    val delayTime = ((seconds * 1000) / 100).toLong()
//    var isCompleted = false
//    var job: Job? = null
//
//    job = lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.RESUMED) {
//            ECOLog.showLog("Come here - 2")
//
//            while (isActive && progress <= 100 && !isCompleted) {
//                ECOLog.showLog("Come here - 3, progress: $progress")
//                val isReady = when (condition) {
//                    is AdmobReward -> condition.isAdReady() || condition.isError()
//                    is AdmobRewardInterstitial -> condition.isAdReady() || condition.isError()
//                    is AdmobInterstitial -> condition.isAdReady() || condition.isError()
//                    else -> false
//                }
//                // Loaded -> timeout 1s
//                if (isReady && progress >= 20) {
//                    ECOLog.showLog("Come here - 4: Ad ready, showing ad")
//                    isCompleted = true
//                    onComplete()
//                    break
//                }
//
//                // Loading -> timeout 3s
//                if (!isReady && progress >= 50) {
//                    ECOLog.showLog("Come here - Loading timeout 3s, continue flow")
//                    isCompleted = true
//                    onComplete()
//                    break
//                }
//
//                delay(delayTime)
//                progress++
//            }
//
//            if (!isCompleted) {
//                ECOLog.showLog("Come here - 5: Total timeout, continue flow")
//                onComplete()
//            }
//        }
//        job?.cancel()
//    }
//}

