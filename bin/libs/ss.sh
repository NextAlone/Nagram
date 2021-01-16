#!/bin/bash

source "bin/init/env.sh"

./gradlew ss-rust:assembleRelease || exit 1
cp ss-rust/build/outputs/aar/* TMessagesProj/libs