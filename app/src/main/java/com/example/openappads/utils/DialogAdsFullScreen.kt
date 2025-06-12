package com.example.openappads.utils

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import com.eco.iconchanger.theme.widget.utils.ECOLog
import com.example.openappads.R

class DialogAdsFullScreen(context: Context) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_full_screen)
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun showDialog() {
        if(!isShowing) {
            show()
        }
    }

    fun hideDialog() {
        dismiss()
    }
}