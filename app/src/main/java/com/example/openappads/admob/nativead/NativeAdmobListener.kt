package com.example.openappads.admob.nativead

interface NativeAdmobListener {
    fun onAdLoaded()
    fun onFailedAdLoad(error: String)
    fun onAdChoicesClicked()
}