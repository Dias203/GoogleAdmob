package com.eco.iconchanger.theme.widget.utils

import android.util.Log

class ECOLog {

    companion object {
        @JvmStatic
        val TAG = "ECOLog"

        @JvmStatic
        var isLogEnable = true

        @JvmStatic
        fun getCurrentMethod(): String {
            return try {
                val stacktraceObj = Thread.currentThread().stackTrace
                val stackTraceElement = stacktraceObj[5]
                var className = stackTraceElement.className
                className = className.substring(className.lastIndexOf(".") + 1, className.length)
                " [" + className + "] " + stackTraceElement.methodName
            } catch (e: Exception) {
                ""
            }
        }

        @JvmStatic
        fun composeDefaultMessage(message: String): String {
            return getCurrentMethod() + " = " + message
        }

        @JvmStatic
        fun state(enable: Boolean) {
            isLogEnable = enable
        }

        @JvmStatic
        fun showLog(message: Any?) {
            if (!isLogEnable) {
                return
            }
            Log.i(TAG, composeDefaultMessage(message.toString()))
        }
    }
}