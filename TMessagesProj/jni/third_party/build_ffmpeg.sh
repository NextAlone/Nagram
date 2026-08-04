#!/usr/bin/env bash
# Build FFmpeg static libraries for Android and package the final
# include/ + per-ABI library tree consumed by jni/CMakeLists.txt.
#
# Usage:
#   ./build_ffmpeg.sh [ABI...]
#   PACKAGE_ONLY=1 ./build_ffmpeg.sh       # skip builds, only repackage
#                                          #   $BUILD_ROOT/{libvpx,dav1d,ffmpeg}/<ABI>
#                                          #   into $PACKAGE_DIR/build_android_final.
#
# libvpx and dav1d must have been built first (build_libvpx.sh /
# build_dav1d.sh); their install trees under $BUILD_ROOT are located through
# pkg-config.

source "$(dirname -- "${BASH_SOURCE[0]}")/build_common.sh"

# ---------------------------------------------------------------------------
# Component options
# ---------------------------------------------------------------------------
ENABLE_SMALL="${ENABLE_SMALL:-1}"

# configure appends its own optimization flag (-Os with --enable-small, else
# -O3) *after* --extra-cflags, so an -O level passed there is silently
# overridden. --optflags is the only flag that wins. Empty = configure default.
FFMPEG_OPTFLAGS="${FFMPEG_OPTFLAGS:-}"

# Thin libvpxdec/libvpxenc wrappers for VP8 in addition to VP9.
FFMPEG_LIBVPX_VP8="${FFMPEG_LIBVPX_VP8:-1}"
FFMPEG_PARSERS="${FFMPEG_PARSERS:-1}"

# CPU/assembly switches. Accepted values: auto, on, off.
FFMPEG_RUNTIME_CPUDETECT="${FFMPEG_RUNTIME_CPUDETECT:-on}"
FFMPEG_MMX="${FFMPEG_MMX:-auto}"
FFMPEG_MMXEXT="${FFMPEG_MMXEXT:-auto}"
FFMPEG_SSE="${FFMPEG_SSE:-auto}"
FFMPEG_SSE2="${FFMPEG_SSE2:-auto}"
FFMPEG_SSE3="${FFMPEG_SSE3:-auto}"
FFMPEG_SSSE3="${FFMPEG_SSSE3:-auto}"
FFMPEG_SSE4="${FFMPEG_SSE4:-auto}"
FFMPEG_SSE42="${FFMPEG_SSE42:-auto}"
FFMPEG_AVX="${FFMPEG_AVX:-off}"
FFMPEG_AVX2="${FFMPEG_AVX2:-auto}"
FFMPEG_AVX512="${FFMPEG_AVX512:-auto}"
FFMPEG_FMA3="${FFMPEG_FMA3:-auto}"
FFMPEG_FMA4="${FFMPEG_FMA4:-auto}"
FFMPEG_BMI1="${FFMPEG_BMI1:-auto}"
FFMPEG_BMI2="${FFMPEG_BMI2:-auto}"

FFMPEG_X86_64_MMX="${FFMPEG_X86_64_MMX:-off}"
FFMPEG_X86_64_INLINE_ASM="${FFMPEG_X86_64_INLINE_ASM:-off}"
FFMPEG_X86_64_X86ASM="${FFMPEG_X86_64_X86ASM:-auto}"
FFMPEG_X86_MMX="${FFMPEG_X86_MMX:-off}"
FFMPEG_X86_INLINE_ASM="${FFMPEG_X86_INLINE_ASM:-off}"
FFMPEG_X86_X86ASM="${FFMPEG_X86_X86ASM:-off}"

FFMPEG_X86_64_EXTRA_ISA_CFLAGS="${FFMPEG_X86_64_EXTRA_ISA_CFLAGS:-}"
FFMPEG_X86_EXTRA_ISA_CFLAGS="${FFMPEG_X86_EXTRA_ISA_CFLAGS:-}"

require_bool ENABLE_SMALL "$ENABLE_SMALL"
require_bool FFMPEG_LIBVPX_VP8 "$FFMPEG_LIBVPX_VP8"
require_bool FFMPEG_PARSERS "$FFMPEG_PARSERS"
require_bool PACKAGE_ONLY "${PACKAGE_ONLY:-0}"

for toggle_name in \
    FFMPEG_RUNTIME_CPUDETECT FFMPEG_MMX FFMPEG_MMXEXT FFMPEG_SSE \
    FFMPEG_SSE2 FFMPEG_SSE3 FFMPEG_SSSE3 FFMPEG_SSE4 FFMPEG_SSE42 \
    FFMPEG_AVX FFMPEG_AVX2 FFMPEG_AVX512 FFMPEG_FMA3 FFMPEG_FMA4 \
    FFMPEG_BMI1 FFMPEG_BMI2 FFMPEG_X86_64_MMX \
    FFMPEG_X86_64_INLINE_ASM FFMPEG_X86_64_X86ASM FFMPEG_X86_MMX \
    FFMPEG_X86_INLINE_ASM FFMPEG_X86_X86ASM
do
    require_toggle "$toggle_name" "${!toggle_name}"
done

[[ -f "$FFMPEG_SOURCE_DIR/configure" ]] || die "FFmpeg sources were not found in: $FFMPEG_SOURCE_DIR
Run: git submodule update --init TMessagesProj/jni/third_party/ffmpeg"
FFMPEG_SOURCE_DIR="$(cd "$FFMPEG_SOURCE_DIR" && pwd)"

command -v pkg-config >/dev/null 2>&1 || die "pkg-config is required to locate libvpx and dav1d."

# FFmpeg's libdav1d check is require_pkg_config with no check_lib fallback, so
# pkg-config has to resolve dav1d.pc (and vpx.pc). Both .pc files are collected
# into a single directory per ABI so PKG_CONFIG_LIBDIR never needs a path-list
# separator, which differs between MSYS pkg-config (':') and a native
# Windows pkgconf (';').
#
# Whether that directory has to be spelled as a POSIX or a Windows path depends
# on which pkg-config is on PATH, so probe it instead of guessing.
resolve_pkgconfig_libdir() {
    local dir="$1" candidate
    for candidate in "$dir" "$(to_native_path "$dir")"; do
        if PKG_CONFIG_LIBDIR="$candidate" PKG_CONFIG_PATH="" \
            pkg-config --exists vpx dav1d 2>/dev/null
        then
            printf '%s' "$candidate"
            return 0
        fi
    done
    error "pkg-config cannot resolve vpx.pc and dav1d.pc in: $dir"
    return 1
}

build_ffmpeg_for_abi() {
    local abi="$1"
    local arch target cpu extra_cflags
    local -a abi_flags=()

    target="$(abi_triple "$abi")"
    case "$abi" in
        arm64-v8a)
            arch="aarch64"
            cpu="armv8-a"
            extra_cflags="-march=armv8-a"
            ;;
        armeabi-v7a)
            arch="arm"
            cpu="armv7-a"
            extra_cflags="-march=armv7-a -mfloat-abi=softfp -mfpu=neon"
            abi_flags+=(--enable-neon)
            ;;
        x86_64)
            arch="x86_64"
            cpu="x86-64"
            extra_cflags="-march=x86-64${FFMPEG_X86_64_EXTRA_ISA_CFLAGS:+ $FFMPEG_X86_64_EXTRA_ISA_CFLAGS}"
            append_configure_toggle abi_flags mmx "$FFMPEG_X86_64_MMX"
            append_configure_toggle abi_flags inline-asm "$FFMPEG_X86_64_INLINE_ASM"
            append_configure_toggle abi_flags x86asm "$FFMPEG_X86_64_X86ASM"
            ;;
        x86)
            arch="x86"
            cpu="i686"
            extra_cflags="-march=i686${FFMPEG_X86_EXTRA_ISA_CFLAGS:+ $FFMPEG_X86_EXTRA_ISA_CFLAGS}"
            append_configure_toggle abi_flags mmx "$FFMPEG_X86_MMX"
            append_configure_toggle abi_flags inline-asm "$FFMPEG_X86_INLINE_ASM"
            append_configure_toggle abi_flags x86asm "$FFMPEG_X86_X86ASM"
            ;;
        *) error "Unsupported ABI: $abi"; return 1 ;;
    esac

    check_x86_assembler "$abi" || return 1

    local vpx_prefix="$LIBVPX_OUTPUT_DIR/$abi"
    local dav1d_prefix="$DAV1D_OUTPUT_DIR/$abi"

    [[ -f "$vpx_prefix/lib/libvpx.a" ]] || {
        error "Missing libvpx for $abi. Run build_libvpx.sh first."
        return 1
    }
    [[ -f "$vpx_prefix/lib/pkgconfig/vpx.pc" ]] || { error "Missing vpx.pc for $abi"; return 1; }
    [[ -f "$dav1d_prefix/lib/libdav1d.a" ]] || {
        error "Missing dav1d for $abi. Run build_dav1d.sh first."
        return 1
    }
    [[ -f "$dav1d_prefix/lib/pkgconfig/dav1d.pc" ]] || { error "Missing dav1d.pc for $abi"; return 1; }

    local prefix="$FFMPEG_OUTPUT_DIR/$abi"
    local build_dir="$FFMPEG_WORK_DIR/$abi"
    local cc cxx
    cc="$(clang_driver "$target" clang)"
    cxx="$(clang_driver "$target" clang++)"

    if [[ "$CLEAN" == "1" ]]; then
        rm -rf "$build_dir" "$prefix"
    fi
    mkdir -p "$build_dir" "$prefix"

    local pc_dir="$build_dir/pkgconfig"
    rm -rf "$pc_dir"
    mkdir -p "$pc_dir"
    cp -f "$vpx_prefix/lib/pkgconfig/vpx.pc" "$dav1d_prefix/lib/pkgconfig/dav1d.pc" "$pc_dir/"

    local pkgconfig_libdir
    pkgconfig_libdir="$(resolve_pkgconfig_libdir "$pc_dir")" || return 1

    local -a size_flags=()
    [[ "$ENABLE_SMALL" == "1" ]] && size_flags+=(--enable-small)

    local -a configure_env=(
        "PKG_CONFIG_PATH=$pkgconfig_libdir"
        "PKG_CONFIG_LIBDIR=$pkgconfig_libdir"
        "ARFLAGS=rcD"
    )

    local -a configure_args=(
        --prefix="$prefix"
        --target-os=android
        --arch="$arch"
        --cpu="$cpu"
        --enable-cross-compile
        --cc="$cc"
        --cxx="$cxx"
        --ar="$AR_BIN"
        --nm="$NM_BIN"
        --ranlib="$RANLIB_BIN"
        --strip="$STRIP_BIN"
        --sysroot="$(to_native_path "$TOOLCHAIN/sysroot")"

        --enable-pic
        --enable-static
        --disable-shared
        --enable-optimizations
        --enable-pthreads

        --disable-doc
        --disable-debug
        --disable-programs
        --disable-avdevice
        --disable-avfilter
        --disable-network
        --disable-autodetect
        --disable-everything

        --enable-avcodec
        --enable-avformat
        --enable-avutil
        --enable-swscale
        --enable-swresample

        --enable-protocol=file

        --enable-decoder=h264
        --enable-decoder=hevc
        --enable-decoder=mpeg4
        --enable-decoder=mjpeg
        --enable-decoder=gif
        --enable-decoder=alac
        --enable-decoder=opus
        --enable-decoder=mp3
        --enable-decoder=aac

        --enable-demuxer=mov
        --enable-demuxer=gif
        --enable-demuxer=ogg
        --enable-demuxer=matroska
        --enable-demuxer=mp3
        --enable-demuxer=aac

        --enable-muxer=matroska

        --enable-bsf=vp9_superframe
        --enable-bsf=vp9_raw_reorder

        --enable-libvpx
        --enable-libdav1d
        --enable-decoder=libdav1d
        --enable-decoder=libvpx_vp9
        --enable-encoder=libvpx_vp9
        --pkg-config-flags=--static

        # --disable-autodetect only stops probing for zlib, it does not forbid
        # a system copy. (--disable-postproc is gone: libpostproc was removed
        # in FFmpeg 8.0 and configure rejects the unknown option.)
        --disable-zlib

        "--extra-cflags=-fPIC -DANDROID $extra_cflags${COMMON_CFLAGS:+ $COMMON_CFLAGS} -I$(to_native_path "$vpx_prefix/include") -I$(to_native_path "$dav1d_prefix/include")"
        "--extra-ldflags=-Wl,-Bsymbolic -L$(to_native_path "$vpx_prefix/lib") -L$(to_native_path "$dav1d_prefix/lib")"
        # Static-only build against prebuilt libvpx.a + libdav1d.a. -ldl comes
        # from pkg-config on some hosts but is not shipped by NDK r27; lld on
        # Windows then deadlocks while scanning it. Keep it out, the NDK libc
        # already exports the relevant symbols.
        "--extra-libs=-lm"
    )

    [[ -n "$FFMPEG_OPTFLAGS" ]] && configure_args+=("--optflags=$FFMPEG_OPTFLAGS")

    if [[ "$FFMPEG_PARSERS" == "1" ]]; then
        configure_args+=(
            --enable-parser=h264
            --enable-parser=hevc
            --enable-parser=mpeg4video
            --enable-parser=mpegaudio
            --enable-parser=aac
            --enable-parser=opus
            --enable-parser=av1
        )
    fi

    if [[ "$FFMPEG_LIBVPX_VP8" == "1" ]]; then
        configure_args+=(
            --enable-decoder=libvpx_vp8
            --enable-encoder=libvpx_vp8
        )
    fi

    if [[ "$abi" == "x86" || "$abi" == "x86_64" ]]; then
        append_configure_toggle configure_args runtime-cpudetect "$FFMPEG_RUNTIME_CPUDETECT"
        append_configure_toggle configure_args mmx "$FFMPEG_MMX"
        append_configure_toggle configure_args mmxext "$FFMPEG_MMXEXT"
        append_configure_toggle configure_args sse "$FFMPEG_SSE"
        append_configure_toggle configure_args sse2 "$FFMPEG_SSE2"
        append_configure_toggle configure_args sse3 "$FFMPEG_SSE3"
        append_configure_toggle configure_args ssse3 "$FFMPEG_SSSE3"
        append_configure_toggle configure_args sse4 "$FFMPEG_SSE4"
        append_configure_toggle configure_args sse42 "$FFMPEG_SSE42"
        append_configure_toggle configure_args avx "$FFMPEG_AVX"
        append_configure_toggle configure_args avx2 "$FFMPEG_AVX2"
        append_configure_toggle configure_args avx512 "$FFMPEG_AVX512"
        append_configure_toggle configure_args fma3 "$FFMPEG_FMA3"
        append_configure_toggle configure_args fma4 "$FFMPEG_FMA4"
        append_configure_toggle configure_args bmi1 "$FFMPEG_BMI1"
        append_configure_toggle configure_args bmi2 "$FFMPEG_BMI2"
    fi

    configure_args+=("${size_flags[@]}")
    configure_args+=("${abi_flags[@]}")

    echo
    echo "========== FFmpeg: $abi, API $API =========="

    pushd "$build_dir" >/dev/null

    if ! env "${configure_env[@]}" "$FFMPEG_SOURCE_DIR/configure" "${configure_args[@]}"; then
        error "FFmpeg configure failed for $abi. Log: $build_dir/ffbuild/config.log"
        popd >/dev/null
        return 1
    fi

    local components_header="$build_dir/config_components.h"
    [[ -f "$components_header" ]] || components_header="$build_dir/config.h"

    local -a required_components=(
        CONFIG_LIBDAV1D_DECODER
        CONFIG_LIBVPX_VP9_DECODER
        CONFIG_LIBVPX_VP9_ENCODER
    )
    if [[ "$FFMPEG_LIBVPX_VP8" == "1" ]]; then
        required_components+=(
            CONFIG_LIBVPX_VP8_DECODER
            CONFIG_LIBVPX_VP8_ENCODER
        )
    fi

    local component
    for component in "${required_components[@]}"; do
        if ! grep -Eq "^#define[[:space:]]+$component[[:space:]]+1$" "$components_header"; then
            error "FFmpeg component is disabled for $abi: $component (see $components_header and $build_dir/ffbuild/config.log)"
            popd >/dev/null
            return 1
        fi
    done

    # Keep only the specialized implementations: VP8/VP9 -> libvpx, AV1 -> dav1d.
    for component in CONFIG_VP8_DECODER CONFIG_VP9_DECODER CONFIG_AV1_DECODER; do
        if ! grep -Eq "^#define[[:space:]]+$component[[:space:]]+0$" "$components_header"; then
            error "Built-in FFmpeg decoder is unexpectedly enabled for $abi: $component"
            popd >/dev/null
            return 1
        fi
    done

    local enabled_av1_decoders
    enabled_av1_decoders="$(
        awk '/^#define[[:space:]]+CONFIG_.*(AV1|DAV1D).*_DECODER[[:space:]]+1$/ { print $2 }' \
            "$components_header" | LC_ALL=C sort
    )"
    if [[ "$enabled_av1_decoders" != "CONFIG_LIBDAV1D_DECODER" ]]; then
        error "Unexpected enabled AV1 decoders for $abi:"
        printf '%s\n' "${enabled_av1_decoders:-<none>}" >&2
        error "Exactly CONFIG_LIBDAV1D_DECODER must be enabled."
        popd >/dev/null
        return 1
    fi

    if ! make -j"$JOBS" 2>&1 | tee "$build_dir/build.log"; then
        error "FFmpeg build failed for $abi. Log: $build_dir/build.log"
        popd >/dev/null
        return 1
    fi

    if ! make install 2>&1 | tee "$build_dir/install.log"; then
        error "FFmpeg install failed for $abi. Log: $build_dir/install.log"
        popd >/dev/null
        return 1
    fi

    popd >/dev/null
}

# ---------------------------------------------------------------------------
# Packaging
# ---------------------------------------------------------------------------
package_libvpx_headers() {
    local include_dir="$1"
    local temporary_include="$BUILD_ROOT/libvpx_include"

    rm -rf "$temporary_include"
    mkdir -p "$temporary_include"

    # libvpx installs its public headers under include/vpx. Merge them there
    # first so the ABI comparison and dispatcher generation see the original
    # layout.
    merge_component_headers "libvpx" "$LIBVPX_OUTPUT_DIR" "$temporary_include" || return 1

    [[ -d "$temporary_include/vpx" ]] || {
        error "Merged libvpx headers were not found: $temporary_include/vpx"
        return 1
    }

    rm -rf "$include_dir/libvpx"
    mv "$temporary_include/vpx" "$include_dir/libvpx"
    rm -rf "$temporary_include"

    # Installed libvpx headers refer to one another as <vpx/...>, but the
    # Telegram tree includes them as <libvpx/...>. Rewrite only those include
    # paths in the final package; the intermediate installs stay untouched so
    # FFmpeg's own <vpx/...> includes keep working.
    local header
    while IFS= read -r -d '' header; do
        sed -i.bak \
            -e 's#<vpx/#<libvpx/#g' \
            -e 's#"vpx/#"libvpx/#g' \
            "$header"
        rm -f "${header}.bak"
    done < <(find "$include_dir/libvpx" -type f -name '*.h' -print0)

    [[ -f "$include_dir/libvpx/vpx_codec.h" ]] || {
        error "Missing packaged libvpx header: $include_dir/libvpx/vpx_codec.h"
        return 1
    }
    [[ -f "$include_dir/libvpx/vpx_decoder.h" ]] || {
        error "Missing packaged decoder header: $include_dir/libvpx/vpx_decoder.h"
        return 1
    }
}

report_archive_flags() {
    [[ -x "$READELF_BIN" ]] || return 0

    echo
    echo "========== Section report =========="
    echo "  function-sections / addrsig enable -Wl,--gc-sections and -Wl,--icf=safe;"
    echo "  eh_frame is unwind data that survives into the linked .so."

    local abi archive name sections count_text count_addrsig count_eh
    for abi in $ABIS; do
        for name in libavcodec libvpx libdav1d; do
            archive="$PACKAGE_DIR/$abi/$name.a"
            [[ -f "$archive" ]] || continue

            sections="$("$READELF_BIN" --section-headers --wide "$archive" 2>/dev/null || true)"
            count_text="$(grep -c ' \.text\.' <<<"$sections" || true)"
            count_addrsig="$(grep -c '\.llvm_addrsig' <<<"$sections" || true)"
            count_eh="$(grep -c ' \.eh_frame' <<<"$sections" || true)"

            printf '  %-12s %-11s .text.* = %-6s .llvm_addrsig = %-6s .eh_frame = %s\n' \
                "$abi" "$name" "$count_text" "$count_addrsig" "$count_eh"
        done
    done
}

package_outputs() {
    echo
    echo "========== Packaging common include and per-ABI libraries =========="

    OUTPUT_DIR="$PACKAGE_DIR/build"

    mkdir -p "$OUTPUT_DIR"
    rm -rf "$OUTPUT_DIR/include"
    mkdir -p "$OUTPUT_DIR/include"

    local abi
    for abi in $ABIS; do
        rm -rf "$OUTPUT_DIR/$abi"
        mkdir -p "$OUTPUT_DIR/$abi"
    done

    package_libvpx_headers "$OUTPUT_DIR/include"
    copy_component_libraries "libvpx" "$LIBVPX_OUTPUT_DIR" "$OUTPUT_DIR"

    merge_component_headers "dav1d" "$DAV1D_OUTPUT_DIR" "$OUTPUT_DIR/include"
    copy_component_libraries "dav1d" "$DAV1D_OUTPUT_DIR" "$OUTPUT_DIR"

    merge_component_headers "ffmpeg" "$FFMPEG_OUTPUT_DIR" "$OUTPUT_DIR/include"
    copy_component_libraries "ffmpeg" "$FFMPEG_OUTPUT_DIR" "$OUTPUT_DIR"

    echo "Package created:"
    echo "  Headers:   $OUTPUT_DIR/include"
    echo "  Libraries: $OUTPUT_DIR/<ABI>"

    report_archive_flags
}

TARGET_ABIS="${*:-$ABIS}"

mkdir -p "$FFMPEG_WORK_DIR" "$FFMPEG_OUTPUT_DIR"

if [[ "${PACKAGE_ONLY:-0}" != "1" ]]; then
    for abi in $TARGET_ABIS; do
        build_ffmpeg_for_abi "$abi"
    done
fi

# Packaging merges headers across $ABIS, so it can only run once every ABI in
# that list has been built. Skip it for a partial rebuild.
if [[ "$PACKAGE_OUTPUT" == "1" && "$TARGET_ABIS" == "$ABIS" ]]; then
    package_outputs
fi

echo
echo "FFmpeg install: $FFMPEG_OUTPUT_DIR/<ABI>"

