# Install Snow Owl with .zip or .tar.gz

Snow Owl is provided as a `.zip` and as a `.tar.gz` package. These packages can be used to install Snow Owl on any system and are the easiest package format to use when trying out Snow Owl.

The latest stable version of Snow Owl can be found on the [Snow Owl Releases](https://github.com/b2ihealthcare/snow-owl/releases) page.

{% hint style="info" %}
Snow Owl requires Java 8 or later. Use the official Oracle distribution or an open-source distribution such as OpenJDK.
{% endhint %}

## Download and install the `zip` package

The `.zip` archive for Snow Owl can be downloaded and installed as follows:

```bash
wget https://github.com/b2ihealthcare/snow-owl/releases/download/<version>/snow-owl-oss-<version>.zip
wget https://github.com/b2ihealthcare/snow-owl/releases/download/<version>/snow-owl-oss-<version>.zip.sha512
shasum -a 512 -c snowowl-oss-<version>.zip.sha512 # compares the SHA of the downloaded archive, should output: `snowowl-oss-<version>.zip: OK.`
unzip snowowl-oss-<version>.zip
cd snowowl-oss-<version>/ # This directory is known as `$SO_HOME`
```

## Download and install the `.tar.gz` package

The `.tar.gz` archive for Snow Owl can be downloaded and installed as follows:

```bash
wget https://github.com/b2ihealthcare/snow-owl/releases/download/<version>/snow-owl-oss-<version>.tar.gz
wget https://github.com/b2ihealthcare/snow-owl/releases/download/<version>/snow-owl-oss-<version>.tar.gz.sha512
shasum -a 512 -c snowowl-oss-<version>.tar.gz.sha512 # compares the SHA of the downloaded archive, should output: `snowowl-oss-<version>.tar.gz: OK.` 
tar -xzf snowowl-oss-<version>.tar.gz
cd snowowl-oss-<version>/ # This directory is known as `$SO_HOME`
```

## Running Snow Owl from the command line

Snow Owl can be started from the command line as follows:

```bash
./bin/startup
```

By default, Snow Owl runs in the foreground, prints its logs to the standard output (stdout), and can be stopped by pressing Ctrl-C.

{% hint style="info" %}
All scripts packaged with Snow Owl assume that Bash is available at /bin/bash. As such, Bash should be available at this path either directly or via a symbolic link.
{% endhint %}

## Checking that Snow Owl is running

You can test that your instance is running by sending an HTTP request to Snow Owl's status endpoint:

```bash
curl http://localhost:8080/snowowl/admin/info
```

which should give you a response like this:

```json
{
  "version": "7.0.0",
  "description": "You Know, for Terminologies",
  "repositories": {
    "items": [
      {
        "id": "snomedStore",
        "health": "GREEN"
      }
    ]
  }
}
```

## Running in the background

You can send the Snow Owl process to the background using the combination of `nohup` and the `&` character:

```bash
nohup ./bin/startup > /dev/null &
```

Log messages can be found in the `$SO_HOME/serviceability/logs/` directory.

To shut down Snow Owl, you can kill the process ID directly:

```bash
kill <pid>
```

or using the provided shutdown script:

```bash
./bin/shutdown
```

## Directory layout of `.zip` and `.tar.gz` archives:

The `.zip` and `.tar.gz` packages are entirely self-contained. All files and directories are, by default, contained within `$SO_HOME` — the directory created when unpacking the archive.

This is very convenient because you don’t have to create any directories to start using Snow Owl, and uninstalling Snow Owl is as easy as removing the `$SO_HOME` directory. However, it is advisable to change the default locations of the config directory, the data directory, and the logs directory so that you do not delete important data later on.

| Type          | Description             | Default Location  | Setting |
| ------------- | ----------------------- | ----------------- | ------- |
| home          | Snow Owl home directory or `$SO_HOME` | Directory created by unpacking the archive ||
| bin           | Binary scripts including startup/shutdown to start/stop the instance       | $SO_HOME/bin ||
| conf          | Configuration files including `snowowl.yml` | $SO_HOME/configuration | [SO_PATH_CONF](../configure/index.md#config-files-location) |
| data          | The location of the data files and resources. | $SO_HOME/resources | path.data |
| logs          | Log files location. | $SO_HOME/serviceability/logs ||

## Next steps

You now have a test Snow Owl environment set up. Before you start serious development or go into production with Snow Owl, you must do some additional setup:

* Learn how to [configure Snow Owl](../configure/index.md).
* Configure [important Snow Owl settings](../configure/important-settings.md).
* Configure [important system settings](../configure/).