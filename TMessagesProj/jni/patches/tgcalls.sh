#!/bin/bash

set -e

patch -d tgcalls -p1 < patches/tgcalls-0001-feat-custom-group-voip-audio-bitrate.patch
