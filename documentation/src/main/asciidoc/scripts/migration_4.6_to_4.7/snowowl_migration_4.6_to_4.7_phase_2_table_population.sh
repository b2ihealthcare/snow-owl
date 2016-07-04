#!/usr/bin/env bash

#
# Copyright (c) 2013-2016 B2i Healthcare. All rights reserved.
#

# Populates the new Code System tables for the terminology.

# Usage: ./snowowl_migration_4.6_to_4.7_phase_2_table_population.sh <password> <terminology> <codeSystemShortName> <codeSystemAbbreviation>
# E.g. ./snowowl_migration_4.6_to_4.7_phase_2_table_population.sh admin snomed SNOMEDCT sct

PASSWORD=$1
TERMINOLOGY=$2
CODE_SYSTEM_SHORT_NAME=$3
CODE_SYSTEM_ABBR=$4

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

if [ "x$CODE_SYSTEM_SHORT_NAME" = "x" ]; then
  echo -e "Please set the code system short name (e.g. SNOMEDCT) before running this script. Exiting with error."
  exit 1
fi

if [ "x$CODE_SYSTEM_ABBR" = "x" ]; then
  echo -e "Please set the code system abbreviation (e.g. sct) before running this script. Exiting with error."
  exit 1
fi

COMMAND=""

echo -e "Starting Snow Owl migration procedure phase 2 - Table population."

echo -e "\t1. Populating terminologymetadata_codesystem table."

COMMAND="${COMMAND} USE ${TERMINOLOGY}Store;"

COMMAND="${COMMAND} INSERT INTO \`terminologymetadata_codesystem\` (\`cdo_id\`, \`cdo_version\`, \`cdo_branch\`, \`cdo_created\`, \`cdo_revised\`, \`cdo_resource\`, \`cdo_container\`, \`cdo_feature\`, \`shortName\`, \`codeSystemOID\`, \`name\`, \`maintainingOrganizationLink\`, \`language\`, \`citation\`, \`iconPath\`, \`terminologyComponentId\`)
  SELECT \`cdo_id\`, \`cdo_version\`, \`cdo_branch\`, \`cdo_created\`, \`cdo_revised\`, \`cdo_resource\`, \`cdo_container\`, \`cdo_feature\`, \`shortName\`, \`codeSystemOID\`, \`name\`, \`maintainingOrganizationLink\`, \`language\`, \`citation\`, \`iconPath\`, \`terminologyComponentId\`
  FROM \`${TERMINOLOGY}_codesystem\` c
  WHERE \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\";"

COMMAND="${COMMAND} UPDATE \`terminologymetadata_codesystem\`
  SET \`cdo_container\` = 0, \`repositoryUuid\` = \"${TERMINOLOGY}Store\", \`branchPath\` = \"MAIN\"
  WHERE \`terminologymetadata_codesystem\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\";"

COMMAND="${COMMAND} UPDATE \`terminologymetadata_codesystem\` 
  SET \`codeSystemVersions\` = (SELECT COUNT(*) 
  FROM \`${TERMINOLOGY}_codesystem\` c, \`${TERMINOLOGY}_codesystemversion\` v
  WHERE \`c\`.\`cdo_container\` = \`v\`.\`cdo_container\` AND \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\")
  WHERE \`terminologymetadata_codesystem\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\";"

COMMAND="${COMMAND} UPDATE \`terminologymetadata_codesystem\` 
  SET \`cdo_resource\` = (SELECT \`g\`.\`cdo_resource\`
  FROM \`${TERMINOLOGY}_codesystem\` c, \`${TERMINOLOGY}_codesystemversiongroup\` g
  WHERE \`c\`.\`cdo_container\` = \`g\`.\`cdo_id\`
  LIMIT 1);"

${MYSQL} -u${USER} -p${PASSWORD} -e "${COMMAND}"

echo -e "\tPopulating terminologymetadata_codesystem table process is complete."


echo -e "\t2. Populating terminologymetadata_codesystem_codesystemversions_list table."

COMMAND="USE ${TERMINOLOGY}Store;"

COMMAND="${COMMAND} INSERT INTO \`terminologymetadata_codesystem_codesystemversions_list\` (\`cdo_source\`, \`cdo_branch\`, \`cdo_version_added\`, \`cdo_version_removed\`, \`cdo_idx\`, \`cdo_value\`)
  SELECT \`l\`.\`cdo_source\`, \`l\`.\`cdo_branch\`, \`l\`.\`cdo_version_added\`, \`l\`.\`cdo_version_removed\`, \`l\`.\`cdo_idx\`, \`l\`.\`cdo_value\`
  FROM \`${TERMINOLOGY}_codesystemversiongroup_codesystemversions_list\` l, \`${TERMINOLOGY}_codesystem\` c
  WHERE \`l\`.\`cdo_source\` = \`c\`.\`cdo_container\` AND \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\";"
  
COMMAND="${COMMAND} UPDATE \`terminologymetadata_codesystem_codesystemversions_list\` 
  SET \`cdo_source\` = (SELECT \`c\`.\`cdo_id\`
  FROM \`${TERMINOLOGY}_codesystem\` c, \`${TERMINOLOGY}_codesystemversion\` v
  WHERE \`c\`.\`cdo_container\` = \`v\`.\`cdo_container\` AND \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\"
  LIMIT 1);"
  
COMMAND="${COMMAND} UPDATE \`terminologymetadata_codesystem_codesystemversions_list\` 
  SET \`cdo_version_added\` = (SELECT \`c\`.\`cdo_version\`
  FROM \`${TERMINOLOGY}_codesystem\` c, \`${TERMINOLOGY}_codesystemversion\` v
  WHERE \`c\`.\`cdo_container\` = \`v\`.\`cdo_container\` AND \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\"
  LIMIT 1);"
  
${MYSQL} -u${USER} -p${PASSWORD} -e "${COMMAND}"

echo -e "\tPopulating terminologymetadata_codesystem_codesystemversions_list table process is complete."


echo -e "\t3. Populating terminologymetadata_codesystemversion table."

COMMAND="USE ${TERMINOLOGY}Store;"

COMMAND="${COMMAND} INSERT INTO \`terminologymetadata_codesystemversion\` (\`cdo_id\`, \`cdo_version\`, \`cdo_branch\`, \`cdo_created\`, \`cdo_revised\`, \`cdo_resource\`, \`cdo_container\`, \`cdo_feature\`, \`versionId\`, \`description\`, \`effectiveDate\`, \`importDate\`, \`lastUpdateDate\`)
  SELECT \`v\`.\`cdo_id\`, \`v\`.\`cdo_version\`, \`v\`.\`cdo_branch\`, \`v\`.\`cdo_created\`, \`v\`.\`cdo_revised\`, \`v\`.\`cdo_resource\`, \`v\`.\`cdo_container\`, \`v\`.\`cdo_feature\`, \`v\`.\`versionId\`, \`v\`.\`description\`, \`v\`.\`effectiveDate\`, \`v\`.\`importDate\`, \`v\`.\`lastUpdateDate\`
  FROM \`${TERMINOLOGY}_codesystemversion\` v, \`${TERMINOLOGY}_codesystem\` c
  WHERE \`c\`.\`cdo_container\` = \`v\`.\`cdo_container\` AND \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\";"

COMMAND="${COMMAND} UPDATE \`terminologymetadata_codesystemversion\` 
  SET \`parentBranchPath\` = \"MAIN\";"

COMMAND="${COMMAND} UPDATE \`terminologymetadata_codesystemversion\` 
  SET \`cdo_container\` = (SELECT \`c\`.\`cdo_id\`
  FROM \`${TERMINOLOGY}_codesystem\` c, \`${TERMINOLOGY}_codesystemversion\` v
  WHERE \`c\`.\`cdo_container\` = \`v\`.\`cdo_container\` AND \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\"
  LIMIT 1);"

${MYSQL} -u${USER} -p${PASSWORD} -e "${COMMAND}"

echo -e "\tPopulating terminologymetadata_codesystemversion table process is complete."

echo -e "\t4. Updating eresource_cdoresource_contents_list table."

COMMAND="USE ${TERMINOLOGY}Store;"

COMMAND="${COMMAND} UPDATE \`eresource_cdoresource_contents_list\`
  SET \`cdo_value\` = (SELECT \`c\`.\`cdo_id\` FROM \`terminologymetadata_codesystem\` c, \`${TERMINOLOGY}_codesystemversiongroup\` g WHERE \`c\`.\`cdo_resource\` = \`g\`.\`cdo_resource\` AND \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\" LIMIT 1)
  WHERE \`cdo_source\` = (SELECT \`c\`.\`cdo_resource\` FROM \`terminologymetadata_codesystem\` c, \`${TERMINOLOGY}_codesystemversiongroup\` g WHERE \`c\`.\`cdo_resource\` = \`g\`.\`cdo_resource\` AND \`c\`.\`shortName\` = \"${CODE_SYSTEM_SHORT_NAME}\" LIMIT 1);"

${MYSQL} -u${USER} -p${PASSWORD} -e "${COMMAND}"

echo -e "\tUpdating eresource_cdoresource_contents_list table process is complete."

echo -e "\t5. Updating cdo_external_refs and cdo_objects tables."

COMMAND="USE ${TERMINOLOGY}Store;"

COMMAND="${COMMAND} INSERT INTO \`cdo_external_refs\` SELECT -(COUNT(*)+1), \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersion\", -1 FROM \`cdo_external_refs\`;"

COMMAND="${COMMAND} INSERT INTO \`cdo_external_refs\` SELECT -(COUNT(*)+1), \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersion/parentBranchPath\", -1 FROM \`cdo_external_refs\`;"

COMMAND="${COMMAND} INSERT INTO \`cdo_external_refs\` SELECT -(COUNT(*)+1), \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem\", -1 FROM \`cdo_external_refs\`;"

COMMAND="${COMMAND} INSERT INTO \`cdo_external_refs\` SELECT -(COUNT(*)+1), \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem/repositoryUuid\", -1 FROM \`cdo_external_refs\`;"

COMMAND="${COMMAND} INSERT INTO \`cdo_external_refs\` SELECT -(COUNT(*)+1), \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem/branchPath\", -1 FROM \`cdo_external_refs\`;"

COMMAND="${COMMAND} INSERT INTO \`cdo_external_refs\` SELECT -(COUNT(*)+1), \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem/extensionOf\", -1 FROM \`cdo_external_refs\`;"

COMMAND="${COMMAND} INSERT INTO \`cdo_external_refs\` SELECT -(COUNT(*)+1), \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem/codeSystemVersions\", -1 FROM \`cdo_external_refs\`;"

COMMAND="${COMMAND} UPDATE \`cdo_objects\` SET \`cdo_class\` = (SELECT \`id\` FROM \`cdo_external_refs\` WHERE \`uri\` = \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersion\")
  WHERE \`cdo_class\` = (SELECT \`id\` FROM \`cdo_external_refs\` WHERE \`uri\` = \"http://b2international.com/snowowl/${CODE_SYSTEM_ABBR}/1.0#//CodeSystemVersion\");"

COMMAND="${COMMAND} UPDATE \`cdo_objects\` SET \`cdo_class\` = (SELECT \`id\` FROM \`cdo_external_refs\` WHERE \`uri\` = \"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem\")
  WHERE \`cdo_class\` = (SELECT \`id\` FROM \`cdo_external_refs\` WHERE \`uri\` = \"http://b2international.com/snowowl/${CODE_SYSTEM_ABBR}/1.0#//CodeSystem\");"

COMMAND="${COMMAND} DELETE FROM \`cdo_external_refs\` WHERE \`uri\`=\"http://b2international.com/snowowl/${CODE_SYSTEM_ABBR}/1.0#//CodeSystemVersionGroup\";"

COMMAND="${COMMAND} DELETE FROM \`cdo_external_refs\` WHERE \`uri\`=\"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersionGroup/repositoryUuid\";"

COMMAND="${COMMAND} DELETE FROM \`cdo_external_refs\` WHERE \`uri\`=\"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersionGroup/codeSystems\";"

COMMAND="${COMMAND} DELETE FROM \`cdo_external_refs\` WHERE \`uri\`=\"urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersionGroup/codeSystemVersions\";"

COMMAND="${COMMAND} DELETE FROM \`cdo_external_refs\` WHERE \`uri\`=\"http://b2international.com/snowowl/${CODE_SYSTEM_ABBR}/1.0#//CodeSystemVersion\";"

COMMAND="${COMMAND} DELETE FROM \`cdo_external_refs\` WHERE \`uri\`=\"http://b2international.com/snowowl/${CODE_SYSTEM_ABBR}/1.0#//CodeSystem\";"

${MYSQL} -u${USER} -p${PASSWORD} -e "${COMMAND}"

echo -e "\tUpdating cdo_external_refs and cdo_objects tables process is complete."

echo -e "Snow Owl migration procedure phase 2 - Table population is complete."
