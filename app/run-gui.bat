@echo off
cd /d %~dp0
start "run" "%JAVA_HOME%\bin\javaw" -jar -Xmx2048m "duplicate-files-gui-swing.jar"
