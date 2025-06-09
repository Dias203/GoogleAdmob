package com.example.openappads.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        val intentAd = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        showInterstitialAd(intentAd)
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