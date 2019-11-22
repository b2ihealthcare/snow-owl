#!/usr/bin/env bash
set -euo pipefail
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

#List of base urls of the Snow Owl servers to import to
SNOW_OWL_BASE_URLS=()

# If set then a centralized Bugzilla instance will be used, otherwise falls back to the pattern: 'http://snowowl.server.url/bugzilla'
DEFAULT_BUGZILLA_URL=""

#Login credentials to bugzilla
BUGZILLA_USERNAME=""
BUGZILLA_PASSWORD=""

#
# Global variables / constants, advanced parameters
#

#Default Snow Owl base url to use if none are specified
DEFAULT_SNOW_OWL_BASE_URL="http://localhost:8080"

# URL for Snow Owl's REST API
SNOW_OWL_API_URL="/snowowl/snomed-ct/v3"

# Media type to use for REST requests
MEDIA_TYPE="application/vnd.com.b2international.snowowl+json"

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
    -f
        Path to folder where the exported content can be found
    -b
        Snow Owl base URLs (more than one can be defined), defaults to 'http://localhost:8080'
    -a
        REST API URL of the Snow Owl server, defaults to '/snowowl/snomed-ct/v3'
    -z
        Centralized Bugzilla URL, if set all requests will be made against that instance
    -U
        Defines the Bugzilla user
    -P
        Defines the Bugzilla password for the above user

NOTES:

    This script can be used to import content to a Snow Owl terminology server.

    Mandatory variables:
        - SNOW OWL user that is able to authenticate through the REST API
        - Password for the above user
        - Source folder with the delta export files
        - Bugzilla user
        - Bugzilla password for the above user

EXAMPLES:

    Run against a single Snow Owl server instance, Bugzilla is expected to be under 'http://snowowl.server.url/bugzilla':

        $0 -u snowowl-user -p snowowl-user-pass -f /path/to/export/archives -b http://snowowl.server.url -U bugzilla-user -P bugzilla-user-pass

    Run against multiple Snow Owl server instances, Bugzilla is expected to be under 'http://snowowl.server.url/bugzilla':

        $0 -u snowowl-user -p snowowl-user-pass -f /path/to/export/archives -b http://snowowl.server.url -b http://snowowl.server.url2 -U bugzilla-user -P bugzilla-user-pass

    Run against multiple Snow Owl server instances, the same centralized Bugzilla instance is used for each import:

        $0 -u snowowl-user -p snowowl-user-pass -f /path/to/export/archives -b http://snowowl.server.url -b http://snowowl.server.url2 -z http://bugzilla.url -U bugzilla-user -P bugzilla-user-pass

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

check_file_validity() {
	if [ -f "$1" ]; then
		echo "Valid export file, $1 found."
	else
		echo_date "$2"
		exit 1
	fi
}

validate_variables() {

	check_if_empty "${SNOW_OWL_USER}" "Snow Owl username must be specified"
	check_if_empty "${SNOW_OWL_USER_PASSWORD}" "User password must be specified"
	check_if_empty "${SOURCE_FOLDER}" "Source folder must be specified"
	SOURCE_FILE=$(ls ${SOURCE_FOLDER}/snow_owl_DELTA_export* -t | head -n1)
	check_if_empty "${SOURCE_FILE}" "Source folder must contain a valid delta export file"
	check_file_validity "${SOURCE_FILE}" "Source file must be a valid file"
	check_if_empty "${BUGZILLA_USERNAME}" "Bugzilla username must be specified"
	check_if_empty "${BUGZILLA_PASSWORD}" "Bugzilla password must be specified"

	if [ ! -z "${DEFAULT_BUGZILLA_URL}" ]; then
		BUGZILLA_URL="${DEFAULT_BUGZILLA_URL}"
	else
		BUGZILLA_URL="${SNOW_OWL_BASE_URL}/bugzilla"
	fi

}

bugzilla_login() {
	BUGZILLA_LOGIN_ENDPOINT="${BUGZILLA_URL}/rest/login?login=${BUGZILLA_USERNAME}&password=${BUGZILLA_PASSWORD}"
	BUGZILLA_TOKEN=$(curl --request GET \
		--silent --show-error --fail \
		"${BUGZILLA_LOGIN_ENDPOINT}" | awk -F'"' '{print $6}')
}

bugzilla_lougout() {
	BUGZILLA_LOGOUT_ENDPOINT="${BUGZILLA_URL}/rest/logout?token=${BUGZILLA_TOKEN}"

	LOGOUT_RESPONSE=$(curl --request GET \
		--include --silent --show-error --fail \
		"${BUGZILLA_LOGOUT_ENDPOINT}")
}

create_bug() {
	CREATE_BUG_ENDPOINT="${BUGZILLA_URL}/rest/bug?token=${BUGZILLA_TOKEN}"
	CREATE_BUG_POST_INPUT='{"product": "com.b2international.snowowl.terminology.snomed", "component": "SNOMEDCT", "version": "unspecified", "op_sys": "All", "platform": "All", "summary" : "Delta import task"}'
	BUG_ID=$(curl --request POST \
		--header "Content-type: application/json" \
		--data "${CREATE_BUG_POST_INPUT}" \
		--include --silent --show-error --fail \
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
		--include --silent --show-error --fail \
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
		--include --silent --show-error --fail \
		"${IMPORTS_ENDPOINT}" | grep -Fi Location)

	ID=${RESPONSE##*/}
	IMPORT_UUID=${ID%$'\r'}

	IMPORTS_ARCHIVE_POST_ENDPOINT="${SNOW_OWL_BASE_URL}${SNOW_OWL_API_URL}/imports/${IMPORT_UUID}/archive"
	RESPONSE=$(curl --user "${SNOW_OWL_USER}:${SNOW_OWL_USER_PASSWORD}" \
		--request POST \
		-F "file=@${SOURCE_FILE}" \
		--include --silent --show-error --fail \
		"${IMPORTS_ARCHIVE_POST_ENDPOINT}")
}

execute() {
	validate_variables
	bugzilla_login
	create_bug
	create_branch
	initiate_import
	bugzilla_lougout
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
		SNOW_OWL_BASE_URLS+=(${OPTARG})
		;;
	a)
		SNOW_OWL_API_URL=${OPTARG}
		;;
	z)
		DEFAULT_BUGZILLA_URL=${OPTARG}
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

if [ ${#SNOW_OWL_BASE_URLS[@]} -eq 0 ]; then
	SNOW_OWL_BASE_URLS=($DEFAULT_SNOW_OWL_BASE_URL)
fi

for SNOW_OWL_BASE_URL in "${SNOW_OWL_BASE_URLS[@]}"; do
	echo "Initiating Snow Owl import on $SNOW_OWL_BASE_URL."
	execute
done

exit 0
