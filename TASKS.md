# Tasks milestone

Added Room persistence (`TaskEntity`, `TaskDao`, `AgentDatabase`) and a WorkManager-backed `TaskScheduler` for deferred work. Tasks have durable status, retry count, errors, confirmation flag, and cancellation by stable ID.

The worker intentionally stops at `WAITING_USER` rather than executing a raw command. Before background execution is enabled, commands must be parsed into a typed, confirmation-safe plan and dispatched through `ToolRegistry`; this prevents arbitrary command execution and avoids claiming scheduled actions succeeded.

Exact wall-clock alarms remain a separate capability and require `AlarmManager` plus special access checks; WorkManager is intentionally used for non-exact deferred work.
