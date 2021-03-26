#!/bin/bash

source "bin/init/env.sh"

git submodule update --init ss-rust/src/main/rust/shadowsocks-rust
rm -rf ss-rust/build/outputs/aar
./gradlew ss-rust:assembleRelease || exit 1
mkdir -p TMessagesProj/libs
cp ss-rust/build/outputs/aar/* TMessagesProj/libs
