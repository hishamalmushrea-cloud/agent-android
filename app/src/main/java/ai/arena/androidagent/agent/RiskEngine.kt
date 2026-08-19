package ai.arena.androidagent.agent

class RiskEngine(private val confirmMedium: Boolean = true) {
    fun requiresConfirmation(action: AgentAction): Boolean = when (action) {
        is AgentAction.Call, is AgentAction.SendSms -> true
        else -> false
    }
    fun classify(action: AgentAction): Risk = when (action) {
        is AgentAction.Call, is AgentAction.SendSms -> Risk.MEDIUM
        else -> Risk.LOW
    }
}

data class ConfirmationRequest(val action: AgentAction, val explanation: String)
