# Migration from 6.x version

The following major differences, features and topics are worth mentioning when comparing features present in Snow Owl 6 and 7 and migrating an existing 6.x deployment to Snow Owl 7.x.

NOTE: It is highly recommended to keep the previous Snow Owl 6 deployment up and running until you have the data and all connected services migrated to the new version successfully. The new Snow Owl 7 system should get its own dedicated machine and deployment environment. Rolling back to the previous state should be available and must be executed when the upgrade cannot be performed successfully.

## Java 11

From Snow Owl 7.1, Snow Owl compiles and runs on Java 11+ versions. It is recommended to use the latest OpenJDK or OracleJDK 11 LTS version. Install from [OpenJDK](https://openjdk.java.net/), [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [AdoptJDK](https://adoptopenjdk.net/). The Oracle JDK comes with commercial usage restrictions, see [here](https://www.oracle.com/technetwork/java/javase/overview/oracle-jdk-faqs.html) before installing.

## RDBMS vs Elasticsearch

While Snow Owl 6 was relying on two data sources for reading and writing data, a primary RDBMS (MySQL) for writing and a secondary Elasticsearch index for full-text search, queries and quick access, Snow Owl 7 in the other hand requires only a single data source, an Elasticsearch cluster. 

If you were using an external Elasticsearch cluster then we recommended installing the new Elasticsearch 7.x version first, then installing Snow Owl 7.x and finally connecting the two (or using the appropriate Docker images).
If you were using the embedded version, then installing the new Snow Owl 7 version is enough.

After the migration, the MySQL software dependency can be uninstalled from the machine if there are no other services depending on it.

## Database content

Due to schema changes the old content present in the RDBMS and index cannot be used by a Snow Owl 7 installation. To migrate an existing dataset to the new version, perform an export in the old system and use the exported files to import the content back into the new Snow Owl 7 version.

## LDAP Authorization

The new Snow Owl 7 version comes with complete authorization support using JWT authorization tokens. The old `User - Role - Permission` system can be used by performing the following migration steps:

1. Add the administrator permission to all administrator roles: `*:*` 
2. Remove the `unused` permission values from all roles used by Snow Owl
3. Add the `classify:*` permission declaration and assign it to all roles that should be able to run classifications

## Configuration changes

Snow Owl 7 configuration file has been renamed to `snowowl.yml` (from `snowowl_config.yml`) and moved to the `<HOME>/configuration` folder.

The following configuration settings have been changed:
* `repository.database` configuration setting has been removed completely
* `repository.numberOfWorkers` has been renamed to `repository.maxThreads` and its default value became `200`.
* `metrics` settings has been renamed to `monitoring`

Apply these changes to the configuration before starting your Snow Owl Terminology Server.

## Startup and shutdown

The old `startup.bat`, `startup.sh`, `shutdown.bat`, `shutdown.sh` have been replaced with the new `snowowl.sh`, `snowowl.bat` and `shutdown.sh` scripts.

## Packaging

Snow Owl 7 comes in four distribution formats:
* `zip`/`tar.gz` for manual deployments
* `rpm` for `CentOS/RHEL` based Linux system deployments
* `deb` for `Debian` based Linux system deployments
* `docker` for `Docker` based deployments 