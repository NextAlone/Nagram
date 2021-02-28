#!/bin/bash

source "bin/init/env.sh"

git submodule update --init ss-rust/src/main/rust/shadowsocks-rust
./gradlew ss-rust:assembleRelease || exit 1
cp ss-rust/build/outputs/aar/* TMessagesProj/libs
