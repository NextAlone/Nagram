# Claude Code 说明

## 打包

- 打 release 包（无论是否签名）必须基于最新的 `dev`：先 `jj git fetch --remote origin`，把待打包的 change rebase 到 `dev` 上（`jj rebase -b @ -d dev`），不要基于本地过时的 `dev` 或无关分支（如原型分支）打包。
- rebase 后 jj 不会同步子模块，打包前执行 `git submodule update --init --recursive`，并确认 `git submodule status` 没有 `-`、`+` 开头的行。
- 构建命令与 CI 一致：`./gradlew TMessagesProj:assembleRelease --build-cache --no-configuration-cache`，签名来自 `local.properties` 与 `TMessagesProj/release.keystore`。
- 产物在 `TMessagesProj/build/outputs/apk/release/`，交付前用 `apksigner verify --print-certs` 校验签名，并在交付说明里写明基于的 `dev` 提交。

## 更新日志

- 每个 release 的更新日志是 `docs/releases/<version code>.md`（中文）和可选的 `docs/releases/<version code>.en.md`（英文），release workflow 原样把它们发到频道帖的「更新日志 | Changelog」引用块里，英文折叠在引用块末尾。
- 只写相对上一个 release 的变化，不写开头的说明段落（测试情况、版本来源等）。
- 一级分类按顺序用 `## 新增`、`## 改善`、`## 修复`，没有内容的分类省略：
  - 新增：以前没有的功能或设置。
  - 改善：对已有功能或逻辑的调整、改进，包括默认值变化、预览、构建方式等。
  - 修复：问题修复。
- 一个分类下条目较多时，用 `### <分组>`（如「群组管理」「设置与界面」「隐私与安全」）分组；频道帖里分类显示为加粗标签，分组显示为普通标签。
- 一条只描述一个动作；同一功能的多处改动、多个问题拆成多条，不用分号或「同时」把几件事写进一条，也不加解释性的尾句。
- 新增的设置写成 `<u>设置-Nagram-<页面>[-<子页>]</u> <设置名>`，路径以 app 里的实际页面为准；不确定路径时写成 `「<功能>」 | <描述>`。
- 修复写成 `「<功能>」 | <现象>的问题`，描述用户看到的现象，不写实现细节；没有明确的功能名时直接写现象。
- 每组最后一条以「。」结尾，其余以「；」结尾。
- 英文文件与中文逐节、逐组、逐条对应：`## New`、`## Improvements`、`## Fixes`，修复条目写成现象的名词短语，结尾用 `;` 和 `.`。
