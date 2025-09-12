# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.18.1]

## [1.18.0]

## [1.17.6]

## [1.17.5]

## [1.17.4]

## [1.17.3]

From `1.17.3` upwards, changes are logged using github releases. Click the headers to see changes.

## [1.17.2]

### Fixed

- Fix metadata schemas (defaults, ordering, FDP shape)
- Default license in metadata schemas and configuration

## [1.17.1]

❗Manual check of custom and customized metadata schemas is required.

### Fixed

- Use `dcterms:hasVersion` has been changed to `dcat:version`

## [1.17.0]

### Added

- Support for configuration using environment variables

### Changed

- Updated to Spring Boot 3
- Updated several other dependencies

### Fixed

- Reset metadata schema to defaults
- Validation on metadata record deletion

## [1.16.2]

### Fixed

- Target class URIs detection

## [1.16.1]

### Fixed

- Permissions in DB migration (caused by refactoring)

## [1.16.0]

### Added

- Application title and subtitle in config and settings
- Possibility to configure ping endpoints in config file
- SHACL preview for metadata schemas
- (Index) Cleanup of harvested records before next harvesting

### Fixed

- Child UUIDs for a metadata schema repetition due to versions

## [1.15.0]

### Added

- Extended search (filters, simple and complex query)
- Add saved search queries
- Several dependencies updated

## [1.14.0]

### Added

- Security audit via GitHub Actions (Snyk and CodeQL)

### Changed

- Introduced metadata schemas (as replacement of shapes) including versioning and importing
- Updated RDF4J to 4.0
- Several dependencies updated

## [1.13.2]

### Fixed

- Harvest also `fdp-o:MetadataService` in FDP Index

## [1.13.1]

### Changed

- Upgrade to Spring Boot 2.6.6 due to [CVE-2022-22965](https://tanzu.vmware.com/security/cve-2022-22965) ([more info](https://spring.io/blog/2022/03/31/spring-boot-2-6-6-available-now))

## [1.13.0]

### Added

- Profile resources contain `rdfs:label` with Shape name

### Changed

- Replaced `**` wildcards with safer pattern
- Added restriction to URL prefixes of Resource Definitions (`[a-zA-Z_-]*`)
- Upgrade Java JDK from 16 to 17
- Updated SpringDoc OpenAPI UI and several other dependencies
- Compliance with FDP-O ontology (`fdp-o:FAIRDataPoint`)

### Fixed

- Missing `xsd` prefix in some default metadataSchemas

## [1.12.4]

### Changed

- Forcing log4j (indirect dependency) to v2.17.1 due to [CVE-2021-44832](https://logging.apache.org/log4j/2.x/)

## [1.12.3]

### Changed

- Forcing log4j (indirect dependency) to v2.17.0 due to [vulnerability](https://spring.io/blog/2021/12/10/log4j2-vulnerability-and-spring-boot)

## [1.12.2]

### Changed

- Forcing log4j (indirect dependency) to v2.16.0 due to [vulnerability](https://spring.io/blog/2021/12/10/log4j2-vulnerability-and-spring-boot) (again)

## [1.12.1]

### Changed

- Forcing log4j (indirect dependency) to v2.15.0 due to [vulnerability](https://spring.io/blog/2021/12/10/log4j2-vulnerability-and-spring-boot)

## [1.12.0]

### Added

- New endpoints for settings (metrics and ping)

### Changed

- Reset to defaults works with settings as well
- Metadata endpoint `**/expanded` is marked as *Deprecated*
- Several dependencies updated (including Spring Boot 2.5.3)

### Fixed

- Multiple children resource definitions with the same child relation
- Ordering of target classes
- Computing cache on DB migration and reset to defaults
- Profile namespace `/` vs `#`
- Creating metadata of a resource definition that has multiple parents

## [1.11.0]

### Added

- All metadata have dct:conformsTo with profile based on resource definition
- Resolving labels for RDF resources
- Registration of standard namespaces in RDF output

### Changed

- Resource definitions are related directly to metadataSchemas

## [1.10.0]

### Added

- Allow to change internal metadataSchemas
- Reset to "factory defaults" (users, resource definitions, metadata)

### Changed

- Upgrade Java JDK from 15 to 16

## [1.9.0]

### Added

- Publishing and sharing SHACL metadataSchemas between FDPs
- Pagination for child resources

### Changed

- Migrated API Docs to SpringDoc OpenAPI
- Generating OpenAPI based on resource definitions
- Explicit content types for responses in OpenAPI
- Allow to configure multiple ping endpoints
- Upgrade to Spring Boot 2.4.5
- Migrated from Mongobee to Mongock
- Several minor dependencies updates

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
- Validation for SHACL definitions in metadataSchemas
- Production migration for metadataSchema definitions

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
[1.9.0]: /../../tree/v1.9.0
[1.10.0]: /../../tree/v1.10.0
[1.11.0]: /../../tree/v1.11.0
[1.12.0]: /../../tree/v1.12.0
[1.12.1]: /../../tree/v1.12.1
[1.12.2]: /../../tree/v1.12.2
[1.12.3]: /../../tree/v1.12.3
[1.12.4]: /../../tree/v1.12.4
[1.13.0]: /../../tree/v1.13.0
[1.13.1]: /../../tree/v1.13.1
[1.13.2]: /../../tree/v1.13.2
[1.14.0]: /../../tree/v1.14.0
[1.15.0]: /../../tree/v1.15.0
[1.16.0]: /../../tree/v1.16.0
[1.16.1]: /../../tree/v1.16.1
[1.16.2]: /../../tree/v1.16.2
[1.17.0]: /../../tree/v1.17.0
[1.17.1]: /../../tree/v1.17.1
[1.17.2]: /../../tree/v1.17.2
[1.17.3]: https://github.com/FAIRDataTeam/FAIRDataPoint/releases/tag/v1.17.3
[1.17.4]: https://github.com/FAIRDataTeam/FAIRDataPoint/releases/tag/v1.17.4
[1.17.5]: https://github.com/FAIRDataTeam/FAIRDataPoint/releases/tag/v1.17.5
[1.17.6]: https://github.com/FAIRDataTeam/FAIRDataPoint/releases/tag/v1.17.6
[1.18.0]: https://github.com/FAIRDataTeam/FAIRDataPoint/releases/tag/v1.18.0
[1.18.1]: https://github.com/FAIRDataTeam/FAIRDataPoint/releases/tag/v1.18.1
