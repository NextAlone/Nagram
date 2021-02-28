#!/usr/bin/env bash

source $HOME/.bashrc
source "bin/init/env.sh"

# Import golang
export PATH=$PATH:$HOME/.go/bin
export PATH=$PATH:$(go env GOPATH)/bin

# Native dependencies
bin/init/libs/ffmpeg.sh
bin/init/libs/boringssl.sh

# Build v2ray-core
bin/libs/v2ray.sh