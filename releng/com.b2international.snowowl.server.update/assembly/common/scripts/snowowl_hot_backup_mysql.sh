#!/usr/bin/env bash

# This sample backup script for the Snow Owl server creates a .zip file in the 
# current directory with the saved contents of the MySQL databases and semantic
# indexes for each terminology, as well as supporting index content, while the
# server is kept running.

# The following variables should be set by editing this script before running it:

# The location of the Snow Owl Server installation, eg. /opt/snowowl (no trailing slash)
SNOW_OWL_SERVER_HOME=""

# The username used for creating hot backups (should be a valid Snow Owl user)
SNOWOWL_USERNAME="user@localhost.localdomain"

# The password for the user given above
SNOWOWL_PASSWORD="password123"

# The MySQL username used for creating the database dump
MYSQL_USERNAME="snowowl"

# The password for the user given above
MYSQL_PASSWORD="snowowl"

# The timestamp suffix for the top-level directory, eg. 20120904_1021
CURRENT_DATE=`date +%Y%m%d_%H%M`

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

# The starting directory
INITIAL_PWD="$PWD"

# The working directory and the resulting archive file prefix
ARCHIVE_PREFIX="snowowl_$CURRENT_DATE"

# The absolute path to the above
ABSOLUTE_ARCHIVE_PREFIX="$INITIAL_PWD/$ARCHIVE_PREFIX"

# The base URL of the REST services to use
BASE_URL="http://localhost:8080/snowowl"

# The base URL for administrative services
ADMIN_BASE_URL="$BASE_URL/admin"

# Prints a message to stdout with the current date and time.
echo_date() {
	echo -e "[`date +\"%Y-%m-%d %H:%M:%S\"`] $@"
}

# Prints an error message to stderr and exits the script with a non-zero status.
error_exit() {
	echo -e "[`date +\"%Y-%m-%d %H:%M:%S\"`] $@" >&2
	exit 1
}

# Invokes curl to make an HTTP request to the server; stores returned message and HTTP status code in output variables.
rest_call() {
	CURL_OUTPUT=`curl -q --fail --silent --show-error --connect-timeout "$CONNECTION_TIMEOUT_SECONDS" --user "$SNOWOWL_USERNAME:$SNOWOWL_PASSWORD" --write-out "\n%{http_code}" "$@"`
	CURL_MESSAGE=`echo "$CURL_OUTPUT" | head -n-1`
	CURL_HTTP_STATUS=`echo "$CURL_OUTPUT" | tail -n1`
}

# Tries to acquire a global lock that prevents writing to any of the terminology stores on any available branch.
lock_all_repositories() {
	echo_date "Locking repositories..."
	rest_call --data-urlencode "timeoutMillis=$LOCK_TIMEOUT_MILLIS" "$ADMIN_BASE_URL/repositories/lock"
}

# Removes the lock from all repositories.
unlock_all_repositories() {
	echo_date "Unlocking repositories..."
	rest_call --data-urlencode "" "$ADMIN_BASE_URL/repositories/unlock"
}

# Cleans up the global repository lock and exits with an error.
unlock_all_repositories_and_exit() {
	unlock_all_repositories
	error_exit "$1"
}

# Locks the specified repository, preventing write access to all of its branches.
lock_repository() {
	echo_date "Locking repository $REPOSITORY..."
	rest_call --data-urlencode "" "$ADMIN_BASE_URL/repositories/$REPOSITORY/lock"
	
	if [ "$CURL_HTTP_STATUS" -ne "204" ]; then
		unlock_all_repositories_and_exit "Couldn't lock repository $REPOSITORY. Exiting with error."
	fi
}

# Unlocks the specified repository if it was already locked.
unlock_repository() {
	echo_date "Unlocking repository $REPOSITORY..."
	rest_call --data-urlencode "" "$ADMIN_BASE_URL/repositories/$REPOSITORY/unlock"
}

# Unlocks the specified repository, cleans up the global repository lock and exits with an error.
unlock_repository_and_exit() {
	unlock_repository
	unlock_all_repositories_and_exit "$1"
}

# Copies the contents of the specified repository to the zip file.
backup_repository() {
	lock_repository
	
	echo_date "Creating SQL dump from contents of repository $REPOSITORY..."
	DATABASE_DUMP_FILE="$REPOSITORY.sql"
	mysqldump --user="$MYSQL_USERNAME" --password="$MYSQL_PASSWORD" "$REPOSITORY" > "$ABSOLUTE_ARCHIVE_PREFIX/$DATABASE_DUMP_FILE" || unlock_repository_and_exit "Couldn't create SQL dump for repository $REPOSITORY. Exiting with error."

	echo_date "Saving index content for repository $REPOSITORY..."
	INDEX_NAME="${REPOSITORY%Store}"
	rsync --verbose --recursive --dirs --prune-empty-dirs --exclude=segments.gen --exclude=write.lock "$INDEX_NAME" "$ABSOLUTE_ARCHIVE_PREFIX/indexes" || unlock_repository_and_exit "Couldn't copy files from $REPOSITORY index folder. Exiting with error."
	
	unlock_repository
	
	echo_date "Done backing up repository $REPOSITORY."
}

# Retrieves the list of terminology repositories and backs them up one by one.
backup_repositories() {
	echo_date "Backing up installed terminology repositories..."
	rest_call "$ADMIN_BASE_URL/repositories"
	
	if [ "$CURL_HTTP_STATUS" -ne "200" ]; then
		unlock_all_repositories_and_exit "Failed to retrieve list of repositories. Exiting with error."
	fi
	
	echo "$CURL_MESSAGE" | while read -r REPOSITORY
	do
		backup_repository || break
	done
	
	# Check if the loop above was left via a break
	if [ $? -ne 0 ]; then exit 1; fi
	
	echo_date "Done backing up installed repositories."
}

# Sends a message to connected users.
send_message() {
	rest_call -H "Content-Type:text/plain" --data "$1" "$ADMIN_BASE_URL/messages/send"
}

# Checks input arguments and test whether the script is ready to be executed.
check_arguments() {
	if [ "x$SNOW_OWL_SERVER_HOME" = "x" ]; then
		error_exit "Please set the variable SNOW_OWL_SERVER_HOME before running this script. Exiting with error."
	fi

	if [ ! -d "$SNOW_OWL_SERVER_HOME/resources/indexes" ]; then
		error_exit "No index directory could be found for the installation under '$SNOW_OWL_SERVER_HOME'. Exiting with error."
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

	echo_date "Create backup destination directory '$ABSOLUTE_ARCHIVE_PREFIX'."
	
	# Creates both $ABSOLUTE_ARCHIVE_PREFIX and $ABSOLUTE_ARCHIVE_PREFIX/indexes 
	mkdir -pv "$ABSOLUTE_ARCHIVE_PREFIX/indexes" || error_exit "Couldn't create directory '$ABSOLUTE_ARCHIVE_PREFIX'. Exiting with error."
	
	echo_date "Starting backup; sending message to connected users."
	send_message "Write access to repositories will be disabled while the system creates a backup in $INITIAL_WAIT_MINUTES minutes."
	
	if [ "$CURL_HTTP_STATUS" = "000" ]; then
		error_exit "Couldn't send message to users; the server is not running. Exiting with error."
	fi
	
	echo_date "Waiting $INITIAL_WAIT_MINUTES minutes for users to finish..."
	sleep "$INITIAL_WAIT_MINUTES"m
	
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
	cd "$SNOW_OWL_SERVER_HOME/resources/indexes" || error_exit "Couldn't switch to index directory '$SNOW_OWL_SERVER_HOME/resources'. Exiting with error." 

	backup_repositories
	unlock_all_repositories
	
	echo_date "Notifying users that write access has been restored."
	send_message "Write access to repositories restored."	

	echo_date "Creating archive..."
	
	# Return to the initial directory
	cd "$INITIAL_PWD"
	zip --recurse-paths --move --test "$ARCHIVE_PREFIX.zip" "$ARCHIVE_PREFIX" || error_exit "Archive creation failed; the backup is incomplete. Exiting with error."
	
	echo_date "Finished successfully."
	exit 0
}

# Ensures that only a single instance is running at any time
LOCKFILE="/var/run/snowowl-backup/instance.lock"

(
        flock -n 200 || error_exit "Another backup script is already running. Exiting with error."
        trap "rm $LOCKFILE" EXIT
        main
) 200> $LOCKFILE