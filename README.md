<img src="https://itsv1eds.ru/icon.png" width="150" align="left"/>

## exteraGram

Experimental Telegram client based on [official Telegram sources](https://github.com/DrKLO/Telegram).

[![Channel](https://img.shields.io/badge/Channel-Telegram-red.svg)](https://t.me/exteragram)
[![Chat](https://img.shields.io/badge/English%20Chat-Telegram-red.svg)](https://t.me/en_exteraChat)
[![Chat](https://img.shields.io/badge/Russian%20Chat-Telegram-red.svg)](https://t.me/exteraChat)


 ## Thanks to:
- [Telegram](https://github.com/DrKLO/Telegram)
- [Catogram](https://github.com/Catogram/Catogram)

## Importing API hash and keys
- You should get **YOUR OWN API KEY AND HASH** here: https://core.telegram.org/api/obtaining_api_id and create a file called `API_KEYS` in the source root directory. Also you should get **YOUR OWN MAPS API KEY** here: https://console.cloud.google.com/google/maps-apis/credentials and add it to this file.
The contents should look like this:
```
APP_ID = 123456
APP_HASH = abcdef0123456789 (32 chars)
MAPS_V2_API = abcdef01234567-abcdef012345678910111213
```
- **exteraGram** can be built with **Android Studio** or from the command line with **Gradle**:
```
./gradlew assembleAfatRelease
```
