#!/bin/bash

set -e

check() {
  local filePath="$TRAVIS_BUILD_DIR/TMessagesProj/build/outputs/apk/afat/debug/TMessagesProj-afat-debug.apk"
  ls $TRAVIS_BUILD_DIR/TMessagesProj/build/outputs/apk/afat/debug/
  if test -f "$filePath"; then
    echo "Build successfully done! :)"

    local size;
    size=$(stat -c %s "$filePath")
    echo "File size of ${filePath}: ${size} Bytes"
  else
    echo "Build error, output file does not exist"
    exit 1
  fi
}

check
