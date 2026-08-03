#!/bin/bash

source "bin/init/env.sh"

cd TMessagesProj/jni/third_party || exit 1
git submodule update --init libvpx || exit 1

cd libvpx || exit 1
git reset --hard
git clean -fdx
cd ..

./build_libvpx.sh || exit 1
