package tw.nekomimi.nekogram.utils;

import org.telegram.tgnet.TLRPC;

public class NeteaseEmbed {

    public static boolean isNeteaseWebPage(String url) {
        return url != null && url.startsWith("music.163.com/");
    }

    public static String getNeteaseAlbumId(String url) {
        if (url.contains("album")) {
            return url.replace("music.163.com/album?id=", "");
        }
        return null;
    }

    public static String getNeteaseAlbumEmbed(String id) {
        return "https://music.163.com/outchain/player?type=1&auto=1&id=" + id;
    }

    public static String getNeteaseSongId(String url) {
        if (url.contains("song")) {
            return url.replace("music.163.com/song?id=", "");
        }
        return null;
    }

    public static String getNeteaseSongEmbed(String id) {
        return "https://music.163.com/outchain/player?type=2&auto=1&id=" + id;
    }

    public static void fixWebPage(TLRPC.WebPage webpage) {
        if (webpage == null || !isNeteaseWebPage(webpage.display_url)) {
            return;
        }
        String albumId = getNeteaseAlbumId(webpage.display_url);
        String songId = getNeteaseSongId(webpage.display_url);
        if (albumId != null) {
            webpage.embed_url = getNeteaseAlbumEmbed(albumId);
            webpage.embed_type = "iframe";
            webpage.embed_width = 300;
            webpage.embed_height = 380;
        }
        if (songId != null) {
            webpage.embed_url = getNeteaseSongEmbed(songId);
            webpage.embed_type = "iframe";
            webpage.embed_width = 300;
            webpage.embed_height = 380;
        }
    }
}
