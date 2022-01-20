#!/bin/bash

. /s/SUL/Config/sirsi.env

[[ -s "/usr/local/rvm/scripts/rvm" ]] && source "/usr/local/rvm/scripts/rvm" # Load RVM into a shell session *as a function*

HOME=/s/SUL/Harvester/current
LOG=$HOME/log
OUT=$HOME/out
XSLT=$HOME/xslt
FOLIO=/s/SUL/Bin/folio_api_client/current
KEYS=$2
DATE=$3

if [[ ! "$IS_PRODUCTION" ]]; then
  UAT="-uat"
fi

# Run the registry harvest
if [[ $1 == 'file' ]]; then
  $HOME/run/person_file_load.sh $KEYS
else
  $HOME/run/person_runonce.sh
fi

if [[ -z $DATE ]]; then
  DATE=`date +%Y%m%d%H%M`
fi

# Remove carriage returns from the log
perl -p -i -e 's/\r//g' $LOG/harvest.log

# Use this when using the LibraryPatron Java xslt transformer instead of the packaged one provided by MaIS
perl -p -i -e "s/<!DOCTYPE Person SYSTEM \"http:\/\/registry${UAT}.stanford.edu\/xml\/person\/1.2\/Person.dtd\">//g" $OUT/harvest.xml.out
sed -i '/^$/d' $OUT/harvest.xml.out

# Generate the flat file for Symphony
java -cp $HOME/lib/Person-jar-with-dependencies.jar edu.stanford.LibraryPatron $OUT/harvest.xml.out $XSLT/library_patron.xsl > $OUT/harvest.out

# Fix up the harvest.out files
$HOME/run/usertrans.sh $DATE

# Send yesterday's keys to ILLiad
illiad_date=`/s/sirsi/Unicorn/Bin/transdate -d-1`
echo "Updating/Inserting keys from /s/SUL/Batchlog/userload.keys.$illiad_date into ILLiad" >> $LOG/harvest.log
$HOME/run/pop2illiad.sh $illiad_date

# Run harvest.xml.out through folio_api_client ruby script to load users into FOLIO
if [[ $FOLIO ]]; then
  cd $FOLIO
  # Split into batches of 1000
  batch=0
  while mapfile -t -n 1000 array && ((${#array[@]}))
  do
      batch++
      printf '%s\n' "${array[@]}" > $OUT/tmp.xml
      echo "----------batch ${batch}----------" >> $LOG/folio.log
      STAGE="${STAGE}" ruby bin/folio_user.rb $OUT/tmp.xml >> $LOG/folio.log 2> $LOG/folio_err.log
      rm $OUT/tmp.xml
  done < $OUT/harvest.xml.out
fi

# Email and move/reset work files
cat $LOG/harvest.log | mailx -s 'Harvest Log' sul-unicorn-devs@lists.stanford.edu
cat $LOG/folio.log | mailx -s 'Folio User Load' sul-unicorn-devs@lists.stanford.edu

# Save output files
mv $OUT/harvest.out $OUT/harvest.out.$DATE
mv $OUT/harvest.xml.out $OUT/harvest.xml.out.$DATE

# Save and reset log files
mv $LOG/harvest.log $LOG/harvest.log.$DATE
mv $LOG/illiad.log $LOG/illiad.log.$illiad_date.$DATE
mv $LOG/folio.log $LOG/folio.log.$DATE
mv $LOG/folio_err.log $LOG/folio_err.log.$DATE

touch $LOG/harvest.log
touch $LOG/illiad.log
touch $LOG/folio.log
touch $LOG/folio_err.log

usage(){
    echo "Usage: $0 [ no argument | 'file' ] [ file of user keys (if arg0 == file) ] [ DATE (optional: to append to log and out files) ]"
    exit 1
}

[[ $0 =~ "help" ]] && usage
