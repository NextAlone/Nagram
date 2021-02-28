#!/usr/bin/env bash

## Install rust
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- --default-toolchain none -y
source $HOME/.cargo/env
rustup install $(cat ss-rust/src/main/rust/shadowsocks-rust/rust-toolchain)
rustup target install armv7-linux-androideabi aarch64-linux-android i686-linux-android x86_64-linux-android

echo "rust.rustcCommand=$HOME/.cargo/bin/rustc" >> local.properties
echo "rust.cargoCommand=$HOME/.cargo/bin/cargo" >> local.properties
echo "rust.pythonCommand=/usr/bin/python3" >> local.properties

# Install Golang
curl -o golang.tar.gz https://storage.googleapis.com/golang/go1.15.8.linux-amd64.tar.gz
mkdir -p "$HOME/.go"
tar -C "$HOME/.go" --strip-components=1 -xzf golang.tar.gz
rm golang.tar.gz
export PATH=$PATH:$HOME/.go/bin
go version

# Find Android NDK
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

rm -r jni/boringssl/fuzz
rm jni/libwebp/swig/libwebp.jar
rm jni/libwebp/gradle/wrapper/gradle-wrapper.jar
rm jni/boringssl/util/ar/testdata/mac/libsample.a
rm jni/boringssl/util/ar/testdata/linux/libsample.a

popd

rm -r ssr-libev/src/main/jni/pcre/dist/testdata
rm -r ssr-libev/src/main/jni/mbedtls/programs/fuzz/corpuses
rm -r ssr-libev/src/main/jni/mbedtls/tests/data_files