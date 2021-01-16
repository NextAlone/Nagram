#!/bin/bash

V2RAY_CORE_VERSION="4.34.0"

source "bin/init/env.sh"
v2rayCore="$(go env GOPATH)/src/v2ray.com/core"

if [ ! -d "$v2rayCore" ]; then
  mkdir -p "$v2rayCore"
  git clone https://github.com/v2fly/v2ray-core.git "$v2rayCore" -b "v$V2RAY_CORE_VERSION" --depth 1
  cd "$v2rayCore"
else
  cd "$v2rayCore"
  git fetch origin "v$V2RAY_CORE_VERSION" && git reset "v$V2RAY_CORE_VERSION" --hard || git clone https://github.com/v2fly/v2ray-core.git "$v2rayCore" -b "v$V2RAY_CORE_VERSION" --depth 1
fi

export PATH="$PATH/$(go env $GOPATH/bin)"
go mod download -x

cd "$PROJECT/TMessagesProj/libs"
go get -v golang.org/x/mobile/cmd/...
go get -d github.com/2dust/AndroidLibV2rayLite
gomobile init

gomobile bind -v -ldflags='-s -w' github.com/2dust/AndroidLibV2rayLite || exit 1

rm *-sources.jar