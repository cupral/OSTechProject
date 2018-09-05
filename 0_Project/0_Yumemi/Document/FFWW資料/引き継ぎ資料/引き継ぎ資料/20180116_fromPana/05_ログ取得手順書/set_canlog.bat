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
    ECHO �yOK�zSETTING COMPLETED
    TIMEOUT /T 3 /NOBREAK
) ELSE (
    ECHO;
    ECHO;
    ECHO ������ ERROR ������
    ECHO �� �J�n�ł��܂���B�f�o�C�X�Ƃ̐ڑ����m�F���ĉ������B
    TIMEOUT /T 30
)

:END