#
# Copyright (c) 2019 B2i Healthcare. All rights reserved.
#

# Modifies the query column length to 8192
# Usage: execute each SQL statement.  

USE snomedStore;

ALTER TABLE
  snomedrefset_snomedqueryrefsetmember
MODIFY
  query varchar(8192);