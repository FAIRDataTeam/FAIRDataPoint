# FAIR Data Point

[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/FAIRDataTeam/FAIRDataPoint?sort=semver)](https://github.com/FAIRDataTeam/FAIRDataPoint/releases)
[![Libraries.io](https://img.shields.io/librariesio/github/FAIRDataTeam/FAIRDataPoint)](https://libraries.io/github/FAIRDataTeam/FAIRDataPoint)
[![License](https://img.shields.io/github/license/FAIRDataTeam/FAIRDataPoint)](https://github.com/FAIRDataTeam/FAIRDataPoint/blob/master/LICENSE)
[![Docker Pulls](https://img.shields.io/docker/pulls/fairdata/fairdatapoint)](https://hub.docker.com/r/fairdata/fairdatapoint)
[![Documentation Status](https://readthedocs.org/projects/fairdatapoint/badge/?version=latest)](https://fairdatapoint.readthedocs.io/en/latest/?badge=latest)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.19236251.svg)](https://doi.org/10.5281/zenodo.19236251)

The [FAIR Data Point (FDP)] provides an HTTP API for creating, storing, and serving FAIR (**F**indable, **A**ccessible, **I**nteroperable, **R**eusable) metadata in the form of [RDF].
The metadata content is generated semi-automatically according to the [FAIR Data Point specification].

## Usage

More information about the FDP, how to deploy it, and how to use it, can be found in the [FDP documentation].

### API Documentation

The FAIR Data Point API also comes with embedded [OpenAPI] documentation based on SwaggerUI.
Each FDP serves its own human friendly API documentation at the path `/swagger-ui.html`, for example, https://app.fairdatapoint.org/swagger-ui.html.
In addition, machine readable API docs (JSON) are available at the path `/v3/api-docs`, for example, https://app.fairdatapoint.org/v3/api-docs.

## Development

### Technology Stack

The FDP runs on the following technology stack and is typically deployed as a Docker container. 

- **Spring-boot**
- **Java**
- **Maven**
- **MongoDB**
- **Docker** (only required for building a Docker image and running the container)

### Build & Run

The FDP requires a MongoDB instance to store its application data, such as user accounts and settings.
This can be achieved by running the official [mongo docker image].

To configure the FDP to use MongoDB with standard connection (`mongodb://localhost:27017/fdp`), instruct Spring Boot to use the `development` profile, as follows:

```bash
$ mvn spring-boot:run -Dspring-boot.run.profiles=development
```

Alternatively, create an [application.yml] file in the project root with the desired configuration settings, and run:

```bash
$ mvn spring-boot:run
```

### Run tests

Tests can be run, from the root of the project, as follows:

```bash
$ mvn test
```

### Create a Docker image

To build a Docker image, run this from the project root:

```bash
$ docker build -f Dockerfile -t fairdatapoint:local .
```

## Security

Most of the API endpoints allow `GET` requests without authentication.
In contrast, `POST`, `PUT`, `DELETE`, and `PATCH` requests typically do require authentication.
We use [JWT Tokens](https://jwt.io/) and [Bearer Token Authentication](https://swagger.io/docs/specification/authentication/bearer-authentication/).
A token can be obtained by posting username and password to the `/tokens` endpoint.
For more details, visit the FDP OpenAPI documentation at `/swagger-ui.html`.

By default, the FDP sets up the following *default users*.

>[!WARNING]
>The default users are convenient for local testing, but they *must* be removed, or modified, *before* exposing the FDP to the public internet.  

- **ADMIN:**
    - Username: `albert.einstein@example.com`
    - Password: `password`
- **USER:**
    - Username: `nikola.tesla@example.com`
    - Password: `password`

## Contributing

Interested in contributing to FDP development?
Take a look at our [contribution guidelines](CONTRIBUTING.md) and [code of conduct](CODE_OF_CONDUCT.md).

## Citation

The following paper can be cited as a reference for the FAIR Data Point:

```bibtex
@article{
  10.1162/dint_a_00160,
  author = {Bonino da Silva Santos, Luiz Olavo and Burger, Kees and Kaliyaperumal, Rajaram and Wilkinson, Mark D.},
  title = "{FAIR Data Point: A FAIR-oriented approach for metadata publication}",
  journal = {Data Intelligence},
  pages = {1-21},
  year = {2022},
  month = {08},
  issn = {2641-435X},
  doi = {10.1162/dint_a_00160},
  url = {https://doi.org/10.1162/dint\_a\_00160},
  eprint = {https://direct.mit.edu/dint/article-pdf/doi/10.1162/dint\_a\_00160/2038268/dint\_a\_00160.pdf}
}
```
    
## License

This project is licensed under the MIT License - see the [LICENSE] file for more details.

## Related GitHub Projects

- [FAIR Data Point Client]
- [FAIR Data Point Documentation]

[FAIR Data Point (FDP)]: https://www.fairdatapoint.org
[FAIR Data Point Client]: https://github.com/FAIRDataTeam/FAIRDataPoint-client
[FAIR Data Point Documentation]: https://github.com/FAIRDataTeam/FAIRDataPoint-Docs
[FAIR Data Point specification]: https://specs.fairdatapoint.org
[FDP documentation]: https://fairdatapoint.readthedocs.io/
[OpenAPI]: https://swagger.io/specification/
[application.yml]: src/main/resources/application.yml
[LICENSE]: LICENSE
[contribution guidelines]: CONTRIBUTING.md
[code of conduct]: CODE_OF_CONDUCT.md
[mongo docker image]: https://hub.docker.com/_/mongo/
[RDF]: https://www.w3.org/TR/rdf11-primer/
