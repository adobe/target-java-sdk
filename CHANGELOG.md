# Changelog
All notable changes to this project will be documented in this file.

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
