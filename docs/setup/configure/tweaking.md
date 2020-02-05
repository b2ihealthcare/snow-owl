# Tweaking for performance

## Scheduler

```bash
# noop I/O scheduler, should be set in eg. /etc/rc.local for solid state disks:
echo noop > /sys/block/sdX/queue/scheduler
```