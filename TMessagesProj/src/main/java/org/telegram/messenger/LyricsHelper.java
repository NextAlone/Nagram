package org.telegram.messenger;

import android.text.TextUtils;

import org.telegram.messenger.audioinfo.AudioInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lyrics loading / caching / display support for the in-app audio player.
 * <p>
 * 1. Extracts lyrics from the media file metadata (ID3v2 USLT, MP4/M4A ©lyr, ...) through {@link AudioInfo}.
 * 2. Parses both timed LRC lyrics and plain text lyrics.
 * 3. Caches the parsed result in memory (LRU) and on disk, keyed by file identity, so
 *    re-parsing the file on every play is avoided.
 * <p>
 * Note: OPlus / ColorOS lock-screen lyrics are handled by the dev-branch Kotlin helper
 * (xyz.nextalone.nagram.helper.LyricsHelper) that feeds the media-session "lyricInfo" metadata.
 */
public class LyricsHelper {

    private static final int MEMORY_CACHE_MAX = 64;
    private static final long DISK_CACHE_MAX_BYTES = 512 * 1024;
    /** Total cap for the on-disk lyrics cache; entries are evicted oldest-first when exceeded. */
    private static final long DISK_CACHE_MAX_TOTAL_BYTES = 8L * 1024 * 1024;
    /** Bounded set of file keys known to have no lyrics, so we don't re-parse them on every play. */
    private static final int NEGATIVE_CACHE_MAX = 64;
    private static final Pattern TIME_TAG = Pattern.compile("\\[(\\d{1,3}):(\\d{1,2})(?:[.:](\\d{1,3}))?\\]");
    private static final Pattern META_TAG = Pattern.compile("\\[[a-zA-Z]+:[^\\]]*\\]");
    private static final Pattern ANY_TAG = Pattern.compile("\\[[^\\]]*\\]");

    public static class LyricsLine {
        public final long timeMs;   // -1 for unsynced lines
        public final String text;

        public LyricsLine(long timeMs, String text) {
            this.timeMs = timeMs;
            this.text = text;
        }
    }

    public static class LyricsData {
        public final String rawText;
        public final boolean isSynced;
        public final List<LyricsLine> lines;

        LyricsData(String rawText, boolean isSynced, List<LyricsLine> lines) {
            this.rawText = rawText;
            this.isSynced = isSynced;
            this.lines = lines;
        }

        public static LyricsData fromRawText(String raw) {
            if (TextUtils.isEmpty(raw)) {
                return null;
            }
            List<LyricsLine> lines = new ArrayList<>();
            long offsetMs = 0;
            boolean anySynced = false;
            String[] rows = raw.split("\\r?\\n");
            for (String row : rows) {
                if (row == null) {
                    continue;
                }
                String trimmed = row.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                Matcher timeMatcher = TIME_TAG.matcher(trimmed);
                List<Long> times = new ArrayList<>();
                while (timeMatcher.find()) {
                    times.add(parseTime(timeMatcher));
                    anySynced = true;
                }
                if (times.isEmpty()) {
                    Matcher metaMatcher = META_TAG.matcher(trimmed);
                    boolean isMeta = false;
                    while (metaMatcher.find()) {
                        isMeta = true;
                        String tag = metaMatcher.group().trim();
                        if (tag.regionMatches(true, 1, "offset:", 0, 7)) {
                            try {
                                offsetMs = Long.parseLong(tag.substring(8, tag.length() - 1).trim());
                            } catch (Exception ignore) {
                                // invalid offset, ignore
                            }
                        }
                    }
                    if (!isMeta) {
                        lines.add(new LyricsLine(-1, trimmed));
                    }
                } else {
                    String text = ANY_TAG.matcher(trimmed).replaceAll("").trim();
                    for (Long t : times) {
                        lines.add(new LyricsLine(Math.max(0, t + offsetMs), text));
                    }
                }
            }
            if (lines.isEmpty()) {
                return null;
            }
            boolean synced = anySynced;
            if (synced) {
                // stable sort keeps the original order of same-timestamp lines (e.g. bilingual groups)
                lines.sort((a, b) -> Long.compare(a.timeMs, b.timeMs));
            }
            return new LyricsData(raw, synced, lines);
        }
    }

    private static long parseTime(Matcher matcher) {
        long minutes = Long.parseLong(matcher.group(1));
        long seconds = Long.parseLong(matcher.group(2));
        long fractionMs = 0;
        String fraction = matcher.group(3);
        if (!TextUtils.isEmpty(fraction)) {
            if (fraction.length() == 1) {
                fractionMs = Long.parseLong(fraction) * 100;
            } else if (fraction.length() == 2) {
                fractionMs = Long.parseLong(fraction) * 10;
            } else {
                fractionMs = Long.parseLong(fraction);
            }
        }
        return (minutes * 60 + seconds) * 1000 + fractionMs;
    }

    /**
     * Loads lyrics for a file: memory cache -> negative cache -> disk cache -> metadata extraction.
     * Returns null when the file has no lyrics. Never throws.
     */
    public static LyricsData loadLyrics(File file) {
        return loadLyrics(file, null);
    }

    /**
     * Same as {@link #loadLyrics(File)} but accepts an already parsed {@link AudioInfo} to avoid
     * re-reading the file when the caller already parsed it. Callers must make sure the AudioInfo
     * belongs to the same track as the file, otherwise wrong lyrics could be cached under its key.
     */
    public static LyricsData loadLyrics(File file, AudioInfo audioInfo) {
        if (file == null || !file.exists()) {
            return null;
        }
        String key = cacheKey(file);
        synchronized (memoryCache) {
            LyricsData cached = memoryCache.get(key);
            if (cached != null) {
                return cached;
            }
        }
        if (isNegativeCached(key)) {
            return null;
        }
        File cacheFile = diskCacheFile(key);
        String raw = readDiskCache(cacheFile);
        if (raw != null) {
            LyricsData data = LyricsData.fromRawText(raw);
            if (data != null) {
                putMemoryCache(key, data);
                return data;
            }
        }
        LyricsData data = extractFromMetadata(file, audioInfo);
        if (data != null) {
            putMemoryCache(key, data);
            writeDiskCache(cacheFile, data.rawText);
        } else {
            putNegativeCache(key);
        }
        return data;
    }

    private static LyricsData extractFromMetadata(File file, AudioInfo audioInfo) {
        if (audioInfo == null) {
            try {
                audioInfo = AudioInfo.getAudioInfo(file);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (audioInfo == null) {
            return null;
        }
        try {
            String lyrics = audioInfo.getLyrics();
            if (TextUtils.isEmpty(lyrics)) {
                return null;
            }
            return LyricsData.fromRawText(lyrics);
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    // ------------------------------------------------------------------------------------------
    // Caching
    // ------------------------------------------------------------------------------------------

    private static final LinkedHashMap<String, LyricsData> memoryCache = new LinkedHashMap<String, LyricsData>(MEMORY_CACHE_MAX, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, LyricsData> eldest) {
            return size() > MEMORY_CACHE_MAX;
        }
    };

    /** Keys of files that were inspected and found to have no lyrics (insertion-ordered, bounded). */
    private static final LinkedHashSet<String> noLyricsKeys = new LinkedHashSet<>();

    private static boolean isNegativeCached(String key) {
        synchronized (noLyricsKeys) {
            return noLyricsKeys.contains(key);
        }
    }

    private static void putNegativeCache(String key) {
        synchronized (noLyricsKeys) {
            noLyricsKeys.add(key);
            while (noLyricsKeys.size() > NEGATIVE_CACHE_MAX) {
                java.util.Iterator<String> it = noLyricsKeys.iterator();
                it.next();
                it.remove();
            }
        }
    }

    private static void putMemoryCache(String key, LyricsData data) {
        synchronized (memoryCache) {
            memoryCache.put(key, data);
        }
    }

    private static String cacheKey(File file) {
        return Utilities.MD5(file.getAbsolutePath() + "|" + file.length() + "|" + file.lastModified());
    }

    private static File getDiskCacheDir() {
        return new File(ApplicationLoader.applicationContext.getCacheDir(), "lyrics_cache");
    }

    private static File diskCacheFile(String key) {
        return new File(getDiskCacheDir(), key + ".txt");
    }

    private static String readDiskCache(File file) {
        if (file == null || !file.exists() || file.length() <= 0 || file.length() > DISK_CACHE_MAX_BYTES) {
            return null;
        }
        try (InputStream in = new FileInputStream(file)) {
            byte[] buf = new byte[(int) file.length()];
            int read = 0;
            while (read < buf.length) {
                int r = in.read(buf, read, buf.length - read);
                if (r < 0) {
                    break;
                }
                read += r;
            }
            return new String(buf, 0, read, StandardCharsets.UTF_8);
        } catch (IOException e) {
            FileLog.e(e);
        }
        return null;
    }

    private static void writeDiskCache(File file, String content) {
        if (file == null || TextUtils.isEmpty(content)) {
            return;
        }
        try {
            File dir = getDiskCacheDir();
            if (!dir.exists() && !dir.mkdirs()) {
                return;
            }
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            if (bytes.length > DISK_CACHE_MAX_BYTES) {
                return;
            }
            try (OutputStream out = new FileOutputStream(file)) {
                out.write(bytes);
            }
            trimDiskCache(dir);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /**
     * Keeps the total on-disk cache under {@link #DISK_CACHE_MAX_TOTAL_BYTES} by deleting the
     * oldest entries (by last-modified time) once the cap is exceeded.
     */
    private static void trimDiskCache(File dir) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        long total = 0;
        for (File f : files) {
            total += f.length();
        }
        if (total <= DISK_CACHE_MAX_TOTAL_BYTES) {
            return;
        }
        java.util.Arrays.sort(files, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
        for (File f : files) {
            if (total <= DISK_CACHE_MAX_TOTAL_BYTES) {
                break;
            }
            total -= f.length();
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
    }
}
