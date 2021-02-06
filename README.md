# NekoX

NekoX is an **free and open source** third-party Telegram client, based on Telegram-FOSS with features added.

- [Google Play Store](https://play.google.com/store/apps/details?id=nekox.messenger)
- [Update News Telegram](https://t.me/NekogramX)
- [GitHub Feedback](https://github.com/NekoX-Dev/NekoX/issues)
- [Group Chat (English / Chinese)](https://t.me/NekoXChat) 
- [Group Chat (Persian)](https://t.me/NekogramX_Persian)
- [Group Chat (Indonesia)](https://t.me/NekoxID)

## NekoX Changes

- Most of Nekogram's features
- Max account limit set to 32
- OpenCC Chinese Convert
- Built-in Vmess, Shadowsocks, SSR proxies support
- Built-in public proxy list / Proxy subscription support.
- Proxies import and export, remarks, speed measurement, sorting, delete unusable nodes, etc.
- Scan the qrcode (any link, can add a proxy).
- The ( vemss / vmess1 / ss / ssr / rb ) proxy link in the message can be clicked.
- Allow auto disable proxy when VPN is enabled
- Proxy automatic switcher
- Add stickers without sticker pack
- Allow disable vibration
- Allow clicking on links in self profile
- Sticker set list backup / restore / share
- Full InstantView translation support
- Translation support for selected text on input and in messages
- Delete all messages in group
- Dialog sorting is optional "Unread and can be prioritized for reminding" etc.
- Allow to skip "regret within five seconds"
- Unblock all users support
- Login via qrcode
- Scan and confirm the login qrcode directly
- Allow clear application data
- Option to not send comment first when forwarding
- 0ption to use nekox chat input menu: replace record button with a menu which contains an switch to control link preview (enabled by default)
- Option to disable link preview by default: to prevent the server from knowing that the link is shared through Telegram.
- Option to ignore Android-only content restrictions (except for the Play Store version).
- OpenKaychain client ( sign / verify / decrypt / import )
- Google Cloud Translate / Yandex.Translate support
- Custom cache directory (supports external storage)
- Custom AppId and Hash (optional NekoX / Andorid / Android X or Manual input)
- Custom server (official, test DC or Manual input)
- Keep the original file name when downloading files
- View the data center you belong to when you don't have an avatar
- Proxies, groups, channels, sticker packs are able to shared as qrcodes.
- Force English emoji keywords to be loaded
- Add "@Name" when long press @ user option
- Enhanced notification service, optional version without Google Services.
- Don't alert "Proxy unavailable" for non-current account
- Option to block others from starting a secret chat with you
- Allow create group without invite
- Option to upgrade group to supergroup
- Mark dialogs as read using tab menu
- Option to hide device info
- Improved session dialog
- Improved link long click menu
- Text replacer
- Option to disable trending
- Tgx style message unpin menu
- Built-in Material Design themes / Telegram X style icons

## Compilation Guide

**NOTE: Building on Windows is, unfortunately, not supported.
Consider using a Linux VM or dual booting.**

**Important:**

1. Install Android Sdk and NDK ( default location is $HOME/Android/Sdk, otherwise you need to specify $ANDROID_HOME for it )

It is recommended to use [AndroidStudio](https://developer.android.com/studio) to install.  [here is how to install AndroidStudio](https://developer.android.com/studio/install).

2. Install golang ( >= 1.15.5 ).
```shell
apt install -y golang
```

3. Install rust and its stdlib for android abis, add environment variables for it.

It is recommended to use the official script, otherwise you may not find rustup.

```shell
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- --default-toolchain none -y
echo "source \$HOME/.cargo/env" >> $HOME/.bashrc && source $HOME/.cargo/env

cd ss-rust/src/main/rust/shadowsocks-rust
rustup target install armv7-linux-androideabi aarch64-linux-android i686-linux-android x86_64-linux-android
```

4. Build native dependencies: `./run init libs`
5. Build external libraries and native code: `./run libs update`
6. Fill out `TELEGRAM_APP_ID` and `TELEGRAM_APP_HASH` in `local.properties`
7. Replace TMessagesProj/google-services.json if you want fcm to work.
8. Replace release.keystore with yours and fill out `ALIAS_NAME`, `KEYSTORE_PASS` and `ALIAS_PASS` in `local.properties` if you want a non-debug build.

`./gradlew assemble<Full/Mini><Debug/Release/ReleaseNoGcm>`

## FAQ

#### What is the relationship between NekoX and Nekogram?

More features, **without** [additional trackers](https://gitlab.com/search?utf8=%E2%9C%93&snippets=false&scope=&repository_ref=master&search=AnalyticsHelper&group_id=10273976&project_id=22804922).

#### What is the difference between Full and Mini version?

The full version comes with built-in proxy support for v2ray, shadowsocks, and shadowsocksr, which is usually provided to advanced users to help friends who have no computer knowledge in mainland China to bypass censorship. Don't complain about imperfect functions or ask to add other rare proxy types, you can use their clients directly.

#### What if I don't need a proxy?

Then it is recommended to use the `Mini` version.

#### I've encountered a bug!

First, make sure you have the latest version installed (check the channel, play store updates usually have a delay).

Then, if the issue appears on the official telegram as well, please submit it to the officials, ( but be careful not to show NekoX in the description and screenshots, the official developers don't like us! ).

Then, please *detail* your issue, create an issue or submit it to our [group](https://t.me/NekoXChat) with #bug.

If you experience a *crash*, you also need to click on the version number at the bottom of the settings and select "Enable Log" and send it to us.

#### How can I help with Localization?

Join the project at https://nekox.crowdin.com/nekox.

##### My language is not in the translation platform / needs to be improved !

Same as submitting a bug, plus you need to submit your nekox.crowdin.com account.

## Credits

<ul>
    <li>Telegram-FOSS: <a href="https://github.com/Telegram-FOSS-Team/Telegram-FOSS/blob/master/LICENSE">GPLv2</a></li>
    <li>Nekogram: <a href="https://gitlab.com/Nekogram/Nekogram/-/blob/master/LICENSE">GPLv2</a></li>
    <li>v2rayNG: <a href="https://github.com/2dust/v2rayNG/blob/master/LICENSE">GPLv3</a></li>
    <li>AndroidLibV2rayLite: <a href="https://github.com/2dust/AndroidLibV2rayLite/blob/master/LICENSE">LGPLv3</a></li>
    <li>shadowsocks-android: <a href="https://github.com/shadowsocks/shadowsocks-android/blob/master/LICENSE">GPLv3</a></li>
    <li>shadowsocksRb-android: <a href="https://github.com/shadowsocksRb/shadowsocksRb-android/blob/master/LICENSE">GPLv3</a></li>
    <li>HanLP: <a href="https://github.com/hankcs/HanLP/blob/1.x/LICENSE">Apache License 2.0</a></li>
    <li>OpenCC: <a href="https://github.com/BYVoid/OpenCC/blob/master/LICENSE">Apache License 2.0</a></li>
    <li>opencc-data: <a href="https://github.com/nk2028/opencc-data">Apache License 2.0</a></li>
</ul>
