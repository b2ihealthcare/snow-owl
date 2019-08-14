@echo off
rem Snow Owl CIS Startup Script

if "%OS%" == "Windows_NT" setlocal

rem Derive full path for script (includes trailing backslash)
  set SCRIPT_DIR=%~dp0

rem Derive KERNEL_HOME full path from script's parent (no backslash)
  for %%I in ("%SCRIPT_DIR%..") do set KERNEL_HOME=%%~fsI 

REM Heap settings
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Xms2g
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Xmx2g
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+AlwaysPreTouch
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Xss1m

REM Equinox Config 
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -server
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djava.awt.headless=true
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Declipse.ignoreApp=true
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.noShutdown=true
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Declipse.application.launchDefault=false
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.configuration.area="%CONFIG_AREA%"

REM Parallel classloader configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.classloader.type=nonparallel
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+AlwaysLockClassLoader

REM Jetty configuration 
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.port=9090
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.home=%KERNEL_HOME%/configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.etc.config.urls=jetty.xml,jetty-http.xml,jetty-deployer.xml
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.Slf4jLog

REM GC configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+UseConcMarkSweepGC
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:CMSInitiatingOccupancyFraction=75
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+UseCMSInitiatingOccupancyOnly
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djdk.security.defaultKeySize=DSA:1024

REM Run Snow Owl
PUSHD %KERNEL_HOME%
"%JAVA_HOME%\bin\java" %SO_JAVA_OPTS% -jar plugins\org.eclipse.equinox.launcher_1.3.0.v20130327-1440.jar 
POPD
