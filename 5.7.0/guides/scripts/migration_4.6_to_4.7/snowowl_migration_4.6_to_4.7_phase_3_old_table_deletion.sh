#!/usr/bin/env bash

#
# Copyright (c) 2013-2016 B2i Healthcare. All rights reserved.
#

# Deletes the old terminology specific code system related tables.

# Usage: ./snowowl_migration_4.6_to_4.7_phase_3_old_table_deletion.sh <password> <terminology>
# E.g. ./snowowl_migration_4.6_to_4.7_phase_3_old_table_deletion.sh admin snomed

PASSWORD=$1
TERMINOLOGY=$2

MYSQL=`which mysql`
USER=root

if [ "x$PASSWORD" = "x" ]; then
  echo -e "Please set the mysql password before running this script. Exiting with error."
  exit 1
fi

if [ "x$TERMINOLOGY" = "x" ]; then
  echo -e "Please set the terminology name (e.g. snomed) before running this script. Exiting with error."
  exit 1
fi

echo -e "Starting Snow Owl migration procedure phase 3 - Old tables deletion."

echo -e "\t1. Deleting old Code System tables."

COMMAND="USE ${TERMINOLOGY}Store;"

COMMAND="${COMMAND} DROP TABLE IF EXISTS \`${TERMINOLOGY}_codesystem\`;"

COMMAND="${COMMAND} DROP TABLE IF EXISTS \`${TERMINOLOGY}_codesystemversion\`;"

COMMAND="${COMMAND} DROP TABLE IF EXISTS \`${TERMINOLOGY}_codesystemversiongroup\`;"

COMMAND="${COMMAND} DROP TABLE IF EXISTS \`${TERMINOLOGY}_codesystemversiongroup_codesystems_list\`;"

COMMAND="${COMMAND} DROP TABLE IF EXISTS \`${TERMINOLOGY}_codesystemversiongroup_codesystemversions_list\`;"

${MYSQL} -u${USER} -p${PASSWORD} -e "${COMMAND}"

echo -e "\tDeleting old Code System tables process is complete."

echo -e "Snow Owl migration procedure phase 3 - Old tables deletion is complete."