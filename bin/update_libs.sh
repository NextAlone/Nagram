#!/bin/bash

if [ ! -x "$(command -v go)" ]; then

#  if [ ! -x "$(command -v gvm)" ]; then
#
#    apt install -y bison
#
#    bash < <(curl -s -S -L https://raw.githubusercontent.com/moovweb/gvm/master/binscripts/gvm-installer)
#
#    source "$HOME/.bashrc"
#
#  fi
#
#  gvm install go1.14 -B
#  gvm use go1.14 --default

  echo "install golang please!"

  exit 1

fi

if [ ! -x "$(command -v rustc)" ]; then

#  curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh

#  rustup target install armv7-linux-androideabi aarch64-linux-android i686-linux-android x86_64-linux-android

  echo "install rust please!"

  exit 1

fi

if [ ! -f "$ANDROID_HOME/ndk-bundle/source.properties" ]; then

  export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/21.3.6528147

fi


rm -rf TMessagesProj/libs/*.aar
./gradlew ss-rust:assembleRelease --stacktrace &&
cp ss-rust/build/outputs/aar/* TMessagesProj/libs &&
./gradlew ssr-libev:assembleRelease &&
cp ssr-libev/build/outputs/aar/* TMessagesProj/libs &&
cd TMessagesProj/libs &&

go get -v golang.org/x/mobile/cmd/... &&

v2rayCore="$(go env GOPATH)/src/v2ray.com/core" &&
rm -rf "$v2rayCore" &&
mkdir -p "$v2rayCore" &&
git clone https://github.com/v2fly/v2ray-core.git "$v2rayCore" &&
go get -d github.com/2dust/AndroidLibV2rayLite &&

gomobile init &&
gomobile bind -v -ldflags='-s -w' github.com/2dust/AndroidLibV2rayLite &&
rm *-sources.jar &&

cd  ../.. &&

./gradlew TMessagesProj:externalNativeBuildFullFoss &&

OUT=TMessagesProj/build/intermediates/ndkBuild/fullFoss/obj/local &&
DIR=TMessagesProj/src/main/libs &&

rm -rf $DIR/armeabi-v7a &&
mkdir -p $DIR/armeabi-v7a &&
cp $OUT/armeabi-v7a/*.so $DIR/armeabi-v7a &&

rm -rf $DIR/arm64-v8a &&
mkdir -p $DIR/arm64-v8a &&
cp $OUT/arm64-v8a/*.so $DIR/arm64-v8a &&

rm -rf $DIR/x86 &&
mkdir -p $DIR/x86 &&
cp $OUT/x86/*.so $DIR/x86 &&

rm -rf $DIR/x86_64 &&
mkdir -p $DIR/x86_64 &&
cp $OUT/x86_64/*.so $DIR/x86_64
