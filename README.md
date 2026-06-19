# Hotspot Automator

A simple Android app that helps toggle the phone hotspot based on charger state:

- **Plug charger in:** requests hotspot ON.
- **Unplug charger:** requests hotspot OFF.
- **App lock:** first launch creates a passcode; later launches require it.
- **Reboot support:** after you start automation once, the foreground service is restarted after reboot.
- **On-screen permissions only:** notification and accessibility permission are opened/requested from the app UI. No ADB workflow is required.

## Important Android limitation

Normal Android apps cannot silently enable or disable the hotspot on modern Android unless they are system/owner apps. This project keeps the flow user-visible: it opens Android tethering settings and uses an Accessibility service, if the user enables it on-screen, to tap the hotspot switch.

## Build with GitHub Actions

Push this repository to GitHub and open the **Actions** tab. The workflow builds a debug APK and uploads it as `HotspotAutomator-debug-apk`.

## Use

1. Install the debug APK.
2. Open the app and create a passcode.
3. Tap **Allow notification permission** and grant it if Android shows the permission dialog.
4. Tap **Open Accessibility settings** and enable **Hotspot Automator**.
5. Return to the app and tap **Start automation**.
