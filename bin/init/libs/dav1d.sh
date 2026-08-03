#!/bin/bash

source "bin/init/env.sh"

cd TMessagesProj/jni/third_party || exit 1
git submodule update --init dav1d || exit 1

cd dav1d || exit 1
git reset --hard
git clean -fdx
cd ..

./build_dav1d.sh || exit 1
