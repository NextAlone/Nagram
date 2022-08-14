# Zngram

Zngram is a third-party Telegram client based on [Nagram](https://github.com/NextAlone/Nagram) with some modifications.

- Official Site: <https://gram.xinzer.cafe>
- Telegram Update Channel: <https://t.me/Zngram_offical>
- Releases: <https://github.com/StudentYee/Zngram/releases>
- Issues here: <https://github.com/StudentYee/Zngram/issues>

## Additional feature over NekoX

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

- [Nagram](https://github.com/NextAlone/Nagram)
- [NekoX](https://github.com/NekoX-Dev/NekoX)
- [Nekogram](https://gitlab.com/Nekogram/Nekogram)
- [Pigeongram](https://gitlab.com/JasonKhew96/Nekogram)
- [Nullgram](https://github.com/qwq233/Nullgram)
- [TeleTux](https://github.com/TeleTux/TeleTux)
- [OwlGram](https://github.com/OwlGramDev/OwlGram)

