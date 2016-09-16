# registry-harvester
Registry harvester that transforms registry people documents into a flat file format for loading into Symphony. This instance has beeen modified from the delivered MAiS project to further transform end export user records to ILLiad to support the Scan and Deliver service via SearchWorks.

#### Checkout the project into /s/SUL on the Symphony server:

git clone https://github.com/sul-dlss/registry-harvester.git Harvester

#### Compile the java classes for the Person harvester
```
cd classes
javac -cp .:../lib/sqljdbc4.jar:../lib/commons-io-2.4.jar:../lib/jdom-2.0.6.jar *.java
```

#### Compile the java classes for the course harvester
```
javac -cp .:../../lib/sqljdbc4.jar:../../lib/commons-io-2.4.jar:../../lib/jdom-2.0.6.jar *.java
```

#### Copy /etc and /conf files from shared_configs

https://github.com/sul-dlss/shared_configs

The shared_configs branches are: registry-harvester-test and registry-harvester-prod.

The server.conf file contains connection info for the ILLiad user export. It is important to make sure that you have the right server.conf file in place so that test data does not get exported to production ILLiad and vice versa.

The harvester.properties file contains the registry connection information. It is important that you have the right harvester.properties file in place so that UAT (User Acceptance Testing) data does not get harvested on production Symphony, and that production registry data does not get harvested in test. If the latter happens you will have to grep | awk out the regids from the harvest.log file and run them against the run/do-harvest-file script.

#### Rerun the registry harvest using a file of keys or IDs

Using a file of University IDs or Registry IDs you can re-harvest the registry documents for those people and they will automatically be populated later in ILLiad and Symphony.

If you need to re-run a batch of registry IDs from a previously run harvest, you can extract the registry IDs using awk:
```
cd /s/SUL/Harvester/log
grep "e.person" harvest.log | awk '{print $9}' | awk -F':' '{print $2}' > regid_file
```
Then use the file as the first argument of the do-harvest-file script:
```
/s/SUL/Harvester/run/do-harvest-file /path/to/regid_file YYYYMMDDHHmm(optional [default is now])
```
Or use the same script with a file of University IDs.

#### Manually populate the ILLiad Users Table

With a file of Symphony user keys or a file of sunet ids you can populate the ILLiad Users table with one the following commands:
```
cd classes
```
With user keys:
```
java -cp .:../lib/sqljdbc4.jar:../lib/commons-io-2.4.jar Pop2ILLiad /path/to/userkey/file
```
With sunet ids:
```
java -cp .:../lib/sqljdbc4.jar:../lib/commons-io-2.4.jar Pop2ILLiad /path/to/sunetid/file sunet
```
