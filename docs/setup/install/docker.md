# Install Snow Owl with Docker

Snow Owl is also available as Docker images. The images use [centos:7](https://hub.docker.com/_/centos/) as the base image.

A list of all published Docker images and tags is available at [Docker Hub](https://hub.docker.com/r/b2ihealthcare/snow-owl-oss/).

These images are free to use under the Apache 2.0 license. They contain open source features only.

## Pulling the image

Obtaining Snow Owl for Docker is as simple as issuing a `docker pull` command against the Docker Hub registry.

```bash
docker pull snow-owl-oss:latest
```

## Running Snow Owl from the command line

### Development mode

Snow Owl can be quickly started for development or testing use with the following command:

```bash
docker run -p 8080:8080 snow-owl-oss:latest
```

### Production mode

{% hint style="info" %}
The `vm.max_map_count` kernel setting needs to be set to at least `262144` permanently in `/etc/sysctl.conf` for production use.
To apply the setting on a live system type: `sysctl -w vm.max_map_count=262144`
{% endhint %}

The following example brings up Snow Owl instance with its dedicated Elasticsearch node. 
To bring up the cluster, use the [docker-compose.yml](https://github.com/b2ihealthcare/snow-owl/blob/8.x/docker/docker-compose.yml) and just type:

```bash
docker-compose up
```

{% hint style="info" %}
`docker-compose` is not pre-installed with Docker on Linux. Instructions for installing it can be found on the [Docker Compose webpage](https://docs.docker.com/compose/install/#install-using-pip).
{% endhint %}

The node `snowowl` listens on `localhost:8080` while it talks to the `elasticsearch` node over a Docker network.

To stop the cluster, type `docker-compose down`. Data volumes/mounts will persist, so it's possible to start the stack again with the same data using
docker-compose up`.

## Configuring Snow Owl with Docker

Snow Owl loads its configuration from files under `/usr/share/snowowl/config/`.
These configuration files are documented in the [Configure Snow Owl](../configure/index.md) pages.

The image offers several methods for configuring Snow Owl settings with the
conventional approach being to provide customized files, that is to say,
`snowowl.yml`. It's also possible to use environment variables to set
options:

* A. Bind-mounted configuration
Create your custom config file and mount this over the image's corresponding file.
For example, bind-mounting a `custom_snowowl.yml` with `docker run` can be
accomplished with the parameter:

```bash
-v full_path_to/custom_snowowl.yml:/usr/share/snowowl/configuration/snowowl.yml
```

{% hint style="warn" %}
The container **runs Snow Owl as user `snowowl` using uid:gid `1000:1000`**.
Bind mounted host directories and files, such as `custom_snowowl.yml` above,
**need to be accessible by this user**. For the mounted data and log dirs,
such as `/usr/share/snowowl/resources`, write access is required as well.
{% endhint %}

* B. Customized image
In some environments, it may make more sense to prepare a custom image containing
your configuration. A `Dockerfile` to achieve this may be as simple as:

```bash
FROM snow-owl-oss:{version}
COPY --chown=snowowl:snowowl snowowl.yml /usr/share/snowowl/configuration/
```

You could then build and try the image with something like:

```bash
docker build --tag=snow-owl-oss-custom .
docker run -ti -v /usr/share/snowowl/resources snow-owl-oss-custom
```

## Notes for production use and defaults

We have collected a number of best practices for production use.
Any Docker parameters mentioned below assume the use of `docker run`.

By default, Snow Owl runs inside the container as user `snowowl` using uid:gid `1000:1000`.

* If you are bind-mounting a local directory or file, ensure it is readable by
this user, while the <<path-settings,data and log dirs>> additionally require
write access. A good strategy is to grant group access to gid `1000` or `0` for
the local directory. As an example, to prepare a local directory for storing
data through a bind-mount:

```
  mkdir sodatadir
  chmod g+rwx sodatadir
  chgrp 1000 sodatadir
```

* It is important to ensure increased ulimits for `nofile`
and `nproc` are available for the Snow Owl containers.
Verify the [init system](https://github.com/moby/moby/tree/ea4d1243953e6b652082305a9c3cda8656edab26/contrib/init)
for the Docker daemon is already setting those to acceptable values and, if
needed, adjust them in the Daemon, or override them per container, for example
using `docker run`:

```
  --ulimit nofile=65535:65535
```

NOTE: One way of checking the Docker daemon defaults for the aforementioned
ulimits is by running:

```
  docker run --rm centos:7 /bin/bash -c 'ulimit -Hn && ulimit -Sn && ulimit -Hu && ulimit -Su'
```

* Swapping needs to be disabled for performance and stability. This can be
achieved through any of the methods mentioned in the [system settings](../configure/system-settings.md). 

* The image [exposes](https://docs.docker.com/engine/reference/builder/#/expose)
TCP ports 8080 and 2036.

* Use the `SO_JAVA_OPTS` environment variable to set heap size. For example, to
use 16GB use `SO_JAVA_OPTS="-Xms16g -Xmx16g"` with `docker run`.

* Pin your deployments to a specific version of the Snow Owl OSS Docker image. For
example, `snow-owl-oss:7.2.0`.

* Consider centralizing your logs by using a different https://docs.docker.com/engine/admin/logging/overview/[logging driver]. Also note
that the default json-file logging driver is not ideally suited for production use.