#!/bin/bash

# Using custom openapi-generator fork based on version 5.2.0 due to the main version introducing breaking changes to our models
rm -rf ./build ../src/main/java/com/adobe/target/delivery/v1
mkdir ../src/main/java/com/adobe/target/delivery/v1
git clone git@github.com:dcottingham/openapi-generator.git
cd openapi-generator
git fetch --tags
git checkout tags/v5.2.0_custom
./mvnw clean install package -Dmaven.javadoc.skip=true -Dmaven.test.skip=true
java -jar modules/openapi-generator-cli/target/openapi-generator-cli.jar generate -g java -c ../config.json -i ../../openapi/delivery/api.yaml -o ../build -t ../template --skip-validate-spec
mkdir ../../src/main/java/com/adobe/target/delivery/v1/model/
cp ../build/src/main/java/com/adobe/target/delivery/v1/model/* ../../src/main/java/com/adobe/target/delivery/v1/model/
