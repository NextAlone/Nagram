#!/bin/bash

./gradlew TMessagesProj:assembleFullRelease || exit 1

trap 'kill $(jobs -p)' SIGINT

function upload() {

  for apk in $outPath/*arm64*.apk; do

    echo ">> Uploading $apk"

    curl https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendDocument \
      -X POST \
      -F chat_id="$TELEGRAM_CHANNEL" \
      -F document="@$apk" \
      --silent --show-error --fail >/dev/null &

  done

  for job in $(jobs -p); do
    wait $job
  done

}

outPath="TMessagesProj/build/outputs/apk/full/release"

upload