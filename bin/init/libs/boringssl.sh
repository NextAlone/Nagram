#!/bin/bash

source "bin/init/env.sh"

export NINJA_PATH="$(command -v ninja)"
export PATH=$(echo "$ANDROID_HOME"/cmake/*/bin):$PATH

cd TMessagesProj/jni || exit 1
git submodule update --init boringssl

cd boringssl
git reset --hard
git clean -fdx
cd ..

./patch_boringssl.sh || exit 1
./build_boringssl.sh
