package com.example.openappads.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.MyApplication
import com.example.openappads.admob.banner.BannerAd
import com.example.openappads.admob.reward_interstitial.AdmobRewardInterstitial
import com.example.openappads.admob.reward.AdmobReward
import com.example.openappads.databinding.ActivityMainBinding
import com.example.openappads.extensions.loadAdMob
import com.example.openappads.extensions.onActivityDestroyed
import com.example.openappads.extensions.progressUpdatedReward
import com.example.openappads.extensions.progressUpdatedRewardInterstitial
import com.example.openappads.extensions.setOnClick
import com.example.openappads.utils.CountDownTimer

class MainActivity : AppCompatActivity(), CountDownTimer.UpdateProgress {
    internal lateinit var binding: ActivityMainBinding

    val bannerAd by lazy { BannerAd(applicationContext) }
    val rewardAd by lazy { AdmobReward(applicationContext) }
    val rewardInterstitialAd by lazy { AdmobRewardInterstitial(applicationContext) }
    val countDownTimer by lazy { CountDownTimer() }
    val admobOpenAppManager by lazy {
        (applicationContext as MyApplication).admobAppOpenManager
    }
    var isAdRequest = false

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
        countDownTimer.stopJob()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        ECOLog.showLog("onResume")
        if(!isAdRequest) return
        if (rewardAd.isLoading() || rewardAd.isAdReady()) {
            countDownTimer.startJob(this)
        }
        if (rewardInterstitialAd.isLoading() || rewardInterstitialAd.isAdReady()) {
            countDownTimer.startJob(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onActivityDestroyed()
    }

   override fun onUpdateProgress(count: Int) {
        progressUpdatedReward()
        progressUpdatedRewardInterstitial()
    }
}
