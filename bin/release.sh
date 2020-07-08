#!/usr/bin/env bash

./gradlew TMessagesProj:clean \
          TMessagesProj:assembleFullBlobEmojiRelease \
          TMessagesProj:assembleFullBlobEmojiReleaseNoGcm \
          TMessagesProj:assembleMiniBlobEmojiRelease \
          TMessagesProj:assembleMiniBlobEmojiReleaseNoGcm \
          TMessagesProj:assembleMiniNoEmojiRelease \
          TMessagesProj:assembleMiniNoEmojiReleaseNoGcm &&
./bin/publish_repo_apks.sh &&
./bin/publish_release_apks.sh "$1"