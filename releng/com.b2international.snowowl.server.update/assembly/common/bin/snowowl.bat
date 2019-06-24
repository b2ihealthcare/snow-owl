@echo off
rem Snow Owl Windows startup script

if "%OS%" == "Windows_NT" setlocal

REM Heap settings
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Xms6g
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Xmx6g
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
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.port=8080
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djetty.home.bundle=org.eclipse.jetty.osgi.boot
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.Slf4jLog

REM GC configuration
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+UseConcMarkSweepGC
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:CMSInitiatingOccupancyFraction=75
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+UseCMSInitiatingOccupancyOnly
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError
set SO_JAVA_OPTS=%SO_JAVA_OPTS% -Djdk.security.defaultKeySize=DSA:1024

REM Run Snow Owl
"%JAVA_HOME%\bin\java" %SO_JAVA_OPTS% -jar ..\plugins\org.eclipse.equinox.launcher_1.5.300.v20190213-1655.jar 