#!/bin/sh
#
mvn release:clean
mvn release:prepare -Darguments="-Dmaven.test.skip=true -Dmaven.javadoc.skip=true"
