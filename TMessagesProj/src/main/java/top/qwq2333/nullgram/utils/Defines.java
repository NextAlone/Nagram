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
    public static final String disableGreetingSticker = "disableGreetingSticker";
    public static final String codeSyntaxHighlight = "codeSyntaxHighlight";
    public static final String channelAlias = "aliasChannel"; // ignore typo
    public static final String channelAliasPrefix = "aliasChannelName_"; // ignore typo
    public static final String hidePhone = "hidePhone";
    public static final String disableJumpToNextChannel = "disableJumpToNextChannel";
    public static final String verifyLinkTip = "verifyLinkTip";
    public static final String showExactNumber = "showExactNumber";
    public static final String disableTrendingSticker = "disableTrendingSticker";
    public static final String disableQuickReaction = "disableQuickReaction";
    public static final String disableInstantCamera = "disableInstantCamera";
    public static final String showHiddenSettings = "showHiddenSettings";
    public static final String confirmToSendMediaMessages = "confirmToSendMediaMessages";
    public static final String disableUndo = "disableUndo";
    public static final String skipOpenLinkConfirm = "skipOpenLinkConfirm";

    // Custom API
    public static final String customAPI = "customAPI";
    public static final String customAppId = "customAppId";
    public static final String customAppHash = "customAppHash";
    public static final int disableCustomAPI = 0;
    public static final int useTelegramAPI = 1;
    public static final int useCustomAPI = 2;
    public static final int telegramID = 4;
    public static final String telegramHash = "014b35b6184100b085b0d0572f9b5103";

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
    public static final String updateChannel = "updateChannel";
    public static final int stableChannel = 1;
    public static final int disableAutoUpdate = 0;
    public static final int ciChannel = 2;
    public static final String updateChannelSkip = "updateChannelSkip";

    // Misc
    public static final long[] officialID = {
        966253902,  // Developer
        1668888324, // Channel
        1578562490, // Developer Channel
        1645976613, // Update Channel
        1714986438, // CI Channel
        1477185964  // Discussion Group
    };
}
