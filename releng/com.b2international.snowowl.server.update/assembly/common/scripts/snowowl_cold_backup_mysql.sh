#!/usr/bin/env bash

# This sample backup script for Snow Owl server creates a .zip file in the 
# current directory with the saved contents of the MySQL databases and semantic
# indexes for each terminology, as well as supporting index content.
#
# The server should be stopped before running the script.
#
# The following variables should be set by editing this script before running it:

# The location of the Snow Owl Server installation, eg. /opt/snowowl (no trailing slash)
SNOW_OWL_SERVER_HOME=""

# The MySQL username used for creating the database dump
MYSQL_USERNAME="snowowl"

# The password for the user given above
MYSQL_PASSWORD="snowowl"

# The timestamp suffix for the top-level directory, eg. 20120904_1021
CURRENT_DATE=`date +%Y%m%d_%H%M`

# The starting directory
INITIAL_PWD="$PWD"

# The working directory and the resulting archive file prefix
ARCHIVE_PREFIX="snowowl_$CURRENT_DATE"

# The absolute path to the above
ABSOLUTE_ARCHIVE_PREFIX="$INITIAL_PWD/$ARCHIVE_PREFIX"

# The list of known terminology stores to preserve
REPOSITORIES=( snomedStore )

# Prints a message to stdout with the current date and time.
echo_date() {
	echo -e "[`date +\"%Y-%m-%d %H:%M:%S\"`] $@"
}

# Prints an error message to stderr and exits the script with a non-zero status.
error_exit() {
	echo -e "[`date +\"%Y-%m-%d %H:%M:%S\"`] $@" >&2
	exit 1
}

# Checks input arguments and test whether the script is ready to be executed.
check_arguments() {
	if [ "x$SNOW_OWL_SERVER_HOME" = "x" ]; then
		error_exit "Please set the variable SNOW_OWL_SERVER_HOME before running this script. Exiting with error."
	fi

	if [ "x$MYSQL_USERNAME" = "x" ]; then
		error_exit "Please set the variable MYSQL_USERNAME before running this script. Exiting with error."
	fi
	
	if [ "x$MYSQL_PASSWORD" = "x" ]; then
		error_exit "Please set the variable MYSQL_PASSWORD before running this script. Exiting with error."
	fi
	
	if [ ! -d "$SNOW_OWL_SERVER_HOME/resources/indexes" ]; then
		error_exit "No index directory could be found for the installation under '$SNOW_OWL_SERVER_HOME'. Exiting with error."
	fi
}

# Saves all index content to the destination archive.
backup_indexes() {
	echo_date "Saving index content..."

	# Need to switch directories because of relative paths within the zip
	cd "$SNOW_OWL_SERVER_HOME/resources" || error_exit "Couldn't switch to index directory '$SNOW_OWL_SERVER_HOME/resources'. Exiting with error." 
	rsync --verbose --recursive --dirs --exclude=segments.gen --exclude=write.lock "indexes" "$ABSOLUTE_ARCHIVE_PREFIX" || error_exit "Couldn't copy files from 'indexes'. Exiting with error."
	
	echo_date "Done saving index content."
}

# Saves content from a single repository.
backup_repository() {
	cd "$INITIAL_PWD"
	DATABASE_DUMP_FILE="$REPOSITORY.sql"
	
	echo_date "Creating SQL dump from contents of repository $REPOSITORY to $DATABASE_DUMP_FILE..."
	mysqldump --user="$MYSQL_USERNAME" --password="$MYSQL_PASSWORD" "$REPOSITORY" > "$ABSOLUTE_ARCHIVE_PREFIX/$DATABASE_DUMP_FILE" || error_exit "Couldn't create SQL dump for repository $REPOSITORY. Exiting with error."
}

# Saves all terminology content in database dumps and moves them to the destination archive.
backup_repositories() {
	echo_date "Backing up installed terminology repositories..."
	
	for REPOSITORY in "${REPOSITORIES[@]}"; do
		backup_repository || break
	done
	
	# Check if the loop above was left via a break
	if [ $? -ne 0 ]; then exit 1; fi
	
	echo_date "Done backing up installed repositories."
}

# Main script starts here.
main() {
	echo_date "----------------------------"
	check_arguments
	
	echo_date "Creating backup destination directory '$ABSOLUTE_ARCHIVE_PREFIX'."
	mkdir -pv "$ABSOLUTE_ARCHIVE_PREFIX" || error_exit "Couldn't create directory '$ABSOLUTE_ARCHIVE_PREFIX'. Exiting with error."

	backup_repositories
	backup_indexes
	
	echo_date "Creating archive..."
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