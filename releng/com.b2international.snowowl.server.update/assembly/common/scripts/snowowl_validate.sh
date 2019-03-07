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
# Snow Owl terminology server validation script
# See usage or execute the script with the -h flag to get further information.


#
# Parameters that must be configured before execution
#

# The username to authenticate through the REST API
SNOW_OWL_ADMIN_USER=""

# The password for the above user
SNOW_OWL_ADMIN_USER_PASSWORD=""

# The target folder to save the validation report
TARGET_FOLDER=""

#
# Global variables / constants, advanced parameters
#

# Default branch to validate
BRANCH_TO_VALIDATE="MAIN"

# Code system short name
CODE_SYSTEM_SHORT_NAME="SNOMEDCT"

# Interval to poll for the validation result
SLEEP_INTERVAL=60

# Should the validation be ran on unpublished content
IS_UNPUBLISHED_VALIDATION=true

# Rules to validate (id-s of rules)
RULES_TO_VALIDATE=("34" "38a" "38b" "45a" "45b" "53" "54" "55" "74" "75" "77a" "77b" "78a" "78b" "79" "80" "83" "84" "110" "113" "115a" "115b" "115c" "148" "150" "158" "160" "161" "179" "187" "204" "205" "212" "check_VMPP_Has_VMP" "check_AMPP_Has_AMP" "266" "278" "281" "289a" "289b" "291" "293" "305" "326" "370" "371" "387" "390" "400" "401" "402" "424" "425" "428" "429" "456" "473" "482" "532a" "532b" "544" "547" "636" "snomed-common-1" "snomed-common-2")

# Base URL of the Snow Owl server
SNOW_OWL_BASE_URL="http://localhost:8080"

# URL for Snow Owl's REST API
SNOW_OWL_ADMIN_URL="/snowowl/admin"

# The address where the validation endpoint can be found
VALIDATIONS_POST_ENDPOINT="${SNOW_OWL_ADMIN_URL}/validations"

# Media type to use for REST requests
MEDIA_TYPE="application/vnd.com.b2international.snowowl+json"

# The UUID of the validation request given by Snow Owl
VALIDATION_UUID=""

# The input data for the validation config
VALIDATION_CONFIG_POST_INPUT=""

usage() {

cat <<EOF
NAME:
    Validation script for the Snow Owl terminology server
    
    OPTIONS:
    -h
        Show this help
    -u
        Define a username with admin privileges to the Snow Owl REST API
    -p
        Define the password for the above user
    -b
        Snow Owl base URL, defaults to 'http://localhost:8080'
    -t
        Target folder where the validation report should be saved
    -c
        The codesystems shortname where the validation should be ran (Default value is SNOMEDCT)
    -i
        If the validation should run on unpublished content or not (Default value is true)
NOTES:
    This script can be used to validate content on a Snow Owl terminology server. 
    Mandatory variables:
        - Admin SNOW OWL user that is able to authenticate through the REST API
        - Password for the above user
        - Target folder for the validation report
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

    check_if_empty "${SNOW_OWL_ADMIN_USER}" "Snow Owl admin username must be specified"
    check_if_empty "${SNOW_OWL_ADMIN_USER_PASSWORD}" "User password must be specified"
    check_if_empty "${TARGET_FOLDER}" "Target folder must be specified"

    if [[ ! -d "${TARGET_FOLDER}" ]]; then
        echo_date "Creating target folder @ ${TARGET_FOLDER}"
        mkdir "${TARGET_FOLDER}"
    fi

}

initiate_validation() {
    
    RULEIDS_JSON_ARRAY="["
    FIRST="first"
    for i in ${!RULES_TO_VALIDATE[@]}; do
        if [[ ${FIRST} = "first" ]]; then
            RULEIDS_JSON_ARRAY=''${RULEIDS_JSON_ARRAY}' "'"${RULES_TO_VALIDATE[i]},"'"'
            FIRST=""
        else
            RULEIDS_JSON_ARRAY=''${RULEIDS_JSON_ARRAY}', "'"${RULES_TO_VALIDATE[i]}"'"'
        fi
    done

    RULEIDS_JSON_ARRAY="${RULEIDS_JSON_ARRAY}]"
    VALIDATION_CONFIG_POST_INPUT='{"branch": "'"${BRANCH_TO_VALIDATE}"'", "isUnpublishedValidation": "'"${IS_UNPUBLISHED_VALIDATION}"'", "codeSystemShortName": "'"${CODE_SYSTEM_SHORT_NAME}"'", "ruleIds": '${RULEIDS_JSON_ARRAY}'}'
    VALIDATION_ENDPOINT="${SNOW_OWL_BASE_URL}${VALIDATIONS_POST_ENDPOINT}"

    VALIDATION_TYPE_TEXT=
    if [[ ${IS_UNPUBLISHED_VALIDATION} == true ]]; then
        VALIDATION_TYPE_TEXT="unpublished"
    else
        VALIDATION_TYPE_TEXT="published"
    fi

    echo_date "Initating "${VALIDATION_TYPE_TEXT}" validation with config: "${VALIDATION_CONFIG_POST_INPUT}" on target: "${VALIDATION_ENDPOINT}""

    RESPONSE="$(curl --user "${SNOW_OWL_ADMIN_USER}:${SNOW_OWL_ADMIN_USER_PASSWORD}" \
       --request POST \
       --header "Content-type: ${MEDIA_TYPE}" \
       --data "${VALIDATION_CONFIG_POST_INPUT}" \
       --include --silent --show-error \
       "${VALIDATION_ENDPOINT}" | grep -Fi Location)"

    ID=${RESPONSE##*/}
    VALIDATION_UUID=${ID%$'\r'}
    
    get_validation_results

}

get_validation_results() {

    check_if_empty "${VALIDATION_UUID}" "Unique validation identifier is missing"

    poll_validation

    VALIDATION_ISSUES_GET_ENDPOINT="${SNOW_OWL_BASE_URL}${VALIDATIONS_POST_ENDPOINT}/${VALIDATION_UUID}/issues"

    VALIDATION_ISSUES_REPORT=$(curl --user "${SNOW_OWL_ADMIN_USER}:${SNOW_OWL_ADMIN_USER_PASSWORD}" \
        --request GET \
        --header "Accept: text/csv" \
        --silent --show-error \
        "${VALIDATION_ISSUES_GET_ENDPOINT}")


    DATE=$(date +"%Y%m%d_%H%M%S")

    VALIDATION_REPORT_FILE="${TARGET_FOLDER}/snowowl_validation_report_${BRANCH_TO_VALIDATE}_${DATE}.tsv"

    echo "${VALIDATION_ISSUES_REPORT}" > "${VALIDATION_REPORT_FILE}"

    echo "Finished downloading the validation report to ${VALIDATION_REPORT_FILE}"

}

poll_validation() {
    VALIDATIONS_STATE_ENDPOINT="${SNOW_OWL_BASE_URL}${VALIDATIONS_POST_ENDPOINT}/${VALIDATION_UUID}"

    # This is going to poll for the validation results for a maximum of an hour
    ((POLLING_END_TIME=${SECONDS}+3600))
    echo_date "Starting polling for validation result"
    while ((${SECONDS} < ${POLLING_END_TIME}))
    do
        VALIDATIONS_STATE_RESPONSE="$(curl --user "${SNOW_OWL_ADMIN_USER}:${SNOW_OWL_ADMIN_USER_PASSWORD}" \
            --request GET \
            --header "Content-type: ${MEDIA_TYPE}" \
            --data "${VALIDATIONS_STATE_ENDPOINT}" \
            --silent --show-error \
            "${VALIDATIONS_STATE_ENDPOINT}" | grep -Po '"state":.*?[^\\]",' | sed 's/\"state\":\"\(.*\)\",/\1/')"

        if [[ $VALIDATIONS_STATE_RESPONSE = "FINISHED" ]]; then
            echo "Validation has finished. Downloading report."
            break
        else 
            echo_date "Validation has not finished yet. Current state is: ${VALIDATIONS_STATE_RESPONSE}"
        fi
        sleep ${SLEEP_INTERVAL}
    done

     if [[ $VALIDATIONS_STATE_RESPONSE != "FINISHED" ]]; then
        echo_date "Validation did not finish in 1 hour. Exiting."
        exit 0
     fi
}

execute() {

    validate_variables

    initiate_validation

    exit 0
}

while getopts ":hu:p:b:t:c:i" option; do
    case "${option}" in
    h)
        usage
        exit 0
        ;;
    u)
        SNOW_OWL_ADMIN_USER=${OPTARG};;
    p)
        SNOW_OWL_ADMIN_USER_PASSWORD=${OPTARG};;
    b)
        SNOW_OWL_BASE_URL=${OPTARG};;
    t)
        TARGET_FOLDER=${OPTARG};;
    c)
        CODE_SYSTEM_SHORT_NAME=${OPTARG};;
    i)
        IS_UNPUBLISHED_VALIDATION=${OPTARG};;
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