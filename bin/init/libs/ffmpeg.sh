#!/bin/bash

source "bin/init/env.sh"

cd TMessagesProj/jni || exit 1
git submodule update --init ffmpeg

cd ffmpeg
git reset --hard
git clean -fdx
cd ..

./build_ffmpeg_clang.sh || exit 1
./patch_ffmpeg.sh || exit 1
