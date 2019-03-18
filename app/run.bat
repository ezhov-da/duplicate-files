@echo off
cd /d %~dp0
"%JAVA_HOME%\bin\java" -Dfile.path.root=D:/изображения/жена-mi-20190317 -Dfile.path.report=D:/duplicate-files-md5.xml -jar -Xmx2014m "duplicate-files-1.0-SNAPSHOT.jar" create