#!/bin/bash

source "bin/init/env.sh"

cd TMessagesProj/jni || exit 1
git submodule update --init tgcalls

cd tgcalls
git reset --hard
git clean -fdx
cd ..

./patches/tgcalls.sh || exit 1
