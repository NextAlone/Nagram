#!/bin/bash

outPath="TMessagesProj/build/outputs/apk/full/release"

for apk in $outPath/*.apk; do

    echo ">> Uploading $apk"

    curl https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendDocument \
        -X POST \
        -F chat_id="$TELEGRAM_CHANNEL" \
        -F document="@$apk" \
        --silent --show-error --fail > /dev/null &

done

trap 'kill $(jobs -p)' SIGINT

for job in `jobs -p`; do
  wait $job
done