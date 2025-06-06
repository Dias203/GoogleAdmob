/*
package com.example.openappads.admob.openapp

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.constants.admob.ADS_OPEN_APP_UNIT_ID_DEFAULT
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdmobAppOpenApplication(
    private val application: Application,
    val context: Context
) : DefaultLifecycleObserver {

    private val admobAppOpen by lazy { AdmobAppOpen(context).apply { setAdUnitId(
        ADS_OPEN_APP_UNIT_ID_DEFAULT) } }

    private var currentActivity: Activity? = null

    private var isLocked = false


    fun locked() {
        isLocked = true
    }

    fun unlock() {
        isLocked = false
    }

    fun initialize() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(application) {}
        }
    }

    fun onActivityStarted(activity: Activity) {
        ECOLog.showLog("onActivityStarted")
        if (!admobAppOpen.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        if (currentActivity == null ||
            admobAppOpen.isLoaded().not() ||
            admobAppOpen.isShowing()
        ) return
        admobAppOpen.showAdIfAvailable(currentActivity!!) {
            loadAd()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        ECOLog.showLog("STOP")
    }

    fun onActivityDestroyed(activity: Activity) {
        ECOLog.showLog("onActivityStarted")
        currentActivity = null
    }


    private fun loadAd() {
        admobAppOpen.loadAd()
    }

    private fun showAd(activity: Activity) {
        if (isLocked) return
        admobAppOpen.showAdIfAvailable(activity) {
        }
    }

}*/

package com.example.openappads.admob.openapp

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.constants.admob.ADS_OPEN_APP_UNIT_ID_DEFAULT
import com.example.openappads.screens.SplashActivity
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdmobAppOpenApplication(
    private val application: Application, private val context: Context
) : DefaultLifecycleObserver {

    private val admobAppOpen by lazy {
        AdmobAppOpen(context).apply {
            setAdUnitId(ADS_OPEN_APP_UNIT_ID_DEFAULT)
            listener = object : OpenAppAdListener {
                override fun onAdLoaded() {
                    ECOLog.showLog("Ad loaded successfully")
                }

                override fun onFailedAdLoad(message: String) {
                    ECOLog.showLog("Ad failed to load: $message")
                }

                override fun onAdDismiss() {
                    ECOLog.showLog("Ad dismissed")
                    loadAd()
                }
            }
        }
    }

    private var currentActivity: Activity? = null
    private var isLocked = false
    private var isMobileAdsInitialized = false



    fun initialize() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(application) {
                isMobileAdsInitialized = true
                loadAd()
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun locked() {
        isLocked = true
    }

    fun unlock() {
        isLocked = false
    }


    fun onActivityStarted(activity: Activity) {
        ECOLog.showLog("onActivityStarted: ${activity.javaClass.simpleName}")
        currentActivity = activity
    }

    fun onActivityDestroyed(activity: Activity) {
        ECOLog.showLog("onActivityDestroyed: ${activity.javaClass.simpleName}")
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        if (currentActivity is SplashActivity) return
        if (admobAppOpen.isShowing()) return
        if (isLocked) return

        showAd(currentActivity!!)
    }

    override fun onStop(owner: LifecycleOwner) {
        ECOLog.showLog("Application onStop")
    }

    private fun loadAd() {
        admobAppOpen.loadAd()
    }

    private fun showAd(activity: Activity) {
        if (isLocked) return
        admobAppOpen.attachOverlayToActivity(activity)
        admobAppOpen.showAdIfAvailable(activity) {}
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        admobAppOpen.destroyAd()
    }
}