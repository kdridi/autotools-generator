#!/usr/bin/env bash

cd $(dirname $0)

rm -rf target
mvn -Dmaven.test.skip=true assembly:assembly
cp target/autotools-generator-0.0.1-SNAPSHOT-jar-with-dependencies.jar generator.jar
rm -rf target
