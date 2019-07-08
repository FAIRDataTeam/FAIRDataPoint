[![Build Status](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=develop)](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/61f029299b814ca8be2b8edbaab6ce50)](https://www.codacy.com/app/rajaram5/FAIRDataPoint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DTL-FAIRData/FAIRDataPoint&amp;utm_campaign=Badge_Grade)
[![Coverage Status](https://coveralls.io/repos/github/FAIRDataTeam/FAIRDataPoint/badge.svg?branch=develop)](https://coveralls.io/github/FAIRDataTeam/FAIRDataPoint?branch=develop)

### Summary 
`FAIRDataPoint` is a REST api for creating, storing and servering `FAIR metadata`. The metadata contents are in this api are generated `semi-automatically` according to the [FAIR Data Point software specification](https://github.com/FAIRDataTeam/FAIRDataPoint-Spec) document. In the current version of api we support `GET, POST and PATCH` requests.

#### Deployment machine's requirement
* JRE 1.8
* Tomcat 7 or higher 

#### How to Install

Make sure your deployment machine meets the requirements listed above. Deploy the `fdp.war` file (download link can be found [here](https://github.com/DTL-FAIRData/FAIRDataPoint/releases)) in your  deployment machine's tomcat server. The instructions for deploying `.war` in the tomcat server can be found in this [link](https://tomcat.apache.org/tomcat-7.0-doc/deployer-howto.html).

#### API documentation
`FAIRDataPoint` (fdp) api comes with an embedded [swagger document] (http://swagger.io/), the details of api calls can be found here. To access the fdp swagger document please visit the following url via web browser
 
 `<TOMCAT_BASE_URL>/fdp/swagger-ui.html` 
 
 `An example swagger doc uri :` http://localhost:8084/fdp/swagger-ui.html
 
In the current implementation the `REPOSITORY` layer metadata is automatically created, however this metadata can be updated through PATCH calls. The metadata of `other` layers can be stored in the `FAIRDataPoint` through POST calls. The table below gives an overview of api calls allowed on different `FAIR metadata` layers. 
 
|Metadata layer|GET|POST|PATCH|
| :---: | :---: | :---: | :---: |
| Repository | Yes | No | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/master/src/main/resources/nl/dtls/fairdatapoint/utils/dtl-fdp.ttl)) |
| Catalog | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/master/src/main/resources/nl/dtls/fairdatapoint/utils/textmining-catalog.ttl)) | No |
| Dataset | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/master/src/main/resources/nl/dtls/fairdatapoint/utils/gda-lumc.ttl)) | No |
| Distribution | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/master/src/main/resources/nl/dtls/fairdatapoint/utils/gda-lumc-sparql.ttl)) | No |


#### List of active FAIRDataPoints

|Short name (dct:title)|Description|Location|
| :---: | :---: | :---: |
|ID card FAIR Data Point (beta) | FDP containing dummified data from Biobanks and Registries | [Link](http://semlab1.liacs.nl:8080/fdp/swagger-ui.html)	
|DTL FAIR Data Point (beta)	| FDP for the fairification doc (VCF); fantom5; GeneDisease/DisGeNet |	[Link](http://dev-vm.fair-dtls.surf-hosted.nl:8082/fdp/swagger-ui.html)
| WikiPathways FDP (beta) | FDP with biological pathways | [Link](http://fdp.wikipathways.org)
