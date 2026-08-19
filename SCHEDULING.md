# Scheduling design

Scheduling is not yet wired into the UI. The planned implementation uses Room for durable task state and chooses the scheduler by semantics:

- `WorkManager`: deferred/non-exact work, constraints, retry, and persistence.
- `AlarmManager`: user-visible alarms/reminders requiring a wall-clock trigger; exact alarms only after checking `canScheduleExactAlarms()` and explaining the special access.
- A receiver/worker will reload tasks after reboot and transition them through `SCHEDULED -> RUNNING -> VERIFYING -> SUCCESS/FAILED`.

Tasks must store a redacted command, typed plan, schedule, status, retry count, and last error. Communication tasks remain confirmation-gated; a background worker must not silently send sensitive content without a policy-approved confirmation captured at scheduling time.
