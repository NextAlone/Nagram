#!/bin/bash

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
assemble