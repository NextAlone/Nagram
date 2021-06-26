#!/bin/bash

set -e

patch -d ffmpeg -p1 < patches/ffmpeg/0001-compilation-magic.patch

libavformat=('dv.h' 'isom.h')
libavcodec=('bytestream.h' 'get_bits.h' 'put_bits.h' 'golomb.h' 'vlc.h')
libavutil=('intmath.h' 'reverse.h')

for arch in arm64-v8a armeabi-v7a x86 x86_64
  do
    for file in ${libavformat[*]}
    do
      cp ffmpeg/libavformat/"$file" ffmpeg/build/$arch/include/libavformat/"$file"
    done

    # fix DrKLo's mystery include since 7.8.0
    for file in ${libavcodec[*]}
    do
      cp ffmpeg/libavcodec/"$file" ffmpeg/build/$arch/include/libavcodec/"$file"
    done
    for file in ${libavutil[*]}
    do
      cp ffmpeg/libavutil/"$file" ffmpeg/build/$arch/include/libavutil/"$file"
    done
    cp ffmpeg_mathops_fix.h ffmpeg/build/$arch/include/libavcodec/ffmpeg_mathops_fix.h
    sed -i 's/mathops/ffmpeg_mathops_fix/g' ffmpeg/build/$arch/include/libavcodec/get_bits.h
  done
