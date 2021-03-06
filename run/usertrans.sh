#!/bin/bash
# Modifies the xsl output and calls the expect program to ftp file to symphony
# use: no parameters

HOME=/s/SUL/Harvester/current
LOG=$HOME/log
DATE=$1

if [ -z $DATE ]
then
    DATE_FULL="$(date +%Y%m%d)"
    DATE_STAMP="$(date +%Y%m%d%H%M)"
else
    DATE_FULL=`echo $DATE | awk '{print substr($1, 1, 8)}'`
    DATE_STAMP=$DATE
fi

SOURCE=/s/SUL/Dataload/Registry

#remove duplicate records and blank lines and sets TODAYSDATE
cat $HOME/out/harvest.out | gawk '!x[$0]++==1' RS=".REC." | sed "s/TODAYSDATE/$DATE_FULL/" | sed '/^$/d' > $HOME/out/harvest.out2

touch $HOME/out/harvest.done

echo "Copying fixed harvest.out file to \"$SOURCE/harvest.$DATE_STAMP\"" >> $LOG/harvest.log
cp $HOME/out/harvest.out2 $SOURCE/harvest.$DATE_STAMP >> $LOG/harvest.log 2>&1

echo "Copying harvest.done file to \"$SOURCE\"" >> $LOG/harvest.log
cp   $HOME/out/harvest.done $SOURCE/ >> $LOG/harvest.log 2>&1
echo "" >> $LOG/harvest.log
