@echo off
cd /d %~dp0
start "run" "%JAVA_HOME%\bin\java" -jar -Xms2048m -Xmx4096m "duplicate-files-gui-swing.jar"
