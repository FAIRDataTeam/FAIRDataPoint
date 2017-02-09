[![Build Status](https://travis-ci.org/DTL-FAIRData/FAIRDataPoint.svg?branch=fdp-spec-1_0)](https://travis-ci.org/DTL-FAIRData/FAIRDataPoint)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/61f029299b814ca8be2b8edbaab6ce50)](https://www.codacy.com/app/rajaram5/FAIRDataPoint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DTL-FAIRData/FAIRDataPoint&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://www.versioneye.com/user/projects/589adf56475a4f003b59406e/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/589adf56475a4f003b59406e)
[![Coverage Status](https://coveralls.io/repos/github/DTL-FAIRData/FAIRDataPoint/badge.svg?branch=fdp-spec-1_0)](https://coveralls.io/github/DTL-FAIRData/FAIRDataPoint?branch=fdp-spec-1_0)

### Summary 
`FAIRDataPoint` is a REST api for creating, storing and servering `FAIR metadata`. The metadata contents are in this api are generated `semi-automatically` according to the [FAIR Data Point software specification](https://dtl-fair.atlassian.net/wiki/display/FDP/FAIR+Data+Point+software+specification) document. In the current version of api we are support `GET, POST and PATCH` requests.

#### Deployment machine's requirement
* JRE 1.8
* Tomcat 7 or higher 

#### API documentation
`FAIRDataPoint` (fdp) api comes with an embedded [swagger document] (http://swagger.io/) the details of api calls can be found on the fdp swagger document. To access the fdp swagger document please visit the following url via web browser
 
 `<TOMCAT_BASE_URL>/fdp/swagger-ui.html` 
 
 `An example swagger doc uri :` http://localhost:8084/fdp/swagger-ui.html
 
In the current implementation the `REPOSITORY` layer metadata is automatically created however this metadata can be updated through PATCH calls. The metadata of `other` layers can be stored in the `FAIRDataPoint` through POST calls. The table below gives an overview of api calls allowed on different `FAIR metadata` layers. 
 
|Metadata layer|GET|POST|PATCH|
| :---: | :---: | :---: | :---: |
| Repository | Yes | No | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/fdp-spec-1_0/src/main/resources/nl/dtls/fairdatapoint/utils/dtl-fdp.ttl)) |
| Catalog | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/fdp-spec-1_0/src/main/resources/nl/dtls/fairdatapoint/utils/textmining-catalog.ttl)) | No |
| Dataset | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/fdp-spec-1_0/src/main/resources/nl/dtls/fairdatapoint/utils/gda-lumc.ttl)) | No |
| Distribution | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/fdp-spec-1_0/src/main/resources/nl/dtls/fairdatapoint/utils/gda-lumc-sparql.ttl)) | No |


