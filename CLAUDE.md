# Claude Code 说明

## 打包

- 打 release 包（无论是否签名）必须基于最新的 `dev`：先 `jj git fetch --remote origin`，把待打包的 change rebase 到 `dev` 上（`jj rebase -b @ -d dev`），不要基于本地过时的 `dev` 或无关分支（如原型分支）打包。
- rebase 后 jj 不会同步子模块，打包前执行 `git submodule update --init --recursive`，并确认 `git submodule status` 没有 `-`、`+` 开头的行。
- 构建命令与 CI 一致：`./gradlew TMessagesProj:assembleRelease --build-cache --no-configuration-cache`，签名来自 `local.properties` 与 `TMessagesProj/release.keystore`。
- 产物在 `TMessagesProj/build/outputs/apk/release/`，交付前用 `apksigner verify --print-certs` 校验签名，并在交付说明里写明基于的 `dev` 提交。
