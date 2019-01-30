#!/usr/bin/env bash
#
# Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
# Snow Owl terminology server deploy script
# See usage or execute the script with the -h flag to get further information.
#

# The user for the user authentication for the REST API.
SNOW_OWL_USER=""

# The password for the above user.
SNOW_OWL_PASSWORD=""

# The export type to use for the export config.
EXPORT_TYPE="DELTA"

# Forces the export config to export concepts and relationships only.
IS_CONCEPT_AND_RELATIONSHIPS_ONLY=${false}

# The target folder to save the exported snapshot.
TARGET_FOLDER=""

# The server where the export should be initiated on
TARGET_ENVIRONMENT=""

# UUID of the snapshot export.
EXPORT_UUID=""

# Media type to use for the export configuration.
MEDIA_TYPE="application/vnd.com.b2international.snowowl+json"

# Accept header for archive download.
ACCEPT_HEADER="application/octet-stream"

# The address where the export config endpoint can be found.
EXPORT_CONFIG_POST_ENDPOINT="snowowl/snomed-ct/v3/exports"

# The address where the branch get endpoint can be found.
BRANCH_GET_ENDPOINT="snowowl/snomed-ct/v3/branches"

# The input data for the export config.
EXPORT_CONFIG_POST_INPUT=""

# The uuid of the export request given by Snow Owl.
EXPORT_UUID=""

# The input data for the code system version create config.
CODE_SYSTEM_VERSION_CREATE_POST_INPUT=""

# The initial name of the export.
EXPORT_FILE_NAME="snow_owl_delta_export"

# The renamed version of the export file.
RENAMED_EXPORT_FILE="snow_owl_delta_export"

# Calculates the current location of this specific bash script (works until the path is not a symlink)
SCRIPT_LOCATION="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

usage() {

	cat <<EOF
NAME:
	Automated delta export script. The export file will be saved where the script is located. By defining a target envrionemnt this script doesn't need to be present on the server.
OPTIONS:
	-h
		Show this help
	-u
		Define a username with privileges to the Snow Owl REST API
	-p
		Define the password for the above user
    -t
        Target environment which server the export should be initiated on
NOTES:
	This script can be used to initiate a delta export job that will run an export from a snow owl server every day at 20:00 server time and it will save it to the folder the script is within.
	Mandatory variables:
		- SNOW OWL user to use for the snapshot export
	    - SNOW OWL password for the above user
        - The target enviroment where the Snow Owl server runs
EOF

}

validate_variables() {
    check_if_empty "${SNOW_OWL_USER}" "Snow Owl username must be specified"
	check_if_empty "${SNOW_OWL_PASSWORD}" "Snow Owl password must be specified"
	check_if_empty "${TARGET_FOLDER}" "Target folder must be specified"
    check_if_empty "${TARGET_ENVIRONMENT}" "Target environment must be specified"

    if [[ ! -d "${TARGET_FOLDER}" ]]; then
        echo "Creating target folder"
        mkdir "${TARGET_FOLDER}"
    fi

}

check_if_empty() {
	if [[ -z "$1" ]]; then
		echo "$2"
		exit 1
	fi
}

export_delta() {
    echo "Creating snapshot export config"
    EXPORT_CONFIG_POST_INPUT='{"branchPath":"MAIN","codeSystemShortName":"SNOMEDCT","type":"DELTA"}'
    EXPORT_UUID="$(curl -u "${SNOW_OWL_USER}:${SNOW_OWL_PASSWORD}" -i -X POST -H "Content-type: ${MEDIA_TYPE}" "http://${TARGET_ENVIRONMENT}:8080/${EXPORT_CONFIG_POST_ENDPOINT}" -d "${EXPORT_CONFIG_POST_INPUT}" | tr -d '\r' | sed -En 's/^Location: (.*)/\1/p')"
    download_delta
}

download_delta() {
    echo "EXPORT_UUID: ${EXPORT_UUID}"
    EXPORT_DOWNLOAD_GET_ENDPOINT="${EXPORT_UUID}/archive"

    echo "Downloading snapshot export with UUID: ${EXPORT_DOWNLOAD_GET_ENDPOINT}"

    curl -u "${SNOW_OWL_USER}:${SNOW_OWL_PASSWORD}" -X GET -H "Accept: ${ACCEPT_HEADER}" -ko "${EXPORT_FILE_NAME}" "${EXPORT_DOWNLOAD_GET_ENDPOINT}" 

    DATE=$(date +"%Y%m%d_%H%M%S")

    RENAMED_EXPORT_FILE="${EXPORT_FILE_NAME}_${DATE}.zip"
     
    mv "${EXPORT_FILE_NAME}" "${RENAMED_EXPORT_FILE}"
}

execute() {

    TARGET_FOLDER="${SCRIPT_LOCATION}"

    validate_variables

    export_delta

    exit 0
}

while getopts "u:p::t:h" option; do
    case "${option}" in
    h)
        usage
        exit 0
        ;;
    u)
        SNOW_OWL_USER=${OPTARG};;
    p)
        SNOW_OWL_PASSWORD=${OPTARG};;
    t)
        TARGET_ENVIRONMENT=${OPTARG};;
    \?)
        echo "Invalid option: {$OPTARG}." >&2
        usage
        exit 1
        ;;
    :)
        echo "Option -{$OPTARG} requires an argument." >&2
        usage
        exit 1
        ;;
    esac
done

execute
