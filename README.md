# agent-android

[![GitHub Repo stars](https://img.shields.io/github/stars/hishamalmushrea-cloud/agent-android?style=social)](https://github.com/hishamalmushrea-cloud/agent-android)
[![GitHub](https://img.shields.io/github/license/hishamalmushrea-cloud/agent-android)](https://github.com/hishamalmushrea-cloud/agent-android/blob/main/LICENSE)
[![Trendshift](https://trendshift.io/api/badge/repositories/4119)](https://trendshift.io/)
[![Docs Website](https://img.shields.io/badge/Docs-Website-blue?style=for-the-badge&logo=readthedocs)](https://github.com/hishamalmushrea-cloud/agent-android)
[![Discord](https://img.shields.io/badge/Discord-Join%20Us-7289DA?style=for-the-badge&logo=discord&logoColor=white)](https://discord.com/)
[![X (formerly Twitter) Follow](https://img.shields.io/twitter/follow/hishamalmushrea-cloud?style=social)](https://x.com/hishamalmushrea-cloud)

---

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
