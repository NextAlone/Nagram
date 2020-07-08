#!/usr/bin/env bash

function assemble() {

  ./gradlew TMessagesProj:assembleFullRelease \
            TMessagesProj:assembleFullReleaseNoGcm \
            TMessagesProj:assembleMiniRelease \
            TMessagesProj:assembleMiniReleaseNoGcm \
            TMessagesProj:assembleMiniNoEmojiRelease \
            TMessagesProj:assembleMiniNoEmojiReleaseNoGcm

  return $?

}

#./gradlew TMessagesProj:assembleRelease \
#          TMessagesProj:assembleReleaseNoGcm

assemble &&
assemble &&
./bin/publish_repo_apks.sh &&
./bin/publish_release_apks.sh "$1"