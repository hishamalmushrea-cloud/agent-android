# Android Agent

Android Agent is a Kotlin/Jetpack Compose foundation for a privacy-first, tool-based phone agent. It unifies text and future voice input into a typed plan, permission checks, risk/confirmation, execution, verification, and recovery.

## Current milestone
- Android Studio project scaffold (Kotlin, Compose, minSdk 26, target/compile SDK 35)
- Arabic local fast-path contracts for safe navigation and media commands
- Explicit capability/security documentation
- No fake claims: the current UI is a shell; real system tools are the next implementation milestone.

## Build
Open in Android Studio with an installed Android SDK 35 and run `./gradlew :app:assembleDebug`. This checkout has no SDK available in the agent sandbox, so build verification must be completed on an Android Studio/CI host.

## Research basis
The capability matrix follows Android Developers guidance for `VoiceInteractionService`/`ROLE_ASSISTANT`, AccessibilityService, foreground-service restrictions, exact alarms, MediaSession, and App Functions. App Functions are currently beta/preview and cannot be treated as a universal third-party automation API.

See: [ARCHITECTURE](ARCHITECTURE.md), [CAPABILITIES](CAPABILITIES.md), [SECURITY](SECURITY.md), [LIMITATIONS](LIMITATIONS.md), [ROADMAP](ROADMAP.md).
