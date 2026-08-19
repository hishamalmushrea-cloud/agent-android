package ai.arena.androidagent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ai.arena.androidagent.agent.*
import ai.arena.androidagent.tools.*
import ai.arena.androidagent.data.*
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AgentViewModel(app: Application) : AndroidViewModel(app) {
    private val parser: LocalCommandParser = ArabicFastPath()
    private val riskEngine = RiskEngine()
    private val registry = ToolRegistry(listOf(OpenAppTool(app), NavigationTool(app), DialContactTool(app)))
    private val scheduler = TaskScheduler(app)
    val tasks = AgentDatabase.get(app).tasks().observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private var pendingConfirmation: AgentAction? = null
    private val _status = MutableStateFlow("جاهز — ماذا تريد مني؟")
    val status: StateFlow<String> = _status

    fun submit(command: String) = viewModelScope.launch {
        val plan = parser.parse(command)
        if (plan == null) { _status.value = "لا أستطيع تفسير الأمر محليًا بعد؛ لم يتم إرسال شيء للخارج."; return@launch }
        _status.value = "أخطط لتنفيذ الأمر…"
        val action = plan.actions.single()
        if (action is AgentAction.CreateReminder) {
            val id = UUID.randomUUID().toString()
            scheduler.schedule(TaskEntity(id, command, System.currentTimeMillis() + action.delayMs))
            _status.value = "تمت جدولة التذكير."
            return@launch
        }
        if (riskEngine.requiresConfirmation(action)) {
            pendingConfirmation = action
            _status.value = "هذا الإجراء قد يبدأ اتصالًا أو يرسل بيانات. قل: أوافق للتأكيد، أو ألغِ."
            return@launch
        }
        val result = when (action) {
            is AgentAction.OpenApp -> registry.run("open_app", action.query)
            AgentAction.GoHome, AgentAction.GoBack, AgentAction.OpenRecents -> registry.run("system_navigation", action)
            else -> ToolResult(ToolResult.Status.FAILED, "هذه الأداة لم تُربط بعد")
        }
        _status.value = when (result.status) {
            ToolResult.Status.SUCCESS -> result.message
            ToolResult.Status.NEEDS_PERMISSION -> "أحتاج صلاحية الوصول لتنفيذ هذا الأمر."
            ToolResult.Status.CANCELLED -> "تم إلغاء العملية."
            else -> result.message
        }
    }

    fun cancelTask(id: String) = viewModelScope.launch {
        scheduler.cancel(id)
        AgentDatabase.get(getApplication()).tasks().updateStatus(id, "CANCELLED")
    }

    fun confirmPending(confirmed: Boolean) = viewModelScope.launch {
        val action = pendingConfirmation ?: return@launch
        pendingConfirmation = null
        if (!confirmed) { _status.value = "تم إلغاء العملية."; return@launch }
        val result = when (action) {
            is AgentAction.Call -> registry.run("call_contact", action.personOrNumber)
            else -> ToolResult(ToolResult.Status.FAILED, "لا توجد أداة تأكيد لهذا الإجراء")
        }
        _status.value = result.message
    }
}
