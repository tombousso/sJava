@echo off
make %1 || exit /b
::Exclude %1
setlocal ENABLEDELAYEDEXPANSION
set "_args=%*"
set "_args=!_args:*%1=!"
java -cp "bin/%1;lib/*" Main %_args%