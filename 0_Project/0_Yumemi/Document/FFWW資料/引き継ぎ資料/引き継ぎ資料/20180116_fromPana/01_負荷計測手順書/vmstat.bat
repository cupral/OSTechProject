@ECHO OFF
REM adb shell から取得した端末時刻をファイル名として利用可能な文字列に変換する。

SET TIME_STRINGS=NULL

FOR /f "delims=" %%a IN ('adb shell date +"%%Y%%m%%d%%I%%M%%S"') DO SET TIME_STRINGS=%%a

IF %TIME_STRINGS%==NULL (
    ECHO ■■■ ERROR ■■■
    ECHO ≫ 開始できません。デバイスとの接続を確認して下さい。
    TIMEOUT /T 30
) ELSE (
    SET FILE_NAME=vmstat_%TIME_STRINGS%.log
    CALL :STARTWATCH
)

GOTO END

REM -----------------------------------------------------------------------------------------
:STARTWATCH
    SET DISP_TIME=%TIME_STRINGS:~0,4%/%TIME_STRINGS:~4,2%/%TIME_STRINGS:~6,2% %TIME_STRINGS:~8,2%:%TIME_STRINGS:~10,2%:%TIME_STRINGS:~12,2%
    ECHO ■■■ %DISP_TIME% 計測を開始しました。■■■
    ECHO ≫ 終了するには [CTRL] + [C] キーを押して下さい。
    cd /d %~dp0
    adb shell vmstat 1 > %FILE_NAME%
EXIT

REM -----------------------------------------------------------------------------------------

:END