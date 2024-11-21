#!/bin/bash
set -e
function build_one {
	echo "Building ${ARCH}..."

	PREBUILT=${NDK}/toolchains/${PREBUILT_ARCH}-${VERSION}/prebuilt/${BUILD_PLATFORM}
	PLATFORM=${NDK}/platforms/android-${ANDROID_API}/arch-${ARCH}

	TOOLS_PREFIX="${LLVM_BIN}/${ARCH_NAME}-linux-${BIN_MIDDLE}-"

	export LD=${TOOLS_PREFIX}ld
	export AR=${TOOLS_PREFIX}ar
	export STRIP=${TOOLS_PREFIX}strip
	export RANLIB=${TOOLS_PREFIX}ranlib
	export NM=${TOOLS_PREFIX}nm

	export CC_PREFIX="${LLVM_BIN}/${CLANG_PREFIX}-linux-${BIN_MIDDLE}${ANDROID_API}-"

	export CC=${CC_PREFIX}clang
	export CXX=${CC_PREFIX}clang++
	export AS=${CC_PREFIX}clang++
	export CROSS_PREFIX=${PREBUILT}/bin/${ARCH_NAME}-linux-${BIN_MIDDLE}-
	
	
	export CFLAGS="-DANDROID -fpic -fpie ${OPTIMIZE_CFLAGS}"
	export CPPFLAGS="${CFLAGS}"
	export CXXFLAGS="${CFLAGS} -std=c++11"
	export ASFLAGS="-D__ANDROID__"
	export LDFLAGS="-L${PLATFORM}/usr/lib"

	meson setup builddir-${ARCH} \
    --prefix "$PREFIX" \
    --libdir="lib" \
    --includedir="include" \
    --buildtype=release -Denable_tests=false -Denable_tools=false -Ddefault_library=static \
    --cross-file <(echo "
      [binaries]
      c = '${CC}'
      ar = '${AR}'

      [host_machine]
      system = '${BIN_MIDDLE}'
      cpu_family = '${PREBUILT_ARCH}'
      cpu = '${CPU}'
      endian = 'little'
    ")
  cd builddir-${ARCH}
  meson compile && meson install
  cd ..
#  ninja -C builddir-${ARCH}
#  ninja -C builddir-${ARCH} install
}

function setCurrentPlatform {

	CURRENT_PLATFORM="$(uname -s)"
	case "${CURRENT_PLATFORM}" in
		Darwin*)
			BUILD_PLATFORM=darwin-x86_64
			COMPILATION_PROC_COUNT=`sysctl -n hw.physicalcpu`
			;;
		Linux*)
			BUILD_PLATFORM=linux-x86_64
			COMPILATION_PROC_COUNT=$(nproc)
			;;
		*)
			echo -e "\033[33mWarning! Unknown platform ${CURRENT_PLATFORM}! falling back to linux-x86_64\033[0m"
			BUILD_PLATFORM=linux-x86_64
			COMPILATION_PROC_COUNT=1
			;;
	esac

	echo "Build platform: ${BUILD_PLATFORM}"
	echo "Parallel jobs: ${COMPILATION_PROC_COUNT}"

}

function checkPreRequisites {

	if ! [ -d "dav1d" ] || ! [ "$(ls -A dav1d)" ]; then
		echo -e "\033[31mFailed! Submodule 'dav1d' not found!\033[0m"
		echo -e "\033[31mTry to run: 'git submodule init && git submodule update'\033[0m"
		exit
	fi

	if [ -z "$NDK" -a "$NDK" == "" ]; then
		echo -e "\033[31mFailed! NDK is empty. Run 'export NDK=[PATH_TO_NDK]'\033[0m"
		exit
	fi
}

setCurrentPlatform
checkPreRequisites

cd dav1d

## common
LLVM_PREFIX="${NDK}/toolchains/llvm/prebuilt/linux-x86_64"
LLVM_BIN="${LLVM_PREFIX}/bin"
PREFIX_D=$(realpath .)
VERSION="4.9"
ANDROID_API=21

function build {
	for arg in "$@"; do
		case "${arg}" in
			x86_64)
        ANDROID_API=21
				ARCH=x86_64
				ARCH_NAME=x86_64
				PREBUILT_ARCH=x86_64
				CLANG_PREFIX=x86_64
				BIN_MIDDLE=android
				CPU=x86_64
				CPU_NAME=x86_64
				PREFIX=${PREFIX_D}/build/$CPU
				build_one
			;;
			x86)
        ANDROID_API=21
				ARCH=x86
				ARCH_NAME=i686
				PREBUILT_ARCH=x86
				CLANG_PREFIX=i686
				BIN_MIDDLE=android
				CPU=i686
				CPU_NAME=i686
				PREFIX=${PREFIX_D}/build/x86
				build_one
			;;
			arm64)
        ANDROID_API=21
				ARCH=arm64
				ARCH_NAME=aarch64
				PREBUILT_ARCH=aarch64
				CLANG_PREFIX=aarch64
				BIN_MIDDLE=android
				CPU=arm64-v8a
				CPU_NAME=arm64
				PREFIX=${PREFIX_D}/build/$CPU
				build_one
			;;
			arm)
        ANDROID_API=21
				ARCH=arm
				ARCH_NAME=arm
				PREBUILT_ARCH=arm
				CLANG_PREFIX=armv7a
				BIN_MIDDLE=androideabi
				CPU=armeabi-v7a
				CPU_NAME=armv7
				PREFIX=${PREFIX_D}/build/$CPU
				build_one
			;;
			*)
			;;
		esac
	done
}

if (( $# == 0 )); then
	build arm arm64
else
	build $@
fi
