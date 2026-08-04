#!/usr/bin/env bash
# Build libvpx static libraries for Android.
#
# Usage:
#   ./build_libvpx.sh [ABI...]
#
# Runs for every ABI in $ABIS when no argument is given. See build_common.sh
# for the shared options (API, NDK_VERSION, CLEAN, JOBS, ...).

source "$(dirname -- "${BASH_SOURCE[0]}")/build_common.sh"

# ---------------------------------------------------------------------------
# Component options. Defaults reproduce the reference build in
# ../ffmpeg/build_ffmpeg_libvpx_dav1d_android_ndk27_merged.sh.
# ---------------------------------------------------------------------------
LIBVPX_REALTIME_ONLY="${LIBVPX_REALTIME_ONLY:-1}"
LIBVPX_SMALL="${LIBVPX_SMALL:-1}"
LIBVPX_BETTER_HW_COMPATIBILITY="${LIBVPX_BETTER_HW_COMPATIBILITY:-1}"
LIBVPX_WEBM_IO="${LIBVPX_WEBM_IO:-0}"

# auto = enabled for x86/x86_64, disabled for ARM.
LIBVPX_RUNTIME_CPU_DETECT="${LIBVPX_RUNTIME_CPU_DETECT:-auto}"

# AArch64 ISA extension kernels (dotprod / i8mm / SVE / SVE2). Off by default;
# turning them on trades ~150 KB for faster VP9 decode on modern SoCs.
LIBVPX_ARM_EXTENSIONS="${LIBVPX_ARM_EXTENSIONS:-0}"

# Decoder input size guard. Set to an empty value to remove the limit.
LIBVPX_SIZE_LIMIT="${LIBVPX_SIZE_LIMIT:-4096x4096}"

require_bool LIBVPX_REALTIME_ONLY "$LIBVPX_REALTIME_ONLY"
require_bool LIBVPX_SMALL "$LIBVPX_SMALL"
require_bool LIBVPX_BETTER_HW_COMPATIBILITY "$LIBVPX_BETTER_HW_COMPATIBILITY"
require_bool LIBVPX_WEBM_IO "$LIBVPX_WEBM_IO"
require_bool LIBVPX_ARM_EXTENSIONS "$LIBVPX_ARM_EXTENSIONS"
require_toggle LIBVPX_RUNTIME_CPU_DETECT "$LIBVPX_RUNTIME_CPU_DETECT"

[[ -f "$LIBVPX_SOURCE_DIR/configure" ]] || die "libvpx sources were not found in: $LIBVPX_SOURCE_DIR
Run: git submodule update --init TMessagesProj/jni/third_party/libvpx"
LIBVPX_SOURCE_DIR="$(cd "$LIBVPX_SOURCE_DIR" && pwd)"

# libvpx renames and removes configure toggles between releases (neon_i8mm,
# sve and sve2 do not exist before 1.15; neon_asm is ARM-only). Probe the
# option list instead of failing on an unknown flag.
libvpx_has_option() {
    grep -qE "^[[:space:]]*$1[[:space:]]*$" "$LIBVPX_SOURCE_DIR/configure"
}

# usage: libvpx_toggle <array-name> <underscored_option> <0|1>
libvpx_toggle() {
    local -n destination="$1"
    local option="$2" enable="$3"
    libvpx_has_option "$option" || return 0
    if [[ "$enable" == "1" ]]; then
        destination+=("--enable-${option//_/-}")
    else
        destination+=("--disable-${option//_/-}")
    fi
}

build_libvpx_for_abi() {
    local abi="$1"
    local target vpx_target extra_cflags

    target="$(abi_triple "$abi")"
    case "$abi" in
        arm64-v8a)
            vpx_target="arm64-android-gcc"
            extra_cflags="-O3 -fPIC -march=armv8-a"
            ;;
        armeabi-v7a)
            vpx_target="armv7-android-gcc"
            extra_cflags="-O3 -fPIC -march=armv7-a -mfloat-abi=softfp -mfpu=neon"
            ;;
        x86_64)
            vpx_target="x86_64-android-gcc"
            extra_cflags="-O3 -fPIC -march=x86-64"
            ;;
        x86)
            vpx_target="x86-android-gcc"
            extra_cflags="-O3 -fPIC -march=i686 -mssse3 -mfpmath=sse"
            ;;
        *) error "Unsupported ABI: $abi"; return 1 ;;
    esac

    extra_cflags="$extra_cflags${LIBVPX_COMMON_CFLAGS:+ $LIBVPX_COMMON_CFLAGS}"

    check_x86_assembler "$abi" || return 1

    local prefix="$LIBVPX_OUTPUT_DIR/$abi"
    local build_dir="$LIBVPX_WORK_DIR/$abi"
    local cc cxx
    cc="$(clang_driver "$target" clang)"
    cxx="$(clang_driver "$target" clang++)"

    if [[ "$CLEAN" == "1" ]]; then
        rm -rf "$build_dir" "$prefix"
    fi
    mkdir -p "$build_dir" "$prefix"

    echo
    echo "========== libvpx: $abi, API $API =========="

    local -a configure_env=(
        "CC=$cc"
        "CXX=$cxx"
        "LD=$cc"
        "AR=$AR_BIN"
        "ARFLAGS=crsD"
        "NM=$NM_BIN"
        "STRIP=$STRIP_BIN"
    )

    # ARM .S goes through the clang integrated assembler. x86 .asm must be
    # handled by NASM/Yasm, which libvpx's configure detects on its own.
    if [[ "$abi" == "arm64-v8a" || "$abi" == "armeabi-v7a" ]]; then
        configure_env+=("AS=$cc")
    fi

    local -a vpx_args=(
        --target="$vpx_target"
        --prefix="$prefix"
        --disable-shared
        --enable-static
        --enable-pic
        --enable-optimizations
        --enable-multithread
        --disable-examples
        --disable-tools
        --disable-docs
        --disable-unit-tests
        --disable-install-bins
        --disable-install-docs
        --disable-debug
        --enable-vp8
        --enable-vp9
        --enable-vp8-decoder
        --enable-vp8-encoder
        --enable-vp9-decoder
        --enable-vp9-encoder
    )

    local runtime_cpu_detect="$LIBVPX_RUNTIME_CPU_DETECT"
    if [[ "$runtime_cpu_detect" == "auto" ]]; then
        case "$abi" in
            x86|x86_64) runtime_cpu_detect="on" ;;
            *)          runtime_cpu_detect="off" ;;
        esac
    fi
    if [[ "$runtime_cpu_detect" == "on" ]]; then
        vpx_args+=(--enable-runtime-cpu-detect)
    else
        vpx_args+=(--disable-runtime-cpu-detect)
    fi

    libvpx_toggle vpx_args realtime_only           "$LIBVPX_REALTIME_ONLY"
    libvpx_toggle vpx_args small                   "$LIBVPX_SMALL"
    libvpx_toggle vpx_args better_hw_compatibility "$LIBVPX_BETTER_HW_COMPATIBILITY"
    libvpx_toggle vpx_args webm_io                 "$LIBVPX_WEBM_IO"

    case "$abi" in
        armeabi-v7a)
            libvpx_toggle vpx_args neon_asm 0
            ;;
        arm64-v8a)
            libvpx_toggle vpx_args neon_asm     0
            libvpx_toggle vpx_args neon_dotprod "$LIBVPX_ARM_EXTENSIONS"
            libvpx_toggle vpx_args neon_i8mm    "$LIBVPX_ARM_EXTENSIONS"
            libvpx_toggle vpx_args sve          "$LIBVPX_ARM_EXTENSIONS"
            libvpx_toggle vpx_args sve2         "$LIBVPX_ARM_EXTENSIONS"
            ;;
    esac

    [[ -n "$LIBVPX_SIZE_LIMIT" ]] && vpx_args+=(--size-limit="$LIBVPX_SIZE_LIMIT")

    vpx_args+=(
        --extra-cflags="$extra_cflags"
        --log="$build_dir/config.log"
    )

    pushd "$build_dir" >/dev/null

    # AS/ASFLAGS inherited from the environment would override the per-ABI
    # choice made above.
    if ! env -u AS -u ASFLAGS "${configure_env[@]}" \
        "$LIBVPX_SOURCE_DIR/configure" "${vpx_args[@]}"
    then
        error "libvpx configure failed for $abi. Log: $build_dir/config.log"
        popd >/dev/null
        return 1
    fi

    # `%.a: %_g.a` runs `$(STRIP) --strip-debug`, which zeroes sh_link on
    # SHT_LLVM_ADDRSIG and makes lld reject the table under --icf=safe.
    # Degrade the rule to a plain copy so the table stays usable.
    if [[ "$LIBVPX_SKIP_STRIP" == "1" && -f "$build_dir/config.mk" ]]; then
        if grep -q '^HAVE_GNU_STRIP=' "$build_dir/config.mk"; then
            sed -i.bak 's/^HAVE_GNU_STRIP=.*/HAVE_GNU_STRIP=no/' "$build_dir/config.mk"
            rm -f "$build_dir/config.mk.bak"
        else
            echo 'HAVE_GNU_STRIP=no' >> "$build_dir/config.mk"
        fi
    fi

    if ! make -j"$JOBS" 2>&1 | tee "$build_dir/build.log"; then
        error "libvpx build failed for $abi. Log: $build_dir/build.log"
        popd >/dev/null
        return 1
    fi

    if ! make install 2>&1 | tee "$build_dir/install.log"; then
        error "libvpx install failed for $abi. Log: $build_dir/install.log"
        popd >/dev/null
        return 1
    fi

    popd >/dev/null

    [[ -f "$prefix/lib/libvpx.a" ]] || { error "Missing $prefix/lib/libvpx.a"; return 1; }
    [[ -d "$prefix/include/vpx" ]] || { error "Missing $prefix/include/vpx"; return 1; }
    [[ -f "$prefix/lib/pkgconfig/vpx.pc" ]] || { error "Missing vpx.pc for $abi"; return 1; }

    # WebRTC links directly against both VP8 and VP9 encoder/decoder interfaces.
    local symbol
    for symbol in \
        vpx_codec_vp8_cx \
        vpx_codec_vp8_dx \
        vpx_codec_vp9_cx \
        vpx_codec_vp9_dx
    do
        if ! "$NM_BIN" -g --defined-only "$prefix/lib/libvpx.a" \
            | grep -E "[[:space:]]${symbol}$" >/dev/null;
        then
            error "Missing libvpx symbol for $abi: $symbol"
            return 1
        fi
    done
}

TARGET_ABIS="${*:-$ABIS}"

mkdir -p "$LIBVPX_WORK_DIR" "$LIBVPX_OUTPUT_DIR"

for abi in $TARGET_ABIS; do
    build_libvpx_for_abi "$abi"
done

echo
echo "libvpx install: $LIBVPX_OUTPUT_DIR/<ABI>"
