#!/usr/bin/env bash

source "bin/init/env.sh"
export GO111MOUDLE=on
export PATH="$PATH:$(go env GOPATH)/bin"

cd "$PROJECT/build/v2ray"
gomobile init
gomobile bind -v -ldflags='-s -w' . || exit 1

/bin/cp -f libv2ray.aar "$PROJECT/TMessagesProj/libs"