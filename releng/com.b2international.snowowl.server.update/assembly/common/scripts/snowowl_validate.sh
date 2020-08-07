#!/usr/bin/env bash
#
# Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
# Parameters that could be configured before execution
#

# The username to authenticate through the REST API
SNOW_OWL_USER=""

# The password for the above user
SNOW_OWL_USER_PASSWORD=""

# The target folder to save the validation report
TARGET_FOLDER="/tmp"

#
# Global variables / constants, advanced parameters
#

# Default branch to validate
BRANCH_TO_VALIDATE="SNOMEDCT/HEAD"

# Interval to poll for the validation result
SLEEP_INTERVAL=10

# Should the validation be ran on unpublished content
IS_UNPUBLISHED_VALIDATION=true

# Rules to validate (id-s of rules)
RULES_TO_VALIDATE=()

# Base URL of the Snow Owl server
SNOW_OWL_BASE_URL="http://localhost:8080"

# The address where the validation endpoint can be found
VALIDATION_ENDPOINT="${SNOW_OWL_BASE_URL}/snowowl/snomed-ct/v3/validations"

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
    -c
        Branch path (e.g. MAIN/2020-07-31/SNOMED-EXTENSION) or code system URI (e.g. SNOMED-EXTENSION/HEAD) to validate
    -t
        Target folder where the validation report should be saved
    -i
        If the validation should run on unpublished content or not (Default value is true)
NOTES:

    This script can be used to validate SNOMED CT content on a Snow Owl terminology server.

EOF

}

echo_date() {
	echo -e "[$(date +"%Y-%m-%d %H:%M:%S")] $1"
}

echo_error() {
	echo_date "ERROR: $1" >&2
}

echo_exit() {
	echo_error "$1"
	exit 1
}

check_if_empty() {
	if [[ -z "$1" ]]; then
		echo_exit "$2"
	fi
}

rest_call() {

	REQUEST_TYPE=$1
	shift

	CURL_OUTPUT=$(
		curl --request "${REQUEST_TYPE}" \
			--silent \
			--show-error \
			--header "Content-type: application/json" \
			--user "${SNOW_OWL_USER}:${SNOW_OWL_USER_PASSWORD}" \
			--write-out "\n%{http_code}" \
			"$@"
	)

	CURL_MESSAGE=$(echo "${CURL_OUTPUT}" | head -n -1)
	CURL_HTTP_STATUS=$(echo "${CURL_OUTPUT}" | tail -n 1)

}

validate_variables() {

	check_if_empty "${SNOW_OWL_USER}" "Snow Owl admin username must be specified"
	check_if_empty "${SNOW_OWL_USER_PASSWORD}" "User password must be specified"
	check_if_empty "${TARGET_FOLDER}" "Target folder must be specified"

	check_if_empty "${BRANCH_TO_VALIDATE}" "A branch or a code system URI must be specified"
	check_if_empty "${SNOW_OWL_BASE_URL}" "Snow Owl's base URL must be specified"

	if [[ ! -d "${TARGET_FOLDER}" ]]; then
		echo_date "Creating target folder @ ${TARGET_FOLDER}"
		mkdir --parents "${TARGET_FOLDER}"
	fi

}

initiate_validation() {

	if [[ ${#RULES_TO_VALIDATE[@]} -eq 0 ]]; then

		echo_date "Couldn't find predefined rules to validate, using all rules as default."
		VALIDATION_CONFIG_POST_INPUT='{"branch": "'"${BRANCH_TO_VALIDATE}"'", "unpublishedOnly": "'"${IS_UNPUBLISHED_VALIDATION}"'"}'

	else

		RULEIDS_JSON_ARRAY="["
		FIRST="first"

		for i in "${!RULES_TO_VALIDATE[@]}"; do
			if [[ "${FIRST}" == "first" ]]; then
				RULEIDS_JSON_ARRAY=''${RULEIDS_JSON_ARRAY}' "'"${RULES_TO_VALIDATE[$i]}"'"'
				FIRST=""
			else
				RULEIDS_JSON_ARRAY=''${RULEIDS_JSON_ARRAY}', "'"${RULES_TO_VALIDATE[$i]}"'"'
			fi
		done

		RULEIDS_JSON_ARRAY="${RULEIDS_JSON_ARRAY}]"

		VALIDATION_CONFIG_POST_INPUT='{"branch": "'"${BRANCH_TO_VALIDATE}"'", "unpublishedOnly": "'"${IS_UNPUBLISHED_VALIDATION}"'", "ruleIds": '${RULEIDS_JSON_ARRAY}'}'

	fi

	if [[ "${IS_UNPUBLISHED_VALIDATION}" == true ]]; then
		VALIDATION_TYPE_TEXT="unpublished"
	else
		VALIDATION_TYPE_TEXT="published"
	fi

	echo_date "Initiating ${VALIDATION_TYPE_TEXT} validation with config: ${VALIDATION_CONFIG_POST_INPUT} on target: ${VALIDATION_ENDPOINT}"

	rest_call "POST" --include --data "${VALIDATION_CONFIG_POST_INPUT}" "${VALIDATION_ENDPOINT}"

	if [[ "${CURL_HTTP_STATUS}" != "201" ]]; then
		echo_exit "Failed to initiate validation job with response: '${CURL_MESSAGE}'"
	fi

	RESPONSE=$(echo "${CURL_MESSAGE}" | grep -Fi "Location")

	ID=${RESPONSE##*/}
	VALIDATION_UUID=${ID%$'\r'}

	get_validation_results

}

poll_validation() {

	VALIDATION_GET_ENDPOINT="${VALIDATION_ENDPOINT}/${VALIDATION_UUID}"

	# This is going to poll for the validation results for a maximum of an hour
	((POLLING_END_TIME = ${SECONDS} + 3600))
	echo_date "Started polling validation with ID '${VALIDATION_UUID}'..."

	while ((${SECONDS} < ${POLLING_END_TIME})); do

		rest_call "GET" --include "${VALIDATION_GET_ENDPOINT}"

		if [[ "${CURL_HTTP_STATUS}" != "200" ]]; then
			echo_exit "Validation with ID '${VALIDATION_UUID}' does not exist, response: '${CURL_MESSAGE}'"
		fi

		VALIDATION_STATE_RESPONSE=$(echo "${CURL_MESSAGE}" | grep -Po '"state":.*?[^\\]",' | sed 's/\"state\":\"\(.*\)\",/\1/')

		if [[ "${VALIDATION_STATE_RESPONSE}" == "FINISHED" ]]; then
			echo_date "Validation with ID '${VALIDATION_UUID}' has finished, downloading report..."
			break
		elif [[ "${VALIDATION_STATE_RESPONSE}" == "FAILED" ]]; then
			echo_exit "Validation with ID '${VALIDATION_UUID}' failed..."
		else
			echo_date "Current validation state is: '${VALIDATION_STATE_RESPONSE}'"
		fi

		sleep ${SLEEP_INTERVAL}

	done

	if [[ "${VALIDATION_STATE_RESPONSE}" != "FINISHED" ]]; then
		echo_exit "Validation with ID '${VALIDATION_UUID}' did not finish in 1 hour. Exiting."
	fi

}

get_validation_results() {

	check_if_empty "${VALIDATION_UUID}" "Unique validation identifier is missing"

	poll_validation

	VALIDATION_GET_ISSUES_ENDPOINT="${VALIDATION_ENDPOINT}/validations/${VALIDATION_UUID}/issues"

	DATE=$(date +"%Y%m%d_%H%M%S")
	SAFE_BRANCH_NAME=$(echo "${BRANCH_TO_VALIDATE}" | sed -e 's/\//_/g')
	VALIDATION_REPORT_FILE="${TARGET_FOLDER}/snowowl_validation_report_${SAFE_BRANCH_NAME}_${DATE}.tsv"

	rest_call "GET" --header "accept: text/csv;charset=UTF-8" "${VALIDATION_GET_ISSUES_ENDPOINT}"

	if [[ "${CURL_HTTP_STATUS}" != "200" ]]; then
		echo_exit "Failed to fetch validation results for job with ID '${VALIDATION_UUID}', response: '${CURL_HTTP_STATUS}' - '${CURL_MESSAGE}'"
	else
		echo "${CURL_MESSAGE}" >"${VALIDATION_REPORT_FILE}"
		echo_date "Validation report is available @ '${VALIDATION_REPORT_FILE}'"
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
		SNOW_OWL_USER=${OPTARG}
		;;
	p)
		SNOW_OWL_USER_PASSWORD=${OPTARG}
		;;
	b)
		SNOW_OWL_BASE_URL=${OPTARG}
		;;
	t)
		TARGET_FOLDER=${OPTARG}
		;;
	c)
		BRANCH_TO_VALIDATE=${OPTARG}
		;;
	i)
		IS_UNPUBLISHED_VALIDATION=${OPTARG}
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
