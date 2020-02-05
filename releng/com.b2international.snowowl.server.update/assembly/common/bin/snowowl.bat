@echo off
rem Snow Owl Windows startup script

if "%OS%" == "Windows_NT" setlocal

rem Derive full path for script (includes trailing backslash)
  set SCRIPT_DIR=%~dp0

rem Derive KERNEL_HOME full path from script's parent (no backslash)
  for %%I in ("%SCRIPT_DIR%..") do set KERNEL_HOME=%%~fsI

set CONFIG_AREA=%KERNEL_HOME%/work

REM Heap settings
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Xms6g
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Xmx6g
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+AlwaysPreTouch
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Xss1m

REM Equinox Config 
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -server
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djava.awt.headless=true
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.noShutdown=true
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.configuration.area="%CONFIG_AREA%"
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.console=2501

REM Parallel classloader configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.classloader.type=nonparallel
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+AlwaysLockClassLoader

REM Jetty configuration 
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.port=8080

REM GC configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+UseConcMarkSweepGC
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:CMSInitiatingOccupancyFraction=75
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+UseCMSInitiatingOccupancyOnly
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError

REM Misc configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djdk.security.defaultKeySize=DSA:1024

REM Run Snow Owl
PUSHD %KERNEL_HOME%
"%JAVA_HOME%\bin\java" %SO_JAVA_OPTS% -jar plugins\org.eclipse.equinox.launcher_1.5.400.v20190515-0925.jar
POPD
