# Android Agent Architecture

## Status
Phase 1 scaffold: a buildable Kotlin/Compose shell and a typed, side-effect-free Arabic fast path. Execution is intentionally not claimed yet.

## Pipeline
`Text/Voice -> InputNormalizer -> LocalFastPath or AiProvider -> Structured ActionPlan -> PermissionManager -> Risk/Confirmation -> ToolRegistry -> Executor -> Verifier -> Recovery -> Result -> Text/TTS`

The model never receives arbitrary Android handles and cannot execute code. It returns typed actions that must be accepted by the registry.

## Modules (planned)
The first release keeps a single app module to reduce build risk. Boundaries are packages/interfaces: `agent`, `tools`, `permissions`, `data`, `automation`, `voice`, and `ui`. They can become Gradle modules after contracts stabilize.

## State machine
IDLE, LISTENING, THINKING, PLANNING, WAITING_PERMISSION, CONFIRMING, EXECUTING, VERIFYING, WAITING, RECOVERING, SUCCESS, FAILED, CANCELLED.

## Reliability
Every tool has prerequisites, timeout, retry budget, verifier, and user-facing failure. Unknown results are never reported as success. Destructive or communication actions require explicit policy evaluation and, by default, confirmation.

## Provider abstraction
`AiProvider` will accept normalized conversation context and return a schema-validated `ActionPlan`; `LocalProvider` handles safe common commands offline. Cloud providers are opt-in and must redact/minimize sensitive context.
