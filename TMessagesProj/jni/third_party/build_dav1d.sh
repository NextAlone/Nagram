#!/usr/bin/env bash
# Build dav1d static libraries for Android.
#
# Usage:
#   ./build_dav1d.sh [ABI...]
#
# Requires meson, ninja, python3 and pkg-config on PATH.

source "$(dirname -- "${BASH_SOURCE[0]}")/build_common.sh"

[[ -f "$DAV1D_SOURCE_DIR/meson.build" ]] || die "dav1d sources were not found in: $DAV1D_SOURCE_DIR
Run: git submodule update --init TMessagesProj/jni/third_party/dav1d"
DAV1D_SOURCE_DIR="$(cd "$DAV1D_SOURCE_DIR" && pwd)"

for tool in meson ninja python3 pkg-config; do
    command -v "$tool" >/dev/null 2>&1 || die "Required dav1d build tool is missing: $tool"
done

write_dav1d_cross_file() {
    local abi="$1" file="$2"
    local triple cpu_family cpu extra_c_args

    triple="$(abi_triple "$abi")"
    case "$abi" in
        armeabi-v7a)
            cpu_family="arm"
            cpu="armv7-a"
            extra_c_args="'-march=armv7-a', '-mthumb', '-mfpu=neon', '-mfloat-abi=softfp'"
            ;;
        arm64-v8a)
            cpu_family="aarch64"
            cpu="armv8-a"
            extra_c_args="'-march=armv8-a'"
            ;;
        x86)
            cpu_family="x86"
            cpu="i686"
            extra_c_args="'-march=i686', '-msse3', '-mfpmath=sse'"
            ;;
        x86_64)
            cpu_family="x86_64"
            cpu="x86_64"
            extra_c_args="'-march=x86-64', '-msse4.1'"
            ;;
        *) error "Unsupported ABI for dav1d: $abi"; return 1 ;;
    esac

    # Meson spawns the compiler directly, so the binaries must be given in the
    # host's native form (a .cmd wrapper and Windows paths on MINGW64).
    local cc
    cc="$(clang_driver "$triple" clang native)"

    # Meson wants a quoted list, so turn COMMON_CFLAGS into "'a', 'b'".
    local shared_c_args="" flag
    for flag in $COMMON_CFLAGS; do
        shared_c_args+=", '$flag'"
    done

    cat > "$file" <<EOF
[binaries]
c = '$(to_native_path "$cc")'
ar = '$(to_native_path "$AR_BIN")'
strip = '$(to_native_path "$STRIP_BIN")'
ranlib = '$(to_native_path "$RANLIB_BIN")'
nm = '$(to_native_path "$NM_BIN")'
objcopy = '$(to_native_path "$OBJCOPY_BIN")'
pkg-config = 'pkg-config'

[host_machine]
system = 'android'
cpu_family = '$cpu_family'
cpu = '$cpu'
endian = 'little'

[properties]
needs_exe_wrapper = true

[built-in options]
c_args = [$extra_c_args, '-fPIC'$shared_c_args]
c_link_args = ['-Wl,--gc-sections']
EOF
}

verify_dav1d_archive() {
    local abi="$1" library="$2"

    [[ -f "$library" ]] || { error "Missing dav1d archive for $abi: $library"; return 1; }

    if ! "$NM_BIN" -g --defined-only "$library" \
        | awk '$NF == "dav1d_open" { found = 1 } END { exit found ? 0 : 1 }';
    then
        error "dav1d_open was not found in $library"
        return 1
    fi
}

build_dav1d_for_abi() {
    local abi="$1"
    check_x86_assembler "$abi" || return 1

    local prefix="$DAV1D_OUTPUT_DIR/$abi"
    local build_dir="$DAV1D_WORK_DIR/$abi"
    local cross_file="$DAV1D_WORK_DIR/crossfiles/$abi.ini"

    if [[ "$CLEAN" == "1" ]]; then
        rm -rf "$build_dir" "$prefix"
    fi
    mkdir -p "$build_dir" "$prefix" "$(dirname "$cross_file")"
    write_dav1d_cross_file "$abi" "$cross_file" || return 1

    echo
    echo "========== dav1d: $abi, API $API =========="

    # Meson resolves the prefix and build directory itself (it may be a native
    # Windows Python), so hand it host-native paths.
    if ! meson setup "$(to_native_path "$build_dir")" "$(to_native_path "$DAV1D_SOURCE_DIR")" \
        --cross-file "$(to_native_path "$cross_file")" \
        --prefix "$(to_native_path "$prefix")" \
        --libdir lib \
        --buildtype release \
        --default-library static \
        -Db_ndebug=true \
        -Db_lto=false \
        -Denable_tools=false \
        -Denable_examples=false \
        -Denable_tests=false \
        -Denable_docs=false \
        -Denable_asm=true;
    then
        error "dav1d configure failed for $abi."
        return 1
    fi

    local native_build_dir
    native_build_dir="$(to_native_path "$build_dir")"

    if ! ninja -C "$native_build_dir" -j "$JOBS" 2>&1 | tee "$build_dir/build.log"; then
        error "dav1d build failed for $abi. Log: $build_dir/build.log"
        return 1
    fi

    if ! ninja -C "$native_build_dir" install 2>&1 | tee "$build_dir/install.log"; then
        error "dav1d install failed for $abi. Log: $build_dir/install.log"
        return 1
    fi

    verify_dav1d_archive "$abi" "$prefix/lib/libdav1d.a" || return 1
    [[ -f "$prefix/include/dav1d/dav1d.h" ]] || { error "Missing dav1d headers for $abi"; return 1; }
    [[ -f "$prefix/lib/pkgconfig/dav1d.pc" ]] || { error "Missing dav1d.pc for $abi"; return 1; }
}

TARGET_ABIS="${*:-$ABIS}"

mkdir -p "$DAV1D_WORK_DIR" "$DAV1D_OUTPUT_DIR"

for abi in $TARGET_ABIS; do
    build_dav1d_for_abi "$abi"
done

echo
echo "dav1d install: $DAV1D_OUTPUT_DIR/<ABI>"
