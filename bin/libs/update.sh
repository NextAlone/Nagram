#!/bin/bash

bin/libs/v2ray.sh || exit 1
bin/libs/shadowsocks.sh || exit 1
bin/libs/ssr.sh || exit 1
bin/libs/native.sh || exit 1
