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
assemble &&

rm -rf build/apks &&
mkdir -p build/apks &&
find TMessagesProj/build/outputs/apk -name "*.apk" -exec cp {} build/apks \; &&
cp build/update.json build/apks &&
cd build/apks &&
rm *universal* &&
xz *.apk &&
export GIT_SSH_COMMAND="ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" &&
git init &&
git config --global user.name "世界" &&
git config --global user.email "i@nekox.me" &&
git remote add origin "git@github.com:NekoX-Dev/Resources.git" &&
git add . --all &&
git commit -m "Update"