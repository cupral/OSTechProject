@ECHO OFF

SET CANLOG_SETTING=NULL

REM --- set canlog enable ---
adb shell setprop persist.elog.debug.localcandump 1
adb shell sync

REM --- check setting ---
FOR /f "delims=" %%a IN ('adb shell getprop persist.elog.debug.localcandump') DO SET CANLOG_SETTING=%%a

IF %CANLOG_SETTING%==1 (
    ECHO;
    ECHO;
    ECHO 【OK】SETTING COMPLETED
    TIMEOUT /T 3 /NOBREAK
) ELSE (
    ECHO;
    ECHO;
    ECHO ■■■ ERROR ■■■
    ECHO ≫ 開始できません。デバイスとの接続を確認して下さい。
    TIMEOUT /T 30
)

:END