#!/bin/bash

if [ -z "$ANDROID_HOME" ]; then
  if [ -n "$ANDROID_SDK_ROOT" ] && [ -d "$ANDROID_SDK_ROOT" ]; then
    export ANDROID_HOME="$ANDROID_SDK_ROOT"
  elif [ -d "$HOME/Android/Sdk" ]; then
    export ANDROID_HOME="$HOME/Android/Sdk"
  elif [ -d "$HOME/.local/lib/android/sdk" ]; then
    export ANDROID_HOME="$HOME/.local/lib/android/sdk"
  elif [ -d "$HOME/Library/Android/sdk" ]; then
    export ANDROID_HOME="$HOME/Library/Android/sdk"
  elif [ -n "$LOCALAPPDATA" ] && [ -d "$LOCALAPPDATA/Android/Sdk" ]; then
    # MSYS2/MINGW64 default install location.
    export ANDROID_HOME="$LOCALAPPDATA/Android/Sdk"
  fi
fi


_NDK="$ANDROID_HOME/ndk/27.2.12479018"
[ -f "$_NDK/source.properties" ] || _NDK="$ANDROID_NDK_HOME"
[ -f "$_NDK/source.properties" ] || _NDK="$NDK"
[ -f "$_NDK/source.properties" ] || _NDK="$ANDROID_HOME/ndk-bundle"

if [ ! -f "$_NDK/source.properties" ]; then
  echo "Error: NDK not found."
  exit 1
fi

export ANDROID_NDK_HOME=$_NDK
export NDK=$_NDK
export PROJECT=$(realpath .)

if [ ! $(command -v go) ]; then
  if [ -d /usr/lib/go-1.16 ]; then
    export PATH=$PATH:/usr/lib/go-1.16/bin
  elif [ -d $HOME/.go ]; then
    export PATH=$PATH:$HOME/.go/bin
  fi
fi

if [ $(command -v go) ]; then
  export PATH=$PATH:$(go env GOPATH)/bin
fi
