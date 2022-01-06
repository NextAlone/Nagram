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
    * YES!!
      1 ([f9852](https://github.com/nikitasius/Telegraher/commit/f985223fcb0eac8ce382595f940aaac8cae1454e))
    * no more sponsored messages, we still download them but do not display
        * we still count views for this ADS to hide our behavior of the app "who don't earn money
          for
          TG" ([d4243](https://github.com/nikitasius/Telegraher/commit/d4243d3bcd676e7b2e35626d9969a0de3a448e1d))
* EVERY element have `save to downloads`/`save to gallery`
    * messages are elements too, you still can click but cannot save them into downloads
        * however i do not recomment this
    * use it wisely
* 6 accounts instead of 3
    * client support upto 6
      accounts ([384bf](https://github.com/nikitasius/Telegraher/commit/384bf4421f26773a7f970578ef4bfe6676fbe312))
        * ~~on the 1st run when you type phone number and continue it can throw an API error~~
            * error was only on the version with 16 accounts
                * to get more accounts w/o having problems with TGs api need to change the App's
                  flow
            * in case of an error close the error by clicking OK and let it relax for 5 minutes
            * continue and log in
* DISABLED REMOTE DELETIONS
    * NO more deletions via GCM PUSHES (chats and messages), WTF 💩 (
      [58150](https://github.com/nikitasius/Telegraher/commit/5815025ab8fbc71d2b1be59dd2750f6edfb0c2b8))
    * NO more deletions in groups and channels
        * i hate the channels that wipes the content
    * NO more remote deletions in private chats
    * NO more remote deletions in secret
      chats ([3a87a](https://github.com/nikitasius/Telegraher/commit/3a87a80cb44f8e646a884a3cccb0d41306f2dbb9)
      [ec5dd](https://github.com/nikitasius/Telegraher/commit/ec5ddde611eaa795ffe7dc6d58a8e9090872f6b5))
        * self destruction timer doesn't work but other folks see that you're opened and deleted it
    * NO more "history wipe" or chat deletions
        * history/messages remains where they are
        * chat becomes inactive if other folks are deleted it for them and/or for you
* FULL ACCESS in "restrict saving content" chats
    * screenshots, gif imports, media
      saving ([8a35b](https://github.com/nikitasius/Telegraher/commit/8a35b741310fd29acc06993cf260af98bbf36057)
      [bd5c0](https://github.com/nikitasius/Telegraher/commit/bd5c0a3c7e66fe0336cbcbd2837861e705e6053c))
    * DO NOT save GIFS via saving gifs
        * just click on them and choose "save to
          gallery" ([d9cdf](https://github.com/nikitasius/Telegraher/commit/d9cdf1c38dc4db04707b093116de32975b276b95))
        * after this share this media no matter where as a video file **WITHOUT** sound
        * it will become a GIF in your collection
    * you **can't forward** message as is due it use **telegram API** and their server will block
      it, so save/copy
        * save GIF / forward message ARE using telegrams API, while SHARING or DOWNLOADING not
    * [d0905](https://github.com/nikitasius/Telegraher/commit/d09053dad35a97470d23da8680cd360a03b360d9)
* FULL ACCESS in secret chats (GREEN ONES!)
    * you can download any medias and
      documents ([f61d6](https://github.com/nikitasius/Telegraher/commit/f61d6147fe307214fc6f4853c513668ee4ba96cd))
    * you can take screenshots or record your screen
        * WTF apple can do that, we can do it
          too! ([0256c](https://github.com/nikitasius/Telegraher/commit/0256c6891b657975566b98700a349afb55fe81b1))
* GIFs have controls
    * you can start/stop GIFs or navigate on a
      timeline ([a8a22](https://github.com/nikitasius/Telegraher/commit/a8a22c09d7215f7c138513e9c2dec4260771b8d3))
* KEEP CACHED chats
    * cached chats are always with you even if you're BANNED (
      [03307](https://github.com/nikitasius/Telegraher/commit/03307a9d90b86bcb5864880592110c4ee3427ebb))
        * once banned you will get a message about this
        * you can navigate in chat where you was banned using your cache
        * to remove cached chat you must delete it ("leave that chat")
    * even when when you restart your app it will load cached chats
* HISTORY in private chats
    * message separated inside by RFC1123 timestamp
      field ([aa1c8](https://github.com/nikitasius/Telegraher/commit/aa1c84667d1a889e580e3557cb1f3619898b1092)
      [2a279](https://github.com/nikitasius/Telegraher/commit/2a27946fd18761efaf3ba03d58343e5b2bfc99fa) [8d7ef](https://github.com/nikitasius/Telegraher/commit/8d7efe07173ffaa3bd1d7f9c793626aacb79f6ed))
    * when someone will send you a PM and will change it, while you in chat you will see `edited` as
      usual
        * to see changes you need to **close/open** this **chat**
            * this will be probably fixed in future (display in real time changes)
    * this also affect bots, so when your bots edit their messages, you will see old/new versions
* NO MORE timer
    * when someone send you a media with a timer in a secret (green) chat this message will be
      deleted on a device who sent it once you open
      it ([3fb56](https://github.com/nikitasius/Telegraher/commit/3fb5636d5141758796561e32810eda35dd4812ba) [b427e](https://github.com/nikitasius/Telegraher/commit/b427e9936fd5b1d1a0547fea83baaffa86b4e2b9))
    * when someone send you a media with a timer in a private (NON-green) chat this message will be
      deleted on a device who sent it once OR twice you open it (
      2491b5f4ad7b968c8e3f954fe377a27502fca831)
        * EASY AND SHORT: open photo twice and it will WIPE it from a device who sent it ;-)
        * LONG AND DETAILED: due it will send an event only if full file is downloaded by your
          client when you open this (guess due file size). In most of cases client download media
          file during 1st open, so you need to open it again (just tap on it)
        * sometimes full media downloaded when preview generated, in such case open once file and it
          will wipe it from a user who sent it
* NO MORE `edit_hide`
    * telegram can send you messages with `edit_hide==true` to hide what this message has been
      edited ([9b871](https://github.com/nikitasius/Telegraher/commit/9b87154dce324cb4eeb0cd3c02e32f7815c67359) [80fc8](https://github.com/nikitasius/Telegraher/commit/80fc81ceabd250fb87d23d516eb065c7ca72b889))
    * now if message contains signs of editions it will marked
* DISABLED emulator detections
    * idk why the client use this, but i disabled
      it ([26033](https://github.com/nikitasius/Telegraher/commit/26033c0ebc982c8097bfc3587cdf95ebde39d792))
    * if i want to run it on emulator telegram no need to know it
* APP name changed & APP icon changed & APP package changed
    * of you want to run using old name just rollback those commits and build the app
        * [14734](https://github.com/nikitasius/Telegraher/commit/14734a444cac5883e3d169808bf4162da3aa7cdc) [95108](https://github.com/nikitasius/Telegraher/commit/951084eb38d75f6c52a21be77648f66f4eb07712)
          [ca70b](https://github.com/nikitasius/Telegraher/commit/ca70b56631ce57f45bf6dc443dc35141b1429109) [3aa51](https://github.com/nikitasius/Telegraher/commit/3aa519bc456e4cefdd43ecc9016e22ee672b5e53)
* APPs api & hash are changed for legit TG
  client ([f8eb8](https://github.com/nikitasius/Telegraher/commit/f8eb8ab357a0f557062a35f51e05e04c2919882c))

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
    * `63ee2e2d3c4ee5ecf794f69163ee8ddceffd63274690ab5f779303f44d618ba3`  [Telegraher.8.4.2.arm64-v8a.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.2_release3/Telegraher.8.4.2.arm64-v8a.apk)
    * `df98d3d7a5754cb1e970802e049427552d20178e55fc3011be3bdfb3ca667fc7`  [Telegraher.8.4.2.arm64-v8a-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.2_release3/Telegraher.8.4.2.arm64-v8a-sdk23.apk)
* armeabi-v7a (old devices)
    * `542070e4d2685304b4c3c1966ec5a92b1a7a247b471490206f38328ae6b52f94`  [Telegraher.8.4.2.armeabi-v7a.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.2_release3/Telegraher.8.4.2.armeabi-v7a.apk)
    * `7a649a5a5243f58c6a390e71d8b087c3366bca153f1cd0997177c62cf312bca7`  [Telegraher.8.4.2.armeabi-v7a-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.2_release3/Telegraher.8.4.2.armeabi-v7a-sdk23.apk)
* PC x86, 32 bits (for an emulator for example)
    * `a3d847a606ab1e938107caeb18879d119ece1c70de68ea77385a468e7cdd9da5`  [Telegraher.8.4.2.x86.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.2_release3/Telegraher.8.4.2.x86.apk)
    * `2c1e876b2bc10c1fb710aff573fb927e98e657677496b269d7d60b363eec4a81`  [Telegraher.8.4.2.x86-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.2_release3/Telegraher.8.4.2.x86-sdk23.apk)
* PC x86, 64 bits (for 64 bits CPU)
    * `b36d3c6d2633d090d0fe28aff3cec9caf1495de5114befaa8dcec3748b938402`  [Telegraher.8.4.2.x86-64.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.2_release3/Telegraher.8.4.2.x86-64.apk)
    * `a8e67b86e04f86389327938c5ec9573e9ad6eeccb0f4d4c6577abafc2a4d5b06`  [Telegraher.8.4.2.x86-64-sdk23.apk](https://github.com/nikitasius/Telegraher/releases/download/noshit_8.4.2_release3/Telegraher.8.4.2.x86-64-sdk23.apk)

### Issues/Wishlist

Feel free to use the "issues section". I'm not an Android programmer, i'm a Java developper.
Probably it's a good thing 😃

### Already installed this version?

Android will offer you to reinstall, simply accept this option and it the app will be reinstalled
and it will keep all the settings/accounts.

### Coffee

* Here is my [PayPal](https://paypal.me/nikitasius) `https://paypal.me/nikitasius`
* Here is
  my [BTC](bitcoin:bc1q5egmj6vjejmsu4lu3nmdshvx6p0kcajlw5u9a0?message=github_telegraher) `bc1q5egmj6vjejmsu4lu3nmdshvx6p0kcajlw5u9a0`
* Here is
  my [Yoomoney](https://yoomoney.ru/to/410015481871381) `https://yoomoney.ru/to/410015481871381`

> In fact, forget the park!