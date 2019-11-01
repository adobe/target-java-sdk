# Adobe Target Java SDK Samples

This repository contains Spring Boot basedsamples for [Target Java SDK](https://github.com/adobe/target-java-sdk).

## Initiating Client

The `TargetClient` is instantiated as a Spring bean in [ClientSampleApplication](src/main/java/com/adobe/target/sample/ClientSampleApplication.java) class
as follows:
```java
@Bean
TargetClient marketingCloudClient() {
    ClientConfig clientConfig = ClientConfig.builder()
            .client("emeaprod4")
            .organizationId("0DD934B85278256B0A490D44@AdobeOrg")
            .build();

    return TargetClient.create(clientConfig);
}
```

## Samples
The samples are divided into 3 sub-sections. All samples have java-docs provided to help explain what
 each API does.

[ProductController](src/main/java/com/adobe/target/sample/controller/ProductController.java): This example
demonstrates live offers being applied to product listing page and product view page.

[TargetController](src/main/java/com/adobe/target/sample/controller/TargetController.java) This example
demonstrates different kind of target integrations for eg. Target-Only, Target-ECID, Target-ECID-CustomerId,
Target-Analytics etc. It also demos how manage sessions using cookies and how to make asynchronous requests.
All of this is demonstrated keeping browser as end client in mind. All the offers are set in `serverState`
variable which are eventually applied by at.js in browser. The web pages displays cookie and serverState
values in html for debugging purposes.

[TargetRestController](src/main/java/com/adobe/target/sample/controller/TargetRestController.java) This
example contains minimal samples to demonstrate target java backend as a rest service. This can serve
various clients like: node.js serving front-end, mobile and IOT devices etc.


## Development

Check out our [Contribution guidelines](.github/CONTRIBUTING.md) as well as [Code of Conduct](CODE_OF_CONDUCT.md) prior
to contributing to Target Java SDK Samples.  