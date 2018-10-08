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

## Debian packages (Coming Soon)

## RPM packages (Coming Soon)

## Docker images (Coming Soon)