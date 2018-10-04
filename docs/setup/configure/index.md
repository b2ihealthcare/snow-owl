# Configuring Snow Owl

Snow Owl ships with good defaults and requires very little configuration.

## Config files location
Snow Owl has three configuration files:

* `snowowl.yml` for configuring Snow Owl
* `serviceability.xml` for configuring Snow Owl logging
* `elasticsearch.yml` for configuring the underlying Elasticsearch instance in case of embedded deployments

These files are located in the config directory, whose default location depends on whether or not the installation is from an archive distribution (`tar.gz` or `zip`) or a package distribution (Debian or RPM packages).

For the archive distributions, the config directory location defaults to `$SO_PATH_HOME/config`. The location of the config directory can be changed via the `SO_PATH_CONF` environment variable as follows:

```
SO_PATH_CONF=/path/to/my/config ./bin/startup
```

Alternatively, you can export the SO_PATH_CONF environment variable via the command line or via your shell profile.

For the package distributions, the config directory location defaults to `/etc/snowowl`. The location of the config directory can also be changed via the `SO_PATH_CONF` environment variable, but note that setting this in your shell is not sufficient. Instead, this variable is sourced from `/etc/default/snowowl` (for the Debian package) and `/etc/sysconfig/snowowl` (for the RPM package). You will need to edit the `SO_PATH_CONF=/etc/snowowl` entry in one of these files accordingly to change the config directory location.

## Config file format

The configuration format is [YAML](http://www.yaml.org/). Here is an example of changing the path of the data directory:

```
path:
    data: /var/lib/snowowl
```

Settings can also be flattened as follows:

```
path.data: /var/lib/snowowl
```

### Environment variable substitution

Environment variables referenced with the `${...}` notation within the configuration file will be replaced with the value of the environment variable, for instance:

```
repository.host: ${HOSTNAME}
repository.port: ${SO_REPOSITORY_PORT}
```