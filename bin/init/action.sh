#!/bin/bash

source "bin/init/env.sh"

cat > local.properties << EOF
ndk.dir=$NDK
EOF