# NekoX

NekoX is an open source third-party Telegram android app.

- Google play store: (https://play.google.com/store/apps/details?id=nekox.messenger)
- Update news : https://t.me/NekoX-Dev
- Feedback: https://github.com/NekoX-Dev/NekoX/issues
- Chat Group: https://t.me/NekoXChat
- Chat Group (Persian) : https://t.me/NekogramX_Persian
- FAQ: https://telegra.ph/NekoX-FAQ-03-31
- FAQ (Chinese): https://telegra.ph/NekoX-%E5%B8%B8%E8%A6%8B%E5%95%8F%E9%A1%8C-03-31

## Telegram-FOSS Changes:

*Replacement of non-FOSS, untrustworthy or suspicious binaries or source code:*
- Do location sharing with OpenStreetMap(osmdroid) instead of Google Maps
- Use Twemoji emoji set instead of Apple's emoji
- Google Play Services GCM replaced with Telegram's push service
- Has to show a notification on Oreo+, ask Google
- **SECURITY:** Old BoringSSL prebuilts are replaced with the newest upstream source code built at compile time
- **SECURITY:** Old FFmpeg prebuilts are replaced with the newest upstream source code built at compile time
- **SECURITY:** Bundled libWebP is updated

*Removal of non-FOSS, untrustworthy or suspicious binaries or source code and their functionality:*
- Google Vision face detection and barcode scanning (Passport)
- Google Wallet and Android Pay integration
- Google Voice integration
- HockeyApp crash reporting and self-updates
- Google SMS retrieval. You have to type the code manually

*Other:*
- Allow to set a proxy before login
- Added the ability to parse locations from intents containing a `geo:<lat>,<lon>,<zoom>` string
- Force static map previews from Telegram

## Old 0penSource Nekogram Changes

We regret that [Nekogram](https://github.com/Nekogram/Nekogram-issue-tracker) is no longer open source, but uses drklo/master to [disguise open source](https://github.com/Nekogram/Android).

- Repeat others' message in one click.
- Save to saved messages in one click.
- Ignore messages from blocked users.
- Forward messages without quoting.
- Create a mention by user's ID.
- Allow non-admin users to view group chat permissions and administrators.
- Select a map preview provider for normal dialogs.
- Promote/restrict user directly from contextual menu clicking on user's message.
- Show user chat history in groups from contextual menu clicking on user's message.
- Delete single downloaded file.
- Customize stickers display size.
- Show and export message details.
- Unlimited favorite stickers.
- Filter chats list: users, groups, channels, bots, admin, unmuted.
- Multi-accounts (up to 8).
- Log in with bot accounts.
- Decide whether to sync contacts on first login.
- Show ID and data center.
- Transparent status bar.
- Change displaying name order.
- Hide mobile number from navigation menu drawer and settings menu.
- Built-in Chinese and Japanese language.
- Use system font and emojis.
- Store cache into app's private directory.
- Hide proxy sponsor channels.
- Toogle to show sensitive media contents in public channels.

## NekoX Changes

- Built-in Vmess, Shadowsocks, SSR, <del>RelayBaton (ESNI)</del> proxies support
- Built-in public proxy list
- Proxy subscription support.
- Proxies import and export, remarks, speed measurement, sorting, delete unusable nodes, etc.
- Scan the qrcode (any link, can add a proxy).
- The ( vemss / vmess1 / ss / ssr / rb ) proxy link in the message can be clicked.
- Allow auto disable proxy when VPN is enabled
- Proxy automatic switcher
- Add stickers without sticker pack
- Allow disable vibration
- Sticker set list backup / restore / share
- Full InstantView translation support
- Translation support for selected text on input and in messages
- Delete all messages in group
- Dialog sorting is optional "Unread and can be prioritized for reminding" etc.
- Allow to skip "regret within five seconds"
- Unblock all users support
- OpenKaychain client ( sign / verify / decrypt / import )
- Google Cloud Translate / Yandex.Translate support
- Custom cache directory (supports external storage)
- Custom AppId and Hash (optional NekoX / Andorid / Android X or Manual input)
- Custom server (official, test DC or Manual input)
- Keep the original file name when downloading files
- View the data center you belong to when you don't have an avatar
- Proxies, groups, channels, sticker packs are able to shared as QRCodes.
- Force English emoji keywords to be loaded
- Add "@Name" when long press @ user option
- Enhanced notification service, optional version without Google Services.
- Built-in Material Design themes / Telegram X style icons

## How to get Google Cloud Translate Key

https://telegra.ph/google-cloud-trans-key-04-26

## Compilation Guide

**NOTE: Building on Windows is, unfortunately, not supported.
Consider using a Linux VM or dual booting.**

**Important:**

1. Install Android Sdk and NDK ( default location is $HOME/Android/Sdk, otherwise you need to specify $ANDROID_HOME for it )

It is recommended to use AndroidStudio to install.

2. Install golang ( >= 1.15 ), and add GOPATH to the PATH variable.

It is recommended to use gvm for the installation. if you are using the system package manager, don't forget to add environment variable.

```shell
apt install -y bison gcc make
bash < <(curl -s -S -L https://raw.githubusercontent.com/moovweb/gvm/master/binscripts/gvm-installer)
source "$HOME/.bashrc"

gvm install go1.15 -B
gvm use go1.15 --default
```

3. Install rust and its stdlib for android abis, add environment variables for it.

It is recommended to use the official script, otherwise you may not find rustup.

```shell
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
echo "source \$HOME/.cargo/env" >> $HOME/.bashrc && source $HOME/.cargo/env
rustup target install armv7-linux-androideabi aarch64-linux-android i686-linux-android x86_64-linux-android
```

(One command at a time)

4. Build native dependencies: `bin/native_libs.sh`
5. Build external libraries and native code: `bin/update_libs.sh`

### Specify APP_ID and APP_HASH

Fill out TELEGRAM_APP_ID and TELEGRAM_APP_HASH in local.properties

### Build Types

#### Debug

`./gradlew assemble<Variant>Debug`

The default debug key is used, and placing yours is not needed.

#### Release

`./gradlew assemble<Variant>Release`

The difference between release and other build types is that it adds fcm and firebase crash analysis, if you don't like them, use releaseNoGcm.

To compile the release version, please place your keysotre at TMessageProj/release.keystore, and fill in KEYSTORE_PASS, ALIAS_NAME, ALIAS_PASS in local.properties, environment variables are also recommended

If you don't use NekoX's APP_ID and APP_HASH, you need to register a physical firebase app and replace google-services.json to ensure fcm works

#### Foss

`./gradlew assemble<Variant>Foss`

OK, a version without firebase cloud messaging and precompiled native libraries, maybe this makes you feel more free, or your phone does not have Google services.

To compile the foss version, please refer to [scripts](./bin).

### Build Variants

Available variant list:

`Full`
`Mini` ( without ss/ssr/v2ray proxies )  
  
`AppleEmoji`  
`TwitterEmoji`
`NoEmoji`

## Localization

Join project at https://nekox.crowdin.com/nekox.

## Credits

<ul>
    <li>Telegram-FOSS: <a href="https://github.com/Telegram-FOSS-Team/Telegram-FOSS/blob/master/LICENSE">GPLv2</a></li>
    <li><del>Nekogram: <a href="https://github.com/Nekogram/Nekogram/blob/master/LICENSE">GPLv2</a></del> (No longer open source)</li>
    <li>v2rayNG: <a href="https://github.com/2dust/v2rayNG/blob/master/LICENSE">GPLv3</a></li>
    <li>AndroidLibV2rayLite: <a href="https://github.com/2dust/AndroidLibV2rayLite/blob/master/LICENSE">LGPLv3</a></li>
    <li>shadowsocks-android: <a href="https://github.com/shadowsocks/shadowsocks-android/blob/master/LICENSE">GPLv3</a></li>
    <li>shadowsocksRb-android: <a href="https://github.com/shadowsocksRb/shadowsocksRb-android/blob/master/LICENSE">GPLv3</a></li>
</ul>