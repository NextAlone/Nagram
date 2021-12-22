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

So **all the code changes** are in `noshit_8.3.1`.

### Detailed summary / noshit_8.3.1

* DISABLED ADS
    * YES!!1
    * no more sponsored messages, we still download them but do not display
* EVERY element have `save to downloads`/`save to gallery`
    * messages are elements too, you still can click but cannot save them into downloads
        * however i do not recomment this
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
* KEEP CACHED chats
    * cached chats are always with you even if you're BANNED
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
        * git checkout lastest **noshit** branch
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
    * `0108f669043ca48ed5d5b848547316f09a0e4a6c348a396010e7d609e5868bee`  [Telegraher.8.3.1.arm64-v8a.apk](/apk/Telegraher.8.3.1.arm64-v8a.apk)
    * `095259d282749302866820db61e161b3192c34a5646e0d5c556e413d8e746884`  [Telegraher.8.3.1.arm64-v8a-sdk23.apk](/apk/Telegraher.8.3.1.arm64-v8a-sdk23.apk)
* armeabi-v7a (old devices)
    * `5a2e36bb012c41c8f1df4bc9269e9abeb08f82fcf0a9a8de9fd17be49c2251b1`  [Telegraher.8.3.1.armeabi-v7a.apk](/apk/Telegraher.8.3.1.armeabi-v7a.apk)
    * `cba750994fd83bb84c3da5b5c1d55ce0c28f8d46d2c1db35a17d7e264b90ac6f`  [Telegraher.8.3.1.armeabi-v7a-sdk23.apk](/apk/Telegraher.8.3.1.armeabi-v7a-sdk23.apk)
* PC x86, 32 bits (for an emulator for example)
    * `aec0a9e8751c4b616ffbce4bef420460877e04496680c0246a1778b4366b640d`  [Telegraher.8.3.1.x86.apk](/apk/Telegraher.8.3.1.x86.apk)
    * `bc0e72618b85aabcfe37236f379bc54ea1e002ee224b3061b97e388680f58314`  [Telegraher.8.3.1.x86-sdk23.apk](/apk/Telegraher.8.3.1.x86-sdk23.apk)
* PC x86, 64 bits (for 64 bits CPU)
    * `9637419f5980e3171ece9ebaa4fa4da3d1e9f414da6b87b3d5249028408ece81`  [Telegraher.8.3.1.x86-64.apk](/apk/Telegraher.8.3.1.x86-64.apk)
    * `ad2d4987366a45a36f67640d71d5f5e2e0043b6559944d273158195ba790f51e`  [Telegraher.8.3.1.x86-64-sdk23.apk](/apk/Telegraher.8.3.1.x86-64-sdk23.apk)

### Issues/Wishlist

Feel free to use the "issues section". I'm not an Android programmer, i'm a Java developper.
Probably it's a good thing 😃

### Coffee

* Here is my [PayPal](https://paypal.me/nikitasius) `https://paypal.me/nikitasius`
* Here is
  my [BTC](bitcoin:bc1q5egmj6vjejmsu4lu3nmdshvx6p0kcajlw5u9a0?message=github_telegraher) `bc1q5egmj6vjejmsu4lu3nmdshvx6p0kcajlw5u9a0`

> In fact, forget the park!