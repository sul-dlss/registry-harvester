#!/bin/bash

. /s/SUL/Config/sirsi.env

HOME=/s/SUL/Harvester/current
LOG=$HOME/log
OUT=$HOME/out
XSLT=$HOME/xslt
KEYS=$2
DATE=$3

# Run the registry harvest
if [[ $1 == 'file' ]]; then
  $HOME/run/person-file-load.sh $KEYS
else
  $HOME/run/person-runonce.sh
fi

if [[ -z $DATE ]]; then
  DATE=`date +%Y%m%d%H%M`
fi

# Remove carriage returns from the log
sed -i '/\r/d' $LOG/harvest.log

# Use the LibraryPatron Java xslt transformer instead of the packaged one provided by MaIS to get the raw xml lines
# Generate the flat file for Symphony
sed -i '/DOCTYPE Person SYSTEM/d' $OUT/harvest.xml.out
java -cp $HOME/lib/Person-jar-with-dependencies.jar edu.stanford.LibraryPatron $OUT/harvest.xml.out $XSLT/library_patron.xsl > $OUT/harvest.out

# Send yesterday's keys to ILLiad
illiad_date=`/s/sirsi/Unicorn/Bin/transdate -d-1`
echo "Updating/Inserting keys from /s/SUL/Batchlog/userload.keys.$illiad_date into ILLiad" >> $LOG/harvest.log
$HOME/run/pop2illiad.sh $illiad_date

# Run harvest.xml.out through folio_api_client ruby script to load users into FOLIO
$HOME/run/folio-userload.sh

# Email and move/reset work files
cat $LOG/harvest.log | mailx -s 'Harvest Log' sul-unicorn-devs@lists.stanford.edu

# Save output files
mv $OUT/harvest.out $OUT/harvest.out.$DATE
mv $OUT/harvest.xml.out $OUT/harvest.xml.out.$DATE

# Save and reset log files
mv $LOG/harvest.log $LOG/harvest.log.$DATE
mv $LOG/illiad.log $LOG/illiad.log.$illiad_date.$DATE

touch $LOG/harvest.log
touch $LOG/illiad.log

usage(){
    echo "Usage: $0 [ no argument | 'file' ] [ file of user keys (if arg0 == file) ] [ DATE (optional: to append to log and out files) ]"
    exit 1
}

[[ $0 =~ "help" ]] && usage
