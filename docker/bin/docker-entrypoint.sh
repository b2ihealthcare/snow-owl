#!/bin/bash
set -e

# Inspired by https://github.com/elastic/elasticsearch/blob/6.8/distribution/docker/src/docker/bin/docker-entrypoint.sh

# Files created by Snow Owl should always be group writable too
umask 0002

# Allow user specify custom CMD, e.g. to run /bin/bash to check the image
if [[ "$1" != "sowrapper" ]]; then
	exec "$@"
fi

exec /usr/share/snowowl/bin/snowowl.sh
