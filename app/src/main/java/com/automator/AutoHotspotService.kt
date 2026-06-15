package com.automator

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoHotspotService : AccessibilityService() {

    companion object {
        // Shared state handled between Broadcast tracking state and Accessibility context action
        var targetState: Boolean? = null 
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (targetState == null) return

        val nodeInfo = rootInActiveWindow ?: return
        evaluateAndToggleInterfaceNodes(nodeInfo)
    }

    private fun evaluateAndToggleInterfaceNodes(node: AccessibilityNodeInfo) {
        // Targets system toggle interaction elements dynamically
        if (node.className == "android.widget.Switch" || node.className == "android.widget.CompoundButton") {
            val switchCurrentValue = node.isChecked
            
            if (targetState == true && !switchCurrentValue) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                targetState = null 
                performGlobalAction(GLOBAL_ACTION_HOME) // Automatically returns back to workspace layout
            } else if (targetState == false && switchCurrentValue) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                targetState = null 
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
        }

        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i) ?: continue
            evaluateAndToggleInterfaceNodes(childNode)
        }
    }

    override fun onInterrupt() {}
}
