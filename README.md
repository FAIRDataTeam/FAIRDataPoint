# FAIR Data Point

[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/FAIRDataTeam/FAIRDataPoint?sort=semver)](https://github.com/FAIRDataTeam/FAIRDataPoint/releases)
[![Documentation Status](https://readthedocs.org/projects/fairdatapoint/badge/?version=latest)](https://fairdatapoint.readthedocs.io/en/latest/?badge=latest)
[![License](https://img.shields.io/github/license/FAIRDataTeam/FAIRDataPoint)](https://github.com/FAIRDataTeam/FAIRDataPoint/blob/develop/LICENSE)
[![Docker Pulls](https://img.shields.io/docker/pulls/fairdata/fairdatapoint)](https://hub.docker.com/r/fairdata/fairdatapoint)
[![Libraries.io](https://img.shields.io/librariesio/github/FAIRDataTeam/FAIRDataPoint)](https://libraries.io/github/FAIRDataTeam/FAIRDataPoint)

[**FAIR Data Point** (FDP)](https://www.fairdatapoint.org) is a REST API for creating, storing, and serving **FAIR
metadata**. This FDP implementation also presents a Web-based graphical user interface (GUI). The metadata contents are
generated **semi-automatically** according to
the [FAIR Data Point software specification](https://specs.fairdatapoint.org) document.

## Usage

More information about FDP, how to deploy it and use it can be found in
the [FDP Deployment and REST API usage Documentation](https://fairdatapoint.readthedocs.io/).

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
e.g. [app.fairdatapoint.org/swagger-ui.html](https://app.fairdatapoint.org/swagger-ui.html)).  More detailed descriptions and examples of these API calls is available in the [Deployment and Usage instructions](https://fairdatapoint.readthedocs.io/)

## Development

### Technology Stack

- **Java** (JDK 17)
- **MongoDB** (4.2)
- **Maven** (3.2.5 or higher)
- **Docker** (19.03.0-ce or higher) - *for building Docker image only*

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
and [Code of Conduct](CODE_OF_CONDUCT.md).

## Citation

The following paper can be cite as a reference paper for the FAIR Data Point:

    @article{10.1162/dint_a_00160,
    author = {Bonino da Silva Santos, Luiz Olavo and Burger, Kees and Kaliyaperumal, Rajaram and Wilkinson, Mark D.},
    title = "{FAIR Data Point: A FAIR-oriented approach for metadata publication}",
    journal = {Data Intelligence},
    pages = {1-21},
    year = {2022},
    month = {08},
    issn = {2641-435X},
    doi = {10.1162/dint_a_00160},
    url = {https://doi.org/10.1162/dint\_a\_00160},
    eprint = {https://direct.mit.edu/dint/article-pdf/doi/10.1162/dint\_a\_00160/2038268/dint\_a\_00160.pdf}}
    
## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for more details.
