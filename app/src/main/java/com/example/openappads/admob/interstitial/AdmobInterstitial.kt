package com.example.openappads.admob.interstitial

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.constants.admob.ADS_INTERSTITIAL_UNIT_ID
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdmobInterstitial(private val context: Context) {
    var listener: InterstitialAdmobListener? = null
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false


    fun preloadInterstitialAd() {
        if (interstitialAd == null && !isLoading) {
            loadAd()
        }
    }

    fun isAdReady(): Boolean {
        ECOLog.showLog("$interstitialAd - $isLoading")
        return interstitialAd != null && !isLoading
    }

    fun isLoading() : Boolean {
        ECOLog.showLog("$interstitialAd - $isLoading")
        return interstitialAd == null && isLoading
    }

    fun loadAd() {
        if (interstitialAd != null || isLoading) return

        isLoading = true

        InterstitialAd.load(context,
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
            })
    }

    private fun setStateOnAdLoaded(ad: InterstitialAd) {
        ECOLog.showLog("Quảng cáo interstitial đã được tải thành công")
        interstitialAd = ad
        isLoading = false
        listener?.onAdLoaded()
    }

    private fun setStateOnAdFailedToLoad(error: LoadAdError) {
        interstitialAd = null
        isLoading = false

        ECOLog.showLog("Tải quảng cáo interstitial thất bại - Error Message: ${error.message}")
        listener?.onFailedAdLoad(error.message)
    }


    fun showAd(activity: AppCompatActivity) {
        ECOLog.showLog("!isAdReady(): " + !isAdReady())
        if (!isAdReady()) {
            ECOLog.showLog("Vao day")
            return
        }

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                setStateOnAdDismissedFullScreenContent()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                ECOLog.showLog("Hiển thị quảng cáo interstitial thất bại - Error Message: ${adError.message}")
                setStateOnAdFailedToShowFullScreenContent(adError)
            }

            override fun onAdShowedFullScreenContent() {
                setStateOnAdShowedFullScreenContent()
            }
        }
        interstitialAd?.show(activity)
    }


    private fun setStateOnAdDismissedFullScreenContent() {
        interstitialAd = null
        isLoading = false
        listener?.onAdDismiss()
    }

    private fun setStateOnAdFailedToShowFullScreenContent(adError: AdError) {
        interstitialAd = null
        isLoading = false
        listener?.onFailedToShow(adError.message)
    }

    private fun setStateOnAdShowedFullScreenContent() {
        listener?.onShowed()
    }

    fun destroyAd() {
        interstitialAd = null
        isLoading = false
    }


}

