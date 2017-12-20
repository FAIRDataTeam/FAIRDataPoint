#!/bin/sh
#
# The MIT License
# Copyright © 2017 DTL
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#


# Install nano, git and maven openjdk-8-jdk
apk update
apk upgrade
apk add --no-cache bash nano git maven
apk add --no-cache openjdk8

# clone fairmetadata4j repository
git clone --depth 1 -b develop https://github.com/DTL-FAIRData/fairmetadata4j /fairmetadata4j

# Install fairmetadata4j
cd  /fairmetadata4j
mvn --quiet --fail-fast install

# Clone FAIRDataPoint repository
cd /
git clone --depth 1 -b develop https://github.com/DTL-FAIRData/FAIRDataPoint /FAIRDataPoint

# Build FAIRDataPoint
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