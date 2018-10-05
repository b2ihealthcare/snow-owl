# Setting JVM options

You should rarely need to change Java Virtual Machine (JVM) options. If you do, the most likely change is setting the [heap size](important-settings.md). 

The preferred method of setting JVM options (including system properties and JVM flags) is via the the `SO_JAVA_OPTS` environment variable. For instance:

```
export SO_JAVA_OPTS="$SO_JAVA_OPTS -Djava.io.tmpdir=/path/to/temp/dir"
./bin/startup
```

When using the RPM or Debian packages, `SO_JAVA_OPTS` can be specified in the system configuration file.

{% hint style="info" %}
Some other Java programs support the `JAVA_OPTS` environment variable. This is not a mechanism built into the JVM but instead a convention in the ecosystem. However, we do **not** support this environment variable, instead supporting setting JVM options via the environment variable `SO_JAVA_OPTS` as above.
{% endhint %}

