#!/bin/bash

source "bin/init/env.sh"

OUT=TMessagesProj/build/intermediates/stripped_native_libs/release/out/lib
DIR=TMessagesProj/src/main/libs

export COMPILE_NATIVE=1
./gradlew TMessagesProj:stripReleaseDebugSymbols || exit 1

function install() {
  local ABI="$1"
  if [ ! -f $OUT/$ABI/libtmessages*.so ]; then
    echo ">> Skip $ABI"
    return 0
  fi
  rm -rf $DIR/$ABI
  mkdir -p $DIR/$ABI
  cp $OUT/$ABI/libtmessages*.so $DIR/$ABI
  echo ">> Install $DIR/$ABI/$(ls $DIR/$ABI)"
}

install armeabi-v7a
install arm64-v8a
