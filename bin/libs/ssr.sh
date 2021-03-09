#!/bin/bash

source "bin/init/env.sh"

git submodule update --init 'ssr-libev/src/main/jni/*'
./gradlew ssr-libev:assembleRelease || exit 1
mkdir -p TMessagesProj/libs
cp ssr-libev/build/outputs/aar/* TMessagesProj/libs
