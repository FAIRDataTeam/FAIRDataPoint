# FAIR Data Point

[![Build Status](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=master)](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/61f029299b814ca8be2b8edbaab6ce50)](https://www.codacy.com/app/rajaram5/FAIRDataPoint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DTL-FAIRData/FAIRDataPoint&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://www.versioneye.com/user/projects/589dd946940b230031fbadd6/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/589dd946940b230031fbadd6)
[![Coverage Status](https://coveralls.io/repos/github/DTL-FAIRData/FAIRDataPoint/badge.svg?branch=master)](https://coveralls.io/github/DTL-FAIRData/FAIRDataPoint?branch=master)

`FAIRDataPoint` is a REST API for creating, storing and servering `FAIR metadata`. The metadata contents are in this API are generated `semi-automatically` according to the [FAIR Data Point software specification](https://dtl-fair.atlassian.net/wiki/display/FDP/FAIR+Data+Point+software+specification) document. In the current version of API we support `GET, POST and PATCH` requests.

## Deployment

The simplest way is to use [Docker Compose](https://docs.docker.com/compose/). Requirements are just to have Docker installed, privileges for current user and the Docker daemon started.

1.  Create a folder (e.g., `/fdp`, all commands in this manual are from this working directory)
2.  Copy (and adjust) docker-compose.yml provided below
3.  Run the FAIR Data Point with Docker compose `docker-compose up -d`
4.  After starting up, you will be able to open the FAIR Data Point in your browser on <http://localhost>
5.  You can use `docker-compose logs` to see the logs and `docker-compose down` to stop all the services

```
version: '3'
services:

  fdp:
    image: fairtools/fairdatapoint
    restart: always
    ports:
      - 80:8080
```

## Contribute

### Requirements

 - **Java** (recommended 1.8)
 - **Maven** (recommended 3.2.5 or higher)
 - **Docker** (recommended 17.09.0-ce or higher) - *for build of production image*

### Build & Run

Run these comands from the root of the project

```bash
$ mvn spring-boot:start
```

### Run tests

Run these comands from the root of the project

```bash
$ mvn test
```

### Security

The all `GET` and `OPTIONS` are public-accessible. `POST` and `PATCH` endpoints are secured
and for accessing them, you need to be authorized. We use JWT Tokens for authorization.
The token can be retrieved in `/tokens` endpoint where you send username and password. 
Currently, there exists just one default user (username: `user`, password: `password`) and 
there is no possibility of how to change it.  

### API documentation

`FAIRDataPoint` (FDP) API comes with an embedded [swagger document](http://swagger.io/), the details of API calls can be found here. To access the FDP swagger document please visit the following url via web browser:
 
 `<BASE_URL>/swagger-ui.html` 
 
An example swagger URI: http://localhost:8080/swagger-ui.html
 
In the current implementation the `REPOSITORY` layer metadata is automatically created, however this metadata can be updated through PATCH calls. The metadata of `other` layers can be stored in the `FAIRDataPoint` through POST calls. The table below gives an overview of API calls allowed on different `FAIR metadata` layers. 
 
|Metadata layer|GET|POST|PATCH|
| :---: | :---: | :---: | :---: |
| Repository | Yes | No | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/master/src/main/resources/nl/dtls/fairdatapoint/utils/dtl-fdp.ttl)) |
| Catalog | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/master/src/main/resources/nl/dtls/fairdatapoint/utils/textmining-catalog.ttl)) | No |
| Dataset | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/master/src/main/resources/nl/dtls/fairdatapoint/utils/gda-lumc.ttl)) | No |
| Distribution | Yes | Yes <br/>([Example request body](https://github.com/DTL-FAIRData/FAIRDataPoint/blob/master/src/main/resources/nl/dtls/fairdatapoint/utils/gda-lumc-sparql.ttl)) | No |


### List of active FAIRDataPoints

|Short name (dct:title)|Description|Location|
| :---: | :---: | :---: |
|ID card FAIR Data Point (beta) | FDP containing dummified data from Biobanks and Registries | [Link](http://semlab1.liacs.nl:8080/fdp/swagger-ui.html)	
|DTL FAIR Data Point (beta)	| FDP for the fairification doc (VCF); fantom5; GeneDisease/DisGeNet |	[Link](http://dev-vm.fair-dtls.surf-hosted.nl:8082/fdp/swagger-ui.html)


## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for more details.
