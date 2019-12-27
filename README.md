# FAIR Data Point

[![Build Status](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=master)](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/61f029299b814ca8be2b8edbaab6ce50)](https://www.codacy.com/app/rajaram5/FAIRDataPoint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DTL-FAIRData/FAIRDataPoint&amp;utm_campaign=Badge_Grade)
[![Dependency Status](https://www.versioneye.com/user/projects/589dd946940b230031fbadd6/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/589dd946940b230031fbadd6)
[![Coverage Status](https://coveralls.io/repos/github/DTL-FAIRData/FAIRDataPoint/badge.svg?branch=master)](https://coveralls.io/github/DTL-FAIRData/FAIRDataPoint?branch=master)

`FAIRDataPoint` is a REST API for creating, storing, and serving `FAIR metadata`. The metadata contents are generated `semi-automatically` according to the [FAIR Data Point software specification](https://dtl-fair.atlassian.net/wiki/display/FDP/FAIR+Data+Point+software+specification) document.

More information about FDP and how to deploy can be found at [FDP Public Documentation](https://fairdatapoint.readthedocs.io/).

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

## Security

Most of the `GET` requests are publicly accessible compares to `POST`, `PUT`, and `PATCH` requests, which are mainly secured. We use [JWT Tokens](https://jwt.io/) and [Bearer Token Authentication](https://swagger.io/docs/specification/authentication/bearer-authentication/). The token can be retrieved in `/tokens` endpoint where you send username and password. 

**Default users**

- **ADMIN:**
    - Username: `albert.einstein@example.com`
    - Password: `password`
- **USER:**
    - Username: `nikola.tesla@example.com`
    - Password: `password`


## API documentation

`FAIRDataPoint` (FDP) API comes with an embedded [swagger documentation](http://swagger.io/), the details of API calls can be found here. To access the FDP swagger document please visit the following url via web browser `http://localhost:8080/swagger-ui.html` 

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for more details.
