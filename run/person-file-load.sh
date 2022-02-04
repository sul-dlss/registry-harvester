#!/bin/sh
#$Id: fileLoad.sh,v 1.2 2008/07/22 19:43:28 dtayl Exp $
#
#
# to do:
#

APP_HOME=/s/SUL/Harvester/current
CONF_HOME=$APP_HOME/Person/src/main/resources
LOG=$APP_HOME/log
OUT=$APP_HOME/out
JAVA_HOME=/usr
LOAD_FILE=$1

if [[ -z $DATE ]]; then
  DATE=`date +%Y%m%d%H%M`
fi

cd $APP_HOME/run

LD_LIBRARY_PATH=$APP_HOME/lib
export LD_LIBRARY_PATH

# looping to build classpath. skipping anything named old.*.jar
#
#
#echo "building classpath"
for file in `ls $APP_HOME/jar/` ; do
 case "$file" in
  old.*.jar) #echo skipping $file
  ;;
  *.jar|*.zip) #echo ADDING $file
        if [ "$CLASSPATH" != "" ]; then
           CLASSPATH=${CLASSPATH}:$APP_HOME/jar/$file
        else
           CLASSPATH=$APP_HOME/jar/$file
        fi
  ;;
 esac
done

# Weblogic jar file
for file in `ls $APP_HOME/WebLogic_lib/` ; do
 case "$file" in
  old.*.jar) echo skipping $file >>$HARNESS_LOG;;
  *.jar|*.zip) echo ADDING $file >> $HARNESS_LOG
        if [ "$CLASSPATH" != "" ]; then
           CLASSPATH=${CLASSPATH}:$APP_HOME/WebLogic_lib/$file
        else
           CLASSPATH=$APP_HOME/WebLogic_lib/$file
        fi
        ;;
 esac
done
#
# Log4j requires that its property file be specified in the CLASSPATH as
# well as the name of its property file be specified as a command-line argument
# -Dlog4j.configuration=<property file>
#
CLASSPATH=${CLASSPATH}:$CONF_HOME

$JAVA_HOME/bin/java -Djava.security.egd=file:///dev/urandom -Dlog4j.configuration=harvester.properties -Dhttps.protocols=TLSv1.2 -cp $CLASSPATH edu.stanford.harvester.Harvester $CONF_HOME/harvester.properties $CONF_HOME/processor.properties $LOAD_FILE
EXIT_CODE=$?

$HOME/run/folio-userload.sh

# Save output files
mv $OUT/harvest.out $OUT/harvest.out.$DATE
mv $OUT/harvest.xml.out $OUT/harvest.xml.out.$DATE

# Save and reset log files
mv $LOG/harvest.log $LOG/harvest.log.$DATE

touch $LOG/harvest.log

if [ $EXIT_CODE -gt 0 ] ; then
  echo "Processor exited abnormally. Check log file for details"
  exit $EXIT_CODE
fi
