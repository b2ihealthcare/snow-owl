#
# Copyright (c) 2019 B2i Healthcare. All rights reserved.
#
# Creates potentially missing Singleton Concept Set Definition table
# Usage: execute each SQL statement.
​
USE snomedStore;
​
# Create new table mrcm_singletonconceptsetdefinition
​
DROP TABLE IF EXISTS mrcm_singletonconceptsetdefinition;
CREATE TABLE mrcm_singletonconceptsetdefinition (
 cdo_id bigint(20) NOT NULL,
 cdo_version int(11) NOT NULL,
 cdo_branch int(11) NOT NULL,
 cdo_created bigint(20) NOT NULL,
 cdo_revised bigint(20) NOT NULL,
 cdo_resource bigint(20) NOT NULL,
 cdo_container bigint(20) NOT NULL,
 cdo_feature int(11) NOT NULL,
 uuid varchar(2048) DEFAULT NULL,
 active tinyint(1) DEFAULT NULL,
 effectiveTime timestamp DEFAULT 0,
 author varchar(2048) DEFAULT NULL,
 conceptId varchar(2048) DEFAULT NULL,
 UNIQUE KEY mrcm_singletonconceptsetdefinition_idx0 (cdo_id,cdo_version,cdo_branch),
 KEY mrcm_singletonconceptsetdefinition_idx1 (cdo_id,cdo_revised)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
​
INSERT INTO cdo_external_refs SELECT -(COUNT(*)+1), "http://b2international.com/snowowl/snomed/mrcm#//SingletonConceptSetDefinition", -1 FROM cdo_external_refs;
INSERT INTO cdo_external_refs SELECT -(COUNT(*)+1), "http://b2international.com/snowowl/snomed/mrcm#//SingletonConceptSetDefinition/conceptId", -1 FROM cdo_external_refs;