#!/usr/bin/env bash

#
# Copyright (c) 2013-2015 B2i Healthcare. All rights reserved.
#

# Generates and executes an SQL script which will create all required databases for Snow Owl.

DATABASES=( atcStore icd10Store icd10amStore \
localterminologyStore loincStore snomedStore \
valuesetStore mappingsetStore )

MYSQL=$(which mysql)
USER="root"
PASSWORD="root_pwd"
COMMAND=""

for i in "${DATABASES[@]}"
do
	COMMAND="${COMMAND} CREATE DATABASE \`${i}\` DEFAULT CHARSET 'utf8';"
	COMMAND="${COMMAND} GRANT ALL PRIVILEGES ON \`${i}\`.* to 'snowowl'@'localhost';"
done

${MYSQL} -u${USER} -p${PASSWORD} -e "${COMMAND}"
