#!/usr/bin/env bash

#
# Copyright 2015-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
# This script creates all required databases and user for Snow Owl Server US edition.
#

DATABASES=( snomedStore )

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