package ai.arena.androidagent.data

import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.room.*
import androidx.work.*
import java.util.concurrent.TimeUnit
import java.util.UUID

@Entity(tableName = "agent_tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val command: String,
    val scheduledAt: Long,
    val status: String = "SCHEDULED",
    val retryCount: Int = 0,
    val lastError: String? = null,
    val requiresConfirmation: Boolean = false
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM agent_tasks ORDER BY scheduledAt ASC") fun observeAll(): kotlinx.coroutines.flow.Flow<List<TaskEntity>>
    @Query("SELECT * FROM agent_tasks WHERE id = :id LIMIT 1") suspend fun get(id: String): TaskEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(task: TaskEntity)
    @Query("UPDATE agent_tasks SET status = :status, lastError = :error WHERE id = :id") suspend fun updateStatus(id: String, status: String, error: String? = null)
    @Query("DELETE FROM agent_tasks WHERE id = :id") suspend fun delete(id: String)
}

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class AgentDatabase : RoomDatabase() {
    abstract fun tasks(): TaskDao
    companion object { @Volatile private var instance: AgentDatabase? = null
        fun get(context: Context): AgentDatabase = instance ?: synchronized(this) { instance ?: Room.databaseBuilder(context, AgentDatabase::class.java, "agent.db").build().also { instance = it } }
    }
}

class ScheduledTaskWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val id = inputData.getString("task_id") ?: return Result.failure()
        AgentDatabase.get(applicationContext).tasks().updateStatus(id, "RUNNING")
        val task = AgentDatabase.get(applicationContext).tasks().get(id)
        notifyReminder(applicationContext, task?.command ?: "حان وقت التذكير")
        AgentDatabase.get(applicationContext).tasks().updateStatus(id, "SUCCESS")
        return Result.success()
    }

    private fun notifyReminder(context: Context, text: String) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel("reminders", "التذكيرات", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(context, "reminders")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Android Agent")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .build()
        manager.notify(text.hashCode(), notification)
    }
    }
}

class TaskScheduler(private val context: Context) {
    suspend fun schedule(task: TaskEntity): UUID {
        AgentDatabase.get(context).tasks().upsert(task)
        val delay = (task.scheduledAt - System.currentTimeMillis()).coerceAtLeast(0)
        val request = OneTimeWorkRequestBuilder<ScheduledTaskWorker>().setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(workDataOf("task_id" to task.id)).build()
        WorkManager.getInstance(context).enqueueUniqueWork("task-${task.id}", ExistingWorkPolicy.REPLACE, request)
        return request.id
    }
    fun cancel(id: String) { WorkManager.getInstance(context).cancelUniqueWork("task-$id") }
}
