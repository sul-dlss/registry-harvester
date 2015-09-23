#!/bin/sh
#$Id: fileLoad.sh,v 1.2 2008/07/22 19:43:28 dtayl Exp $
# 
# 
# to do: 
#

APP_NAME=harvester
APP_HOME=/s/SUL/Harvester
JAVA_HOME=/usr
LOAD_FILE=$1

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

#
# Log4j requires that its property file be specified in the CLASSPATH as
# well as the name of its property file be specified as a command-line argument
# -Dlog4j.configuration=<property file>
#
CLASSPATH="/home/harvester/WebLogic_lib/wlfullclient-10.3.5.jar":${CLASSPATH}:$APP_HOME/conf

$JAVA_HOME/bin/java -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Dssl.SocketFactory.provider=com.sun.net.ssl.internal.SSLSocketFactoryImpl -DUseSunHttpHandler=true -Dweblogic.wsee.client.ssl.usejdk=true -Djava.security.egd=file:///dev/urandom -Dlog4j.configuration=harvester.properties -cp $CLASSPATH edu.stanford.harvester.Harvester $APP_HOME/conf/harvester.properties $APP_HOME/conf/processor.properties $LOAD_FILE
EXIT_CODE=$?

if [ $EXIT_CODE -gt 0 ] ; then
  echo "Processor exited abnormally. Check log file for details"
else
  echo "Processor exited successfully"
fi

