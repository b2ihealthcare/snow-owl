@echo off
rem Snow Owl CIS Startup Script

if "%OS%" == "Windows_NT" setlocal

rem Derive full path for script (includes trailing backslash)
set SCRIPT_DIR=%~dp0

rem Derive KERNEL_HOME full path from script's parent (no backslash)
  for %%I in ("%SCRIPT_DIR%..") do set KERNEL_HOME="%%~fsI"

set CONFIG_AREA=%KERNEL_HOME%/work

IF DEFINED JAVA_HOME (
	set JAVA_EXECUTABLE=%JAVA_HOME%/bin/java.exe
) else (
	set JAVA_EXECUTABLE=%KERNEL_HOME%/plugins/org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.*/jre/bin/java.exe
)

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
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.console=2501

REM Parallel classloader configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dosgi.classloader.type=nonparallel
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+AlwaysLockClassLoader

REM Jetty configuration 
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.port=9090
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.home="%KERNEL_HOME%/configuration"
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.etc.config.urls=jetty.xml,jetty-http.xml,jetty-deployer.xml
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.Slf4jLog

REM GC configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError

REM Misc configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% --add-opens java.base/java.lang.reflect=ALL-UNNAMED
set SO_JAVA_OPTS=%SO_JAVA_OPTS% --add-opens java.base/java.lang=ALL-UNNAMED
set SO_JAVA_OPTS=%SO_JAVA_OPTS% --add-opens java.base/java.util=ALL-UNNAMED
set SO_JAVA_OPTS=%SO_JAVA_OPTS% --add-opens java.base/java.time=ALL-UNNAMED
set SO_JAVA_OPTS=%SO_JAVA_OPTS% --add-opens java.base/sun.security.x509=ALL-UNNAMED
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djdk.security.defaultKeySize=DSA:1024
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dlogback.configurationFile="%KERNEL_HOME%/configuration/serviceability.xml"

REM Run Snow Owl
PUSHD %KERNEL_HOME%
"%JAVA_EXECUTABLE%" %SO_JAVA_OPTS% -jar plugins\org.eclipse.equinox.launcher_1.5.800.v20200727-1323.jar  -console 2501 
POPD
