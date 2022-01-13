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
* APP name changed & APP icon changed & APP package changed
    * of you want to run using old name just rollback those commits and build the app
* APPs api & hash are changed for legit TG client

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
    * `0330b380bda785058f92972ef5489b5078f3212d3f2aeff53d57c525364fc834`  [Telegraher.8.4.3.arm64-v8a.apk](--https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release1/Telegraher.8.4.3.arm64-v8a.apk)
    * `5938058641db2539aaea8a21323f0738af38b6e1f8d8d8fa3bae238f6199294b`  [Telegraher.8.4.3.arm64-v8a-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release1/Telegraher.8.4.3.arm64-v8a-sdk23.apk)
* armeabi-v7a (old devices)
    * `2d475546cf1da4a1c98667bdbe75a43c36702ff8ce941c650c2fa8e20d8843c3`  [Telegraher.8.4.3.armeabi-v7a.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release1/Telegraher.8.4.3.armeabi-v7a.apk)
    * `53fb75ddfe51441a0ec0755aef652cf14083c22bb238e3f9190b24ebe7f1129d`  [Telegraher.8.4.3.armeabi-v7a-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release1/Telegraher.8.4.3.armeabi-v7a-sdk23.apk)
* PC x86, 32 bits (for an emulator for example)
    * `3e4f3df803fbaf3defb519ab353f507e8675108a3fc4ae14d6cc8022ddeaabbe`  [Telegraher.8.4.3.x86.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release1/Telegraher.8.4.3.x86.apk)
    * `243f10499129e9b87902b6fec638b70f179c3b1d220cfd53dcb84a1612c339db`  [Telegraher.8.4.3.x86-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release1/Telegraher.8.4.3.x86-sdk23.apk)
* PC x86, 64 bits (for 64 bits CPU)
    * `866e009ba007e8b5e4d9d5155d1530da433162dcb9653c6b1632680b0ca457e8`  [Telegraher.8.4.3.x86-64.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release1/Telegraher.8.4.3.x86-64.apk)
    * `c14a2287f8a6f7a7c5b5208eefc1bc2c552317f0d5ea802c39ad7cad9be3c238`  [Telegraher.8.4.3.x86-64-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.3_release1/Telegraher.8.4.3.x86-64-sdk23.apk)

### Issues/Wishlist

Feel free to use the "issues section". I'm not an Android programmer, i'm a Java developper.
Probably it's a good thing 😃

### Already installed this version?

Android will offer you to reinstall, simply accept this option and it the app will be reinstalled
and it will keep all the settings/accounts.

### Code mirrors

* Github: https://github.com/nikitasius/Telegraher
* Gitlab: https://gitlab.com/nikitasius/Telegraher
    * autosync from github
* HTTPS: https://git.evildayz.com/Telegraher/
    * manually sync (add a script later 😀)
    * `releases` w/ actual releases and cloned `Telegraher` & `Telegraher.git` as is and
      in `.tar.gz`

### Coffee

* Here is my [PayPal](https://paypal.me/nikitasius) `https://paypal.me/nikitasius`
* Here is
  my [BTC](bitcoin:bc1q5egmj6vjejmsu4lu3nmdshvx6p0kcajlw5u9a0?message=github_telegraher) `bc1q5egmj6vjejmsu4lu3nmdshvx6p0kcajlw5u9a0`
* Here is
  my [Yoomoney](https://yoomoney.ru/to/410015481871381) `https://yoomoney.ru/to/410015481871381`

> In fact, forget the park!