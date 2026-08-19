package ai.arena.androidagent.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import ai.arena.androidagent.agent.*

class ContactResolver(private val context: Context) {
    fun find(query: String): List<String> {
        val result = mutableListOf<String>()
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        return try {
            context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?", arrayOf("%$query%"), null)?.use { c ->
                val number = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (c.moveToNext()) result += c.getString(number)
            }
            result.distinct()
        } catch (_: SecurityException) { emptyList() }
    }
}

class DialContactTool(private val context: Context) : AgentTool<String> {
    private val contacts = ContactResolver(context)
    override val spec = ToolSpec("call_contact", "العثور على جهة اتصال وفتح شاشة الاتصال", Risk.MEDIUM, requiredPermissions = setOf("android.permission.READ_CONTACTS"))
    override suspend fun execute(input: String): ToolResult {
        if (input.filter { it.isDigit() }.length >= 3) return dial(input)
        val numbers = contacts.find(input)
        if (numbers.isEmpty()) return ToolResult(ToolResult.Status.FAILED, "لم أجد جهة اتصال باسم $input")
        if (numbers.size > 1) return ToolResult(ToolResult.Status.UNKNOWN, "وجدت أكثر من رقم باسم $input؛ أحتاج اختيارك")
        return dial(numbers.single())
    }
    private fun dial(number: String): ToolResult = try {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(number)}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        ToolResult(ToolResult.Status.SUCCESS, "فتحت شاشة الاتصال بالرقم المحدد")
    } catch (_: Exception) { ToolResult(ToolResult.Status.FAILED, "تعذر فتح شاشة الاتصال") }
}
