
#!/bin/sh

# Install nano, git and maven openjdk-8-jdk
apk update
apk upgrade
apk add --no-cache bash nano git maven
apk add --no-cache openjdk8

# clone fairmetadata4j  lib repo
git clone --depth 1 -b develop https://github.com/DTL-FAIRData/fairmetadata4j /fairmetadata4j

# Install fairmetadata4j
cd  /fairmetadata4j
mvn --quiet --fail-fast install

# Clone fdp repo
cd /
git clone --depth 1 -b develop https://github.com/DTL-FAIRData/FAIRDataPoint /FAIRDataPoint

# Build FDP
cd /FAIRDataPoint
mvn --quiet --fail-fast verify

# Remove ROOT dir
rm -rf /usr/local/tomcat/webapps/ROOT/*

# Add FDP folder to tomcat webapps
cp -rf /FAIRDataPoint/target/fdp/* /usr/local/tomcat/webapps/ROOT

# Remove unused DIR
cd /
rm -rf /FAIRDataPoint
rm -rf /fairmetadata4j
rm -rf /root/.m2/repository/*

# Remove unused apps
apk del git maven openjdk8