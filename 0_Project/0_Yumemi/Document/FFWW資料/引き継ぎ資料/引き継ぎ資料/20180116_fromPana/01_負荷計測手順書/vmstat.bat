@ECHO OFF
REM adb shell ����擾�����[���������t�@�C�����Ƃ��ė��p�\�ȕ�����ɕϊ�����B

SET TIME_STRINGS=NULL

FOR /f "delims=" %%a IN ('adb shell date +"%%Y%%m%%d%%I%%M%%S"') DO SET TIME_STRINGS=%%a

IF %TIME_STRINGS%==NULL (
    ECHO ������ ERROR ������
    ECHO �� �J�n�ł��܂���B�f�o�C�X�Ƃ̐ڑ����m�F���ĉ������B
    TIMEOUT /T 30
) ELSE (
    SET FILE_NAME=vmstat_%TIME_STRINGS%.log
    CALL :STARTWATCH
)

GOTO END

REM -----------------------------------------------------------------------------------------
:STARTWATCH
    SET DISP_TIME=%TIME_STRINGS:~0,4%/%TIME_STRINGS:~4,2%/%TIME_STRINGS:~6,2% %TIME_STRINGS:~8,2%:%TIME_STRINGS:~10,2%:%TIME_STRINGS:~12,2%
    ECHO ������ %DISP_TIME% �v�����J�n���܂����B������
    ECHO �� �I������ɂ� [CTRL] + [C] �L�[�������ĉ������B
    cd /d %~dp0
    adb shell vmstat 1 > %FILE_NAME%
EXIT

REM -----------------------------------------------------------------------------------------

:END