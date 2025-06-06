package com.example.openappads.manager

import android.app.Activity
import android.content.Context
import com.example.openappads.MyApplication
import com.example.openappads.constants.admob.TEST_DEVICE_HASHED_ID
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

/**
 * SDK Google Mobile Ads cung cấp User Messaging Platform (nền tảng quản lý sự đồng ý được chứng nhận IAB của Google)
 * như một giải pháp để thu thập sự đồng ý từ người dùng ở các quốc gia bị ảnh hưởng bởi GDPR.
 * Đây là một ví dụ và bạn có thể chọn một nền tảng quản lý sự đồng ý khác để thu thập sự đồng ý.
 */
class GoogleMobileAdsConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    /** Định nghĩa giao diện cho một callback được gọi khi quá trình thu thập sự đồng ý hoàn tất. */
    fun interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    /** Biến hỗ trợ để xác định xem ứng dụng có thể yêu cầu quảng cáo hay không. */
    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    /** Biến hỗ trợ để xác định xem biểu mẫu tùy chọn quyền riêng tư có được yêu cầu hay không. */
    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Phương thức hỗ trợ để gọi các phương thức của UMP SDK nhằm yêu cầu thông tin đồng ý và tải/hiển thị
     * biểu mẫu đồng ý nếu cần thiết.
     */
    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener,
    ) {
        // Để kiểm thử, bạn có thể ép buộc một DebugGeography là EEA hoặc NOT_EEA.
        val debugSettings =
            ConsentDebugSettings.Builder(activity)
                // .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(TEST_DEVICE_HASHED_ID)
                .build()

        val params = ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()

        // Yêu cầu cập nhật thông tin đồng ý nên được gọi mỗi khi ứng dụng khởi động.
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    // Sự đồng ý đã được thu thập.
                    onConsentGatheringCompleteListener.consentGatheringComplete(formError)
                }
            },
            { requestConsentError ->
                onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
            },
        )
    }

    /** Phương thức hỗ trợ để gọi phương thức UMP SDK nhằm hiển thị biểu mẫu tùy chọn quyền riêng tư. */
    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: OnConsentFormDismissedListener,
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    companion object {
        @Volatile private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
                }
    }
}