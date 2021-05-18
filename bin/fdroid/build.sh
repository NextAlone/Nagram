#!/usr/bin/env bash

source $HOME/.bashrc
source "bin/init/env.sh"

# Native dependencies
bin/init/libs/ffmpeg.sh
bin/init/libs/boringssl.sh
