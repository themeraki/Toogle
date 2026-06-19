# Hotspot Automator

A simple Android app that toggles the phone hotspot based on charger state:

- **Plug charger in:** tries to turn hotspot ON automatically.
- **Unplug charger:** tries to turn hotspot OFF automatically.
- **App lock:** first launch creates a passcode; later launches require it.
- **Reboot support:** after you start automation once, the foreground service is restarted after reboot.
- **On-screen permissions only:** notification and accessibility permission are opened/requested from the app UI. No ADB workflow is required.
- **Battery-conscious:** the foreground service registers only charger-change receivers and stays idle between plug/unplug events.

## Important Android limitation

The app first tries Android tethering APIs through device-supported system services. Some manufacturers allow this, while many modern Android builds restrict hotspot changes to system/device-owner apps. When Android blocks direct control, the app falls back to a user-visible flow: it opens tethering settings and uses Accessibility, if the user enabled it on-screen, to tap the hotspot switch.

## Build with GitHub Actions

Push this repository to GitHub and open the **Actions** tab. The workflow builds a debug APK and uploads it as `HotspotAutomator-debug-apk`.

## Use

1. Install the debug APK.
2. Open the app and create a passcode.
3. Tap **Allow notification permission** and grant it if Android shows the permission dialog.
4. Tap **Open Accessibility settings** and enable **Hotspot Automator**.
5. Return to the app and tap **Start automation**.
