#!/usr/bin/env bash
#
# 处理 git submodule:
#
#   1. 若仓库根目录存在 submodules.tar.gz, 直接解压还原 submodule 内容;
#   2. 否则执行 git submodule sync && git submodule update --init --force,
#      并把 .gitmodules 中声明的所有 submodule 路径打包成 submodules.tar.gz.
#
# 用法:
#   bash bin/scripts/submodule.sh
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

GITMODULES="$REPO_ROOT/.gitmodules"
ARCHIVE_NAME="submodules.tar.gz"

warn() { echo "[submodule] $*" >&2 || true; }

cd "$REPO_ROOT"

if [ ! -f "$GITMODULES" ]; then
  warn "找不到 .gitmodules: $GITMODULES"
  exit 1
fi

# 读取 .gitmodules 中声明的所有 submodule path
# git config -z 输出 "key\nvalue\0", 用 NUL 分隔可兼容路径中含空格的情况
SUBMODULE_PATHS=()
while IFS= read -r -d '' record; do
  path="${record#*$'\n'}"
  [ -n "$path" ] && SUBMODULE_PATHS+=("$path")
done < <(git config -z --file "$GITMODULES" --get-regexp '^submodule\..*\.path$')

if [ "${#SUBMODULE_PATHS[@]}" -eq 0 ]; then
  warn ".gitmodules 中没有找到 submodule path"
  exit 1
fi

echo "[submodule] 共发现 ${#SUBMODULE_PATHS[@]} 个 submodule"

# 只打包实际存在的目录, 避免个别 submodule 未初始化时 tar 直接失败
EXISTING_PATHS=()
for path in "${SUBMODULE_PATHS[@]}"; do
  if [ -d "$path" ]; then
    EXISTING_PATHS+=("$path")
  else
    warn "跳过不存在的 submodule 目录: $path"
  fi
done

if [ -f "$ARCHIVE_NAME" ]; then
  echo "[submodule] 检测到 $ARCHIVE_NAME, 开始解压..."

  # docker nonroot 下无权还原属主/权限/时间, 必须显式关闭这些动作,
  # 否则 tar 会报 "Cannot utime" / "Cannot change mode: Operation not permitted".
  #   --no-same-owner       不还原属主, 文件归当前用户
  #   --no-same-permissions 不还原权限, 由 umask 决定
  #   --touch               不还原 mtime
  #   --no-overwrite-dir    已存在的目录保留原有元数据(GNU tar 专有)
  EXTRACT_OPTS=(--no-same-owner --no-same-permissions --touch)
  TAR_HELP="$(tar --help 2>&1 || true)"
  case "$TAR_HELP" in
    *--no-overwrite-dir*) EXTRACT_OPTS+=(--no-overwrite-dir) ;;
  esac

  tar_status=0
  tar -xzf "$ARCHIVE_NAME" -C "$REPO_ROOT" "${EXTRACT_OPTS[@]}" || tar_status=$?
  # tar 退出码 1 表示部分文件有差异(告警), 2 及以上才是致命错误
  if [ "$tar_status" -gt 1 ]; then
    warn "解压失败 (tar 退出码 $tar_status)"
    exit 1
  fi

  echo "[submodule] 解压完成: $REPO_ROOT/$ARCHIVE_NAME"
  echo "[submodule] 开始拉取 tgcalls..."
  rm -rf TMessagesProj/jni/tgcalls && git submodule update --init --force --depth 1 TMessagesProj/jni/tgcalls
  exit 0
fi

echo "[submodule] 未检测到 $ARCHIVE_NAME, 开始拉取 submodule..."
git submodule sync
git submodule update --init --force --depth 1

echo "[submodule] 打包 submodule -> $ARCHIVE_NAME"
# tar 在文件读取期间被修改时返回 1, 这里只关心压缩包是否生成成功
tar -czf "$ARCHIVE_NAME" "${EXISTING_PATHS[@]}" || true
if [ ! -f "$ARCHIVE_NAME" ]; then
  warn "打包失败: $ARCHIVE_NAME 未生成"
  exit 1
fi

echo "[submodule] 完成: $REPO_ROOT/$ARCHIVE_NAME"
