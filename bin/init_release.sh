#!/bin/bash

function assemble() {

  ./gradlew TMessagesProj:assembleRelease
  ./gradlew TMessagesProj:assembleReleaseNoGcm

  return $?

}

#./gradlew TMessagesProj:assembleRelease \
#          TMessagesProj:assembleReleaseNoGcm

assemble