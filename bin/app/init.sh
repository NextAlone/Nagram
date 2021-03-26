#!/bin/bash

function flavor() {
  for f in "$@"; do
    ./gradlew TMessagesProj:assemble${1}Release || exit 1
    ./gradlew TMessagesProj:assemble${1}ReleaseNoGcm || exit 1
  done
}

rm -rf TMessagesProj/build/outputs/apk

flavor Full
flavor FullAppleEmoji
flavor Mini
flavor MiniAppleEmoji
