[![Build Status](https://travis-ci.org/DTL-FAIRData/FAIRDataPoint.svg?branch=fdp-spec-1_0)](https://travis-ci.org/DTL-FAIRData/FAIRDataPoint)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f201d68cb6ca4bd192abda93d5d320da)](https://www.codacy.com/app/burger-github/FAIRDataPoint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DTL-FAIRData/FAIRDataPoint&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://www.versioneye.com/user/projects/57ea451779806f003983556f/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57ea451779806f003983556f)
[![Coverage Status](https://coveralls.io/repos/github/DTL-FAIRData/FAIRDataPoint/badge.svg?branch=fdp-spec-1_0)](https://coveralls.io/github/DTL-FAIRData/FAIRDataPoint?branch=fdp-spec-1_0)

### Summary 
In the current implementation we only support `GET` requests and only `FAIR MetaData` are stored and served. The metadata contents should be generated `manually` according to the [FAIR Data Point software specification](https://dtl-fair.atlassian.net/wiki/display/FDP/FAIR+Data+Point+software+specification) document.

#### Deployment machine's requirement
* JRE 1.8
* Tomcat 7 or higher 


#### Configurations
###### Triple store (triple-store.properties)
In the current implementation, we provide an interface to connect to the `standalone triple store`. Current version of FDP has been tested with `Virtuoso` and `AllegroGraph` triple stores.  If you would like to configure your FDP to connect to the standalone triple store, then follow the instructions below.

In the `triple-store.properties` file use the following values
* `store-type=2`   
* `store-url=YOUR_TRIPLE_STORE_SPARQL_ENDPOINT_URL`

It is also possible to configure FDP to use the `inMemory triple store`. Since we don't support `POST` requests in the current implementation we strongly recommend you **not to use** the inMemory triple store option. If you would still like to configure your FDP to use the inMemory triple store, then follow the instructions below.

In the `triple-store.properties` file use the following values
* `store-type=1`   
* `store-prepopulate=true`

> **LOCATION** of triple-store.properties file : `<TOMCAT_BASE>/webapps/fdp/WEB-INF/classes/conf`
/triple-store.properties 

###### Tomcat server (fdp-server.properties)
This is an optional property file, it is primarily used to store the static metadata turtle files into the inMemory triple store. If you configured your FDP to use the inMemory triple store then follow the instructions below.

In the `fdp-server.properties` file use the following values
* base-uri=YOUR_HOST_TOMCAT_URL

`An example base-uri = http://145.100.59.120:8082/`

> **LOCATION** of fdp-server.properties file : `<TOMCAT_BASE>/webapps/fdp/WEB-INF/classes/conf/fdp-server.properties`

**Note :** This property file will be removed when we start supporting `POST` requests 

#### How to upload the metadata files
###### Standalone store
If you configured your FDP to use the `standalone triple store`, then follow the instructions of the respective triple stores for uploading rdf files.

> **Note:** When generating the metadata files, please use the **FDP URL** as rdf file's **BASE URI**
`An example fdp url = http://145.100.59.120:8082/fdp`    

###### InMemory store

If you chose to use the `inMemory triple store`, then please feel free to add/modify the files in the FDP's `example metadata directory`

> **LOCATION** of example metadata directory : `<TOMCAT_BASE>/webapps/fdp/WEB-INF/classes/nl/dtls/fairdatapoint/utils`

> **Adding new files:** If you are adding new metadata files to the `example metadata directory`, then the new rdf files should have `http://www.dtls.nl/fdp` as a **BASE URI**  
