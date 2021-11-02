# Migration from 7.x version

The following major differences, features and topics are worth mentioning when comparing features present in Snow Owl 7 and 8 and migrating an existing 7.x deployment to Snow Owl 8.x.

NOTE: It is highly recommended to keep the previous Snow Owl 7 deployment up and running until you have the data and all connected services migrated to the new version successfully. The new Snow Owl 8 system should get its own dedicated machine and deployment environment. Rolling back to the previous state should be available and must be executed when the upgrade cannot be performed successfully.

## Database content

Due to resource and access management schema changes the old content present in a 7.x index cannot be used by a Snow Owl 8 installation. To migrate an existing dataset to the new version, perform an export in the old system and use the exported files to import the content back into the new Snow Owl 8 version.

## Configuration changes

The following configuration settings have been changed:
* Most of the `snomed` configuration keys have been added to runtime settings under the `CodeSystem.settings` property. If you have been using any of these configuration values, please raise a ticket [here](https://github.com/b2ihealthcare/snow-owl/issues/new/choose) and we will help you migrate your current installation to the new version.
