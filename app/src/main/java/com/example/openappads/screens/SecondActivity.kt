package com.example.openappads.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.openappads.BaseActivity
import com.example.openappads.MyApplication
import com.example.openappads.R
import com.example.openappads.admob.banner.BannerAd
import com.example.openappads.admob.interstitial.AdmobInterstitial
import com.example.openappads.admob.nativead.NativeAdmob
import com.example.openappads.databinding.ActivitySecondBinding
import com.example.openappads.extensions.loadAdMob
import com.example.openappads.extensions.onActivityDestroyed
import com.example.openappads.extensions.openMainActivity
import com.example.openappads.extensions.setLoadingState
import com.example.openappads.extensions.setOnClick

class SecondActivity : BaseActivity(){
    lateinit var binding: ActivitySecondBinding
    val bannerAd by lazy { BannerAd(this) }
    val interstitialAd by lazy { AdmobInterstitial(applicationContext) }
    val nativeAd by lazy { NativeAdmob(applicationContext) }
    val admobOpenAppManager by lazy {
        (applicationContext as MyApplication).admobAppOpenManager
    }

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

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        dialogAdsFullScreen.showDialog()
        admobOpenAppManager.locked()
        showAdWithTimeout(6, interstitialAd) {
            if(interstitialAd.isAdReady()) {
                interstitialAd.showAd(this)
            }
            else{
                openMainActivity()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onActivityDestroyed()
    }
}