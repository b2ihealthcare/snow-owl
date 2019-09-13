#!/usr/bin/expect
set timeout -1
spawn telnet 127.0.0.1 2501
expect "osgi> "
exp_send "shutdown\r"
expect eof
exit
