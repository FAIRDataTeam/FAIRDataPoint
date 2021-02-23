# FAIR Data Point

[![Build Status](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=master)](https://travis-ci.org/FAIRDataTeam/FAIRDataPoint.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/61f029299b814ca8be2b8edbaab6ce50)](https://www.codacy.com/app/rajaram5/FAIRDataPoint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DTL-FAIRData/FAIRDataPoint&amp;utm_campaign=Badge_Grade)
[![Coverage Status](https://coveralls.io/repos/github/DTL-FAIRData/FAIRDataPoint/badge.svg?branch=master)](https://coveralls.io/github/DTL-FAIRData/FAIRDataPoint?branch=master)

`FAIR Data Point` (FDP) is a REST API for creating, storing, and serving `FAIR metadata`. This FDP implementation also presents a Web-based graphical user interface (GUI). The metadata contents are generated `semi-automatically` according to the [FAIR Data Point software specification](https://github.com/FAIRDataTeam/FAIRDataPoint-Spec) document.

More information about FDP and how to deploy can be found at [FDP Deployment Documentation](https://fairdatapoint.readthedocs.io/).

## Related projects
- [FAIR Data Point Client](https://github.com/FAIRDataTeam/FAIRDataPoint-client)
- [FAIR Data Point E2E Tests](https://github.com/FAIRDataTeam/FAIRDataPoint-E2E-Tests)
- [FAIR Data Point Docs](https://github.com/FAIRDataTeam/FAIRDataPoint-Docs)
- [OpenRefine Metadata Extension](https://github.com/FAIRDataTeam/OpenRefine-metadata-extension)

## How to contribute

### Install requirements

**Stack:**

 - **Java** (minimal: JDK 15)
 - **Maven** (recommended: 3.2.5 or higher)
 - **Docker** (recommended: 17.09.0-ce or higher) - *for build of production image*

### Build & Run

To run the application, a mongodb instance is required to be running. To configure the mongodb address, instruct spring-boot to use the `development` profile. Run these commands from the root of the project.

```bash
$ mvn spring-boot:run -Dspring-boot.run.profiles=development
```

Alternatively, create an `application.yml` file in the project root and [configure the mongodb address](https://fairdatapoint.readthedocs.io/en/latest/deployment/advanced-configuration.html#mongo-db), and then run these commands from the root of the project.

```bash
$ mvn spring-boot:run
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
