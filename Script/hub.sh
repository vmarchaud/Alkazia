#!/bin/bash


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


mc_start() {
		sh hub_1.sh start
		sh hub_2.sh start
		sh hub_3.sh start
}

mc_stop() {
		sh hub_1.sh stop
		sh hub_2.sh stop
		sh hub_3.sh stop
}


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
	*)
		echo "Usage: server {start|stop|restart}"
  ;;
esac
exit 1 
