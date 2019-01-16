#!/usr/bin/env bash

# Copyright 2016-2019 B2i Healthcare Pte Ltd, http://b2i.sg
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# This sample backup script for the Snow Owl server creates a .zip file in the 
# current directory with the saved contents of the MySQL databases and semantic
# indexes for each terminology, as well as supporting index content, while the
# server is kept running.

#
# Basic mandatory configuration options (these should be set by editing this script before running it)
#

# The location of the Snow Owl Server installation directory, eg. /opt/snowowl/server/latest (no trailing slash)
SO_HOME="/opt/snowowl/server/latest"

# The base URL of the REST services to use
BASE_URL="http://localhost:8080/snowowl"

# The username used for creating hot backups (should be a valid Snow Owl user)
SNOWOWL_USERNAME="snowowl"

# The password for the user given above
SNOWOWL_PASSWORD="snowowl"

# The MySQL username used to take backups
MYSQL_USERNAME="backup"

# The password for the user given above
MYSQL_PASSWORD="secret"

# Elasticsearch URL to connect to for taking index snapshots
ES_URL="http://localhost:9200"

# The backup location where all data will be backed up (should be a path on either a local or mounted disk), including metadata files for subsequent backups
BACKUP_DIR="/opt/snowowl/backup"

# Number of snapshots to keep in incremental fashion, by default the last 10 snapshots will be kept
NUMBER_OF_SNAPSHOTS_TO_KEEP=10

#
# Advanced configuration options
#

# The number of milliseconds to wait before giving up trying to lock the complete repository.
LOCK_TIMEOUT_MILLIS=30000

# The connection timeout for HTTP requests in seconds.
CONNECTION_TIMEOUT_SECONDS=5

# The initial waiting time after sending a message to connected users in minutes.
INITIAL_WAIT_MINUTES=5

# The number of retries the first lock is attempted.
RETRIES=5

# The waiting time after an unsuccessful lock attempt in minutes.
RETRY_WAIT_MINUTES=5

# Elasticsearch configuration for repository snapshots (number of bytes per sec to use for snapshot throttle)
SNAPSHOT_BYTES_PER_SEC="250mb"

# Use archive structure without container folder
USE_ROOT_ARCHIVE_STRUCTURE=false

#
# Script variables 
#

# The base URL for administrative services
SO_ADMIN_URL="$BASE_URL/admin"

# The timestamp suffix for the top-level directory, eg. 20120904_1021
CURRENT_DATE=`date +%Y%m%d_%H%M`

# The working directory and the resulting archive file prefix
ARCHIVE_PREFIX="snowowl_$CURRENT_DATE"

# The absolute path to the above
ABSOLUTE_ARCHIVE_PREFIX="$BACKUP_DIR/$ARCHIVE_PREFIX"

usage() {

cat << EOF

NAME:
	Snow Owl Backup/Restore Tool

DESCRIPTION:
	This sample backup script for the Snow Owl server creates a .zip file in the
	current directory with the saved contents of the MySQL databases and semantic
	indexes for each terminology, as well as supporting index content, while the
	server is kept running.

USAGE: $0 [OPTIONS] [PATH_TO_SERVER]

	[PATH_TO_SERVER]	specifing the server's home dir through a parameter will
				always overwrite the stored values. If the path never
				changes it is not mandatory to pass it in as a parameter,
				more useful to store it in the script.

OPTIONS:

	-r	do not use container dir inside the resulting archive

	-i	no initial wait time will be applied for the users, backup
		starts immediately

	-h	display help

EXAMPLES:

	If all user credentials are stored in the script but server home was not
	specified:

		$0 /path/to/server

	If server location and all user credentials are stored in the script but
	the resulting archive must contain all files in it's root:

		$0 -r
	
	If all user credentials are specified, server home dir is provided through
	a parameter and initial wait time can be ignored:
	
		$0 -i /path/to/server

EOF

}

# Prints a message to stdout with the current date and time.
echo_date() {
	echo -e "[`date +\"%Y-%m-%d %H:%M:%S\"`] $@"
}

# Prints an error message to stderr and exits the script with a non-zero status.
error_exit() {
	echo -e "[`date +\"%Y-%m-%d %H:%M:%S\"`] $@" >&2
	exit 1
}

# Invokes curl to make an HTTP request to the Snow Owl Server; stores returned message and HTTP status code in output variables.
so_rest_call() {
	rest_call --connect-timeout "$CONNECTION_TIMEOUT_SECONDS" --user "$SNOWOWL_USERNAME:$SNOWOWL_PASSWORD" "$@"
}

# Invokes curl to make an HTTP request to any Web Server or API; stores returned message and HTTP status code in output variables.
rest_call() {
	CURL_OUTPUT=`curl -q --fail --silent --show-error --write-out "\n%{http_code}" "$@"`
	CURL_MESSAGE=`echo "$CURL_OUTPUT" | head -n-1`
	CURL_HTTP_STATUS=`echo "$CURL_OUTPUT" | tail -n1`
}

# Tries to acquire a global lock that prevents writing to any of the terminology stores on any available branch.
lock_all_repositories() {
	echo_date "Locking repositories..."
	so_rest_call --data-urlencode "timeoutMillis=$LOCK_TIMEOUT_MILLIS" "$SO_ADMIN_URL/repositories/lock"
}

# Removes the lock from all repositories.
unlock_all_repositories() {
	echo_date "Unlocking repositories..."
	so_rest_call --data-urlencode "" "$SO_ADMIN_URL/repositories/unlock"
}

# Cleans up the global repository lock and exits with an error.
unlock_all_repositories_and_exit() {
	unlock_all_repositories
	error_exit "$1"
}

# Locks the specified repository, preventing write access to all of its branches.
lock_repository() {
	echo_date "Locking repository $REPOSITORY..."
	so_rest_call --data-urlencode "" "$SO_ADMIN_URL/repositories/$REPOSITORY/lock"
	
	if [ "$CURL_HTTP_STATUS" -ne "204" ]; then
		unlock_all_repositories_and_exit "Couldn't lock repository $REPOSITORY. Exiting with error."
	fi
}

# Unlocks the specified repository if it was already locked.
unlock_repository() {
	echo_date "Unlocking repository $REPOSITORY..."
	so_rest_call --data-urlencode "" "$SO_ADMIN_URL/repositories/$REPOSITORY/unlock"
}

# Unlocks the specified repository, cleans up the global repository lock and exits with an error.
unlock_repository_and_exit() {
	unlock_repository
	unlock_all_repositories_and_exit "$1"
}

# Copies the contents of the specified repository to the zip file.
backup_mysql() {
	echo_date "Backing up MySQL content for database $REPOSITORY..."
	# Create the MySQL backup directory
	mkdir --parents $BACKUP_DIR/mysql
	cd $BACKUP_DIR/mysql

	FULL_BKP_DIR="base"
	INCREMENTAL_PREFIX="inc_"

	# Check that the full backup directory exists
	if [ ! -d "$BACKUP_DIR/mysql/$FULL_BKP_DIR" ]; 
	then
		# if not take a full backup
   	xtrabackup --backup --target-dir=$FULL_BKP_DIR -u$MYSQL_USERNAME -p$MYSQL_PASSWORD || unlock_repository_and_exit "Couldn't create full backup of MySQL. Exiting with error."
		LAST_BKP=$FULL_BKP_DIR
	else 
		# otherwise get the latest incremental backup directory under $BACKUP_DIR/mysql
		LAST_BKP=$(ls -t . | grep $INCREMENTAL_PREFIX | head -1)
	fi

	# take an incremental backup (even if we did a full seconds ago, this is needed to keep track of number of snapshots properly)
  xtrabackup --backup --target-dir="$INCREMENTAL_PREFIX$CURRENT_DATE" --incremental-basedir=$LAST_BKP -u$MYSQL_USERNAME -p$MYSQL_PASSWORD || unlock_repository_and_exit "Couldn't create incremental backup of MySQL. Exiting with error."

	# Keep only the last N backup, count the current incremental snapshots
	NUMBER_OF_SNAPSHOTS=$( ls . | grep $INCREMENTAL_PREFIX | wc -l )
	# Do the cleanup if we have more than the configured snapshots to keep
	if [ $NUMBER_OF_SNAPSHOTS -gt $NUMBER_OF_SNAPSHOTS_TO_KEEP ];
	then
		SNAPSHOTS_TO_CLEAN=$(ls . | grep $INCREMENTAL_PREFIX | head -$(( $NUMBER_OF_SNAPSHOTS - $NUMBER_OF_SNAPSHOTS_TO_KEEP)) )
		echo_date "Cleaning $(( $NUMBER_OF_SNAPSHOTS - $NUMBER_OF_SNAPSHOTS_TO_KEEP )) snapshot(s) of MySQL backups..."
		# Prepare the full bkp dir for snapshot cleaning
		xtrabackup --prepare --apply-log-only --target-dir=$FULL_BKP_DIR || unlock_repository_and_exit "Couldn't clean up snapshot $snapshot. Exiting with error."
		for snapshot in $SNAPSHOTS_TO_CLEAN; do
			echo_date "Cleaning MySQL snapshot of $snapshot..."
			# Clean the now obsolete snapshot
			xtrabackup --prepare --apply-log-only --target-dir=$FULL_BKP_DIR --incremental-dir=$snapshot || unlock_repository_and_exit "Couldn't clean up snapshot $snapshot. Exiting with error."
			rm -rf $snapshot
		done
	fi

	cd --
	echo_date "Done backing up MySQL database $REPOSITORY."
}

backup_resources() {
	echo_date "Backing up resources directory (attachments, etc)..."

	# Take backup
	mkdir --parents $BACKUP_DIR/resources
	cd $SO_HOME
	if [ -z "$(ls -A $BACKUP_DIR/resources)" ]; 
	then
		rsync --verbose --recursive --delete --dirs --exclude 'indexes' "resources/" "$BACKUP_DIR/resources/$CURRENT_DATE" || unlock_repository_and_exit "Couldn't take full backup of resources directory. Exiting with error."
  	else 
		LAST_BKP=$(ls -t $BACKUP_DIR/resources | head -1) 	
		rsync --verbose --recursive --delete --dirs --exclude 'indexes' --link-dest="$BACKUP_DIR/resources/$LAST_BKP" "resources/" "$BACKUP_DIR/resources/$CURRENT_DATE" || unlock_repository_and_exit "Couldn't take incremental backup of resources directory. Exiting with error."
	fi
	cd --

	# Keep only the last N backup
	cd $BACKUP_DIR/resources
	# Count the current incremental snapshots
	NUMBER_OF_SNAPSHOTS=$( ls . | wc -l )
	# Do the cleanup if we have more than the configured snapshots to keep
	if [ $NUMBER_OF_SNAPSHOTS -gt $NUMBER_OF_SNAPSHOTS_TO_KEEP ];
	then
		SNAPSHOTS_TO_CLEAN=$( ls . | head -$(( $NUMBER_OF_SNAPSHOTS - $NUMBER_OF_SNAPSHOTS_TO_KEEP)) )
		echo_date "Cleaning $(( $NUMBER_OF_SNAPSHOTS - $NUMBER_OF_SNAPSHOTS_TO_KEEP )) snapshot(s) of resources backups..."
		for snapshot in $SNAPSHOTS_TO_CLEAN; do
			echo_date "Cleaning resources snapshot of $snapshot..."
			# Clean the now obsolete snapshot
			rm -rf $snapshot
		done
	fi
	cd --
	
	echo_date "Done backing up resources directory (attachments, etc)."
}

backup_indexes() {
	echo_date "Backing up Elasticsearch indices..."
	mkdir --parents $BACKUP_DIR/indexes

	# Create a backup repository first (TODO check if exists)
	rest_call -XPUT -H 'Content-Type: application/json' $ES_URL/_snapshot/backup -d '{ "type": "fs", "settings": { "location": "$BACKUP_DIR/indexes", "max_snapshot_bytes_per_sec": "$SNAPSHOT_BYTES_PER_SEC" } }'

  # Start backing up the index and wait for its completion
	rest_call -XPUT $ES_URL/_snapshot/backup/$CURRENT_DATE?wait_for_completion=true

	echo_date "Done backing up Elasticsearch indices."
}

# Retrieves the list of terminology repositories and backs them up one by one.
backup_repositories() {
	echo_date "Backing up installed terminology repositories..."

	#backup_mysql
	backup_resources
  #backup_indexes

	echo "$BACKUP_TYPE $CURRENT_DATE" > $BACKUP_DIR/last_backup_info

	echo_date "Done backing up installed repositories."
}

# Sends a message to connected users.
send_message() {
	so_rest_call -H "Content-Type:text/plain" --data "$1" "$SO_ADMIN_URL/messages/send"
}

# Checks input arguments and test whether the script is ready to be executed.
check_arguments() {
	if [ "x$SO_HOME" = "x" ]; then
		error_exit "Please set the variable SO_HOME or pass it in as an argument before running this script. Exiting with error."
	fi

	if [ ! -d "$SO_HOME/resources" ]; then
		error_exit "No resources directory could be found for the installation under '$SO_HOME'. Exiting with error."
	fi
	
	if [ "x$SNOWOWL_USERNAME" = "x" ]; then
		error_exit "Please set the variable SNOWOWL_USERNAME before running this script. Exiting with error."
	fi
	
	if [ "x$SNOWOWL_PASSWORD" = "x" ]; then
		error_exit "Please set the variable SNOWOWL_PASSWORD before running this script. Exiting with error."
	fi

	if [ "x$MYSQL_USERNAME" = "x" ]; then
		error_exit "Please set the variable MYSQL_USERNAME before running this script. Exiting with error."
	fi
	
	if [ "x$MYSQL_PASSWORD" = "x" ]; then
		error_exit "Please set the variable MYSQL_PASSWORD before running this script. Exiting with error."
	fi
}

# Main script starts here.
main() {

	echo_date "----------------------------"
	check_arguments

	#echo_date "Create backup destination directory '$ABSOLUTE_ARCHIVE_PREFIX'."
	
	# Creates both $ABSOLUTE_ARCHIVE_PREFIX and $ABSOLUTE_ARCHIVE_PREFIX/resources 
	#mkdir --parents --verbose "$ABSOLUTE_ARCHIVE_PREFIX/resources" || error_exit "Couldn't create directory '$ABSOLUTE_ARCHIVE_PREFIX'. Exiting with error."
	
	echo_date "Starting backup; sending message to connected users."

	if [ $INITIAL_WAIT_MINUTES -eq 0 ]; then
		send_message "Write access to repositories is disabled while the system creates a backup."
	else
		send_message "Write access to repositories will be disabled while the system creates a backup in $INITIAL_WAIT_MINUTES minutes."
	fi
	
	if [ "$CURL_HTTP_STATUS" = "000" ]; then
		error_exit "Couldn't send message to users; the server is not running. Exiting with error."
	fi
	
	if [ $INITIAL_WAIT_MINUTES -ne 0 ]; then
		echo_date "Waiting $INITIAL_WAIT_MINUTES minutes for users to finish..."
		sleep "$INITIAL_WAIT_MINUTES"m
	fi
	
	for i in $(seq 1 "$RETRIES"); do 
		lock_all_repositories

		if [ "$CURL_HTTP_STATUS" -ne "204" ]; then
			echo_date "Couldn't lock repositories, waiting $RETRY_WAIT_MINUTES minutes to retry ($i)..."
			sleep "$RETRY_WAIT_MINUTES"m
		else
			break
		fi
	done
	
	if [ "$CURL_HTTP_STATUS" -ne "204" ]; then
		error_exit "Couldn't lock repositories after $RETRIES attempts. Exiting with error."
	fi
	
	# Need to switch directories because of relative paths within the zip
	cd "$SO_HOME" || error_exit "Couldn't switch to index directory '$SO_HOME'. Exiting with error." 

	backup_repositories
	unlock_all_repositories
	
	echo_date "Notifying users that write access has been restored."
	send_message "Write access to repositories restored."	

	#echo_date "Creating archive..."

	# Set access permissions for files
	#cd "$ABSOLUTE_ARCHIVE_PREFIX"
	#find . -type f -exec chmod +x '{}' \;
	
	#if [ "$USE_ROOT_ARCHIVE_STRUCTURE" = true ]; then
	#	zip --recurse-paths --move --test "$ABSOLUTE_ARCHIVE_PREFIX.zip" * || error_exit "Archive creation failed; the backup is incomplete. Exiting with error."
	#	rm --recursive --force "$ABSOLUTE_ARCHIVE_PREFIX"
	#else
		# Return to the initial directory
	#	cd "$INITIAL_PWD"
	#	zip --recurse-paths --move --test "$ARCHIVE_PREFIX.zip" "$ARCHIVE_PREFIX" || error_exit "Archive creation failed; the backup is incomplete. Exiting with error."
	#fi
	
	echo_date "Backup script finished successfully."
	exit 0
}

while getopts ":hri" opt; do
	case "$opt" in
		h) 
			usage
			exit 0
			;;
		r)
			USE_ROOT_ARCHIVE_STRUCTURE=true
			;;
		i)
			INITIAL_WAIT_MINUTES=0
			;;
		\?)
			echo "Invalid option: -$OPTARG" >&2
			exit 1
			;;
		:)
			echo "Option -$OPTARG requires an argument." >&2
			exit 1
			;;
	esac
done

shift "$((OPTIND - 1))"

if [ ! -z "$1" ]; then

	if [ -d "$1" ]; then
		SO_HOME=$(echo ${1%/})
	else
		echo_date "Invalid parameter, using stored values."
	fi

fi

if [ ! -z "$2" ]; then
	
	echo_error "More than one parameter is not allowed."
	usage
	exit 1

fi

# Ensure we have an existing backup directory
mkdir --parents $BACKUP_DIR

# Ensures that only a single instance is running at any time
LOCKFILE="$BACKUP_DIR/instance.lock"

(
	flock -n 200 || error_exit "Another backup script is already running. Exiting with error."
	trap "rm $LOCKFILE" EXIT
	main
) 200> $LOCKFILE
