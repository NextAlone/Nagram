#!/bin/bash

function upload {

  for apk in $outPath/*.apk; do

    echo ">> Uploading $apk"

    curl https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendDocument \
      -X POST \
      -F chat_id="$TELEGRAM_CHANNEL" \
      -F document="@$apk" \
      --silent --show-error --fail >/dev/null

  done

}

outPath="TMessagesProj/build/outputs/apk/full/release"

upload

outPath="TMessagesProj/build/outputs/apk/full/releaseNoGcm"

upload