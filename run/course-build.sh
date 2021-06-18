#!/bin/bash

. /s/SUL/Config/sirsi.env

HOME=/s/SUL/Harvester/current
DATA=/s/SUL/Dataload/CourseReserves
LOG=$HOME/log
OUT=$HOME/out
JVM1=$1
JVM2=$2
OUTFile=$3
DATE=`date +%Y%m%d%H%M`

ruby $HOME/run/remove_old_course_harvests.rb >> $LOG/course_build.log
ruby $HOME/run/update_terms_conf.rb >> $LOG/course_build.log

if [[ -z $COURSE_TERMS ]]; then
    export COURSE_TERMS=`cat $HOME/Course/src/main/resources/terms.conf`
fi

if [[ -z $JVM1 ]]
then
  JVM1="2G"
fi

if [[ -z $JVM2 ]]
then
  JVM2="10G"
fi

if [[ "$OUTFile" == "latest" ]]
then
  for F in `ls $OUT/course_harvest.out.* | tail -1`
  do
    OUTFile="$F"
  done
elif [[ -z $OUTFile ]]
then
  for F in `ls $OUT/course_harvest.out.*`
  do
    OUTFile="$OUTFile $F"
  done
fi

if [[  -n `ls $HOME/course_files/*.xml` ]]; then
    cp $HOME/course_files/*.xml $HOME/course_files/LastRun
fi

cd $HOME

#echo "Running: java -jar -Xms${JVM1} -Xmx${JVM2} Course-jar-with-dependencies.jar $OUTFile"

java -jar -Xms${JVM1} -Xmx${JVM2} lib/Course-jar-with-dependencies.jar $OUTFile

UPDATED=`grep " updated$" $LOG/course_build.err | awk '{print $6}' | sort | uniq | wc -l`
INSERTED=`grep " inserted$" $LOG/course_build.err | awk '{print $6}' | sort | uniq | wc -l`
ERRORS=`grep "Exception:" $LOG/course_build.err`

echo "$UPDATED course records updated. $INSERTED new course records added." >> $LOG/course_build.log

if [[ -n $ERRORS ]]; then
    echo "Errors: $ERRORS" >> $LOG/course_build.log
fi

echo "Copied courseXML files to /s/SUL/Dataload/CourseReserves" >> $LOG/course_build.log
cp $HOME/course_files/courseXML_*.xml $DATA/

echo "" >> $LOG/course_build.log
echo "New files generated:" >> $LOG/course_build.log
ls -lS $HOME/course_files/*.xml >> $LOG/course_build.log

echo "" >> $LOG/course_build.log
echo "Last Run:" >> $LOG/course_build.log
ls -lS $HOME/course_files/LastRun/*.xml >> $LOG/course_build.log

echo "Changed files:" >> $LOG/course_build.log
rsync -vI --dry-run --size-only ./course_files/*.xml ./course_files/LastRun/ >> $LOG/course_build.log

cat $LOG/course_build.log | mailx -s 'Course Build Log' sul-unicorn-devs@lists.stanford.edu

mv $LOG/course_build.log $LOG/course_build.log.$DATE
mv $LOG/course_build.err $LOG/course_build.err.$DATE
touch $LOG/course_build.err
touch $LOG/course_build.log
