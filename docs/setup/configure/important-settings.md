# Important Snow Owl configuration

While Snow Owl requires very little configuration, there are a number of settings which need to be considered before going into production.

The following settings **must** be considered before going to production:

* Path settings
* Network settings
* Heap size

## Path settings

If you are using the `.zip` or `.tar.gz` archives, the data and logs directories are sub-folders of `$SO_HOME`. If these important folders are left in their default locations, there is a high risk of them being deleted while upgrading Snow Owl to a new version.

In production use, you will almost certainly want to change the locations of the data and log folders.


```yml
path:
  data: /var/data/snowowl
```

The RPM and Debian distributions already use custom paths for data and logs.

## Network settings

TODO

## Setting the heap size

By default, Snow Owl tells the JVM to use a heap with a minimum and maximum size of 2 GB. When moving to production, it is important to configure heap size to ensure that Snow Owl has enough heap available.

To configure the heap size settings, change the `-Xms` and `-Xmx` settings in the `SO_JAVA_OPTS` environment variable.

```bash
SO_JAVA_OPTS="-Xms12g -Xmx12g" ./bin/startup # Set the minimum and maximum heap size to 12 GB.
```

The value for these setting depends on the amount of RAM available on your server. Good rules of thumb are:

* Set the minimum heap size (`Xms`) and maximum heap size (`Xmx`) to be equal to each other.
* Too much heap can subject to long garbage collection pauses.
* Set `Xmx` to no more than 50% of your physical RAM, to ensure that there is enough physical RAM left for kernel file system caches.