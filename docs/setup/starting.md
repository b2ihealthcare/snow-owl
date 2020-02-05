# Starting Snow Owl

The method for starting Snow Owl varies depending on how you installed it.

## Archive packages (.tar.gz, .zip)

If you installed Snow Owl with a `.tar.gz` or `zip` package, you can start Snow Owl from the command line.

### Running Snow Owl from the command line

Snow Owl can be started from the command line as follows:

```
./bin/startup
```

By default, Snow Owl runs in the foreground, prints some of its logs to the standard output (`stdout`), and can be stopped by pressing `Ctrl-C`.

{% hint style="info" %}
All scripts packaged with Snow Owl assume that Bash is available at /bin/bash. As such, Bash should be available at this path either directly or via a symbolic link.
{% endhint %}

### Running as a daemon

To run Snow Owl as a daemon, use the following command:

```
nohup ./bin/startup > /dev/null &
```

Log messages can be found in the `$SO_HOME/serviceability/logs/` directory.

{% hint style="info" %}
The startup scripts provided in the RPM and Debian packages take care of starting and stopping the Snow Owl process for you.
{% endhint %}

## RPM packages

Snow Owl is not started automatically after installation. How to start and stop Snow Owl depends on whether your system uses `SysV init` or `systemd` (used by newer distributions). You can tell which is being used by running this command:

```
ps -p 1
```

### Running Snow Owl with SysV init

Use the `chkconfig` command to configure Snow Owl to start automatically when the system boots up:

```
sudo chkconfig --add snowowl
```

Snow Owl can be started and stopped using the service command:

```
sudo -i service snowowl start
sudo -i service snowowl stop
```

If Snow Owl fails to start for any reason, it will print the reason for failure to STDOUT. Log files can be found in `/var/log/snowowl/`.

### Running Snow Owl with systemd

To configure Snow Owl to start automatically when the system boots up, run the following commands:

```
sudo /bin/systemctl daemon-reload
sudo /bin/systemctl enable snowowl.service
```

Snow Owl can be started and stopped as follows:

```
sudo systemctl start snowowl.service
sudo systemctl stop snowowl.service
```

These commands provide no feedback as to whether Snow Owl was started successfully or not. Instead, this information will be written in the log files located in `/var/log/snowowl/`.

## Debian packages (Coming Soon)

## Docker images (Coming Soon)