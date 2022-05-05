<img src="https://i.imgur.com/5EmxevP.png" width="150" align="left"/>

## exteraGram
*[Licensed under the GNU General Public License v2.0](https://github.com/exteraSquad/exteraGram/blob/default/LICENSE)*

Experimental **third-party** Telegram client based on [official sources](https://github.com/DrKLO/Telegram).

[![Channel](https://img.shields.io/badge/Channel-Telegram-blue.svg)](https://t.me/exteragram)
[![Chat](https://img.shields.io/badge/Chat-Telegram-blue.svg)](https://t.me/exteraChat)
[![Downloads](https://img.shields.io/badge/Download%20at%20-%20Telegram-blue.svg)](https://t.me/exterareleases)

## Importing API hash and keys
- You should get **YOUR OWN API KEY AND HASH** here: https://core.telegram.org/api/obtaining_api_id and create a file called `API_KEYS` in the source root directory.
- Also you should get **YOUR OWN MAPS API KEY** here: https://console.cloud.google.com/google/maps-apis/credentials and add it to this file.
- And you need get **COMPUTE YOUR APP's HASH STRING** here: https://developers.google.com/identity/sms-retriever/verify#computing_your_apps_hash_string
The contents should look like this:
```
APP_ID = 123456
APP_HASH = abcdef0123456789 (32 chars)
SMS_HASH = FC+9qCX9VSu (11 chars)
MAPS_V2_API = abcdef01234567-abcdef012345678910111213
```

## Compilation Guide
1. Clone exteraGram's source code using `git clone https://github.com/exteraSquad/exteraGram/`
2. Fill out values in `API_KEYS` like [here](https://github.com/exteraSquad/exteraGram#importing-api-hash-and-keys)
3. Open the project in Android Studio. It should be opened, **not imported**
4. You are ready to compile `exteraGram`

- **exteraGram** can be built with **Android Studio** or from the command line with **Gradle**:
```
./gradlew assembleAfatRelease
```

## exteraGram Localization
Since **exteraGram** is a fork of **Telegram for Android** and most localizations follow translations of **Telegram for Android**, check it out [here](https://translations.telegram.org/en/android/). As for specialized strings for **exteraGram**, we use **Crowdin** to translate **exteraGram**. Join the project at [Crowdin](https://crowdin.com/project/exteralocales)!

 ## Thanks to:
- [Telegram](https://github.com/DrKLO/Telegram)
- [Catogram](https://github.com/Catogram/Catogram) and [Catogram X](https://github.com/CatogramX/CatogramX)
- [Nekogram](https://gitlab.com/Nekogram/Nekogram) and [Nekogram X](https://github.com/NekoX-Dev/NekoX)
- [OwlGram](https://github.com/OwlGramDev/OwlGram)
