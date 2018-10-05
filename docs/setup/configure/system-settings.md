# Configuring system settingst

Where to configure systems settings depends on which package you have used to install Snow Owl, and which operating system you are using.

When using the `.zip` or `.tar.gz` packages, system settings can be configured:

* temporarily with [ulimit](#ulimit), or
* permanently in [/etc/security/limits.conf](#/etc/security/limits.conf).

When using the RPM or Debian packages, most system settings are set in the system configuration file. However, systems which use systemd require that system limits are specified in a systemd configuration file.

## ulimit

On Linux systems, `ulimit` can be used to change resource limits on a temporary basis. Limits usually need to be set as root before switching to the user that will run Snow Owl. For example, to set the number of open file handles (`ulimit -n`) to `65,536`, you can do the following:

```bash
sudo su # Become `root`
ulimit -n 65536 # Change the max number of open files
su snowowl # Become the `snowowl` user in order to start Snow Owl
```

The new limit is only applied during the current session.

You can consult all currently applied limits with `ulimit -a`.

## /etc/security/limits.conf

On Linux systems, persistent limits can be set for a particular user by editing the `/etc/security/limits.conf` file. To set the maximum number of open files for the `snowowl` user to `65,536`, add the following line to the limits.conf file:

```
snowowl  -  nofile  65536
```

This change will only take effect the next time the `snowowl` user opens a new session.

{% hint style="info" %}
## Ubuntu and limits.conf
Ubuntu ignores the `limits.conf` file for processes started by `init.d`. To enable the `limits.conf` file, edit `/etc/pam.d/su` and uncomment the following line:

```
# session    required   pam_limits.so
```
{% endhint %}

## Sysconfig file

When using the RPM or Debian packages, system settings and environment variables can be specified in the system configuration file, which is located in:

| Package | Location |
| ------- | -------- |
| RPM | /etc/sysconfig/snowowl |
| Debian | /etc/default/snowowl |

However, for systems which uses systemd, system limits need to be specified via systemd.

## Systemd configuration

When using the RPM or Debian packages on systems that use systemd, system limits must be specified via systemd.

The systemd service file (/usr/lib/systemd/system/snowowl.service) contains the limits that are applied by default.

To override them, add a file called /etc/systemd/system/snowowl.service.d/override.conf (alternatively, you may run `sudo systemctl edit snowowl` which opens the file automatically inside your default editor). Set any changes in this file, such as:

```
[Service]
LimitMEMLOCK=infinity
```

Once finished, run the following command to reload units:

```
sudo systemctl daemon-reload
```


TODO to migrate

## Network settings

8080/TCP:: Used by Snow Owl Server's REST API
2036/TCP:: Used by the Net4J binary protocol connecting Snow Owl clients to the server

For optimal indexing and searching performance, consult the "Important system configuration" section of 
https://www.elastic.co/guide/en/elasticsearch/reference/6.3/system-config.html[Elasticsearch: Reference Guide].
In particular, the following settings are applicable to an installation of the terminology server:

sysctl settings, to be added to /etc/sysctl.conf or equivalent

```
vm.swappiness = 1
vm.max_map_count = 262144
```

# "noop" I/O scheduler, should be set in eg. /etc/rc.local for solid state disks:

```
echo noop > /sys/block/sdX/queue/scheduler
```

# increase the maximum limit of available file descriptors and threads in /etc/security/limits.conf

```
*    soft    nofile 65536
*    hard    nofile 65536  
*    soft    nproc  4096
*    hard    nproc  4096 
```