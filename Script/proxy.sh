#!/bin/bash

# Source function library
## CentOS/Fedora
if [ -f /etc/rc.d/init.d/functions ]
 then
 . /etc/rc.d/init.d/functions
fi
## Ubuntu
if [ -f /lib/lsb/init-functions ]
 then
 . /lib/lsb/init-functions
fi

#---------------------+
#      Utilities      |
#---------------------+

as_user() {
  ME="$(whoami)"
  if [ "$ME" == "$USER" ]; then
    bash -c "$1"
  else
    su - "$USER" -c "$1"
  fi
}

is_running() {
  if ps ax | grep -v grep | grep -iv SCREEN | grep $SERVICE > /dev/null; then
    PID=0
    PID="$(ps ax | grep -v grep | grep -iv SCREEN | grep $SERVICE | awk '{print $1}')"
    return 0
  else
    return 1
  fi
}

mc_start() {
  if is_running ; then
    echo " * [ERROR] $SCRNNAME was already running [PID $PID]. Not starting..."
  else
    echo " * $SCRNNAME is not already running, starting up..."
    as_user "cd \"$MCPATH\" && screen -c /dev/null -dmS $SCRNNAME $INVOCATION"
    sleep 10
    if is_running; then
      echo " * [OK] $SCRNNAME has started up [PID $PID]"
    else
      echo " * [ERROR] Could not start $SCRNNAME"
    fi
  fi
}

mc_stop() {
  if is_running; then
    while is_running; do
      as_user "kill -9 $PID"
      sleep 10
    done
    echo " * $SCRNNAME has shut down"
  else
    echo " * [ERROR] $SCRNNAME was not running. Cannot stop!"
  fi
}

mc_console() {
  if is_running; then
    as_user "screen -S $SCRNNAME -dr"
  else
    echo " * [ERROR] $SCRNNAME was not running!"
  fi
}


#---------------------+
#    Configuration    |
#---------------------+
SCRNNAME="bungeecord"
MCPATH="/home/bungee"
SERVICE="$SCRNNAME.jar"
USER="root"
MINRAM="2G"
MAXRAM="4G"
INVOCATION="java -server -XX:UseSSE=4 -XX:+UseCMSCompactAtFullCollection -XX:ParallelGCThreads=4 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:+AggressiveOpts -Xms$MINRAM -Xmx$MAXRAM -XX:PermSize=128m -jar $SERVICE nogui"

case $1 in
  start)
    mc_start
  ;;
  stop)
    mc_stop
  ;;
  restart)
    mc_stop
    mc_start
  ;;
  console)
    mc_console
  ;;
  *)
    echo "Usage: {start|stop|restart|console} {1|2}"
  ;;
esac
exit 1 
