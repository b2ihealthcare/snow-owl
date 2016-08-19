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

SCRIPT_DIR=`dirname $SCRIPT`
EXECUTABLE="dmk.sh"

#
# identify yourself when running under cygwin
#
cygwin=false
case "$(uname)" in
    CYGWIN*) cygwin=true ;;
esac
export cygwin

#
# Custom cleanup for Snow Owl Server
#
echo "Script directory: $SCRIPT_DIR"
echo "Deleting work directory."
rm -rf $SCRIPT_DIR/../work
echo "Deleting workspace directory."
rm -rf $SCRIPT_DIR/../workspace
echo "Finished cleanup, starting server."

#
# Starting server
# http://veithen.github.io/2014/11/16/sigterm-propagation.html
#

# Set up signal handler for TERM (sent by supervisor) and INT (keyboard interrupt)
# Note: we don't pass additional arguments from the startup script here
trap '{ echo "Stopping server on signal." ; "$SCRIPT_DIR"/"$EXECUTABLE" stop ; }' TERM INT

# Run start command in subshell (child process)
"$SCRIPT_DIR"/"$EXECUTABLE" start "$@" &

# Capture PID of child and wait for completion
PID=$!
wait $PID

# The first wait can return before the child completes if a signal has been received.
# Remove signal handler, then wait again (which will return immediately if the child
# did exit in the meantime)
trap - TERM INT
wait $PID

# Capture the exit status from the child, as reported by wait
exit $?
