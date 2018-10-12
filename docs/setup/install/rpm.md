# Install Snow Owl with RPM

The RPM for Snow Owl can be downloaded from the Downloads section. It can be used to install Snow Owl on any RPM-based system such as OpenSuSE, SLES, Centos, Red Hat, and Oracle Enterprise.

{% hint style="info" %}
RPM install is not supported on distributions with old versions of RPM, such as SLES 11 and CentOS 5. Please see [Install Snow Owl with .zip or .tar.gz](./tarzip.md) instead.
{% endhint %}

## Download and install

```bash
wget https://github.com/b2ihealthcare/snow-owl/releases/download/<version>/snow-owl-oss-<version>.rpm
wget https://github.com/b2ihealthcare/snow-owl/releases/download/<version>/snow-owl-oss-<version>.rpm.sha512
shasum -a 512 -c snow-owl-oss-<version>.rpm.sha512 # Compares the SHA of the downloaded RPM and the published checksum, which should output `snow-owl-oss-<version>.rpm: OK`.
sudo rpm --install snow-owl-oss-<version>.rpm
```

{% hint style="info" %}
On systemd-based distributions, the installation scripts will attempt to set kernel parameters (e.g., `vm.max_map_count`); you can skip this by masking the systemd-sysctl.service unit.
{% endhint %}

## Running Snow Owl with SysV init

Use the chkconfig command to configure Snow Owl to start automatically when the system boots up:

```
sudo chkconfig --add snowowl
```

Snow Owl can be started and stopped using the service command:

```
sudo -i service snowowl start
sudo -i service snowowl stop
```

If Snow Owl fails to start for any reason, it will print the reason for failure to STDOUT. Log files can be found in /var/log/snowowl/.

## Running Snow Owl with systemd

To configure Snow Owl to start automatically when the system boots up, run the following commands:

```bash
sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable snowowl.service
```

Snow Owl can be started and stopped as follows:

```bash
sudo systemctl start snowowl.service
sudo systemctl stop snowowl.service
```

These commands provide no feedback as to whether Snow Owl was started successfully or not. Instead, this information will be written in the log files located in /var/log/snowowl/.

## Checking that Snow Owl is running

You can test that your Snow Owl instance is running by sending an HTTP request to:

```bash
curl http://localhost:8080/snowowl/admin/info
```

which should give you a response something like this:

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

## Configuring Snow Owl

Snow Owl defaults to using `/etc/snowowl` for runtime configuration. The ownership of this directory and all files in this directory are set to `root:snowowl` on package installation and the directory has the setgid flag set so that any files and subdirectories created under `/etc/snowowl` are created with this ownership as well (e.g., if a keystore is created using the keystore tool). It is expected that this be maintained so that the Snow Owl process can read the files under this directory via the group permissions.

Snow Owl loads its configuration from the `/etc/snowowl/snowowl.yml` file by default. The format of this config file is explained in [Configuring Snow Owl](../configure/index.md).

## Directory layout of RPM

The RPM places config files, logs, and the data directory in the appropriate locations for an RPM-based system:

| Type          | Description             | Default Location  | Setting |
| ------------- | ----------------------- | ----------------- | ------- |
| home          | Snow Owl home directory or `$SO_HOME` | `/usr/share/snowowl` ||
| bin           | Binary scripts including startup/shutdown to start/stop the instance       | `/usr/share/snowowl/bin` ||
| conf          | Configuration files including `snowowl.yml` | `/etc/snowowl` | [SO_PATH_CONF](../configure/index.md#config-files-location) |
| data          | The location of the data files and resources. | /var/lib/snowowl | path.data |
| logs          | Log files location. | /var/log/snowowl ||

## Next steps

You now have a test Snow Owl environment set up. Before you start serious development or go into production with Snow Owl, you must do some additional setup:

* Learn how to [configure Snow Owl](../configure/index.md).
* Configure [important Snow Owl settings](../configure/important-settings.md).
* Configure [important system settings](../configure/).
