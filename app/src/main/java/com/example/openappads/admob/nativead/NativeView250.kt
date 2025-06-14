package com.example.openappads.admob.nativead

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.example.openappads.databinding.AdUnifiedBinding
import com.google.android.gms.ads.nativead.NativeAd

class NativeView250 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    var binding: AdUnifiedBinding? = null
    init {
        binding = AdUnifiedBinding.inflate(LayoutInflater.from(context), this, true)
    }


    fun loaded(nativeAd: NativeAd) {
        binding?.let {unifiedAdBinding ->
            val nativeAdView = unifiedAdBinding.root


            unifiedAdBinding.adHeadline.text = nativeAd.headline


            nativeAdView.apply {
                headlineView = unifiedAdBinding.adHeadline
                mediaView = unifiedAdBinding.adMedia
                nativeAd.mediaContent?.let { unifiedAdBinding.adMedia.mediaContent = it }
                callToActionView = unifiedAdBinding.adCallToAction
                unifiedAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
                unifiedAdBinding.adStars.rating = nativeAd.starRating!!.toFloat()
                unifiedAdBinding.adBody.text = nativeAd.body

                setNativeAd(nativeAd)
            }
            /*
            if (nativeAd.body == null) {
                unifiedAdBinding.adBody.visibility = View.INVISIBLE
            } else {
                unifiedAdBinding.adBody.visibility = View.VISIBLE
                unifiedAdBinding.adBody.text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                unifiedAdBinding.adCallToAction.visibility = View.INVISIBLE
            } else {
                unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
                unifiedAdBinding.adCallToAction.text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                unifiedAdBinding.adAppIcon.visibility = View.GONE
            } else {
                unifiedAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
                unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
            }

            if (nativeAd.starRating == null) {
                unifiedAdBinding.adStars.visibility = View.INVISIBLE
            } else {
                unifiedAdBinding.adStars.rating = nativeAd.starRating!!.toFloat()
                unifiedAdBinding.adStars.visibility = View.VISIBLE
            }

            unifiedAdBinding.root.setNativeAd(nativeAd)*/
        }
    }
}