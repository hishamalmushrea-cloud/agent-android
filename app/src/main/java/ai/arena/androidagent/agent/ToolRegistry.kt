package ai.arena.androidagent.agent

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout

/** A tool is the only approved boundary between a plan and Android side effects. */
data class ToolSpec(
    val name: String,
    val description: String,
    val risk: Risk,
    val timeoutMs: Long = 8_000,
    val maxRetries: Int = 1,
    val requiredPermissions: Set<String> = emptySet()
)

data class ToolResult(val status: Status, val message: String, val data: Map<String, String> = emptyMap()) {
    enum class Status { SUCCESS, FAILED, UNKNOWN, CANCELLED, NEEDS_PERMISSION }
}

interface AgentTool<I> {
    val spec: ToolSpec
    suspend fun execute(input: I): ToolResult
    suspend fun verify(result: ToolResult): ToolResult = result
}

class ToolRegistry(private val tools: List<AgentTool<*>>) {
    private val byName = tools.associateBy { it.spec.name }
    fun specs(): List<ToolSpec> = tools.map { it.spec }
    fun contains(name: String): Boolean = byName.containsKey(name)
    fun spec(name: String): ToolSpec? = byName[name]?.spec
    @Suppress("UNCHECKED_CAST")
    suspend fun <I> run(name: String, input: I): ToolResult {
        val tool = byName[name] ?: return ToolResult(ToolResult.Status.FAILED, "الأداة غير متاحة")
        return try { withTimeout(tool.spec.timeoutMs) { (tool as AgentTool<I>).execute(input) } }
        catch (_: CancellationException) { ToolResult(ToolResult.Status.CANCELLED, "تم إلغاء العملية") }
        catch (_: Exception) { ToolResult(ToolResult.Status.UNKNOWN, "تعذر التحقق من نتيجة العملية") }
    }
}
