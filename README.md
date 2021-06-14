# FAIR Data Point

[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/FAIRDataTeam/FAIRDataPoint?sort=semver)](https://github.com/FAIRDataTeam/FAIRDataPoint/releases)
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/FAIRDataTeam/FAIRDataPoint/FAIRDataPoint%20CI)](https://github.com/FAIRDataTeam/FAIRDataPoint/actions)
[![Documentation Status](https://readthedocs.org/projects/fairdatapoint/badge/?version=latest)](https://fairdatapoint.readthedocs.io/en/latest/?badge=latest)
[![License](https://img.shields.io/github/license/FAIRDataTeam/FAIRDataPoint)](https://github.com/FAIRDataTeam/FAIRDataPoint/blob/develop/LICENSE)
[![Docker Pulls](https://img.shields.io/docker/pulls/fairdata/fairdatapoint)](https://hub.docker.com/r/fairdata/fairdatapoint)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/61f029299b814ca8be2b8edbaab6ce50)](https://www.codacy.com/app/rajaram5/FAIRDataPoint?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=DTL-FAIRData/FAIRDataPoint&amp;utm_campaign=Badge_Grade)
[![Coverage Status](https://coveralls.io/repos/github/DTL-FAIRData/FAIRDataPoint/badge.svg?branch=master)](https://coveralls.io/github/DTL-FAIRData/FAIRDataPoint?branch=master)
[![Libraries.io dependency status for GitHub repo](https://img.shields.io/librariesio/github/FAIRDataTeam/FAIRDataPoint)](https://libraries.io/github/FAIRDataTeam/FAIRDataPoint)

[**FAIR Data Point** (FDP)](https://www.fairdatapoint.org) is a REST API for creating, storing, and serving **FAIR
metadata**. This FDP implementation also presents a Web-based graphical user interface (GUI). The metadata contents are
generated **semi-automatically** according to
the [FAIR Data Point software specification](https://github.com/FAIRDataTeam/FAIRDataPoint-Spec) document.

## Usage

More information about FDP, how to deploy it and use it can be found in
the [FDP Deployment Documentation](https://fairdatapoint.readthedocs.io/).

## Related GitHub Projects

- [FAIR Data Point Client](https://github.com/FAIRDataTeam/FAIRDataPoint-client)
- [FAIR Data Point E2E Tests](https://github.com/FAIRDataTeam/FAIRDataPoint-E2E-Tests)
- [FAIR Data Point Documentation](https://github.com/FAIRDataTeam/FAIRDataPoint-Docs)
- [OpenRefine Metadata Extension](https://github.com/FAIRDataTeam/OpenRefine-metadata-extension)

### API Documentation

FAIR Data Point API comes with an embedded [OpenAPI documentation using Swagger](https://swagger.io/specification/). The
details of API calls can be found there. It also allows trying out API calls directly. To access the FDP swagger
document please visit the following url via web
browser [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) (for local deployment)
or `https://your.domain.tld/swagger-ui.html` for your deployment (
e.g. [app.fairdatapoint.org/swagger-ui.html](https://app.fairdatapoint.org/swagger-ui.html)).

## Development

### Technology Stack

- **Java** (JDK 16)
- **MongoDB** (4.2)
- **Maven** (3.2.5 or higher)
- **Docker** (17.09.0-ce or higher) - *for building Docker image only*

### Build & Run

To run the application, a MongoDB instance is required to be running. To configure the MongoDB with standard
connection (`mongodb://localhost:27017/fdp`), simply instruct Spring Boot to use the `development` profile. Then run:

```bash
$ mvn spring-boot:run -Dspring-boot.run.profiles=development
```

Alternatively, create an `application.yml` file in the project root
and [configure the mongodb address](https://fairdatapoint.readthedocs.io/en/latest/deployment/advanced-configuration.html#mongo-db)
, and then run:

```bash
$ mvn spring-boot:run
```

### Run tests

Run from the root of the project:

```bash
$ mvn test
```

### Package the application

Run from the root of the project:

```bash
$ mvn package
```

### Create a Docker image

Run from the root of the project (requires building `jar` file using `mvn package` as shown above):

```bash
$ docker build -t fairdatapoint:local .
```

### Build using Docker

If you do not have Java and Maven locally, you can build the Docker image using Docker (instead of using locally
built `jar` file):

```bash
$ docker build -f Dockerfile.build -t fairdatapoint:local .
```

## Security

Most of the `GET` requests are publicly accessible. In contrast, `POST`, `PUT`, `DELETE`, and `PATCH` requests are
mainly secured. We use [JWT Tokens](https://jwt.io/)
and [Bearer Token Authentication](https://swagger.io/docs/specification/authentication/bearer-authentication/). The
token can be retrieved using `/tokens` endpoint where you send username and password. For details, visit the OpenAPI
documentation.

**Default users**

- **ADMIN:**
    - Username: `albert.einstein@example.com`
    - Password: `password`
- **USER:**
    - Username: `nikola.tesla@example.com`
    - Password: `password`

## Contributing

We maintain a [CHANGELOG](CHANGELOG.md), you should also take a look at our [Contributing guidelines](CONTRIBUTING.md)
and
[Code of Conduct](CODE_OF_CONDUCT.md).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for more details.
