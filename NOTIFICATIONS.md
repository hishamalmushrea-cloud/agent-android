# Reminder notifications

Scheduled reminders now create a notification channel and post a notification from `ScheduledTaskWorker` when WorkManager runs. Android 13+ still requires the user to grant `POST_NOTIFICATIONS`; the UI must request it just in time before enabling reminders. If denied, the task status must become `FAILED` with an actionable settings message rather than claiming the user was notified.

Action tasks (calls, messages, external-app automation) are intentionally not executed by the reminder worker yet. They require a persisted typed plan, confirmation policy, capability checks, and post-action verification.
