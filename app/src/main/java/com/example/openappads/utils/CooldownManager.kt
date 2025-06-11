package com.example.openappads.utils

import com.eco.iconchanger.theme.widget.utils.ECOLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CooldownManager(private val lifecycleScope: CoroutineScope) {
    private var isCooldown = false
    private val cooldownDurationMillis = 20000L

    fun tryShowAd(onShowAd: () -> Unit) {
        if(!isCooldown) {
            onShowAd.invoke()
        }
    }

    fun startCooldown() {
        ECOLog.showLog("Cool down")
        isCooldown = true
        lifecycleScope.launch {
            delay(cooldownDurationMillis)
            isCooldown = false
        }
    }
}