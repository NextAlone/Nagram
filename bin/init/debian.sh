#!/bin/bash

function sudodo() {
    sudo $@ || $@
}

sudodo apt-get install bison gcc make curl ninja-build -y

bash < <(curl -s -S -L https://raw.githubusercontent.com/moovweb/gvm/master/binscripts/gvm-installer)
source $HOME/.gvm/scripts/gvm
gvm install go1.15.6 -B
gvm use go1.15.6 --default

curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- --default-toolchain none -y
echo "source \$HOME/.cargo/env" >> $HOME/.bashrc && source $HOME/.cargo/env

git submodule update --init --recursive

cd ss-rust/src/main/rust/shadowsocks-rust
rustup target install armv7-linux-androideabi aarch64-linux-android i686-linux-android x86_64-linux-android