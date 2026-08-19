package ai.arena.androidagent.agent

/** Safe, provider-independent action contract. AI may propose only these typed actions. */
sealed interface AgentAction {
    data class OpenApp(val query: String) : AgentAction
    data object GoBack : AgentAction
    data object GoHome : AgentAction
    data object OpenRecents : AgentAction
    data object PlayMedia : AgentAction
    data object PauseMedia : AgentAction
    data class SetVolume(val percent: Int) : AgentAction
    data class Call(val personOrNumber: String) : AgentAction
    data class SendSms(val recipient: String, val body: String) : AgentAction
    data class Wait(val millis: Long) : AgentAction
    data class CreateReminder(val text: String, val delayMs: Long) : AgentAction
}

enum class Risk { LOW, MEDIUM, HIGH }
data class ActionPlan(val original: String, val actions: List<AgentAction>, val risk: Risk, val needsConfirmation: Boolean)

enum class AgentState { IDLE, LISTENING, THINKING, PLANNING, WAITING_PERMISSION, CONFIRMING, EXECUTING, VERIFYING, WAITING, RECOVERING, SUCCESS, FAILED, CANCELLED }

interface LocalCommandParser { fun parse(input: String): ActionPlan? }

/** Arabic fast-path for safe, deterministic commands; never executes side effects itself. */
class ArabicFastPath : LocalCommandParser {
    override fun parse(input: String): ActionPlan? {
        val s = input.trim().lowercase()
        val action = when {
            s.matches(Regex("(افتح|شغل|دخلني على)\\s+واتس(اب)?|open whatsapp")) -> AgentAction.OpenApp("WhatsApp")
            s.matches(Regex("(افتح|شغل|دخلني على)\\s+.+")) -> AgentAction.OpenApp(s.replaceFirst(Regex("^(افتح|شغل|دخلني على)\\s+"), "").trim())
            s in listOf("ارجع", "رجوع", "للخلف") -> AgentAction.GoBack
            s in listOf("الرئيسية", "روح الرئيسية", "اذهب للرئيسية") -> AgentAction.GoHome
            s.contains("التطبيقات الأخيرة") || s == "اخر التطبيقات" -> AgentAction.OpenRecents
            s in listOf("شغل الموسيقى", "شغلها", "شغل الأغنية") -> AgentAction.PlayMedia
            s in listOf("وقف الموسيقى", "وقفها", "أوقف الأغنية") -> AgentAction.PauseMedia
            s.matches(Regex("ذكرني بعد \\d+ (دقيقة|دقائق|ساعة|ساعات).+")) -> {
                val n = Regex("\\d+").find(s)?.value?.toLongOrNull() ?: return null
                val unit = if (s.contains("ساعة")) 3_600_000L else 60_000L
                AgentAction.CreateReminder(s, n * unit)
            }
            s.startsWith("اتصل بـ") || s.startsWith("اتصل ب") || s.startsWith("دق على") || s.startsWith("كلم ") -> {
                val person = s.replaceFirst(Regex("^(اتصل\\s+بـ?|دق\\s+على|كلم\\s+)"), "").trim()
                if (person.isBlank()) return null else AgentAction.Call(person)
            }
            else -> null
        } ?: return null
        return ActionPlan(input, listOf(action), Risk.LOW, false)
    }
}
