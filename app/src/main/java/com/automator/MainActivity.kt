package com.automator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("AutomatorSecurityConfig", Context.MODE_PRIVATE)

        val layoutPassword = findViewById<LinearLayout>(R.id.layoutPassword)
        val layoutDashboard = findViewById<LinearLayout>(R.id.layoutDashboard)
        val tvPasswordTitle = findViewById<TextView>(R.id.tvPasswordTitle)
        val etPasswordInput = findViewById<EditText>(R.id.etPasswordInput)
        val btnPasswordAction = findViewById<Button>(R.id.btnPasswordAction)
        
        val btnAccessibilityPerm = findViewById<Button>(R.id.btnAccessibilityPerm)
        val btnToggleTracking = findViewById<Button>(R.id.btnToggleTracking)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val btnUpdatePassword = findViewById<Button>(R.id.btnUpdatePassword)

        val hasPasswordSet = prefs.contains("app_lock_key")

        if (!hasPasswordSet) {
            tvPasswordTitle.text = "Create App Password"
            btnPasswordAction.text = "Save & Set"
        } else {
            tvPasswordTitle.text = "Enter Access Password"
            btnPasswordAction.text = "Unlock App"
        }

        btnPasswordAction.setOnClickListener {
            val input = etPasswordInput.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!prefs.contains("app_lock_key")) {
                prefs.edit().putString("app_lock_key", input).apply()
                Toast.makeText(this, "Password Configuration Saved!", Toast.LENGTH_SHORT).show()
                layoutPassword.visibility = View.GONE
                layoutDashboard.visibility = View.VISIBLE
            } else {
                val savedPass = prefs.getString("app_lock_key", "")
                if (input == savedPass) {
                    layoutPassword.visibility = View.GONE
                    layoutDashboard.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, "Incorrect Password! Access Denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnAccessibilityPerm.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        btnToggleTracking.setOnClickListener {
            val serviceIntent = Intent(this, ChargerService::class.java)
            startService(serviceIntent)
            Toast.makeText(this, "Charger Target Tracker Background Service Started", Toast.LENGTH_SHORT).show()
        }

        btnUpdatePassword.setOnClickListener {
            val newPass = etNewPassword.text.toString().trim()
            if (newPass.isNotEmpty()) {
                prefs.edit().putString("app_lock_key", newPass).apply()
                etNewPassword.text.clear()
                Toast.makeText(this, "Password Updated Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
