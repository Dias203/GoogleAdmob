//package com.example.openappads.screens
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.openappads.MyApplication
//import com.example.openappads.R
//import com.example.openappads.admob.banner.BannerAd
//import com.example.openappads.admob.reward_interstitial.AdmobInterstitial
//import com.example.openappads.admob.interstitial.InterstitialAdmob
//import com.example.openappads.admob.interstitial.InterstitialAdmobListener
//import com.example.openappads.admob.nativead.NativeAdmob
//import com.example.openappads.databinding.ActivitySecondBinding
//import com.example.openappads.extensions.loadAdMob
//import com.example.openappads.extensions.onActivityDestroyed
//import com.example.openappads.extensions.onActivityPaused
//import com.example.openappads.extensions.onActivityResumed
//import com.example.openappads.extensions.setLoadingState
//import com.example.openappads.extensions.setOnClick
//import com.example.openappads.utils.CountDownTimer
//
//class SecondActivity : AppCompatActivity(){
//    lateinit var binding: ActivitySecondBinding
//    val bannerAd by lazy { BannerAd(this) }
//    val interstitialAd by lazy { AdmobInterstitial(applicationContext) }
//    val nativeAd by lazy { NativeAdmob(applicationContext) }
//    val admobOpenAppManager by lazy {
//        (applicationContext as MyApplication).admobAppOpenManager
//    }
//
//    val countDownTimer by lazy { CountDownTimer() }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySecondBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bgSecond)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        setOnClick()
//        loadAdMob()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        onActivityPaused()
//    }
//
//    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
//    override fun onBackPressed() {
//        setLoadingState(true)
//        admobOpenAppManager.locked()
//        super.onBackPressed()
//        val intent = Intent(this, MainActivity::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        }
//
//        interstitialAd.listener = object : InterstitialAdmobListener {
//            override fun onAdDismiss() {
//                startActivity(intent)
//                setLoadingState(false)
//                admobOpenAppManager.unlock()
//                finish()
//            }
//
//            override fun onAdLoaded() {}
//            override fun onFailedAdLoad(error: String) {
//                startActivity(intent)
//                setLoadingState(false)
//                finish()
//            }
//
//            override fun onFailedToShow(error: String) {
//                startActivity(intent)
//                finish()
//            }
//        }
//
//        interstitialAd.showInterstitialAd(this)
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        //onActivityResumed()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        onActivityDestroyed()
//    }
//}


package com.example.openappads.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.openappads.MyApplication
import com.example.openappads.R
import com.example.openappads.admob.banner.BannerAd
import com.example.openappads.admob.interstitial.AdmobInterstitial
import com.example.openappads.admob.nativead.NativeAdmob
import com.example.openappads.databinding.ActivitySecondBinding
import com.example.openappads.extensions.loadAdMob
import com.example.openappads.extensions.onActivityDestroyed
import com.example.openappads.extensions.progressUpdatedInterstitial
import com.example.openappads.extensions.setLoadingState
import com.example.openappads.extensions.setOnClick
import com.example.openappads.extensions.showInterstitialAd
import com.example.openappads.utils.CooldownManager
import com.example.openappads.utils.CountDownTimer

class SecondActivity : AppCompatActivity(), CountDownTimer.UpdateProgress {
    lateinit var binding: ActivitySecondBinding
    val bannerAd by lazy { BannerAd(this) }
    val interstitialAd by lazy { AdmobInterstitial(applicationContext) }
    val nativeAd by lazy { NativeAdmob(applicationContext) }
    val admobOpenAppManager by lazy {
        (applicationContext as MyApplication).admobAppOpenManager
    }
    val handler = Handler(Looper.getMainLooper())
    var isAdRequest = false
    val countDownTimer by lazy { CountDownTimer() }
    val cooldownAd by lazy { CooldownManager(lifecycleScope) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bgSecond)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setOnClick()
        loadAdMob()
    }

    override fun onPause() {
        super.onPause()
        countDownTimer.stopJob()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        setLoadingState(true)
        admobOpenAppManager.locked()
        showInterstitialAd()
    }


    override fun onResume() {
        super.onResume()
        if (isAdRequest && (interstitialAd.isLoading() || interstitialAd.isAdReady())) {
            countDownTimer.startJob(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onActivityDestroyed()
    }

    override fun onUpdateProgress(count: Int) {
        progressUpdatedInterstitial()
    }
}