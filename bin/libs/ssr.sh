#!/bin/bash

source "bin/init/env.sh"

./gradlew ssr-libev:assembleRelease || exit 1
cp ssr-libev/build/outputs/aar/* TMessagesProj/libs