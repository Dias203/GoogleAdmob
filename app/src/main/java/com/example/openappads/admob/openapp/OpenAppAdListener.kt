package com.example.openappads.admob.openapp

interface OpenAppAdListener {
    fun onAdLoaded()
    fun onFailedAdLoad(errorMessage: String)
    fun onAdDismiss()
}