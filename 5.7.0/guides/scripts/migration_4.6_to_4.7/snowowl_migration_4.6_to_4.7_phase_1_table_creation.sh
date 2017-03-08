#!/usr/bin/env bash

#
# Copyright (c) 2013-2016 B2i Healthcare. All rights reserved.
#

# Creates the new Code System tables for the terminology.

# Usage: ./snowowl_migration_4.6_to_4.7_phase_1_table_creation.sh <password> <terminology>
# E.g. ./snowowl_migration_4.6_to_4.7_phase_1_table_creation.sh admin snomed

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

COMMAND="USE ${TERMINOLOGY}Store;"

echo -e "Starting Snow Owl migration procedure phase 1 - Table creation."

echo -e "\t1. Creating new Code System tables."

COMMAND="${COMMAND} DROP TABLE IF EXISTS \`terminologymetadata_codesystem\`;"

COMMAND="${COMMAND} CREATE TABLE \`terminologymetadata_codesystem\` (
  \`cdo_id\` bigint(20) NOT NULL,
  \`cdo_version\` int(11) NOT NULL,
  \`cdo_branch\` int(11) NOT NULL,
  \`cdo_created\` bigint(20) NOT NULL,
  \`cdo_revised\` bigint(20) NOT NULL,
  \`cdo_resource\` bigint(20) NOT NULL,
  \`cdo_container\` bigint(20) NOT NULL,
  \`cdo_feature\` int(11) NOT NULL,
  \`shortName\` varchar(2048) DEFAULT NULL,
  \`codeSystemOID\` varchar(255) DEFAULT NULL,
  \`name\` varchar(2048) DEFAULT NULL,
  \`maintainingOrganizationLink\` varchar(2048) DEFAULT NULL,
  \`language\` varchar(255) DEFAULT NULL,
  \`citation\` varchar(2048) DEFAULT NULL,
  \`iconPath\` varchar(2048) DEFAULT NULL,
  \`terminologyComponentId\` varchar(2048) DEFAULT NULL,
  \`repositoryUuid\` varchar(255) DEFAULT NULL,
  \`branchPath\` varchar(2048) DEFAULT NULL,
  \`extensionOf\` bigint(20) DEFAULT NULL,
  \`codeSystemVersions\` int(11) DEFAULT NULL,
  UNIQUE KEY \`terminologymetadata_CodeSystem_idx0\` (\`cdo_id\`,\`cdo_version\`,\`cdo_branch\`),
  KEY \`terminologymetadata_CodeSystem_idx1\` (\`cdo_id\`,\`cdo_revised\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; "

COMMAND="${COMMAND} DROP TABLE IF EXISTS \`terminologymetadata_codesystem_codesystemversions_list\`;"

COMMAND="${COMMAND} CREATE TABLE \`terminologymetadata_codesystem_codesystemversions_list\` (
  \`cdo_source\` bigint(20) DEFAULT NULL,
  \`cdo_branch\` int(11) DEFAULT NULL,
  \`cdo_version_added\` int(11) DEFAULT NULL,
  \`cdo_version_removed\` int(11) DEFAULT NULL,
  \`cdo_idx\` int(11) DEFAULT NULL,
  \`cdo_value\` bigint(20) DEFAULT NULL,
  KEY \`terminologymetadata_CodeSystem_codeSystemVersions_list_idx0\` (\`cdo_source\`),
  KEY \`terminologymetadata_CodeSystem_codeSystemVersions_list_idx1\` (\`cdo_branch\`),
  KEY \`terminologymetadata_CodeSystem_codeSystemVersions_list_idx2\` (\`cdo_version_added\`),
  KEY \`terminologymetadata_CodeSystem_codeSystemVersions_list_idx3\` (\`cdo_version_removed\`),
  KEY \`terminologymetadata_CodeSystem_codeSystemVersions_list_idx4\` (\`cdo_idx\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; "

COMMAND="${COMMAND} DROP TABLE IF EXISTS \`terminologymetadata_codesystemversion\`;"

COMMAND="${COMMAND} CREATE TABLE \`terminologymetadata_codesystemversion\` (
  \`cdo_id\` bigint(20) NOT NULL,
  \`cdo_version\` int(11) NOT NULL,
  \`cdo_branch\` int(11) NOT NULL,
  \`cdo_created\` bigint(20) NOT NULL,
  \`cdo_revised\` bigint(20) NOT NULL,
  \`cdo_resource\` bigint(20) NOT NULL,
  \`cdo_container\` bigint(20) NOT NULL,
  \`cdo_feature\` int(11) NOT NULL,
  \`versionId\` varchar(2048) DEFAULT NULL,
  \`description\` varchar(2048) DEFAULT NULL,
  \`parentBranchPath\` varchar(2048) DEFAULT NULL,
  \`effectiveDate\` timestamp NULL DEFAULT NULL,
  \`importDate\` timestamp NULL DEFAULT NULL,
  \`lastUpdateDate\` timestamp NULL DEFAULT NULL,
  UNIQUE KEY \`terminologymetadata_CodeSystemVersion_idx0\` (\`cdo_id\`,\`cdo_version\`,\`cdo_branch\`),
  KEY \`terminologymetadata_CodeSystemVersion_idx1\` (\`cdo_id\`,\`cdo_revised\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; "

${MYSQL} -u${USER} -p${PASSWORD} -e "${COMMAND}"

echo -e "\tCreating new Code System tables process is complete."

echo -e "Snow Owl migration procedure phase 1 - Table creation is complete."
