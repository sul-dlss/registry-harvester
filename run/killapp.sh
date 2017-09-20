#!/bin/sh  
# based on:
# $Id: killapp.sh,v 1.1 2005/04/07 17:58:57 chandau Exp $
#
# this is rather heroic. it would be nicer if the application
# accepted a kill message like tomcat, jboss, etc.
#

APP_NAME=harvester
APP_HOME=/s/SUL/Harvester/current

cd ${APP_HOME}/run

ps -elf | grep `cat ${APP_HOME}/run/harness.pid ` | awk '{print $4}' > /tmp/${APP_NAME}_java.pid

echo "\n\n`date` killapp.sh: Kill the harness for ${APP_NAME}"

# now we kill the harness
kill `cat ${APP_HOME}/run/harness.pid`

echo "Kill the java (if there is none do not worry it already died and you are off the hook) "

kill -9 `cat /tmp/${APP_NAME}_java.pid`

/bin/rm /tmp/${APP_NAME}_java.pid

# just in case all the above didn't work
# this last bit keeps track of the previous PID, so one can try and find the
# new child by hand.


/bin/mv ${APP_HOME}/run/harness.pid ${APP_HOME}/run/harness.pid.prior


# $Log: killapp.sh,v $
# Revision 1.1  2005/04/07 17:58:57  chandau
# initial version - currently in test
#
# Revision 1.1  2005/01/26 19:18:33  molive
# Adding standard start and stop scripts
#
# Revision 1.6  2004/02/09 20:33:53  caseyd1
# remove lifecycle notification from template killapp.sh
#
# Revision 1.5  2003/12/10 23:05:15  caseyd1
# clean up timestamping
# get cvs "meta" tags everywhere
# add lifecycle reporting
#
# Revision 1.4  2003/12/10 22:25:41  caseyd1
# add cvs tokens and meta-tokens
#
#
#
#
#
