#!/bin/bash

# Run from TMessagesProj/jni.

sed -i 's/System.getenv("ADDITIONAL_BUILD_NUMBER") as String/"'$1'"/g' build.gradle
sed -i 's/def fdroid = false/def fdroid = true/g' build.gradle
sed -i 's/System.getenv("APP_ID")/"'$2'"/g' build.gradle
sed -i 's/System.getenv("APP_HASH")/"'$3'"/g' build.gradle
