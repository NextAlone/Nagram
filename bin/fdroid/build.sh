#!/usr/bin/env bash

source $HOME/.bashrc
source "bin/init/env.sh"

# Native dependencies
bin/init/libs/ffmpeg.sh
bin/init/libs/boringssl.sh
bin/init/libs/libvpx_x86.sh

# Build v2ray-core
bin/libs/v2ray/build.sh