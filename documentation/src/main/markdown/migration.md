# Data migration guide

## Introduction

This document lists the required data migration steps for each release.

## 5.0.0

Follow these intructions to migrate datasets created prior to 5.0.0:

> Warning | Make sure you execute the data migration process on a backup first before you try to migrate your production servers!

### Steps

 1. Stop the currently running snow-owl and deploy a new v5.0.0 version
 
 2.  Backup the <SO_DATADIR>/indexes directory for recovery in case of migration failure
 
 3. Change the following configuration values in **snowowl_config.yml** file:
		i. Set *commitInterval* from default *15 seconds* to *15 minutes* (value *900000*)
		ii. Set *translogSyncInterval* from default *5 seconds* to *5 minutes* (value *300000*)
		iii. Set *revisionCache* to false
		iv. You can find an example configuration in the **configuration/migration/5.0** subdirectory
		
4. Change memory settings to fixed **12g** in the *<SO_HOME>/bin/dmk.sh* file with JVM configuration *-Xms12g -Xmx12g*
 
5. Start snow-owl v5.0.0
 
6. Connect to the OSGi console via *telnet localhost 2501*

7. Start the migration process with the command  *snowowl reindex <repositoryId>*. For SNOMED CT, the repository identifier is  *snomedStore*.

### Troubleshooting

If the migration process fails, it prints out relevant messages in the console and the logs. The printed message contains information about the failed commit, with its commit timestamp. You can (re)start the migration process from the failed commit timestamp, if you specify the commit time at the end of the command as follows, *snowowl reindex snomedStore* *< failedCommitTimestamp>*.

> Note | It is recommended to stop and restart the server before continuing a failed migration process.

## 4.7.0

There are two ways to migrate data from 4.6 to 4.7. Either run the provided SQL statements against the database or use the shell scripts which migrate the data in three steps.

### SQL

The  `migration_4.6_to_4.7/snowowl_migration_4.6_to_4.7_terminology_snomed.sql`  file contains the necessary SQL statements for the migration. Execute each SQL statement against the database to migrate your data.

### Shell scripts

The  `migration_4.6_to_4.7`  folder contains three shell scripts for the migration.

The first script  `snowowl_migration_4.6_to_4.7_phase_1_table_creation.sh`  creates the new Code System tables for the given terminology. Use the following command to run the script:

    ./migration_4.6_to_4.7/snowowl_migration_4.6_to_4.7_phase_1_table_creation.sh admin snomed

The second script `snowowl_migration_4.6_to_4.7_phase_2_table_population.sh` populates the previously created tables with the necessary data. Use the following command to run the script:

    ./migration_4.6_to_4.7/snowowl_migration_4.6_to_4.7_phase_2_table_population.sh admin snomed SNOMEDCT sct

The third script `snowowl_migration_4.6_to_4.7_phase_3_old_table_deletion.sh` deletes the old Code System tables which are no longer needed. Use the following command to run the script:

    ./migration_4.6_to_4.7/snowowl_migration_4.6_to_4.7_phase_3_old_table_deletion.sh admin snomed
