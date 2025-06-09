package com.example.openappads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.admob.openapp.AdmobAppOpenApplication
import com.example.openappads.constants.admob.ADS_OPEN_APP_UNIT_ID_DEFAULT

class MyApplication : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    val admobAppOpenManager by lazy {  AdmobAppOpenApplication(this, applicationContext) }

    override fun onCreate() {
        super<MultiDexApplication>.onCreate()
        registerActivityLifecycleCallbacks(this)

        ECOLog.showLog("onCreate")

        admobAppOpenManager.initialize()
        ProcessLifecycleOwner.get().lifecycle.addObserver(admobAppOpenManager)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        admobAppOpenManager.onActivityStarted(activity)
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        admobAppOpenManager.onActivityDestroyed(activity)
    }

    /*fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OpenAppApplication.OnShowAdCompleteListener) {
        openAppAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    fun loadAdWithCallback(activity: Activity, onLoaded: () -> Unit) {
        openAppAdManager.loadAdWithCallback(activity, onLoaded)
    }*/
}