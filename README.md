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

MTProto protocol manuals: <https://core.telegram.org/mtproto>

## Compilation Guide

**NOTE: For Windows users, please consider using a Linux VM (such as WSL2) or dual booting.**

Environment:

- A Linux distribution based on Debian (e.g. Ubuntu)

- Native tools: `gcc` `go` `make` `cmake` `ninja` `yasm`
  
  ```shell
  sudo apt install gcc golang make cmake ninja-build yasm
  ```
- Android SDK: `build-tools;33.0.0` `platforms;android-33` `ndk;21.4.7075529` `cmake;3.18.1` (the default location is **$HOME/Android/SDK**, otherwise you need to specify **$ANDROID_HOME** for it)

  It is recommended to use [Android Studio](https://developer.android.com/studio) to install, but you can also use `sdkmanager`:

  ```shell
  sudo apt install sdkmanager
  sdkmanager --sdk_root $HOME/Android/SDK --install "build-tools;33.0.0" "platforms;android-33" "ndk;21.4.7075529" "cmake;3.18.1"
  ```

Build: 

1. Checkout submodules

   ```shell
   git submodule update --init --recursive
   ```

2. Build native dependencies:
   ```shell
   ./run init libs
   ```

3. Build external libraries and native code: 
   ```shell
   ./run libs native
   ```

4. Fill out `TELEGRAM_APP_ID` and `TELEGRAM_APP_HASH` in **local.properties** (from [Telegram Developer](https://my.telegram.org/auth))

5. Replace **TMessagesProj/google-services.json** if you want FCM to work.

6. Replace **release.keystore** with yours and fill out `ALIAS_NAME`, `KEYSTORE_PASS` and `ALIAS_PASS` in **local.properties**.

7. Build with Gradle:

   ```shell
   ./gradlew assembleMini<Debug/Release/ReleaseNoGcm>
   ```

----

## Compilation with GitHub Action

1. Create your own `release.keystore` to replace `TMessagesProj/release.keystore`.

2. Prepare LOCAL_PROPERTIES

- KEYSTORE_PASS: from your keystore
- ALIAS_NAME: from your keystore
- ALIAS_PASS: from your keystore
- TELEGRAM_APP_ID: from [Telegram Developer](https://my.telegram.org/auth)
- TELEGRAM_APP_HASH: from [Telegram Developer](https://my.telegram.org/auth)

```env
KEYSTORE_PASS=123456
ALIAS_NAME=key0
ALIAS_PASS=123456
TELEGRAM_APP_ID=123456
TELEGRAM_APP_HASH=abcdefg
```

Then, use base64 to encode the above.

3. Add Repo Action Secrets

- LOCAL_PROPERTIES: from step 2
- HELPER_BOT_TOKEN: from telegram [@Botfather](https://t.me/Botfather), such as `1111:abcd`
- HELPER_BOT_TARGET: from telegram chat id, such as `777000`

4. Run Release Build

## FAQ

#### What is the differences between Nagram, NekoX and Nekogram?

Developed by different developers, read the feature list above to understand the differences.

#### What is the noGcm version?

Google Cloud Messaging, also known as gcm / fcm, message push service by google used by original Telegram android app, it requires your device to have Google Service Framework (non-free) installed.

#### I've encountered a bug!

First, make sure you have the latest version installed (check the channel).

Then, if the issue appears in the official Telegram client too, please submit it to the officials, (be careful not to show NekoX in the description and screenshots, the official developers doesn't like us!).

Then, submit it to our [group](https://t.me/nagram_group) with #bug.

If you experience a *crash*, you also need to click on the version number at the bottom of the settings and select "Enable Log" and send it to us.

## Localization

Nagram is forked from Telegram, thus most locales follows the translations of Telegram for Android, checkout <https://translations.telegram.org/en/android/>.

Is Nagram not in your language, or the translation is incorrect or incomplete? Get involved in the translations on our [Weblate](https://xtaolabs.crowdin.com/nagram).

[![Crowdin](https://badges.crowdin.net/e/156df3a631d257cc6b57301566d545fb/localized.svg)](https://xtaolabs.crowdin.com/nagram)

## Thanks

- [NekoX](https://github.com/NekoX-Dev/NekoX)
- [Nekogram](https://gitlab.com/Nekogram/Nekogram)
- [Pigeongram](https://gitlab.com/JasonKhew96/Nekogram)
- [Nullgram](https://github.com/qwq233/Nullgram)
- [TeleTux](https://github.com/TeleTux/TeleTux)
- [OwlGram](https://github.com/OwlGramDev/OwlGram)

