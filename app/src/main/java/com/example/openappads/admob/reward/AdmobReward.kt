package com.example.openappads.admob.reward

import android.content.Context
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

class AdmobReward(private val context: Context) {
    var listener: RewardListener? = null
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    private var isError = false


    fun preloadRewardAd() {
        if (rewardedAd == null && !isLoading) {
            loadRewardAd()
        }
    }

    fun isAdReady(): Boolean {
        //ECOLog.showLog("$rewardedAd - $isLoading")
        return rewardedAd != null && !isLoading
    }

    fun isAdError(): Boolean {
        return isError
    }

    fun isLoading(): Boolean {
        //ECOLog.showLog("$rewardedAd - $isLoading")
        return rewardedAd == null && isLoading
    }

    fun loadRewardAd() {
        if (rewardedAd != null || isLoading) return

        isLoading = true

        RewardedAd.load(context,
            ADS_REWARD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    setStateOnAdLoaded(ad)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    setStateOnAdFailedToLoad(error)
                }
            })
    }

    private fun setStateOnAdLoaded(ad: RewardedAd) {
        ECOLog.showLog("Quảng cáo reward đã được tải thành công")
        rewardedAd = ad
        isLoading = false
        isError = false
        listener?.onAdLoaded()
    }

    private fun setStateOnAdFailedToLoad(error: LoadAdError) {
        rewardedAd = null
        isLoading = false
        isError = true
        ECOLog.showLog("Tải quảng cáo reward thất bại - Error Message: ${error.message}")
        listener?.onAdFailedToLoad(error.message)
    }


    fun showAd(activity: AppCompatActivity) {
        if (!isAdReady()) return


        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                setStateOnAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                ECOLog.showLog("Hiển thị quảng cáo reward thất bại - Error Message: ${adError.message}")
                setStateOnAdFailedToShowFullScreenContent(adError)
            }
        }
        rewardedAd?.show(activity, OnUserEarnedRewardListener {})
    }


    private fun setStateOnAdDismissedFullScreenContent() {
        rewardedAd = null
        isLoading = false
        isError = false
        listener?.onShowFullScreen(true)
    }

    private fun setStateOnAdFailedToShowFullScreenContent(adError: AdError) {
        rewardedAd = null
        isLoading = false
        isError = false
        listener?.onShowFullScreen(false)
    }

    fun destroyAd() {
        rewardedAd?.fullScreenContentCallback = null
        rewardedAd = null
        isLoading = false
        isError = false
    }
}

