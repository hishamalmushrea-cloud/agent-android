package ai.arena.androidagent.tools

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import ai.arena.androidagent.agent.*

class AppResolver(private val context: Context) {
    fun resolve(query: String): Intent? {
        val pm = context.packageManager
        val launchables = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), PackageManager.MATCH_ALL)
        val normalized = query.trim().lowercase()
        val aliases = mapOf("واتساب" to "whatsapp", "الواتس" to "whatsapp", "يوتيوب" to "youtube", "الخرائط" to "maps", "خرائط" to "maps")
        val needle = aliases[normalized] ?: normalized
        return launchables.firstOrNull { info ->
            val label = info.loadLabel(pm).toString().lowercase()
            label == needle || label.contains(needle) || info.activityInfo.packageName.contains(needle)
        }?.let { pm.getLaunchIntentForPackage(it.activityInfo.packageName) }
    }
}

class OpenAppTool(private val context: Context) : AgentTool<String> {
    private val resolver = AppResolver(context)
    override val spec = ToolSpec("open_app", "العثور على تطبيق مثبت وفتحه", Risk.LOW)
    override suspend fun execute(input: String): ToolResult {
        val intent = resolver.resolve(input) ?: return ToolResult(ToolResult.Status.FAILED, "لم أجد تطبيقًا مثبتًا باسم $input")
        return try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            ToolResult(ToolResult.Status.SUCCESS, "تم فتح التطبيق", mapOf("query" to input))
        } catch (_: Exception) { ToolResult(ToolResult.Status.FAILED, "تعذر فتح التطبيق") }
    }
}
