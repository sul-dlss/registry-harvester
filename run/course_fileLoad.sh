#!/bin/sh

APP_HOME=/s/SUL/Harvester
JAVA_HOME=/usr
LOAD_FILE=$1

cd $APP_HOME/run

LD_LIBRARY_PATH=$APP_HOME/lib
export LD_LIBRARY_PATH

# looping to build classpath. skipping anything named old.*.jar
#
#
# echo "building classpath"
for file in `ls $APP_HOME/jar/` ; do
 case "$file" in
  old.*.jar) # echo skipping $file
  ;;
  *.jar|*.zip) # echo ADDING $file
        if [ "$CLASSPATH" != "" ]; then
           CLASSPATH=${CLASSPATH}:$APP_HOME/jar/$file
        else
           CLASSPATH=$APP_HOME/jar/$file
        fi
        ;;
 esac
done

#
# Log4j requires that its property file be specified in the CLASSPATH as
# well as the name of its property file be specified as a command-line argument
# -Dlog4j.configuration=<property file>
#
CLASSPATH="/s/SUL/Harvester/WebLogic_lib/wlthint3client-12.2.1.jar":${CLASSPATH}:$APP_HOME/conf

$JAVA_HOME/bin/java -Djava.security.egd=file:///dev/urandom -Dlog4j.configuration=course_harvester.properties -Dhttps.protocols=TLSv1.2 -cp $CLASSPATH edu.stanford.harvester.Harvester $APP_HOME/conf/course_harvester.properties $APP_HOME/conf/course_processor.properties $LOAD_FILE
EXIT_CODE=$?

if [ $EXIT_CODE -gt 0 ] ; then
  echo "Processor exited abnormally. Check log file for details"
else
  echo "Processor exited successfully"
fi
