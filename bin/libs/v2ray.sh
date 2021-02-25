#!/bin/bash

// v4.34.0
V2RAY_CORE_VERSION="a6efb4d60b86789a68ed8ac8d52cfcec2d80229a"

source "bin/init/env.sh"
export GO111MOUDLE=on
export PATH="$PATH:$(go env GOPATH)/bin"

cd "$PROJECT/build"
[ -d "v2ray" ] || git clone https://github.com/2dust/AndroidLibV2rayLite v2ray
cd v2ray
git reset --hard && git clean -fdx
sed -i -e "s|go 1.14|go 1.16|g" go.mod
sed -i -e "s|core master|core $V2RAY_CORE_VERSION|g" go.mod
go mod download -x || exit 1
go get -v golang.org/x/mobile/cmd/...
gomobile init
gomobile bind -v -ldflags='-s -w' . || exit 1

/bin/cp -f libv2ray.aar "$PROJECT/TMessagesProj/libs"
