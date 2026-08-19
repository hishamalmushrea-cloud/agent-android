package ai.arena.androidagent.tools

import android.content.Context
import android.content.Intent
import android.provider.Settings
import ai.arena.androidagent.agent.*

class OpenSettingsTool(private val context: Context) : AgentTool<String> {
    override val spec = ToolSpec("open_settings", "فتح صفحة إعدادات النظام", Risk.LOW)
    override suspend fun execute(input: String): ToolResult {
        val action = when (input.lowercase()) {
            "wifi" -> Settings.ACTION_WIFI_SETTINGS
            "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
            "sound" -> Settings.ACTION_SOUND_SETTINGS
            "display" -> Settings.ACTION_DISPLAY_SETTINGS
            "accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
            else -> Settings.ACTION_SETTINGS
        }
        return try { context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); ToolResult(ToolResult.Status.SUCCESS, "تم فتح الإعدادات") }
        catch (_: Exception) { ToolResult(ToolResult.Status.FAILED, "تعذر فتح الإعدادات") }
    }
}

class NavigationTool(private val context: Context) : AgentTool<AgentAction> {
    override val spec = ToolSpec("system_navigation", "التنقل بين الرجوع والرئيسية والتطبيقات الأخيرة", Risk.LOW)
    override suspend fun execute(input: AgentAction): ToolResult {
        // Back and Recents are global actions and require an enabled AccessibilityService.
        if (input != AgentAction.GoHome) return ToolResult(ToolResult.Status.NEEDS_PERMISSION, "يلزم تفعيل خدمة الوصول لتنفيذ هذا التنقل")
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try { context.startActivity(intent); ToolResult(ToolResult.Status.SUCCESS, "تم الانتقال إلى الرئيسية") }
        catch (_: Exception) { ToolResult(ToolResult.Status.FAILED, "تعذر الانتقال إلى الرئيسية") }
    }
}
