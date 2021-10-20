#!/bin/bash

export LD_LIBRARY_PATH="/usr/lib/jvm/default-java/lib/"
java -classpath target/linux-shimeji-0.0.1-SNAPSHOT-jar-with-dependencies.jar -Xmx1000m com.group_finity.mascot.Main
