package com.example.openappads

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.openappads.admob.interstitial.AdmobInterstitial
import com.example.openappads.admob.reward.AdmobReward
import com.example.openappads.admob.reward_interstitial.AdmobRewardInterstitial
import com.example.openappads.utils.DialogAdsFullScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseActivity : AppCompatActivity() {

    val dialogAdsFullScreen by lazy { DialogAdsFullScreen(this) }

    fun showAdWithTimeout(seconds: Int, condition: Any, onComplete: () -> Unit) {
        var job: Job? = null
        val delayStep = ((seconds * 1000) / 100).toLong()
        job = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                var progress = 0
                while (isActive && progress <= 100) {
                    val isReady = when (condition) {
                        is AdmobReward -> condition.isAdReady() || condition.isError()
                        is AdmobRewardInterstitial -> condition.isAdReady() || condition.isError()
                        is AdmobInterstitial -> condition.isAdReady() || condition.isError()
                        else -> false
                    }
                    if (isReady && progress >= 10) break
                    delay(delayStep)
                    progress++
                }
                withContext(Dispatchers.Main) {
                    if (isActive) onComplete()
                }

                job?.cancel()
                job = null
            }
        }
    }

}