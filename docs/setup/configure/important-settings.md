# Important Snow Owl configuration

While Snow Owl requires very little configuration, there are a number of settings which need to be considered before going into production.

The following settings **must** be considered before going to production:

## Elasticsearch settings

By default, Snow Owl includes the OSS version of Elasticsearch and runs it in embedded mode to store terminology data and make it available for search. This is convenient for single node environments (eg. for evaluation, testing and development), but it might not be sufficient when you go into production.

To configure Snow Owl to connect to an Elasticsearch cluster, change the `clusterUrl` property in the `snowowl.yml` configuration file:

```yml
repository:
  index:
    clusterUrl: http://your.es.cluster:9200 # the ES cluster URL
    clusterUsername: snowowl # Optional username to connect to a protected ES cluster
    clusterPassword: snowowl_password # Optional password to connect to a protected ES cluster
```

The value for this setting should be a valid HTTP URL point to the HTTP API of your Elasticsearch cluster, which by default runs on port `9200`.

## Path settings

If you are using the `.zip` or `.tar.gz` archives, the data and logs directories are sub-folders of `$SO_HOME`. If these important folders are left in their default locations, there is a high risk of them being deleted while upgrading Snow Owl to a new version.

In production use, you will almost certainly want to change the locations of the data and log folders.

```yml
path:
  data: /var/data/snowowl
```

The RPM and Debian distributions already use custom paths for data and logs.

## Network settings

To allow clients to connect to Snow Owl, make sure you open access to the following ports:
* 8080/TCP:: Used by Snow Owl Server's REST API for HTTP access
* 8443/TCP:: Used by Snow Owl Server's REST API for HTTPS access
* 2036/TCP:: Used by the Net4J binary protocol connecting Snow Owl clients to the server

## Setting the heap size

By default, Snow Owl tells the JVM to use a heap with a minimum and maximum size of 2 GB. When moving to production, it is important to configure heap size to ensure that Snow Owl has enough heap available.

To configure the heap size settings, change the `-Xms` and `-Xmx` settings in the `SO_JAVA_OPTS` environment variable.

```bash
# Set the minimum and maximum heap size to 12 GB.
SO_JAVA_OPTS="-Xms12g -Xmx12g" ./bin/startup
```

The value for these setting depends on the amount of RAM available on your server and whether you are running Elasticsearch on the some node as Snow Owl (either embedded or as a service) or running it in its own cluster. Good rules of thumb are:

* Set the minimum heap size (`Xms`) and maximum heap size (`Xmx`) to be equal to each other.
* Too much heap can subject to long garbage collection pauses.
* Set `Xmx` to no more than 50% of your physical RAM, to ensure that there is enough physical RAM left for kernel file system caches.
* Snow Owl connecting to a remote Elasticsearch cluster requires less memory, but make sure you still allocate enough for your use cases (classification, batch processing, etc.).