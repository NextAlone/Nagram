#!/usr/bin/env bash

DEPS=$ANDROID_HOME/ndk-bundle/toolchains/llvm/prebuilt/linux-x86_64/bin

ANDROID_ARM_CC=$DEPS/armv7a-linux-androideabi16-clang
ANDROID_ARM_STRIP=$DEPS/arm-linux-androideabi-strip

ANDROID_ARM64_CC=$DEPS/aarch64-linux-android21-clang
ANDROID_ARM64_STRIP=$DEPS/aarch64-linux-android-strip

ANDROID_X86_CC=$DEPS/i686-linux-android16-clang
ANDROID_X86_STRIP=$DEPS/i686-linux-android-strip

ANDROID_X86_64_CC=$DEPS/x86_64-linux-android21-clang
ANDROID_X86_64_STRIP=$DEPS/x86_64-linux-android-strip

git clone https://github.com/cloudflare/tls-tris -b pwu/esni tls

BASEDIR=$(realpath tls/_dev)

GOENV="$(go env GOHOSTOS)_$(go env GOHOSTARCH)"

BUILD_DIR=${BASEDIR}/GOROOT make -f $BASEDIR/Makefile >&2

export GOROOT="$BASEDIR/GOROOT/$GOENV"

export GO111MOD=on
export CGO_ENABLED=1
export GOOS=android

PKG="github.com/iyouport-org/relaybaton/main"
OUTPUT="relaybaton"
LIB_OUTPUT="lib$OUTPUT.so"
AAR_OUTPUT="$OUTPUT.aar"

go get -v $PKG

ROOT="relaybaton/src/main/jniLibs"

DIR="$ROOT/armeabi-v7a"
mkdir -p $DIR
env CC=$ANDROID_ARM_CC GOARCH=arm GOARM=7 go build -o $DIR/$LIB_OUTPUT -v $PKG
$ANDROID_ARM_STRIP $DIR/$LIB_OUTPUT

DIR="$ROOT/arm64-v8a"
mkdir -p $DIR
env CC=$ANDROID_ARM64_CC GOARCH=arm64 go build -o $DIR/$LIB_OUTPUT -v $PKG
$ANDROID_ARM64_STRIP $DIR/$LIB_OUTPUT

DIR="$ROOT/x86"
mkdir -p $DIR
env CC=$ANDROID_X86_CC GOARCH=386 go build -o $DIR/$LIB_OUTPUT -v $PKG
$ANDROID_X86_STRIP $DIR/$LIB_OUTPUT

DIR="$ROOT/x86_64"
mkdir -p $DIR
env CC=$ANDROID_X86_64_CC GOARCH=amd64 go build -o $DIR/$LIB_OUTPUT -v $PKG
$ANDROID_X86_64_STRIP $DIR/$LIB_OUTPUT