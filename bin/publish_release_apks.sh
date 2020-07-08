#!/usr/bin/env bash

if [ ! -x "$(command -v ghr)" ]; then

  wget -O ghr.tar.gz https://github.com/tcnksm/ghr/releases/download/v0.13.0/ghr_v0.13.0_linux_amd64.tar.gz &&
  tar xvzf ghr.tar.gz &&
  rm ghr.tar.gz &&
  sudo mv ghr*linux_amd64/ghr /usr/local/bin &&
  rm -rf ghr*linux_amd64 || exit 1

fi

rm -rf build/apks &&
mkdir -p build/apks &&
find TMessagesProj -name "*.apk" -exec cp {} build/apks \; &&
ghr -delete -n "$1" "$1" build/apks/