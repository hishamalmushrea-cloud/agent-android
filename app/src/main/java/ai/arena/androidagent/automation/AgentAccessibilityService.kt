package ai.arena.androidagent.automation

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.os.Bundle

/**
 * Opt-in automation boundary. It uses semantic nodes and never coordinates.
 * External-app adapters must remain explicit and bounded; this service does not
 * bypass authentication, secure screens, or user confirmation.
 */
class AgentAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) { /* screen state observers are added in the next milestone */ }
    override fun onInterrupt() = Unit

    fun findByText(text: String): List<AccessibilityNodeInfo> =
        rootInActiveWindow?.findAccessibilityNodeInfosByText(text).orEmpty()

    fun click(node: AccessibilityNodeInfo): Boolean =
        node.isEnabled && node.isClickable && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)

    fun setText(node: AccessibilityNodeInfo, text: String): Boolean {
        if (!node.isEditable) return false
        return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        })
    }

    fun scrollForward(node: AccessibilityNodeInfo): Boolean =
        node.isScrollable && node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
}
