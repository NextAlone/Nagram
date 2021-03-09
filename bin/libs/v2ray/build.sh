#!/usr/bin/env bash

source "bin/init/env.sh"
export GO111MOUDLE=on
export GO386=softfloat

cd "$PROJECT/v2ray"
gomobile init
gomobile bind -v -ldflags='-s -w' . || exit 1

mkdir -p "$PROJECT/TMessagesProj/libs"
/bin/cp -f libv2ray.aar "$PROJECT/TMessagesProj/libs"
