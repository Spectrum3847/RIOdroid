#!/bin/sh
 case "$1" in
    start)
          # Start daemon.
          echo -e "Starting ADB:"
          /usr/bin/adb start-server
          ;;
    stop)
          # Stop daemons.
          echo -e "Shutting ADB:"
          /usr/bin/adb kill-server
          ;;
    restart)
          $0 stop
          $0 start
          ;;
    *)
          echo "Usage: $0 {start|stop|restart}"
          exit 1
 esac
 exit 0