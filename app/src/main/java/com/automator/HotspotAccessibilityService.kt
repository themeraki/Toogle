package com.automator

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class HotspotAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val desired = AutomationState.requestedHotspotEnabled ?: return
        val root = rootInActiveWindow ?: return
        if (tapMatchingSwitch(root, desired)) {
            AutomationState.requestedHotspotEnabled = null
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    private fun tapMatchingSwitch(node: AccessibilityNodeInfo, desired: Boolean): Boolean {
        val className = node.className?.toString().orEmpty()
        val clickableSwitch = className.contains("Switch", ignoreCase = true) || className.contains("CompoundButton", ignoreCase = true)
        if (clickableSwitch && node.isCheckable && node.isChecked != desired) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        for (index in 0 until node.childCount) {
            node.getChild(index)?.let { child ->
                if (tapMatchingSwitch(child, desired)) return true
            }
        }
        return false
    }

    override fun onInterrupt() = Unit
}
