# Snow Owl migration to 6.21.0

This guide describes the migration steps need to be taken when migrating an existing Snow Owl server application from any version prior to 6.21.0 to 6.21.0.

## Migration steps

1. Before shutting down the current Snow Owl instance delete the jobs index by executing the following command `curl -XDELETE localhost:9200/jobs-job`
2. Shut down the Snow Owl instance
3. Migrate the `snomedStore` RDBMS database by executing the `snomedStore_6.21.0.sql` script
4. Upgrade the Snow Owl server installation to 6.21.0
5. Move the resources directory contents to the newly unzipped 6.21.0 location (or create a symlink to an existing resources directory)
6. Start the Snow Owl instance 
