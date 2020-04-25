# NekoX

NekoX is an open source third-party Telegram android app.

- Google play store: (https://play.google.com/store/apps/details?id=nekox.messenger)
- Update news : https://t.me/NekoX-Dev
- Feedback: https://t.me/NekoXChat
- Feedback (Persian) : https://t.me/NekogramX_Persian
- Feedback: https://github.com/NekoX-Dev/NekoX/issues
- FAQ: https://telegra.ph/NekoX-FAQ-03-31
- FAQ (Chinese): https://telegra.ph/NekoX-%E5%B8%B8%E8%A6%8B%E5%95%8F%E9%A1%8C-03-31

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

`Afat`, ( android 4.1 + )
`MinApi21` ( android 5 + )

## Localization

Join project at https://nekox.crowdin.com/nekox and https://neko.crowdin.com/ .

## Credits

<ul>
    <li>Telegram-FOSS: <a href="https://github.com/Telegram-FOSS-Team/Telegram-FOSS/blob/master/LICENSE">GPLv2</a></li>
    <li>Nekogram: <a href="https://github.com/Nekogram/Nekogram/blob/master/LICENSE">GPLv2</a></li>
    <li>v2rayNG: <a href="https://github.com/2dust/v2rayNG/blob/master/LICENSE">GPLv3</a></li>
    <li>AndroidLibV2rayLite: <a href="https://github.com/2dust/AndroidLibV2rayLite/blob/master/LICENSE">LGPLv3</a></li>
    <li>shadowsocks-libev: <a href="https://github.com/shadowsocks/shadowsocks-libev/blob/master/LICENSE">GPLv3</a></li>
    <li>shadowsocksRb-android: <a href="https://github.com/shadowsocksRb/shadowsocksRb-android/blob/master/LICENSE">GPLv3</a></li>
</ul>