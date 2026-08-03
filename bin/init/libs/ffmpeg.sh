#!/bin/bash

source "bin/init/env.sh"

cd TMessagesProj/jni/third_party || exit 1
git submodule update --init ffmpeg || exit 1

cd ffmpeg || exit 1
git reset --hard
git clean -fdx
cd ..

# Packages the merged include/ tree and the per-ABI archives into
# TMessagesProj/jni/ffmpeg, so libvpx and dav1d must already be built.
./build_ffmpeg.sh || exit 1
