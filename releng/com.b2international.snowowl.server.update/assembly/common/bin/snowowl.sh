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

echo "SO_PATH_CONF env: $SO_PATH_CONF"
SO_JAVA_OPTS="-Xms6g \
                -Xmx6g \
                -XX:+AlwaysPreTouch \
                -Xss1m \
                -server \
                -Djava.awt.headless=true \
                -Dosgi.noShutdown=true \
                -Dosgi.classloader.type=nonparallel \
                -Dosgi.console=2501 \
                -XX:+AlwaysLockClassLoader \
                -Djetty.port=8080 \
                -XX:+UseConcMarkSweepGC \
                -XX:CMSInitiatingOccupancyFraction=75 \
                -XX:+UseCMSInitiatingOccupancyOnly \
                -XX:+HeapDumpOnOutOfMemoryError \
                -Djdk.security.defaultKeySize=DSA:1024 \
                -Dso.path.conf="$SO_PATH_CONF" \
                $SO_JAVA_OPTS"

pushd "$KERNEL_HOME"

echo "JAVA_EXECUTABLE $JAVA_EXECUTABLE"
echo "SO_JAVA_OPTS $SO_JAVA_OPTS"
echo "TMP_DIR $TMP_DIR"
echo "JAVA_EXECUTABLE $JAVA_EXECUTABLE"
echo "KERNEL_HOME $KERNEL_HOME"
echo "CONFIG_AREA $CONFIG_AREA"
exec $JAVA_EXECUTABLE $SO_JAVA_OPTS \
  -Djava.io.tmpdir="$TMP_DIR" \
  -Dosgi.install.area="$KERNEL_HOME" \
  -Dosgi.configuration.area="$CONFIG_AREA" \
  -jar plugins/org.eclipse.equinox.launcher_1.5.700.v20200207-2156.jar

popd
