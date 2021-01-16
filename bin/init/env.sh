#!/bin/bash

if [ -z "$ANDROID_HOME" ]; then
  if [ -d "$HOME/Android/Sdk" ]; then
    export ANDROID_HOME="$HOME/Android/Sdk"
  fi
fi

if [ -f "$ANDROID_HOME/ndk-bundle/source.properties" ]; then
  export NDK=$ANDROID_HOME/ndk-bundle
else
  export NDK=$ANDROID_HOME/ndk/21.3.6528147
fi

export ANDROID_NDK_HOME=$NDK

export PROJECT=$(realpath .)