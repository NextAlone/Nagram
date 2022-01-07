package tw.nekomimi.nkmr;


import org.telegram.messenger.MessageObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;

public class NekomuraUtil {
    /*
     * Java文件操作 获取不带扩展名的文件名
     *
     *  Created on: 2011-8-2
     *      Author: blueeagle
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    // 消息是否为文件
    public static boolean messageObjectIsFile(int type, MessageObject messageObject) {
        boolean cansave = (type == 4 || type == 5 || type == 6 || type == 10);
        boolean downloading = messageObject.loadedFileSize > 0;

        //图片的问题
        if (type == 4 && messageObject.getDocument() == null) {
            return false;
        }
        return cansave || downloading;
    }

    // 当文件有过加载过程，loadedFileSize > 0 ，所以不能用loadedFileSize判断是否正在下载
    public static boolean messageObjectIsDownloading(int type) {
        boolean cansave = (type == 4 || type == 5 || type == 6 || type == 10);
        return !cansave;
    }

    // 取出整数的（二进制）某一位
    public static boolean getBit(int value, int i) {
        int mask = 1 << i;
        int ii = value & mask;
        if (ii == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static int setBit(int value, int i, boolean v) {
        int mask = 1 << i;
        if (v) {
            value |= mask;
        } else {
            value &= ~mask;
        }
        return value;
    }

    public static int reverseBit(int value, int i) {
        int mask = 1 << i;
        return value ^ mask;
    }

    /**
     * 取两个文本之间的文本值
     *
     * @param text  源文本 比如：欲取全文本为 12345
     * @param left  文本前面
     * @param right 后面文本
     * @return 返回 String
     */
    public static String getSubString(String text, String left, String right) {
        String result = "";
        int zLen;
        if (left == null || left.isEmpty()) {
            zLen = 0;
        } else {
            zLen = text.indexOf(left);
            if (zLen > -1) {
                zLen += left.length();
            } else {
                zLen = 0;
            }
        }
        int yLen = text.indexOf(right, zLen);
        if (yLen < 0 || right == null || right.isEmpty()) {
            yLen = text.length();
        }
        result = text.substring(zLen, yLen);
        return result;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }


    /**
     * Read the file and calculate the SHA-1 checksum
     *
     * @param file the file to read
     * @return the hex representation of the SHA-1 using uppercase chars
     * @throws FileNotFoundException    if the file does not exist, is a directory rather than a
     *                                  regular file, or for some other reason cannot be opened for
     *                                  reading
     * @throws IOException              if an I/O error occurs
     * @throws NoSuchAlgorithmException should never happen
     */
    public static String calcSHA1(File file) throws FileNotFoundException,
            IOException, NoSuchAlgorithmException {

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {

            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            return convertToHex(sha1.digest());
        }
    }

    public static byte[] uncompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}