# Number of threads

Snow Owl uses a number of thread pools for different types of operations. It is important that it is able to create new threads whenever needed. Make sure that the number of threads that the Snow Owl user can create is at least `4096`.

This can be done by setting `ulimit -u 4096` as root before starting Snow Owl, or by setting `nproc` to `4096` in `/etc/security/limits.conf`.

The package distributions when run as services under systemd will configure the number of threads for the Snow Owl process automatically. No additional configuration is required.