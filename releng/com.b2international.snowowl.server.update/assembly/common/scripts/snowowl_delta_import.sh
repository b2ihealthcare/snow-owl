#!/usr/bin/env bash
#
# Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
# Snow Owl terminology server import script
# See usage or execute the script with the -h flag to get further information.

#
# Parameters that must be configured before execution
#

# The username to authenticate through the REST API
SNOW_OWL_USER=""

# The password for the above user
SNOW_OWL_USER_PASSWORD=""

# The source folder with the exported RF2 content
SOURCE_FOLDER=""

#
# Global variables / constants, advanced parameters
#

# Base URL of the Snow Owl server
SNOW_OWL_BASE_URL="http://localhost:8080"

# URL for Snow Owl's REST API
SNOW_OWL_API_URL="/snowowl/snomed-ct/v3"

# Media type to use for REST requests
MEDIA_TYPE="application/vnd.com.b2international.snowowl+json"

# The url of the bugzilla repository, e.g.: http://localhost:9090/bugzilla
BUGZILLA_URL=""

#Login credentials to bugzilla
BUGZILLA_USERNAME=""
BUGZILLA_PASSWORD=""

usage() {

	cat <<EOF

NAME:

    Delta import script for the Snow Owl terminology server

    OPTIONS:

    -h
        Show this help
    -u
        Define a username with privileges to the Snow Owl REST API
    -p
        Define the password for the above user
    -b
        Snow Owl base URL, defaults to 'http://localhost:8080'
    -f
        Source folder where the exported content can be found
    -s
        Branch from Snow Owl to initiate the import on
    -a
        REST API URL of the Snow Owl server, defaults to '/snowowl/snomed-ct/v3'
	-z
		Bugzilla URL
	-U
		Defines the bugzilla user
	-P
		Defines the bugzilla pasword for the above user

NOTES:

    This script can be used to import content to a Snow Owl terminology server.

    Mandatory variables:
        - SNOW OWL user that is able to authenticate through the REST API
        - Password for the above user
        - Source folder with the delta export files
		- Bugzilla URL
		- Bugzilla user
		- Bugzilla password for the above user

EOF

}

echo_date() {
	echo -e "[$(date +"%Y-%m-%d %H:%M:%S")] $@"
}

check_if_empty() {
	if [[ -z "$1" ]]; then
		echo_date "$2"
		exit 1
	fi
}

validate_variables() {
	check_if_empty "${SNOW_OWL_USER}" "Snow Owl username must be specified"
	check_if_empty "${SNOW_OWL_USER_PASSWORD}" "User password must be specified"
	check_if_empty "${SOURCE_FOLDER}" "Source folder must be specified"
	SOURCE_FILE=$(ls -t snow_owl_DELTA_export* | head -1)
	check_if_empty "${SOURCE_FILE}" "Source folder must contain a valid delta export file"
	check_if_empty "${BUGZILLA_URL}" "Bugzilla url must be specified"
	check_if_empty "${BUGZILLA_USERNAME}" "Bugzilla username must be specified"
	check_if_empty "${BUGZILLA_PASSWORD}" "Bugzilla pasword must be specified"
}

bugzilla_login() {
	BUGZILLA_LOGIN_ENDPOINT="${BUGZILLA_URL}/rest/login?login=${BUGZILLA_USERNAME}&password=${BUGZILLA_PASSWORD}"
	BUGZILLA_TOKEN=$(curl --request GET \
		--include --silent --show-error \
		"${BUGZILLA_LOGIN_ENDPOINT}" | awk -F'"' '{print $6}')
}

bugzilla_lougout() {
	BUGZILLA_LOGOUT_ENDPOINT="${BUGZILLA_URL}/rest/logout?token=${BUGZILLA_TOKEN}"
	BUGZILLA_LOGOUT_ENDPOINT="$(echo -e "${BUGZILLA_LOGOUT_ENDPOINT}" | tr -d '[:space:]')"

	LOGOUT_RESPONSE=$(curl --request GET \
		--include --silent --show-error \
		"${BUGZILLA_LOGOUT_ENDPOINT}")
}

create_bug() {
	CREATE_BUG_ENDPOINT="${BUGZILLA_URL}/rest/bug?token=${BUGZILLA_TOKEN}"
	CREATE_BUG_ENDPOINT="$(echo -e "${CREATE_BUG_ENDPOINT}" | tr -d '[:space:]')"
	CREATE_BUG_POST_INPUT='{"product": "com.b2international.snowowl.terminology.snomed", "component": "SNOMEDCT", "version": "unspecified", "op_sys": "All", "platform": "All", "summary" : "Delta import task"}'
	BUG_ID=$(curl --request POST \
		--header "Content-type: application/json" \
		--data "${CREATE_BUG_POST_INPUT}" \
		--include --silent --show-error \
		"${CREATE_BUG_ENDPOINT}" | awk -F'{|}' '{print $2}')
	
	BUG_ID=${BUG_ID##*:}
	echo "Task with id: ${BUG_ID} created"
}

create_branch() {
	CREATE_BRANCH_POST_INPUT='{"parent": "MAIN", "name": "'"${BUG_ID}"'", "metadata": {}}'
	CREATE_BRANCH_ENDPOINT="${SNOW_OWL_BASE_URL}${SNOW_OWL_API_URL}/branches"

	BRANCH_CREATE_RESPONSE=$(curl --user "${SNOW_OWL_USER}:${SNOW_OWL_USER_PASSWORD}" \
		--request POST \
		--header "Content-type: ${MEDIA_TYPE}" \
		--data "${CREATE_BRANCH_POST_INPUT}" \
		--include --silent --show-error \
		"${CREATE_BRANCH_ENDPOINT}" | grep -Fi Location)
}

initiate_import() {
	IMPORT_BRANCH="MAIN/${BUG_ID}"
	IMPORTS_ENDPOINT="${SNOW_OWL_BASE_URL}${SNOW_OWL_API_URL}/imports"
	IMPORT_CONFIG_POST_INPUT='{"branchPath": "'"${IMPORT_BRANCH}"'", "codeSystemShortName": "SNOMEDCT", "createVersions": false, "type": "DELTA"}'
	echo_date "Initating delta import with config: "${IMPORT_CONFIG_POST_INPUT}" on target: "${IMPORTS_ENDPOINT}""

	RESPONSE=$(curl --user "${SNOW_OWL_USER}:${SNOW_OWL_USER_PASSWORD}" \
		--request POST \
		--header "Content-type: ${MEDIA_TYPE}" \
		--data "${IMPORT_CONFIG_POST_INPUT}" \
		--include --silent --show-error \
		"${IMPORTS_ENDPOINT}" | grep -Fi Location)

	ID=${RESPONSE##*/}
	IMPORT_UUID=${ID%$'\r'}
	echo "import uuid: ${IMPORT_UUID}"

	IMPORTS_ARCHIVE_POST_ENDPOINT="${SNOW_OWL_BASE_URL}${SNOW_OWL_API_URL}/imports/${IMPORT_UUID}/archive"
	RESPONSE=$(curl --user "${SNOW_OWL_USER}:${SNOW_OWL_USER_PASSWORD}" \
		--request POST \
		-F "file=@${SOURCE_FOLDER}" \
		--include --silent --show-error \
		"${IMPORTS_ARCHIVE_POST_ENDPOINT}" | grep -Fi Location)
}

execute() {
	validate_variables
	bugzilla_login
	create_bug
	create_branch
	initiate_import
	bugzilla_lougout
	exit 0
}

while getopts ":hu:p:f:b:a:z:U:P:" option; do
	case "${option}" in
	h)
		usage
		exit 0
		;;
	u)
		SNOW_OWL_USER=${OPTARG}
		;;
	p)
		SNOW_OWL_USER_PASSWORD=${OPTARG}
		;;
	f)
		SOURCE_FOLDER=${OPTARG}
		;;
	b)
		SNOW_OWL_BASE_URL=${OPTARG}
		;;
	a)
		SNOW_OWL_API_URL=${OPTARG}
		;;
	z)
		BUGZILLA_URL=${OPTARG}
		;;
	U)
		BUGZILLA_USERNAME=${OPTARG}
		;;
	P)
		BUGZILLA_PASSWORD=${OPTARG}
		;;
	\?)
		echo_date "Invalid option: $OPTARG." >&2
		usage
		exit 1
		;;
	:)
		echo_date "Option -$OPTARG requires an argument." >&2
		usage
		exit 1
		;;
	esac
done

execute
