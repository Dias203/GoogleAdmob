/*
package com.example.openappads.admob.interstitial

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.openappads.constants.admob.ADS_INTERSTITIAL_UNIT_ID
import com.example.openappads.constants.admob.AdStatusManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdmob(private val context: Context) {
    var listener: InterstitialAdmobListener? = null
    private var isLoading = false  // -> Theo dõi trạng thái tải quảng cáo
    var isShowingRequested = false  // -> Theo dõi trạng thái yêu cầu hiển thị quảng cáo
    private var isActivityPaused = false  // -> Theo dõi trạng thái Activity
    var interstitialAd: InterstitialAd? = null
    val handler = Handler(Looper.getMainLooper())
    private val LOAD_TIMEOUT_MS = 5000L // 5 giây
    private var isTimedOut = false

    private val timeoutRunnable = Runnable {
        if (isLoading && interstitialAd == null) {
            isTimedOut = true
            isLoading = false
            listener?.onFailedAdLoad("Ad loading timed out after ${LOAD_TIMEOUT_MS}ms")
        }
    }

    fun setActivityPaused(paused: Boolean = false) {
        isActivityPaused = paused
        // Nếu Activity resume và có request show đang chờ
        if (!paused && isShowingRequested && isAdReady()) {
            Log.i("DUC", "Activity resumed, showing pending reward ad")
            // Cần activity context để show, sẽ được handle trong Activity
        }
    }

    // Method để check xem có pending show request không
    fun hasPendingShowRequest(): Boolean {
        return isShowingRequested
    }

    // Method để cancel pending request (nếu cần)
    fun cancelPendingShowRequest() {
        isShowingRequested = false
        AdStatusManager.isRewardAdShowingOrLoading = false
    }

    // Preload ad - gọi khi Activity onCreate
    fun preloadInterstitialAd() {
        if (interstitialAd == null && !isLoading) {
            loadInterstitialAd()
        }
    }

    fun cleanup() {
        handler.removeCallbacks(timeoutRunnable)
        isShowingRequested = false
        AdStatusManager.isInterstitialShowOrLoading = false
    }

    // Check if ad is ready to show
   fun isAdReady(): Boolean {
        return interstitialAd != null && !isLoading
    }

    private fun loadInterstitialAd() {
        Log.i("DUONG", "loadAd: $interstitialAd")
        if (interstitialAd == null && !isLoading) {
            isLoading = true
            isTimedOut = false // reset timeout flag
            AdStatusManager.isInterstitialShowOrLoading = true
            handler.postDelayed(timeoutRunnable, LOAD_TIMEOUT_MS)
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                ADS_INTERSTITIAL_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.i("DUONG", "onAdLoaded: load thành công")
                        super.onAdLoaded(ad)
                        handler.removeCallbacks(timeoutRunnable)
                        interstitialAd = ad
                        isLoading = false
                        AdStatusManager.isInterstitialShowOrLoading = false
                        listener?.onAdLoaded()
                    }


                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.i("DUONG", "onAdFailedToLoad: load thất bại")
                        super.onAdFailedToLoad(error)
                        handler.removeCallbacks(timeoutRunnable)
                        interstitialAd = null
                        isLoading = false
                        AdStatusManager.isInterstitialShowOrLoading = false
                        listener?.onFailedAdLoad(error.message)
                    }
                }
            )
        }
    }

    private fun loadInterstitialWithCallback(activity: AppCompatActivity) {
        Log.i("DUONG", "loadInterstitialWithCallback: hoặc là gọi vào đây")
        Log.i("DUC", "loadRewardWithCallback: $interstitialAd")
        if (isLoading) return

        isTimedOut = false
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        handler.postDelayed(timeoutRunnable, LOAD_TIMEOUT_MS)

        InterstitialAd.load(
            context,
            ADS_INTERSTITIAL_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    super.onAdLoaded(ad)
                    handler.removeCallbacks(timeoutRunnable)
                    if (isTimedOut) {
                        Log.i("DUC", "onAdLoaded: TimeOut")
                        return
                    }
                    interstitialAd = ad
                    isLoading = false
                    listener?.onAdLoaded()
                    if (isShowingRequested && !isActivityPaused) {
                        setupAndShowAd(activity)
                        isShowingRequested = false
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    handler.removeCallbacks(timeoutRunnable)
                    interstitialAd = null
                    isLoading = false
                    isShowingRequested = false
                    AdStatusManager.isRewardAdShowingOrLoading = false
                    listener?.onFailedAdLoad(error.message)
                }
            }
        )

    }

    private fun setupAndShowAd(activity: AppCompatActivity) {
        Log.i("DUONG", "setupAndShowAd: gọi vào đây")
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                interstitialAd = null
                isLoading = false
                isShowingRequested = false  // reset ở đây để tránh lặp
                AdStatusManager.isInterstitialShowOrLoading = false
                preloadInterstitialAd()
                listener?.onAdDismiss()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                interstitialAd = null
                isLoading = false
                isShowingRequested = false
                // Reset flag khi show thất bại
                AdStatusManager.isRewardAdShowingOrLoading = false
                listener?.onFailedToShow(adError.message)
            }
        }
        interstitialAd?.show(activity)
    }

    fun showInterstitialAd(activity: AppCompatActivity) {
        Log.i(
            "DUONG",
            "showInterstitialAd: interstitialAd - $interstitialAd ______ isActivityPaused - $isActivityPaused"
        )
        AdStatusManager.isInterstitialShowOrLoading = true
        isShowingRequested = true
        if (isAdReady() && !isActivityPaused) {
            // Ad is ready, show immediately
            setupAndShowAd(activity)
        } else if (!isLoading) {
            // Ad not ready and not loading, load then show
            loadInterstitialWithCallback(activity)
        } else {
            // Ad is currently loading, just wait for it to load then show
            Log.i("DUC", "Ad is loading, will show when ready")
        }
    }


}*/

package com.example.openappads.admob.interstitial

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.constants.admob.ADS_INTERSTITIAL_UNIT_ID
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdmob(private val context: Context) {
    var listener: InterstitialAdmobListener? = null
    var interstitialAd: InterstitialAd? = null
    //var isShowingRequested = false
    private var isLoading = false
    val handler = Handler(Looper.getMainLooper())

    // Timeout cho show ad
    private val SHOW_TIMEOUT_LOADING_MS = 3000L
    private val SHOW_TIMEOUT_LOADED_MS = 1000L
    private var isActivityPaused = false

    private var isShowTimeout = false

    /*private val showTimeoutRunnable = Runnable {
        if (isShowingRequested) {
            isShowTimeout = true
            isShowingRequested = false
            listener?.onFailedToShow("Ad show timeout")
        }
    }*/

    fun setActivityPaused(paused: Boolean = false): Boolean {
        isActivityPaused = paused

        return isActivityPaused
    }


    /*fun hasPendingShowRequest(): Boolean {
        return isShowingRequested
    }*/

    fun cleanup() {
        interstitialAd = null
        //handler.removeCallbacks(showTimeoutRunnable)
        //isShowingRequested = false
    }


    fun preloadInterstitialAd() {
        if (interstitialAd == null && !isLoading) {
            loadInterstitialAd()
        }
    }


    fun isAdReady(): Boolean {
        return interstitialAd != null && !isLoading
    }

    private fun loadInterstitialAd() {
        if (interstitialAd != null || isLoading) return

        isLoading = true

        InterstitialAd.load(
            context,
            ADS_INTERSTITIAL_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
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

    private fun setStateOnAdLoaded(ad: InterstitialAd) {
        ECOLog.showLog("Quảng cáo Interstitial đã được tải thành công")
        interstitialAd = ad
        isLoading = false
        listener?.onAdLoaded()
    }

    private fun setStateOnAdFailedToLoad(error: LoadAdError) {
        interstitialAd = null
        //handler.removeCallbacks(showTimeoutRunnable)
        isLoading = false
        //isShowingRequested = false
        isShowTimeout = false


        ECOLog.showLog("Tải quảng cáo Interstitial thất bại - Error Message: ${error.message}")
        listener?.onFailedAdLoad(error.message)
    }


    fun showInterstitialAd(activity: AppCompatActivity) {
        //isShowingRequested = true
        isShowTimeout = false

        when {
            // TH1: Ad loaded
            isAdReady() && !isActivityPaused -> {
                ECOLog.showLog("Quảng cáo Interstitial đã sẵn sàng, hiển thị với timeout 1 giây")
                //startShowTimeout(SHOW_TIMEOUT_LOADED_MS)
                setupAndShowAd(activity)
            }

            // TH2: Ad loading
            isLoading -> {
                ECOLog.showLog("Quảng cáo Interstitial đang tải, hiển thị với timeout 3 giây")
                //startShowTimeout(SHOW_TIMEOUT_LOADING_MS)
                // Sẽ show khi load xong trong loadRewardWithCallback
            }

            // TH3: Ad chưa có ad trong bộ nhớ và không đang load (rewardedInterstitialAd == null và isLoading == false.)
            else -> {
                ECOLog.showLog("Quảng cáo Interstitial không được tải, bắt đầu tải với timeout 3 giây")
                //startShowTimeout(SHOW_TIMEOUT_LOADING_MS)
                loadInterstitialAd()
            }
        }
    }

    /*private fun startShowTimeout(timeoutMs: Long) {
        handler.removeCallbacks(showTimeoutRunnable)
        handler.postDelayed(showTimeoutRunnable, timeoutMs)
    }*/

    private fun setupAndShowAd(activity: AppCompatActivity) {
        if (isShowTimeout) {
            ECOLog.showLog("Vượt quá timeout, không hiển thị quảng cáo")
            return
        }


        //handler.removeCallbacks(showTimeoutRunnable)

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()

                setStateOnAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                super.onAdFailedToShowFullScreenContent(adError)
                Log.e("DUC", "Failed to show Interstitial ad: ${adError.message}")
                ECOLog.showLog("Hiển thị quảng cáo Interstitial thất bại - Error Message: ${adError.message}")

                setStateOnAdFailedToShowFullScreenContent(adError)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()

                setStateOnAdShowedFullScreenContent()
            }
        }

        interstitialAd?.show(activity)
    }

    private fun setStateOnAdDismissedFullScreenContent() {
        interstitialAd = null
        isLoading = false
        //isShowingRequested = false
        isShowTimeout = false


        // Tự động tải trước quảng cáo tiếp theo sau khi quảng cáo hiện tại được đóng.
        preloadInterstitialAd()

        listener?.onAdDismiss()
    }

    private fun setStateOnAdFailedToShowFullScreenContent(adError: AdError) {
        interstitialAd = null
        isLoading = false
        //isShowingRequested = false
        isShowTimeout = false

        listener?.onFailedToShow(adError.message)
    }

    private fun setStateOnAdShowedFullScreenContent() {
        // Reset showing request khi ad đã hiển thị thành công
        //isShowingRequested = false
        isShowTimeout = false
        // Giữ flag = true khi ad đang hiển thị
    }
}


