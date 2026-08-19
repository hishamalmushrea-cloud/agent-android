package ai.arena.androidagent.tools

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import ai.arena.androidagent.agent.*

class MediaControlTool(context: Context) : AgentTool<AgentAction> {
    private val audio = context.getSystemService(AudioManager::class.java)
    override val spec = ToolSpec("media_control", "التحكم في جلسة الوسائط النشطة", Risk.LOW)
    override suspend fun execute(input: AgentAction): ToolResult {
        val key = when (input) {
            AgentAction.PlayMedia -> KeyEvent.KEYCODE_MEDIA_PLAY
            AgentAction.PauseMedia -> KeyEvent.KEYCODE_MEDIA_PAUSE
            else -> return ToolResult(ToolResult.Status.FAILED, "تحكم الوسائط غير مدعوم")
        }
        return try {
            audio.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, key))
            audio.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, key))
            ToolResult(ToolResult.Status.UNKNOWN, "أرسلت أمر الوسائط، لكن يلزم MediaSession للتحقق")
        } catch (_: Exception) { ToolResult(ToolResult.Status.FAILED, "تعذر التحكم في الوسائط") }
    }
}
