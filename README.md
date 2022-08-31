# Nagram

Nagram is a third-party Telegram client based on [NekoX](https://github.com/NekoX-Dev/NekoX) with some modifications.

- Official Site: <https://nextalone.xyz>
- Telegram Update Channel: <https://t.me/nagram_channel>
- Releases: <https://github.com/NextAlone/Nagram/releases>
- Issues here: <https://github.com/NextAlone/Nagram/issues>

## Additional feature over Nagram

1. Nice icon (thanks to MaitungTM)
2. Combine message
3. Editable text style 
4. Forced copy
5. Invert reply
6. Quick reply in longClick menu (thanks to @blxueya)
7. Undo and Redo
8. Scrollable chat preview (thanks to TeleTux)
9. Noise suppress and voice enhance (thanks to )

----

## NekoX Changes

- Most of Nekogram's features
- Unlimited login accounts
- **Proxy**
    - Built-in VMess, Shadowsocks, SSR, Trojan-GFW proxies support (No longer maintained)
    - Built-in public proxy (WebSocket relay via Cloudflare CDN), [documentation and for PC](https://github.com/arm64v8a/NekoXProxy)
    - Proxy subscription support
    - Ipv6 MTProxy support
    - Able to parse all proxy subscription format: SIP008, ssr, v2rayN, vmess1, shit ios app formats, clash config and more
    - Proxies import and export, remarks, speed measurement, sorting, delete unusable nodes, etc
    - Scan the QR code (any link, can add a proxy)
    - The ( vmess / vmess1 / ss / ssr / trojan ) proxy link in the message can be clicked
    - Allow auto-disabling proxy when VPN is enabled
    - Proxy automatic switcher
    - Don't alert "Proxy unavailable" for non-current account
- **Stickers**
    - Custom [Emoji packs](https://github.com/NekoX-Dev/NekoX/wiki/emoji)
    - Add stickers without sticker pack
    - Sticker set list backup / restore / share
- **Internationalization**
    - OpenCC Chinese Convert
    - Full InstantView translation support
    - Translation support for selected text on input and in messages
    - Google Cloud Translate / Yandex.Translate support
    - Force English emoji keywords to be loaded
    - Persian calendar support
- **Additional Options**
    - Option to disable vibration
    - Dialog sorting is optional "Unread and can be prioritized for reminding" etc
    - Option to skip "regret within five seconds"
    - Option to not send comment first when forwarding
    - Option to use nekox chat input menu: replace record button with a menu which contains an switch to control link preview (enabled by default)
    - Option to disable link preview by default: to prevent the server from knowing that the link is shared through Telegram.
    - Option to ignore Android-only content restrictions (except for the Play Store version).
    - Custom cache directory (supports external storage)
    - Custom server (official, test DC)
    - Option to block others from starting a secret chat with you
    - Option to disable trending
- **Additional Actions**
    - Allow clicking on links in self profile
    - Delete all messages in group
    - Unblock all users support
    - Login via QR code
    - Scan and confirm the login QR code directly
    - Allow clearing app data
    - Proxies, groups, channels, sticker packs are able to be shared as QR codes
    - Add "@Name" when long-pressing @user option
    - Allow creating a group without inviting anyone
    - Allow upgrading a group to a supergroup
    - Mark dialogs as read using tab menu
    - Enabled set auto delete timer option for private chats and private groups
    - Support saving multiple selected messages to Saved Messages
    - Support unpinning multiple selected messages
    - View stats option for messages
- **Optimization**
    - Keep the original file name when downloading files
    - View the data center you belong to when you don't have an avatar
    - Enhanced notification service, optional version without Google Services
    - Improved session dialog
    - Improved link long click menu
    - Improved hide messages from blocked users feature
    - Don't process cleanup draft events after opening chat
- **Others**
    - OpenKeychain client (sign / verify / decrypt / import)
    - Text replacer
- **UI**
    - Telegram X style menu for unpinning messages
    - Built-in Material Design themes / Telegram X style icons
- And more :)

## API and Protocol documentation

Telegram API manuals: <https://core.telegram.org/api>

MTproto protocol manuals: <https://core.telegram.org/mtproto>

## Compilation Guide(By NekoX-dev)

**NOTE: Building on Windows is, unfortunately, not supported.
Consider using a Linux VM or dual booting.**

**Important:**

1. Checkout all submodules

```
git submodule update --init --recursive
```

2. Install Android SDK and NDK (default location is $HOME/Android/SDK, otherwise you need to specify $ANDROID_HOME for it)

It is recommended to use [AndroidStudio](https://developer.android.com/studio) to install.

3. Install golang and yasm

```shell
apt install -y golang-1.16 yasm
```

4. Install Rust and its stdlib for Android ABIs, and add environment variables for it.

It is recommended to use the official script, otherwise you may not find rustup.

```shell
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- --default-toolchain none -y
echo "source \$HOME/.cargo/env" >> $HOME/.bashrc && source $HOME/.cargo/env

rustup install $(cat ss-rust/src/main/rust/shadowsocks-rust/rust-toolchain)
rustup default $(cat ss-rust/src/main/rust/shadowsocks-rust/rust-toolchain)
rustup target install armv7-linux-androideabi aarch64-linux-android i686-linux-android x86_64-linux-android
```

This step can be skipped if you want to build a `mini` release.

5. Build native dependencies: `./run init libs`
6. Build external libraries and native code:

For full release:

uncomment lines in settings.gradle  

`./run libs update`

For mini release:

```
./run libs v2ray
./run libs native # libtmessages.so
```

1. Fill out `TELEGRAM_APP_ID` and `TELEGRAM_APP_HASH` in `local.properties`
2. Replace TMessagesProj/google-services.json if you want fcm to work.
3. Replace release.keystore with yours and fill out `ALIAS_NAME`, `KEYSTORE_PASS` and `ALIAS_PASS` in `local.properties` if you want a custom sign key.

`./gradlew assemble<Full/Mini><Debug/Release/ReleaseNoGcm>`

----

## Localization

Nagram is forked from Telegram, thus most locales follows the translations of Telegram for Android, checkout <https://translations.telegram.org/en/android/>.

Is Nagram not in your language, or the translation is incorrect or incomplete? Get involved in the translations on our [Weblate](https://hosted.weblate.org/engage/nekox/).

## Thanks

- [NekoX](https://github.com/NekoX-Dev/NekoX)
- [Nekogram](https://gitlab.com/Nekogram/Nekogram)
- [Pigeongram](https://gitlab.com/JasonKhew96/Nekogram)
- [Nullgram](https://github.com/qwq233/Nullgram)
- [TeleTux](https://github.com/TeleTux/TeleTux)
- [OwlGram](https://github.com/OwlGramDev/OwlGram)

