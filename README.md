# registry-harvester
Registry harvester that transforms registry people documents into a flat file format for loading into Symphony. This instance has beeen modified from the delivered MAiS project to further transform end export user records to ILLiad to support the Scan and Deliver service via SearchWorks.

## Installation and setup

### One Time Setup

Dependencies
- Java 8
- Maven 3
- Oracle maven artifact access (see below)

The Oracle JDBC maven artifacts require a license, follow the instructions at:
- http://docs.oracle.com/middleware/1213/core/MAVEN/config_maven_repo.htm
- https://blogs.oracle.com/dev2dev/entry/oracle_maven_repository_instructions_for

Once the Oracle sign-up/sign-in and license agreement is accepted, add the sign-in
credentials to maven settings.  Follow maven instructions to encrypt the passwords, see
- https://maven.apache.org/guides/mini/guide-encryption.html
  - encrypt a maven master password:

          $ mvn --encrypt-master-password
          Master password: TYPE_YOUR_PASSWD_HERE
          {L+bX9REL8CAH/EkcFM4NPLUxjaEZ6nQ79feSk+xDxhE=}

  - add the encrypted maven master password to `~/.m2/settings-security.xml` in a block like:

          <settingsSecurity>
              <master>{L+bX9REL8CAH/EkcFM4NPLUxjaEZ6nQ79feSk+xDxhE=}</master>
          </settingsSecurity>

  - encrypt oracle server password:

          $ mvn --encrypt-password
          Password: TYPE_YOUR_PASSWD_HERE
          {JhJfPXeAJm0HU9VwsWngQS5qGreK29EQ3fdm/7Q7A7c=}

  - add this encrypted oracle server password to `~/.m2/settings.xml` as a `server` element using this template:

          <settings>
            <servers>
              <server>
                <id>maven.oracle.com</id>
                <username>your_oracle_username</username>
                <password>{JhJfPXeAJm0HU9VwsWngQS5qGreK29EQ3fdm/7Q7A7c=}</password>
                <configuration>
                  <basicAuthScope>
                    <host>ANY</host>
                    <port>ANY</port>
                    <realm>OAM 11g</realm>
                  </basicAuthScope>
                  <httpConfiguration>
                    <all>
                      <params>
                        <property>
                          <name>http.protocol.allow-circular-redirects</name>
                          <value>%b,true</value>
                        </property>
                      </params>
                    </all>
                  </httpConfiguration>
                </configuration>
              </server>
            </servers>
          </settings>

- For additional information about maven settings, see
    - https://maven.apache.org/settings.html
    - https://books.sonatype.com/nexus-book/reference/_adding_credentials_to_your_maven_settings.html
    
#### Checkout the project into /s/SUL on the Symphony server:

git clone https://github.com/sul-dlss/registry-harvester.git Harvester

#### Create the CourseBuild and PersonToILLiad JAR files

Using Maven:
```
mvn clean install
```

#### Copy the config files from shared_configs

https://github.com/sul-dlss/shared_configs

The shared_configs branches are: `registry-harvester-test` and `registry-harvester-prod`. You only need to do this
one time unless you make further configuration changes:

- Log on to the Symphony box and navigate into the `Harvester/shared` directory
- `git clone` the https://github.com/sul-dlss/shared_configs.git repo
- `git fetch`
- `git checkout registry-harvester-{stage}`
- `cp -rlf * ../` 

The server.conf file contains connection info for the ILLiad user export. 
It is important to make sure that you have the right server.conf file in place so that 
test data does not get exported to production ILLiad and vice versa.

The harvester.properties file contains the registry connection information. 
It is important that you have the right harvester.properties file in place so that 
UAT (User Acceptance Testing) data does not get harvested on production Symphony, and that 
production registry data does not get harvested in test. If that happens you will have to `grep | awk` 
out the regids from the harvest.log file and run them against the run/do-harvest-file script.

#### Create a LastRun directory for the course xml files
If it is not already there:
```
mkdir course_files/LastRun
```

## Using the registry harvester

The person registry harvester is normally run via the cron on libappprod1. The `do-harvest` script makes calls to a few other scripts to do log file preparation and to run the process to update the ILLiad database with data that was previously loaded into Symphony.
```
15 21 * * * /s/SUL/Harvester/current/run/do-harvest  > /s/SUL/Harvester/current/log/harvest.log 2>&1
```

Similarly, the course harvester is also normally run via cron with a subsequent script to build and copy the courseXML files for the <a href="https://github.com/sul-dlss/course_reserves">Course Reserves App</a>.
```
15 15 * * 1-5 /s/SUL/Harvester/current/run/do-course-harvest  > /s/SUL/Harvester/current/log/course-harvest.log 2>&1
40 7 * * 1-5 /s/SUL/Harvester/current/run/course-build 3G 12G latest > /s/SUL/Harvester/current/log/course_build.err 2>&1
```

In some circumstances it may be necessary to run harvests or the course build manually.

#### Rerun a registry harvest using a file of keys or IDs

Using a file of University IDs, Registry IDs, or Course IDs you can re-harvest the registry documents for those people or courses, and they will automatically be populated later in ILLiad and Symphony, or included in the courseXML build respectively.

<b>Person</b>: If you need to re-run a batch of registry IDs from a previously run harvest, you can extract the registry IDs using awk:
```
cd /s/SUL/Harvester/current/log
grep "e.person" harvest.log | awk '{print $9}' | awk -F':' '{print $2}' > regid_file
```
Then use the file as the first argument of the do-harvest-file script:
```
/s/SUL/Harvester/current/run/do-harvest-file /path/to/regid_file YYYYMMDDHHmm(optional [default is now])
```
Or use the same script with a file of University IDs.

<b>Course</b>: If you need to re-run a batch of course IDs you can similarly extract the course IDs for a particular term using the term code as 4 digits: 1 (year 2000) 17 (16-17) 2/4/6/8(fall/winter/spring/summer):
```
cd /s/SUL/Harvester/current/log
grep "1174" course_harvest.log.201612021153 | awk '{print $12}' > courseid_file
```
Then use the file as the first argument of the do-course-harvest-file script:
```
/s/SUL/Harvester/current/run/do-course-harvest-file /path/to/courseid_file
```

#### Manually populate the ILLiad Users Table

With a file of Symphony user keys or a file of sunet ids you can populate the ILLiad Users table with one the following commands:
```
cd /s/SUL/Harvester/current/lib
```
With user keys:
```
java -jar Person-jar-with-dependencies.jar edu.stanford.Pop2ILLiad /path/to/userkey/file
```
With sunet ids:
```
java -jar Person-jar-with-dependencies.jar edu.stanford.Pop2ILLiad /path/to/sunetid/file sunet
```
#### Run the Course Build

As mentioned above, you can cron the job by setting the appropriate date on the line that looks like this:
```
10 10 10 10 * /s/SUL/Harvester/current/run/course-build 4G 16G > /s/SUL/Harvester/current/log/course_build.log 2>&1
```
Or simply run that command from the command line with an extra `&` at the end. The arguments represent the java heap size, Xms and Xmx respectively. 
To run an update using one or more new course_harvest.out files, append the file name(s) as a third argument. 
If there is no third argument the course-build will rerun on all course_harvest.out* files in the /s/SUL/Harvester/current/out directory, 
which will take many hours to complete. If you append `latest` as the third argument, it will run the course build on the most recent course_harvest.out file. If you do not supply any arguments, it will run the build on all files with the JVM defaults of -Xms2G -Xmx10G.

Prior to executing the course build, the `course-build` script will move all of the old `course_harvest.out` files to the `OldCourses` directory as specified by the codified term schedule in the `run/remove_old_course_harvests.rb` script.

#### Specifying the course terms to build

You can run the course build for one or more particular terms (fall, winter, spring, summer) in order to speed up processing. Do this by setting the system COURSE_TERMS environment variable:
```
$ export COURSE_TERMS=summer,spring,winter,fall
```

<b>Important</b>: The default set of COURSE_TERMS is specified in the /s/SUL/Config/sirsi.env file. 
During each intersession it helps to make sure you are building courseXML files for the terms that 
are needed by the Course Reserves App (the current term and the next upcoming term).

#### Check on the status of the course build while it is running

From the Harvester root directory `/s/SUL/Bin/Harvester` do `tail log/course_build.log` which will show you the last few courses processed. `grep "Processing" log/course_build.log` will show you the course_harvest.out files that were processed. To see the actual process information this command is useful: `ps aux --sort -rss | less`

The BuildCourseXML java process will most likely be at the very top. You can also see the courseXML files and the registry term xml saved files being built by listing the `includes/courses/` directory.
