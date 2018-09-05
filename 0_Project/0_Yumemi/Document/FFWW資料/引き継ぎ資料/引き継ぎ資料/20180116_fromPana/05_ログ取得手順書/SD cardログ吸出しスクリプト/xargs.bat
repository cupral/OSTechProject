@echo off
if "%~1"=="" (
exit/b 1
)

set LIST=
for /f "delims=" %%i in ('findstr .*') do call :add %%i
%* %LIST% .\
exit/b

:add
if "%LIST%"=="" (
set LIST=%*
) else (
set LIST=%LIST% %*
)