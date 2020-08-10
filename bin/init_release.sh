#!/bin/bash

function assemble() {

  ./gradlew TMessagesProj:assembleFullRelease \
            TMessagesProj:assembleFullAppleEmojiRelease \
            TMessagesProj:assembleFullTwitterEmojiRelease \
            TMessagesProj:assembleFullFacebookEmojiRelease \
            TMessagesProj:assembleFullReleaseNoGcm \
            TMessagesProj:assembleMiniRelease \
            TMessagesProj:assembleMiniReleaseNoGcm \
            TMessagesProj:assembleMiniNoEmojiReleaseNoGcm

  return $?

}

#./gradlew TMessagesProj:assembleRelease \
#          TMessagesProj:assembleReleaseNoGcm

assemble &&
assemble