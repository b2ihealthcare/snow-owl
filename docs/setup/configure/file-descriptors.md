# File Descriptors

{% hint style="info" %}
This is only relevant if you are running Snow Owl with an embedded Elasticsearch and not connecting it to an existing cluster.
{% endhint %}

Snow Owl (with embedded Elasticsearch) uses a lot of file descriptors or file handles. Running out of file descriptors can be disastrous and will most probably lead to data loss. Make sure to increase the limit on the number of open files descriptors for the user running Snow Owl to 65,536 or higher.

For the `.zip` and `.tar.gz` packages, set `ulimit -n 65536` as root before starting Snow Owl, or set `nofile` to `65536` in `/etc/security/limits.conf`.

RPM and Debian packages already default the maximum number of file descriptors to `65536` and do not require further configuration.