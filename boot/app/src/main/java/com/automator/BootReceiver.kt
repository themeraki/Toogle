package com.automator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences(AutomationState.PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(AutomationState.KEY_ENABLED, false)) {
            val serviceIntent = Intent(context, ChargerMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
