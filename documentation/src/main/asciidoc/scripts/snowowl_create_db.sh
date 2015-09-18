#!/usr/bin/env bash

#
# Copyright (c) 2015 B2i Healthcare. All rights reserved.
#
# This script creates all required databases and user for Snow Owl Server US edition.
#

DATABASES=( atcStore icd10Store icd10amStore \
icd10cmStore localterminologyStore loincStore \
mappingsetStore snomedStore umlsStore valuesetStore )

MYSQL=`which mysql`
USER="root"
PASSWORD="root_pwd"

echo -e "\nStarting Snow Owl database setup procedure."

# Create user
${MYSQL} -u${USER} -p${PASSWORD} -e "CREATE USER 'snowowl'@'localhost' identified by 'snowowl';" > /dev/null 2>&1
echo -e "\n\tCreated snowowl/mysql user."

# Create databases
echo -e "\n\tCreating Snow Owl databases:"
for i in "${DATABASES[@]}"
	do
		${MYSQL} -u${USER} -p${PASSWORD} -e "CREATE DATABASE \`${i}\` DEFAULT CHARSET 'utf8';" > /dev/null 2>&1
		echo -e "\t\tCreated Snow Owl database ${i}."
		${MYSQL} -u${USER} -p${PASSWORD} -e "GRANT ALL PRIVILEGES ON \`${i}\`.* to 'snowowl'@'localhost';" > /dev/null 2>&1
		echo -e "\t\tPrivileges granted on ${i} for snowowl user."
	done
echo -e "\tCreation of Snow Owl databases are now complete."
	
${MYSQL} -u${USER} -p${PASSWORD} -e "FLUSH PRIVILEGES;" > /dev/null 2>&1
echo -e "\n\tGrant tables reloaded."

echo -e "\nSnow Owl database setup procedure has finished.\n"