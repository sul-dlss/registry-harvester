# registry-harvester
Registry harvester that transforms registry people documents into a flat file format for loading into Symphony

## Compile the java classes
cd classes
javac -cp .:../lib/sqljdbc4.jar:../lib/commons-io-2.4.jar:../lib/jdom-2.0.5.jar *.java

## Copy /etc and /conf files from shared_configs
