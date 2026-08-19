package ai.arena.androidagent.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

sealed class AccessState { data object Granted : AccessState(); data object Missing : AccessState(); data object SpecialAccess : AccessState() }

class PermissionManager(private val context: Context) {
    fun runtime(permission: String): AccessState =
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) AccessState.Granted else AccessState.Missing

    fun request(activity: Activity, permission: String, requestCode: Int) {
        if (runtime(permission) is AccessState.Missing) ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    fun openAccessibilitySettings() { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    fun openNotificationListenerSettings() { context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    fun openAssistantRoleSettings() { context.startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}
