#!/usr/bin/env bash

env

## Install Go 1.15+
wget -q -O - https://raw.githubusercontent.com/canha/golang-tools-install-script/master/goinstall.sh | bash -s -- --version 1.15.8

## Install rust
source $HOME/.cargo/env
pushd ss-rust/src/main/rust/shadowsocks-rust
rustup target install armv7-linux-androideabi aarch64-linux-android i686-linux-android x86_64-linux-android
popd

echo "rust.rustcCommand=$HOME/.cargo/bin/rustc" >> local.properties
echo "rust.cargoCommand=$HOME/.cargo/bin/cargo" >> local.properties
echo "rust.pythonCommand=/usr/bin/python3" >> local.properties

_NDK="$ANDROID_HOME/ndk/21.3.6528147"
[ -f "$_NDK/source.properties" ] || _NDK="$ANDROID_NDK_HOME"
[ -f "$_NDK/source.properties" ] || _NDK="$ANDROID_HOME/ndk-bundle"
[ -f "$_NDK/source.properties" ] || echo "Error: NDK Not found"
ANDROID_NDK_HOME=$_NDK

echo "NDK Found: $_NDK"
echo "sdk.dir=$ANDROID_HOME" >> local.properties
echo "ndk.dir=$ANDROID_NDK_HOME" >> local.properties

## Remove unused non-free dependencies
pushd TMessagesProj

sed -i -e /play:core/d build.gradle
sed -i -e /firebase/d build.gradle
sed -i -e /gms/d build.gradle