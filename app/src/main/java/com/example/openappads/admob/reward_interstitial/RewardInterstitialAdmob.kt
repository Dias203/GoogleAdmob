package com.example.openappads.admob.reward_interstitial

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.constants.admob.ADS_REWARD_INTERSTITIAL_AD
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class RewardInterstitialAdmob(private val context: Context) {
    var listener: RewardInterstitialAdmobListener? = null
    var rewardedInterstitialAd: RewardedInterstitialAd? = null
    var isShowingRequested = false
    private var isLoading = false
    val handler = Handler(Looper.getMainLooper())

    // Timeout cho show ad
    private val SHOW_TIMEOUT_LOADING_MS = 3000L
    private val SHOW_TIMEOUT_LOADED_MS = 1000L
    var isRewardInterstitialShow: Boolean = false
    private var isActivityPaused = false
    private var isShowTimeout = false

    private val showTimeoutRunnable = Runnable {
        if (isShowingRequested) {
            isShowTimeout = true
            isShowingRequested = false
            isRewardInterstitialShow = false
            listener?.onFailedToShow("Ad show timeout")
        }
    }

    /*companion object {
        var isRewardInterstitialShow: Boolean = false
    }*/

    fun setActivityPaused(paused: Boolean = false): Boolean {
        isActivityPaused = paused

        return isActivityPaused
    }

    // Method để check xem có pending show request không
    fun hasPendingShowRequest(): Boolean {
        return isShowingRequested
    }

    fun cleanup() {
        handler.removeCallbacks(showTimeoutRunnable)
        isShowingRequested = false
        isRewardInterstitialShow = false
    }

    // Preload ad - gọi khi Activity onCreate
    fun preloadRewardInterstitialAd() {
        if (rewardedInterstitialAd == null && !isLoading) {
            loadRewardInterstitialAd()
        }
    }

    // Check quảng cáo sẵn sàng để show
    fun isAdReady(): Boolean {
        return rewardedInterstitialAd != null && !isLoading
    }

    private fun loadRewardInterstitialAd() {
        if (rewardedInterstitialAd != null || isLoading) return

        isLoading = true

        RewardedInterstitialAd.load(
            context,
            ADS_REWARD_INTERSTITIAL_AD,
            AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    super.onAdLoaded(ad)
                    setOnStateOnAdLoaded(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    setStateOnAdFailedToLoad(error)
                }
            }
        )
    }

    private fun setOnStateOnAdLoaded(ad: RewardedInterstitialAd){
        ECOLog.showLog("Quảng cáo rewardInterstitial đã được tải thành công")
        rewardedInterstitialAd = ad
        isLoading = false
        isRewardInterstitialShow = false
        listener?.onAdLoaded()
    }

    private fun setStateOnAdFailedToLoad(error: LoadAdError) {
        rewardedInterstitialAd = null
        handler.removeCallbacks(showTimeoutRunnable)
        isLoading = false
        isShowingRequested = false
        isShowTimeout = false

        // Reset flag khi load thất bại
        isRewardInterstitialShow = false

        ECOLog.showLog("Tải quảng cáo rewardInterstitial thất bại - Error Message: ${error.message}")
        listener?.onFailedAdLoad(error.message)
    }

    fun showRewardInterstitialAd(activity: AppCompatActivity) {
        // Set flag ngay khi user click để block open app ad
        isRewardInterstitialShow = true
        isShowingRequested = true
        isShowTimeout = false

        when {
            // TH1: Ad loaded
            isAdReady() && !isActivityPaused -> {
                ECOLog.showLog("Quảng cáo rewardInterstitial đã sẵn sàng, hiển thị với timeout 1 giây")
                startShowTimeout(SHOW_TIMEOUT_LOADED_MS)
                setupAndShowAd(activity)
            }

            // TH2: Ad loading
            isLoading -> {
                ECOLog.showLog("Quảng cáo rewardInterstitial đang tải, hiển thị với timeout 3 giây")
                startShowTimeout(SHOW_TIMEOUT_LOADING_MS)
                // Sẽ show khi load xong trong loadRewardWithCallback
            }

            // TH3: Ad chưa có ad trong bộ nhớ và không đang load (rewardedInterstitialAd == null và isLoading == false.)
            else -> {
                ECOLog.showLog("Quảng cáo rewardInterstitial không được tải, bắt đầu tải với timeout 3 giây")
                startShowTimeout(SHOW_TIMEOUT_LOADING_MS)
                loadRewardInterstitialAd()
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

        rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()

                setStateOnAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                Log.e("DUC", "Failed to show rewardInterstitial ad: ${adError.message}")
                ECOLog.showLog("Hiển thị quảng cáo rewardInterstitial thất bại - Error Message: ${adError.message}")

                setStateOnAdFailedToShowFullScreenContent(adError)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()

                setStateOnAdShowedFullScreenContent()
            }
        }

        rewardedInterstitialAd?.show(activity, OnUserEarnedRewardListener {})
    }


    private fun setStateOnAdDismissedFullScreenContent() {
        rewardedInterstitialAd = null
        isLoading = false
        isShowingRequested = false
        isShowTimeout = false

        // Reset flag để cho phép open app ad hoạt động trở lại
        isRewardInterstitialShow = false
        listener?.onAdDismiss()

        // Tự động tải trước quảng cáo tiếp theo sau khi quảng cáo hiện tại được đóng.
        preloadRewardInterstitialAd()
    }

    private fun setStateOnAdFailedToShowFullScreenContent(adError: AdError) {
        rewardedInterstitialAd = null
        isLoading = false
        isShowingRequested = false
        isShowTimeout = false
        // Reset flag khi show thất bại
        isRewardInterstitialShow = false

        listener?.onFailedToShow(adError.message)
    }

    private fun setStateOnAdShowedFullScreenContent() {
        // Reset showing request khi ad đã hiển thị thành công
        isShowingRequested = false
        isShowTimeout = false
        // Giữ flag = true khi ad đang hiển thị
    }
}

