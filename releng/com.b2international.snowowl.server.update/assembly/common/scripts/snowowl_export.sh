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


#
# Parameters that must be configured before execution
#

# The username to authenticate through the REST API
SNOW_OWL_USER=""

# The password for the above user
SNOW_OWL_USER_PASSWORD=""

# The target folder to save the exported RF2 content
TARGET_FOLDER=""


#
# Global variables / constants, advanced parameters
#

# Branch to export defaults to MAIN
BRANCH_TO_EXPORT="MAIN"

# Specified moduleIds to export
MODULES_TO_EXPORT

# Specified refsets to export
REFSETS_TO_EXPORT

# The export type to use for the export config
EXPORT_TYPE="DELTA"

# Base URL of the Snow Owl server
SNOW_OWL_BASE_URL="http://localhost:8080"

# URL for Snow Owl's REST API
SNOW_OWL_API_URL="/snowowl/snomed-ct/v3"

# The address where the export config endpoint can be found
EXPORTS_POST_ENDPOINT="${SNOW_OWL_API_URL}/exports"

# Media type to use for REST requests
MEDIA_TYPE="application/vnd.com.b2international.snowowl+json"

# Accept header for archive download
ACCEPT_HEADER="application/octet-stream"

# The UUID of the export request given by Snow Owl
EXPORT_UUID=""

# The input data for the export config
EXPORT_CONFIG_POST_INPUT=""

usage() {

cat <<EOF

NAME:

    Export script for the Snow Owl terminology server
    
    OPTIONS:

    -h
        Show this help
    -u
        Define a username with privileges to the Snow Owl REST API
    -p
        Define the password for the above user
    -b
        Snow Owl base URL, defaults to 'http://localhost:8080'
    -t
        Target folder where the exported content should be saved
    -e
        Release format (possible values are SNAPSHOT, DELTA, FULL), defaults to DELTA
    -m
        Snomed CT module IDs to filter components of the resulting RF2 by a set of module IDs.
    -s
        Branch from Snow Owl to initiate the export on
    -r
        Snomed CT reference set IDs to include in the export RF2
    -a
        REST API URL of the Snow Owl server, defaults to '/snowowl/snomed-ct/v3'

NOTES:

    This script can be used to export content from a Snow Owl terminology server. 

    Mandatory variables:
        - SNOW OWL user that is able to authenticate through the REST API
        - Password for the above user
        - Target folder for the export archive

EOF

}

echo_date() {
    echo -e "[`date +\"%Y-%m-%d %H:%M:%S\"`] $@"
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
    check_if_empty "${TARGET_FOLDER}" "Target folder must be specified"
    check_if_empty "${TARGET_FOLDER}" "Branch must be specified"

    if [[ "${EXPORT_TYPE}" != "DELTA" && "${EXPORT_TYPE}" != "SNAPSHOT" && "${EXPORT_TYPE}" != "FULL" ]]; then
        echo_date "ERROR: Unrecognized export type was given as parameter: ${EXPORT_TYPE}"
        exit 1;
    fi
    
    if [[ ! -d "${TARGET_FOLDER}" ]]; then
        echo_date "Creating target folder @ ${TARGET_FOLDER}"
        mkdir "${TARGET_FOLDER}"
    fi

    EXPORT_FILE_NAME="snow_owl_${EXPORT_TYPE}_export"

}

initiate_export() {
    EXPORT_CONFIG_POST_INPUT='{"branchPath": "'"${BRANCH_TO_EXPORT}"'", "type": "'"${EXPORT_TYPE}"'", "codeSystemShortName": "SNOMEDCT"'

    if (( ${#REFSETS_TO_EXPORT[@]} )); then
		# Append refsets to config
        REFSETS_JSON_ARRAY="["
        for refset in "${REFSETS_TO_EXPORT[@]::${#REFSETS_TO_EXPORT[@]}-1}" ; do
            REFSETS_JSON_ARRAY+='"'"${refset}"'", '
        done
        REFSETS_JSON_ARRAY+='"'"${REFSETS_TO_EXPORT[@]: -1:1}"'"]'
        echo "REFSETS AFTER MANIPULATION ${REFSETS_JSON_ARRAY}"
        EXPORT_CONFIG_POST_INPUT+=', "'"refsets"'": '${REFSETS_JSON_ARRAY}''
    fi

    if (( ${#MODULES_TO_EXPORT[@]} )); then
    	# Append modules to config
        MODULES_JSON_ARRAY="["
        for module in "${MODULES_TO_EXPORT[@]::${#MODULES_TO_EXPORT[@]}-1}" ; do
            MODULES_JSON_ARRAY+='"'"${module}"'", '
        done
        MODULES_JSON_ARRAY+='"'"${MODULES_TO_EXPORT[@]: -1:1}"'"]'
        echo "REFSETS AFTER MANIPULATION ${MODULES_JSON_ARRAY}"
        EXPORT_CONFIG_POST_INPUT+=', "moduleIds": '${MODULES_JSON_ARRAY}''
    fi
    
    EXPORT_CONFIG_POST_INPUT+="}"
    
    EXPORTS_ENDPOINT="${SNOW_OWL_BASE_URL}${EXPORTS_POST_ENDPOINT}"

    echo_date "Initating "${EXPORT_TYPE}" export with config: "${EXPORT_CONFIG_POST_INPUT}" on target: "${EXPORTS_ENDPOINT}""

    RESPONSE="$(curl --user "${SNOW_OWL_USER}:${SNOW_OWL_USER_PASSWORD}" \
        --request POST \
        --header "Content-type: ${MEDIA_TYPE}" \
        --data "${EXPORT_CONFIG_POST_INPUT}" \
        --include --silent --show-error \
        "${EXPORTS_ENDPOINT}" | grep -Fi Location)"

    ID=${RESPONSE##*/}
    EXPORT_UUID=${ID%$'\r'}

    download_export_archive

}

download_export_archive() {

    check_if_empty "${EXPORT_UUID}" "Unique export identifier is missing"

    EXPORT_DOWNLOAD_GET_ENDPOINT="${SNOW_OWL_BASE_URL}${EXPORTS_POST_ENDPOINT}/${EXPORT_UUID}/archive"

    echo_date "Downloading ${EXPORT_TYPE} export with UUID: ${EXPORT_UUID}"

    DATE=$(date +"%Y%m%d_%H%M%S")

    RENAMED_EXPORT_FILE="${EXPORT_FILE_NAME}_${DATE}.zip"

    HTTP_STATUS=$(curl --user "${SNOW_OWL_USER}:${SNOW_OWL_USER_PASSWORD}" \
        --request GET \
        --header "Accept: ${ACCEPT_HEADER}" \
        --output "${TARGET_FOLDER}/${RENAMED_EXPORT_FILE}" \
        --silent --show-error \
        --write-out %{http_code} \
        "${EXPORT_DOWNLOAD_GET_ENDPOINT}")

    if [ "${HTTP_STATUS}" != "200" ]; then
        echo_date "Download of export archive returned with code ${HTTP_STATUS}"
    else
        echo_date "Export archive is available @ ${TARGET_FOLDER}/${RENAMED_EXPORT_FILE}"
    fi

}

execute() {

    validate_variables

    initiate_export

    exit 0
}

while getopts ":hu:p:t:e:b:a:s:m:r:" option; do
    case "${option}" in
    h)
        usage
        exit 0
        ;;
    u)
        SNOW_OWL_USER=${OPTARG};;
    p)
        SNOW_OWL_USER_PASSWORD=${OPTARG};;
    t)
        TARGET_FOLDER=${OPTARG};;
    e)
        EXPORT_TYPE=${OPTARG};;
    b)
        SNOW_OWL_BASE_URL=${OPTARG};;
    a)
        SNOW_OWL_API_URL=${OPTARG};;    
    s)
        BRANCH_TO_EXPORT=${OPTARG};;
    m)
        MODULES_TO_EXPORT+=("${OPTARG}");;
    r)
        REFSETS_TO_EXPORT+=("${OPTARG}");;
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
