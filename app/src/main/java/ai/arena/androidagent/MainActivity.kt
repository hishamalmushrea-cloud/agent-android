package ai.arena.androidagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val contactsPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AgentHome(onRequestContacts = { contactsPermission.launch(Manifest.permission.READ_CONTACTS) }) }
    }
}

@Composable
private fun AgentHome(
    onRequestContacts: () -> Unit,
    agent: AgentViewModel = viewModel()
) {
    var command by remember { mutableStateOf("") }
    val status by agent.status.collectAsStateWithLifecycle()
    val tasks by agent.tasks.collectAsStateWithLifecycle()
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("Android Agent", style = MaterialTheme.typography.headlineMedium)
                Text(status, style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(command, { command = it }, Modifier.fillMaxWidth(), label = { Text("اكتب أمرًا طبيعيًا") }, singleLine = false)
                Button(onClick = {
                    if (command.isBlank()) return@Button
                    if (command.contains("اتصل") || command.contains("دق") || command.contains("كلم")) onRequestContacts()
                    agent.submit(command)
                }, Modifier.fillMaxWidth()) { Text("تنفيذ") }
                if (status.contains("أوافق")) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { agent.confirmPending(true) }, Modifier.weight(1f)) { Text("أوافق") }
                        OutlinedButton(onClick = { agent.confirmPending(false) }, Modifier.weight(1f)) { Text("إلغاء") }
                    }
                }
                FilledTonalButton(onClick = { status = "الإدخال الصوتي سيُفعّل بعد منح صلاحية الميكروفون" }, Modifier.align(Alignment.CenterHorizontally)) { Text("🎙  تحدث مع الوكيل") }
                Text("المهام المجدولة", style = MaterialTheme.typography.titleMedium)
                LazyColumn(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tasks, key = { it.id }) { task ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) { Text(task.command); Text(task.status, color = MaterialTheme.colorScheme.primary) }
                                if (task.status == "SCHEDULED") OutlinedButton(onClick = { agent.cancelTask(task.id) }) { Text("إلغاء") }
                            }
                        }
                    }
                }
                Text("الحالة: IDLE", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
