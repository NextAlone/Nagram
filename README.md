# NekoX
![Logo](https://raw.githubusercontent.com/NekoX-Dev/NekoX/master/TMessagesProj/src/main/res/mipmap-xxxhdpi/ic_launcher.png)  

NekoX-Dev is an UNOFFICIAL app that uses Telegram's API.

- Google play store: (https://play.google.com/store/apps/details?id=nekox.messenger)
- Update news : https://t.me/NekoX-Dev
- Feedback: https://t.me/NekoXChat
- Feedback (Persian) : https://t.me/NekogramX_Persian
- Feedback: https://github.com/NekoX-Dev/NekoX/issues

## API, Protocol documentation

Telegram API manuals: https://core.telegram.org/api

MTProto protocol manuals: https://core.telegram.org/mtproto

## Compilation Guide

### Specify APP_ID and APP_HASH

Just fill out TELEGRAM_APP_ID and TELEGRAM_APP_HASH in local.properties

### Build Types

#### Debug

`./gradlew assemble<Variant>Debug`

The default debug key is used, and placing yours is not needed.

#### Release

`./gradlew assemble<Variant>Release`

The difference between release and other build types is that it adds fcm and firebase crash analysis, if you don't like them, use releaseNoGcm.

To compile the release version, please place your keysotre at TMessageProj/release.jks, and fill in KEYSTORE_PASS, ALIAS_NAME, ALIAS_PASS in local.properties, environment variables are also recommended

If you don't use NekoX's APP_ID and APP_HASH, you need to register a physical firebase app and replace google-services.json to ensure fcm works

#### Foss

`./gradlew assemble<Variant>Foss`

OK, a version without firebase cloud messaging and precompiled native libraries, maybe this makes you feel more free, or your phone does not have Google services.

To compile the foss version, please refer to [this script](.github/workflows/foss.yml).

### Build Variants

Available variant list:

`Afat`, `MinApi21`


## Localization

NekoX is forked from Nekogram-FOSS, thus most locales follows the translations of Telegram for Android, checkout https://translations.telegram.org/en/android/.

As for the specialized strings, we use Crowdin to translate Nekogram. Join project at https://neko.crowdin.com/nekogram and https://nekox.crowdin.com/nekox. Help us bring Nekogram to the world!

## Credits

<ul>
    <li>Nekogram: <a href="https://github.com/Nekogram/Nekogram/blob/master/LICENSE">GPLv2</a></li>
    <li>Telegram-FOSS: <a href="https://github.com/Telegram-FOSS-Team/Telegram-FOSS/blob/master/LICENSE">GPLv2</a></li>
    <li>v2rayNG: <a href="https://github.com/2dust/v2rayNG/blob/master/LICENSE">GPLv3</a></li>
    <li>AndroidLibV2rayLite: <a href="https://github.com/2dust/AndroidLibV2rayLite/blob/master/LICENSE">LGPLv3</a></li>
    <li>shadowsocks-libev: <a href="https://github.com/shadowsocks/shadowsocks-libev/blob/master/LICENSE">GPLv3</a></li>
    <li>shadowsocksRb-android: <a href="https://github.com/shadowsocksRb/shadowsocksRb-android/blob/master/LICENSE">GPLv3</a></li>
</ul>

## Contributors

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
| [<img src="https://avatars3.githubusercontent.com/u/56506714?s=460&v=4" width="80px;"/><br /><sub>世界</sub>](https://github.com/nekohasekai)<br />[💻](https://github.com/NekoX-Dev/NekoX/commits?author=nekohasekai "Code") | [<img src="https://avatars2.githubusercontent.com/u/42698724?s=460&v=4" width="80px;"/><br /><sub>猫耳逆变器</sub>](https://github.com/NekoInverter)<br />[💻](https://github.com/NekoX-Dev/NekoX/commits?author=NekoInverter "Code") | [<img src="https://avatars1.githubusercontent.com/u/18373361?s=460&v=4" width="80px;"/><br /><sub>梨子</sub>](https://github.com/rikakomoe)<br />[💻](https://github.com/NekoX-Dev/NekoX/commits?author=rikakomoe "Code") | [<img src="https://i.loli.net/2020/01/17/e9Z5zkG7lNwUBPE.jpg" width="80px;"/><br /><sub>呆瓜</sub>](https://t.me/Duang)<br /> [🎨](#design-duang "Design") |
| :---: | :---: | :---: | :---: |
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/kentcdodds/all-contributors) specification. Contributions of any kind welcome!

## :)

[关于 "NekoX 举报 Nekogram" 的声明](https://telegra.ph/page-04-12)