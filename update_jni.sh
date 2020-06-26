#!/bin/bash

rm -f TMessagesProj/libs/*.aar

./gradlew ss-rust:assembleRelease

cp ss-rust/build/outputs/aar/* TMessagesProj/libs

./gradlew ssr-libev:assembleRelease

cp ssr-libev/build/outputs/aar/* TMessagesProj/libs

cd TMessagesProj/libs

go get -u github.com/golang/protobuf/protoc-gen-go
go get -v golang.org/x/mobile/cmd/...
go get -v go.starlark.net/starlark
go get -v github.com/refraction-networking/utls
go get -v github.com/gorilla/websocket
go get -v -insecure v2ray.com/core
go get github.com/2dust/AndroidLibV2rayLite

gomobile init
env GO111MODULE=off gomobile bind -v -ldflags='-s -w' github.com/2dust/AndroidLibV2rayLite
rm *-sources.jar

cd  ../..

./gradlew TMessagesProj:externalNativeBuildFullFoss

OUT=TMessagesProj/build/intermediates/ndkBuild/fullFoss/obj/local
DIR=TMessagesProj/src/main/libs

rm -rf $DIR/armeabi-v7a
mkdir -p $DIR/armeabi-v7a
cp $OUT/armeabi-v7a/*.so $DIR/armeabi-v7a

rm -rf $DIR/arm64-v8a
mkdir -p $DIR/arm64-v8a
cp $OUT/arm64-v8a/*.so $DIR/arm64-v8a

rm -rf $DIR/x86
mkdir -p $DIR/x86
cp $OUT/x86/*.so $DIR/x86

rm -rf $DIR/x86_64
mkdir -p $DIR/x86_64
cp $OUT/x86_64/*.so $DIR/x86_64