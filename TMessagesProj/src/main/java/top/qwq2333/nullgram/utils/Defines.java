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


/**
 * ConfigManager用到的Key都塞这 统一管理比较方便些
 */
public class Defines {

    // Function
    public static final String showBotAPIID = "showBotAPIID";
    public static final String ignoreBlockedUser = "ignoreBlockedUser";
    public static final String blockSponsorAds = "blockSponsorMessages";
    public static final String hideGroupSticker = "hideGroupSticker";
    public static final String allowScreenshotOnNoForwardChat = "allowScreenshotOnNoForwardChat";
    public static final String labelChannelUser = "labelChannelUser";
    public static final String displaySpoilerMsgDirectly = "displaySpoilerMsgDirectly";

    // Menu Display
    public static final String showDeleteDownloadFiles = "showDeleteDownloadFiles";
    public static final String showNoQuoteForward = "showNoQuoteForward";
    public static final String showMessagesDetail = "showMessagesDetail";
    public static final String showSaveMessages = "showSaveMessages";
    public static final String showViewHistory = "showViewHistory";
    public static final String showRepeat = "showRepeat";

    // Auto Update
    public static final String ignoredUpdateTag = "skipUpdate";
    public static final String lastCheckUpdateTime = "lastCheckUpdateTime";
    public static final String nextUpdateCheckTime = "nextUpdateCheckTime";
    public static final String skipUpdateVersion = "skipUpdateVersion";

    // Storage
    public static final String cachePath = "cachePath";

    /**
     * 0为release 1为ci构筑
     */
    public static String releaseChannel = "releaseChannel";

    // Misc
    public static final long[] officialID = {
        966253902,  // Developer
        1668888324, // Channel
        1578562490, // Developer Channel
        1645976613, // CI Channel
        1477185964  // Discussion Group
    };


}
