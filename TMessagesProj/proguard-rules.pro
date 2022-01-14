-keep public class com.google.android.gms.* { public *; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keep class org.webrtc.* { *; }
-keep class org.webrtc.audio.* { *; }
-keep class org.webrtc.voiceengine.* { *; }
-keep class org.telegram.messenger.* { *; }
-keep class org.telegram.messenger.camera.* { *; }
-keep class org.telegram.messenger.secretmedia.* { *; }
-keep class org.telegram.messenger.support.* { *; }
-keep class org.telegram.messenger.support.* { *; }
-keep class org.telegram.messenger.time.* { *; }
-keep class org.telegram.messenger.video.* { *; }
-keep class org.telegram.messenger.voip.* { *; }
-keep class org.telegram.SQLite.** { *; }
-keep class org.telegram.tgnet.ConnectionsManager { *; }
-keep class org.telegram.tgnet.NativeByteBuffer { *; }
-keep class org.telegram.tgnet.RequestDelegateInternal { *; }
-keep class org.telegram.tgnet.RequestTimeDelegate { *; }
-keep class org.telegram.tgnet.RequestDelegate { *; }
-keep class org.telegram.tgnet.QuickAckDelegate { *; }
-keep class org.telegram.tgnet.WriteToSocketDelegate { *; }
-keep class com.google.android.exoplayer2.ext.** { *; }
-keep class com.google.android.exoplayer2.util.FlacStreamMetadata { *; }
-keep class com.google.android.exoplayer2.metadata.flac.PictureFrame { *; }
-keep class com.google.android.exoplayer2.decoder.SimpleOutputBuffer { *; }

-keep class org.dizitart.no2.**  { *; }
-keep class org.slf4j.** { *; }
-keep class org.h2.** { *; }
-keep class org.objenesis.** { *; }
-keep class com.fasterxml.jackson.** { *; }
-keep class com.alibaba.fastjson.** { *; }
-keep class cn.hutool.** { *; }
-keep class org.springframework.** { *; }
-keep class org.thymeleaf.** { *; }
-keep class org.tinylog.** { *; }
-keep class org.wltea.** { *; }
-keep class org.yaml.** { *; }
-keep class oshi.** { *; }
-keep class redis.clients.** { *; }
-keep class retrofit2.** { *; }
-keep class springfox.documentation.spring.web.json.Json

-keep class ch.ethz.** { *; }
-keep class cn.beecp.** { *; }
-keep class com.alibaba.** { *; }
-keep class com.chenlb.** { *; }
-keep class com.github.houbb.** { *; }
-keep class com.github.promeg.** { *; }
-keep class com.github.stuxuhai.** { *; }
-keep class com.google.common.** { *; }
-keep class com.google.zxing.** { *; }
-keep class com.googlecode.** { *; }
-keep class com.hankcs.** { *; }
-keep class com.jcraft.** { *; }
-keep class com.jfinal.** { *; }
-keep class com.jfirer.** { *; }
-keep class com.mayabot.** { *; }
-keep class com.mchange.** { *; }
-keep class com.mongodb.** { *; }
-keep class com.rnkrsoft.** { *; }
-keep class com.rnkrsoft.bopomofo4j.ToneType
-keep class com.sun.net.** { *; }
-keep class com.vdurmont.** { *; }
-keep class com.zaxxer.** { *; }
-keep class freemarker.** { *; }
-keep class io.github.logtube.** { *; }
-keep class java.awt.** { *; }
-keep class java.beans.** { *; }
-keep class java.lang.management.ClassLoadingMXBean
-keep class javax.** { *; }
-keep class net.sf.** { *; }
-keep class net.sourceforge.** { *; }
-keep class okhttp3.** { *; }
-keep class org.ansj.** { *; }
-keep class org.apache.** { *; }
-keep class org.apdplat.** { *; }
-keep class org.beetl.** { *; }
-keep class org.bouncycastle.** { *; }
-keep class org.febit.** { *; }
-keep class org.glassfish.** { *; }
-keep class org.javamoney.** { *; }
-keep class org.jboss.** { *; }
-keep class org.jboss.** { *; }
-keep class org.joda.** { *; }
-keep class org.lionsoul.** { *; }
-keep class org.mozilla.** { *; }
-keep class org.mvel2.** { *; }
-keep class org.ofdrw.** { *; }
-keep class org.openxmlformats.** { *; }
-keep class org.pmw.tinylog.** { *; }
-keep class org.rythmengine.** { *; }
-keep class org.slf4j.** { *; }

# https://developers.google.com/ml-kit/known-issues#android_issues
-keep class com.google.mlkit.nl.languageid.internal.LanguageIdentificationJni { *; }

# Constant folding for resource integers may mean that a resource passed to this method appears to be unused. Keep the method to prevent this from happening.
-keep class com.google.android.exoplayer2.upstream.RawResourceDataSource {
  public static android.net.Uri buildRawResourceUri(int);
}

# Methods accessed via reflection in DefaultExtractorsFactory
-dontnote com.google.android.exoplayer2.ext.flac.FlacLibrary
-keepclassmembers class com.google.android.exoplayer2.ext.flac.FlacLibrary {

}

# Some members of this class are being accessed from native methods. Keep them unobfuscated.
-keep class com.google.android.exoplayer2.video.VideoDecoderOutputBuffer {
  *;
}

-dontnote com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.opus.LibopusAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioProcessor[]);
}
-dontnote com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioProcessor[]);
}
-dontnote com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer
-keepclassmembers class com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer {
  <init>(android.os.Handler, com.google.android.exoplayer2.audio.AudioRendererEventListener, com.google.android.exoplayer2.audio.AudioProcessor[]);
}

# Constructors accessed via reflection in DefaultExtractorsFactory
-dontnote com.google.android.exoplayer2.ext.flac.FlacExtractor
-keepclassmembers class com.google.android.exoplayer2.ext.flac.FlacExtractor {
  <init>();
}

# Constructors accessed via reflection in DefaultDownloaderFactory
-dontnote com.google.android.exoplayer2.source.dash.offline.DashDownloader
-keepclassmembers class com.google.android.exoplayer2.source.dash.offline.DashDownloader {
  <init>(android.net.Uri, java.util.List, com.google.android.exoplayer2.offline.DownloaderConstructorHelper);
}
-dontnote com.google.android.exoplayer2.source.hls.offline.HlsDownloader
-keepclassmembers class com.google.android.exoplayer2.source.hls.offline.HlsDownloader {
  <init>(android.net.Uri, java.util.List, com.google.android.exoplayer2.offline.DownloaderConstructorHelper);
}
-dontnote com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader
-keepclassmembers class com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloader {
  <init>(android.net.Uri, java.util.List, com.google.android.exoplayer2.offline.DownloaderConstructorHelper);
}

# Constructors accessed via reflection in DownloadHelper
-dontnote com.google.android.exoplayer2.source.dash.DashMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.dash.DashMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}
-dontnote com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory
-keepclasseswithmembers class com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource$Factory {
  <init>(com.google.android.exoplayer2.upstream.DataSource$Factory);
}

# Don't warn about checkerframework and Kotlin annotations
-dontwarn org.checkerframework.**
-dontwarn javax.annotation.**

# Use -keep to explicitly keep any other classes shrinking would remove
-dontoptimize
-dontobfuscate
