package com.automator

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper

object TetheringController {
    private const val TETHERING_WIFI = 0

    fun setWifiHotspot(context: Context, enabled: Boolean): Boolean {
        return if (enabled) startWifiTethering(context) else stopWifiTethering(context)
    }

    private fun startWifiTethering(context: Context): Boolean {
        return startWithTetheringManager(context) || startWithConnectivityManager(context)
    }

    private fun stopWifiTethering(context: Context): Boolean {
        return stopWithTetheringManager(context) || stopWithConnectivityManager(context)
    }

    private fun startWithTetheringManager(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return false
        return runCatching {
            val service = context.getSystemService("tethering") ?: return false
            val requestClass = Class.forName("android.net.TetheringManager\$TetheringRequest")
            val builderClass = Class.forName("android.net.TetheringManager\$TetheringRequest\$Builder")
            val callbackClass = Class.forName("android.net.TetheringManager\$StartTetheringCallback")
            val requestBuilder = builderClass.getConstructor(Int::class.javaPrimitiveType).newInstance(TETHERING_WIFI)
            val request = builderClass.getMethod("build").invoke(requestBuilder)
            val executor = context.mainExecutor
            val callback = callbackClass.getConstructor().newInstance()
            service.javaClass.getMethod("startTethering", requestClass, java.util.concurrent.Executor::class.java, callbackClass)
                .invoke(service, request, executor, callback)
            true
        }.getOrDefault(false)
    }

    private fun stopWithTetheringManager(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return false
        return runCatching {
            val service = context.getSystemService("tethering") ?: return false
            service.javaClass.getMethod("stopTethering", Int::class.javaPrimitiveType).invoke(service, TETHERING_WIFI)
            true
        }.getOrDefault(false)
    }

    private fun startWithConnectivityManager(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return runCatching {
            val callbackClass = Class.forName("android.net.ConnectivityManager\$OnStartTetheringCallback")
            val callback = callbackClass.getConstructor().newInstance()
            manager.javaClass.getMethod(
                "startTethering",
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                callbackClass,
                Handler::class.java
            ).invoke(manager, TETHERING_WIFI, true, callback, Handler(Looper.getMainLooper()))
            true
        }.getOrDefault(false)
    }

    private fun stopWithConnectivityManager(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return runCatching {
            manager.javaClass.getMethod("stopTethering", Int::class.javaPrimitiveType).invoke(manager, TETHERING_WIFI)
            true
        }.getOrDefault(false)
    }
}
