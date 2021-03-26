#!/bin/bash

function flavor() {
  for f in "$@"; do
    ./gradlew TMessagesProj:assemble${1}Release || exit 1
    ./gradlew TMessagesProj:assemble${1}ReleaseNoGcm || exit 1
  done
}

source bin/app/build.sh

rm -rf TMessagesProj/build/outputs/apk
flavor Full
flavor Mini &
upload full/release
upload full/releaseNoGcm

for job in $(jobs -p); do
  wait $job || exit 1
done

upload mini/release
upload mini/releaseNoGcm
