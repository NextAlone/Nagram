/*
 * Copyright (C) 2019-2022 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package top.qwq2333.nullgram.utils;

import org.telegram.messenger.ApplicationLoader;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class APKUtils {

    /**
     * @return 获取到的abi
     * @throws IllegalStateException 如果没找到lib目录就会抛出这错误 一般不太可能发生
     */
    public static String getAbi() throws Exception {
        String filePath = ApplicationLoader.applicationContext.getApplicationInfo().sourceDir;
        ZipFile file = new ZipFile(filePath);
        Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.contains("lib")) {
                Log.i("getAbi: " + entry.getName().split("/")[1]);
                String target = entry.getName().split("/")[1];
                if (target.contains("arm64")) {
                    return "arm64";
                } else if (target.contains("armeabi")) {
                    return "arm32";
                } else if (target.contains("x86") && !target.contains("x86_64")) {
                    return "x86";
                } else {
                    return "x86_64";
                }
            }
        }

        throw new IllegalStateException("Directory Not Found");
    }

    /**
     * 挂起当前线程
     *
     * @param millis 挂起的毫秒数
     * @return 被中断返回false，否则true
     */
    public static boolean sleep(long millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

}
