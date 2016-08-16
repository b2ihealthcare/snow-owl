#!/usr/bin/env bash

#
# Copyright (c) 2013-2015 B2i Healthcare. All rights reserved.
#

# Loads database content for Snow Owl from SQL dumps. Uses the name of the SQL
# file(s) specified as the target database for each file.
#
# Usage: ./snowowl_load_db [dbfile1] [dbfile2] ...

if [[ $# -eq 0 ]]; then
    echo "No SQL files were specified for loading. Exiting."
    exit 1
fi

MYSQL=$(which mysql)
BASENAME=$(which basename)
USER="snowowl"
PASSWORD="snowowl_pwd"

for DBFILE in "$@"
do
    DB="$(${BASENAME} "$DBFILE")"
    ${MYSQL} --batch -u${USER} -p${PASSWORD} "${DB%.sql}" < "${DBFILE}"

    if [ $? -ne 0 ]; then
        echo "Loading from file ${DB} failed."
    else
        echo "Loading from file ${DB} finished successfully."
    fi
done

echo "All files processed."
exit 0
