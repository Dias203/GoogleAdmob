package com.example.openappads.admob.interstitial

interface InterstitialAdmobListener {
    fun onAdLoaded()
    fun onFailedAdLoad(error: String)
    fun onAdDismiss()
    fun onFailedToShow(error: String)
}