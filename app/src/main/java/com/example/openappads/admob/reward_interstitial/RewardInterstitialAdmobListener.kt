package com.example.openappads.admob.reward_interstitial

interface RewardInterstitialAdmobListener {
    fun onAdLoaded()
    fun onFailedAdLoad(error: String)
    fun onAdDismiss()
    fun onFailedToShow(error: String)
}