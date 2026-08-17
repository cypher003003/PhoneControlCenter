# Phone Control Center — Step 3

This stage adds an Android WebView wrapper and a JavaScript bridge.

Features in this stage:
- HTML/CSS/JS dashboard packaged into an APK
- Camera through WebView
- Call history when permission is granted
- Incoming call event display
- Incoming SMS event display
- Android notification-listener integration
- Permission/settings buttons
- GitHub Actions debug APK build

Important:
- Notification access must be enabled by the user in Android settings.
- SMS/call permissions must be granted by the user.
- This app is intended for the phone owner's own device.
- The dashboard is local and does not secretly hide permissions.

Build:
GitHub Actions -> Build APK -> PhoneControlCenter-debug artifact.
