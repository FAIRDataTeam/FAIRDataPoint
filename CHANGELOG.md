# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Publishing and sharing SHACL shapes between FDPs

## [1.8.0]

### Added

- Denylist for FDP Index pings
- Endpoints for managing Index settings from [Client]

### Changed

- Upgrade Java JDK from 14 to 15
- Rate limits use forwarded IP by proxy based on config
- Index settings are moved to the database
- Admin trigger now accepts the same DTO as ping

### Fixed

- Fix metadata not found error

## [1.7.0]

### Added

- Possibility to change profile and password for current user
- FDP Index functionality (moved from [FAIRDataPoint-index](https://github.com/FAIRDataTeam/FAIRDataPoint-index))
- Harvestor for collecting metadata
- Support for PROF profiles
- Metadata search including RDF type

### Fixed

- Fix schema.org URL in pom.xml

## [1.6.0]

### Added

- API keys
- Draft state for stored metadata

## [1.5.0]

### Added

- Support custom resource definitions allowing more children

### Changed

- Upgrade Java JDK from 11 to 14
- Switch to `OffsetDateTime`

## [1.4.0]

### Added

- Ping service (for *call home* functionality)

### Fixed

- Fix saving of nested entities in metadata
- Fix Git app info in actuator endpoint

### Removed

- `themeTaxonomies` on incoming catalog

## [1.3.0]

### Added

- Shape definitions with DASH support
- Endpoint for bootstrapping [Client]
- Validation for SHACL definitions in shapes
- Production migration for shape definitions

### Changed

- Embed fairmetadata4j into the project
- Split instanceUrl to clientUrl and persistentUrl

### Removed

- Internal PID system
- Dashboard identifier

## [1.2.1]

### Fixed

- HTTP XFF (`X-Forwarded-For`) headers and `PUBLIC_PATH` envvar replaced using `instanceUrl`

## [1.2.0]

### Added

- References to related repositories
- Option to customize metamodel (metadata layers)

### Changed

- Switch to GitHub Actions (from Travis CI)
- Swagger config reflects `instanceUrl`
- Reformatted and updated SHACL definitions to use `sh:and` according to the FDP specification

## [1.1.0]

### Added

- Endpoint for [Client] configuration
- Spring Boot Actuator for monitoring and service info

### Changed

- Unified package names (`dtl` to `dtls`)
- Loading of production configuration

### Fixed

- Fix crashing on mapping null descriptions, licenses, etc.

### Removed

- Fallback to `InMemory` when a connection to the configured repository fails

## [1.0.0]

Refactored and cleaned-up version of reference FAIR Data Point implementation supporting a new [FAIRDataPoint-client](https://github.com/FAIRDataTeam/FAIRDataPoint-client).

### Added

- ACLs and use Spring Security with Mongo for authentication
- User Management and Metadata for [FAIRDataPoint-client](https://github.com/FAIRDataTeam/FAIRDataPoint-client)
- Themes caching for catalog and several other optimizations

### Changed

- Complete refactoring, cleaning accumulated changed and deprecations, reformatting code
- Upgrade Java JDK from 8 to 11
- Enhanced Swagger UI API documentation
- Improve CI (on Travis) to automatically build and publish Docker image

### Fixed

- Several fixes of metadata, configuration, tests, and convertors

## [0.1-beta]

The first release of reference FAIR Data Point implementation.

### Added

- REST API according to the FDP specification supporting `GET`, `POST`, and `PATCH` for **repository**, **catalog**, **dataset**, and **distribution** layers
- API documentation using Swagger UI


[Client]: https://github.com/FAIRDataTeam/FAIRDataPoint-client

[Unreleased]: /../../compare/master...develop
[0.1-beta]: /../../tree/0.1-beta
[1.0.0]: /../../tree/v1.0.0
[1.1.0]: /../../tree/v1.1.0
[1.2.0]: /../../tree/v1.2.0
[1.2.1]: /../../tree/v1.2.1
[1.3.0]: /../../tree/v1.3.0
[1.4.0]: /../../tree/v1.4.0
[1.5.0]: /../../tree/v1.5.0
[1.6.0]: /../../tree/v1.6.0

[1.7.0]: /../../tree/v1.7.0

[1.8.0]: /../../tree/v1.8.0
