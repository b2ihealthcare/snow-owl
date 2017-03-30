#
# Copyright (c) 2013-2016 B2i Healthcare. All rights reserved.
#

# Migrates the content of snomedStore from version 4.6 to 4.7.
# Usage: execute each SQL statement.  

USE snomedStore;

# Create new table terminologymetadata_codesystem

DROP TABLE IF EXISTS `terminologymetadata_codesystem`;
CREATE TABLE `terminologymetadata_codesystem` (
  `cdo_id` bigint(20) NOT NULL,
  `cdo_version` int(11) NOT NULL,
  `cdo_branch` int(11) NOT NULL,
  `cdo_created` bigint(20) NOT NULL,
  `cdo_revised` bigint(20) NOT NULL,
  `cdo_resource` bigint(20) NOT NULL,
  `cdo_container` bigint(20) NOT NULL,
  `cdo_feature` int(11) NOT NULL,
  `shortName` varchar(2048) DEFAULT NULL,
  `codeSystemOID` varchar(255) DEFAULT NULL,
  `name` varchar(2048) DEFAULT NULL,
  `maintainingOrganizationLink` varchar(2048) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `citation` varchar(2048) DEFAULT NULL,
  `iconPath` varchar(2048) DEFAULT NULL,
  `terminologyComponentId` varchar(2048) DEFAULT NULL,
  `repositoryUuid` varchar(255) DEFAULT NULL,
  `branchPath` varchar(2048) DEFAULT NULL,
  `extensionOf` bigint(20) DEFAULT NULL,
  `codeSystemVersions` int(11) DEFAULT NULL,
  UNIQUE KEY `terminologymetadata_CodeSystem_idx0` (`cdo_id`,`cdo_version`,`cdo_branch`),
  KEY `terminologymetadata_CodeSystem_idx1` (`cdo_id`,`cdo_revised`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

# Insert data from snomed_codesystem to terminologymetadata_codesystem

INSERT INTO `terminologymetadata_codesystem` (`cdo_id`, `cdo_version`, `cdo_branch`, `cdo_created`, `cdo_revised`, `cdo_resource`, `cdo_container`, `cdo_feature`, `shortName`, `codeSystemOID`, `name`, `maintainingOrganizationLink`, `language`, `citation`, `iconPath`, `terminologyComponentId`)
    SELECT `cdo_id`, `cdo_version`, `cdo_branch`, `cdo_created`, `cdo_revised`, `cdo_resource`, `cdo_container`, `cdo_feature`, `shortName`, `codeSystemOID`, `name`, `maintainingOrganizationLink`, `language`, `citation`, `iconPath`, `terminologyComponentId`
    FROM `snomed_codesystem`;

# Update cdo_container, repositoryUuid and branchPath columns with default values

UPDATE	`terminologymetadata_codesystem`
SET `cdo_container` = 0, `repositoryUuid` = "snomedStore", `branchPath` = "MAIN";

# Update codeSystemVersions column with the number of existing versions

UPDATE `terminologymetadata_codesystem` 
SET `codeSystemVersions` = (SELECT COUNT(*) 
	FROM `snomed_codesystem`, `snomed_codesystemversion`
	WHERE `snomed_codesystem`.`cdo_container` = `snomed_codesystemversion`.`cdo_container`);

# Set cdo_resource to meta snomed 

UPDATE `terminologymetadata_codesystem` 
SET `cdo_resource` = (SELECT `g`.`cdo_resource`
	FROM `snomed_codesystem` c, `snomed_codesystemversiongroup` g
	WHERE `c`.`cdo_container` = `g`.`cdo_id`
	LIMIT 1);

# Create new table terminologymetadata_codesystem_codesystemversions_list

DROP TABLE IF EXISTS `terminologymetadata_codesystem_codesystemversions_list`;
CREATE TABLE `terminologymetadata_codesystem_codesystemversions_list` (
  `cdo_source` bigint(20) DEFAULT NULL,
  `cdo_branch` int(11) DEFAULT NULL,
  `cdo_version_added` int(11) DEFAULT NULL,
  `cdo_version_removed` int(11) DEFAULT NULL,
  `cdo_idx` int(11) DEFAULT NULL,
  `cdo_value` bigint(20) DEFAULT NULL,
  KEY `terminologymetadata_CodeSystem_codeSystemVersions_list_idx0` (`cdo_source`),
  KEY `terminologymetadata_CodeSystem_codeSystemVersions_list_idx1` (`cdo_branch`),
  KEY `terminologymetadata_CodeSystem_codeSystemVersions_list_idx2` (`cdo_version_added`),
  KEY `terminologymetadata_CodeSystem_codeSystemVersions_list_idx3` (`cdo_version_removed`),
  KEY `terminologymetadata_CodeSystem_codeSystemVersions_list_idx4` (`cdo_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

# Insert data from snomed_codesystemversiongroup_codesystemversions_list to terminologymetadata_codesystem_codesystemversions_list

INSERT INTO `terminologymetadata_codesystem_codesystemversions_list` (`cdo_source`, `cdo_branch`, `cdo_version_added`, `cdo_version_removed`, `cdo_idx`, `cdo_value`)
	SELECT `cdo_source`, `cdo_branch`, `cdo_version_added`, `cdo_version_removed`, `cdo_idx`, `cdo_value`
	FROM `snomed_codesystemversiongroup_codesystemversions_list`;

# Set the cdo_source to the cdo ID of the snomed code system
	
UPDATE `terminologymetadata_codesystem_codesystemversions_list` 
SET `cdo_source` = (SELECT `snomed_codesystem`.`cdo_id`
	FROM `snomed_codesystem`, `snomed_codesystemversion`
	WHERE `snomed_codesystem`.`cdo_container` = `snomed_codesystemversion`.`cdo_container`
	LIMIT 1);

# Set the cdo_version_added to the version of snomed code system

UPDATE `terminologymetadata_codesystem_codesystemversions_list` 
SET `cdo_version_added` = (SELECT `snomed_codesystem`.`cdo_version`
	FROM `snomed_codesystem`, `snomed_codesystemversion`
	WHERE `snomed_codesystem`.`cdo_container` = `snomed_codesystemversion`.`cdo_container`
	LIMIT 1);

# Create new table terminologymetadata_codesystemversion

DROP TABLE IF EXISTS `terminologymetadata_codesystemversion`;
CREATE TABLE `terminologymetadata_codesystemversion` (
  `cdo_id` bigint(20) NOT NULL,
  `cdo_version` int(11) NOT NULL,
  `cdo_branch` int(11) NOT NULL,
  `cdo_created` bigint(20) NOT NULL,
  `cdo_revised` bigint(20) NOT NULL,
  `cdo_resource` bigint(20) NOT NULL,
  `cdo_container` bigint(20) NOT NULL,
  `cdo_feature` int(11) NOT NULL,
  `versionId` varchar(2048) DEFAULT NULL,
  `description` varchar(2048) DEFAULT NULL,
  `parentBranchPath` varchar(2048) DEFAULT NULL,
  `effectiveDate` timestamp NULL DEFAULT NULL,
  `importDate` timestamp NULL DEFAULT NULL,
  `lastUpdateDate` timestamp NULL DEFAULT NULL,
  UNIQUE KEY `terminologymetadata_CodeSystemVersion_idx0` (`cdo_id`,`cdo_version`,`cdo_branch`),
  KEY `terminologymetadata_CodeSystemVersion_idx1` (`cdo_id`,`cdo_revised`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

# Insert data from snomed_codesystemversion to terminologymetadata_codesystemversion

INSERT INTO `terminologymetadata_codesystemversion` (`cdo_id`, `cdo_version`, `cdo_branch`, `cdo_created`, `cdo_revised`, `cdo_resource`, `cdo_container`, `cdo_feature`, `versionId`, `description`, `effectiveDate`, `importDate`, `lastUpdateDate`)
	SELECT `cdo_id`, `cdo_version`, `cdo_branch`, `cdo_created`, `cdo_revised`, `cdo_resource`, `cdo_container`, `cdo_feature`, `versionId`, `description`, `effectiveDate`, `importDate`, `lastUpdateDate`
	FROM `snomed_codesystemversion`;

# Set the parentBranchPath to MAIN

UPDATE `terminologymetadata_codesystemversion` 
SET `parentBranchPath` = "MAIN";

# Set the cdo_container to the cdo ID of the snomed code system

UPDATE `terminologymetadata_codesystemversion` 
SET `cdo_container` = (SELECT `snomed_codesystem`.`cdo_id`
	FROM `snomed_codesystem`, `snomed_codesystemversion`
	WHERE `snomed_codesystem`.`cdo_container` = `snomed_codesystemversion`.`cdo_container`
	LIMIT 1);

# Update eresource_cdoresource_contents_list table

UPDATE `eresource_cdoresource_contents_list`
SET `cdo_value` = (SELECT `c`.`cdo_id` FROM `terminologymetadata_codesystem` c, `snomed_codesystemversiongroup` g WHERE `c`.`cdo_resource` = `g`.`cdo_resource` LIMIT 1)
WHERE `cdo_source` = (SELECT `c`.`cdo_resource` FROM `terminologymetadata_codesystem` c, `snomed_codesystemversiongroup` g WHERE `c`.`cdo_resource` = `g`.`cdo_resource` LIMIT 1);

# Insert new values (new/updated Code System and Version fields) into cdo_external_refs

INSERT INTO `cdo_external_refs` SELECT -(COUNT(*)+1), "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersion", -1 FROM cdo_external_refs;
INSERT INTO `cdo_external_refs` SELECT -(COUNT(*)+1), "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersion/parentBranchPath", -1 FROM cdo_external_refs;
INSERT INTO `cdo_external_refs` SELECT -(COUNT(*)+1), "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem", -1 FROM cdo_external_refs;
INSERT INTO `cdo_external_refs` SELECT -(COUNT(*)+1), "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem/repositoryUuid", -1 FROM cdo_external_refs;
INSERT INTO `cdo_external_refs` SELECT -(COUNT(*)+1), "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem/branchPath", -1 FROM cdo_external_refs;
INSERT INTO `cdo_external_refs` SELECT -(COUNT(*)+1), "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem/extensionOf", -1 FROM cdo_external_refs;
INSERT INTO `cdo_external_refs` SELECT -(COUNT(*)+1), "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem/codeSystemVersions", -1 FROM cdo_external_refs;

# Set the new cdo_class based on the previously updated cdo_external_refs

UPDATE `cdo_objects` SET `cdo_class` = (SELECT `id` FROM `cdo_external_refs` WHERE `uri` = "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersion")
  WHERE `cdo_class` = (SELECT `id` FROM `cdo_external_refs` WHERE `uri` = "http://b2international.com/snowowl/sct/1.0#//CodeSystemVersion");
UPDATE `cdo_objects` SET `cdo_class` = (SELECT `id` FROM `cdo_external_refs` WHERE `uri` = "urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystem")
  WHERE `cdo_class` = (SELECT `id` FROM `cdo_external_refs` WHERE `uri` = "http://b2international.com/snowowl/sct/1.0#//CodeSystem");

# Delete old cdo_external_refs rows

DELETE FROM `cdo_external_refs` WHERE uri="http://b2international.com/snowowl/sct/1.0#//CodeSystemVersionGroup";
DELETE FROM `cdo_external_refs` WHERE uri="urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersionGroup/repositoryUuid";
DELETE FROM `cdo_external_refs` WHERE uri="urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersionGroup/codeSystems";
DELETE FROM `cdo_external_refs` WHERE uri="urn:com:b2international:snowowl:terminologymetadata:model#//CodeSystemVersionGroup/codeSystemVersions";
DELETE FROM `cdo_external_refs` WHERE uri="http://b2international.com/snowowl/sct/1.0#//CodeSystemVersion";
DELETE FROM `cdo_external_refs` WHERE uri="http://b2international.com/snowowl/sct/1.0#//CodeSystem";

# Drop old, unused tables

DROP TABLE IF EXISTS `snomed_codesystem`;
DROP TABLE IF EXISTS `snomed_codesystemversion`;
DROP TABLE IF EXISTS `snomed_codesystemversiongroup`;
DROP TABLE IF EXISTS `snomed_codesystemversiongroup_codesystems_list`;
DROP TABLE IF EXISTS `snomed_codesystemversiongroup_codesystemversions_list`;
