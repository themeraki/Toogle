package com.automator

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ChargerService : Service() {

    private val powerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    AutoHotspotService.targetState = true
                    invokeSystemHotspotScreen(context)
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    AutoHotspotService.targetState = false
                    invokeSystemHotspotScreen(context)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        registerReceiver(powerReceiver, filter)
        deployPersistentNotification()
    }

    private fun invokeSystemHotspotScreen(context: Context) {
        try {
            val intent = Intent().apply {
                action = Intent.ACTION_MAIN
                setClassName("com.android.settings", "com.android.settings.Settings\$TetherSettingsActivity")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallback = Intent("android.settings.WIRELESS_SETTINGS").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }

    private fun deployPersistentNotification() {
        val channelId = "automator_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Hotspot Tracking Active", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Hotspot Automation is Running")
            .setContentText("Monitoring system power status changes...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .build()

        startForeground(101, notification)
    }

    override fun onDestroy() {
        unregisterReceiver(powerReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null
}
