package com.example.openappads.admob.banner

interface BannerListener {
    fun onLoad()
    fun onLoadFail(error: String)
}