#!/bin/bash

SCRIPT="$0"

# SCRIPT may be an arbitrarily deep series of symlinks. Loop until we have the concrete path.
while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

# determine kernel home
KERNEL_HOME=`dirname "$SCRIPT"`/..

# make KERNEL_HOME absolute
KERNEL_HOME=`cd "$KERNEL_HOME"; pwd`

# execute user setenv script if needed
if [ -r "$KERNEL_HOME/bin/setenv.sh" ]
then
	. $KERNEL_HOME/bin/setenv.sh
fi

# setup classpath and java environment
. "$KERNEL_HOME/bin/setupClasspath.sh"

# Run java version check with the discovered java jvm.
. "$KERNEL_HOME/bin/checkJava.sh"

shopt -s extglob
	
# parse the command we executing
COMMAND=$1
shift;
	
if [ "$COMMAND" = "start" ]
then
	
	# parse the standard arguments
	CONFIG_DIR=$KERNEL_HOME/configuration
	CLEAN_FLAG=
	NO_START_FLAG=

	SHELL_FLAG=
	
	DEBUG_FLAG=
	DEBUG_PORT=8000
	SUSPEND=n
	if [ -z "$JMX_PORT" ]
	then
		JMX_PORT=9875
	fi
	
	if [ -z "$KEYSTORE_PASSWORD" ]
	then
		KEYSTORE_PASSWORD=changeit
	fi
	
	ADDITIONAL_ARGS=

	while (($# > 0))
		do
		case $1 in
		-debug)
				DEBUG_FLAG=1
				if [[ "$2" == +([0-9]) ]]
				then
					DEBUG_PORT=$2
					shift;
				fi
				;;
		-clean)
				CLEAN_FLAG=1
				;;
		-configDir)
				CONFIG_DIR=$2
				shift;
				;;
		-jmxport)
				JMX_PORT=$2
				shift;
				;;
		-keystore)
				KEYSTORE_PATH=$2
				shift;
				;;
		-keystorePassword)
				KEYSTORE_PASSWORD=$2
				shift;
				;;
		-noStart)
				NO_START_FLAG=1
				;;
				
		-suspend)
				SUSPEND=y
				;;
		-shell)
				SHELL_FLAG=1
				;;
		*)
				ADDITIONAL_ARGS="$ADDITIONAL_ARGS $1"
				;;
		esac
		shift
	done
	
	# start the kernel
	if [[ "$CONFIG_DIR" != /* ]]
	then
	    CONFIG_DIR=$KERNEL_HOME/$CONFIG_DIR
	fi

	if [ -z "$KEYSTORE_PATH" ]
	then
	    KEYSTORE_PATH=$CONFIG_DIR/keystore
	fi

	if [ "$DEBUG_FLAG" ]
	then
		DEBUG_OPTS=" \
			-Xdebug \
			-Xrunjdwp:transport=dt_socket,address=$DEBUG_PORT,server=y,suspend=$SUSPEND"
	fi

	if [ "$CLEAN_FLAG" ]
	then
        echo "Cleaning the serviceability and working directories..."
        rm -rf "$KERNEL_HOME/work"
        rm -rf "$KERNEL_HOME/serviceability"

        LAUNCH_OPTS="$LAUNCH_OPTS -clean" #equivalent to setting osgi.clean to "true"
	fi
	
	if [ "$SHELL_FLAG" ]
	then
	    echo "Warning: Kernel shell not supported; -shell option ignored."
		# LAUNCH_OPTS="$LAUNCH_OPTS -Forg.eclipse.virgo.kernel.shell.local=true"
	fi

    ACCESS_PROPERTIES=$CONFIG_DIR/org.eclipse.virgo.kernel.jmxremote.access.properties
    AUTH_LOGIN=$CONFIG_DIR/org.eclipse.virgo.kernel.authentication.config
    AUTH_FILE=$CONFIG_DIR/org.eclipse.virgo.kernel.users.properties
    CONFIG_AREA=$KERNEL_HOME/work
    JAVA_PROFILE=$KERNEL_HOME/configuration/java-server.profile

    if $cygwin; then
        ACCESS_PROPERTIES=$(cygpath -wp $ACCESS_PROPERTIES)
        AUTH_LOGIN=$(cygpath -wp $AUTH_LOGIN)
        AUTH_FILE=$(cygpath -wp $AUTH_FILE)
        KERNEL_HOME=$(cygpath -wp $KERNEL_HOME)
        CONFIG_DIR=$(cygpath -wp $CONFIG_DIR)
        CONFIG_AREA=$(cygpath -wp $CONFIG_AREA)
        JAVA_PROFILE=$(cygpath -wp $JAVA_PROFILE)
    fi
	
	# Set the required permissions on the JMX configuration files
	chmod 600 "$ACCESS_PROPERTIES"

   	if [ -z "$JAVA_HOME" ]
    then
      	JAVA_EXECUTABLE=java
    else
     	JAVA_EXECUTABLE=$JAVA_HOME/bin/java
    fi

	# If we get here we have the correct Java version.
	
	if [ -z "$NO_START_FLAG" ]
	then
		TMP_DIR=$KERNEL_HOME/work/tmp
		# Ensure that the tmp directory exists
		mkdir -p "$TMP_DIR"
		
		#Added awt.headless - http://mail-archives.apache.org/mod_mbox/poi-user/200705.mbox/%3C15719338671.20070504144714@dinom.ru%3E
        JAVA_OPTS="	$JAVA_OPTS \
        			-Xms12g \
       		        -Xmx12g \
                    -XX:+AlwaysPreTouch \
                    -Xss1m \
                    -Xloggc:$KERNEL_HOME/`date +%F_%H%M-%S`-gc.log \
                    -XX:+PrintGCDetails \
					-XX:+PrintGCDateStamps \
					-XX:+PrintGCApplicationStoppedTime \
					-XX:+PrintGCApplicationConcurrentTime \
					-XX:+PrintTenuringDistribution \
					-XX:+PrintGCCause \
					-XX:+UseGCLogFileRotation \
					-XX:NumberOfGCLogFiles=10 \
					-XX:GCLogFileSize=2M \
                    -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl \
                    -Djavax.xml.transform.TransformerFactory=com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl \
                    -Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl \
                    -Djava.awt.headless=true \
                    -XX:+AlwaysLockClassLoader \
                    -Dosgi.classloader.type=nonparallel" \
                    -Djdk.security.defaultKeySize=DSA:1024

		cd "$KERNEL_HOME"; exec $JAVA_EXECUTABLE \
			$JAVA_OPTS \
			$DEBUG_OPTS \
			$JMX_OPTS \
			-XX:+HeapDumpOnOutOfMemoryError \
			-XX:ErrorFile="$KERNEL_HOME/serviceability/error.log" \
			-XX:HeapDumpPath="$KERNEL_HOME/serviceability/heap_dump.hprof" \
			-Djava.security.auth.login.config="$AUTH_LOGIN" \
			-Dorg.eclipse.virgo.kernel.authentication.file="$AUTH_FILE" \
			-Djava.io.tmpdir="$TMP_DIR" \
			-Dorg.eclipse.virgo.kernel.home="$KERNEL_HOME" \
			-Dorg.eclipse.virgo.kernel.config="$CONFIG_DIR" \
			-Dosgi.sharedConfiguration.area="$CONFIG_DIR" \
			-Dosgi.java.profile="file:$JAVA_PROFILE" \
            -Declipse.ignoreApp=true \
            -Dosgi.install.area="$KERNEL_HOME" \
            -Dosgi.configuration.area="$CONFIG_AREA" \
            -Dssh.server.keystore="$CONFIG_DIR/hostkey.ser" \
            -Dosgi.frameworkClassPath="$FWCLASSPATH" \
            -Djava.endorsed.dirs="$KERNEL_HOME/lib/endorsed" \
            -Dcom.sun.management.jmxremote.port=$JMX_PORT \
		    -Dcom.sun.management.jmxremote.authenticate=true \
	    	-Dcom.sun.management.jmxremote.login.config=virgo-kernel \
    		-Dcom.sun.management.jmxremote.access.file="$ACCESS_PROPERTIES" \
		    -Djavax.net.ssl.keyStore="$KEYSTORE_PATH" \
		    -Djavax.net.ssl.keyStorePassword="$KEYSTORE_PASSWORD" \
		    -Dcom.sun.management.jmxremote.ssl=true \
		    -Dcom.sun.management.jmxremote.ssl.need.client.auth=false \
            -classpath "$CLASSPATH" \
			org.eclipse.equinox.launcher.Main \
            -noExit \
			$LAUNCH_OPTS \
			$ADDITIONAL_ARGS
	fi
elif [ "$COMMAND" = "stop" ]
then

	CONFIG_DIR="$KERNEL_HOME/configuration"

	#parse args for the script
	if [ -z "$TRUSTSTORE_PATH" ]
	then
		TRUSTSTORE_PATH=$CONFIG_DIR/keystore
	fi
	
	if [ -z "$TRUSTSTORE_PASSWORD" ]	
	then
		TRUSTSTORE_PASSWORD=changeit
	fi

	if [ -z "$JMX_PORT" ]
	then
		JMX_PORT=9875
	fi

	shopt -s extglob

	while (($# > 0))
		do
		case $1 in
		-truststore)
				TRUSTSTORE_PATH=$2
				shift;
				;;
		-truststorePassword)
				TRUSTSTORE_PASSWORD=$2
				shift;
				;;
		-configDir)
				CONFIG_DIR=$2
				shift;
				;;
		-jmxport)
				JMX_PORT=$2
				shift;
				;;
		*)
			OTHER_ARGS+=" $1"
			;;
		esac
		shift
	done
	
	OTHER_ARGS+=" -jmxport $JMX_PORT"

    if $cygwin; then
        KERNEL_HOME=$(cygpath -wp $KERNEL_HOME)
        CONFIG_DIR=$(cygpath -wp $CONFIG_DIR)
    fi

	exec $JAVA_EXECUTABLE \
	     $JAVA_OPTS \
	     $JMX_OPTS \
		-classpath "$CLASSPATH" \
		-Djavax.net.ssl.trustStore="$TRUSTSTORE_PATH" \
		-Djavax.net.ssl.trustStorePassword="$TRUSTSTORE_PASSWORD" \
		-Dorg.eclipse.virgo.kernel.home="$KERNEL_HOME" \
		-Dorg.eclipse.virgo.kernel.authentication.file="$CONFIG_DIR/org.eclipse.virgo.kernel.users.properties" \
		org.eclipse.virgo.nano.shutdown.ShutdownClient $OTHER_ARGS
	
else
	echo "Unknown command: ${COMMAND}"
fi

