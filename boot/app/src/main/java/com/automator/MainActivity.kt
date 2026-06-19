package com.automator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.Activity

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences(AutomationState.PREFS, Context.MODE_PRIVATE) }
    private lateinit var lockPanel: LinearLayout
    private lateinit var dashboardPanel: LinearLayout
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lockPanel = findViewById(R.id.lockPanel)
        dashboardPanel = findViewById(R.id.dashboardPanel)
        statusText = findViewById(R.id.statusText)

        val passwordTitle = findViewById<TextView>(R.id.passwordTitle)
        val passcodeInput = findViewById<EditText>(R.id.passcodeInput)
        val passcodeButton = findViewById<Button>(R.id.passcodeButton)
        val hasPasscode = prefs.contains(AutomationState.KEY_PASSCODE)
        passwordTitle.text = if (hasPasscode) "Enter app passcode" else "Create app passcode"

        passcodeButton.setOnClickListener {
            val typed = passcodeInput.text.toString().trim()
            if (typed.isBlank()) {
                toast("Enter a passcode first")
            } else if (!prefs.contains(AutomationState.KEY_PASSCODE)) {
                prefs.edit().putString(AutomationState.KEY_PASSCODE, typed).apply()
                unlock()
            } else if (typed == prefs.getString(AutomationState.KEY_PASSCODE, null)) {
                unlock()
            } else {
                toast("Wrong passcode")
            }
        }

        findViewById<Button>(R.id.notificationPermissionButton).setOnClickListener { askNotificationPermission() }
        findViewById<Button>(R.id.accessibilityButton).setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        findViewById<Button>(R.id.startButton).setOnClickListener { startAutomation() }
    }

    private fun unlock() {
        lockPanel.visibility = View.GONE
        dashboardPanel.visibility = View.VISIBLE
        renderStatus()
    }

    private fun startAutomation() {
        prefs.edit().putBoolean(AutomationState.KEY_ENABLED, true).apply()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(Intent(this, ChargerMonitorService::class.java)) else startService(Intent(this, ChargerMonitorService::class.java))
        toast("Automation started")
        renderStatus()
    }

    private fun renderStatus() {
        val enabled = prefs.getBoolean(AutomationState.KEY_ENABLED, false)
        statusText.text = if (enabled) "Automation is enabled." else "Automation is stopped."
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
        } else {
            toast("Notification permission is already allowed or not needed")
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
