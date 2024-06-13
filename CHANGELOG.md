# Changelog
All notable changes to this project will be documented in this file.

## [2.6.0] - 2024-06-13
### Added
- (On Device Decisioning) Added new configuration option (`shouldArtifactRequestBypassProxyCache`).  When enabled and a proxy is configured, send empty Authorization header on artifact rules request to bypass proxy-level cache

## [2.5.1] - 2024-01-12
### Fixed
- Fix geo context not being cleaned up
## [2.5.0] - 2022-09-29
### Added
- Expose new connection pool configuration options (`connectionTtlMs`, `idleConnectionValidationMs`, `evictIdleConnectionsAfterSecs`) 
- Document configuration builder API
### Fixed
- Default idle connection validation reduced from `2` seconds to `1` second
- Default idle connection eviction reduced from `30` seconds to `20` seconds
- Fix `requestInterceptor` configuration builder

## [2.4.0] - 2022-10-25
### Added
- Custom HTTP client support to Client configuration
### Fixed
- Additional support for complex domain parsing for on-device-decisioning
- SDK version no longer set in source code

## [2.3.1] - 2022-09-12
### Fixed
- Client custom Mbox parameters now correctly support dot notation
- stickyLocationHint is updated regardless of Decisioning method

## [2.3.0] - 2022-05-26
### Added
- Added ClientHints support

## [2.2.2] - 2022-03-30
### Fixed
- Updated ECID-service dependency to fix parsing expiry values from AMCV cookie
- Add ClientHints to Delivery API model in preparation for ClientHints support

## [2.2.1] - 2022-03-10
### Fixed
- Fixed incorrect logic for execution mode calculation.
- Always use context.beacon=false for sendNotifications() calls.

## [2.2.0] - 2022-01-24
### Added
- Added SDK telemetry data collection.
- Improved unit tests coverage
- Refactored On-Device Decisioning code

## [2.1.6] - 2021-10-04
### Changed
- Improved logging for exceptions.
- `TargetRequestException` now includes the request object for improved debugging.

## [2.1.5] - 2021-08-30
### Changed
- Fix incorrect MaxAge set in response cookies
- (On Device Decisioning) Downloaded artifact will only include activities for specified property if `defaultPropertyToken` is set in ClientConfig

## [2.1.4] - 2021-04-29
### Changed
- Fix incorrect handling of error scenario in `TargetMetrics.begin()`
- Updating ECID Service version
- Fix incorrect handling of traffic allocation

## [2.1.3] - 2021-03-04
### Changed
- (On Device Decisioning) Updated the bucketing algorithm to be consistent across all platforms

## [2.1.2] - 2021-02-11

### Changed
- (On Device Decisioning) Replace blocking notification call with an async call

## [2.1.1] - 2021-02-03
### Changed
- Updating ECID Service version

## [2.1.0] - 2021-01-11
### Changed
- (On Device Decisioning) Updated allocation calculation to be identical to Target Delivery API ( Allocation is determined using the first non-null visitor id in this order 1. ECID, 2. TNTID, 3. Third Party ID )
- (server-side decisioning) Support for using imsOrgId in the config. This can be used instead of client for calls to Delivery API.

## [2.0.0] - 2020-10-27
### Added
- Support for [on-device decisioning](https://adobetarget-sdks.gitbook.io/docs/on-device-decisioning/introduction-to-on-device-decisioning)
- New parameters added in create() when initializing the Java SDK
  - decisioningMethod
  - pollingInterval
  - artifactLocation
  - artifactPayload
  - events
- New method getAttributes() to fetch experimentation and personalized experiences from Target and extract attribute values.
 
## [1.1.0] - 2019-12-16
### Added
- Added support for proxy config. Thanks @hisham-hassan for contribution.


## [1.0.1] - 2019-11-11
### Fixed
- Send supplemental data ID in a Target request even when there is no Visitor API cookie present 

## [1.0.0] - 2019-10-31
### Added
- Target View Delivery v1 API support, including Page Load and View prefetch
- Full support for delivering all types of offers authored in Visual Experience Composer
- Support for prefetching and notifications, that allows for performance optimization by caching prefetched content
- Support for optimizing performance in hybrid Target integrations via serverState, when Target is deployed both on the server-side and on the client-side
- We are introducing a setting called serverState that will contain experiences retrieved via server-side, so that at.js v2.2+ will not make an additional server call to retrieve the experiences. This approach optimizes page load performance.
- Open sourced on GitHub as Target Java SDK
- New sendNotifications() API method, for sending displayed/clicked notifications to Target for content prefetched via getOffers()
- Async support for all delivery APIs
- Validation of SDK API method arguments
- Added README, samples and unit tests
- Added CoC, Contribution guidelines, PR and issue templates

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
