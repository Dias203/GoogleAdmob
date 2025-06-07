/*
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

fun MainActivity.setOnClick() {
    binding.apply {
        admobRewardInterstitialButton.setOnClickListener { handleRewardInterstitialClick() }
        admobRewardVideoButton.setOnClickListener { handleRewardVideoClick() }
        openSecondActivity.setOnClickListener { handleOpenSecondActivityClick() }
    }
}

private fun MainActivity.handleRewardInterstitialClick() {
    admobOpenAppManager.locked()
    setLoadingState(true)
    rewardInterstitialAd.listener = object : RewardInterstitialAdmobListener {
        override fun onAdDismiss() {
            showToast("Xuất Video thành công!")
            setLoadingState(false)
            admobOpenAppManager.unlock()
        }

        override fun onAdLoaded() {}
        override fun onFailedAdLoad(error: String) {
            setLoadingState(false)
            admobOpenAppManager.unlock()
        }

        override fun onFailedToShow(error: String) {
            setLoadingState(false)
        }
    }
    rewardInterstitialAd.showRewardInterstitialAd(this)
}

private fun MainActivity.handleRewardVideoClick() {
    showDialogReward()
}

fun MainActivity.showRewardAd(){
    setLoadingState(true)
    admobOpenAppManager.locked()
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
            admobOpenAppManager.unlock()
        }

        override fun onFailedToShow(error: String) {
            setLoadingState(false)
            if (error.contains("timeout", ignoreCase = true)) {
                showToast("Quảng cáo tải quá lâu, tiếp tục luồng!")
            } else {
                showToast("Không thể hiển thị quảng cáo!")
            }
            admobOpenAppManager.unlock()
        }
    }
    rewardAd.showRewardAd(this)
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
    val intent = Intent(this, SecondActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
    showRewardInterstitial(intent)
}

fun MainActivity.showRewardInterstitial(intent: Intent){
    setLoadingState(true)
    admobOpenAppManager.locked()
    rewardInterstitialAd.listener = object : RewardInterstitialAdmobListener {
        override fun onAdDismiss() {
            startActivity(intent)
            setLoadingState(false)
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
    }
    rewardInterstitialAd.showRewardInterstitialAd(this)
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
    rewardInterstitialAd.preloadRewardInterstitialAd()
}

// Thêm methods để handle Activity lifecycle
fun MainActivity.onActivityPaused() {
    rewardAd.setActivityPaused(true)
    rewardInterstitialAd.setActivityPaused(true)
}

fun MainActivity.onActivityResumed() {
    // Check nếu có pending show request thì show ngay
    if (!rewardAd.setActivityPaused() && rewardAd.hasPendingShowRequest() && rewardAd.isAdReady()) {
        Log.i("DUC", "Showing pending reward ad after resume")
        rewardAd.showRewardAd(this)
    }
    if (!rewardInterstitialAd.setActivityPaused() && rewardInterstitialAd.hasPendingShowRequest() && rewardInterstitialAd.isAdReady()) {
        Log.i("DUC", "Showing pending reward interstitial ad after resume")
        rewardInterstitialAd.showRewardInterstitialAd(this)
    }
}

fun MainActivity.onActivityDestroyed() {
    // Cleanup để tránh memory leak
    rewardAd.cleanup()
    rewardInterstitialAd.cleanup()
    bannerAd.onDestroy()
}

private fun MainActivity.setLoadingState(isLoading: Boolean) {

    binding.apply {
        loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        bgMain.setBackgroundColor(
            if (isLoading) Color.parseColor("#8C8B8B") else Color.WHITE
        )
        admobRewardInterstitialButton.isEnabled = !isLoading
        admobRewardVideoButton.isEnabled = !isLoading
        openSecondActivity.isEnabled = !isLoading
    }
}

private fun MainActivity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
*/
