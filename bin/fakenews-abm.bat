@echo off
setlocal
set APP_DIR=%~dp0..
java -cp "%APP_DIR%\FAKENEWS-ABM.jar;%APP_DIR%\lib\*" Main %*
