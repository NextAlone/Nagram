#!/usr/bin/env bash
# Shared configuration for the Android prebuilt dependencies
# (libvpx / dav1d / FFmpeg).
#
# Reference: ../ffmpeg/build_ffmpeg_libvpx_dav1d_android_ndk27_merged.sh
# That file is upstream and stays untouched. The scripts here reproduce its
# configuration and verification steps, split into per-component entry points
# (build_libvpx.sh / build_dav1d.sh / build_ffmpeg.sh) and extended with
# Windows MINGW64 host support.
#
# Layout produced (consumed by TMessagesProj/jni/CMakeLists.txt):
#   jni/ffmpeg/include/           merged headers (libvpx/, dav1d/, libav*/, libsw*/)
#   jni/ffmpeg/<ABI>/*.a          static libraries
#   jni/ffmpeg/build_android/     intermediate build + install trees

if [[ -n "${BUILD_COMMON_SOURCED:-}" ]]; then
    return 0
fi
BUILD_COMMON_SOURCED=1

set -Eeuo pipefail

# Reproducible-build environment; SOURCE_DATE_EPOCH may be overridden.
SOURCE_DATE_EPOCH="${SOURCE_DATE_EPOCH:-946684800}"
export SOURCE_DATE_EPOCH
export TZ=UTC
export LC_ALL=C
export LANG=C
export PYTHONHASHSEED=0
export ZERO_AR_DATE=1
umask 022

error() { echo "ERROR: $*" >&2; }
die() { error "$*"; exit 1; }

require_bool() {
    case "$2" in
        0|1) ;;
        *) die "$1 must be 0 or 1, got: $2" ;;
    esac
}

require_toggle() {
    case "$2" in
        auto|on|off) ;;
        *) die "$1 must be auto, on or off, got: $2" ;;
    esac
}

# usage: append_configure_toggle <array-name> <feature> <auto|on|off>
append_configure_toggle() {
    local -n destination="$1"
    case "$3" in
        auto) ;;
        on) destination+=("--enable-$2") ;;
        off) destination+=("--disable-$2") ;;
    esac
}

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
THIRD_PARTY_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
JNI_DIR="$(cd "$THIRD_PARTY_DIR/.." && pwd)"

LIBVPX_SOURCE_DIR="${LIBVPX_SOURCE_DIR:-$THIRD_PARTY_DIR/libvpx}"
DAV1D_SOURCE_DIR="${DAV1D_SOURCE_DIR:-$THIRD_PARTY_DIR/dav1d}"
FFMPEG_SOURCE_DIR="${FFMPEG_SOURCE_DIR:-$THIRD_PARTY_DIR/ffmpeg}"

# Final package consumed by jni/CMakeLists.txt.
PACKAGE_DIR="${PACKAGE_DIR:-$JNI_DIR/ffmpeg}"
BUILD_ROOT="${BUILD_ROOT:-$PACKAGE_DIR/build_android}"

LIBVPX_WORK_DIR="$BUILD_ROOT/work/libvpx"
DAV1D_WORK_DIR="$BUILD_ROOT/work/dav1d"
FFMPEG_WORK_DIR="$BUILD_ROOT/work/ffmpeg"
LIBVPX_OUTPUT_DIR="$BUILD_ROOT/libvpx"
DAV1D_OUTPUT_DIR="$BUILD_ROOT/dav1d"
FFMPEG_OUTPUT_DIR="$BUILD_ROOT/ffmpeg"

# ---------------------------------------------------------------------------
# Build options
# ---------------------------------------------------------------------------
NDK_VERSION="${NDK_VERSION:-27.2.12479018}"
API="${API:-21}"
# All four ABIs are built by default: the packaging step merges headers across
# ABIS and emits per-ABI dispatchers, so a package built from a subset would
# #error out when compiled for a missing ABI.
#ABIS="${ABIS:-arm64-v8a armeabi-v7a x86_64 x86}"
ABIS="${ABIS:-arm64-v8a armeabi-v7a}"
CLEAN="${CLEAN:-1}"
PACKAGE_OUTPUT="${PACKAGE_OUTPUT:-1}"
JOBS="${JOBS:-$(getconf _NPROCESSORS_ONLN 2>/dev/null || sysctl -n hw.logicalcpu 2>/dev/null || echo 8)}"

# Code-generation switches shared by all three components. See the upstream
# reference script for the rationale behind each default.
UNWIND_TABLES="${UNWIND_TABLES:-0}"
FRAME_POINTERS="${FRAME_POINTERS:-0}"
FUNCTION_SECTIONS="${FUNCTION_SECTIONS:-1}"
ADDRSIG="${ADDRSIG:-1}"
LIBVPX_ADDRSIG="${LIBVPX_ADDRSIG:-0}"
HIDDEN_VISIBILITY="${HIDDEN_VISIBILITY:-0}"

require_bool CLEAN "$CLEAN"
require_bool PACKAGE_OUTPUT "$PACKAGE_OUTPUT"
require_bool UNWIND_TABLES "$UNWIND_TABLES"
require_bool FRAME_POINTERS "$FRAME_POINTERS"
require_bool FUNCTION_SECTIONS "$FUNCTION_SECTIONS"
require_bool ADDRSIG "$ADDRSIG"
require_bool LIBVPX_ADDRSIG "$LIBVPX_ADDRSIG"
require_bool HIDDEN_VISIBILITY "$HIDDEN_VISIBILITY"

# ---------------------------------------------------------------------------
# Host detection
#
# HOST_IS_WINDOWS=1 on MSYS2/MINGW64/Cygwin. On those hosts:
#   - the per-API clang drivers are ${triple}${API}-clang.cmd wrappers and the
#     binutils are llvm-*.exe;
#   - configure scripts that hand paths to a native (non-MSYS) tool need
#     Windows paths, so to_native_path() converts them with cygpath -m.
# ---------------------------------------------------------------------------
HOST_IS_WINDOWS=0
case "$(uname -s)" in
    Linux)
        case "$(uname -m)" in
            x86_64) HOST_TAG="linux-x86_64" ;;
            aarch64|arm64) HOST_TAG="linux-aarch64" ;;
            *) die "Unsupported Linux host architecture: $(uname -m)" ;;
        esac
        ;;
    Darwin)
        case "$(uname -m)" in
            x86_64) HOST_TAG="darwin-x86_64" ;;
            arm64) HOST_TAG="darwin-arm64" ;;
            *) die "Unsupported macOS host architecture: $(uname -m)" ;;
        esac
        ;;
    MINGW*|MSYS*|CYGWIN*)
        HOST_IS_WINDOWS=1
        HOST_TAG="windows-x86_64"
        ;;
    *) die "Unsupported host: $(uname -s). Use Linux, macOS or MSYS2/MINGW64." ;;
esac

# Convert an MSYS/Cygwin path to a Windows path. No-op elsewhere.
to_native_path() {
    if [[ "$HOST_IS_WINDOWS" == "1" ]] && command -v cygpath >/dev/null 2>&1; then
        cygpath -m -- "$1"
    else
        printf '%s' "$1"
    fi
}

# ---------------------------------------------------------------------------
# NDK / toolchain
# ---------------------------------------------------------------------------
locate_ndk() {
    local candidate sdk_root

    if [[ -n "${ANDROID_NDK_HOME:-}" && -d "$ANDROID_NDK_HOME" ]]; then
        :
    elif [[ -n "${NDK:-}" && -d "$NDK" ]]; then
        ANDROID_NDK_HOME="$NDK"
    else
        sdk_root="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"
        if [[ -z "$sdk_root" ]]; then
            for candidate in \
                "${HOME:-}/Android/Sdk" \
                "${HOME:-}/Library/Android/sdk" \
                "${HOME:-}/.local/lib/android/sdk" \
                "${LOCALAPPDATA:-}/Android/Sdk"
            do
                if [[ -d "$candidate/ndk/$NDK_VERSION" ]]; then
                    sdk_root="$candidate"
                    break
                fi
            done
        fi
        [[ -n "$sdk_root" ]] && ANDROID_NDK_HOME="$sdk_root/ndk/$NDK_VERSION"
    fi

    if [[ -z "${ANDROID_NDK_HOME:-}" || ! -d "$ANDROID_NDK_HOME" ]]; then
        error "Android NDK $NDK_VERSION was not found."
        echo "Set ANDROID_NDK_HOME, for example:" >&2
        echo "  ANDROID_NDK_HOME=\"\$HOME/Android/Sdk/ndk/$NDK_VERSION\" $0" >&2
        exit 1
    fi

    ANDROID_NDK_HOME="$(cd "$ANDROID_NDK_HOME" && pwd)"
    export ANDROID_NDK_HOME

    TOOLCHAIN="$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/$HOST_TAG"
    [[ -d "$TOOLCHAIN" ]] || die "LLVM toolchain was not found: $TOOLCHAIN"

    # NDK r23+ ships unified llvm-* binutils only; the ${triple}-ar/ld/nm
    # wrappers are gone. On Windows they carry a .exe suffix.
    TOOL_SUFFIX=""
    [[ -x "$TOOLCHAIN/bin/llvm-ar.exe" ]] && TOOL_SUFFIX=".exe"

    AR_BIN="$TOOLCHAIN/bin/llvm-ar$TOOL_SUFFIX"
    NM_BIN="$TOOLCHAIN/bin/llvm-nm$TOOL_SUFFIX"
    STRIP_BIN="$TOOLCHAIN/bin/llvm-strip$TOOL_SUFFIX"
    RANLIB_BIN="$TOOLCHAIN/bin/llvm-ranlib$TOOL_SUFFIX"
    OBJCOPY_BIN="$TOOLCHAIN/bin/llvm-objcopy$TOOL_SUFFIX"
    READELF_BIN="$TOOLCHAIN/bin/llvm-readelf$TOOL_SUFFIX"

    for tool in "$AR_BIN" "$NM_BIN" "$STRIP_BIN" "$RANLIB_BIN"; do
        [[ -x "$tool" ]] || die "Missing NDK tool: $tool"
    done
}

# clang driver for an ABI triple.
#
# On Windows the NDK ships two wrappers per triple: an extension-less bash
# script and a ${name}.cmd batch file. Which one is usable depends on who
# spawns it:
#   shell  (default) - the script. Anything launched through the MSYS shell or
#                      make can run it, and libvpx's configure only appends the
#                      required `-c` to ASFLAGS when $AS matches the glob
#                      `*clang`, which the .cmd name would not.
#   native           - the .cmd. Required for tools that spawn the compiler
#                      through CreateProcess without a shell (meson/ninja).
clang_driver() {
    local triple="$1" kind="${2:-clang}" flavor="${3:-shell}" base
    base="$TOOLCHAIN/bin/${triple}${API}-${kind}"

    if [[ "$HOST_IS_WINDOWS" == "1" && "$flavor" == "native" ]]; then
        [[ -f "$base.cmd" ]] || die "clang driver was not found: $base.cmd"
        printf '%s' "$base.cmd"
        return 0
    fi

    [[ -f "$base" ]] || die "clang driver was not found: $base"
    printf '%s' "$base"
}

# Clang target triple for an ABI.
abi_triple() {
    case "$1" in
        arm64-v8a)   printf 'aarch64-linux-android' ;;
        armeabi-v7a) printf 'armv7a-linux-androideabi' ;;
        x86_64)      printf 'x86_64-linux-android' ;;
        x86)         printf 'i686-linux-android' ;;
        *) die "Unsupported ABI: $1" ;;
    esac
}

check_x86_assembler() {
    case "$1" in
        x86|x86_64)
            if ! command -v nasm >/dev/null 2>&1 && ! command -v yasm >/dev/null 2>&1; then
                error "NASM or Yasm is required for $1."
                echo "Linux: sudo apt install nasm    MSYS2: pacman -S nasm" >&2
                return 1
            fi
            ;;
    esac
}

# ---------------------------------------------------------------------------
# Shared compiler flags
# ---------------------------------------------------------------------------
build_common_cflags() {
    local flags=""

    # Strip host-specific absolute paths out of DWARF, __FILE__ and
    # diagnostics. Windows paths must be passed in native form or clang will
    # never match them.
    local src_root build_root
    src_root="$(to_native_path "$JNI_DIR")"
    build_root="$(to_native_path "$BUILD_ROOT")"

    flags+=" -ffile-prefix-map=$src_root=/src"
    flags+=" -ffile-prefix-map=$build_root=/build"
    flags+=" -fdebug-compilation-dir=."

    [[ "$FUNCTION_SECTIONS" == "1" ]] && flags+=" -ffunction-sections -fdata-sections"
    [[ "$HIDDEN_VISIBILITY" == "1" ]] && flags+=" -fvisibility=hidden"
    if [[ "$UNWIND_TABLES" == "0" ]]; then
        flags+=" -fno-asynchronous-unwind-tables -fno-unwind-tables"
    fi
    if [[ "$FRAME_POINTERS" == "1" ]]; then
        flags+=" -fno-omit-frame-pointer"
    else
        flags+=" -fomit-frame-pointer"
    fi

    printf '%s' "${flags# }"
}

locate_ndk

# Both the libvpx and the FFmpeg configure scripts derive their scratch
# directory from TMPDIR/TEMPDIR/TMP. Under MSYS2 those often carry the Windows
# TEMP value, which mixes separators into every temp-file path the configure
# checks build. Point them at a POSIX directory we own.
if [[ "$HOST_IS_WINDOWS" == "1" ]]; then
    TMPDIR="$BUILD_ROOT/tmp"
    mkdir -p "$TMPDIR"
    export TMPDIR
    unset TEMPDIR
fi

COMMON_CFLAGS_BASE="$(build_common_cflags)"
COMMON_CFLAGS="$COMMON_CFLAGS_BASE"
LIBVPX_COMMON_CFLAGS="$COMMON_CFLAGS_BASE"

# -faddrsig emits the address-significance table that -Wl,--icf=safe needs.
# Clang does not enable it by default on Android. libvpx is excluded because
# its `%.a: %_g.a` strip rule zeroes sh_link on the table and lld then rejects
# it under --fatal-warnings; see build_libvpx.sh.
LIBVPX_SKIP_STRIP=0
if [[ "$ADDRSIG" == "1" ]]; then
    COMMON_CFLAGS+=" -faddrsig"
    if [[ "$LIBVPX_ADDRSIG" == "1" ]]; then
        LIBVPX_COMMON_CFLAGS+=" -faddrsig"
        LIBVPX_SKIP_STRIP=1
    fi
fi

# ---------------------------------------------------------------------------
# Packaging
#
# Each component installs into $BUILD_ROOT/<component>/<ABI>/{include,lib}.
# Packaging merges the per-ABI include trees into $PACKAGE_DIR/include and
# copies the archives to $PACKAGE_DIR/<ABI>. Headers that differ between ABIs
# (config-dependent ones) are emitted as <name>.<abi>.h plus a dispatcher
# <name>.h that selects on the predefined ABI macros.
# ---------------------------------------------------------------------------
abi_identifier() {
    case "$1" in
        arm64-v8a) printf 'arm64_v8a' ;;
        armeabi-v7a) printf 'armeabi_v7a' ;;
        x86_64) printf 'x86_64' ;;
        x86) printf 'x86' ;;
        *) die "Unsupported ABI in abi_identifier: $1" ;;
    esac
}

abi_cpp_condition() {
    case "$1" in
        arm64-v8a) printf 'defined(__aarch64__)' ;;
        armeabi-v7a) printf 'defined(__arm__) && !defined(__aarch64__)' ;;
        x86_64) printf 'defined(__x86_64__)' ;;
        x86) printf 'defined(__i386__)' ;;
        *) die "Unsupported ABI in abi_cpp_condition: $1" ;;
    esac
}

list_relative_files() {
    ( cd "$1" && find . -type f -print | sed 's#^\./##' | LC_ALL=C sort )
}

copy_or_dispatch_header() {
    local component="$1" relative_path="$2" source_base="$3" destination_root="$4"
    local reference_abi="" reference_file="" abi source_file
    local files_identical=1

    for abi in $ABIS; do
        source_file="$source_base/$abi/include/$relative_path"
        [[ -f "$source_file" ]] || { error "Missing $component header for $abi: $source_file"; return 1; }

        if [[ -z "$reference_abi" ]]; then
            reference_abi="$abi"
            reference_file="$source_file"
        elif ! cmp -s "$reference_file" "$source_file"; then
            files_identical=0
        fi
    done

    local destination_file="$destination_root/$relative_path"
    mkdir -p "$(dirname "$destination_file")"

    if [[ "$files_identical" == "1" ]]; then
        if [[ -e "$destination_file" ]] && ! cmp -s "$reference_file" "$destination_file"; then
            error "Header collision while packaging: $relative_path"
            return 1
        fi
        cp -f "$reference_file" "$destination_file"
        return 0
    fi

    if [[ "$relative_path" != *.h ]]; then
        error "$component file differs between ABIs and is not a header: $relative_path"
        return 1
    fi

    local destination_dir base_name stem
    destination_dir="$(dirname "$destination_file")"
    base_name="$(basename "$destination_file")"
    stem="${base_name%.h}"

    local abi_id
    for abi in $ABIS; do
        abi_id="$(abi_identifier "$abi")"
        cp -f "$source_base/$abi/include/$relative_path" "$destination_dir/${stem}.${abi_id}.h"
    done

    local guard
    guard="ANDROID_ABI_DISPATCH_$(printf '%s' "$relative_path" | tr '[:lower:]/.-' '[:upper:]____')"

    {
        echo "/* Auto-generated ABI dispatcher for $component. */"
        echo "#ifndef $guard"
        echo "#define $guard"
        echo
        local first=1 condition
        for abi in $ABIS; do
            abi_id="$(abi_identifier "$abi")"
            condition="$(abi_cpp_condition "$abi")"
            if [[ "$first" == "1" ]]; then
                echo "#if $condition"
                first=0
            else
                echo "#elif $condition"
            fi
            echo "#include \"${stem}.${abi_id}.h\""
        done
        echo "#else"
        echo "#error Unsupported Android ABI"
        echo "#endif"
        echo
        echo "#endif /* $guard */"
    } > "$destination_file"

    echo "ABI-dependent header: $component/$relative_path"
}

merge_component_headers() {
    local component="$1" source_base="$2" destination_root="$3"
    local reference_abi="" reference_manifest="" abi include_dir manifest

    local manifest_dir="$BUILD_ROOT/manifests"
    mkdir -p "$manifest_dir"

    for abi in $ABIS; do
        include_dir="$source_base/$abi/include"
        [[ -d "$include_dir" ]] || {
            error "Missing include directory for $component/$abi: $include_dir"
            return 1
        }

        manifest="$manifest_dir/${component}.${abi}.headers"
        list_relative_files "$include_dir" > "$manifest"

        if [[ -z "$reference_abi" ]]; then
            reference_abi="$abi"
            reference_manifest="$manifest"
        elif ! cmp -s "$reference_manifest" "$manifest"; then
            error "$component installs a different header set for $reference_abi and $abi."
            diff -u "$reference_manifest" "$manifest" || true
            return 1
        fi
    done

    local relative_path
    while IFS= read -r relative_path; do
        [[ -n "$relative_path" ]] || continue
        copy_or_dispatch_header "$component" "$relative_path" "$source_base" "$destination_root"
    done < "$reference_manifest"
}

copy_component_libraries() {
    local component="$1" source_base="$2" destination_root="$3"
    local abi source_lib_dir destination_lib_dir library destination_library

    for abi in $ABIS; do
        source_lib_dir="$source_base/$abi/lib"
        destination_lib_dir="$destination_root/$abi"

        [[ -d "$source_lib_dir" ]] || {
            error "Missing library directory for $component/$abi: $source_lib_dir"
            return 1
        }

        mkdir -p "$destination_lib_dir"

        while IFS= read -r -d '' library; do
            destination_library="$destination_lib_dir/$(basename "$library")"
            if [[ -e "$destination_library" ]] && ! cmp -s "$library" "$destination_library"; then
                error "Library collision while packaging: $destination_library"
                return 1
            fi
            cp -f "$library" "$destination_library"
        done < <(find "$source_lib_dir" -maxdepth 1 -type f \( -name '*.a' -o -name '*.so' \) -print0)
    done
}
