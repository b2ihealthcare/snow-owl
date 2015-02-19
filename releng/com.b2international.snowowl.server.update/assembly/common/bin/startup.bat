@ECHO OFF
IF "%OS%" == "Windows_NT" SETLOCAL

SET SCRIPT_DIR=%~dp0%
SET EXECUTABLE=dmk.bat

::
:: Custom cleanup for Snow Owl Server
::
echo Script directory: %SCRIPT_DIR%
echo Deleting work directory...
rd /S /Q %SCRIPT_DIR%\..\work
echo Deleting workspace directory...
rd /S /Q %SCRIPT_DIR%\..\workspace
echo Finished cleanup, starting server.

::
:: Starting server
::
call "%SCRIPT_DIR%%EXECUTABLE%" start %*
if not "%ERRORLEVEL%"=="0" exit /B %ERRORLEVEL%
