package com.example.openappads.admob.reward

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.constants.admob.ADS_REWARD_UNIT_ID
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAdmob(private val context: Context) {
    var listener: RewardListener? = null
    var rewardedAd: RewardedAd? = null
    var isShowingRequested = false
    private var isLoading = false
    val handler = Handler(Looper.getMainLooper())

    // Timeout cho show ad
    private val SHOW_TIMEOUT_LOADING_MS = 3000L
    private val SHOW_TIMEOUT_LOADED_MS = 1000L
    var isRewardAdShow: Boolean = false
    private var isActivityPaused = false
    private var isShowTimeout = false
    private var currentActivity: AppCompatActivity? = null


    private val showTimeoutRunnable = Runnable {
        if (isShowingRequested) {
            isShowTimeout = true
            isShowingRequested = false
            isRewardAdShow = false
            listener?.onFailedToShow("Ad show timeout")
        }
    }
    fun setActivityPaused(paused: Boolean = false): Boolean {
        isActivityPaused = paused
        return isActivityPaused
    }

    fun hasPendingShowRequest(): Boolean {
        return isShowingRequested
    }


    fun cleanup() {
        handler.removeCallbacks(showTimeoutRunnable)
        isShowingRequested = false
        isRewardAdShow = false
        rewardedAd = null
    }

    fun preloadRewardAd() {
        ECOLog.showLog("Gọi đến đây")
        if (rewardedAd == null && !isLoading) {
            loadRewardAd()
        }
    }

    fun isAdReady(): Boolean {
        return rewardedAd != null && !isLoading
    }

    private fun loadRewardAd() {
        ECOLog.showLog("Gọi đến đây")
        if (rewardedAd != null || isLoading) return

        isLoading = true

        RewardedAd.load(
            context,
            ADS_REWARD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    super.onAdLoaded(ad)
                    setStateOnAdLoaded(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    setStateOnAdFailedToLoad(error)
                }
            }
        )
    }

    private fun setStateOnAdLoaded(ad: RewardedAd){
        ECOLog.showLog("Quảng cáo reward đã được tải thành công")
        rewardedAd = ad
        isLoading = false
        listener?.onAdLoaded()
    }

    private fun setStateOnAdFailedToLoad(error: LoadAdError) {
        rewardedAd = null
        handler.removeCallbacks(showTimeoutRunnable)
        isLoading = false
        isShowingRequested = false
        isShowTimeout = false

        ECOLog.showLog("Tải quảng cáo reward thất bại - Error Message: ${error.message}")
        listener?.onAdFailedToLoad(error.message)
    }

    fun showRewardAd(activity: AppCompatActivity) {
        // Lưu reference activity để dùng sau
        currentActivity = activity

        // Set flag ngay khi user click để block open app ad
        isRewardAdShow = true
        isShowingRequested = true
        isShowTimeout = false

        when {
            // TH1: Ad loaded
            isAdReady() && !isActivityPaused -> {
                ECOLog.showLog("Quảng cáo reward đã sẵn sàng, hiển thị với timeout 1 giây")
                startShowTimeout(SHOW_TIMEOUT_LOADED_MS)
                setupAndShowAd(activity)
            }

            // TH2: Ad loading
            isLoading -> {
                ECOLog.showLog("Quảng cáo reward đang tải, hiển thị với timeout 3 giây")
                startShowTimeout(SHOW_TIMEOUT_LOADING_MS)
                // Sẽ show khi load xong trong loadRewardWithCallback
            }

            // TH3: Ad chưa load và không đang load (rewardAd == null && isLoading = false)
            else -> {
                ECOLog.showLog("Quảng cáo reward không được tải, bắt đầu tải với timeout 3 giây")
                startShowTimeout(SHOW_TIMEOUT_LOADING_MS)
                loadRewardAd()
            }
        }
    }

    private fun startShowTimeout(timeoutMs: Long) {
        handler.removeCallbacks(showTimeoutRunnable)
        handler.postDelayed(showTimeoutRunnable, timeoutMs)
    }


    private fun setupAndShowAd(activity: AppCompatActivity) {
        if (isShowTimeout) {
            ECOLog.showLog("Vượt quá timeout, không hiển thị quảng cáo")
            return
        }

        // Remove show timeout vì ad sắp show
        handler.removeCallbacks(showTimeoutRunnable)

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()

                setStateOnAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                Log.e("DUC", "Failed to show reward ad: ${adError.message}")
                ECOLog.showLog("Hiển thị quảng cáo reward thất bại - Error Message: ${adError.message}")

                setStateOnAdFailedToShowFullScreenContent(adError)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()

                setStateOnAdShowedFullScreenContent()
            }
        }

        rewardedAd?.show(activity, OnUserEarnedRewardListener {})
    }


    private fun setStateOnAdDismissedFullScreenContent() {
        rewardedAd = null
        isLoading = false
        isShowingRequested = false
        isShowTimeout = false

        // Reset flag để cho phép open app ad hoạt động trở lại
        isRewardAdShow = false

        // Tự động tải trước quảng cáo tiếp theo sau khi quảng cáo hiện tại được đóng.
        preloadRewardAd()

        listener?.onAdDismiss()
    }

    private fun setStateOnAdFailedToShowFullScreenContent(adError: AdError) {
        rewardedAd = null
        isLoading = false
        isShowingRequested = false
        isShowTimeout = false
        // Reset flag khi show thất bại
        isRewardAdShow = false

        listener?.onFailedToShow(adError.message)
    }

    private fun setStateOnAdShowedFullScreenContent() {
        // Reset showing request khi ad đã hiển thị thành công
        isShowingRequested = false
        isShowTimeout = false
    }
}

