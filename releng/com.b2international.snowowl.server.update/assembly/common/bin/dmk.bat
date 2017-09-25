@echo off
rem Script for starting and stopping the kernel

if "%OS%" == "Windows_NT" setlocal

rem Derive full path for script (includes trailing backslash)
  set SCRIPT_DIR=%~dp0

if exist "%SCRIPT_DIR%setupVars.bat" (
  call "%SCRIPT_DIR%setupVars.bat"
  if not "%ERRORLEVEL%"=="0" (
    if "%OS%" == "Windows_NT" endlocal
    exit /B %ERRORLEVEL%
  )
) else (
  echo Cannot set up environment. "setupVars.bat" file missing.
  if "%OS%" == "Windows_NT" endlocal
  exit /B 1
)

rem Select command we are to run

  rem First parm is command
    set COMMAND=%~1
    rem Rest are parameters - shift done in subroutines
   
  rem Switch on COMMAND in {"start","stop"}

    if "%COMMAND%" == "start" (
      call :doStartCommand %*
      if "%OS%" == "Windows_NT" endlocal
      exit /B 0
    )

    if "%COMMAND%" == "stop"  (
      call :doStopCommand  %*
      if "%OS%" == "Windows_NT" endlocal
      exit /B 0
    )

    echo Unknown command: %COMMAND%
    if "%OS%" == "Windows_NT" endlocal
    exit /B 1

rem ------------------ Subroutines
rem ------------------------------
:doStartCommand

  shift
  rem The shift must be here :()

  rem Check further file that needs to exist
  for %%I in ("%KERNEL_HOME%\bin\jmxPermissions.vbs") do if not exist "%%~I" (
    echo File "%%~I" does not exist but is required to continue.
    exit /B 1
  )

  rem Set defaults
    set CONFIG_DIR=%KERNEL_HOME%\configuration
    set CLEAN_FLAG=
    set NO_START_FLAG=
    set DEBUG_FLAG=
    set DEBUG_PORT=8000
    set SUSPEND=n
    if not defined JMX_PORT set JMX_PORT=9875
    if not defined KEYSTORE_PASSWORD set KEYSTORE_PASSWORD=changeit
    set ADDITIONAL_ARGS=

  rem Loop through options

  :startOptionLoop
  if "%~1"=="" goto endStartOptionLoop
  if "%~1"=="-debug"             goto debug
  if "%~1"=="-clean"             goto clean
  if "%~1"=="-configDir"         goto configDir
  if "%~1"=="-jmxport"           goto jmxport
  if "%~1"=="-keystore"          goto keystore
  if "%~1"=="-keystorePassword"  goto keystorePassword
  if "%~1"=="-noStart"           goto noStart
  if "%~1"=="-suspend"           goto suspend
  if "%~1"=="-shell"             goto shell

  set ADDITIONAL_ARGS=%ADDITIONAL_ARGS% "%~1"

  :continueStartOptionLoop
    shift
    goto startOptionLoop

  :debug
    set DEBUG_FLAG=1
    set PORT_CANDIDATE=%~2
    if not "%PORT_CANDIDATE:~0,1%"=="-" (
      set DEBUG_PORT=%PORT_CANDIDATE%
      shift
    )
    goto continueStartOptionLoop
  :clean
    set CLEAN_FLAG=1
    goto continueStartOptionLoop
  :configDir
    set CONFIG_DIR=%~2
    rem unless absolute, treat as relative to kernel home
    if "%CONFIG_DIR:~1%"=="\" goto absoluteConfigDir
    if "%CONFIG_DIR:~1,2%"==":\" goto absoluteConfigDir
    set CONFIG_DIR=%KERNEL_HOME%\%CONFIG_DIR%
  :absoluteConfigDir
    shift
    goto continueStartOptionLoop
  :jmxport
    set JMX_PORT=%~2
    shift
    goto continueStartOptionLoop
  :keystore
    set KEYSTORE_PATH=%~2
    shift
    goto continueStartOptionLoop
  :keystorePassword
    set KEYSTORE_PASSWORD=%~2
    shift
    goto continueStartOptionLoop
  :noStart
    set NO_START_FLAG=1
    goto continueStartOptionLoop
  :suspend
    set SUSPEND=y
    goto continueStartOptionLoop
  :shell
    set SHELL_FLAG=1
    goto continueStartOptionLoop

  :endStartOptionLoop

  
  rem Adjust permissions if necessary
    cscript //NoLogo "%KERNEL_HOME%\bin\jmxPermissions.vbs" "%CONFIG_DIR%\"

  rem Adjust options now all are known
    if "%KEYSTORE_PATH%"=="" set KEYSTORE_PATH=%CONFIG_DIR%\keystore
    if not "%DEBUG_FLAG%"=="" set DEBUG_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=%DEBUG_PORT%,server=y,suspend=%SUSPEND%

  rem do Clean work:
    if not "%CLEAN_FLAG%"=="" (
      rmdir /Q /S "%KERNEL_HOME%\serviceability"
      rmdir /Q /S "%KERNEL_HOME%\work"
      
      set LAUNCH_OPTS=%LAUNCH_OPTS% -clean
    )

  rem do Shell work:
    if not "%SHELL_FLAG%"=="" ( 
      echo "Warning: Kernel shell not supported; -shell option ignored."
      rem set LAUNCH_OPTS=%LAUNCH_OPTS% -Forg.eclipse.virgo.kernel.shell.local=true
    )

  rem Set JMX options
    set JMX_OPTS=%JMX_OPTS% -Dcom.sun.management.jmxremote.port=%JMX_PORT% 
    set JMX_OPTS=%JMX_OPTS% -Dcom.sun.management.jmxremote.authenticate=true 
    set JMX_OPTS=%JMX_OPTS% -Dcom.sun.management.jmxremote.login.config=virgo-kernel 
    set JMX_OPTS=%JMX_OPTS% -Dcom.sun.management.jmxremote.access.file="%CONFIG_DIR%\org.eclipse.virgo.kernel.jmxremote.access.properties" 
    set JMX_OPTS=%JMX_OPTS% -Djavax.net.ssl.keyStore="%KEYSTORE_PATH%" 
    set JMX_OPTS=%JMX_OPTS% -Djavax.net.ssl.keyStorePassword=%KEYSTORE_PASSWORD% 
    set JMX_OPTS=%JMX_OPTS% -Dcom.sun.management.jmxremote.ssl=true 
    set JMX_OPTS=%JMX_OPTS% -Dcom.sun.management.jmxremote.ssl.need.client.auth=false

    if not "%NO_START_FLAG%"=="" goto :eof
    rem ensure that the tmp directory exists:
      set TMP_DIR="%KERNEL_HOME%\work\tmp"
      if not exist "%TMP_DIR%" mkdir "%TMP_DIR%"

	rem Added awt.headless - http://mail-archives.apache.org/mod_mbox/poi-user/200705.mbox/%3C15719338671.20070504144714@dinom.ru%3E

	set JAVA_OPTS=%JAVA_OPTS% -Xms12g
	set JAVA_OPTS=%JAVA_OPTS% -Xmx12g
	set JAVA_OPTS=%JAVA_OPTS% -XX:+AlwaysPreTouch
	set JAVA_OPTS=%JAVA_OPTS% -Xss1m
	set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDetails
	set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCDateStamps
	set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCApplicationStoppedTime
	set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCApplicationConcurrentTime
	set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintTenuringDistribution
	set JAVA_OPTS=%JAVA_OPTS% -XX:+PrintGCCause
	set JAVA_OPTS=%JAVA_OPTS% -XX:+UseGCLogFileRotation
	set JAVA_OPTS=%JAVA_OPTS% -XX:NumberOfGCLogFiles=10
	set JAVA_OPTS=%JAVA_OPTS% -XX:GCLogFileSize=2M
	set JAVA_OPTS=%JAVA_OPTS% -Xloggc:$KERNEL_HOME%\serviceability\logs\`date +%F_%H%M-%S`-gc.log \
	set JAVA_OPTS=%JAVA_OPTS% -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl 
	set JAVA_OPTS=%JAVA_OPTS% -Djavax.xml.transform.TransformerFactory=com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl 
	set JAVA_OPTS=%JAVA_OPTS% -Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl
	set JAVA_OPTS=%JAVA_OPTS% -XX:+AlwaysLockClassLoader
	set JAVA_OPTS=%JAVA_OPTS% -Dosgi.classloader.type=nonparallel
	set JAVA_OPTS=%JAVA_OPTS% -Djava.awt.headless=true
    rem Run the server
  
      rem Marshall parameters
      set KERNEL_JAVA_PARMS=%JAVA_OPTS% %DEBUG_OPTS% %JMX_OPTS%

      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -XX:+HeapDumpOnOutOfMemoryError 
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -XX:ErrorFile="%KERNEL_HOME%\serviceability\error.log" 
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -XX:HeapDumpPath="%KERNEL_HOME%\serviceability\heap_dump.hprof"
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Djava.security.auth.login.config="%CONFIG_DIR%\org.eclipse.virgo.kernel.authentication.config" 
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dorg.eclipse.virgo.kernel.authentication.file="%CONFIG_DIR%\org.eclipse.virgo.kernel.users.properties" 
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Djava.io.tmpdir="%TMP_DIR%" 
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dorg.eclipse.virgo.kernel.home="%KERNEL_HOME%" 
	  set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dosgi.java.profile="file:%CONFIG_DIR%\java-server.profile"
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dssh.server.keystore="%CONFIG_DIR%/hostkey.ser"
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dorg.eclipse.virgo.kernel.config="%CONFIG_DIR%"
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dosgi.sharedConfiguration.area="%CONFIG_DIR%"
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Declipse.ignoreApp="true" 
	  set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dosgi.install.area="%KERNEL_HOME%"
	  set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dosgi.configuration.area="%KERNEL_HOME%\work" 
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Dosgi.frameworkClassPath="%FWCLASSPATH%"
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -Djava.endorsed.dirs="%KERNEL_HOME%\lib\endorsed"
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -classpath "%CLASSPATH%" 
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% org.eclipse.equinox.launcher.Main
	  set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% -noExit
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% %LAUNCH_OPTS%
      set KERNEL_JAVA_PARMS=%KERNEL_JAVA_PARMS% %ADDITIONAL_ARGS%

      rem Now run it
        PUSHD %KERNEL_HOME%
        "%JAVA_HOME%\bin\java" %KERNEL_JAVA_PARMS%
        POPD

goto :eof

rem ------------------------------
:doStopCommand
  
  shift
  rem The shift must be here :()

  rem Set defaults
  set CONFIG_DIR=%KERNEL_HOME%\configuration
  if not defined TRUSTSTORE_PATH set TRUSTSTORE_PATH=%CONFIG_DIR%\keystore
  if not defined TRUSTSTORE_PASSWORD set TRUSTSTORE_PASSWORD=changeit
  if not defined JMX_PORT set JMX_PORT=9875
  set OTHER_ARGS=

  rem Loop through options
  :stopOptionLoop

  if "%~1"=="" goto endStopOptionLoop  
  if "%~1"=="-truststore" goto truststoreStop
  if "%~1"=="-truststorePassword" goto truststorePasswordStop
  if "%~1"=="-configDir" goto configDirStop 
  if "%~1"=="-jmxport" goto jmxportStop
  
  set OTHER_ARGS=%OTHER_ARGS% "%~1"
    
  :continueStopOptionLoop
  shift
  goto stopOptionLoop

  :truststoreStop
  set TRUSTSTORE_PATH=%~2
  shift
  goto continueStopOptionLoop

  :truststorePasswordStop
  set TRUSTSTORE_PASSWORD=%~2
  shift
  goto continueStopOptionLoop

  :configDirStop
    set CONFIG_DIR=%~2
    rem unless absolute, treat as relative to kernel home
    if "%CONFIG_DIR:~1%"=="\" goto absoluteConfigDirStop
    if "%CONFIG_DIR:~1,2%"==":\" goto absoluteConfigDirStop
    set CONFIG_DIR=%KERNEL_HOME%\%CONFIG_DIR%
  :absoluteConfigDirStop
    shift
    goto continueStopOptionLoop

  :jmxportStop
  set JMX_PORT=%~2
  shift
  goto continueStopOptionLoop

  :endStopOptionLoop

  rem Call shutdown client

    rem Extend JMX options
    set JMX_OPTS=%JMX_OPTS% -Djavax.net.ssl.trustStore="%TRUSTSTORE_PATH%"
    set JMX_OPTS=%JMX_OPTS% -Djavax.net.ssl.trustStorePassword=%TRUSTSTORE_PASSWORD%
    set OTHER_ARGS=%OTHER_ARGS% -jmxport %JMX_PORT%

    rem Marshall parameters
    set SHUTDOWN_PARMS= %JAVA_OPTS% %JMX_OPTS%
    set SHUTDOWN_PARMS=%SHUTDOWN_PARMS% -classpath "%CLASSPATH%"
    set SHUTDOWN_PARMS=%SHUTDOWN_PARMS% -Dorg.eclipse.virgo.kernel.home="%KERNEL_HOME%"
    set SHUTDOWN_PARMS=%SHUTDOWN_PARMS% -Dorg.eclipse.virgo.kernel.authentication.file="%CONFIG_DIR%\org.eclipse.virgo.kernel.users.properties"
    set SHUTDOWN_PARMS=%SHUTDOWN_PARMS% org.eclipse.virgo.nano.shutdown.ShutdownClient
    set SHUTDOWN_PARMS=%SHUTDOWN_PARMS% %OTHER_ARGS%

    rem Run Java program
    PUSHD %KERNEL_HOME%
    "%JAVA_HOME%\bin\java" %SHUTDOWN_PARMS%
    POPD

goto :eof
