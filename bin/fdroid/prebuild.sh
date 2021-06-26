#!/usr/bin/env bash

source "bin/init/env.sh"

echo "sdk.dir=$ANDROID_HOME" >>local.properties
echo "ndk.dir=$ANDROID_NDK_HOME" >>local.properties

# Install Golang
curl -o golang.tar.gz https://storage.googleapis.com/golang/go1.16.linux-amd64.tar.gz
mkdir "$HOME/.go"
tar -C "$HOME/.go" --strip-components=1 -xzf golang.tar.gz
rm golang.tar.gz
export PATH="$PATH:$HOME/.go/bin"
go version || exit 1

bin/libs/v2ray/init.sh

## Remove unused non-free dependencies
pushd TMessagesProj

sed -i -e /play:core/d build.gradle
sed -i -e /firebase/d build.gradle
sed -i -e /gms/d build.gradle

rm -rf jni/boringssl/fuzz
rm jni/libwebp/swig/libwebp.jar
rm jni/libwebp/gradle/wrapper/gradle-wrapper.jar
rm jni/boringssl/util/ar/testdata/mac/libsample.a
rm jni/boringssl/util/ar/testdata/linux/libsample.a

popd

rm -rf ssr-libev/src/main/jni/pcre/dist/testdata
rm -rf ssr-libev/src/main/jni/mbedtls/programs/fuzz/corpuses
rm -rf ssr-libev/src/main/jni/mbedtls/tests/data_files
