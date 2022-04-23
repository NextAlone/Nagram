# Nullgram

[![Telegram](https://img.shields.io/static/v1?label=Telegram&message=@NullgramClient&color=0088cc)](https://t.me/NullgramClient)[![CI build](https://github.com/qwq233/Nullgram/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/qwq233/Nullgram/actions/workflows/debug.yml)[![Crowdin](https://badges.crowdin.net/nullgram/localized.svg)](https://crowdin.com/project/nullgram)

[中文](README_CN.md)

Nullgram is an **free and open source** third-party Telegram client, based on Telegram, [NekoX](https://github.com/NekoX-Dev/NekoX) and [Nekogram](https://gitlab.com/Nekogram/Nekogram).
 the official source code for [Telegram App for Android](https://play.google.com/store/apps/details?id=org.telegram.messenger).

## Why Nullgram
Due to the fragmentation of the NekoX and Nekogram communities, and the serious problems with the main developers of NekoX and Nekogram[^1], I decided to make Nullgram to integrate both of their functions to avoid these problems.

Null used in computer programming for an uninitialized, undefined, empty, or meaningless value.
In the name of Nullgram, the purpose is to express that there is no such bad things. Nullgram won't push FCM-Notification "nmsl"[^2] or somethings like that to your phone, won't send ads[^4] to channels, won't compete maliciously[^5] or publish malicious rumors about competitors

## How to Contribute

### I want to add new feature
Great!

Make sure you fully understand [the Development Document](.github/CONTRIBUTING.md).
If you haven't read it.**THEN GO READ IT.**

Then just create a new pull request and I should be review in a couple of days.

### I've encountered a bug!
First, make sure you have the latest version installed (check the channel, Play store versions usually have a delay).

Then, if the issue appears in the official Telegram client too, please submit it to the officials, (be careful not to show Nullgram in the description and screenshots, the official developers doesn't like us!).

Then, please detail your issue (ENGLISH ONLY), create an issue or submit it to our group with #bug.

Make sure using the issue template and writing the detailed version number I DO NOT KNOW WTF IS *I HAVE BEEN USING THE LATEST VERSION*

If you experience a crash, you can use logcat to catch the log (TAG:  `Nullgram` ).

### Compilation Guide

**Note**: In order to support [reproducible builds](https://core.telegram.org/reproducible-builds), this repo contains dummy release.keystore,  google-services.json and filled variables inside BuildVars.java. Before publishing your own APKs please make sure to replace all these files with your own.

You will require Android Studio 3.4, Android NDK rev. 20 and Android SDK 8.1

1. Download the Telegram source code from https://github.com/qwq233/Nullgram
2. Copy your release.keystore into TMessagesProj/config
3. Fill out RELEASE_KEY_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_STORE_PASSWORD in gradle.properties to access your  release.keystore
4.  Go to https://console.firebase.google.com/, create two android apps with application IDs `top.qwq2333.nullgram` and `top.qwq2333.nullgram.beta`, turn on firebase messaging and download google-services.json, which should be copied to the same folder as TMessagesProj.
5. Open the project in the Studio (note that it should be opened, NOT imported).
6. Fill out values in TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java – there’s a link for each of the variables showing where and which data to obtain.
7. You are ready to compile Telegram.


[^1]: https://telegra.ph/%E6%9C%89%E5%85%B3-Nekogram-Lite-%E7%9A%84%E6%95%85%E4%BA%8B-04-09

[^2]:https://sm.ms/image/FAKi3mx6XwqlvRj

[^3]:https://t.me/NekogramX/418

[^4]:https://t.me/zuragram/392

[^5]:https://t.me/sayingArchive/15428
