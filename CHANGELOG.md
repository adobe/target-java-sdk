# Changelog
All notable changes to this project will be documented in this file.

## [1.0.0] - 2019-31-10
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
