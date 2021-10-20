#!/bin/bash

export LD_LIBRARY_PATH="/usr/lib/jvm/default-java/lib/"
java -classpath /usr/share/linux-shimeji/shimeji.jar -Xmx1000m com.group_finity.mascot.Main >/tmp/linux-shimeji.log 2>&1
