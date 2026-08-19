package ai.arena.androidagent

import ai.arena.androidagent.agent.*
import org.junit.Assert.assertTrue
import org.junit.Test

class ArabicFastPathTest {
    private val parser = ArabicFastPath()
    @Test fun whatsappAliasesBecomeSameSafeAction() {
        val a = parser.parse("افتح الواتس")
        val b = parser.parse("دخلني على واتساب")
        assertTrue(a?.actions?.single() is AgentAction.OpenApp)
        assertTrue(b?.actions?.single() is AgentAction.OpenApp)
    }
    @Test fun delayedReminderIsParsed() {
        val plan = parser.parse("ذكرني بعد 5 دقائق بالدوام")!!
        assertTrue(plan.actions.single() is AgentAction.CreateReminder)
    }
    @Test fun navigationIsLowRisk() {
        val plan = parser.parse("ارجع")!!
        assertTrue(plan.risk == Risk.LOW && !plan.needsConfirmation)
    }
}
