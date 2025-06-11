package com.example.openappads.admob.reward

interface RewardListener {
    fun onAdLoaded()
    fun onAdFailedToLoad(error: String)
    fun onShowFullScreen(isDismiss: Boolean)
}