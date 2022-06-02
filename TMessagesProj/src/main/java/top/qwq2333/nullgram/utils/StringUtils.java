package top.qwq2333.nullgram.utils;

import androidx.annotation.NonNull;

public class StringUtils {
    /**
     * <p>字符串是否为空白，空白的定义如下：</p>
     * <ol>
     *     <li>{@code null}</li>
     *     <li>空字符串：{@code ""}</li>
     *     <li>空格、全角空格、制表符、换行符，等不可见字符</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StringUtils.isBlank(null)     // true}</li>
     *     <li>{@code StringUtils.isBlank("")       // true}</li>
     *     <li>{@code StringUtils.isBlank(" \t\n")  // true}</li>
     *     <li>{@code StringUtils.isBlank("abc")    // false}</li>
     * </ul>
     *
     * @param str 被检测的字符串
     * @return 若为空白，则返回 true
     */
    public static boolean isBlank(CharSequence str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (!isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c) || Character.isSpaceChar(c) || c == '\ufeff' || c == '\u202a' || c == '\u0000';
    }

    /**
     * 是否空白符<br>
     * 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     */
    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    /**
     * Return a string with a maximum length of <code>length</code> characters.
     * If there are more than <code>length</code> characters, then string ends with an ellipsis ("...").
     *
     * @param text   text
     * @param length maximum length you want
     * @return Return a string with a maximum length of <code>length</code> characters.
     */
    @NonNull
    public static String ellipsis(@NonNull final String text, int length) {
        // The letters [iIl1] are slim enough to only count as half a character.
        length += Math.ceil(text.replaceAll("[^iIl]", "").length() / 2.0d);

        if (text.length() > length) {
            return text.substring(0, length - 3) + "...";
        }

        return text;
    }
}
