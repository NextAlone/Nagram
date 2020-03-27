# Pre-compiled binary libraries.

About foss compilation, please refer to [here](https://github.com/NekogramX/NekoX/blob/master/.github/workflows/foss.yml)

### libv2ray.aar
 ```bash
# bash < <(curl -s -S -L https://raw.githubusercontent.com/moovweb/gvm/master/binscripts/gvm-installer)
# gvm install go1.13
# gvm use go1.13

go get -u github.com/golang/protobuf/protoc-gen-go
go get -v golang.org/x/mobile/cmd/...
go get -v go.starlark.net/starlark
go get -v github.com/refraction-networking/utls
go get -v github.com/gorilla/websocket
go get -v -insecure v2ray.com/core
go get github.com/2dust/AndroidLibV2rayLite

gomobile init
env GO111MODULE=off gomobile bind -v -ldflags='-s -w' github.com/2dust/AndroidLibV2rayLite
```

### ss-libev-release.aar

`./gradlew ss-libev:assembleRelease`

### ssr-libev-release.aar

`./gradlew ssr-libev:assembleRelease`