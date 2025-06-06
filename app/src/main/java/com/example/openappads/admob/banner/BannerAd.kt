package com.example.openappads.admob.banner

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.constants.admob.ADS_BANNER_COllAP_UNIT_ID
import com.example.openappads.constants.admob.ADS_BANNER_UNIT_ID
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class BannerAd(private val context: Context) {
    var listener: BannerListener? = null
    private var adView: AdView? = null
    private var startLoadAd = false

    fun load(view: ViewGroup, isCollapsible: Boolean = false) {
        if(startLoadAd) return
        startLoadAd = true
        createAdBanner(view, isCollapsible)
    }

    fun loadInlineBanner(view: ViewGroup, width: Int, height: Int) {
        Log.i("DUC", "loadInlineBanner: $height")
        view.removeAllViews()
        adView = AdView(context).apply {
            adUnitId = ADS_BANNER_UNIT_ID
            setAdSize(getSize(width, height))

            adListener = object : AdListener(){
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    listener?.onLoad()
                    Log.i("DUC", "onAdLoaded: InlineBanner")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    listener?.onLoadFail(p0.message)
                    Log.i("DUC", "onAdFailedToLoad: fail - $p0")
                }
            }
        }
        val adRequest = AdRequest.Builder().build()

        view.addView(adView)
        adView?.loadAd(adRequest)
    }


    private fun createAdBanner(view: ViewGroup, isCollapsible: Boolean) {
        val extras = Bundle()
        view.removeAllViews()
        adView = AdView(context).apply {
            adUnitId = ADS_BANNER_COllAP_UNIT_ID
            setAdSize(getSize())

            adListener = object : AdListener(){
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    listener?.onLoad()
                    Log.i("DUC", "onAdLoaded: Banner Ads")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.i("DUC", "onAdClicked: click")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    listener?.onLoadFail(p0.message)
                }
            }
        }
        val adRequest = AdRequest.Builder().apply {
            if(isCollapsible) {
                extras.putString("collapsible", "bottom")
                addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }
        }.build()

        view.addView(adView)
        adView?.loadAd(adRequest)
    }

    private fun getSize(): AdSize {
        val displayMetrics = context.resources.displayMetrics
        val density = displayMetrics.density
        val adWidth = (displayMetrics.widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)

    }

    private fun getSize(adWidth: Int, maxHeight: Int): AdSize {
        val displayMetrics = context.resources.displayMetrics
        val density = displayMetrics.density
        val adWidthDp = (adWidth / density).toInt()
        val adHeightDp = (maxHeight / density).toInt()
        return AdSize.getInlineAdaptiveBannerAdSize(adWidthDp, adHeightDp)
    }

    fun onDestroy() {
        adView?.destroy()
    }
}