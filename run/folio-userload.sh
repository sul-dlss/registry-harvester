#!/bin/bash

API=/s/SUL/Bin/folio-tasks/current
DATE=`date +%Y%m%d%H%M`
HOME=/s/SUL/Harvester/current
LOG=$HOME/log
OUT=$HOME/out

if [[ -z $HARVEST ]]; then
  HARVEST=$OUT/harvest.xml.out
fi

. /s/SUL/Config/sirsi.env

[[ -s "/usr/local/rvm/scripts/rvm" ]] && source "/usr/local/rvm/scripts/rvm" # Load RVM into a shell session *as a function*

if [[ $FOLIO ]]; then
  cd $API
  # Split into batches of 100
  batch=0
  while mapfile -t -n 100 array && ((${#array[@]}))
  do
      let batch=batch+1
      printf '%s\n' "${array[@]}" > $OUT/tmp.xml
      echo "----------batch ${batch}: ${#array[@]} records ----------" >> $LOG/folio.log
      STAGE="${STAGE}" ruby bin/folio_user.rb $OUT/tmp.xml >> $LOG/folio.log 2> $LOG/folio_err.log
      rm $OUT/tmp.xml
  done < $HARVEST

  STAGE="${STAGE}" rake users:deactivate_users > $LOG/folio_inactive.log 2>&1
fi

cat $LOG/folio_err.log $LOG/folio.log | mailx -s 'Folio User Load' sul-unicorn-devs@lists.stanford.edu

# Save and reset log files
mv $LOG/folio.log $LOG/folio.log.$DATE
mv $LOG/folio_err.log $LOG/folio_err.log.$DATE
mv $LOG/folio_inactive.log $LOG/folio_inactive.log.$DATE

touch $LOG/folio.log
touch $LOG/folio_err.log
touch $LOG/folio_inactive.log

usage(){
    echo "Usage: $0 [ no argument | full path to xml.out file ]"
    exit 1
}

[[ $1 =~ "help" ]] && usage
