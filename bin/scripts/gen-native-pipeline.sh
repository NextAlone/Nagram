#!/usr/bin/env bash
set -euo pipefail

: "${FFMPEG_KEY:?FFMPEG_KEY missing}"
: "${LIBVPX_KEY:?LIBVPX_KEY missing}"
: "${BORINGSSL_KEY:?BORINGSSL_KEY missing}"
: "${NATIVE_KEY:?NATIVE_KEY missing}"

NEED_FFMPEG=0
NEED_BORINGSSL=0
NEED_NATIVE_V7A=0
NEED_NATIVE_V8A=0

if [ ! -d "TMessagesProj/jni/ffmpeg/build" ] \
   || [ -z "$(ls -A TMessagesProj/jni/ffmpeg/build 2>/dev/null)" ]; then
  NEED_FFMPEG=1
fi

if [ ! -d "TMessagesProj/jni/boringssl/build" ] \
   || [ -z "$(ls -A TMessagesProj/jni/boringssl/build 2>/dev/null)" ]; then
  NEED_BORINGSSL=1
fi

if [ ! -d "TMessagesProj/src/main/libs/armeabi-v7a" ] \
   || [ -z "$(ls -A TMessagesProj/src/main/libs/armeabi-v7a 2>/dev/null)" ]; then
  NEED_NATIVE_V7A=1
fi

if [ ! -d "TMessagesProj/src/main/libs/arm64-v8a" ] \
   || [ -z "$(ls -A TMessagesProj/src/main/libs/arm64-v8a 2>/dev/null)" ]; then
  NEED_NATIVE_V8A=1
fi

>&2 echo "Cache analysis (keys driven by fetch-status fingerprint):"
>&2 echo "  FFMPEG_KEY    = ${FFMPEG_KEY}"
>&2 echo "  LIBVPX_KEY    = ${LIBVPX_KEY}"
>&2 echo "  BORINGSSL_KEY = ${BORINGSSL_KEY}"
>&2 echo "  NATIVE_KEY    = ${NATIVE_KEY}"
>&2 echo "  ffmpeg/libvpx/dav1d need build: $NEED_FFMPEG"
>&2 echo "  boringssl     need build: $NEED_BORINGSSL"
>&2 echo "  native v7a    need build: $NEED_NATIVE_V7A"
>&2 echo "  native v8a    need build: $NEED_NATIVE_V8A"

cat <<'YAML_HEAD'
stages:
  - native-deps
  - native

default:
  image: registry.gitlab.com/xtao-labs/android-ndk:35-jdk17.0.19_10-ndk27.2.12479018-cmake3.22.1-ci
  tags:
    - saas-linux-medium-amd64
  before_script:
    - export ANDROID_HOME="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
    - echo "sdk.dir=${ANDROID_HOME}" > local.properties
    - echo "ndk.dir=${ANDROID_HOME}/ndk/27.2.12479018" >> local.properties
    - git config --global --add safe.directory $CI_PROJECT_DIR

YAML_HEAD

if [ "$NEED_FFMPEG" = "1" ]; then
cat <<YAML
ffmpeg:
  stage: native-deps
  cache:
    - key: "ffmpeg-libvpx-${FFMPEG_KEY}-${LIBVPX_KEY}"
      paths:
        - TMessagesProj/jni/ffmpeg/build/
  script:
    - sed -i -E 's#https://github\.com/[^/]+/#https://gitlab.com/xtao-labs/#g' .gitmodules
    - git submodule sync
    - ./run init libs libvpx
    - ./run init libs dav1d
    - ./run init libs ffmpeg

YAML
fi

if [ "$NEED_BORINGSSL" = "1" ]; then
cat <<YAML
boringssl:
  stage: native-deps
  cache:
    - key: "boringssl-${BORINGSSL_KEY}"
      paths:
        - TMessagesProj/jni/boringssl/build/
  script:
    - sed -i -E 's#https://github\.com/[^/]+/#https://gitlab.com/xtao-labs/#g' .gitmodules
    - git submodule sync
    - ./run init libs boringssl

YAML
fi

emit_native_job() {
  local target="$1"
cat <<YAML
native:${target}:
  stage: native
  variables:
    NATIVE_TARGET: "${target}"
YAML
  if [ "$NEED_FFMPEG" = "1" ] || [ "$NEED_BORINGSSL" = "1" ]; then
cat <<YAML
  needs:
    - job: ffmpeg
      optional: true
    - job: boringssl
      optional: true
YAML
  fi
cat <<YAML
  cache:
    - key: "native-${target}-${NATIVE_KEY}"
      paths:
        - TMessagesProj/src/main/libs/${target}/
    - key: "ffmpeg-libvpx-${FFMPEG_KEY}-${LIBVPX_KEY}"
      paths:
        - TMessagesProj/jni/ffmpeg/build/
      policy: pull
    - key: "boringssl-${BORINGSSL_KEY}"
      paths:
        - TMessagesProj/jni/boringssl/build/
      policy: pull
  script:
    - |
      for sm in TMessagesProj/jni/third_party/ffmpeg TMessagesProj/jni/third_party/libvpx TMessagesProj/jni/third_party/dav1d TMessagesProj/jni/boringssl; do
        if [ -d "\$sm" ] && [ ! -d "\$sm/.git" ] && [ ! -f "\$sm/.git" ]; then
          mv "\$sm" "\${sm}_artifacts_backup"
        fi
      done
    - sed -i -E 's#https://github\.com/[^/]+/#https://gitlab.com/xtao-labs/#g' .gitmodules
    - git submodule sync
    - git submodule update --init --force 'TMessagesProj/jni/*'
    - |
      for sm in TMessagesProj/jni/third_party/ffmpeg TMessagesProj/jni/third_party/libvpx TMessagesProj/jni/third_party/dav1d TMessagesProj/jni/boringssl; do
        if [ -d "\${sm}_artifacts_backup" ]; then
          cp -rn "\${sm}_artifacts_backup"/. "\$sm"/
          rm -rf "\${sm}_artifacts_backup"
        fi
      done
    - cd TMessagesProj/jni && ./patch_boringssl.sh || true
    - cd \$CI_PROJECT_DIR
    - export NATIVE_TARGET="${target}"
    - ./run libs native

YAML
}

if [ "$NEED_NATIVE_V7A" = "1" ]; then
  emit_native_job "armeabi-v7a"
fi

if [ "$NEED_NATIVE_V8A" = "1" ]; then
  emit_native_job "arm64-v8a"
fi

if [ "$NEED_FFMPEG" = "0" ] && [ "$NEED_BORINGSSL" = "0" ] \
   && [ "$NEED_NATIVE_V7A" = "0" ] && [ "$NEED_NATIVE_V8A" = "0" ]; then
cat <<'YAML'
noop:
  stage: native-deps
  image: alpine:3.20
  tags:
    - saas-linux-small-amd64
  before_script: []
  script:
    - echo "All native caches hit; nothing to build."
YAML
fi
