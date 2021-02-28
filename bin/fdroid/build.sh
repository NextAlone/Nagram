#!/usr/bin/env bash

source $HOME/.bashrc

env

source "bin/init/env.sh"

bin/init/libs/ffmpeg.sh
bin/init/libs/boringssl.sh
bin/libs/v2ray.sh