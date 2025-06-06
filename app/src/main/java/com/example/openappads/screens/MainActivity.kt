package com.example.openappads.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.MyApplication
import com.example.openappads.admob.banner.BannerAd
import com.example.openappads.admob.interstitial.InterstitialAdmob
import com.example.openappads.admob.reward.RewardAdmob
import com.example.openappads.admob.reward_interstitial.RewardInterstitialAdmob
import com.example.openappads.databinding.ActivityMainBinding
import com.example.openappads.extensions.loadAdMob
import com.example.openappads.extensions.onActivityDestroyed
import com.example.openappads.extensions.onActivityPaused
import com.example.openappads.extensions.onActivityResumed
import com.example.openappads.extensions.setOnClick

class MainActivity : AppCompatActivity() {
    internal lateinit var binding: ActivityMainBinding

    val bannerAd by lazy { BannerAd(applicationContext) }
    val interstitialAd by lazy { InterstitialAdmob(applicationContext) }
    val rewardAd by lazy { RewardAdmob(applicationContext) }
    val rewardInterstitialAd by lazy { RewardInterstitialAdmob(applicationContext) }

    val admobOpenAppManager by lazy {
        (applicationContext as MyApplication).admobAppOpenManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ECOLog.showLog("onCreate")

        loadAdMob()
        setOnClick()
    }


    override fun onPause() {
        super.onPause()
        onActivityPaused()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        onActivityResumed()
    }

    override fun onDestroy() {
        super.onDestroy()
        onActivityDestroyed()
    }
}
