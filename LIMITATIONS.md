# Limitations

This milestone does not yet implement real calls, SMS sending, Accessibility, notification reading, voice capture/TTS, scheduling, database persistence, or AI provider calls. The UI does not claim those actions succeeded. These require device testing, explicit permission flows, verifiers, and policy review.

Android is not a general remote-control API: WhatsApp/Telegram automation has no universal public send API; protected settings, lock-screen actions, Wi‑Fi toggles, background microphone/camera, exact alarms, and default dialer/SMS/assistant roles are conditional. OEM battery managers can delay background work.
