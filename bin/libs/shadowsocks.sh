#!/bin/bash

source "bin/init/env.sh"

[ -f gradlew ] && CMD="./gradlew" || CMD="gradle"

git submodule update --init ss-rust/src/main/rust/shadowsocks-rust
$CMD ss-rust:assembleRelease || exit 1
cp ss-rust/build/outputs/aar/* TMessagesProj/libs