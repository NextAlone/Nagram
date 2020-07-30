#!/usr/bin/env bash

./bin/init_release.sh &&
./bin/publish_release_apks.sh "$1" &&
./bin/publish_repo_apks.sh &&
./bin/publish_play.sh