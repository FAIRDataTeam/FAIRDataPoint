# FAIR Data Point

[![Build Status](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=master)](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/61f029299b814ca8be2b8edbaab6ce50)](https://www.codacy.com/app/rajaram5/FAIRDataPoint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DTL-FAIRData/FAIRDataPoint&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://www.versioneye.com/user/projects/589dd946940b230031fbadd6/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/589dd946940b230031fbadd6)
[![Coverage Status](https://coveralls.io/repos/github/DTL-FAIRData/FAIRDataPoint/badge.svg?branch=master)](https://coveralls.io/github/DTL-FAIRData/FAIRDataPoint?branch=master)

`FAIRDataPoint` is a REST API for creating, storing, and serving `FAIR metadata`. The metadata contents are generated `semi-automatically` according to the [FAIR Data Point software specification](https://dtl-fair.atlassian.net/wiki/display/FDP/FAIR+Data+Point+software+specification) document.

## Features

- Store catalogs, datasets, and distributions 
- Manage users
- Manage access rights to your catalogs, datasets, and distributions
- Integration with [FAIR Data Point Client](https://github.com/FAIRDataTeam/FAIRDataPoint-client)

## Deployment

FAIR Data Point is distributed as a Docker image. For a basic setup, you need to run just [Mongo DB database](https://docs.mongodb.com/). You can use Docker Compose to run FDP and Mongo DB together:

1.  Create a folder (e.g., `/fdp`) and enter it
2.  Copy docker-compose.yml provided below
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

  mongo:
      image: mongo:4.0.12
      restart: always
      ports:
        - 27017:27017
      command: mongod
```

### Override application configuration

You can override default settings in `application-production.yml` file. You can take inspiration in the [default configuration](https://github.com/FAIRDataTeam/FAIRDataPoint/blob/develop/src/main/resources/application.yml) and [default production configuration](https://github.com/FAIRDataTeam/FAIRDataPoint/blob/develop/src/main/resources/application-production.yml). Create the `application-production.yml` in the `/fdp` folder and attach it into a docker container using `volumes` directive.

```
fdp:
    image: fairtools/fairdatapoint
    restart: always
    volumes:
      - ./application-production.yml:/fdp/application-production.yml
    ports:
      - 80:8080
```

**Possible configuration**

Here you can list possible configuration. The configuration marked as required should be addressed if you are intending to use the FDP professionally.
 
| Customization                  | Level         | Description   |
| ------------------------------ | ------------- | ------------- |
| **Application (instance) URL** | **Required**  | Override property `instance.url` (e.g., `http://fdp-staging.fair-dtls.surf-hosted.nl`) |
| Server Port                    | Optional      | Override property `server.port` (e.g., `80`) |
| **JWT Token secret**           | **Required**  | Override property `security.jwt.token.secret-key` |
| Metadata Properties            | Optional      | Override property `metadataProperties` with nested properties: `rootSpecs`, `catalogSpecs`, `datasetSpecs`, `distributionSpecs`, `publisherURI`, `publisherName`, `language`, `license`, `accessRightsDescription` |
| Metadata Metrics               | Optional      | Override property `metadataMetrics`. Nested properties are captured as Map with metric uri as a key (e.g., `https://purl.org/fair-metrics/FM_F1A`) and with its value (e.g., `https://www.ietf.org/rfc/rfc3986.txt`)
| PID                            | Optional      | Override property `pid`. You can choose between 2 types of persistent identifiers (`default PIDSystem (1)`, `purl.org PID System (2)`). Select one of those and write the number of the type into `type` property. To configure the concrete PID System, create a property named by the type of the PID System and include the required information for that repository. For `default`, you don't need to configure anything. For `purl`, you need to configure `baseUrl`.
| **Mongo DB**                   | **Required**  | Override property `spring.data.mongodb.uri` with connection string (e.g. `mongodb://mongo:27017/fdp`)
| Triple Store                   | **Required**  | Override property `repository`. You can choose between 5 types of triple stores (`inMemoryStore (1)`, `NativeStore (2)`, `AllegroGraph (3)`, `graphDB (4)`, `blazegraph (5)`). Select one of those and write the number of the type into `type` property. To configure the concrete repository, create a property named by the type of repository and include the required information for that repository. For `agraph`, you need to configure `url`, `username` and `password`. For `graphDb`, you need to configure `url` and `repository`. For `blazegraph`, you need to configure `url` and `repository`. And for `native`, you need to configure `/tmp/fdp-store`. |
   
**Run application in nested URL**

If you plan to use FAIR Data Point in the nested URL (e.g., `https://example.com/fairdatapoint`), you need to configure an environment variable `PUBLIC_PATH` (same as in FAIR Data Point Client). For setting it up, add `environment` directive to your `docker-compose.yml` file.

```
fdp:
    image: fairtools/fairdatapoint
    restart: always
    environment:
      - PUBLIC_PATH=/fairdatapoint
    ports:
      - 80:8080
```
   
## How to contribute

### Install requirements

**Stack:**

 - **Java** (recommended JDK 11)
 - **Maven** (recommended 3.2.5 or higher)
 - **Docker** (recommended 17.09.0-ce or higher) - *for build of production image*

**Additional libraries:**

1. Install `fairmetadata4j`

    ```bash
    $ git clone https://github.com/FAIRDataTeam/fairmetadata4j
    $ cd fairmetadata4j
    $ mvn install
    ```
2. Install `spring-rdf-migration`

    ```
    $ git clone https://github.com/FAIRDataTeam/spring-rdf-migration.git
    $ cd spring-rdf-migration
    $ mvn install
    ```

3. Install `spring-security-acl-mongodb`

    ```
    $ git clone https://github.com/FAIRDataTeam/spring-security-acl-mongodb
    $ cd spring-security-acl-mongodb
    $ mvn install
    ```

### Build & Run

Run these commands from the root of the project

```bash
$ mvn spring-boot:start
```

### Run tests

Run these commands from the root of the project

```bash
$ mvn test
```

### Package the application

Run these commands from the root of the project

```bash
$ mvn package
```

### Create a Docker image

Run these commands from the root of the project

```bash
$ docker build -t fairdata/fairdatapoint .
```

### Security

#### From user point of view
We have two levels of accessibility in FDP. All resources (e.g., catalogs, datasets,...) are publicly accessible. You don't need to be logged in to browse them. If you want to upload your own resources, you need to be logged in. To get an account, you need to contact an administrator of the FDP. By default, all uploaded resources are publicly accessible by anyone. But if you want to allow someone to manage your resources (edit/delete), you need to allow it in the resource settings. 

We have two types of roles in FDP - an administrator and a user. The administrator is allowed to manage users and all resources. The user can manage just the resources which he owns.


#### From technical point of view  

Most of the `GET` requests are publicly accessible compares to `POST`, `PUT`, and `PATCH` requests, which are mainly secured. We use [JWT Tokens](https://jwt.io/) and [Bearer Token Authentication](https://swagger.io/docs/specification/authentication/bearer-authentication/). The token can be retrieved in `/tokens` endpoint where you send username and password. 

**Default users**

- **ADMIN:**
    - Username: `albert.einstein@example.com`
    - Password: `password`
- **USER:**
    - Username: `nikola.tesla@example.com`
    - Password: `password`


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
