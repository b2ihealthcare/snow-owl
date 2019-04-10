#!/bin/sh
  
SERVER_ARCHIVE_PATH=$1
SERVER_ANCHOR_FILE="snowowl_config.yml"
SERVER_PATH="/opt/snowowl/server"

check_server_zip() {
    echo "Server archive path: ${SERVER_ARCHIVE_PATH}"
    if [[ -f "${SERVER_ARCHIVE_PATH}" ]] ; then
        echo "Passed in argument was indeed a file: ${SERVER_ARCHIVE_PATH}"
        echo "Installing Snow Owl"
    else
        echo "Passed in argument was not a file: ${SERVER_ARCHIVE_PATH}"
        echo "Exiting with error"
        exit 1
    fi
}

check_snowowl_in_zip() {
    CONFIG_LOCATION=$(unzip -l ${SERVER_ARCHIVE_PATH} | grep ${SERVER_ANCHOR_FILE} | sed 's/ /\n/g' | tail -n1 | sed 's/ //g')

    if [ -z "${CONFIG_LOCATION}" ]; then
        echo "Unable to locate Snow Owl server within '${SERVER_ARCHIVE_PATH}'"
        exit 1
    else
        SERVER_PATH_WITHIN_ARCHIVE=$(dirname "$CONFIG_LOCATION")
        if [ "${SERVER_PATH_WITHIN_ARCHIVE}" = "." ]; then
            echo "Found Snow Owl server in the root of '${SERVER_ARCHIVE_PATH}'"
        else
            echo "Found Snow Owl server within the provided archive: '${SERVER_ARCHIVE_PATH}/${SERVER_PATH_WITHIN_ARCHIVE}'"
        fi
    fi
}

unzip_server() {

	echo "Unzipping server archive"


	TMP_SERVER_DIR=$(mktemp -d -p "/usr/share")

	unzip -q "${SERVER_ARCHIVE_PATH}" -d "${TMP_SERVER_DIR}"

	CURRENT_DATE=$(date +%Y%m%d_%H%M%S)

	if [ ! -d "${SERVER_PATH}" ]; then
		mkdir "${SERVER_PATH}"
	fi

	if [ "${SERVER_PATH_WITHIN_ARCHIVE}" = "." ]; then
		mv "${TMP_SERVER_DIR}/"* "${SERVER_PATH}"
	else
		mv "${TMP_SERVER_DIR}/${SERVER_PATH_WITHIN_ARCHIVE}/"* "${SERVER_PATH}"
	fi

    if [[ -d "${TMP_SERVER_DIR}" ]]; then
        rm -rf "${TMP_SERVER_DIR}"
    fi 

	echo "Extracted server files to: '${SERVER_PATH}'"

	# logs

    if [[ ! -d "${SERVER_PATH}/sserviceability" ]]; then
        mkdir "${SERVER_PATH}/serviceability" && mkdir "${SERVER_PATH}/serviceability/logs"
    fi

    

	# resources
    if [[ ! -d "${SERVER_PATH}/resources" ]]; then
        mkdir "${SERVER_PATH}/resources"
    fi
}

main() {

    check_server_zip

    check_snowowl_in_zip

    unzip_server

	exit 0
}

main