#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

export LD_LIBRARY_PATH="/usr/lib/jvm/default-java/lib/"
java -classpath target/linux-shimeji-0.0.1-SNAPSHOT-jar-with-dependencies.jar -Xmx1000m com.group_finity.mascot.Main
