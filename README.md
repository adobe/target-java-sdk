# Adobe Target Java SDK

The Adobe Target Java SDK uses the [Target View Delivery API] to retrieve and deliver personalized experiences using
best practices. Furthermore, the Java SDK helps manage integrations with Experience Cloud solutions like Visitor API and Adobe 
Analytics.

- Checkout [Server-Side Optimization](https://medium.com/adobetech/server-side-optimization-with-the-new-target-java-sdk-421dc418a3f2) post for benefits and best practices of using Target Java SDK.

- Standalone Spring Boot based sample is available at [target-java-sdk-samples](https://github.com/adobe/target-java-sdk-samples) 

## Getting started

### Prerequisites

- Java 8+
- Maven or Gradle

### Installation  

To get started with Target Java SDK, just add it as a dependency in `gradle` as:
```groovy
compile 'com.adobe.target:target-java-sdk:2.0.0'
```
or `maven` as:
```xml
<dependency>
    <groupId>com.adobe.target</groupId>
    <artifactId>target-java-sdk</artifactId>
    <version>2.0.0 </version>
</dependency>
```

## Super Simple to Use

Please take a look at our [documentation](https://adobetarget-sdks.gitbook.io/docs/sdk-reference-guides/java-sdk) to learn how to use the Java SDK.

## Samples

The Adobe Target Java SDK Samples can be found [here](https://github.com/adobe/target-java-sdk-samples).

## Development

Check out our [Contribution guidelines](.github/CONTRIBUTING.md) as well as [Code of Conduct](CODE_OF_CONDUCT.md) prior
to contributing to Target Java SDK development.  
1. To build the project: `./gradlew build`  
2. To install `java-sdk` locally: `./gradle install`

## Delivery API Client generation

The SDK depends on [Target Open API](https://github.com/adobe/target-openapi). It uses Open API and the `Open API generator` to generate the low level HTTP client.

To be able to use `Target Open API` for code generation, we are leveraging Git subtree.

To refresh the local `target-openapi` subtree, use the command:

```bash
git subtree pull --prefix openapi git@github.com:adobe/target-openapi.git main --squash
```

The openapi-generator config is located in the `codegeneration` directory, but there is no need to invoke it directly. To regenerate the openapi models use the command:  `./gradlew codegen spotlessApply`
