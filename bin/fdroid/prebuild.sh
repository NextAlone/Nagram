#!/usr/bin/env bash

source "bin/init/env.sh"

## Install rust
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- --default-toolchain none -y
source $HOME/.cargo/env
rustup install $(cat ss-rust/src/main/rust/shadowsocks-rust/rust-toolchain)
rustup target install armv7-linux-androideabi aarch64-linux-android i686-linux-android x86_64-linux-android

echo "rust.rustcCommand=$HOME/.cargo/bin/rustc" >>local.properties
echo "rust.cargoCommand=$HOME/.cargo/bin/cargo" >>local.properties
echo "rust.pythonCommand=/usr/bin/python3" >>local.properties

# Install Golang
curl -o golang.tar.gz https://storage.googleapis.com/golang/go1.16.linux-amd64.tar.gz
mkdir "$HOME/.go"
tar -C "$HOME/.go" --strip-components=1 -xzf golang.tar.gz
rm golang.tar.gz
export PATH="$PATH:$HOME/.go/bin"
go version || exit 1

echo "sdk.dir=$ANDROID_HOME" >>local.properties
echo "ndk.dir=$ANDROID_NDK_HOME" >>local.properties

bin/libs/v2ray/init.sh

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
