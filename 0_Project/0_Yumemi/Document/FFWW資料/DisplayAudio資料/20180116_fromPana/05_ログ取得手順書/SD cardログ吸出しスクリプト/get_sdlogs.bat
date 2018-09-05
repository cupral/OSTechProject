@ECHO OFF

REM --- ƒƒOæ“¾ ----
cd /d %~dp0
adb shell ls /sdcard/colog*.log | xargs adb pull

REM --- /sdcard cleanup ---
adb shell rm -f /sdcard/colog*.log
adb shell rm -fr /sdcard/security*
