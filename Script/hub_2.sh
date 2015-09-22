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
#    Configuration    |
#---------------------+
SCRNNAME="hub_2"
SERVICE="$SCRNNAME.jar"
MCPATH="/home/$SCRNNAME"
BACKUP_PATH="$MCPATH/backups"
WORLDNAME="$(cat $MCPATH/server.properties | grep -E 'level-name' | sed -e s/.*level-name=//)"
SERVERPORT="$(cat $MCPATH/server.properties | grep -E 'server-port' | sed -e s/.*server-port=//)"
USER="thisismac"
CPU_COUNT="4"
MINRAM="2G"
MAXRAM="4G"
INVOCATION="java -Xms$MINRAM -Xmx$MAXRAM -jar $SERVICE nogui"

#---------------------+
#       Messages      |
#---------------------+
SAVE_START="Sauvegardes des mondes en cours ..."
SAVE_END="La sauvegarde des mondes a été effectué !"
STOP_WARNING_MAX="Le serveur redémarre dans 5 minutes ..."
STOP_WARNING_MID="Le serveur redémarre dans  1 minute ..."
STOP_WARNING_FINAL="Le serveur redémarre dans  10 secondes ..."

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

mc_run() {
  if is_running; then
    as_user "screen -p 0 -S $SCRNNAME -X eval 'stuff \"$1\"\015'"
  fi
}

mc_alert() {
  mc_run "alert $1"
}

mc_save() {
  if is_running; then
    echo " * Saving map named \"$WORLDNAME\" to disk..."
    mc_alert "$SAVE_START"
    mc_run "save-off"
    mc_run "save-all"
    sync ; sleep 10
    mc_alert "$SAVE_END"
    mc_run "save-on"
    echo " * World save complete"
  else
    echo " * [ERROR] $SCRNNAME was not running, cannot save world"
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
    steps=0
    while is_running; do
      if [[ $steps -ge 10 ]]; then
		    echo " * [ERROR] Failed to shut down cleanly. Forcing shutdown..."
		    as_user "kill -9 $PID"
	    fi

	    if [[ $steps -eq 0 ]]; then
        echo " * $SCRNNAME is running [PID $PID]. Shutting down..."
        mc_alert "$STOP_WARNING_FINAL"
        mc_save
      fi
      mc_run "restart"
      sleep 5
	    steps=$steps+1
	  done
	  echo " * $SCRNNAME has shut down"
  else
    echo " * [ERROR] $SCRNNAME was not running. Cannot stop!"
  fi
}

mc_verify_folders() {
  if [ ! -d $BACKUP_PATH ]; then
    as_user "/bin/mkdir $BACKUP_PATH"
  fi
  if [ ! -d $BACKUP_PATH/server_logs ]; then
    as_user "/bin/mkdir $BACKUP_PATH/server_logs"
  fi
  if [ ! -d $BACKUP_PATH/worlds ]; then
    as_user "/bin/mkdir $BACKUP_PATH/worlds/"
  fi
  if [ ! -d $BACKUP_PATH/worlds/$WORLDNAME/ ]; then
    as_user "/bin/mkdir $BACKUP_PATH/worlds/$WORLDNAME/"
  fi
  if [ ! -d $BACKUP_PATH/worlds/$WORLDNAME/old/ ]; then
    as_user "/bin/mkdir $BACKUP_PATH/worlds/$WORLDNAME/old/"
  fi
}

mc_log_rotate() {
  mc_verify_folders
  NOW="$(date +%Y-%m-%d.%H-%M-%S)"
  as_user "/bin/cp $MCPATH/server.log $BACKUP_PATH/server_logs/$NOW.log"
  as_user "echo -n \"\" > $MCPATH/server.log"

  LOGLIST=$(ls -r $BACKUP_PATH/server_logs/* | grep -v lck)
  COUNT=12
  CURCOUNT=0
  for i in $LOGLIST; do
    CURCOUNT=$CURCOUNT+1
    if [[ $CURCOUNT -gt $COUNT ]]; then
      as_user "rm -f $i"
    fi
  done
}

mc_server_restart() {
  echo " * Server Restarting in 5 minutes..."
  mc_alert "$STOP_WARNING_MAX"
  sleep 240
  mc_alert "$STOP_WARNING_MID"
  sleep 60
	mc_server_stop
	mc_start
}

mc_server_stop() {
	mc_stop
	mc_log_rotate
}


mc_server_backup() {
  mc_verify_folders
  echo " * Backing up $SCRNNAME map named \"$WORLDNAME\"..."
  if [ -d $BACKUP_PATH/worlds/$WORLDNAME/current/ ]; then
    NOW="$(date +%Y-%m-%d.%H-%M-%S)"
    as_user "cd "$BACKUP_PATH/worlds/$WORLDNAME/" && tar cfzP \"$BACKUP_PATH/worlds/$WORLDNAME/old/backup_"$NOW".tar.gz\" \"$BACKUP_PATH/worlds/$WORLDNAME/current/\""
  fi  
  
  mc_run "save-off"
  as_user "rsync --checksum --group --human-readable --copy-links --owner --perms --recursive --times --update --delete $MCPATH/$WORLDNAME/ $BACKUP_PATH/worlds/$WORLDNAME/current/"
  sleep 10
  mc_run "save-on"
  echo " * [OK] Backed up map \"$WORLDNAME\"."

  echo " * Removing backups older than 7 days..."
  as_user "cd $BACKUP_PATH/worlds/$WORLDNAME/old/ && find . -name \"*backup*\" -type f -mtime +7 | xargs rm -fv"
  echo " * Removed old backups."
}

mc_server_backup_plugin() {
  echo " * Backing up $SCRNNAME all plugin ..."
  if [ -d $BACKUP_PATH/plugins/current/ ]; then
    NOW="$(date +%Y-%m-%d.%H-%M-%S)"
    as_user "cd "$BACKUP_PATH/plugins" && tar cfzP \"$BACKUP_PATH/plugins/old/backup_"$NOW".tar.gz\" \"$BACKUP_PATH/worlds/$WORLDNAME/current/\""
  fi  
  
  mc_run "save-off"
  as_user "rsync --checksum --group --human-readable --copy-links --owner --perms --recursive --times --update --delete $MCPATH/plugins/ $BACKUP_PATH/plugins/current/"
  sleep 10
  mc_run "save-on"
  echo " * [OK] Backed up all plugin."

  echo " * Removing backups older than 7 days..."
  as_user "cd $BACKUP_PATH/plugins/old/ && find . -name \"*backup*\" -type f -mtime +7 | xargs rm -fv"
  echo " * Removed old backups."
}

mc_console() {
  if is_running; then
    as_user "screen -S $SCRNNAME -dr"
  else
    echo " * [ERROR] $SCRNNAME was not running!"
  fi
}

mc_info() {
  if is_running; then
    RSS="$(ps --pid $PID --format rss | grep -v RSS)"
    echo " - Java Path          : $(readlink -f $(which java))"
    echo " - Start Command      : $INVOCATION"
    echo " - Server Path        : $MCPATH"
    echo " - World Name         : $WORLDNAME"
    echo " - Process ID         : $PID"
    echo " - Screen Session     : $SCRNNAME"
    echo " - Memory Usage       : $[$RSS/1024] Mb [$RSS kb]"
  # Check for HugePages support in kernel, display statistics if HugePages are in use, otherwise skip
  if [ -n "$(cat /proc/meminfo | grep HugePages_Total | awk '{print $2}')" -a "$(cat /proc/meminfo | grep HugePages_Total | awk '{print $2}')" -gt 0 ]; then
    HP_SIZE="$(cat /proc/meminfo | grep Hugepagesize | awk '{print $2}')"
    HP_TOTAL="$(cat /proc/meminfo | grep HugePages_Total | awk '{print $2}')"
    HP_FREE="$(cat /proc/meminfo | grep HugePages_Free | awk '{print $2}')"
    HP_RSVD="$(cat /proc/meminfo | grep HugePages_Rsvd | awk '{print $2}')"
    HP_USED="$[$HP_TOTAL-$HP_FREE+$HP_RSVD]"
    TOTALMEM="$[$RSS+$[$HP_USED*$HP_SIZE]]"
    echo " - HugePage Usage     : $[$HP_USED*$[$HP_SIZE/1024]] Mb [$HP_USED HugePages]"
    echo " - Total Memory Usage : $[$TOTALMEM/1024] Mb [$TOTALMEM kb]"
  fi
    echo " - Active Connections : "
    netstat --inet -tna | grep -E "Proto|$SERVERPORT"
  else
    echo " * $SCRNNAME is not running. Unable to give info."
  fi
}


#---------------------+
# Commandline parsing |
#---------------------+
#  start:         Starts the service
#  stop:          Stops the service
#  restart:       Restarts the service (if not running, starts the service)
#  console:       Opens the console
#  info:          Tells user some information about connections and server usage
#  backup:        Runs a backup for worlds in $WORLD_DIR
#  run:           Executes a server command
#  say:           Sends a server alert
#---------------------+

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
	  mc_console
	;;
	save)
	  mc_save
  ;;
	console)
	  mc_console
	;;
	info)
	  mc_info
	;;
	run)
	  echo " * Ran \"$2\" on $SCRNNAME"
	  mc_run "$2"
	;;
	say)
	  echo " * Alerted all users on $SCRNNAME that \"$2\""
	  mc_alert "$2"
	;;
	sreset)
	  mc_stop
	  mc_remove_worlds
	  mc_start
  ;;
	srestart)
	  mc_server_restart
	;;
	sbackup)
	  mc_server_backup
	;;
	backup_plugin)
	  mc_server_backup_plugin
	;;
	*)
		echo "Usage: server {start|stop|restart|save|console|info|run|say||sreset|srestart|sbackup|backup_plugin}"
  ;;
esac
exit 1 
