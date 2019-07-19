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

shopt -s extglob

# start the kernel
if [[ "$CONFIG_DIR" != /* ]]
then
    CONFIG_DIR=$KERNEL_HOME/$CONFIG_DIR
fi

CONFIG_AREA=$KERNEL_HOME/work

if [ ! -z $cygwin ]; then
    KERNEL_HOME=$(cygpath -wp $KERNEL_HOME)
    CONFIG_AREA=$(cygpath -wp $CONFIG_AREA)
fi

if [ -z "$JAVA_HOME" ]
then
        JAVA_EXECUTABLE=java
else
        JAVA_EXECUTABLE=$JAVA_HOME/bin/java
fi

TMP_DIR=$KERNEL_HOME/work/tmp
# Ensure that the tmp directory exists
mkdir -p "$TMP_DIR"

SO_JAVA_OPTS="-Xms2g \
                -Xmx2g \
                -XX:+AlwaysPreTouch \
                -Xss1m \
                -server \
                -Djava.awt.headless=true \
                -Declipse.ignoreApp=true \
                -Dosgi.noShutdown=true \
                -Declipse.application.launchDefault=false \
                -Dosgi.classloader.type=nonparallel \
                -XX:+AlwaysLockClassLoader \
                -Djetty.port=9090 \
				-Djetty.home=$KERNEL_HOME/configuration \
				-Djetty.etc.config.urls=jetty.xml,jetty-http.xml,jetty-deployer.xml \
                -Djetty.home.bundle=org.eclipse.jetty.osgi.boot \
                -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.Slf4jLog \
                -XX:+UseConcMarkSweepGC \
                -XX:CMSInitiatingOccupancyFraction=75 \
                -XX:+UseCMSInitiatingOccupancyOnly \
                -XX:+HeapDumpOnOutOfMemoryError \
                -Djdk.security.defaultKeySize=DSA:1024 \
                $SO_JAVA_OPTS"

cd "$KERNEL_HOME";
exec "$JAVA_EXECUTABLE $SO_JAVA_OPTS \
  -Djava.io.tmpdir="$TMP_DIR" \
  -Dosgi.install.area="$KERNEL_HOME" \
  -Dosgi.configuration.area="$CONFIG_AREA" \
  -jar plugins/org.eclipse.equinox.launcher_1.5.300.v20190213-1655.jar"