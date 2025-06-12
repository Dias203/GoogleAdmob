package com.example.openappads.utils

object CoolOffTime {
    private var lastTimeAdsShow = 0L
    private const val coolOffTime = 20000L

    fun setLastTimeAdsShow() {
        lastTimeAdsShow = System.currentTimeMillis()
    }

    fun finnishCoolOffTime() : Boolean {
        return System.currentTimeMillis() - lastTimeAdsShow >= coolOffTime // show ad
    }
}