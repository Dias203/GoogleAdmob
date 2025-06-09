package com.example.openappads.admob.reward_interstitial

import android.content.Context
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

class AdmobRewardInterstitial(private val context: Context) {
    var listener: RewardInterstitialAdmobListener? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isLoading = false
    private var isLoaded = false


    fun preloadRewardIntersAd() {
        if (rewardedInterstitialAd == null && !isLoading) {
            loadAd()
        }
    }

    fun isAdReady(): Boolean {
        //ECOLog.showLog("$rewardedInterstitialAd - $isLoading")
        return rewardedInterstitialAd != null && !isLoading
    }

    fun isLoading() : Boolean {
        ECOLog.showLog("$rewardedInterstitialAd - $isLoading")
        return rewardedInterstitialAd == null && isLoading
    }

    fun isLoaded() = isLoaded

    fun loadAd() {
        if (rewardedInterstitialAd != null || isLoading) return

        isLoading = true

        RewardedInterstitialAd.load(context,
            ADS_REWARD_INTERSTITIAL_AD,
            AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    super.onAdLoaded(ad)
                    setStateOnAdLoaded(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    setStateOnAdFailedToLoad(error)
                }
            })
    }

    private fun setStateOnAdLoaded(ad: RewardedInterstitialAd) {
        ECOLog.showLog("Quảng cáo rewardedInterstitialAd đã được tải thành công")
        rewardedInterstitialAd = ad
        isLoading = false
        isLoaded = true
        listener?.onAdLoaded()
    }

    private fun setStateOnAdFailedToLoad(error: LoadAdError) {
        rewardedInterstitialAd = null
        isLoading = false

        ECOLog.showLog("Tải quảng cáo rewardedInterstitialAd thất bại - Error Message: ${error.message}")
        listener?.onFailedAdLoad(error.message)
    }


    fun showAd(activity: AppCompatActivity) {
        /*if (!isAdReady()) {
            ECOLog.showLog("Call !isAdReady(): " + !isAdReady())
            return
        }*/
        ECOLog.showLog("SHOW AD")

        rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                ECOLog.showLog("Dismiss12312313")
                setStateOnAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                ECOLog.showLog("Hiển thị quảng cáo rewardedInterstitialAd thất bại - Error Message: ${adError.message}")
                setStateOnAdFailedToShowFullScreenContent(adError)
            }

            override fun onAdShowedFullScreenContent() {
                setStateOnAdShowedFullScreenContent()
            }
        }
        rewardedInterstitialAd?.show(activity, OnUserEarnedRewardListener{})
    }


    private fun setStateOnAdDismissedFullScreenContent() {
        rewardedInterstitialAd = null
        isLoading = false
        listener?.onAdDismiss()
    }

    private fun setStateOnAdFailedToShowFullScreenContent(adError: AdError) {
        rewardedInterstitialAd = null
        isLoading = false
        listener?.onFailedToShow(adError.message)
    }

    private fun setStateOnAdShowedFullScreenContent() {
        listener?.onShowed()
    }

    fun destroyAd() {
        rewardedInterstitialAd = null
        isLoading = false
    }


}

