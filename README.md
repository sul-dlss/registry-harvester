# registry-harvester
Registry harvester that transforms registry people documents into a flat file format for loading into Symphony

Checkout the project into /s/SUL on the Symphony server:

git clone https://github.com/sul-dlss/registry-harvester.git Harvester

### Compile the java classes
cd classes
javac -cp .:../lib/sqljdbc4.jar:../lib/commons-io-2.4.jar:../lib/jdom-2.0.5.jar *.java

### Copy /etc and /conf files from shared_configs

https://github.com/sul-dlss/shared_configs

The shared_configs branches are: registry-harvester-test and registry-harvester-prod.

The server.conf file contains connection info for the ILLiad user export. It is important to make sure that you have the right server.conf file in place so that test data does not get exported to production ILLiad and vice versa.

The harvester.properties file contains the registry connection information. It is important that you have the right harvester.properties file in place so that UAT (User Acceptance Testing) data does not get harvested on production Symphony, and that production registry data does not get harvested in test. If the latter happens you will have to grep | awk out the regids from the harvest.log file and run them against the run/do-harvest-file script.
