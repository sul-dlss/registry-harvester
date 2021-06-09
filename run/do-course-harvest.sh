#!/bin/bash

. /s/SUL/Config/sirsi.env

HOME=/s/SUL/Harvester/current
LOG=$HOME/log
OUT=$HOME/out
COURSE_IDS=$2
DATE=$3

if [ -z $DATE ]
then
  DATE=`date +%Y%m%d%H%M`
fi

if [[ $1 == 'file' ]]; then
    $HOME/run/course_file_load.sh $COURSE_IDS
else
    $HOME/run/course_runonce.sh
fi

cat $LOG/course_harvest.log | mailx -s 'Course Harvest Log' sul-unicorn-devs@lists.stanford.edu

# Save and reset output and log files
/usr/sbin/logrotate --verbose $HOME/logrotate-course.conf --state /s/SUL/Harvester/shared/logrotate-state >> $LOG/logrotate.log 2>&1

usage(){
    echo "Usage: $0 [ no argument | 'file' ] [ file of course IDs (if arg0 == file) ] [ DATE (optional: to append to log and out files) ]"
    exit 1
}

[[ $0 =~ "help" ]] && usage
