# Capability matrix (Android 10–16 target reality)

| Feature | Status | Access / method | Important limitation |
|---|---|---|---|
| Launch installed app | ✅ | PackageManager + launcher Intent | Package visibility and OEM launchers vary; resolve, do not hard-code only |
| Back/Home/Recents | ⚠️ | Accessibility global actions; Home via Intent | Accessibility special access is required for global actions; no silent system takeover |
| Voice assistant | ⚠️ | VoiceInteractionService + ROLE_ASSISTANT | User must select the role; background microphone is restricted |
| Screen inspection/click/type | ⚠️ | AccessibilityService | Intended for accessibility; UI changes and policy constraints; semantic nodes first |
| Contacts lookup | ✅ | READ_CONTACTS + ContactsContract | Just-in-time runtime permission; ambiguity must be surfaced |
| Phone call | ⚠️ | ACTION_DIAL (no call permission) or ACTION_CALL | Direct call needs CALL_PHONE; default dialer requirements and emergency guard |
| SMS compose | ✅ | ACTION_SENDTO | User interaction is safest and broadly available |
| Silent SMS send | ⚠️ | SmsManager + SEND_SMS | Permission, role/policy and device/carrier constraints; default SMS may be required by distribution policy |
| WhatsApp/Telegram send | ⚠️ | Official deep link if available, otherwise Accessibility | No general public send API; never claim success without verification |
| Media play/pause/next | ✅/⚠️ | MediaSession/MediaController | Requires active controllable session; app-specific play/search varies |
| Exact user alarm | ⚠️ | AlarmManager | Android 12+ special access; Android 14 commonly denied by default for new installs; use inexact/WorkManager when acceptable |
| Deferred background work | ✅/⚠️ | WorkManager | Timing is not exact; execution may be delayed by Doze/OEM restrictions |
| Notifications | ⚠️ | NotificationListenerService special access | User grants access; minimize and do not persist content unnecessarily |
| Clipboard | ⚠️ | ClipboardManager | Android privacy notifications and background access restrictions |
| Flashlight | ✅ | CameraManager | Hardware may be absent/busy |
| Brightness | ⚠️ | Settings.System + WRITE_SETTINGS | Special access and policy; otherwise open settings |
| Wi‑Fi/Bluetooth | ⚠️ | Official APIs/settings panels | Direct toggles are restricted on newer Android; provide settings panel |
| DND | ⚠️ | NotificationManager policy access | Special access and user-controlled policy |
| Camera capture | ⚠️ | CameraX / ACTION_IMAGE_CAPTURE | Camera permission/lifecycle/user interaction; background camera prohibited |
| App Functions | ⚠️ experimental | App Functions / trusted system agents | Beta/preview and permission-gated; optional integration, not core dependency |
| Lock screen / shutdown | ❌/⚠️ | Device admin or system privileges | Not generally available to ordinary apps |

No capability marked ⚠️ is silently upgraded to ✅. Device, OEM, API level, role, and user settings are checked at runtime.
