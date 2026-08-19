# Security and privacy

- Tool allowlist only; no shell, reflection, arbitrary code, or model-generated intents without validation.
- Just-in-time permissions with rationale; no blanket onboarding permission request.
- Communication, deletion, publishing, financial, and other irreversible actions require confirmation. Cancellation is best effort and is surfaced honestly.
- Keep conversation/task data local by default. Cloud AI is opt-in, minimized, redacted, and never receives contacts, messages, notifications, audio, photos, or files unless the user explicitly enables the relevant feature.
- Audit logs are optional, minimized, redact message bodies and phone numbers, and support deletion.
- Accessibility and notification access are high-trust capabilities. Explain scope, expose enable/disable controls, and never use them to bypass another app's security or user confirmation.
- Secrets belong in Android Keystore-backed storage; no API keys in the APK.
