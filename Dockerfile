FROM tomcat:8.0-jre8-alpine

MAINTAINER Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>

# Copy setup file
COPY dockerSetup.sh /
# RUN setup file
RUN sh /dockerSetup.sh