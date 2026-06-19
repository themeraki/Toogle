package com.automator

object AutomationState {
    const val PREFS = "hotspot_automator_prefs"
    const val KEY_PASSCODE = "passcode"
    const val KEY_ENABLED = "automation_enabled"

    @Volatile
    var requestedHotspotEnabled: Boolean? = null
}
