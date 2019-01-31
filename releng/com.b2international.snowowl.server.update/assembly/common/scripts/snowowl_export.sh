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
# Snow Owl terminology server export script
# See usage or execute the script with the -h flag to get further information.
# Configurable params
#

# The user for the user authentication for the REST API.
SNOW_OWL_USER=""

# The password for the above user.
SNOW_OWL_PASSWORD=""

# The export type to use for the export config.
EXPORT_TYPE="DELTA"

# The target folder to save the exported RF2 content.
TARGET_FOLDER=""

# Base URL for the server that runs Snow Owl.
SNOW_OWL_BASE_URL="http://localhost:8080"

# Api URL of the running Snow Owl server.
SNOW_OWL_API_URL="/snowowl/snomed-ct/v3"

#
# Global variables / constants, advanced configurable params.
#

# Media type to use for the export configuration.
MEDIA_TYPE="application/vnd.com.b2international.snowowl+json"

# The uuid of the export request given by Snow Owl.
EXPORT_UUID=""

# The input data for the export config.
EXPORT_CONFIG_POST_INPUT=""

# Accept header for archive download.
ACCEPT_HEADER="application/octet-stream"

# Forces the export config to export concepts and relationships only.
IS_CONCEPT_AND_RELATIONSHIPS_ONLY=${false}

# The address where the export config endpoint can be found.
EXPORT_CONFIG_POST_ENDPOINT=""

# The initial name of the export.
EXPORT_FILE_NAME=""

# The renamed version of the export file.
RENAMED_EXPORT_FILE=""

usage() {

	cat <<EOF

NAME:
	Snow Owl export script. The export file will be saved where the user has set it to.
    
    OPTIONS:
	-h
		Show this help
	-u
		Define a username with privileges to the Snow Owl REST API
	-p
		Define the password for the above user
    -b
        Base URL for the server defaults to (http://localhost:8080)
    -f
        Target folder where the exported content should be saved
    -e
        Export type for the export configuration (possible values are SNAPSHOT, DELTA, FULL)
    -a
        Base API URL of the Snow Owl server defaults to (/snowowl/snomed-ct/v3)
NOTES:
	This script can be used to initiate an export job that will run an export from a Snow Owl server. 
	Mandatory variables:
		- SNOW OWL user to use for the Snow Owl export
	    - SNOW OWL password for the above user
        - Target folder to save the exported content
EOF

}

validate_variables() {
    check_if_empty "${SNOW_OWL_USER}" "Snow Owl username must be specified"
	check_if_empty "${SNOW_OWL_PASSWORD}" "Snow Owl password must be specified"
	check_if_empty "${TARGET_FOLDER}" "Target folder must be specified"

    if [[ "${EXPORT_TYPE}" != "DELTA" && "${EXPORT_TYPE}" != "SNAPSHOT" && "${EXPORT_TYPE}" != "FULL" ]]; then
        echo "ERROR: Unrecognized export type was given as parameter: ${EXPORT_TYPE}"
        exit 1;
    fi
    
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
    EXPORT_CONFIG_POST_ENDPOINT="${SNOW_OWL_API_URL}/exports"

    EXPORT_CONFIG_POST_INPUT='{"branchPath": "MAIN", "type": "'"${EXPORT_TYPE}"'", "codeSystemShortName": "SNOMEDCT"}'
    EXPORT_LOCATION="${SNOW_OWL_BASE_URL}${EXPORT_CONFIG_POST_ENDPOINT}"

    echo "Initating "${EXPORT_TYPE}" export with config: "${EXPORT_CONFIG_POST_INPUT}" on target: "${EXPORT_LOCATION}""

    EXPORT_UUID="$(curl -u "${SNOW_OWL_USER}:${SNOW_OWL_PASSWORD}" -i -X POST -H "Content-type: ${MEDIA_TYPE}" "${EXPORT_LOCATION}" -d "${EXPORT_CONFIG_POST_INPUT}" | tr -d '\r' | sed -En 's/^Location: (.*)/\1/p')"
    download_delta
}

download_delta() {
    EXPORT_DOWNLOAD_GET_ENDPOINT="${EXPORT_UUID}/archive"

    echo "Downloading ${EXPORT_TYPE} export with UUID: ${EXPORT_DOWNLOAD_GET_ENDPOINT}"

    DATE=$(date +"%Y%m%d_%H%M%S")

    RENAMED_EXPORT_FILE="${EXPORT_FILE_NAME}_${DATE}.zip"

    curl -u "${SNOW_OWL_USER}:${SNOW_OWL_PASSWORD}" -X GET -H "Accept: ${ACCEPT_HEADER}" -ko "${TARGET_FOLDER}/${RENAMED_EXPORT_FILE}" "${EXPORT_DOWNLOAD_GET_ENDPOINT}"
}

execute() {

    validate_variables

    EXPORT_FILE_NAME="snow_owl_${EXPORT_TYPE}_export"

    export_delta

    exit 0
}

while getopts "u:p::f:e:b:a:h" option; do
    case "${option}" in
    h)
        usage
        exit 0
        ;;
    u)
        SNOW_OWL_USER=${OPTARG};;
    p)
        SNOW_OWL_PASSWORD=${OPTARG};;
    f)
        TARGET_FOLDER=${OPTARG};;
    e)
        EXPORT_TYPE=${OPTARG};;
    b)
        SNOW_OWL_BASE_URL=${OPTARG};;
    a)
        SNOW_OWL_API_URL=${OPTARG};;    
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
