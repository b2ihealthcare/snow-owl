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

# Run start command in the same process using exec
exec "$SCRIPT_DIR"/"$EXECUTABLE" start "$@"
