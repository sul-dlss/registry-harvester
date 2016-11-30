# registry-harvester
Registry harvester that transforms registry people documents into a flat file format for loading into Symphony. This instance has beeen modified from the delivered MAiS project to further transform end export user records to ILLiad to support the Scan and Deliver service via SearchWorks.

#### Checkout the project into /s/SUL on the Symphony server:

git clone https://github.com/sul-dlss/registry-harvester.git Harvester

#### Compile the java classes for the Person harvester
```
cd classes
javac -cp .:../lib/sqljdbc4.jar:../lib/commons-io-2.4.jar:../lib/jdom-2.0.6.jar:shared/PropGet.class person/*.java
```

#### Compile the java classes for the course harvester
```
cd classes/
javac -cp .:../lib/commons-io-2.4.jar:../lib/jdom-2.0.6.jar:shared/PropGet.class course/*.java
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
java -cp .:../lib/sqljdbc4.jar:../lib/commons-io-2.4.jar:../shared/PropGet.class Pop2ILLiad /path/to/userkey/file
```
With sunet ids:
```
java -cp .:../lib/sqljdbc4.jar:../lib/commons-io-2.4.jar:../shared/PropGet.class Pop2ILLiad /path/to/sunetid/file sunet
```
#### Run the Course Build

You can crontab the job by setting the appropriate date on the line that looks like this:
```
10 10 10 10 * /s/SUL/Harvester/run/course-build 4G 16G > /s/SUL/Harvester/log/course_build.log 2>&1
```
Or simply run that command from the command line with an extra `&` at the end. The arguments represent the java heap size, Xms and Xmx respectively. To run an update using one or more new course_harvest.out files, append the file name(s) as a third argument. If there is no third argument the course-build will rerun on all course_harvest.out* files in the /s/SUL/Harvester/out directory, which will take many hours to complete. If you append `latest` as the third argument, it will run the course build on the most recent course_harvest.out file. If you do not supply any arguments, it will run the build on all files with the JVM defaults of -Xms2G -Xmx10G.

You can run the course build for one or more particular terms (fall, winter, spring, summer) by uncommenting or creating a line in the conf/terms.conf file, e.g.:
```
TERMS=summer,spring,winter,fall
# TERMS=summer,spring
# TERMS=winter,fall
```

#### Check on the status of the course build while it is running

From the Harvester root directory `/s/SUL/Bin/Harvester` do `tail log/course_build.log` which will show you the last few courses processed. `grep "Processing" log/course_build.log` will show you the course_harvest.out files that were processed. To see the actual process information this command is useful: `ps aux --sort -rss | less`

The BuildCourseXML java process will most likely be at the very top. You can also see the courseXML files and the registry term xml saved files being built by listing the `includes/courses/` directory.
