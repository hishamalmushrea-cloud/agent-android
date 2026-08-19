# Tool system

`ToolRegistry` is the side-effect boundary. Each tool publishes a name, risk, timeout, retry budget, and required permissions. Plans may reference only registered names; unknown names fail safely. Execution is cancellable and time-bounded. A verifier must be added before reporting success for tools whose effects can be observed.

Implemented foundation:
- `open_app`: resolves launcher activities using PackageManager and Arabic aliases; does not assume package names.
- `open_settings`: opens an appropriate system settings page.
- `system_navigation`: Home has a safe intent fallback; Back/Recents explicitly return `NEEDS_PERMISSION` until Accessibility is implemented.
- `PermissionManager`: runtime permission state plus explicit settings entry points.

Important: the current Compose button is still a UI shell and does not dispatch plans yet. Wiring UI -> planner -> registry is the next step, followed by verifier-backed Android instrumented tests.
