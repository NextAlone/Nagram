> Yeah, well... I'm gonna go build my own theme park, with blackjack and hookers.

(c) Bender Bending Rodríguez

![Telegraher](/TMessagesProj/src/main/res/mipmap-xhdpi/ic_launcher_sa.png)

## Telegraher

* **No one gets to decide** what i run on my device
* **No one gets to decide** where i run my app
* **No one gets to decide** what must be deleted

This is my device so i control it 😎

This app have nothing with privacy, it's remotely controlled. It's pissing me off, so i changed
that.

I took an original Telegram client from ["official" repo](https://github.com/DrKLO/Telegram) and
made my own theme park with blackjack and hoookers.

Special thanks:

* my wife and my dog, love them 🍑
* mr Rodríguez for the inspiration
* some anonymous folks over the telegram for the great ideas (can't share their names here, cause
  they are anonymous)
* "Telegram🦄magic🦄team" for their "magic🦄updates" including ~~private~~ chats and "magic🦄ads"

### WTF?! / is it legit?

Follow the ~~white rabbit~~ the git flow:

* i took and forked the original client
* i cloned the latest `master` branch (with 8.3.1 patch) into `master_8.3.1`
* i made another branch `noshit_8.3.1` from `master_8.3.1`, it contain changes

It gives us `telegram` -> `master` -> `master_8.3.1` -> `noshit_8.3.1`

So **all the code changes** are in `noshit_8.3.1` (when this project started, actual version is
different)

### Detailed summary / noshit_8.4.3

* DISABLED ADS
    * YES!!1
    * no more sponsored messages, we still download them but do not display
        * we still count views for this ADS to hide our behavior of the app "who don't earn money
          for TG"
* EVERY element have `save to downloads`/`save to gallery`
    * ~~messages are elements too, you still can click but cannot save them into downloads~~ fixed
        * ~~however i do not recomment this~~
    * use it wisely
* 6 accounts instead of 3
    * client support upto 6 accounts
        * ~~on the 1st run when you type phone number and continue it can throw an API error~~
            * error was only on the version with 16 accounts
                * to get more accounts w/o having problems with TGs api need to change the App's
                  flow
            * in case of an error close the error by clicking OK and let it relax for 5 minutes
            * continue and log in
* DISABLED REMOTE DELETIONS
    * NO more deletions via GCM PUSHES (chats and messages), WTF 💩
    * NO more deletions in groups and channels
        * i hate the channels that wipes the content
    * NO more remote deletions in private chats
    * NO more remote deletions in secret chats
        * self destruction timer doesn't work but other folks see that you're opened and deleted it
    * NO more "history wipe" or chat deletions
        * history/messages remains where they are
        * chat becomes inactive if other folks are deleted it for them and/or for you
* FULL ACCESS in "restrict saving content" chats
    * screenshots, gif imports, media saving
    * DO NOT save GIFS via saving gifs
        * just click on them and choose "save to gallery"
        * after this share this media no matter where as a video file **WITHOUT** sound
        * it will become a GIF in your collection
    * you **can't forward** message as is due it use **telegram API** and their server will block
      it, so save/copy
        * save GIF / forward message ARE using telegrams API, while SHARING or DOWNLOADING not
* FULL ACCESS in secret chats (GREEN ONES!)
    * you can download any medias and documents
    * you can take screenshots or record your screen
        * WTF apple can do that, we can do it too!
* GIFs have controls
    * you can start/stop GIFs or navigate on a timeline
* KEEP CACHED chats
    * cached chats are always with you even if you're BANNED (
        * once banned you will get a message about this
        * you can navigate in chat where you was banned using your cache
        * to remove cached chat you must delete it ("leave that chat")
    * even when when you restart your app it will load cached chats
* HISTORY in private chats
    * message separated inside by RFC1123 timestamp field
    * when someone will send you a PM and will change it, while you in chat you will see `edited` as
      usual
        * to see changes you need to **close/open** this **chat**
            * this will be probably fixed in future (display in real time changes)
    * this also affect bots, so when your bots edit their messages, you will see old/new versions
* NO MORE timer
    * when someone send you a media with a timer in a secret (green) chat this message will be
      deleted on a device who sent it once you open it
    * when someone send you a media with a timer in a private (NON-green) chat this message will be
      deleted on a device who sent it once OR twice you open it
        * EASY AND SHORT: open photo twice and it will WIPE it from a device who sent it ;-)
        * LONG AND DETAILED: due it will send an event only if full file is downloaded by your
          client when you open this (guess due file size). In most of cases client download media
          file during 1st open, so you need to open it again (just tap on it)
        * sometimes full media downloaded when preview generated, in such case open once file and it
          will wipe it from a user who sent it
* NO MORE `edit_hide`
    * telegram can send you messages with `edit_hide==true` to hide what this message has been
      edited
    * now if message contains signs of editions it will marked
* DISABLED emulator detections
    * idk why the client use this, but i disabled it
    * if i want to run it on emulator telegram no need to know it
* LEGIT Phone
    * for the app and TG you have simcard, sim is online, phone is actual
        * app don't check it anymore, it's disabled
* Hi, i'm Vanilla 💅
    * we use actual sha256 fingerprint from vanilla version 8.4.3
    * we say we're `org.telegram.messenger`
    * we say Google installed us `com.android.vending`
* APP name changed & APP icon changed & APP package changed
    * of you want to run using old name just rollback those commits and build the app
* APPs api & hash are changed for legit TG client
    * actually they are all on `4`/`014b35b6184100b085b0d0572f9b5103`

### Build

It's very simple

* download the repo `git clone --recurse-submodules git@github.com:nikitasius/Telegram.git`
* build it
    * you can use official guide `https://core.telegram.org/reproducible-builds`
        * open the folder with the repo
        * git checkout lastest **noshit** branch (`git checkout remotes/origin/noshit_8.4.2` for
          example)
        * run `docker build -t telegram-build .`
        * run `docker run --rm -v "$PWD":/home/source telegram-build`
            * and ~1h later you will get 9 different builds (under deb11 with 12 cores and 16Gb Ram
              on NVMe)
    * you can download Android studio `https://developer.android.com/studio`
        * add a bit more ram (i use 4096M for the studio)
        * open the project
        * let gradle it work
        * when it's done go to Build -> Select build variant
            * choose build you need
                * afatDebug - it's debug builds
                * ***Release - release builds

### APKs & sha256

* **sdk23** mean for android 4.2+, the other are working from 6+
* arm64-v8a (new devices)
    * `35064dd3222a05e0876d36d66a91b948141d887851c9bdf437680076bdf26ea0`  [Telegraher.8.4.3.arm64-v8a.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release2/Telegraher.8.4.3.arm64-v8a.apk)
    * `a835691be431264f16b24558b7999f8bc8ac01fa6d881e0e769dcef59ae336d6`  [Telegraher.8.4.3.arm64-v8a-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release2/Telegraher.8.4.3.arm64-v8a-sdk23.apk)
* armeabi-v7a (old devices)
    * `b8a5e7d1dbb60d8de9a0f4503956f16a5900781d42cb64e712ed17a4463a01e3`  [Telegraher.8.4.3.armeabi-v7a.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release2/Telegraher.8.4.3.armeabi-v7a.apk)
    * `9b277e3a50c4e34ce997c4b0d49766f766a9f119a5e6864f1001915df7789794`  [Telegraher.8.4.3.armeabi-v7a-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release2/Telegraher.8.4.3.armeabi-v7a-sdk23.apk)
* PC x86, 32 bits (for an emulator for example)
    * `35bec3a4593506ee8a9d5f6fa161002ff5ac65096513c53cb5baa4d448598e6d`  [Telegraher.8.4.3.x86.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release2/Telegraher.8.4.3.x86.apk)
    * `3a31b339c47c9bbe4e8d96f12f71f4e12635cc530b21d633181ed6499c3b74fd`  [Telegraher.8.4.3.x86-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release2/Telegraher.8.4.3.x86-sdk23.apk)
* PC x86, 64 bits (for 64 bits CPU)
    * `70329341262e930473f6c01cf15a07a2794898523da1cfd86589a332a8876ed4`  [Telegraher.8.4.3.x86-64.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release2/Telegraher.8.4.3.x86-64.apk)
    * `22b31220301bcdce52fd0497ec8edda5d6229f8f51d73f71cdcca9cfad267824`  [Telegraher.8.4.3.x86-64-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release2/Telegraher.8.4.3.x86-64-sdk23.apk)

### Issues/Wishlist

Feel free to use the "issues section". I'm not an Android programmer, i'm a Java developper.
Probably it's a good thing 😃

### Changes

* noshit_8.4.3_release2
    * we use now fingerprint, package name and referer (who installed us, i.e. Google Play) from a
      vanilla version
    * APP lost few permission due it not need it anymore
        * app do not check number, sim state or is number is the actual you use on
          regitration/login, due we just say "yes"/`true`
    * due app is from github official "check update" disabled, so app will not ask TG servers if
      there are new one.
    * api keys are `4`/`014b35b6184100b085b0d0572f9b5103` due gplay/store/web versions are use them
* noshit_8.4.3_release1
    * update to 8.4.3
    * disabled access to all reactions
        * since TG again moderate/censor your private (non-green) chats you can't use them anymore,
          because server simply ignores it and reject w/ an error.
* noshit_8.4.2_release3
    * now the ads is **loaded**, views are **counted** but **the ads isn't displayed**
* noshit_8.4.2_release2
    * fixed issue #4
    * fixed save menu buttons
    * disabled auto reaction on doubletap
    * fixed `edit_hide`
    * all **official** reaction are available for private messages
        * doesn't work in groups/channels due TG servers are using whitelists
* noshit_8.4.2_release1
    * use Telegram 8.4.2 code base now
    * added video controls to GIFs
* noshit_8.3.1_release2
    * Fixed issues #1 and #2

### Already installed this version?

Android will offer you to reinstall, simply accept this option and it the app will be reinstalled
and it will keep all the settings/accounts.

### Code mirrors

* Github: https://github.com/nikitasius/Telegraher
* Gitlab: https://gitlab.com/nikitasius/Telegraher
    * autosync from github
* HTTPS: https://git.evildayz.com/Telegraher/
    * manually sync (add a script later 😀)
    * `releases` w/ actual releases and cloned `Telegraher` & `Telegraher.git` in `.tar.gz`

### Coffee

* Here is my [PayPal](https://paypal.me/nikitasius) `https://paypal.me/nikitasius`
* Here is
  my [BTC](bitcoin:bc1q5egmj6vjejmsu4lu3nmdshvx6p0kcajlw5u9a0?message=github_telegraher) `bc1q5egmj6vjejmsu4lu3nmdshvx6p0kcajlw5u9a0`
* Here is
  my [Yoomoney](https://yoomoney.ru/to/410015481871381) `https://yoomoney.ru/to/410015481871381`

> In fact, forget the park!