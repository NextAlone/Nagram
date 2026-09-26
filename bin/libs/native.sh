#!/bin/bash

source "bin/init/env.sh"

OUT=TMessagesProj/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib
DIR=TMessagesProj/src/main/libs

if [ -n "$NATIVE_TARGET" ]; then
  ABIS=("$NATIVE_TARGET")
else
  ABIS=(armeabi-v7a arm64-v8a)
fi

# Prebuilt libs in $DIR take precedence over the CMake output when merging,
# so drop them first or the stale copies get installed back.
for ABI in "${ABIS[@]}"; do
  rm -rf "$DIR/$ABI"
done

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

for ABI in "${ABIS[@]}"; do
  install "$ABI"
done
