#!/bin/bash

V2RAY_CORE_VERSION="4.34.0"

source "bin/init/env.sh"
#v2rayCore="$(go env GOPATH)/src/v2ray.com/core"
#
#if [ ! -f "$v2rayCore/go.mod" ]; then
#  git clone https://github.com/v2fly/v2ray-core.git "$v2rayCore" -b "v$V2RAY_CORE_VERSION" --depth 1
#fi
#
#cd "$v2rayCore"
#git fetch origin "v$V2RAY_CORE_VERSION"
#git checkout -b master
#git reset "v$V2RAY_CORE_VERSION" --hard

export GO111MOUDLE=on
go get -v golang.org/x/mobile/cmd/...
export PATH="$PATH:$(go env GOPATH)/bin"
gomobile init

cd "$PROJECT/build"
[ -d "v2ray" ] || git clone https://github.com/2dust/AndroidLibV2rayLite v2ray
cd v2ray
git reset --hard && git clean -fdx
sed -i -e "s|go 1.14|go 1.15|g" go.mod
go mod download -x
gomobile bind -v -ldflags='-s -w' . || exit 1

/bin/cp -f libv2ray.aar "$PROJECT/TMessagesProj/libs"
