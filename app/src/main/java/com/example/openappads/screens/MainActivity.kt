package com.example.openappads.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.BaseActivity
import com.example.openappads.MyApplication
import com.example.openappads.admob.banner.BannerAd
import com.example.openappads.admob.reward_interstitial.AdmobRewardInterstitial
import com.example.openappads.admob.reward.AdmobReward
import com.example.openappads.databinding.ActivityMainBinding
import com.example.openappads.extensions.loadAdMob
import com.example.openappads.extensions.onActivityDestroyed
import com.example.openappads.extensions.setOnClick

class MainActivity : BaseActivity() {
    internal lateinit var binding: ActivityMainBinding

    val bannerAd by lazy { BannerAd(applicationContext) }
    val rewardAd by lazy { AdmobReward(applicationContext) }
    val rewardInterstitialAd by lazy { AdmobRewardInterstitial(applicationContext) }
    val admobOpenAppManager by lazy {
        (applicationContext as MyApplication).admobAppOpenManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAdMob()
        setOnClick()
    }


    override fun onPause() {
        super.onPause()
        ECOLog.showLog("Pause")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        ECOLog.showLog("onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        onActivityDestroyed()
    }
}
