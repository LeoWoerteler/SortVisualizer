#!/bin/sh

DIR=`dirname $0`

pushd "${DIR}/.." > /dev/null

rm -rf mods runtime

# Compile java files to modular classes
mkdir mods
javac --module-path mods:libs -d target/classes $(find src/main/java -name '*.java')

# Create a modular JAR
jar --create --file mods/sortvisualizer.jar --main-class kn.uni.dbis.cs.sorting.gui.Main -C target/classes .

# To create a native launcher in a self-contained application image
# Create compressed JDK with native launcher
jlink --module-path ${JAVA_HOME}/jmods:mods:lib --add-modules kn.uni.dbis.cs.sorting --output runtime --compress=2 --no-header-files --no-man-pages --strip-debug --dedup-legal-notices=error-if-not-same-content

#TODO use jpackage(r) when released in Java 12

popd > /dev/null