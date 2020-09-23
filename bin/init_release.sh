#!/bin/bash

function assemble() {

  ./gradlew TMessagesProj:assembleRelease \
            TMessagesProj:assembleReleaseNoGcm

  return $?

}

#./gradlew TMessagesProj:assembleRelease \
#          TMessagesProj:assembleReleaseNoGcm

assemble