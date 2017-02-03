#!/usr/bin/env bash

#
# Copyright 2016-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

#
# Snow Owl terminology server install script
# See usage or execute the script with the -h flag to get further information about it.
#
# Version: 1.0
#

# The following variables must be filled in before executing the script at the first time:

# A MySQL username with root privileges
MYSQL_USERNAME="root"

# The password for the root MySQL user
MYSQL_PASSWORD="password"

# The MySQL username for Snow Owl server to use
SNOWOWL_MYSQL_USER="snowowl"

# The password for Snow Owl's MySQL user
SNOWOWL_MYSQL_PASSWORD="snowowl"

# A valid Snow Owl user to be able to create backups
SNOWOWL_USERNAME="user@localhost.localdomain"

# The password for the Snow Owl user given above
SNOWOWL_PASSWORD="password123"

# The LDAP server's URL
LDAP_URL="ldap://<ldap_host>:10389/"

# The LDAP password
LDAP_PASSWORD="secret"

###########################################################

# Optional variables that are modifiable:

# Full path to a local hot backup script. It is not necessary to fill in if the provided archive
# contains the script.
HOT_BACKUP_SCRIPT_LOCATION=""

# Set maximum java heap size. If not specified the default (10g) will be used. Can be set by the
# -x parameter as well. Setting this value will always overwrite the value specified through the
# -x parameter.
MAX_JAVA_HEAP_SIZE=0

# The name of the folder where extra server files (documentation, scripts, config files) will be
# extracted during the install process. This folder will be in the server folder itself:
# <path_to_snow_owl_server>/<name_of_the_extra_files_folder>
EXTRA_SERVER_FILES_DIR_NAME="extras"

# Set to either file or ldap. If left empty the default server config will be used.
AUTH_TYPE=""

###########################################################

# Changing the following variables is NOT advised.

# The number of retries to wait for e.g. server shutdown or log file creation.
RETRIES=15

# The number of seconds to wait between retries.
RETRY_WAIT_SECONDS=1

# The anchor file in a server archive which is always in the root of the server folder. This is
# used for identifying the server folder inside an archive with subfolders.
SERVER_ANCHOR_FILE="snowowl_config.yml"

# The anchor file in a dataset/server archive which is always in the root of the dataset folder.
# This is used for identifying the dataset folder inside an archive with subfolders.
DATASET_ANCHOR_FILE="snomedStore.sql"

# List of known database names used by the Snow Owl terminology server.
DATABASES=( atcStore icd10Store icd10amStore icd10cmStore localterminologyStore \
 loincStore mappingsetStore sddStore snomedStore umlsStore valuesetStore )

# Flag for indicating the will to create a backup.
CREATE_BACKUP=false

# Flag for indicating that the dataset should be (re)loaded.
LOAD_DATASET=false

# Flag for indicating the will that the provided server should be started upon finish.
START_SERVER=false

# Flag for ignoring interactive prompts.
IGNORE_PROMPTS=false

# Flag for indicating the will to open up useful terminals after the server was started.
# (two terminals will open, one for the Virgo startup process and one for the server log)
OPEN_TERMINAL=false

# Variable to determine the local MySQL instance.
MYSQL=$(which mysql)

# Variable for the first archive passed in as a parameter.
FIRST_ARCHIVE=""
# Variable for the second archive passed in as a parameter.
SECOND_ARCHIVE=""

# Variable used for storing the server path inside the first archive.
SERVER_ARCHIVE_PATH=""
# Variable used for storing the dataset path inside the first or the second archive.
DATASET_ARCHIVE_PATH=""

# Variable used for storing the currently running Snow Owl server's path.
RUNNING_SERVER_PATH=""

# Variable to store the path of the newly installed server.
SERVER_PATH=""
# Variable to store the path of the newly installed dataset.
DATASET_PATH=""

# The working folder of the script. It could change to the containing folder of the 
# currently running Snow Owl server.
WORKING_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Temporary folder to extract the server archive. It is always cleaned up upon exiting
# the script.
TMP_SERVER_DIR=""

# Temporary folder to extract the dataset archive. It is always cleaned up upon exiting
# the script.
TMP_DATASET_DIR=""

# Enviromental variable used by Jenkins
export BUILD_ID=dontKillMe

usage() {

cat << EOF

NAME:

	Snow Owl terminology server install script

DESCRIPTION:

	This script can be used for deploying Snow Owl terminology server / dataset 
	in the following scenarios:
		- clean install of a server with empty dataset
		- clean install of a server with a provided dataset
		- upgrading to a newer server version without modifying the existing dataset
		- upgrading to a newer version of dataset without modifying the currently running server
		- updating MySQL content from a provided dataset.

NOTES:

	Mandatory variables must be filled in before executing the script. These are:
		- MySQL user with root privileges and it's password (to create the necessary
		  SQL user/databases/tables)
		- the desired MySQL user and password for the Snow Owl terminology server
		  (snowowl/snowowl by default).
		- LDAP host and password
		- an existing Snow Owl user name and password (to execute a backup)
	Optional variables:
		- path to a hot backup script if the provided server archive does not contain
		  any scripts. It is not necessary to provide one if the script is in the server
		  archive.

IMPORTANT:

	This script will NOT perform all necessary configuration steps required to start
	a Snow Owl server on a clean machine. It is advised to use when at least one
	successful Snow Owl server setup was performed before.

	The working folder of the script is determined by the following:
		- if there is no running Snow Owl server upon execution time, then the
		  containing folder of the script.
		- if there is a running Snow Owl server upon execution time, then the
		  containing folder of the running server.

USAGE: $0 [OPTIONS] [SERVER_ARCHIVE|DATASET_ARCHIVE|SERVER_AND_DATASET_ARCHIVE] [DATASET_ARCHIVE]

	[SERVER_ARCIVE]					an archive that contains only a Snow Owl terminology server

	[DATASET_ARCHIVE]				an archive that contains only a dataset for the server

	[SERVER_AND_DATASET_ARCHIVE]	an archive that contains both server and dataset files in 
									separate folders

OPTIONS:

	-b		(backup): if set a dataset backup will be performed for the currently running 
			Snow Owl server. The path to a local backup script must either be provided or
			the server archive must contain one.

	-l		(load): if set the script will try to (re)load the dataset. If no dataset was
			provided through the parameters, then an empty MySQL database structure will
			be created

	-s		(start): if set the script will either start the new server (if it was provided)
			or will restart the	previously running server instance (if it was running upon 
			execution time)

	-f		(force): if set interactive prompts will be ignored

	-t		(terminal): if set two terminals will open upon server start, one for the Virgo
			startup	process	and one for the server log. This can help monitor if the server
			startup	was successful

	-x <value>	sets the maximum java heap size to use by the new server instance. If not set
			and there was no running server upon execution then the default value (10g)
			will be used. If there was a running server upon execution then that server's
			heap settings will be used.

	-a <value>	configure the authentication type through setting the appropriate values in
			snowowl_config.yml and osgi_server.plan. The value can either be 'file' or 'ldap',
			no other values are allowed.

	-h		(help): displays this help

EXAMPLES:

	If a clean server install without a given dataset must be performed, the current database
	can safely be deleted, the server should start and terminals should open at the end:

		Make sure there is no running server in the background and then execute:

		$0 -lst <server_archive_without_dataset>.zip

	If a clean server install with a provided dataset must be performed, the current database
	can safely be deleted, no prompt should ask for confirmation and the server should start
	at the end:

		Make sure there is no running server in the background and then execute:

		$0 -lfs <server_archive_with_dataset>.zip

		OR

		$0 -lfs <server_archive_without_dataset>.zip <dataset_archive>.zip

	If a server version upgrade must be performed, a dataset backup must be created, the 
	current database must be kept, indexes must be moved to the new server folder and the 
	server should start at the end:

		Make sure there is a running server in the background and then execute:

		$0 -bls <server_archive_without_dataset>.zip

	If a dataset upgrade must be performed, a backup must be created (make sure that in this
	case the backup script's location must be specified in this script), the current server
	must be kept, indexes must be moved to the server folder and the server should restart at
	the end:

		Make sure there is a running server in the background and then execute:

		$0 -bls <dataset_archive>.zip

	If a server must be extracted to a folder, all configuration settings (e.g. LDAP host,
	MySQL user/pass) must be set but a dataset reload or server start is not required:

		If configurations of a previous server instance must be kept, then make sure it is
		running and then execute:

		$0 <server_archive>.zip

		If configurations specified in the script must be used, then make sure there is no
		running instance at execution time and then:

		$0 <server_archive>.zip		

EOF

}

echo_date() {
	echo -e "[`date +\"%Y-%m-%d %H:%M:%S\"`] $@"
}

echo_error() {
	echo_date "ERROR: $@" >&2
}

echo_step() {
	echo_date
	echo_date "#### $@ ####"
}

echo_exit() {
	echo_error $@
	exit 1
}

swap_value() {

	FILE_LOCATION=$1
	PATTERN=$2
	NEW_VALUE=$3

	OLD_VALUE=$(grep -Eo "$PATTERN" $FILE_LOCATION)

	sed -i 's,'"$OLD_VALUE"','"$NEW_VALUE"',' $FILE_LOCATION

}

check_if_exists() {

	if [ -z "$1" ]; then
		echo_exit "$2"	
	fi

}

check_variables() {

	check_if_exists "$MYSQL_USERNAME" "MySQL username must be specified"
	check_if_exists "$MYSQL_PASSWORD" "MySQL password must be specified"
	check_if_exists "$SNOWOWL_MYSQL_USER" "Snow Owl's MySQL user must be specified"
	check_if_exists "$SNOWOWL_MYSQL_PASSWORD" "Snow Owl's MySQL password must be specified"
	check_if_exists "$SNOWOWL_USERNAME" "A Snow Owl user must be specified"
	check_if_exists "$SNOWOWL_PASSWORD" "A Snow Owl user's password must be specified"
	check_if_exists "$LDAP_URL" "The LDAP server's URL must be specified"
	check_if_exists "$LDAP_PASSWORD" "The LDAP password must be specified"

	if [ ! -z "$AUTH_TYPE" ] && [ "$AUTH_TYPE" != "file" ] && [ "$AUTH_TYPE" != "ldap" ]; then
		echo_exit "Authentication type must be either 'file' or 'ldap'"
	fi

}

scan_archives() {
	
	echo_step "Inspecting archives"

	# if two archives were specified look for the server in the first and look for the dataset in the second file

	if [ ! -z "$FIRST_ARCHIVE" ] && [ ! -z "$SECOND_ARCHIVE" ]; then

		CONFIG_LOCATION=$(unzip -l $FIRST_ARCHIVE | grep $SERVER_ANCHOR_FILE | sed 's/ /\n/g' | tail -n1 | sed 's/ //g')
		
		if [ -z "$CONFIG_LOCATION" ]; then
			echo_exit "Unable to locate Snow Owl server within '"$FIRST_ARCHIVE"'"
		else
			SERVER_ARCHIVE_PATH=$(dirname "$CONFIG_LOCATION")
			if [ "$SERVER_ARCHIVE_PATH" = "." ]; then
				echo_date "Found Snow Owl server in the root of '"$FIRST_ARCHIVE"'"
			else
				echo_date "Found Snow Owl server within the provided archive: '$FIRST_ARCHIVE/$SERVER_ARCHIVE_PATH'"
			fi
		fi

		SNOMED_STORE_LOCATION=$(unzip -l $SECOND_ARCHIVE | grep $DATASET_ANCHOR_FILE | sed 's/ /\n/g' | tail -n1 | sed 's/ //g')
		
		if [ -z "$SNOMED_STORE_LOCATION" ]; then
			echo_exit "Unable to locate dataset within '"$SECOND_ARCHIVE"'."
		else
			DATASET_ARCHIVE_PATH=$(dirname "$SNOMED_STORE_LOCATION")
			if [ "$DATASET_ARCHIVE_PATH" = "." ]; then
				echo_date "Found dataset in the root of '"$SECOND_ARCHIVE"'"
			else
				echo_date "Found dataset within the provided archive: '$SECOND_ARCHIVE/$DATASET_ARCHIVE_PATH'"
			fi
		fi

	# if only one archive was specified look for the server and the dataset in the same file

	elif [ ! -z "$FIRST_ARCHIVE" ] && [ -z "$SECOND_ARCHIVE" ]; then
		
		CONFIG_LOCATION=$(unzip -l $FIRST_ARCHIVE | grep $SERVER_ANCHOR_FILE | sed 's/ /\n/g' | tail -n1 | sed 's/ //g')
		
		if [ ! -z "$CONFIG_LOCATION" ]; then
			SERVER_ARCHIVE_PATH=$(dirname "$CONFIG_LOCATION")
			if [ "$SERVER_ARCHIVE_PATH" = "." ]; then
				echo_date "Found Snow Owl server in the root of '"$FIRST_ARCHIVE"'"
			else
				echo_date "Found Snow Owl server within the provided archive: '$FIRST_ARCHIVE/$SERVER_ARCHIVE_PATH'"
			fi
		fi

		SNOMED_STORE_LOCATION=$(unzip -l $FIRST_ARCHIVE | grep $DATASET_ANCHOR_FILE | sed 's/ /\n/g' | tail -n1 | sed 's/ //g')
		
		if [ ! -z "$SNOMED_STORE_LOCATION" ]; then
			DATASET_ARCHIVE_PATH=$(dirname "$SNOMED_STORE_LOCATION")
			if [ "$DATASET_ARCHIVE_PATH" = "." ]; then
				echo_date "Found dataset in the root of '"$FIRST_ARCHIVE"'"
			else
				echo_date "Found dataset within the provided archive: '$FIRST_ARCHIVE/$DATASET_ARCHIVE_PATH'"
			fi
		fi

	fi

}

find_running_snowowl_servers() {

	echo_step "Searching for running server instances"

	RUNNING_SERVER_PATH=$(ps aux | grep virgo | sed 's/-D/\n/g' | grep osgi.install.area | sed 's/=/\n/g' | tail -n1 | sed 's/ //g')
	
	if [ ! -z "$RUNNING_SERVER_PATH" ]; then
		echo_date "Found running Snow Owl server instance @ '"$RUNNING_SERVER_PATH"'"
		WORKING_DIR=$(dirname "$RUNNING_SERVER_PATH")
	else
		echo_date "No running Snow Owl server found."
	fi

}

unzip_server() {
	
	if [ ! -z "$SERVER_ARCHIVE_PATH" ]; then

		TMP_SERVER_DIR=$(mktemp -d --tmpdir=$WORKING_DIR)
	
		unzip -q $FIRST_ARCHIVE -d $TMP_SERVER_DIR
	
		FOLDER_NAME=""

		if [ "$SERVER_ARCHIVE_PATH" = "." ]; then
			FILENAME=$(basename $FIRST_ARCHIVE)
			FOLDER_NAME=$(echo ${FILENAME%.*})
		else
			FOLDER_NAME=$(basename $SERVER_ARCHIVE_PATH)
		fi

		if [ ! -d "$WORKING_DIR/$FOLDER_NAME" ]; then
			mkdir "$WORKING_DIR/$FOLDER_NAME"
			SERVER_PATH="$WORKING_DIR/$FOLDER_NAME"
		else
			echo_date "Suffixing server dir name as '"$WORKING_DIR/$FOLDER_NAME"' already exists."
			CURRENT_DATE=$(date +%Y%m%d_%H%M%S)
			mkdir "$WORKING_DIR/"$FOLDER_NAME"_$CURRENT_DATE"
			SERVER_PATH="$WORKING_DIR/"$FOLDER_NAME"_$CURRENT_DATE"
		fi	

		if [ "$SERVER_ARCHIVE_PATH" = "." ]; then
			mv -t $SERVER_PATH "$TMP_SERVER_DIR/"*
		else
			mv -t $SERVER_PATH "$TMP_SERVER_DIR/$SERVER_ARCHIVE_PATH/"*
			rm -rf "$TMP_SERVER_DIR/$SERVER_ARCHIVE_PATH/"
		fi

		echo_date "Extracted server files to: '"$SERVER_PATH"'"
	
	fi

}

unzip_dataset() {
	
	if [ ! -z "$DATASET_ARCHIVE_PATH" ]; then

		if [ ! -z "$FIRST_ARCHIVE" ] && [ ! -z "$SECOND_ARCHIVE" ]; then

			TMP_DATASET_DIR=$(mktemp -d --tmpdir=$WORKING_DIR)
			
			unzip -q $SECOND_ARCHIVE -d $TMP_DATASET_DIR
			
			# $SERVER_PATH must exists at this point

			if [ ! -d "$SERVER_PATH/resources" ]; then
				mkdir "$SERVER_PATH/resources"
			fi	

			DATASET_PATH="$SERVER_PATH/resources"

			if [ "$DATASET_ARCHIVE_PATH" = "." ]; then
				mv -t $DATASET_PATH "$TMP_DATASET_DIR/"*
			else
				mv -t $DATASET_PATH "$TMP_DATASET_DIR/$DATASET_ARCHIVE_PATH/"*
				rm -rf "$TMP_DATASET_DIR/$DATASET_ARCHIVE_PATH/"
			fi

		elif [ ! -z "$FIRST_ARCHIVE" ] && [ -z "$SECOND_ARCHIVE" ]; then
		
			if [ ! -z "$SERVER_PATH" ]; then

				if [ ! -d "$SERVER_PATH/resources" ]; then
					mkdir "$SERVER_PATH/resources"
				fi
				
				DATASET_PATH="$SERVER_PATH/resources"

				if [ "$DATASET_ARCHIVE_PATH" != "." ]; then
					mv -t $DATASET_PATH "$TMP_SERVER_DIR/$DATASET_ARCHIVE_PATH/"*
					rm -rf "$TMP_SERVER_DIR/$DATASET_ARCHIVE_PATH/"
				fi

			else
				
				TMP_DATASET_DIR=$(mktemp -d --tmpdir=$WORKING_DIR)

				unzip -q $FIRST_ARCHIVE -d $TMP_DATASET_DIR

				FOLDER_NAME=""

				if [ "$DATASET_ARCHIVE_PATH" = "." ]; then
					FILENAME=$(basename $FIRST_ARCHIVE)
					FOLDER_NAME=$(echo ${FILENAME%.*})
				else
					FOLDER_NAME=$(basename $DATASET_ARCHIVE_PATH)
				fi

				if [ ! -d "$WORKING_DIR/$FOLDER_NAME" ]; then
					mkdir "$WORKING_DIR/$FOLDER_NAME"
					DATASET_PATH="$WORKING_DIR/$FOLDER_NAME"
				else
					echo_date "Suffixing dataset dir name as '"$WORKING_DIR/$FOLDER_NAME"' already exists."
					CURRENT_DATE=$(date +%Y%m%d_%H%M%S)
					mkdir "$WORKING_DIR/"$FOLDER_NAME"_$CURRENT_DATE"
					DATASET_PATH="$WORKING_DIR/"$FOLDER_NAME"_$CURRENT_DATE"
				fi	

				if [ "$DATASET_ARCHIVE_PATH" = "." ]; then
					mv -t $DATASET_PATH "$TMP_DATASET_DIR/"*
				else
					mv -t $DATASET_PATH "$TMP_DATASET_DIR/$DATASET_ARCHIVE_PATH/"*
					rm -rf "$TMP_DATASET_DIR/$DATASET_ARCHIVE_PATH/"
				fi
	
			fi

		fi

		echo_date "Extracted dataset files to: '"$DATASET_PATH"'"
	
	fi
}

unzip_archives() {
	
	echo_step "Extracting archives"

	unzip_server
	unzip_dataset

	if [ ! -z $TMP_SERVER_DIR ] && [ -n "$(ls -A $TMP_SERVER_DIR)" ]; then
		mkdir "$SERVER_PATH/$EXTRA_SERVER_FILES_DIR_NAME"
		mv -t "$SERVER_PATH/$EXTRA_SERVER_FILES_DIR_NAME" "$TMP_SERVER_DIR/"*
		echo_date "Extracted additional server files to '$SERVER_PATH/$EXTRA_SERVER_FILES_DIR_NAME"
	fi

}

configure_backup_script() {
	
	SCRIPT_LOCATION=$1

	echo_date "Configuring backup script..."

	swap_value $SCRIPT_LOCATION "SNOW_OWL_SERVER_HOME=\"[^ ]*\"" "SNOW_OWL_SERVER_HOME=\"$RUNNING_SERVER_PATH\"" && echo_date "Using server location '$RUNNING_SERVER_PATH'"
	
	swap_value $SCRIPT_LOCATION "SNOWOWL_USERNAME=\"[^ ]*\"" "SNOWOWL_USERNAME=\"$SNOWOWL_USERNAME\"" && echo_date "Using Snow Owl user name '$SNOWOWL_USERNAME'"
	
	swap_value $SCRIPT_LOCATION "SNOWOWL_PASSWORD=\"[^ ]*\"" "SNOWOWL_PASSWORD=\"$SNOWOWL_PASSWORD\"" && echo_date "Using Snow Owl user password '$SNOWOWL_PASSWORD'"

	swap_value $SCRIPT_LOCATION "MYSQL_USERNAME=\"[^ ]*\"" "MYSQL_USERNAME=\"$SNOWOWL_MYSQL_USER\"" && echo_date "Using MySQL user '$SNOWOWL_MYSQL_USER'"
	
	swap_value $SCRIPT_LOCATION "MYSQL_PASSWORD=\"[^ ]*\"" "MYSQL_PASSWORD=\"$SNOWOWL_MYSQL_PASSWORD\"" && echo_date "Using MySQL password '$SNOWOWL_MYSQL_PASSWORD'"

	swap_value $SCRIPT_LOCATION "INITIAL_WAIT_MINUTES=5" "INITIAL_WAIT_MINUTES=0" && echo_date "Using immediate backup execution"

}

backup_and_shutdown() {
	
	if [ "$CREATE_BACKUP" = true ]; then
	
		if [ ! -z "$RUNNING_SERVER_PATH" ]; then

			echo_step "Creating backup"
			
			if [ -z "$HOT_BACKUP_SCRIPT_LOCATION" ]; then
				
				if [ ! -z "$SERVER_PATH" ]; then
					HOT_BACKUP_SCRIPT_LOCATION=$(find $SERVER_PATH -type f -name '*hot_backup*.sh')
				fi

				if [ -z "$HOT_BACKUP_SCRIPT_LOCATION" ]; then
					echo_exit "Unable to locate backup script, see script parameters or check if your server archive contains the script."
				else
					echo_date "Using backup script (found in the server archive) @ '$HOT_BACKUP_SCRIPT_LOCATION'"
					chmod +x $HOT_BACKUP_SCRIPT_LOCATION
				fi

			else
				echo_date "Using backup script @ '$HOT_BACKUP_SCRIPT_LOCATION'"
			fi

			if [ ! -z "$HOT_BACKUP_SCRIPT_LOCATION" ]; then

				configure_backup_script $HOT_BACKUP_SCRIPT_LOCATION
			
				echo_date "Executing backup script..."

				$HOT_BACKUP_SCRIPT_LOCATION && echo_date "Backup created successfully." || echo_error "Backup script failed to finish."
				wait
			
			fi

		else
			echo_error "Unable to create backup as there is no running Snow Owl server."
		fi

	fi

	if [ ! -z "$RUNNING_SERVER_PATH" ]; then

		echo_step "Shutdown"

		if [ "$IGNORE_PROMPTS" = false ]; then
			
			read -p "[`date +\"%Y-%m-%d %H:%M:%S\"`] The currently running Snow Owl server must be shut down. Are you sure you want to continue? (y or n) " -n 1 -r
			echo
			if [[ $REPLY =~ ^[Yy]$ ]]; then
				echo_date "Shutting down server @ '$RUNNING_SERVER_PATH'"
			else
				echo_exit "Server shutdown was interrupted by the user."
			fi

		else
			echo_date "Shutting down server @ '$RUNNING_SERVER_PATH'"
		fi

		"$RUNNING_SERVER_PATH/bin/shutdown.sh" > /dev/null
	
		SERVER_IS_DOWN=false

		for i in $(seq 1 "$RETRIES"); do
			
			SERVER_TO_SHUTDOWN=$(ps aux | grep virgo | sed 's/-D/\n/g' | grep osgi.install.area | sed 's/=/\n/g' | tail -n1 | sed 's/ //g')

			if [ ! -z "$SERVER_TO_SHUTDOWN" ]; then
				sleep "$RETRY_WAIT_SECONDS"s
			else
				echo_date "Shutdown finished."
				SERVER_IS_DOWN=true
				break
			fi

		done

		if [ "$SERVER_IS_DOWN" = false ]; then
			echo_exit "Unable to shutdown server @ '$RUNNING_SERVER_PATH' after $(( $RETRIES * $RETRY_WAIT_SECONDS )) seconds"
		fi

	fi

}

configure_ldap_host() {

	JAAS_CONFIG_LOCATION=$(find $SERVER_PATH -type f ! -path '*.jar*' -name '*jaas_config*.conf')
	
	# set LDAP url

	OLD_VALUE=$(grep -Eo 'ldap://[^ ]+/' $JAAS_CONFIG_LOCATION)

	sed -i 's,'"$OLD_VALUE"','"$LDAP_URL"',' $JAAS_CONFIG_LOCATION

	echo_date "Setting LDAP host to '$LDAP_URL'"
	
	# set LDAP password

	if [ "$LDAP_PASSWORD" != "secret" ]; then

		OLD_VALUE=$(grep -Eo 'bindDnPassword="[^ ]+"' $JAAS_CONFIG_LOCATION)

		NEW_VALUE="bindDnPassword=\"$LDAP_PASSWORD\""

		sed -i 's,'"$OLD_VALUE"','"$NEW_VALUE"',' $JAAS_CONFIG_LOCATION

		echo_date "Setting LDAP password to '$LDAP_PASSWORD'"

	fi

}

configure_mysql_user() {

	SNOWOWL_CONFIG_LOCATION=$(find $SERVER_PATH -type f -name '*config.yml')

	# set Snow Owl MySQL user

	if [ "$SNOWOWL_MYSQL_USER" != "snowowl" ]; then

		OLD_VALUE=$(grep -Eo 'username: [^ ]+' $SNOWOWL_CONFIG_LOCATION)

		NEW_VALUE="username: $SNOWOWL_MYSQL_USER"

		sed -i 's,'"$OLD_VALUE"','"$NEW_VALUE"',' $SNOWOWL_CONFIG_LOCATION

		echo_date "Setting Snow Owl's MySQL user to '$SNOWOWL_MYSQL_USER'"

	fi

	# set Snow Owl MySQL password

	if [ "$SNOWOWL_MYSQL_PASSWORD" != "snowowl" ]; then

		OLD_VALUE=$(grep -Eo 'password: [^ ]+' $SNOWOWL_CONFIG_LOCATION)

		NEW_VALUE="password: $SNOWOWL_MYSQL_PASSWORD"

		sed -i 's,'"$OLD_VALUE"','"$NEW_VALUE"',' $SNOWOWL_CONFIG_LOCATION

		echo_date "Setting Snow Owl's MySQL password to '$SNOWOWL_MYSQL_PASSWORD'"

	fi

}

configure_max_java_heap_size() {

	DMK_LOCATION=$(find $SERVER_PATH -type f -name 'dmk.sh')

	if [ $MAX_JAVA_HEAP_SIZE -ne 0 ]; then

		sed -i -e "s/-Xmx10g/-Xmx"$MAX_JAVA_HEAP_SIZE"g/g" $DMK_LOCATION

		echo_date "Setting max java heap size to '$MAX_JAVA_HEAP_SIZE g'"

	elif [ ! -z "$RUNNING_SERVER_PATH" ]; then
	
		OLD_DMK_LOCATION=$(find $RUNNING_SERVER_PATH -type f -name 'dmk.sh')
		
		OLD_VALUE=$(grep -Eo '\-Xmx[^ ]+g' $OLD_DMK_LOCATION)
		OLD_VALUE=${OLD_VALUE#-Xmx}
		OLD_VALUE=${OLD_VALUE%g}

		sed -i -e "s/-Xmx10g/-Xmx"$OLD_VALUE"g/g" $DMK_LOCATION

		echo_date "Reusing previously configured max heap size '$OLD_VALUE g'"

	fi

}

configure_authentication_type() {

	SNOWOWL_CONFIG_LOCATION=$(find $SERVER_PATH -type f -name '*config.yml')

	if [ "$AUTH_TYPE" = "file" ]; then

		OLD_VALUE=$(grep -Eo 'type: LDAP' $SNOWOWL_CONFIG_LOCATION)

		if [ ! -z "$OLD_VALUE" ]; then

			NEW_VALUE="type: PROP_FILE"

			sed -i 's,'"$OLD_VALUE"','"$NEW_VALUE"',' $SNOWOWL_CONFIG_LOCATION

			echo_date "Setting authentication type to PROP_FILE in snowowl_config.yml"

		fi

	elif [ "$AUTH_TYPE" = "ldap" ]; then

		OLD_VALUE=$(grep -Eo 'type: PROP_FILE' $SNOWOWL_CONFIG_LOCATION)

		if [ ! -z "$OLD_VALUE" ]; then

			NEW_VALUE="type: LDAP"

			sed -i 's,'"$OLD_VALUE"','"$NEW_VALUE"',' $SNOWOWL_CONFIG_LOCATION

			echo_date "Setting authentication type to LDAP in snowowl_config.yml"

		fi

	fi

	PLAN_LOCATION=$(find "$SERVER_PATH/pickup" -type f -name 'osgi_server.plan')

	for i in $(grep -Eo "authentication.[^\" ]+" $PLAN_LOCATION); do

		if [ "$AUTH_TYPE" = "file" ]; then
			sed -i 's,'"${i}"','"authentication.file"',' $PLAN_LOCATION
		elif [ "$AUTH_TYPE" = "ldap" ]; then
			sed -i 's,'"${i}"','"authentication.ldap"',' $PLAN_LOCATION
		fi

	done

	if [ "$AUTH_TYPE" = "file" ]; then
		echo_date "Setting authentication type to file in osgi_server.plan"
	elif [ "$AUTH_TYPE" = "ldap" ]; then
		echo_date "Setting authentication type to LDAP osgi_server.plan"
	fi

}

set_server_variables() {

	if [ ! -z $SERVER_PATH ]; then

		echo_step "Configuring server variables"
	
		configure_ldap_host
		configure_mysql_user
		configure_max_java_heap_size

		if [ ! -z "$AUTH_TYPE" ]; then
		
			configure_authentication_type

		fi

	fi

}

execute_mysql_statement() {
	${MYSQL} --user=${MYSQL_USERNAME} --password=${MYSQL_PASSWORD} --execute="$1" > /dev/null 2>&1 && echo_date "$2"
}

setup_mysql_content() {

	echo_date "Setting up MySQL content..."
	
	if [ "$IGNORE_PROMPTS" = false ] && [ "$CREATE_BACKUP" = false ]; then
		
		read -p "[`date +\"%Y-%m-%d %H:%M:%S\"`] Dataset backup was not performed, all MySQL content will be gone. Are you sure you want to continue? (y or n) " -n 1 -r
		echo
		if [[ $REPLY =~ ^[Yy]$ ]]; then
			echo_date "Continuing dataset reload procedure."
		else
			echo_exit "Dataset reload was interrupted by the user."
		fi

	fi

	SNOWOWL_USER_EXISTS=false

	while read User; do
		if [[ "$SNOWOWL_MYSQL_USER" == "$User" ]]; then
			SNOWOWL_USER_EXISTS=true
			break
		fi
	done < <(${MYSQL} --user=${MYSQL_USERNAME} --password=${MYSQL_PASSWORD} \
		--batch --skip-column-names --execute='use mysql; SELECT `user` FROM `user`;' > /dev/null 2>&1)

	if [ "$SNOWOWL_USER_EXISTS" = false ]; then
		execute_mysql_statement "CREATE USER '${SNOWOWL_MYSQL_USER}'@'localhost' identified by '${SNOWOWL_MYSQL_PASSWORD}';" \
			"Created '${SNOWOWL_MYSQL_USER}' MySQL user with password '${SNOWOWL_MYSQL_PASSWORD}'."
	fi

	for i in "${DATABASES[@]}";	do
		execute_mysql_statement "DROP DATABASE \`${i}\`;" "Dropped database ${i}."
	done

	if [ -z "$DATASET_PATH" ]; then

		for i in "${DATABASES[@]}"; do

			DATABASE_NAME=${i}
		
			execute_mysql_statement "CREATE DATABASE \`${DATABASE_NAME}\` DEFAULT CHARSET 'utf8';" "Created database ${DATABASE_NAME}."
			execute_mysql_statement "GRANT ALL PRIVILEGES ON \`${DATABASE_NAME}\`.* to '${SNOWOWL_MYSQL_USER}'@'localhost';" \
				"Granted all privileges on ${DATABASE_NAME} to '${SNOWOWL_MYSQL_USER}@localhost'."
		
		done

	else

		for i in $(find "$DATASET_PATH" -type f -name '*.sql'); do

			BASENAME=$(basename ${i})
			DATABASE_NAME=${BASENAME%.sql}
		
			execute_mysql_statement "CREATE DATABASE \`${DATABASE_NAME}\` DEFAULT CHARSET 'utf8';" "Created database ${DATABASE_NAME}."
			execute_mysql_statement "GRANT ALL PRIVILEGES ON \`${DATABASE_NAME}\`.* to '${SNOWOWL_MYSQL_USER}'@'localhost';" \
				"Granted all privileges on ${DATABASE_NAME} to '${SNOWOWL_MYSQL_USER}@localhost'."
		
			echo_date "Loading ${BASENAME}..."
			${MYSQL} --user=${MYSQL_USERNAME} --password=${MYSQL_PASSWORD} "${DATABASE_NAME}" < "${i}" > /dev/null 2>&1 && \
				echo_date "Loading of ${BASENAME} finished."

		done

	fi

	execute_mysql_statement "FLUSH PRIVILEGES;" "Reloaded grant tables."

}

setup_dataset() {

	if [ "$LOAD_DATASET" = true ]; then

		echo_step "Preparing dataset"

		if [ ! -z "$FIRST_ARCHIVE" ] && [ ! -z "$SECOND_ARCHIVE" ]; then

			setup_mysql_content

		elif [ ! -z "$FIRST_ARCHIVE" ] && [ -z "$SECOND_ARCHIVE" ]; then

			if [ ! -z "$DATASET_PATH" ] && [ ! -z "$SERVER_PATH" ]; then

				setup_mysql_content

			elif [ -z "$DATASET_PATH" ] && [ ! -z "$SERVER_PATH" ]; then

				if [ ! -z "$RUNNING_SERVER_PATH" ]; then
			
					mv -t "$SERVER_PATH/resources" "$RUNNING_SERVER_PATH/resources/indexes" && echo_date "Moved index folder from '$RUNNING_SERVER_PATH/resources' to '$SERVER_PATH/resources'."
			
				else

					setup_mysql_content

				fi

			elif [ ! -z "$DATASET_PATH" ] && [ -z "$SERVER_PATH" ]; then

				if [ ! -z "$RUNNING_SERVER_PATH" ]; then
			
					setup_mysql_content		

					rm -rf "$RUNNING_SERVER_PATH/resources/indexes" && echo_date "Cleaned up old index folder @ '$RUNNING_SERVER_PATH/resources'."
					mv -t "$RUNNING_SERVER_PATH/resources" "$DATASET_PATH/indexes" && echo_date "Moved '$DATASET_PATH/indexes' folder to '$RUNNING_SERVER_PATH/resources'."

				else

					setup_mysql_content

					echo_date "Index folder to copy under '<snowowl_server>/resources' is available @ '$DATASET_PATH'"

				fi

			fi

		fi

	fi

}

open_terminal_for_startup() {
	
	gnome-terminal --working-directory="$1/bin" --title="$1/bin" -x bash -c "screen -r $(basename $SERVER_PATH); exec bash;" &

}

open_terminal_for_log() {

	LOG_FILE_EXISTS=false

	for i in $(seq 1 "$RETRIES"); do
		if [ ! -f "$1/serviceability/logs/log.log" ]; then
			sleep "$RETRY_WAIT_SECONDS"s
		else
			LOG_FILE_EXISTS=true
			break
		fi
	done

	if [ "$LOG_FILE_EXISTS" = true ]; then
		gnome-terminal --working-directory="$1/serviceability/logs" --title="$1/serviceability/logs/log.log" -x bash -c 'tail --follow=name log.log; exec bash;' &
	fi

}

open_terminals() {

	if [ "$OPEN_TERMINAL" = true ]; then
		open_terminal_for_startup $1
		open_terminal_for_log $1
	fi

}

verify_server_startup() {

	SERVER_IS_UP=false

	for i in $(seq 1 "$RETRIES"); do
		
		SERVER_TO_START=$(ps aux | grep virgo | sed 's/-D/\n/g' | grep osgi.install.area | sed 's/=/\n/g' | tail -n1 | sed 's/ //g')

		if [ -z "$SERVER_TO_START" ]; then
			sleep "$RETRY_WAIT_SECONDS"s
		else
			echo_date "Server started @ '$SERVER_TO_START'"
			SERVER_IS_UP=true
			break
		fi

	done

	if [ "$SERVER_IS_UP" = false ]; then
		echo_exit "Unable to start server @ '$1' after $(( $RETRIES * $RETRY_WAIT_SECONDS )) seconds"
	fi

}

start_server() {

	if [ ! -z "$SERVER_PATH" ] || [ ! -z "$RUNNING_SERVER_PATH" ]; then
	
		if [ "$START_SERVER" = true ]; then

			echo_step "Starting server"

			if [ ! -z "$DATASET_PATH" ]; then
		
				if [ ! -z "$SERVER_PATH" ]; then

					chmod +x $SERVER_PATH/bin/*.sh
			
					screen -d -m -S "$(basename $SERVER_PATH)" -t "$SERVER_PATH" "$SERVER_PATH/bin/startup.sh"
				
					open_terminals $SERVER_PATH
		
					verify_server_startup $SERVER_PATH

				elif [ ! -z "$RUNNING_SERVER_PATH" ]; then

					screen -d -m -S "$(basename $RUNNING_SERVER_PATH)" -t "$RUNNING_SERVER_PATH" "$RUNNING_SERVER_PATH/bin/startup.sh"

					open_terminals $RUNNING_SERVER_PATH

					verify_server_startup $RUNNING_SERVER_PATH

				fi

			elif [ ! -z "$SERVER_PATH" ]; then
		
				screen -d -m -S "$(basename $SERVER_PATH)" -t "$SERVER_PATH" "$SERVER_PATH/bin/startup.sh"

				open_terminals $SERVER_PATH

				verify_server_startup $SERVER_PATH

			fi

		fi

	fi

}

cleanup() {

	if [ -d "$TMP_SERVER_DIR" ] || [ -d "$TMP_DATASET_DIR" ]; then

		echo_step "Clean up"

		if [ -d "$TMP_SERVER_DIR" ]; then
			rm -rf $TMP_SERVER_DIR && echo_date "Deleted temporary server dir @ '$TMP_SERVER_DIR'"
		fi

		if [ -d "$TMP_DATASET_DIR" ]; then
			rm -rf $TMP_DATASET_DIR && echo_date "Deleted temporary dataset dir @ '$TMP_DATASET_DIR'"
		fi

	fi

}

main() {
	
	echo_date "################################"
	echo_date "Snow Owl install script STARTED."
	
	check_variables

	scan_archives

	find_running_snowowl_servers

	unzip_archives

 	backup_and_shutdown

	set_server_variables

	setup_dataset

	start_server
	
	echo_date
	echo_date "Snow Owl install script FINISHED."

	exit 0

}

trap cleanup EXIT

while getopts ":hblsftx:a:" opt; do
	case "$opt" in
		h) 
			usage
			exit 0
			;;
		b)
			CREATE_BACKUP=true
			;;
		l)
			LOAD_DATASET=true
			;;
		s)
			START_SERVER=true
			;;
		f)
			IGNORE_PROMPTS=true
			;;
		t)
			OPEN_TERMINAL=true
			;;
		x)
			MAX_JAVA_HEAP_SIZE=$OPTARG
			;;
		a)
			AUTH_TYPE=$OPTARG
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

shift "$(( OPTIND - 1 ))"

if [ ! -z "$1" ] && [ ! -z "$2" ]; then

	FIRST_ARCHIVE=$1
	SECOND_ARCHIVE=$2

elif [ ! -z "$1" ] && [ -z "$2" ]; then

	FIRST_ARCHIVE=$1

elif [ -z "$1" ] && [ -z "$2"]; then

	echo_error "At least one parameter must be provided."
	usage
	exit 1

fi

if [ ! -z "$3" ]; then
	
	echo_error "More than two parameters are not allowed."
	usage
	exit 1

fi

main