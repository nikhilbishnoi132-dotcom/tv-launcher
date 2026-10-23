# TV Launcher (Apple TV style)

## 1. Build the APK
Open this folder in Android Studio (Hedgehog or newer), let Gradle sync, then
Build > Build APK(s). Output: app/build/outputs/apk/debug/app-debug.apk
(Command line, with Gradle 8.7+: `gradle assembleDebug`.)

## 2. Connect ADB to the TV
TV: Settings > Device Preferences > About > tap "Build" 7 times, then
Developer options > enable USB debugging (and Network debugging if offered).
Most TVs have no host-mode USB port for ADB, so use Wi-Fi:
    adb connect <TV_IP>:5555      (accept the prompt on the TV)
    adb devices

## 3. Install
    adb install -r app/build/outputs/apk/debug/app-debug.apk

## 4. Debloat
    adb shell pm list packages -3 > user_apps.txt
    adb shell pm list packages -s > system_apps.txt
Paste both to Claude to get a safe removal list, put it in scripts/packages.txt, then
    ./scripts/debloat.sh scripts/packages.txt
Restore any package: adb shell cmd package install-existing <package>

## 5. Make it the Home screen
Find the current home app:
    adb shell cmd package resolve-activity -a android.intent.action.MAIN -c android.intent.category.HOME
Press Home on the TV; if a chooser appears, pick "Home" and Always.
If the stock launcher still wins, disable it (reversible):
    adb shell pm disable-user --user 0 <stock launcher package>
Undo:
    adb shell pm enable <stock launcher package>

## Build without installing anything (GitHub)
1. Create a free account at github.com and a new repository.
2. Upload ALL files from this folder (including the hidden .github folder).
3. Open the Actions tab > "Build APK" > wait for the green tick (about 3-5 min).
4. Open the finished run, download the "app-debug-apk" artifact (a zip), unzip it to get app-debug.apk.
