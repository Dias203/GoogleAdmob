package com.example.openappads.admob.reward

interface RewardListener {
    fun onAdLoaded()
    fun onAdFailedToLoad(error: String)
    fun onAdDismiss()
    fun onFailedToShow(error: String)
    fun onShowed()
}