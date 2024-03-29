#!/bin/bash

. /s/SUL/Config/sirsi.env

HOME=/s/SUL/Harvester/current
REM_CR=/s/SUL/Bin/TextUtil/remove_cr.pl
LOG=$HOME/log
DATE=$1

KEY_DIR="/s/SUL/Batchlog"
KEY_FILE="$KEY_DIR/userload.keys.$DATE"

if [ $(grep -c "^[0-9]\+" $KEY_FILE) -gt 5000 ]
then
  cd $KEY_DIR
  split -a 1 -l 2000 $KEY_FILE "userload.keys.$DATE."

  cd $HOME
  echo "" > $LOG/illiad.log

  for FILE in `find $KEY_FILE.*`
  do
    echo "Processing $FILE for ILLiad" >> $LOG/illiad.log
    echo "" >> $LOG/illiad.log
    cat $FILE | sort | uniq > ${FILE}_uniq

    java -jar lib/Person-jar-with-dependencies.jar ${FILE}_uniq keyfile >> $LOG/illiad.log  2>&1
  done
else
  cd $HOME

  echo "Processing $KEY_FILE for ILLiad" > $LOG/illiad.log
  echo "" >> $LOG/illiad.log

  [[ -e $KEY_FILE ]] && cat $KEY_FILE > ${KEY_FILE}_tmp_uniq

  [[ -e ${KEY_FILE}.3 ]] && cat ${KEY_FILE}.3 >> ${KEY_FILE}_tmp_uniq

  cat ${KEY_FILE}_tmp_uniq | sort | uniq > ${KEY_FILE}_uniq

  java -jar lib/Person-jar-with-dependencies.jar ${KEY_FILE}_uniq keyfile >> $LOG/illiad.log  2>&1
fi

if [ -z $(grep "Error" $LOG/illiad.log) ]
then
    VALUES=`grep "VALUES" $LOG/illiad.log | wc -l`
    echo "" >> $LOG/illiad.log
    echo "$VALUES rows inserted into ILLiad Users Table" >> $LOG/illiad.log
fi

cat $LOG/illiad.log | $REM_CR | mailx -s 'ILLiad User Export Log' sul-unicorn-devs@lists.stanford.edu
cat $LOG/illiad.log | $REM_CR | grep 'SKIPPING' | mailx -s 'Rejected ILLiad Users Log' stfborrowing@stanford.edu
rm $KEY_DIR/userload.keys.*_uniq
