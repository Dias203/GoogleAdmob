package com.example.openappads.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.openappads.R
import com.example.openappads.admob.openapp.AdmobAppOpen
import com.example.openappads.extensions.loadAppOpenAdSplash
import com.example.openappads.extensions.startMainActivity
import com.example.openappads.utils.SplashCountDown
import com.example.openappads.utils.SplashCountDown.ProgressUpdated

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), ProgressUpdated {
    val admobAppOpen by lazy { AdmobAppOpen(applicationContext) }
    val coroutineCountDown by lazy { SplashCountDown() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loadAppOpenAdSplash()

    }

    override fun onPause() {
        super.onPause()
        coroutineCountDown.stopJob()
    }

    override fun onResume() {
        super.onResume()
        coroutineCountDown.startJob(this)
    }

    override fun onProgressUpdated(count: Int) {
        if (admobAppOpen.isShowing()) return
        findViewById<TextView>(R.id.timer).text = count.toString()

        if(coroutineCountDown.isProgressMax()){
            startMainActivity()
            return
        }
        if (admobAppOpen.isLoaded()) {
            coroutineCountDown.stopJob()
            admobAppOpen.showAdIfAvailable(this) {
                startMainActivity()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        admobAppOpen.destroyAd()
        coroutineCountDown.destroy()
    }
}