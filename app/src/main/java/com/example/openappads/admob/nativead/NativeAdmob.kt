package com.example.openappads.admob.nativead

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.openappads.constants.admob.ADS_NATIVE_UNIT_ID
import com.example.openappads.databinding.AdUnifiedBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class NativeAdmob(private val context: Context) {
    private var nativeAd: NativeAd? = null
    private val listener: NativeAdmobListener? = null
    private val nativeView250: NativeView250? = null

    /**
     * Điền dữ liệu từ [NativeAdView] vào đối tượng [NativeAd] đã cho.
     *
     * @param nativeAd đối tượng chứa nội dung của quảng cáo
     * @param unifiedAdBinding đối tượng liên kết của bố cục có NativeAdView làm chế độ xem gốc
     */
    private fun populateNativeAdView(nativeAd: NativeAd, unifiedAdBinding: AdUnifiedBinding) {
        // Tạo một bản sao của lớp NativeAdView.
        val nativeAdView = unifiedAdBinding.root

        nativeAdView.mediaView = unifiedAdBinding.adMedia

        nativeAdView.headlineView = unifiedAdBinding.adHeadline
        nativeAdView.bodyView = unifiedAdBinding.adBody
        nativeAdView.iconView = unifiedAdBinding.adAppIcon
        nativeAdView.starRatingView = unifiedAdBinding.adStars
        nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
        nativeAdView.adChoicesView = unifiedAdBinding.adChoices

        unifiedAdBinding.adHeadline.text = nativeAd.headline
        nativeAd.mediaContent?.let { unifiedAdBinding.adMedia.mediaContent = it }

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

        nativeAdView.setNativeAd(nativeAd)
    }

    fun loadNativeAd(view: ViewGroup) {
        val builder = AdLoader.Builder(context, ADS_NATIVE_UNIT_ID)
            .forNativeAd {
                // Hiển thị quảng cáo
                nativeAd?.destroy()
                nativeAd = it
                val inflater = LayoutInflater.from(context)
                val unifiedAdBinding = AdUnifiedBinding.inflate(inflater)
                populateNativeAdView(it, unifiedAdBinding)
                view.removeAllViews()
                view.addView(unifiedAdBinding.root)
            }

        val videoOptions = VideoOptions.Builder().setStartMuted(false).build()
        val adOptions = NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()
        /*val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .build()*/

        val adLoader = builder.withAdListener(object : AdListener() {
            // Lắng nghe sự kiện theo dõi quảng cáo
            override fun onAdLoaded() {
                super.onAdLoaded()
                listener?.onAdLoaded()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                listener?.onFailedAdLoad(error.message)
            }

            override fun onAdClicked() {
                super.onAdClicked()
            }
        })
            .withNativeAdOptions(adOptions) // -> Đặt các tùy chọn cho quảng cáo gốc
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun loadAd(bind: (ad: NativeAd) -> Unit) {
        val builder = AdLoader.Builder(context, ADS_NATIVE_UNIT_ID)
            .forNativeAd {
                nativeAd?.destroy()
                nativeAd = it
                bind(it)
                listener?.onAdLoaded()
            }

        val videoOptions = VideoOptions.Builder().setStartMuted(false).build()
        val adOptions = NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()
        /*val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .build()*/

        val adLoader = builder.withAdListener(object : AdListener() {
            // Lắng nghe sự kiện theo dõi quảng cáo
            override fun onAdLoaded() {
                super.onAdLoaded()
                listener?.onAdLoaded()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                listener?.onFailedAdLoad(error.message)
            }

            override fun onAdClicked() {
                super.onAdClicked()
            }
        })
            .withNativeAdOptions(adOptions) // -> Đặt các tùy chọn cho quảng cáo gốc
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun onDestroy() {
        nativeAd?.destroy()
    }

}