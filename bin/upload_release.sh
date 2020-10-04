#!/bin/bash

trap 'kill $(jobs -p)' SIGINT

function upload() {

  for apk in $outPath/*.apk; do

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

outPath="TMessagesProj/build/outputs/apk/full/releaseNoGcm"

upload

outPath="TMessagesProj/build/outputs/apk/mini/release"

upload

outPath="TMessagesProj/build/outputs/apk/mini/releaseNoGcm"

upload
