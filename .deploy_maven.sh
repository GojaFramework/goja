#!/bin/sh
#
mvn release:perform -Darguments="-Dmaven.test.skip=true -Dmaven.javadoc.skip=true"
