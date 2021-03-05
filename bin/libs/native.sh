#!/bin/bash

source "bin/init/env.sh"

./gradlew TMessagesProj:stripFullFossDebugSymbols || exit 1

OUT=TMessagesProj/build/intermediates/stripped_native_libs/fullFoss/out/lib
DIR=TMessagesProj/src/main/libs
rm -rf $DIR/armeabi-v7a
mkdir -p $DIR/armeabi-v7a
cp $OUT/armeabi-v7a/libtmessages*.so $DIR/armeabi-v7a
rm -rf $DIR/arm64-v8a
mkdir -p $DIR/arm64-v8a
cp $OUT/arm64-v8a/libtmessages*.so $DIR/arm64-v8a
rm -rf $DIR/x86
mkdir -p $DIR/x86
cp $OUT/x86/libtmessages*.so $DIR/x86
rm -rf $DIR/x86_64
mkdir -p $DIR/x86_64
cp $OUT/x86_64/libtmessages*.so $DIR/x86_64
