#!/bin/bash

bin/libs/ss.sh || exit 1
bin/libs/ssr.sh || exit 1
bin/libs/v2ray.sh || exit 1
bin/libs/native.sh || exit 1