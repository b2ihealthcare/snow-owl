# Disable swapping

Most operating systems try to use as much memory as possible for file system caches and eagerly swap out unused application memory. This can result in parts of the JVM heap or even its executable pages being swapped out to disk.

Swapping is very bad for performance, and should be avoided at all costs. It can cause garbage collections to last for minutes instead of milliseconds and can cause services to respond slowly or even time out.

There are two approaches to disabling swapping. The preferred option is to completely disable swap, but if this is not an option, you can minimize swappiness.

## Disable all swap files

Usually Snow Owl is the only service running on a box, and its memory usage is controlled by the JVM options. There should be no need to have swap enabled.

On Linux systems, you can disable swap temporarily by running:

```
sudo swapoff -a
```

To disable it permanently, you will need to edit the `/etc/fstab` file and comment out any lines that contain the word `swap`.

## Configure swappiness

Another option available on Linux systems is to ensure that the sysctl value `vm.swappiness` is set to 1. This reduces the kernel’s tendency to swap and should not lead to swapping under normal circumstances, while still allowing the whole system to swap in emergency conditions.

```
# sysctl settings, to be added to /etc/sysctl.conf or equivalent
vm.swappiness = 1
vm.max_map_count = 262144
```