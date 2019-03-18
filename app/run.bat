@echo off
cd /d %~dp0
"java" -Dfile.path.root=D:/изображения/жена-mi-20190317 -Dfile.path.report=D:/duplicate-files-md5.xml -Xmx2014m -cp "duplicate-files-1.0-SNAPSHOT.jar" "ru.ezhov.duplicate.files.ApplicationDuplicateFiles" create