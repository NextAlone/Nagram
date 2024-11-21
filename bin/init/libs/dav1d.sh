#!/bin/bash

source "bin/init/env.sh"

meson --version || exit 1

cd TMessagesProj/jni || exit 1
git submodule update --init dav1d

cd dav1d
git reset --hard
git clean -fdx
cd ..

./build_dav1d_clang.sh || exit 1
