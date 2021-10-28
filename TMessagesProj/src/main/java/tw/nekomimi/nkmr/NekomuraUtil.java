package tw.nekomimi.nkmr;


        import org.telegram.messenger.ApplicationLoader;
        import org.telegram.messenger.MessageObject;

        import java.io.File;

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
}