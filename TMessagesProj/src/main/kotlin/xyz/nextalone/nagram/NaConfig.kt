package xyz.nextalone.nagram

import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.config.ConfigItem
import xyz.nextalone.nagram.helper.DoubleTap
import tw.nekomimi.nekogram.config.ConfigItemKeyLinked
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import androidx.core.net.toUri


object NaConfig {
    const val TAG =
        "NextAlone"
    val preferences: SharedPreferences =
        NkmrConfig.preferences
    val sync =
        Any()
    private var configLoaded =
        false
    private val configs =
        ArrayList<ConfigItem>()

    // Configs
    val forceCopy =
        addConfig(
            "ForceCopy",
            ConfigItem.configTypeBool,
            false
        )
    val disableSecondAddress =
        addConfig(
            "disableSecondAddress",
            ConfigItem.configTypeBool,
            true
        )
    val showInvertReply =
        addConfig(
            "InvertReply",
            ConfigItem.configTypeBool,
            false
        )
    val showGreatOrPoor =
        addConfig(
            "GreatOrPoor",
            ConfigItem.configTypeBool,
            false
        )
    val showTextBold =
        addConfig(
            "TextBold",
            ConfigItem.configTypeBool,
            true
        )
    val showTextItalic =
        addConfig(
            "TextItalic",
            ConfigItem.configTypeBool,
            true
        )
    val showTextMono =
        addConfig(
            "TextMonospace",
            ConfigItem.configTypeBool,
            true
        )
    val showTextStrikethrough =
        addConfig(
            "TextStrikethrough",
            ConfigItem.configTypeBool,
            true
        )
    val showTextUnderline =
        addConfig(
            "TextUnderline",
            ConfigItem.configTypeBool,
            true
        )
    val showTextQuote =
        addConfig(
            "TextQuote",
            ConfigItem.configTypeBool,
            true
        )
    val showTextSpoiler =
        addConfig(
            "TextSpoiler",
            ConfigItem.configTypeBool,
            true
        )
    val showTextCreateLink =
        addConfig(
            "TextLink",
            ConfigItem.configTypeBool,
            true
        )
    val showTextCreateMention =
        addConfig(
            "TextCreateMention",
            ConfigItem.configTypeBool,
            true
        )
    val showTextRegular =
        addConfig(
            "TextRegular",
            ConfigItem.configTypeBool,
            true
        )
    val combineMessage =
        addConfig(
            "CombineMessage",
            ConfigItem.configTypeInt,
            0
        )
    val showTextUndoRedo =
        addConfig(
            "TextUndoRedo",
            ConfigItem.configTypeBool,
            false
        )
    val noiseSuppressAndVoiceEnhance =
        addConfig(
            "NoiseSuppressAndVoiceEnhance",
            ConfigItem.configTypeBool,
            false
        )
    val showNoQuoteForward =
        addConfig(
            "NoQuoteForward",
            ConfigItem.configTypeBool,
            true
        )
    val showRepeatAsCopy =
        addConfig(
            "RepeatAsCopy",
            ConfigItem.configTypeBool,
            false
        )
    val doubleTapActionIncoming =
        addConfig(
            "DoubleTapActionIncoming",
            ConfigItem.configTypeInt,
            0
        )
    val doubleTapActionOutgoing =
        addConfig(
            "DoubleTapActionOutgoing",
            ConfigItem.configTypeInt,
            0
        )
    val showCopyPhoto =
        addConfig(
            "CopyPhoto",
            ConfigItem.configTypeBool,
            false
        )
    val showReactions =
        addConfig(
            "Reactions",
            ConfigItem.configTypeBool,
            true
        )
    val showServicesTime =
        addConfig(
            "ShowServicesTime",
            ConfigItem.configTypeBool,
            true
        )
    val customTitle =
        addConfig(
            "CustomTitle",
            ConfigItem.configTypeString,
            LocaleController.getString(
                R.string.NekoX
            )
        )
    val useSystemUnlock =
        addConfig(
            "UseSystemUnlock",
            ConfigItem.configTypeBool,
            true
        )
    val codeSyntaxHighlight =
        addConfig(
            "CodeSyntaxHighlight",
            ConfigItem.configTypeBool,
            true
        )
    val dateOfForwardedMsg =
        addConfig(
            "DateOfForwardedMsg",
            ConfigItem.configTypeBool,
            false
        )
    val showForwardCount =
        addConfig(
            "ShowForwardCount",
            ConfigItem.configTypeBool,
            true
        )
    val showMessageID =
        addConfig(
            "ShowMessageID",
            ConfigItem.configTypeBool,
            false
        )
    val showRPCError =
        addConfig(
            "ShowRPCError",
            ConfigItem.configTypeBool,
            false
        )
    val showPremiumStarInChat =
        addConfig(
            "ShowPremiumStarInChat",
            ConfigItem.configTypeBool,
            true
        )
    val showPremiumAvatarAnimation =
        addConfig(
            "ShowPremiumAvatarAnimation",
            ConfigItem.configTypeBool,
            true
        )
    val alwaysSaveChatOffset =
        addConfig(
            "AlwaysSaveChatOffset",
            ConfigItem.configTypeBool,
            true
        )
    val autoReplaceRepeat =
        addConfig(
            "AutoReplaceRepeat",
            ConfigItem.configTypeBool,
            true
        )
    val autoInsertGIFCaption =
        addConfig(
            "AutoInsertGIFCaption",
            ConfigItem.configTypeBool,
            true
        )
    val defaultMonoLanguage =
        addConfig(
            "DefaultMonoLanguage",
            ConfigItem.configTypeString,
            ""
        )
    val disableGlobalSearch =
        addConfig(
            "DisableGlobalSearch",
            ConfigItem.configTypeBool,
            false
        )
    val hideOriginAfterTranslation: ConfigItem =
        addConfig(
            "HideOriginAfterTranslation",
            ConfigItem.configTypeBool,
            false
        )
    val zalgoFilter =
        addConfig(
            "ZalgoFilter",
            ConfigItem.configTypeBool,
            false
        )
    val customChannelLabel =
        addConfig(
            "CustomChannelLabel",
            ConfigItem.configTypeString,
            LocaleController.getString(
                R.string.channelLabel
            )
        )
    val alwaysShowDownloadIcon =
        addConfig(
            "AlwaysShowDownloadIcon",
            ConfigItem.configTypeBool,
            false
        )
    val quickToggleAnonymous =
        addConfig(
            "QuickToggleAnonymous",
            ConfigItem.configTypeBool,
            false
        )
    val realHideTimeForSticker =
        addConfig(
            "RealHideTimeForSticker",
            ConfigItem.configTypeBool,
            false
        )
    val ignoreFolderCount =
        addConfig(
            "IgnoreFolderCount",
            ConfigItem.configTypeBool,
            false
        )
    val customArtworkApi =
        addConfig(
            "CustomArtworkApi",
            ConfigItem.configTypeString,
            ""
        )
    val customGreat =
        addConfig(
            "CustomGreat",
            ConfigItem.configTypeString,
            LocaleController.getString(
                R.string.Great
            )
        )
    val CustomPoor =
        addConfig(
            "CustomPoor",
            ConfigItem.configTypeString,
            LocaleController.getString(
                R.string.Poor
            )

        )
    val customEditedMessage =
        addConfig(
            "CustomEditedMessage",
            ConfigItem.configTypeString,
            ""
        )
    val showEditedIcon =
        addConfig(
            "ShowEditedIcon",
            ConfigItem.configTypeBool,
            false
        )
    val disableProxyWhenVpnEnabled =
        addConfig(
            "DisableProxyWhenVpnEnabled",
            ConfigItem.configTypeBool,
            false
        )
    val fakeHighPerformanceDevice =
        addConfig(
            "FakeHighPerformanceDevice",
            ConfigItem.configTypeBool,
            false
        )
    val disableEmojiDrawLimit =
        addConfig(
            "DisableEmojiDrawLimit",
            ConfigItem.configTypeBool,
            false
        )
    val iconDecoration =
        addConfig(
            "IconDecoration",
            ConfigItem.configTypeInt,
            0
        )
    val notificationIcon =
        addConfig(
            "NotificationIcon",
            ConfigItem.configTypeInt,
            1
        )
    val showSetReminder =
        addConfig(
            "SetReminder",
            ConfigItem.configTypeBool,
            false
        )
    val showOnlineStatus =
        addConfig(
            "ShowOnlineStatus",
            ConfigItem.configTypeBool,
            false
        )
    val showFullAbout =
        addConfig(
            "ShowFullAbout",
            ConfigItem.configTypeBool,
            false
        )
    val hideMessageSeenTooltip =
        addConfig(
            "HideMessageSeenTooltip",
            ConfigItem.configTypeBool,
            false
        )
    val autoTranslate =
        addConfig(
            "AutoTranslate",
            ConfigItem.configTypeBool,
            false
        )
    val typeMessageHintUseGroupName =
        addConfig(
            "TypeMessageHintUseGroupName",
            ConfigItem.configTypeBool,
            false
        )
    val showSendAsUnderMessageHint =
        addConfig(
            "ShowSendAsUnderMessageHint",
            ConfigItem.configTypeBool,
            false
        )
    val hideBotButtonInInputField =
        addConfig(
            "HideBotButtonInInputField",
            ConfigItem.configTypeBool,
            false
        )
    val chatDecoration =
        addConfig(
            "ChatDecoration",
            ConfigItem.configTypeInt,
            0
        )
    val doNotUnarchiveBySwipe =
        addConfig(
            "DoNotUnarchiveBySwipe",
            ConfigItem.configTypeBool,
            false
        )
    val doNotShareMyPhoneNumber =
        addConfig(
            "DoNotShareMyPhoneNumber",
            ConfigItem.configTypeBool,
            false
        )
    val defaultDeleteMenu =
        addConfig(
            "DefaultDeleteMenu",
            ConfigItem.configTypeInt,
            0
        )
    val defaultDeleteMenuBanUsers =
        addConfig(
            "DeleteBanUsers",
            defaultDeleteMenu,
            3,
            false
        )
    val defaultDeleteMenReportSpam =
        addConfig(
            "DeleteReportSpam",
            defaultDeleteMenu,
            2,
            false
        )
    val defaultDeleteMenuDeleteAll =
        addConfig(
            "DeleteAll",
            defaultDeleteMenu,
            1,
            false
        )
    val defaultDeleteMenuDoActionsInCommonGroups =
        addConfig(
            "DoActionsInCommonGroups",
            defaultDeleteMenu,
            0,
            false
        )
    val defaultDeleteMenuDeleteAllReactions =
        addConfig(
            "DeleteAllReactionsFromUsers",
            defaultDeleteMenu,
            4,
            false
        )
    val disableSuggestionView =
        addConfig(
            "DisableSuggestionView",
            ConfigItem.configTypeBool,
            false
        )
    val disableStories =
        addConfig(
            "DisableStories",
            ConfigItem.configTypeBool,
            false
        )
    val disableSendReadStories =
        addConfig(
            "DisableSendReadStories",
            ConfigItem.configTypeBool,
            false
        )
    val hideFilterMuteAll =
        addConfig(
            "HideFilterMuteAll",
            ConfigItem.configTypeBool,
            false
        )
    val useLocalQuoteColor =
        addConfig(
            "UseLocalQuoteColor",
            ConfigItem.configTypeBool,
            false
        )
    val useLocalQuoteColorData =
        addConfig(
            "useLocalQuoteColorData",
            ConfigItem.configTypeString,
            ""
        )
    val showRecentOnlineStatus =
        addConfig(
            "ShowRecentOnlineStatus",
            ConfigItem.configTypeBool,
            false
        )
    val showSquareAvatar =
        addConfig(
            "ShowSquareAvatar",
            ConfigItem.configTypeBool,
            false
        )
    val disableCustomWallpaperUser =
        addConfig(
            "DisableCustomWallpaperUser",
            ConfigItem.configTypeBool,
            false
        )
    val disableCustomWallpaperChannel =
        addConfig(
            "DisableCustomWallpaperChannel",
            ConfigItem.configTypeBool,
            false
        )
    val externalStickerCache =
        addConfig(
            "ExternalStickerCache",
            ConfigItem.configTypeString,
            ""
        )
    var externalStickerCacheUri: Uri?
        get() = externalStickerCache.String().let { if (it.isBlank()) return null else return it.toUri() }
        set(value) = externalStickerCache.setConfigString(value.toString())
    val externalStickerCacheAutoRefresh =
        addConfig(
            "ExternalStickerCacheAutoRefresh",
            ConfigItem.configTypeBool,
            false
        )
    val externalStickerCacheDirNameType =
        addConfig(
            "ExternalStickerCacheDirNameType",
            ConfigItem.configTypeInt,
            0
        )
    val disableMarkdown =
        addConfig(
            "DisableMarkdown",
            ConfigItem.configTypeBool,
            false
        )
    val newMarkdownParser =
        addConfig(
            "NewMarkdownParser",
            ConfigItem.configTypeBool,
            true
        )
    val markdownParseLinks =
        addConfig(
            "MarkdownParseLinks",
            ConfigItem.configTypeBool,
            true
        )
    val disableClickProfileGalleryView =
        addConfig(
            "DisableClickProfileGalleryView",
            ConfigItem.configTypeBool,
            false
        )
    val showSmallGIF =
        addConfig(
            "ShowSmallGIF",
            ConfigItem.configTypeBool,
            false
        )
    val disableClickCommandToSend =
        addConfig(
            "DisableClickCommandToSend",
            ConfigItem.configTypeBool,
            false
        )
    val disableDialogsFloatingButton =
        addConfig(
            "DisableDialogsFloatingButton",
            ConfigItem.configTypeBool,
            false
        )
    val disableFlagSecure =
        addConfig(
            "DisableFlagSecure",
            ConfigItem.configTypeBool,
            false
        )
    val centerActionBarTitle =
        addConfig(
            "CenterActionBarTitle",
            ConfigItem.configTypeBool,
            false
        )
    val showQuickReplyInBotCommands =
        addConfig(
            "ShowQuickReplyInBotCommands",
            ConfigItem.configTypeBool,
            false
        )
    val pushServiceType =
        addConfig(
            "PushServiceType",
            ConfigItem.configTypeInt,
            1
        )
    val pushServiceTypeInAppDialog =
        addConfig(
            "PushServiceTypeInAppDialog",
            ConfigItem.configTypeBool,
            true
        )
    val pushServiceTypeUnifiedGateway =
        addConfig(
            "PushServiceTypeUnifiedGateway",
            ConfigItem.configTypeString,
            "https://p2p.hoyolab.pp.ua/"
        )
    val pushServiceTypeUnifiedSimple =
        addConfig(
            "PushServiceTypeUnifiedSimple",
            ConfigItem.configTypeString,
            ""
        )
    val pushServiceTypeUnifiedWebPushPrivateKey =
        addConfig(
            "PushServiceTypeUnifiedWebPushPrivateKey",
            ConfigItem.configTypeString,
            ""
        )
    val pushServiceTypeUnifiedWebPushPublicKey =
        addConfig(
            "PushServiceTypeUnifiedWebPushPublicKey",
            ConfigItem.configTypeString,
            ""
        )
    val pushServiceTypeUnifiedWebPushAuthSecret =
        addConfig(
            "PushServiceTypeUnifiedWebPushAuthSecret",
            ConfigItem.configTypeString,
            ""
        )
    val sendMp4DocumentAsVideo =
        addConfig(
            "SendMp4DocumentAsVideo",
            ConfigItem.configTypeBool,
            false
        )
    val disableChannelMuteButton =
        addConfig(
            "DisableChannelMuteButton",
            ConfigItem.configTypeBool,
            false
        )
    val disablePreviewVideoSoundShortcut =
        addConfig(
            "DisablePreviewVideoSoundShortcut",
            ConfigItem.configTypeBool,
            false
        )
    val disableAutoWebLogin =
        addConfig(
            "DisableAutoWebLogin",
            ConfigItem.configTypeBool,
            false
        )
    val sentryAnalytics =
        addConfig(
            "SentryAnalytics",
            ConfigItem.configTypeBool,
            true
        )
    val regexFiltersEnabled =
        addConfig(
            "RegexFilters",
            ConfigItem.configTypeBool,
            false
        )
    val regexFiltersData =
        addConfig(
            "RegexFiltersData",
            ConfigItem.configTypeString,
            "[]"
        )
    val regexFiltersEnableInChats =
        addConfig(
            "RegexFiltersEnableInChats",
            ConfigItem.configTypeBool,
            true
        )
    val showTimeHint =
        addConfig(
            "ShowTimeHint",
            ConfigItem.configTypeBool,
            true
        )
    val showHiddenFeature =
        addConfig(
            "ShowHiddenFeature",
            ConfigItem.configTypeBool,
            false
        )
    val searchHashtagDefaultPageChannel =
        addConfig(
            "SearchHashtagDefaultPageChannel",
            ConfigItem.configTypeInt,
            0
        )
    val searchHashtagDefaultPageChat =
        addConfig(
            "SearchHashtagDefaultPageChat",
            ConfigItem.configTypeInt,
            0
        )
    val openUrlOutBotWebViewRegex =
        addConfig(
            "OpenUrlOutBotWebViewRegex",
            ConfigItem.configTypeString,
            ""
        )
    val enablePanguOnSending =
        addConfig(
            "EnablePanguOnSending",
            ConfigItem.configTypeBool,
            false
        )
    val enablePanguOnEditing =
        addConfig(
            "EnablePanguOnEditing",
            ConfigItem.configTypeBool,
            false
        )
    val enablePanguOnReceiving =
        addConfig(
            "EnablePanguOnReceiving",
            ConfigItem.configTypeBool,
            false
        )
    val defaultHlsVideoQuality =
        addConfig(
            "DefaultHlsVideoQuality",
            ConfigItem.configTypeInt,
            0
        )
    val disableBotOpenButton =
        addConfig(
            "DisableBotOpenButton",
            ConfigItem.configTypeBool,
            false
        )
    val customTitleUserName =
        addConfig(
            "CustomTitleUserName",
            ConfigItem.configTypeBool,
            false
        )
    val enhancedVideoBitrate =
        addConfig(
            "EnhancedVideoBitrate",
            ConfigItem.configTypeBool,
            false
        )
    private val disableTrendingFlags =
        addConfig(
            "DisableTrendingFlags",
            ConfigItem.configTypeInt,
            0
        )
    val disableStarsSubscription =
        addConfig(
            "DisableStarsSubscription",
            disableTrendingFlags,
            0,
            false
        )
    val disablePremiumExpiring =
        addConfig(
            "DisablePremiumExpiring",
            disableTrendingFlags,
            1,
            false
        )
    val disablePremiumUpgrade =
        addConfig(
            "DisablePremiumUpgrade",
            disableTrendingFlags,
            2,
            false
        )
    val disablePremiumChristmas =
        addConfig(
            "DisablePremiumChristmas",
            disableTrendingFlags,
            3,
            false
        )
    val disableBirthdayContact =
        addConfig(
            "DisableBirthdayContact",
            disableTrendingFlags,
            4,
            false
        )
    val disablePremiumRestore =
        addConfig(
            "DisablePremiumRestore",
            disableTrendingFlags,
            5,
            false
        )
    val disableFeatuerdEmojis =
        addConfig(
            "DisableFeatuerdEmojis",
            disableTrendingFlags,
            6,
            false
        )
    val disableFeaturedStickers =
        addConfig(
            "DisableFeaturedStickers",
            disableTrendingFlags,
            7,
            false
        )
    val disableFeaturedGifs =
        addConfig(
            "DisableFeaturedGifs",
            disableTrendingFlags,
            8,
            false
        )
    val disablePremiumFavoriteEmojiTags =
        addConfig(
            "DisablePremiumFavoriteEmojiTags",
            disableTrendingFlags,
            9,
            false
        )
    val disableFavoriteSearchEmojiTags =
        addConfig(
            "DisableFavoriteSearchEmojiTags",
            disableTrendingFlags,
            10,
            false
        )
    val disableNonPremiumChannelChatShow =
        addConfig(
            "DisableNonPremiumChannelChatShow",
            disableTrendingFlags,
            11,
            false
        )
    val disableShortcutTagActions =
        addConfig(
            "DisableShortcutTagActions",
            disableTrendingFlags,
            12,
            false
        )
    val disablePhoneSharePrompt =
        addConfig(
            "DisablePhoneSharePrompt",
            disableTrendingFlags,
            13,
            false
        )
    val disablePremiumSendTodo =
        addConfig(
            "DisablePremiumSendTodo",
            disableTrendingFlags,
            14,
            false
        )
    val disableEmptyStarButton =
        addConfig(
            "DisableEmptyStarButton",
            disableTrendingFlags,
            15,
            false
        )
    val disableGifts =
        addConfig(
            "DisableGifts",
            disableTrendingFlags,
            16,
            false
    )
    val disableRepeatInChannel =
        addConfig(
            "DisableRepeatInChannel",
            ConfigItem.configTypeBool,
            false
        )
    val disableActionBarButton =
        addConfig(
            "DisableActionBarButton",
            ConfigItem.configTypeInt,
            0
        )
    val disableActionBarButtonReply =
        addConfig(
            "Reply",
            disableActionBarButton,
            0,
            false
        )
    val disableActionBarButtonEdit =
        addConfig(
            "Edit",
            disableActionBarButton,
            1,
            false
        )
    val disableActionBarButtonSelectBetween =
        addConfig(
            "SelectBetween",
            disableActionBarButton,
            2,
            false
        )
    val disableActionBarButtonCopy =
        addConfig(
            "Copy",
            disableActionBarButton,
            3,
            false
        )
    val disableActionBarButtonForward =
        addConfig(
            "Forward",
            disableActionBarButton,
            4,
            false
        )
    val coloredAdminTitle =
        addConfig(
            "ColoredAdminTitle",
            ConfigItem.configTypeBool,
            false
        )
    val playerDecoder =
        addConfig(
            "PlayerDecoder",
            ConfigItem.configTypeInt,
            2
        )
    val showUserIconsInChatsList =
        addConfig(
            "ShowUserIconsInChatsList",
            ConfigItem.configTypeBool,
            false
        )
    val removeFavouriteStickersInRecentStickers =
        addConfig(
            "RemoveFavouriteStickersInRecentStickers",
            ConfigItem.configTypeBool,
            true
        )
    val showVoteCountBeforeVote =
        addConfig(
            "ShowVoteCountBeforeVote",
            ConfigItem.configTypeBool,
            false
        )
    val hideInstantCamera =
        addConfig(
            "HideInstantCamera",
            ConfigItem.configTypeBool,
            false
        )
    val useSystemAiService =
        addConfig(
            "UseSystemAiService",
            ConfigItem.configTypeBool,
            true
        )
    val navigationAnimationSpring =
        addConfig(
            "NavigationAnimationSpring",
            ConfigItem.configTypeBool,
            true
        )
    val forceEdgeToEdge =
        addConfig(
            "ForceEdgeToEdge",
            ConfigItem.configTypeBool,
            false
        )
    val useSystemPhotoPicker =
        addConfig(
            "UseSystemPhotoPicker",
            ConfigItem.configTypeBool,
            false
        )
    var tabStyle =
        addConfig(
            "TabStyle",
            ConfigItem.configTypeInt,
            0
        )
    val chatActivityNavbarTransparent =
        addConfig(
            "ChatActivityNavbarTransparent",
            ConfigItem.configTypeBool,
            false
        )
    val fixUrlPagePreview =
        addConfig(
            "FixUrlPagePreview",
            ConfigItem.configTypeBool,
            true
        )
    val fixUrlAutoInlineBot =
        addConfig(
            "FixUrlAutoInlineBot",
            ConfigItem.configTypeBool,
            true
        )
    val localInlineBotRulesData =
        addConfig(
            "LocalInlineBotRulesData",
            ConfigItem.configTypeString,
            ""
        )
    val localInlineBotRulesEnabled =
        addConfig(
            "LocalInlineBotRulesEnabled",
            ConfigItem.configTypeString,
            ""
        )
    val disabledRemoteInlineBotRules =
        addConfig(
            "DisabledRemoteInlineBotRules",
            ConfigItem.configTypeString,
            ""
        )
    val fixUrlAutoInlineBotSkipMediaPreview =
        addConfig(
            "FixUrlAutoInlineBotSkipMediaPreview",
            ConfigItem.configTypeBool,
            false
        )
    val deepLxCustomApi =
        addConfig(
            "DeepLxCustomApi",
            ConfigItem.configTypeString,
            ""
        )
    val deepLFormality =
        addConfig(
            "DeepLFormality",
            ConfigItem.configTypeInt,
            0 // 0: default, 1: more formal, 2: less formal
        )
    val deepLApiKey =
        addConfig(
            "DeepLApiKey",
            ConfigItem.configTypeString,
            ""
        )
    val deepLFreeApiKey =
        addConfig(
            "DeepLFreeApiKey",
            ConfigItem.configTypeString,
            ""
        )
    val summarizeTextButton =
        addConfig(
            "SummarizeTextButton",
            ConfigItem.configTypeInt,
            0
        )
    val disablePredictiveBackAnimation =
        addConfig(
            "DisablePredictiveBackAnimation",
            ConfigItem.configTypeBool,
            false
        )
    val llmProvider =
        addConfig(
            "LLMProvider",
            ConfigItem.configTypeInt,
            0
        )
    val llmApiFormat =
        addConfig(
            "LLMApiFormat",
            ConfigItem.configTypeInt,
            0
        )
    val llmApiKeys =
        addConfig(
            "LLMApiKeys",
            ConfigItem.configTypeString,
            ""
        )
    val llmApiUrl =
        addConfig(
            "LLMApiUrl",
            ConfigItem.configTypeString,
            "https://api.openai.com/v1/chat/completions"
        )
    val llmOpenAIModel =
        addConfig(
            "LLMOpenAIModel",
            ConfigItem.configTypeString,
            "gpt-4.1-mini"
        )
    val llmGeminiModel =
        addConfig(
            "LLMGeminiModel",
            ConfigItem.configTypeString,
            "gemini-2.5-flash"
        )
    val llmGroqModel =
        addConfig(
            "LLMGroqModel",
            ConfigItem.configTypeString,
            "llama-3.3-70b-versatile"
        )
    val llmDeepSeekModel =
        addConfig(
            "LLMDeepSeekModel",
            ConfigItem.configTypeString,
            "deepseek-chat"
        )
    val llmXAIModel =
        addConfig(
            "LLMXAIModel",
            ConfigItem.configTypeString,
            "grok-3-mini-fast"
        )
    val llmZhipuAIModel =
        addConfig(
            "LLMZhipuAIModel",
            ConfigItem.configTypeString,
            "GLM-4-Flash"
        )
    val llmMistralModel =
        addConfig(
            "LLMMistralModel",
            ConfigItem.configTypeString,
            "mistral-small-latest"
        )
    val llmOpenRouterModel =
        addConfig(
            "LLMOpenRouterModel",
            ConfigItem.configTypeString,
            "meta-llama/llama-3.3-70b-instruct"
        )
    val llmQwenModel =
        addConfig(
            "LLMQwenModel",
            ConfigItem.configTypeString,
            "qwen-turbo-latest"
        )
    val llmMoonshotModel =
        addConfig(
            "LLMMoonshotModel",
            ConfigItem.configTypeString,
            "moonshot-v1-8k"
        )
    val llmSiliconFlowModel =
        addConfig(
            "LLMSiliconFlowModel",
            ConfigItem.configTypeString,
            "Qwen/Qwen2.5-7B-Instruct"
        )
    val llmCustomModel =
        addConfig(
            "LLMCustomModel",
            ConfigItem.configTypeString,
            ""
        )
    val llmSystemPrompt =
        addConfig(
            "LLMSystemPrompt",
            ConfigItem.configTypeString,
            ""
        )
    val llmTranslationPrompt =
        addConfig(
            "LLMTranslationPrompt",
            ConfigItem.configTypeString,
            ""
        )
    val llmUseContext =
        addConfig(
            "LLMUseContext",
            ConfigItem.configTypeBool,
            false
        )
    val llmTemperature =
        addConfig(
            "LLMTemperature",
            ConfigItem.configTypeString,
            "0.7"
        )
    val mainTabsStyle =
        addConfig(
            "MainTabsStyle",
            ConfigItem.configTypeInt,
            0
        )
    val hideTabBarPermissionWarnings =
        addConfig(
            "HideTabBarPermissionWarnings",
            ConfigItem.configTypeBool,
            false
        )
    val showRecentChatsOnTabLongPress =
        addConfig(
            "ShowRecentChatsOnTabLongPress",
            ConfigItem.configTypeBool,
            false
        )
    val customIpStrategy =
        addConfig(
            "CustomIpStrategy",
            ConfigItem.configTypeInt,
            0
        )
    val customDialogsMenu =
        addConfig(
            "CustomDialogsMenu",
            ConfigItem.configTypeInt,
            59
        )
    val customDialogsMenuTheme =
        addConfig(
            "SwitchThemeToDay",
            customDialogsMenu,
            0,
            true
        )
    val showRecentChatsInSidebar =
        addConfig(
            "ShowRecentChatsInSidebar",
            ConfigItem.configTypeBool,
            true
        )
    val customDialogsMenuNewGroup =
        addConfig(
            "NewGroup",
            customDialogsMenu,
            1,
            true
        )
    val customDialogsMenuNewMessage =
        addConfig(
            "NewMessageTitle",
            customDialogsMenu,
            2,
            false
        )
    val customDialogsMenuSavedMessages =
        addConfig(
            "SavedMessages",
            customDialogsMenu,
            3,
            true
        )
    val customDialogsMenuSettings =
        addConfig(
            "Settings",
            customDialogsMenu,
            4,
            true
        )
    val customDialogsMenuProxy =
        addConfig(
            "MenuProxyTitle",
            customDialogsMenu,
            5,
            true
        )
    val customDialogsMenuAccount =
        addConfig(
            "AddAccount",
            customDialogsMenu,
            6,
            false
        )
    val customDialogsMenuMyProfile =
        addConfig(
            "MyProfile",
            customDialogsMenu,
            7,
            false
        )
    val sidebarSettingsActivity =
        addConfig(
            "SidebarSettingsActivity",
            ConfigItem.configTypeBool,
            true
        )
    val compactMessageMenuOptions =
        addConfig(
            "CompactMessageMenuOptions",
            ConfigItem.configTypeString,
            ""
        )
    val hiddenMessageMenuOptions =
        addConfig(
            "HiddenMessageMenuOptions",
            ConfigItem.configTypeString,
            ""
        )
    val showRecentForwardTab =
        addConfig(
            "ShowRecentForwardTab",
            ConfigItem.configTypeBool,
            false
        )
    val disableProfileAvatarBlur =
        addConfig(
            "DisableProfileAvatarBlur",
            ConfigItem.configTypeBool,
            false
        )
    val extendedFeatureUnlockedToken =
        addConfig(
            "ExtendedFeatureUnlockedToken",
            ConfigItem.configTypeString,
            ""
        )
    val disableAiEditor =
        addConfig(
            "DisableAiEditor",
            ConfigItem.configTypeBool,
            false
        )
    val disableGlareEffects =
        addConfig(
            "DisableGlareEffects",
            ConfigItem.configTypeBool,
            false
        )
    val liquidGlassAngle =
        addConfig(
            "LiquidGlassAngle",
            ConfigItem.configTypeInt,
            0
    )
    val liquidGlassIntensity =
        addConfig(
            "LiquidGlassIntensity",
            ConfigItem.configTypeInt,
            75
        )
    val disableGooeyAvatarAnimation =
        addConfig(
            "DisableGooeyAvatarAnimation",
            ConfigItem.configTypeBool,
            false
        )
    val inputTextAnimations = addConfig("InputTextAnimations", ConfigItem.configTypeBool, false)
    val inputAnimationStrength = addConfig("InputAnimationStrength", ConfigItem.configTypeInt, 2)
    val forceVideoNewRewindMethod =
        addConfig(
            "ForceVideoNewRewindMethod",
            ConfigItem.configTypeBool,
            false
        )
    val tabStyleStroke =
        addConfig(
            "TabStyleStroke",
            ConfigItem.configTypeBool,
            false
        )
    val addCommaAfterMention =
        addConfig(
            "AddCommaAfterMention",
            ConfigItem.configTypeBool,
            false
        )
    val useSystemFontInTitle =
        addConfig(
            "UseSystemFontInTitle",
            ConfigItem.configTypeBool,
            false
        )
    val materialDesign3ChatHeader =
        addConfig(
            "MaterialDesign3ChatHeader",
            ConfigItem.configTypeBool,
            false
        )
    val switchStyle =
        addConfig(
            "SwitchStyle",
            ConfigItem.configTypeInt,
            SwitchStyle.TELEGRAM.value
        )
    val disableChatListSwipeGesture =
        addConfig(
            "DisableChatListSwipeGesture",
            ConfigItem.configTypeBool,
            false
        )
    val globalDisableLinkPreviews =
        addConfig(
            "GlobalDisableLinkPreviews",
            ConfigItem.configTypeBool,
            false
        )
    val alwaysSendVideosInHD =
        addConfig(
            "AlwaysSendVideosInHD",
            ConfigItem.configTypeBool,
            false
        )
    val showChannelIcon =
        addConfig(
            "ShowChannelIcon",
            ConfigItem.configTypeBool,
            false
        )
    val hideSideShareButton =
        addConfig(
            "HideSideShareButton",
            ConfigItem.configTypeBool,
            false
        )
    val askBeforeSendingSticker =
        addConfig(
            "AskBeforeSendingSticker",
            ConfigItem.configTypeBool,
            false
        )
    val keepFormatting =
        addConfig(
            "KeepFormatting",
            ConfigItem.configTypeBool,
            true
        )
    val askBeforeSendingGIF =
        addConfig(
            "AskBeforeSendingGIF",
            ConfigItem.configTypeBool,
            false
        )
    val disableCommunityChatGrouping =
        addConfig(
            "DisableCommunityChatGrouping",
            ConfigItem.configTypeBool,
            false
        )
    val communityAvatarTapAction =
        addConfig(
            "CommunityAvatarTapAction",
            ConfigItem.configTypeInt,
            1
        )
    val useSystemNotificationColor =
        addConfig(
            "UseSystemNotificationColor",
            ConfigItem.configTypeBool,
            false
        )
    val adminActionsOnAvatarLongPress =
        addConfig(
            "AdminActionsOnAvatarLongPress",
            ConfigItem.configTypeBool,
            false
        )
    val adminActionsOnMentionLongPress =
        addConfig(
            "AdminActionsOnMentionLongPress",
            ConfigItem.configTypeBool,
            false
        )
    val deleteMessagesWhenRemovingMembers =
        addConfig(
            "DeleteMessagesWhenRemovingMembers",
            ConfigItem.configTypeBool,
            false
        )

    val switch1 =
        addConfig(
            "Switch1",
            ConfigItem.configTypeBool,
            false
        )
    val switch2 =
        addConfig(
            "Switch2",
            ConfigItem.configTypeBool,
            false
        )
    val switch3 =
        addConfig(
            "Switch3",
            ConfigItem.configTypeBool,
            false
        )
    val switch4 =
        addConfig(
            "Switch4",
            ConfigItem.configTypeBool,
            false
        )
    val switch5 =
        addConfig(
            "Switch5",
            ConfigItem.configTypeBool,
            false
        )
    val switch6 =
        addConfig(
            "Switch6",
            ConfigItem.configTypeBool,
            false
        )
    val switch7 =
        addConfig(
            "Switch7",
            ConfigItem.configTypeBool,
            false
        )
    val switch8 =
        addConfig(
            "Switch8",
            ConfigItem.configTypeBool,
            false
        )
    val switch9 =
        addConfig(
            "Switch9",
            ConfigItem.configTypeBool,
            false
        )
    val switch10 =
        addConfig(
            "Switch10",
            ConfigItem.configTypeBool,
            false
        )
    val switch11 =
        addConfig(
            "Switch11",
            ConfigItem.configTypeBool,
            false
        )
    val switch12 =
        addConfig(
            "Switch12",
            ConfigItem.configTypeBool,
            false
        )
    val switch13 =
        addConfig(
            "Switch13",
            ConfigItem.configTypeBool,
            false
        )
    val switch14 =
        addConfig(
            "Switch14",
            ConfigItem.configTypeBool,
            false
        )
    val switch15 =
        addConfig(
            "Switch15",
            ConfigItem.configTypeBool,
            false
        )
    val switch16 =
        addConfig(
            "Switch16",
            ConfigItem.configTypeBool,
            false
        )
    val switch17 =
        addConfig(
            "Switch17",
            ConfigItem.configTypeBool,
            false
        )
    val switch18 =
        addConfig(
            "Switch18",
            ConfigItem.configTypeBool,
            false
        )
    val switch19 =
        addConfig(
            "Switch19",
            ConfigItem.configTypeBool,
            false
        )
    val switch20 =
        addConfig(
            "Switch20",
            ConfigItem.configTypeBool,
            false
        )
    val switch21 =
        addConfig(
            "Switch21",
            ConfigItem.configTypeBool,
            false
        )
    val switch22 =
        addConfig(
            "Switch22",
            ConfigItem.configTypeBool,
            false
        )
    val switch23 =
        addConfig(
            "Switch23",
            ConfigItem.configTypeBool,
            false
        )
    val switch24 =
        addConfig(
            "Switch24",
            ConfigItem.configTypeBool,
            false
        )
    val switch25 =
        addConfig(
            "Switch25",
            ConfigItem.configTypeBool,
            false
        )
    val switch26 =
        addConfig(
            "Switch26",
            ConfigItem.configTypeBool,
            false
        )
    val switch27 =
        addConfig(
            "Switch27",
            ConfigItem.configTypeBool,
            false
        )
    val switch28 =
        addConfig(
            "Switch28",
            ConfigItem.configTypeBool,
            false
        )
    val switch29 =
        addConfig(
            "Switch29",
            ConfigItem.configTypeBool,
            false
        )
    val switch30 =
        addConfig(
            "Switch30",
            ConfigItem.configTypeBool,
            false
        )
    val switch31 =
        addConfig(
            "Switch31",
            ConfigItem.configTypeBool,
            false
        )
    val switch32 =
        addConfig(
            "Switch32",
            ConfigItem.configTypeBool,
            false
        )
    val switch33 =
        addConfig(
            "Switch33",
            ConfigItem.configTypeBool,
            false
        )
    val switch34 =
        addConfig(
            "Switch34",
            ConfigItem.configTypeBool,
            false
        )
    val switch35 =
        addConfig(
            "Switch35",
            ConfigItem.configTypeBool,
            false
        )
    val switch36 =
        addConfig(
            "Switch36",
            ConfigItem.configTypeBool,
            false
        )
    val switch37 =
        addConfig(
            "Switch37",
            ConfigItem.configTypeBool,
            false
        )
    val switch38 =
        addConfig(
            "Switch38",
            ConfigItem.configTypeBool,
            false
        )
    val switch39 =
        addConfig(
            "Switch39",
            ConfigItem.configTypeBool,
            false
        )
    val switch40 =
        addConfig(
            "Switch40",
            ConfigItem.configTypeBool,
            false
        )
    val switch41 =
        addConfig(
            "Switch41",
            ConfigItem.configTypeBool,
            false
        )
    val switch42 =
        addConfig(
            "Switch42",
            ConfigItem.configTypeBool,
            false
        )
    val switch43 =
        addConfig(
            "Switch43",
            ConfigItem.configTypeBool,
            false
        )
    val switch44 =
        addConfig(
            "Switch44",
            ConfigItem.configTypeBool,
            false
        )
    val switch45 =
        addConfig(
            "Switch45",
            ConfigItem.configTypeBool,
            false
        )
    val switch46 =
        addConfig(
            "Switch46",
            ConfigItem.configTypeBool,
            false
        )
    val switch47 =
        addConfig(
            "Switch47",
            ConfigItem.configTypeBool,
            false
        )
    val switch48 =
        addConfig(
            "Switch48",
            ConfigItem.configTypeBool,
            false
        )
    val switch49 =
        addConfig(
            "Switch49",
            ConfigItem.configTypeBool,
            false
        )
    val switch50 =
        addConfig(
            "Switch50",
            ConfigItem.configTypeBool,
            false
        )
    val switch51 =
        addConfig(
            "Switch51",
            ConfigItem.configTypeBool,
            false
        )
    val switch52 =
        addConfig(
            "Switch52",
            ConfigItem.configTypeBool,
            false
        )
    val switch53 =
        addConfig(
            "Switch53",
            ConfigItem.configTypeBool,
            false
        )
    val switch54 =
        addConfig(
            "Switch54",
            ConfigItem.configTypeBool,
            false
        )
    val switch55 =
        addConfig(
            "Switch55",
            ConfigItem.configTypeBool,
            false
        )
    val switch56 =
        addConfig(
            "Switch56",
            ConfigItem.configTypeBool,
            false
        )
    val switch57 =
        addConfig(
            "Switch57",
            ConfigItem.configTypeBool,
            false
        )
    val switch58 =
        addConfig(
            "Switch58",
            ConfigItem.configTypeBool,
            false
        )
    val switch59 =
        addConfig(
            "Switch59",
            ConfigItem.configTypeBool,
            false
        )
    val switch60 =
        addConfig(
            "Switch60",
            ConfigItem.configTypeBool,
            false
        )
    val switch61 =
        addConfig(
            "Switch61",
            ConfigItem.configTypeBool,
            false
        )
    val switch62 =
        addConfig(
            "Switch62",
            ConfigItem.configTypeBool,
            false
        )
    val switch63 =
        addConfig(
            "Switch63",
            ConfigItem.configTypeBool,
            false
        )
    val switch64 =
        addConfig(
            "Switch64",
            ConfigItem.configTypeBool,
            false
        )
    val switch65 =
        addConfig(
            "Switch65",
            ConfigItem.configTypeBool,
            false
        )
    val switch66 =
        addConfig(
            "Switch66",
            ConfigItem.configTypeBool,
            false
        )
    val switch67 =
        addConfig(
            "Switch67",
            ConfigItem.configTypeBool,
            false
        )
    val switch68 =
        addConfig(
            "Switch68",
            ConfigItem.configTypeBool,
            false
        )
    val switch69 =
        addConfig(
            "Switch69",
            ConfigItem.configTypeBool,
            false
        )
    val switch70 =
        addConfig(
            "Switch70",
            ConfigItem.configTypeBool,
            false
        )
    val switch71 =
        addConfig(
            "Switch71",
            ConfigItem.configTypeBool,
            false
        )
    val switch72 =
        addConfig(
            "Switch72",
            ConfigItem.configTypeBool,
            false
        )
    val switch73 =
        addConfig(
            "Switch73",
            ConfigItem.configTypeBool,
            false
        )
    val switch74 =
        addConfig(
            "Switch74",
            ConfigItem.configTypeBool,
            false
        )
    val switch75 =
        addConfig(
            "Switch75",
            ConfigItem.configTypeBool,
            false
        )
    val switch76 =
        addConfig(
            "Switch76",
            ConfigItem.configTypeBool,
            false
        )
    val switch77 =
        addConfig(
            "Switch77",
            ConfigItem.configTypeBool,
            false
        )
    val switch78 =
        addConfig(
            "Switch78",
            ConfigItem.configTypeBool,
            false
        )
    val switch79 =
        addConfig(
            "Switch79",
            ConfigItem.configTypeBool,
            false
        )
    val switch80 =
        addConfig(
            "Switch80",
            ConfigItem.configTypeBool,
            false
        )
    val switch81 =
        addConfig(
            "Switch81",
            ConfigItem.configTypeBool,
            false
        )
    val switch82 =
        addConfig(
            "Switch82",
            ConfigItem.configTypeBool,
            false
        )
    val switch83 =
        addConfig(
            "Switch83",
            ConfigItem.configTypeBool,
            false
        )
    val switch84 =
        addConfig(
            "Switch84",
            ConfigItem.configTypeBool,
            false
        )
    val switch85 =
        addConfig(
            "Switch85",
            ConfigItem.configTypeBool,
            false
        )
    val switch86 =
        addConfig(
            "Switch86",
            ConfigItem.configTypeBool,
            false
        )
    val switch87 =
        addConfig(
            "Switch87",
            ConfigItem.configTypeBool,
            false
        )
    val switch88 =
        addConfig(
            "Switch88",
            ConfigItem.configTypeBool,
            false
        )
    val switch89 =
        addConfig(
            "Switch89",
            ConfigItem.configTypeBool,
            false
        )
    val switch90 =
        addConfig(
            "Switch90",
            ConfigItem.configTypeBool,
            false
        )
    val switch91 =
        addConfig(
            "Switch91",
            ConfigItem.configTypeBool,
            false
        )
    val switch92 =
        addConfig(
            "Switch92",
            ConfigItem.configTypeBool,
            false
        )
    val switch93 =
        addConfig(
            "Switch93",
            ConfigItem.configTypeBool,
            false
        )
    val switch94 =
        addConfig(
            "Switch94",
            ConfigItem.configTypeBool,
            false
        )
    val switch95 =
        addConfig(
            "Switch95",
            ConfigItem.configTypeBool,
            false
        )
    val switch96 =
        addConfig(
            "Switch96",
            ConfigItem.configTypeBool,
            false
        )
    val switch97 =
        addConfig(
            "Switch97",
            ConfigItem.configTypeBool,
            false
        )
    val switch98 =
        addConfig(
            "Switch98",
            ConfigItem.configTypeBool,
            false
        )
    val switch99 =
        addConfig(
            "Switch99",
            ConfigItem.configTypeBool,
            false
        )
    val switch100 =
        addConfig(
            "Switch100",
            ConfigItem.configTypeBool,
            false
        )
    val switch101 =
        addConfig(
            "Switch101",
            ConfigItem.configTypeBool,
            false
        )
    val switch102 =
        addConfig(
            "Switch102",
            ConfigItem.configTypeBool,
            false
        )
    val switch103 =
        addConfig(
            "Switch103",
            ConfigItem.configTypeBool,
            false
        )
    val switch104 =
        addConfig(
            "Switch104",
            ConfigItem.configTypeBool,
            false
        )
    val switch105 =
        addConfig(
            "Switch105",
            ConfigItem.configTypeBool,
            false
        )
    val switch106 =
        addConfig(
            "Switch106",
            ConfigItem.configTypeBool,
            false
        )
    val switch107 =
        addConfig(
            "Switch107",
            ConfigItem.configTypeBool,
            false
        )
    val switch108 =
        addConfig(
            "Switch108",
            ConfigItem.configTypeBool,
            false
        )
    val switch109 =
        addConfig(
            "Switch109",
            ConfigItem.configTypeBool,
            false
        )
    val switch110 =
        addConfig(
            "Switch110",
            ConfigItem.configTypeBool,
            false
        )
    val switch111 =
        addConfig(
            "Switch111",
            ConfigItem.configTypeBool,
            false
        )
    val switch112 =
        addConfig(
            "Switch112",
            ConfigItem.configTypeBool,
            false
        )
    val switch113 =
        addConfig(
            "Switch113",
            ConfigItem.configTypeBool,
            false
        )
    val switch114 =
        addConfig(
            "Switch114",
            ConfigItem.configTypeBool,
            false
        )
    val switch115 =
        addConfig(
            "Switch115",
            ConfigItem.configTypeBool,
            false
        )
    val switch116 =
        addConfig(
            "Switch116",
            ConfigItem.configTypeBool,
            false
        )
    val switch117 =
        addConfig(
            "Switch117",
            ConfigItem.configTypeBool,
            false
        )
    val switch118 =
        addConfig(
            "Switch118",
            ConfigItem.configTypeBool,
            false
        )
    val switch119 =
        addConfig(
            "Switch119",
            ConfigItem.configTypeBool,
            false
        )
    val switch120 =
        addConfig(
            "Switch120",
            ConfigItem.configTypeBool,
            false
        )
    val switch121 =
        addConfig(
            "Switch121",
            ConfigItem.configTypeBool,
            false
        )
    val switch122 =
        addConfig(
            "Switch122",
            ConfigItem.configTypeBool,
            false
        )
    val switch123 =
        addConfig(
            "Switch123",
            ConfigItem.configTypeBool,
            false
        )
    val switch124 =
        addConfig(
            "Switch124",
            ConfigItem.configTypeBool,
            false
        )
    val switch125 =
        addConfig(
            "Switch125",
            ConfigItem.configTypeBool,
            false
        )
    val switch126 =
        addConfig(
            "Switch126",
            ConfigItem.configTypeBool,
            false
        )
    val switch127 =
        addConfig(
            "Switch127",
            ConfigItem.configTypeBool,
            false
        )
    val switch128 =
        addConfig(
            "Switch128",
            ConfigItem.configTypeBool,
            false
        )
    val switch129 =
        addConfig(
            "Switch129",
            ConfigItem.configTypeBool,
            false
        )
    val switch130 =
        addConfig(
            "Switch130",
            ConfigItem.configTypeBool,
            false
        )
    val switch131 =
        addConfig(
            "Switch131",
            ConfigItem.configTypeBool,
            false
        )
    val switch132 =
        addConfig(
            "Switch132",
            ConfigItem.configTypeBool,
            false
        )
    val switch133 =
        addConfig(
            "Switch133",
            ConfigItem.configTypeBool,
            false
        )
    val switch134 =
        addConfig(
            "Switch134",
            ConfigItem.configTypeBool,
            false
        )
    val switch135 =
        addConfig(
            "Switch135",
            ConfigItem.configTypeBool,
            false
        )
    val switch136 =
        addConfig(
            "Switch136",
            ConfigItem.configTypeBool,
            false
        )
    val switch137 =
        addConfig(
            "Switch137",
            ConfigItem.configTypeBool,
            false
        )
    val switch138 =
        addConfig(
            "Switch138",
            ConfigItem.configTypeBool,
            false
        )
    val switch139 =
        addConfig(
            "Switch139",
            ConfigItem.configTypeBool,
            false
        )
    val switch140 =
        addConfig(
            "Switch140",
            ConfigItem.configTypeBool,
            false
        )
    val switch141 =
        addConfig(
            "Switch141",
            ConfigItem.configTypeBool,
            false
        )
    val switch142 =
        addConfig(
            "Switch142",
            ConfigItem.configTypeBool,
            false
        )
    val switch143 =
        addConfig(
            "Switch143",
            ConfigItem.configTypeBool,
            false
        )
    val switch144 =
        addConfig(
            "Switch144",
            ConfigItem.configTypeBool,
            false
        )
    val switch145 =
        addConfig(
            "Switch145",
            ConfigItem.configTypeBool,
            false
        )
    val switch146 =
        addConfig(
            "Switch146",
            ConfigItem.configTypeBool,
            false
        )
    val switch147 =
        addConfig(
            "Switch147",
            ConfigItem.configTypeBool,
            false
        )
    val switch148 =
        addConfig(
            "Switch148",
            ConfigItem.configTypeBool,
            false
        )
    val switch149 =
        addConfig(
            "Switch149",
            ConfigItem.configTypeBool,
            false
        )
    val switch150 =
        addConfig(
            "Switch150",
            ConfigItem.configTypeBool,
            false
        )
    val switch151 =
        addConfig(
            "Switch151",
            ConfigItem.configTypeBool,
            false
        )
    val switch152 =
        addConfig(
            "Switch152",
            ConfigItem.configTypeBool,
            false
        )
    val switch153 =
        addConfig(
            "Switch153",
            ConfigItem.configTypeBool,
            false
        )
    val switch154 =
        addConfig(
            "Switch154",
            ConfigItem.configTypeBool,
            false
        )
    val switch155 =
        addConfig(
            "Switch155",
            ConfigItem.configTypeBool,
            false
        )
    val switch156 =
        addConfig(
            "Switch156",
            ConfigItem.configTypeBool,
            false
        )
    val switch157 =
        addConfig(
            "Switch157",
            ConfigItem.configTypeBool,
            false
        )
    val switch158 =
        addConfig(
            "Switch158",
            ConfigItem.configTypeBool,
            false
        )
    val switch159 =
        addConfig(
            "Switch159",
            ConfigItem.configTypeBool,
            false
        )
    val switch160 =
        addConfig(
            "Switch160",
            ConfigItem.configTypeBool,
            false
        )
    val switch161 =
        addConfig(
            "Switch161",
            ConfigItem.configTypeBool,
            false
        )
    val switch162 =
        addConfig(
            "Switch162",
            ConfigItem.configTypeBool,
            false
        )
    val switch163 =
        addConfig(
            "Switch163",
            ConfigItem.configTypeBool,
            false
        )
    val switch164 =
        addConfig(
            "Switch164",
            ConfigItem.configTypeBool,
            false
        )
    val switch165 =
        addConfig(
            "Switch165",
            ConfigItem.configTypeBool,
            false
        )
    val switch166 =
        addConfig(
            "Switch166",
            ConfigItem.configTypeBool,
            false
        )
    val switch167 =
        addConfig(
            "Switch167",
            ConfigItem.configTypeBool,
            false
        )
    val switch168 =
        addConfig(
            "Switch168",
            ConfigItem.configTypeBool,
            false
        )
    val switch169 =
        addConfig(
            "Switch169",
            ConfigItem.configTypeBool,
            false
        )
    val switch170 =
        addConfig(
            "Switch170",
            ConfigItem.configTypeBool,
            false
        )
    val switch171 =
        addConfig(
            "Switch171",
            ConfigItem.configTypeBool,
            false
        )
    val switch172 =
        addConfig(
            "Switch172",
            ConfigItem.configTypeBool,
            false
        )
    val switch173 =
        addConfig(
            "Switch173",
            ConfigItem.configTypeBool,
            false
        )
    val switch174 =
        addConfig(
            "Switch174",
            ConfigItem.configTypeBool,
            false
        )
    val switch175 =
        addConfig(
            "Switch175",
            ConfigItem.configTypeBool,
            false
        )
    val switch176 =
        addConfig(
            "Switch176",
            ConfigItem.configTypeBool,
            false
        )
    val switch177 =
        addConfig(
            "Switch177",
            ConfigItem.configTypeBool,
            false
        )
    val switch178 =
        addConfig(
            "Switch178",
            ConfigItem.configTypeBool,
            false
        )
    val switch179 =
        addConfig(
            "Switch179",
            ConfigItem.configTypeBool,
            false
        )
    val switch180 =
        addConfig(
            "Switch180",
            ConfigItem.configTypeBool,
            false
        )
    val switch181 =
        addConfig(
            "Switch181",
            ConfigItem.configTypeBool,
            false
        )
    val switch182 =
        addConfig(
            "Switch182",
            ConfigItem.configTypeBool,
            false
        )
    val switch183 =
        addConfig(
            "Switch183",
            ConfigItem.configTypeBool,
            false
        )
    val switch184 =
        addConfig(
            "Switch184",
            ConfigItem.configTypeBool,
            false
        )
    val switch185 =
        addConfig(
            "Switch185",
            ConfigItem.configTypeBool,
            false
        )
    val switch186 =
        addConfig(
            "Switch186",
            ConfigItem.configTypeBool,
            false
        )
    val switch187 =
        addConfig(
            "Switch187",
            ConfigItem.configTypeBool,
            false
        )
    val switch188 =
        addConfig(
            "Switch188",
            ConfigItem.configTypeBool,
            false
        )
    val switch189 =
        addConfig(
            "Switch189",
            ConfigItem.configTypeBool,
            false
        )
    val switch190 =
        addConfig(
            "Switch190",
            ConfigItem.configTypeBool,
            false
        )
    val switch191 =
        addConfig(
            "Switch191",
            ConfigItem.configTypeBool,
            false
        )
    val switch192 =
        addConfig(
            "Switch192",
            ConfigItem.configTypeBool,
            false
        )
    val switch193 =
        addConfig(
            "Switch193",
            ConfigItem.configTypeBool,
            false
        )
    val switch194 =
        addConfig(
            "Switch194",
            ConfigItem.configTypeBool,
            false
        )
    val switch195 =
        addConfig(
            "Switch195",
            ConfigItem.configTypeBool,
            false
        )
    val switch196 =
        addConfig(
            "Switch196",
            ConfigItem.configTypeBool,
            false
        )
    val switch197 =
        addConfig(
            "Switch197",
            ConfigItem.configTypeBool,
            false
        )
    val switch198 =
        addConfig(
            "Switch198",
            ConfigItem.configTypeBool,
            false
        )
    val switch199 =
        addConfig(
            "Switch199",
            ConfigItem.configTypeBool,
            false
        )
    val switch200 =
        addConfig(
            "Switch200",
            ConfigItem.configTypeBool,
            false
        )
    val switch201 =
        addConfig(
            "Switch201",
            ConfigItem.configTypeBool,
            false
        )
    val switch202 =
        addConfig(
            "Switch202",
            ConfigItem.configTypeBool,
            false
        )
    val switch203 =
        addConfig(
            "Switch203",
            ConfigItem.configTypeBool,
            false
        )
    val switch204 =
        addConfig(
            "Switch204",
            ConfigItem.configTypeBool,
            false
        )
    val switch205 =
        addConfig(
            "Switch205",
            ConfigItem.configTypeBool,
            false
        )
    val switch206 =
        addConfig(
            "Switch206",
            ConfigItem.configTypeBool,
            false
        )
    val switch207 =
        addConfig(
            "Switch207",
            ConfigItem.configTypeBool,
            false
        )
    val switch208 =
        addConfig(
            "Switch208",
            ConfigItem.configTypeBool,
            false
        )
    val switch209 =
        addConfig(
            "Switch209",
            ConfigItem.configTypeBool,
            false
        )
    val switch210 =
        addConfig(
            "Switch210",
            ConfigItem.configTypeBool,
            false
        )
    val switch211 =
        addConfig(
            "Switch211",
            ConfigItem.configTypeBool,
            false
        )
    val switch212 =
        addConfig(
            "Switch212",
            ConfigItem.configTypeBool,
            false
        )
    val switch213 =
        addConfig(
            "Switch213",
            ConfigItem.configTypeBool,
            false
        )
    val switch214 =
        addConfig(
            "Switch214",
            ConfigItem.configTypeBool,
            false
        )
    val switch215 =
        addConfig(
            "Switch215",
            ConfigItem.configTypeBool,
            false
        )
    val switch216 =
        addConfig(
            "Switch216",
            ConfigItem.configTypeBool,
            false
        )
    val switch217 =
        addConfig(
            "Switch217",
            ConfigItem.configTypeBool,
            false
        )
    val switch218 =
        addConfig(
            "Switch218",
            ConfigItem.configTypeBool,
            false
        )
    val switch219 =
        addConfig(
            "Switch219",
            ConfigItem.configTypeBool,
            false
        )
    val switch220 =
        addConfig(
            "Switch220",
            ConfigItem.configTypeBool,
            false
        )
    val switch221 =
        addConfig(
            "Switch221",
            ConfigItem.configTypeBool,
            false
        )
    val switch222 =
        addConfig(
            "Switch222",
            ConfigItem.configTypeBool,
            false
        )
    val switch223 =
        addConfig(
            "Switch223",
            ConfigItem.configTypeBool,
            false
        )
    val switch224 =
        addConfig(
            "Switch224",
            ConfigItem.configTypeBool,
            false
        )
    val switch225 =
        addConfig(
            "Switch225",
            ConfigItem.configTypeBool,
            false
        )
    val switch226 =
        addConfig(
            "Switch226",
            ConfigItem.configTypeBool,
            false
        )
    val switch227 =
        addConfig(
            "Switch227",
            ConfigItem.configTypeBool,
            false
        )
    val switch228 =
        addConfig(
            "Switch228",
            ConfigItem.configTypeBool,
            false
        )
    val switch229 =
        addConfig(
            "Switch229",
            ConfigItem.configTypeBool,
            false
        )
    val switch230 =
        addConfig(
            "Switch230",
            ConfigItem.configTypeBool,
            false
        )
    val switch231 =
        addConfig(
            "Switch231",
            ConfigItem.configTypeBool,
            false
        )
    val switch232 =
        addConfig(
            "Switch232",
            ConfigItem.configTypeBool,
            false
        )
    val switch233 =
        addConfig(
            "Switch233",
            ConfigItem.configTypeBool,
            false
        )
    val switch234 =
        addConfig(
            "Switch234",
            ConfigItem.configTypeBool,
            false
        )
    val switch235 =
        addConfig(
            "Switch235",
            ConfigItem.configTypeBool,
            false
        )
    val switch236 =
        addConfig(
            "Switch236",
            ConfigItem.configTypeBool,
            false
        )
    val switch237 =
        addConfig(
            "Switch237",
            ConfigItem.configTypeBool,
            false
        )
    val switch238 =
        addConfig(
            "Switch238",
            ConfigItem.configTypeBool,
            false
        )
    val switch239 =
        addConfig(
            "Switch239",
            ConfigItem.configTypeBool,
            false
        )
    val switch240 =
        addConfig(
            "Switch240",
            ConfigItem.configTypeBool,
            false
        )
    val switch241 =
        addConfig(
            "Switch241",
            ConfigItem.configTypeBool,
            false
        )
    val switch242 =
        addConfig(
            "Switch242",
            ConfigItem.configTypeBool,
            false
        )
    val switch243 =
        addConfig(
            "Switch243",
            ConfigItem.configTypeBool,
            false
        )
    val switch244 =
        addConfig(
            "Switch244",
            ConfigItem.configTypeBool,
            false
        )
    val switch245 =
        addConfig(
            "Switch245",
            ConfigItem.configTypeBool,
            false
        )
    val switch246 =
        addConfig(
            "Switch246",
            ConfigItem.configTypeBool,
            false
        )
    val switch247 =
        addConfig(
            "Switch247",
            ConfigItem.configTypeBool,
            false
        )
    val switch248 =
        addConfig(
            "Switch248",
            ConfigItem.configTypeBool,
            false
        )
    val switch249 =
        addConfig(
            "Switch249",
            ConfigItem.configTypeBool,
            false
        )
    val switch250 =
        addConfig(
            "Switch250",
            ConfigItem.configTypeBool,
            false
        )
    val switch251 =
        addConfig(
            "Switch251",
            ConfigItem.configTypeBool,
            false
        )
    val switch252 =
        addConfig(
            "Switch252",
            ConfigItem.configTypeBool,
            false
        )
    val switch253 =
        addConfig(
            "Switch253",
            ConfigItem.configTypeBool,
            false
        )
    val switch254 =
        addConfig(
            "Switch254",
            ConfigItem.configTypeBool,
            false
        )
    val switch255 =
        addConfig(
            "Switch255",
            ConfigItem.configTypeBool,
            false
        )
    val switch256 =
        addConfig(
            "Switch256",
            ConfigItem.configTypeBool,
            false
        )
    val switch257 =
        addConfig(
            "Switch257",
            ConfigItem.configTypeBool,
            false
        )
    val switch258 =
        addConfig(
            "Switch258",
            ConfigItem.configTypeBool,
            false
        )
    val switch259 =
        addConfig(
            "Switch259",
            ConfigItem.configTypeBool,
            false
        )
    val switch260 =
        addConfig(
            "Switch260",
            ConfigItem.configTypeBool,
            false
        )
    val switch261 =
        addConfig(
            "Switch261",
            ConfigItem.configTypeBool,
            false
        )
    val switch262 =
        addConfig(
            "Switch262",
            ConfigItem.configTypeBool,
            false
        )
    val switch263 =
        addConfig(
            "Switch263",
            ConfigItem.configTypeBool,
            false
        )
    val switch264 =
        addConfig(
            "Switch264",
            ConfigItem.configTypeBool,
            false
        )
    val switch265 =
        addConfig(
            "Switch265",
            ConfigItem.configTypeBool,
            false
        )
    val switch266 =
        addConfig(
            "Switch266",
            ConfigItem.configTypeBool,
            false
        )
    val switch267 =
        addConfig(
            "Switch267",
            ConfigItem.configTypeBool,
            false
        )
    val switch268 =
        addConfig(
            "Switch268",
            ConfigItem.configTypeBool,
            false
        )
    val switch269 =
        addConfig(
            "Switch269",
            ConfigItem.configTypeBool,
            false
        )
    val switch270 =
        addConfig(
            "Switch270",
            ConfigItem.configTypeBool,
            false
        )
    val switch271 =
        addConfig(
            "Switch271",
            ConfigItem.configTypeBool,
            false
        )
    val switch272 =
        addConfig(
            "Switch272",
            ConfigItem.configTypeBool,
            false
        )
    val switch273 =
        addConfig(
            "Switch273",
            ConfigItem.configTypeBool,
            false
        )
    val switch274 =
        addConfig(
            "Switch274",
            ConfigItem.configTypeBool,
            false
        )
    val switch275 =
        addConfig(
            "Switch275",
            ConfigItem.configTypeBool,
            false
        )
    val switch276 =
        addConfig(
            "Switch276",
            ConfigItem.configTypeBool,
            false
        )
    val switch277 =
        addConfig(
            "Switch277",
            ConfigItem.configTypeBool,
            false
        )
    val switch278 =
        addConfig(
            "Switch278",
            ConfigItem.configTypeBool,
            false
        )
    val switch279 =
        addConfig(
            "Switch279",
            ConfigItem.configTypeBool,
            false
        )
    val switch280 =
        addConfig(
            "Switch280",
            ConfigItem.configTypeBool,
            false
        )
    val switch281 =
        addConfig(
            "Switch281",
            ConfigItem.configTypeBool,
            false
        )
    val switch282 =
        addConfig(
            "Switch282",
            ConfigItem.configTypeBool,
            false
        )
    val switch283 =
        addConfig(
            "Switch283",
            ConfigItem.configTypeBool,
            false
        )
    val switch284 =
        addConfig(
            "Switch284",
            ConfigItem.configTypeBool,
            false
        )
    val switch285 =
        addConfig(
            "Switch285",
            ConfigItem.configTypeBool,
            false
        )
    val switch286 =
        addConfig(
            "Switch286",
            ConfigItem.configTypeBool,
            false
        )
    val switch287 =
        addConfig(
            "Switch287",
            ConfigItem.configTypeBool,
            false
        )
    val switch288 =
        addConfig(
            "Switch288",
            ConfigItem.configTypeBool,
            false
        )
    val switch289 =
        addConfig(
            "Switch289",
            ConfigItem.configTypeBool,
            false
        )
    val switch290 =
        addConfig(
            "Switch290",
            ConfigItem.configTypeBool,
            false
        )
    val switch291 =
        addConfig(
            "Switch291",
            ConfigItem.configTypeBool,
            false
        )
    val switch292 =
        addConfig(
            "Switch292",
            ConfigItem.configTypeBool,
            false
        )
    val switch293 =
        addConfig(
            "Switch293",
            ConfigItem.configTypeBool,
            false
        )
    val switch294 =
        addConfig(
            "Switch294",
            ConfigItem.configTypeBool,
            false
        )
    val switch295 =
        addConfig(
            "Switch295",
            ConfigItem.configTypeBool,
            false
        )
    val switch296 =
        addConfig(
            "Switch296",
            ConfigItem.configTypeBool,
            false
        )
    val switch297 =
        addConfig(
            "Switch297",
            ConfigItem.configTypeBool,
            false
        )
    val switch298 =
        addConfig(
            "Switch298",
            ConfigItem.configTypeBool,
            false
        )
    val switch299 =
        addConfig(
            "Switch299",
            ConfigItem.configTypeBool,
            false
        )
    val switch300 =
        addConfig(
            "Switch300",
            ConfigItem.configTypeBool,
            false
        )
    val switch301 =
        addConfig(
            "Switch301",
            ConfigItem.configTypeBool,
            false
        )
    val switch302 =
        addConfig(
            "Switch302",
            ConfigItem.configTypeBool,
            false
        )
    val switch303 =
        addConfig(
            "Switch303",
            ConfigItem.configTypeBool,
            false
        )
    val switch304 =
        addConfig(
            "Switch304",
            ConfigItem.configTypeBool,
            false
        )
    val switch305 =
        addConfig(
            "Switch305",
            ConfigItem.configTypeBool,
            false
        )
    val switch306 =
        addConfig(
            "Switch306",
            ConfigItem.configTypeBool,
            false
        )
    val switch307 =
        addConfig(
            "Switch307",
            ConfigItem.configTypeBool,
            false
        )
    val switch308 =
        addConfig(
            "Switch308",
            ConfigItem.configTypeBool,
            false
        )
    val switch309 =
        addConfig(
            "Switch309",
            ConfigItem.configTypeBool,
            false
        )
    val switch310 =
        addConfig(
            "Switch310",
            ConfigItem.configTypeBool,
            false
        )
    val switch311 =
        addConfig(
            "Switch311",
            ConfigItem.configTypeBool,
            false
        )
    val switch312 =
        addConfig(
            "Switch312",
            ConfigItem.configTypeBool,
            false
        )
    val switch313 =
        addConfig(
            "Switch313",
            ConfigItem.configTypeBool,
            false
        )
    val switch314 =
        addConfig(
            "Switch314",
            ConfigItem.configTypeBool,
            false
        )
    val switch315 =
        addConfig(
            "Switch315",
            ConfigItem.configTypeBool,
            false
        )
    val switch316 =
        addConfig(
            "Switch316",
            ConfigItem.configTypeBool,
            false
        )
    val switch317 =
        addConfig(
            "Switch317",
            ConfigItem.configTypeBool,
            false
        )
    val switch318 =
        addConfig(
            "Switch318",
            ConfigItem.configTypeBool,
            false
        )
    val switch319 =
        addConfig(
            "Switch319",
            ConfigItem.configTypeBool,
            false
        )
    val switch320 =
        addConfig(
            "Switch320",
            ConfigItem.configTypeBool,
            false
        )
    val switch321 =
        addConfig(
            "Switch321",
            ConfigItem.configTypeBool,
            false
        )
    val switch322 =
        addConfig(
            "Switch322",
            ConfigItem.configTypeBool,
            false
        )
    val switch323 =
        addConfig(
            "Switch323",
            ConfigItem.configTypeBool,
            false
        )
    val switch324 =
        addConfig(
            "Switch324",
            ConfigItem.configTypeBool,
            false
        )
    val switch325 =
        addConfig(
            "Switch325",
            ConfigItem.configTypeBool,
            false
        )
    val switch326 =
        addConfig(
            "Switch326",
            ConfigItem.configTypeBool,
            false
        )
    val switch327 =
        addConfig(
            "Switch327",
            ConfigItem.configTypeBool,
            false
        )
    val switch328 =
        addConfig(
            "Switch328",
            ConfigItem.configTypeBool,
            false
        )
    val switch329 =
        addConfig(
            "Switch329",
            ConfigItem.configTypeBool,
            false
        )
    val switch330 =
        addConfig(
            "Switch330",
            ConfigItem.configTypeBool,
            false
        )
    val switch331 =
        addConfig(
            "Switch331",
            ConfigItem.configTypeBool,
            false
        )
    val switch332 =
        addConfig(
            "Switch332",
            ConfigItem.configTypeBool,
            false
        )
    val switch333 =
        addConfig(
            "Switch333",
            ConfigItem.configTypeBool,
            false
        )
    val switch334 =
        addConfig(
            "Switch334",
            ConfigItem.configTypeBool,
            false
        )
    val switch335 =
        addConfig(
            "Switch335",
            ConfigItem.configTypeBool,
            false
        )
    val switch336 =
        addConfig(
            "Switch336",
            ConfigItem.configTypeBool,
            false
        )
    val switch337 =
        addConfig(
            "Switch337",
            ConfigItem.configTypeBool,
            false
        )
    val switch338 =
        addConfig(
            "Switch338",
            ConfigItem.configTypeBool,
            false
        )
    val switch339 =
        addConfig(
            "Switch339",
            ConfigItem.configTypeBool,
            false
        )
    val switch340 =
        addConfig(
            "Switch340",
            ConfigItem.configTypeBool,
            false
        )
    val switch341 =
        addConfig(
            "Switch341",
            ConfigItem.configTypeBool,
            false
        )
    val switch342 =
        addConfig(
            "Switch342",
            ConfigItem.configTypeBool,
            false
        )
    val switch343 =
        addConfig(
            "Switch343",
            ConfigItem.configTypeBool,
            false
        )
    val switch344 =
        addConfig(
            "Switch344",
            ConfigItem.configTypeBool,
            false
        )
    val switch345 =
        addConfig(
            "Switch345",
            ConfigItem.configTypeBool,
            false
        )
    val switch346 =
        addConfig(
            "Switch346",
            ConfigItem.configTypeBool,
            false
        )
    val switch347 =
        addConfig(
            "Switch347",
            ConfigItem.configTypeBool,
            false
        )
    val switch348 =
        addConfig(
            "Switch348",
            ConfigItem.configTypeBool,
            false
        )
    val switch349 =
        addConfig(
            "Switch349",
            ConfigItem.configTypeBool,
            false
        )
    val switch350 =
        addConfig(
            "Switch350",
            ConfigItem.configTypeBool,
            false
        )
    val switch351 =
        addConfig(
            "Switch351",
            ConfigItem.configTypeBool,
            false
        )
    val switch352 =
        addConfig(
            "Switch352",
            ConfigItem.configTypeBool,
            false
        )
    val switch353 =
        addConfig(
            "Switch353",
            ConfigItem.configTypeBool,
            false
        )
    val switch354 =
        addConfig(
            "Switch354",
            ConfigItem.configTypeBool,
            false
        )
    val switch355 =
        addConfig(
            "Switch355",
            ConfigItem.configTypeBool,
            false
        )
    val switch356 =
        addConfig(
            "Switch356",
            ConfigItem.configTypeBool,
            false
        )
    val switch357 =
        addConfig(
            "Switch357",
            ConfigItem.configTypeBool,
            false
        )
    val switch358 =
        addConfig(
            "Switch358",
            ConfigItem.configTypeBool,
            false
        )
    val switch359 =
        addConfig(
            "Switch359",
            ConfigItem.configTypeBool,
            false
        )
    val switch360 =
        addConfig(
            "Switch360",
            ConfigItem.configTypeBool,
            false
        )
    val switch361 =
        addConfig(
            "Switch361",
            ConfigItem.configTypeBool,
            false
        )
    val switch362 =
        addConfig(
            "Switch362",
            ConfigItem.configTypeBool,
            false
        )
    val switch363 =
        addConfig(
            "Switch363",
            ConfigItem.configTypeBool,
            false
        )
    val switch364 =
        addConfig(
            "Switch364",
            ConfigItem.configTypeBool,
            false
        )
    val switch365 =
        addConfig(
            "Switch365",
            ConfigItem.configTypeBool,
            false
        )
    val switch366 =
        addConfig(
            "Switch366",
            ConfigItem.configTypeBool,
            false
        )
    val switch367 =
        addConfig(
            "Switch367",
            ConfigItem.configTypeBool,
            false
        )
    val switch368 =
        addConfig(
            "Switch368",
            ConfigItem.configTypeBool,
            false
        )
    val switch369 =
        addConfig(
            "Switch369",
            ConfigItem.configTypeBool,
            false
        )
    val switch370 =
        addConfig(
            "Switch370",
            ConfigItem.configTypeBool,
            false
        )
    val switch371 =
        addConfig(
            "Switch371",
            ConfigItem.configTypeBool,
            false
        )
    val switch372 =
        addConfig(
            "Switch372",
            ConfigItem.configTypeBool,
            false
        )
    val switch373 =
        addConfig(
            "Switch373",
            ConfigItem.configTypeBool,
            false
        )
    val switch374 =
        addConfig(
            "Switch374",
            ConfigItem.configTypeBool,
            false
        )
    val switch375 =
        addConfig(
            "Switch375",
            ConfigItem.configTypeBool,
            false
        )
    val switch376 =
        addConfig(
            "Switch376",
            ConfigItem.configTypeBool,
            false
        )
    val switch377 =
        addConfig(
            "Switch377",
            ConfigItem.configTypeBool,
            false
        )
    val switch378 =
        addConfig(
            "Switch378",
            ConfigItem.configTypeBool,
            false
        )
    val switch379 =
        addConfig(
            "Switch379",
            ConfigItem.configTypeBool,
            false
        )
    val switch380 =
        addConfig(
            "Switch380",
            ConfigItem.configTypeBool,
            false
        )
    val switch381 =
        addConfig(
            "Switch381",
            ConfigItem.configTypeBool,
            false
        )
    val switch382 =
        addConfig(
            "Switch382",
            ConfigItem.configTypeBool,
            false
        )
    val switch383 =
        addConfig(
            "Switch383",
            ConfigItem.configTypeBool,
            false
        )
    val switch384 =
        addConfig(
            "Switch384",
            ConfigItem.configTypeBool,
            false
        )
    val switch385 =
        addConfig(
            "Switch385",
            ConfigItem.configTypeBool,
            false
        )
    val switch386 =
        addConfig(
            "Switch386",
            ConfigItem.configTypeBool,
            false
        )
    val switch387 =
        addConfig(
            "Switch387",
            ConfigItem.configTypeBool,
            false
        )
    val switch388 =
        addConfig(
            "Switch388",
            ConfigItem.configTypeBool,
            false
        )
    val switch389 =
        addConfig(
            "Switch389",
            ConfigItem.configTypeBool,
            false
        )
    val switch390 =
        addConfig(
            "Switch390",
            ConfigItem.configTypeBool,
            false
        )
    val switch391 =
        addConfig(
            "Switch391",
            ConfigItem.configTypeBool,
            false
        )
    val switch392 =
        addConfig(
            "Switch392",
            ConfigItem.configTypeBool,
            false
        )
    val switch393 =
        addConfig(
            "Switch393",
            ConfigItem.configTypeBool,
            false
        )
    val switch394 =
        addConfig(
            "Switch394",
            ConfigItem.configTypeBool,
            false
        )
    val switch395 =
        addConfig(
            "Switch395",
            ConfigItem.configTypeBool,
            false
        )
    val switch396 =
        addConfig(
            "Switch396",
            ConfigItem.configTypeBool,
            false
        )
    val switch397 =
        addConfig(
            "Switch397",
            ConfigItem.configTypeBool,
            false
        )
    val switch398 =
        addConfig(
            "Switch398",
            ConfigItem.configTypeBool,
            false
        )
    val switch399 =
        addConfig(
            "Switch399",
            ConfigItem.configTypeBool,
            false
        )
    val switch400 =
        addConfig(
            "Switch400",
            ConfigItem.configTypeBool,
            false
        )
    val switch401 =
        addConfig(
            "Switch401",
            ConfigItem.configTypeBool,
            false
        )
    val switch402 =
        addConfig(
            "Switch402",
            ConfigItem.configTypeBool,
            false
        )
    val switch403 =
        addConfig(
            "Switch403",
            ConfigItem.configTypeBool,
            false
        )
    val switch404 =
        addConfig(
            "Switch404",
            ConfigItem.configTypeBool,
            false
        )
    val switch405 =
        addConfig(
            "Switch405",
            ConfigItem.configTypeBool,
            false
        )
    val switch406 =
        addConfig(
            "Switch406",
            ConfigItem.configTypeBool,
            false
        )
    val switch407 =
        addConfig(
            "Switch407",
            ConfigItem.configTypeBool,
            false
        )
    val switch408 =
        addConfig(
            "Switch408",
            ConfigItem.configTypeBool,
            false
        )
    val switch409 =
        addConfig(
            "Switch409",
            ConfigItem.configTypeBool,
            false
        )
    val switch410 =
        addConfig(
            "Switch410",
            ConfigItem.configTypeBool,
            false
        )
    val switch411 =
        addConfig(
            "Switch411",
            ConfigItem.configTypeBool,
            false
        )
    val switch412 =
        addConfig(
            "Switch412",
            ConfigItem.configTypeBool,
            false
        )
    val switch413 =
        addConfig(
            "Switch413",
            ConfigItem.configTypeBool,
            false
        )
    val switch414 =
        addConfig(
            "Switch414",
            ConfigItem.configTypeBool,
            false
        )
    val switch415 =
        addConfig(
            "Switch415",
            ConfigItem.configTypeBool,
            false
        )
    val switch416 =
        addConfig(
            "Switch416",
            ConfigItem.configTypeBool,
            false
        )
    val switch417 =
        addConfig(
            "Switch417",
            ConfigItem.configTypeBool,
            false
        )
    val switch418 =
        addConfig(
            "Switch418",
            ConfigItem.configTypeBool,
            false
        )
    val switch419 =
        addConfig(
            "Switch419",
            ConfigItem.configTypeBool,
            false
        )
    val switch420 =
        addConfig(
            "Switch420",
            ConfigItem.configTypeBool,
            false
        )
    val switch421 =
        addConfig(
            "Switch421",
            ConfigItem.configTypeBool,
            false
        )
    val switch422 =
        addConfig(
            "Switch422",
            ConfigItem.configTypeBool,
            false
        )
    val switch423 =
        addConfig(
            "Switch423",
            ConfigItem.configTypeBool,
            false
        )
    val switch424 =
        addConfig(
            "Switch424",
            ConfigItem.configTypeBool,
            false
        )
    val switch425 =
        addConfig(
            "Switch425",
            ConfigItem.configTypeBool,
            false
        )
    val switch426 =
        addConfig(
            "Switch426",
            ConfigItem.configTypeBool,
            false
        )
    val switch427 =
        addConfig(
            "Switch427",
            ConfigItem.configTypeBool,
            false
        )
    val switch428 =
        addConfig(
            "Switch428",
            ConfigItem.configTypeBool,
            false
        )
    val switch429 =
        addConfig(
            "Switch429",
            ConfigItem.configTypeBool,
            false
        )
    val switch430 =
        addConfig(
            "Switch430",
            ConfigItem.configTypeBool,
            false
        )
    val switch431 =
        addConfig(
            "Switch431",
            ConfigItem.configTypeBool,
            false
        )
    val switch432 =
        addConfig(
            "Switch432",
            ConfigItem.configTypeBool,
            false
        )
    val switch433 =
        addConfig(
            "Switch433",
            ConfigItem.configTypeBool,
            false
        )
    val switch434 =
        addConfig(
            "Switch434",
            ConfigItem.configTypeBool,
            false
        )
    val switch435 =
        addConfig(
            "Switch435",
            ConfigItem.configTypeBool,
            false
        )
    val switch436 =
        addConfig(
            "Switch436",
            ConfigItem.configTypeBool,
            false
        )
    val switch437 =
        addConfig(
            "Switch437",
            ConfigItem.configTypeBool,
            false
        )
    val switch438 =
        addConfig(
            "Switch438",
            ConfigItem.configTypeBool,
            false
        )
    val switch439 =
        addConfig(
            "Switch439",
            ConfigItem.configTypeBool,
            false
        )
    val switch440 =
        addConfig(
            "Switch440",
            ConfigItem.configTypeBool,
            false
        )
    val switch441 =
        addConfig(
            "Switch441",
            ConfigItem.configTypeBool,
            false
        )
    val switch442 =
        addConfig(
            "Switch442",
            ConfigItem.configTypeBool,
            false
        )
    val switch443 =
        addConfig(
            "Switch443",
            ConfigItem.configTypeBool,
            false
        )
    val switch444 =
        addConfig(
            "Switch444",
            ConfigItem.configTypeBool,
            false
        )
    val switch445 =
        addConfig(
            "Switch445",
            ConfigItem.configTypeBool,
            false
        )
    val switch446 =
        addConfig(
            "Switch446",
            ConfigItem.configTypeBool,
            false
        )
    val switch447 =
        addConfig(
            "Switch447",
            ConfigItem.configTypeBool,
            false
        )
    val switch448 =
        addConfig(
            "Switch448",
            ConfigItem.configTypeBool,
            false
        )
    val switch449 =
        addConfig(
            "Switch449",
            ConfigItem.configTypeBool,
            false
        )
    val switch450 =
        addConfig(
            "Switch450",
            ConfigItem.configTypeBool,
            false
        )
    val switch451 =
        addConfig(
            "Switch451",
            ConfigItem.configTypeBool,
            false
        )
    val switch452 =
        addConfig(
            "Switch452",
            ConfigItem.configTypeBool,
            false
        )
    val switch453 =
        addConfig(
            "Switch453",
            ConfigItem.configTypeBool,
            false
        )
    val switch454 =
        addConfig(
            "Switch454",
            ConfigItem.configTypeBool,
            false
        )
    val switch455 =
        addConfig(
            "Switch455",
            ConfigItem.configTypeBool,
            false
        )
    val switch456 =
        addConfig(
            "Switch456",
            ConfigItem.configTypeBool,
            false
        )
    val switch457 =
        addConfig(
            "Switch457",
            ConfigItem.configTypeBool,
            false
        )
    val switch458 =
        addConfig(
            "Switch458",
            ConfigItem.configTypeBool,
            false
        )
    val switch459 =
        addConfig(
            "Switch459",
            ConfigItem.configTypeBool,
            false
        )
    val switch460 =
        addConfig(
            "Switch460",
            ConfigItem.configTypeBool,
            false
        )
    val switch461 =
        addConfig(
            "Switch461",
            ConfigItem.configTypeBool,
            false
        )
    val switch462 =
        addConfig(
            "Switch462",
            ConfigItem.configTypeBool,
            false
        )
    val switch463 =
        addConfig(
            "Switch463",
            ConfigItem.configTypeBool,
            false
        )
    val switch464 =
        addConfig(
            "Switch464",
            ConfigItem.configTypeBool,
            false
        )
    val switch465 =
        addConfig(
            "Switch465",
            ConfigItem.configTypeBool,
            false
        )
    val switch466 =
        addConfig(
            "Switch466",
            ConfigItem.configTypeBool,
            false
        )
    val switch467 =
        addConfig(
            "Switch467",
            ConfigItem.configTypeBool,
            false
        )
    val switch468 =
        addConfig(
            "Switch468",
            ConfigItem.configTypeBool,
            false
        )
    val switch469 =
        addConfig(
            "Switch469",
            ConfigItem.configTypeBool,
            false
        )
    val switch470 =
        addConfig(
            "Switch470",
            ConfigItem.configTypeBool,
            false
        )
    val switch471 =
        addConfig(
            "Switch471",
            ConfigItem.configTypeBool,
            false
        )
    val switch472 =
        addConfig(
            "Switch472",
            ConfigItem.configTypeBool,
            false
        )
    val switch473 =
        addConfig(
            "Switch473",
            ConfigItem.configTypeBool,
            false
        )
    val switch474 =
        addConfig(
            "Switch474",
            ConfigItem.configTypeBool,
            false
        )
    val switch475 =
        addConfig(
            "Switch475",
            ConfigItem.configTypeBool,
            false
        )
    val switch476 =
        addConfig(
            "Switch476",
            ConfigItem.configTypeBool,
            false
        )
    val switch477 =
        addConfig(
            "Switch477",
            ConfigItem.configTypeBool,
            false
        )
    val switch478 =
        addConfig(
            "Switch478",
            ConfigItem.configTypeBool,
            false
        )
    val switch479 =
        addConfig(
            "Switch479",
            ConfigItem.configTypeBool,
            false
        )
    val switch480 =
        addConfig(
            "Switch480",
            ConfigItem.configTypeBool,
            false
        )
    val switch481 =
        addConfig(
            "Switch481",
            ConfigItem.configTypeBool,
            false
        )
    val switch482 =
        addConfig(
            "Switch482",
            ConfigItem.configTypeBool,
            false
        )
    val switch483 =
        addConfig(
            "Switch483",
            ConfigItem.configTypeBool,
            false
        )
    val switch484 =
        addConfig(
            "Switch484",
            ConfigItem.configTypeBool,
            false
        )
    val switch485 =
        addConfig(
            "Switch485",
            ConfigItem.configTypeBool,
            false
        )
    val switch486 =
        addConfig(
            "Switch486",
            ConfigItem.configTypeBool,
            false
        )
    val switch487 =
        addConfig(
            "Switch487",
            ConfigItem.configTypeBool,
            false
        )
    val switch488 =
        addConfig(
            "Switch488",
            ConfigItem.configTypeBool,
            false
        )
    val switch489 =
        addConfig(
            "Switch489",
            ConfigItem.configTypeBool,
            false
        )
    val switch490 =
        addConfig(
            "Switch490",
            ConfigItem.configTypeBool,
            false
        )
    val switch491 =
        addConfig(
            "Switch491",
            ConfigItem.configTypeBool,
            false
        )
    val switch492 =
        addConfig(
            "Switch492",
            ConfigItem.configTypeBool,
            false
        )
    val switch493 =
        addConfig(
            "Switch493",
            ConfigItem.configTypeBool,
            false
        )
    val switch494 =
        addConfig(
            "Switch494",
            ConfigItem.configTypeBool,
            false
        )
    val switch495 =
        addConfig(
            "Switch495",
            ConfigItem.configTypeBool,
            false
        )
    val switch496 =
        addConfig(
            "Switch496",
            ConfigItem.configTypeBool,
            false
        )
    val switch497 =
        addConfig(
            "Switch497",
            ConfigItem.configTypeBool,
            false
        )
    val switch498 =
        addConfig(
            "Switch498",
            ConfigItem.configTypeBool,
            false
        )
    val switch499 =
        addConfig(
            "Switch499",
            ConfigItem.configTypeBool,
            false
        )
    val switch500 =
        addConfig(
            "Switch500",
            ConfigItem.configTypeBool,
            false
        )
    val switch501 =
        addConfig(
            "Switch501",
            ConfigItem.configTypeBool,
            false
        )
    val switch502 =
        addConfig(
            "Switch502",
            ConfigItem.configTypeBool,
            false
        )
    val switch503 =
        addConfig(
            "Switch503",
            ConfigItem.configTypeBool,
            false
        )
    val switch504 =
        addConfig(
            "Switch504",
            ConfigItem.configTypeBool,
            false
        )
    val switch505 =
        addConfig(
            "Switch505",
            ConfigItem.configTypeBool,
            false
        )
    val switch506 =
        addConfig(
            "Switch506",
            ConfigItem.configTypeBool,
            false
        )
    val switch507 =
        addConfig(
            "Switch507",
            ConfigItem.configTypeBool,
            false
        )
    val switch508 =
        addConfig(
            "Switch508",
            ConfigItem.configTypeBool,
            false
        )
    val switch509 =
        addConfig(
            "Switch509",
            ConfigItem.configTypeBool,
            false
        )
    val switch510 =
        addConfig(
            "Switch510",
            ConfigItem.configTypeBool,
            false
        )
    val switch511 =
        addConfig(
            "Switch511",
            ConfigItem.configTypeBool,
            false
        )
    val switch512 =
        addConfig(
            "Switch512",
            ConfigItem.configTypeBool,
            false
        )
    val switch513 =
        addConfig(
            "Switch513",
            ConfigItem.configTypeBool,
            false
        )
    val switch514 =
        addConfig(
            "Switch514",
            ConfigItem.configTypeBool,
            false
        )
    val switch515 =
        addConfig(
            "Switch515",
            ConfigItem.configTypeBool,
            false
        )
    val switch516 =
        addConfig(
            "Switch516",
            ConfigItem.configTypeBool,
            false
        )
    val switch517 =
        addConfig(
            "Switch517",
            ConfigItem.configTypeBool,
            false
        )
    val switch518 =
        addConfig(
            "Switch518",
            ConfigItem.configTypeBool,
            false
        )
    val switch519 =
        addConfig(
            "Switch519",
            ConfigItem.configTypeBool,
            false
        )
    val switch520 =
        addConfig(
            "Switch520",
            ConfigItem.configTypeBool,
            false
        )
    val switch521 =
        addConfig(
            "Switch521",
            ConfigItem.configTypeBool,
            false
        )
    val switch522 =
        addConfig(
            "Switch522",
            ConfigItem.configTypeBool,
            false
        )
    val switch523 =
        addConfig(
            "Switch523",
            ConfigItem.configTypeBool,
            false
        )
    val switch524 =
        addConfig(
            "Switch524",
            ConfigItem.configTypeBool,
            false
        )
    val switch525 =
        addConfig(
            "Switch525",
            ConfigItem.configTypeBool,
            false
        )
    val switch526 =
        addConfig(
            "Switch526",
            ConfigItem.configTypeBool,
            false
        )
    val switch527 =
        addConfig(
            "Switch527",
            ConfigItem.configTypeBool,
            false
        )
    val switch528 =
        addConfig(
            "Switch528",
            ConfigItem.configTypeBool,
            false
        )
    val switch529 =
        addConfig(
            "Switch529",
            ConfigItem.configTypeBool,
            false
        )
    val switch530 =
        addConfig(
            "Switch530",
            ConfigItem.configTypeBool,
            false
        )
    val switch531 =
        addConfig(
            "Switch531",
            ConfigItem.configTypeBool,
            false
        )
    val switch532 =
        addConfig(
            "Switch532",
            ConfigItem.configTypeBool,
            false
        )
    val switch533 =
        addConfig(
            "Switch533",
            ConfigItem.configTypeBool,
            false
        )
    val switch534 =
        addConfig(
            "Switch534",
            ConfigItem.configTypeBool,
            false
        )
    val switch535 =
        addConfig(
            "Switch535",
            ConfigItem.configTypeBool,
            false
        )
    val switch536 =
        addConfig(
            "Switch536",
            ConfigItem.configTypeBool,
            false
        )
    val switch537 =
        addConfig(
            "Switch537",
            ConfigItem.configTypeBool,
            false
        )
    val switch538 =
        addConfig(
            "Switch538",
            ConfigItem.configTypeBool,
            false
        )
    val switch539 =
        addConfig(
            "Switch539",
            ConfigItem.configTypeBool,
            false
        )
    val switch540 =
        addConfig(
            "Switch540",
            ConfigItem.configTypeBool,
            false
        )
    val switch541 =
        addConfig(
            "Switch541",
            ConfigItem.configTypeBool,
            false
        )
    val switch542 =
        addConfig(
            "Switch542",
            ConfigItem.configTypeBool,
            false
        )
    val switch543 =
        addConfig(
            "Switch543",
            ConfigItem.configTypeBool,
            false
        )
    val switch544 =
        addConfig(
            "Switch544",
            ConfigItem.configTypeBool,
            false
        )
    val switch545 =
        addConfig(
            "Switch545",
            ConfigItem.configTypeBool,
            false
        )
    val switch546 =
        addConfig(
            "Switch546",
            ConfigItem.configTypeBool,
            false
        )
    val switch547 =
        addConfig(
            "Switch547",
            ConfigItem.configTypeBool,
            false
        )
    val switch548 =
        addConfig(
            "Switch548",
            ConfigItem.configTypeBool,
            false
        )
    val switch549 =
        addConfig(
            "Switch549",
            ConfigItem.configTypeBool,
            false
        )
    val switch550 =
        addConfig(
            "Switch550",
            ConfigItem.configTypeBool,
            false
        )
    val switch551 =
        addConfig(
            "Switch551",
            ConfigItem.configTypeBool,
            false
        )
    val switch552 =
        addConfig(
            "Switch552",
            ConfigItem.configTypeBool,
            false
        )
    val switch553 =
        addConfig(
            "Switch553",
            ConfigItem.configTypeBool,
            false
        )
    val switch554 =
        addConfig(
            "Switch554",
            ConfigItem.configTypeBool,
            false
        )
    val switch555 =
        addConfig(
            "Switch555",
            ConfigItem.configTypeBool,
            false
        )
    val switch556 =
        addConfig(
            "Switch556",
            ConfigItem.configTypeBool,
            false
        )
    val switch557 =
        addConfig(
            "Switch557",
            ConfigItem.configTypeBool,
            false
        )
    val switch558 =
        addConfig(
            "Switch558",
            ConfigItem.configTypeBool,
            false
        )
    val switch559 =
        addConfig(
            "Switch559",
            ConfigItem.configTypeBool,
            false
        )
    val switch560 =
        addConfig(
            "Switch560",
            ConfigItem.configTypeBool,
            false
        )
    val switch561 =
        addConfig(
            "Switch561",
            ConfigItem.configTypeBool,
            false
        )
    val switch562 =
        addConfig(
            "Switch562",
            ConfigItem.configTypeBool,
            false
        )
    val switch563 =
        addConfig(
            "Switch563",
            ConfigItem.configTypeBool,
            false
        )
    val switch564 =
        addConfig(
            "Switch564",
            ConfigItem.configTypeBool,
            false
        )
    val switch565 =
        addConfig(
            "Switch565",
            ConfigItem.configTypeBool,
            false
        )
    val switch566 =
        addConfig(
            "Switch566",
            ConfigItem.configTypeBool,
            false
        )
    val switch567 =
        addConfig(
            "Switch567",
            ConfigItem.configTypeBool,
            false
        )
    val switch568 =
        addConfig(
            "Switch568",
            ConfigItem.configTypeBool,
            false
        )
    val switch569 =
        addConfig(
            "Switch569",
            ConfigItem.configTypeBool,
            false
        )
    val switch570 =
        addConfig(
            "Switch570",
            ConfigItem.configTypeBool,
            false
        )
    val switch571 =
        addConfig(
            "Switch571",
            ConfigItem.configTypeBool,
            false
        )
    val switch572 =
        addConfig(
            "Switch572",
            ConfigItem.configTypeBool,
            false
        )
    val switch573 =
        addConfig(
            "Switch573",
            ConfigItem.configTypeBool,
            false
        )
    val switch574 =
        addConfig(
            "Switch574",
            ConfigItem.configTypeBool,
            false
        )
    val switch575 =
        addConfig(
            "Switch575",
            ConfigItem.configTypeBool,
            false
        )
    val switch576 =
        addConfig(
            "Switch576",
            ConfigItem.configTypeBool,
            false
        )
    val switch577 =
        addConfig(
            "Switch577",
            ConfigItem.configTypeBool,
            false
        )
    val switch578 =
        addConfig(
            "Switch578",
            ConfigItem.configTypeBool,
            false
        )
    val switch579 =
        addConfig(
            "Switch579",
            ConfigItem.configTypeBool,
            false
        )
    val switch580 =
        addConfig(
            "Switch580",
            ConfigItem.configTypeBool,
            false
        )
    val switch581 =
        addConfig(
            "Switch581",
            ConfigItem.configTypeBool,
            false
        )
    val switch582 =
        addConfig(
            "Switch582",
            ConfigItem.configTypeBool,
            false
        )
    val switch583 =
        addConfig(
            "Switch583",
            ConfigItem.configTypeBool,
            false
        )
    val switch584 =
        addConfig(
            "Switch584",
            ConfigItem.configTypeBool,
            false
        )
    val switch585 =
        addConfig(
            "Switch585",
            ConfigItem.configTypeBool,
            false
        )
    val switch586 =
        addConfig(
            "Switch586",
            ConfigItem.configTypeBool,
            false
        )
    val switch587 =
        addConfig(
            "Switch587",
            ConfigItem.configTypeBool,
            false
        )
    val switch588 =
        addConfig(
            "Switch588",
            ConfigItem.configTypeBool,
            false
        )
    val switch589 =
        addConfig(
            "Switch589",
            ConfigItem.configTypeBool,
            false
        )
    val switch590 =
        addConfig(
            "Switch590",
            ConfigItem.configTypeBool,
            false
        )
    val switch591 =
        addConfig(
            "Switch591",
            ConfigItem.configTypeBool,
            false
        )
    val switch592 =
        addConfig(
            "Switch592",
            ConfigItem.configTypeBool,
            false
        )
    val switch593 =
        addConfig(
            "Switch593",
            ConfigItem.configTypeBool,
            false
        )
    val switch594 =
        addConfig(
            "Switch594",
            ConfigItem.configTypeBool,
            false
        )
    val switch595 =
        addConfig(
            "Switch595",
            ConfigItem.configTypeBool,
            false
        )
    val switch596 =
        addConfig(
            "Switch596",
            ConfigItem.configTypeBool,
            false
        )
    val switch597 =
        addConfig(
            "Switch597",
            ConfigItem.configTypeBool,
            false
        )
    val switch598 =
        addConfig(
            "Switch598",
            ConfigItem.configTypeBool,
            false
        )
    val switch599 =
        addConfig(
            "Switch599",
            ConfigItem.configTypeBool,
            false
        )
    val switch600 =
        addConfig(
            "Switch600",
            ConfigItem.configTypeBool,
            false
        )
    val switch601 =
        addConfig(
            "Switch601",
            ConfigItem.configTypeBool,
            false
        )
    val switch602 =
        addConfig(
            "Switch602",
            ConfigItem.configTypeBool,
            false
        )
    val switch603 =
        addConfig(
            "Switch603",
            ConfigItem.configTypeBool,
            false
        )
    val switch604 =
        addConfig(
            "Switch604",
            ConfigItem.configTypeBool,
            false
        )
    val switch605 =
        addConfig(
            "Switch605",
            ConfigItem.configTypeBool,
            false
        )
    val switch606 =
        addConfig(
            "Switch606",
            ConfigItem.configTypeBool,
            false
        )
    val switch607 =
        addConfig(
            "Switch607",
            ConfigItem.configTypeBool,
            false
        )
    val switch608 =
        addConfig(
            "Switch608",
            ConfigItem.configTypeBool,
            false
        )
    val switch609 =
        addConfig(
            "Switch609",
            ConfigItem.configTypeBool,
            false
        )
    val switch610 =
        addConfig(
            "Switch610",
            ConfigItem.configTypeBool,
            false
        )
    val switch611 =
        addConfig(
            "Switch611",
            ConfigItem.configTypeBool,
            false
        )
    val switch612 =
        addConfig(
            "Switch612",
            ConfigItem.configTypeBool,
            false
        )
    val switch613 =
        addConfig(
            "Switch613",
            ConfigItem.configTypeBool,
            false
        )
    val switch614 =
        addConfig(
            "Switch614",
            ConfigItem.configTypeBool,
            false
        )
    val switch615 =
        addConfig(
            "Switch615",
            ConfigItem.configTypeBool,
            false
        )
    val switch616 =
        addConfig(
            "Switch616",
            ConfigItem.configTypeBool,
            false
        )
    val switch617 =
        addConfig(
            "Switch617",
            ConfigItem.configTypeBool,
            false
        )
    val switch618 =
        addConfig(
            "Switch618",
            ConfigItem.configTypeBool,
            false
        )
    val switch619 =
        addConfig(
            "Switch619",
            ConfigItem.configTypeBool,
            false
        )
    val switch620 =
        addConfig(
            "Switch620",
            ConfigItem.configTypeBool,
            false
        )
    val switch621 =
        addConfig(
            "Switch621",
            ConfigItem.configTypeBool,
            false
        )
    val switch622 =
        addConfig(
            "Switch622",
            ConfigItem.configTypeBool,
            false
        )
    val switch623 =
        addConfig(
            "Switch623",
            ConfigItem.configTypeBool,
            false
        )
    val switch624 =
        addConfig(
            "Switch624",
            ConfigItem.configTypeBool,
            false
        )
    val switch625 =
        addConfig(
            "Switch625",
            ConfigItem.configTypeBool,
            false
        )
    val switch626 =
        addConfig(
            "Switch626",
            ConfigItem.configTypeBool,
            false
        )
    val switch627 =
        addConfig(
            "Switch627",
            ConfigItem.configTypeBool,
            false
        )
    val switch628 =
        addConfig(
            "Switch628",
            ConfigItem.configTypeBool,
            false
        )
    val switch629 =
        addConfig(
            "Switch629",
            ConfigItem.configTypeBool,
            false
        )
    val switch630 =
        addConfig(
            "Switch630",
            ConfigItem.configTypeBool,
            false
        )
    val switch631 =
        addConfig(
            "Switch631",
            ConfigItem.configTypeBool,
            false
        )
    val switch632 =
        addConfig(
            "Switch632",
            ConfigItem.configTypeBool,
            false
        )
    val switch633 =
        addConfig(
            "Switch633",
            ConfigItem.configTypeBool,
            false
        )
    val switch634 =
        addConfig(
            "Switch634",
            ConfigItem.configTypeBool,
            false
        )
    val switch635 =
        addConfig(
            "Switch635",
            ConfigItem.configTypeBool,
            false
        )
    val switch636 =
        addConfig(
            "Switch636",
            ConfigItem.configTypeBool,
            false
        )
    val switch637 =
        addConfig(
            "Switch637",
            ConfigItem.configTypeBool,
            false
        )
    val switch638 =
        addConfig(
            "Switch638",
            ConfigItem.configTypeBool,
            false
        )
    val switch639 =
        addConfig(
            "Switch639",
            ConfigItem.configTypeBool,
            false
        )
    val switch640 =
        addConfig(
            "Switch640",
            ConfigItem.configTypeBool,
            false
        )
    val switch641 =
        addConfig(
            "Switch641",
            ConfigItem.configTypeBool,
            false
        )
    val switch642 =
        addConfig(
            "Switch642",
            ConfigItem.configTypeBool,
            false
        )
    val switch643 =
        addConfig(
            "Switch643",
            ConfigItem.configTypeBool,
            false
        )
    val switch644 =
        addConfig(
            "Switch644",
            ConfigItem.configTypeBool,
            false
        )
    val switch645 =
        addConfig(
            "Switch645",
            ConfigItem.configTypeBool,
            false
        )
    val switch646 =
        addConfig(
            "Switch646",
            ConfigItem.configTypeBool,
            false
        )
    val switch647 =
        addConfig(
            "Switch647",
            ConfigItem.configTypeBool,
            false
        )
    val switch648 =
        addConfig(
            "Switch648",
            ConfigItem.configTypeBool,
            false
        )
    val switch649 =
        addConfig(
            "Switch649",
            ConfigItem.configTypeBool,
            false
        )
    val switch650 =
        addConfig(
            "Switch650",
            ConfigItem.configTypeBool,
            false
        )
    val switch651 =
        addConfig(
            "Switch651",
            ConfigItem.configTypeBool,
            false
        )
    val switch652 =
        addConfig(
            "Switch652",
            ConfigItem.configTypeBool,
            false
        )
    val switch653 =
        addConfig(
            "Switch653",
            ConfigItem.configTypeBool,
            false
        )
    val switch654 =
        addConfig(
            "Switch654",
            ConfigItem.configTypeBool,
            false
        )
    val switch655 =
        addConfig(
            "Switch655",
            ConfigItem.configTypeBool,
            false
        )
    val switch656 =
        addConfig(
            "Switch656",
            ConfigItem.configTypeBool,
            false
        )
    val switch657 =
        addConfig(
            "Switch657",
            ConfigItem.configTypeBool,
            false
        )
    val switch658 =
        addConfig(
            "Switch658",
            ConfigItem.configTypeBool,
            false
        )
    val switch659 =
        addConfig(
            "Switch659",
            ConfigItem.configTypeBool,
            false
        )
    val switch660 =
        addConfig(
            "Switch660",
            ConfigItem.configTypeBool,
            false
        )
    val switch661 =
        addConfig(
            "Switch661",
            ConfigItem.configTypeBool,
            false
        )
    val switch662 =
        addConfig(
            "Switch662",
            ConfigItem.configTypeBool,
            false
        )
    val switch663 =
        addConfig(
            "Switch663",
            ConfigItem.configTypeBool,
            false
        )
    val switch664 =
        addConfig(
            "Switch664",
            ConfigItem.configTypeBool,
            false
        )
    val switch665 =
        addConfig(
            "Switch665",
            ConfigItem.configTypeBool,
            false
        )
    val switch666 =
        addConfig(
            "Switch666",
            ConfigItem.configTypeBool,
            false
        )
    val switch667 =
        addConfig(
            "Switch667",
            ConfigItem.configTypeBool,
            false
        )
    val switch668 =
        addConfig(
            "Switch668",
            ConfigItem.configTypeBool,
            false
        )
    val switch669 =
        addConfig(
            "Switch669",
            ConfigItem.configTypeBool,
            false
        )
    val switch670 =
        addConfig(
            "Switch670",
            ConfigItem.configTypeBool,
            false
        )
    val switch671 =
        addConfig(
            "Switch671",
            ConfigItem.configTypeBool,
            false
        )
    val switch672 =
        addConfig(
            "Switch672",
            ConfigItem.configTypeBool,
            false
        )
    val switch673 =
        addConfig(
            "Switch673",
            ConfigItem.configTypeBool,
            false
        )
    val switch674 =
        addConfig(
            "Switch674",
            ConfigItem.configTypeBool,
            false
        )
    val switch675 =
        addConfig(
            "Switch675",
            ConfigItem.configTypeBool,
            false
        )
    val switch676 =
        addConfig(
            "Switch676",
            ConfigItem.configTypeBool,
            false
        )
    val switch677 =
        addConfig(
            "Switch677",
            ConfigItem.configTypeBool,
            false
        )
    val switch678 =
        addConfig(
            "Switch678",
            ConfigItem.configTypeBool,
            false
        )
    val switch679 =
        addConfig(
            "Switch679",
            ConfigItem.configTypeBool,
            false
        )
    val switch680 =
        addConfig(
            "Switch680",
            ConfigItem.configTypeBool,
            false
        )
    val switch681 =
        addConfig(
            "Switch681",
            ConfigItem.configTypeBool,
            false
        )
    val switch682 =
        addConfig(
            "Switch682",
            ConfigItem.configTypeBool,
            false
        )
    val switch683 =
        addConfig(
            "Switch683",
            ConfigItem.configTypeBool,
            false
        )
    val switch684 =
        addConfig(
            "Switch684",
            ConfigItem.configTypeBool,
            false
        )
    val switch685 =
        addConfig(
            "Switch685",
            ConfigItem.configTypeBool,
            false
        )
    val switch686 =
        addConfig(
            "Switch686",
            ConfigItem.configTypeBool,
            false
        )
    val switch687 =
        addConfig(
            "Switch687",
            ConfigItem.configTypeBool,
            false
        )
    val switch688 =
        addConfig(
            "Switch688",
            ConfigItem.configTypeBool,
            false
        )
    val switch689 =
        addConfig(
            "Switch689",
            ConfigItem.configTypeBool,
            false
        )
    val switch690 =
        addConfig(
            "Switch690",
            ConfigItem.configTypeBool,
            false
        )
    val switch691 =
        addConfig(
            "Switch691",
            ConfigItem.configTypeBool,
            false
        )
    val switch692 =
        addConfig(
            "Switch692",
            ConfigItem.configTypeBool,
            false
        )
    val switch693 =
        addConfig(
            "Switch693",
            ConfigItem.configTypeBool,
            false
        )
    val switch694 =
        addConfig(
            "Switch694",
            ConfigItem.configTypeBool,
            false
        )
    val switch695 =
        addConfig(
            "Switch695",
            ConfigItem.configTypeBool,
            false
        )
    val switch696 =
        addConfig(
            "Switch696",
            ConfigItem.configTypeBool,
            false
        )
    val switch697 =
        addConfig(
            "Switch697",
            ConfigItem.configTypeBool,
            false
        )
    val switch698 =
        addConfig(
            "Switch698",
            ConfigItem.configTypeBool,
            false
        )
    val switch699 =
        addConfig(
            "Switch699",
            ConfigItem.configTypeBool,
            false
        )
    val switch700 =
        addConfig(
            "Switch700",
            ConfigItem.configTypeBool,
            false
        )
    val switch701 =
        addConfig(
            "Switch701",
            ConfigItem.configTypeBool,
            false
        )
    val switch702 =
        addConfig(
            "Switch702",
            ConfigItem.configTypeBool,
            false
        )
    val switch703 =
        addConfig(
            "Switch703",
            ConfigItem.configTypeBool,
            false
        )
    val switch704 =
        addConfig(
            "Switch704",
            ConfigItem.configTypeBool,
            false
        )
    val switch705 =
        addConfig(
            "Switch705",
            ConfigItem.configTypeBool,
            false
        )
    val switch706 =
        addConfig(
            "Switch706",
            ConfigItem.configTypeBool,
            false
        )
    val switch707 =
        addConfig(
            "Switch707",
            ConfigItem.configTypeBool,
            false
        )
    val switch708 =
        addConfig(
            "Switch708",
            ConfigItem.configTypeBool,
            false
        )
    val switch709 =
        addConfig(
            "Switch709",
            ConfigItem.configTypeBool,
            false
        )
    val switch710 =
        addConfig(
            "Switch710",
            ConfigItem.configTypeBool,
            false
        )
    val switch711 =
        addConfig(
            "Switch711",
            ConfigItem.configTypeBool,
            false
        )
    val switch712 =
        addConfig(
            "Switch712",
            ConfigItem.configTypeBool,
            false
        )
    val switch713 =
        addConfig(
            "Switch713",
            ConfigItem.configTypeBool,
            false
        )
    val switch714 =
        addConfig(
            "Switch714",
            ConfigItem.configTypeBool,
            false
        )
    val switch715 =
        addConfig(
            "Switch715",
            ConfigItem.configTypeBool,
            false
        )
    val switch716 =
        addConfig(
            "Switch716",
            ConfigItem.configTypeBool,
            false
        )
    val switch717 =
        addConfig(
            "Switch717",
            ConfigItem.configTypeBool,
            false
        )
    val switch718 =
        addConfig(
            "Switch718",
            ConfigItem.configTypeBool,
            false
        )
    val switch719 =
        addConfig(
            "Switch719",
            ConfigItem.configTypeBool,
            false
        )
    val switch720 =
        addConfig(
            "Switch720",
            ConfigItem.configTypeBool,
            false
        )
    val switch721 =
        addConfig(
            "Switch721",
            ConfigItem.configTypeBool,
            false
        )
    val switch722 =
        addConfig(
            "Switch722",
            ConfigItem.configTypeBool,
            false
        )
    val switch723 =
        addConfig(
            "Switch723",
            ConfigItem.configTypeBool,
            false
        )
    val switch724 =
        addConfig(
            "Switch724",
            ConfigItem.configTypeBool,
            false
        )
    val switch725 =
        addConfig(
            "Switch725",
            ConfigItem.configTypeBool,
            false
        )
    val switch726 =
        addConfig(
            "Switch726",
            ConfigItem.configTypeBool,
            false
        )
    val switch727 =
        addConfig(
            "Switch727",
            ConfigItem.configTypeBool,
            false
        )
    val switch728 =
        addConfig(
            "Switch728",
            ConfigItem.configTypeBool,
            false
        )
    val switch729 =
        addConfig(
            "Switch729",
            ConfigItem.configTypeBool,
            false
        )
    val switch730 =
        addConfig(
            "Switch730",
            ConfigItem.configTypeBool,
            false
        )
    val switch731 =
        addConfig(
            "Switch731",
            ConfigItem.configTypeBool,
            false
        )
    val switch732 =
        addConfig(
            "Switch732",
            ConfigItem.configTypeBool,
            false
        )
    val switch733 =
        addConfig(
            "Switch733",
            ConfigItem.configTypeBool,
            false
        )
    val switch734 =
        addConfig(
            "Switch734",
            ConfigItem.configTypeBool,
            false
        )
    val switch735 =
        addConfig(
            "Switch735",
            ConfigItem.configTypeBool,
            false
        )
    val switch736 =
        addConfig(
            "Switch736",
            ConfigItem.configTypeBool,
            false
        )
    val switch737 =
        addConfig(
            "Switch737",
            ConfigItem.configTypeBool,
            false
        )
    val switch738 =
        addConfig(
            "Switch738",
            ConfigItem.configTypeBool,
            false
        )
    val switch739 =
        addConfig(
            "Switch739",
            ConfigItem.configTypeBool,
            false
        )
    val switch740 =
        addConfig(
            "Switch740",
            ConfigItem.configTypeBool,
            false
        )
    val switch741 =
        addConfig(
            "Switch741",
            ConfigItem.configTypeBool,
            false
        )
    val switch742 =
        addConfig(
            "Switch742",
            ConfigItem.configTypeBool,
            false
        )
    val switch743 =
        addConfig(
            "Switch743",
            ConfigItem.configTypeBool,
            false
        )
    val switch744 =
        addConfig(
            "Switch744",
            ConfigItem.configTypeBool,
            false
        )
    val switch745 =
        addConfig(
            "Switch745",
            ConfigItem.configTypeBool,
            false
        )
    val switch746 =
        addConfig(
            "Switch746",
            ConfigItem.configTypeBool,
            false
        )
    val switch747 =
        addConfig(
            "Switch747",
            ConfigItem.configTypeBool,
            false
        )
    val switch748 =
        addConfig(
            "Switch748",
            ConfigItem.configTypeBool,
            false
        )
    val switch749 =
        addConfig(
            "Switch749",
            ConfigItem.configTypeBool,
            false
        )
    val switch750 =
        addConfig(
            "Switch750",
            ConfigItem.configTypeBool,
            false
        )
    val switch751 =
        addConfig(
            "Switch751",
            ConfigItem.configTypeBool,
            false
        )
    val switch752 =
        addConfig(
            "Switch752",
            ConfigItem.configTypeBool,
            false
        )
    val switch753 =
        addConfig(
            "Switch753",
            ConfigItem.configTypeBool,
            false
        )
    val switch754 =
        addConfig(
            "Switch754",
            ConfigItem.configTypeBool,
            false
        )
    val switch755 =
        addConfig(
            "Switch755",
            ConfigItem.configTypeBool,
            false
        )
    val switch756 =
        addConfig(
            "Switch756",
            ConfigItem.configTypeBool,
            false
        )
    val switch757 =
        addConfig(
            "Switch757",
            ConfigItem.configTypeBool,
            false
        )
    val switch758 =
        addConfig(
            "Switch758",
            ConfigItem.configTypeBool,
            false
        )
    val switch759 =
        addConfig(
            "Switch759",
            ConfigItem.configTypeBool,
            false
        )
    val switch760 =
        addConfig(
            "Switch760",
            ConfigItem.configTypeBool,
            false
        )
    val switch761 =
        addConfig(
            "Switch761",
            ConfigItem.configTypeBool,
            false
        )
    val switch762 =
        addConfig(
            "Switch762",
            ConfigItem.configTypeBool,
            false
        )
    val switch763 =
        addConfig(
            "Switch763",
            ConfigItem.configTypeBool,
            false
        )
    val switch764 =
        addConfig(
            "Switch764",
            ConfigItem.configTypeBool,
            false
        )
    val switch765 =
        addConfig(
            "Switch765",
            ConfigItem.configTypeBool,
            false
        )
    val switch766 =
        addConfig(
            "Switch766",
            ConfigItem.configTypeBool,
            false
        )
    val switch767 =
        addConfig(
            "Switch767",
            ConfigItem.configTypeBool,
            false
        )
    val switch768 =
        addConfig(
            "Switch768",
            ConfigItem.configTypeBool,
            false
        )
    val switch769 =
        addConfig(
            "Switch769",
            ConfigItem.configTypeBool,
            false
        )
    val switch770 =
        addConfig(
            "Switch770",
            ConfigItem.configTypeBool,
            false
        )
    val switch771 =
        addConfig(
            "Switch771",
            ConfigItem.configTypeBool,
            false
        )
    val switch772 =
        addConfig(
            "Switch772",
            ConfigItem.configTypeBool,
            false
        )
    val switch773 =
        addConfig(
            "Switch773",
            ConfigItem.configTypeBool,
            false
        )
    val switch774 =
        addConfig(
            "Switch774",
            ConfigItem.configTypeBool,
            false
        )
    val switch775 =
        addConfig(
            "Switch775",
            ConfigItem.configTypeBool,
            false
        )
    val switch776 =
        addConfig(
            "Switch776",
            ConfigItem.configTypeBool,
            false
        )
    val switch777 =
        addConfig(
            "Switch777",
            ConfigItem.configTypeBool,
            false
        )
    val switch778 =
        addConfig(
            "Switch778",
            ConfigItem.configTypeBool,
            false
        )
    val switch779 =
        addConfig(
            "Switch779",
            ConfigItem.configTypeBool,
            false
        )
    val switch780 =
        addConfig(
            "Switch780",
            ConfigItem.configTypeBool,
            false
        )
    val switch781 =
        addConfig(
            "Switch781",
            ConfigItem.configTypeBool,
            false
        )
    val switch782 =
        addConfig(
            "Switch782",
            ConfigItem.configTypeBool,
            false
        )
    val switch783 =
        addConfig(
            "Switch783",
            ConfigItem.configTypeBool,
            false
        )
    val switch784 =
        addConfig(
            "Switch784",
            ConfigItem.configTypeBool,
            false
        )
    val switch785 =
        addConfig(
            "Switch785",
            ConfigItem.configTypeBool,
            false
        )
    val switch786 =
        addConfig(
            "Switch786",
            ConfigItem.configTypeBool,
            false
        )
    val switch787 =
        addConfig(
            "Switch787",
            ConfigItem.configTypeBool,
            false
        )
    val switch788 =
        addConfig(
            "Switch788",
            ConfigItem.configTypeBool,
            false
        )
    val switch789 =
        addConfig(
            "Switch789",
            ConfigItem.configTypeBool,
            false
        )
    val switch790 =
        addConfig(
            "Switch790",
            ConfigItem.configTypeBool,
            false
        )
    val switch791 =
        addConfig(
            "Switch791",
            ConfigItem.configTypeBool,
            false
        )
    val switch792 =
        addConfig(
            "Switch792",
            ConfigItem.configTypeBool,
            false
        )
    val switch793 =
        addConfig(
            "Switch793",
            ConfigItem.configTypeBool,
            false
        )
    val switch794 =
        addConfig(
            "Switch794",
            ConfigItem.configTypeBool,
            false
        )
    val switch795 =
        addConfig(
            "Switch795",
            ConfigItem.configTypeBool,
            false
        )
    val switch796 =
        addConfig(
            "Switch796",
            ConfigItem.configTypeBool,
            false
        )
    val switch797 =
        addConfig(
            "Switch797",
            ConfigItem.configTypeBool,
            false
        )
    val switch798 =
        addConfig(
            "Switch798",
            ConfigItem.configTypeBool,
            false
        )
    val switch799 =
        addConfig(
            "Switch799",
            ConfigItem.configTypeBool,
            false
        )
    val switch800 =
        addConfig(
            "Switch800",
            ConfigItem.configTypeBool,
            false
        )
    val switch801 =
        addConfig(
            "Switch801",
            ConfigItem.configTypeBool,
            false
        )
    val switch802 =
        addConfig(
            "Switch802",
            ConfigItem.configTypeBool,
            false
        )
    val switch803 =
        addConfig(
            "Switch803",
            ConfigItem.configTypeBool,
            false
        )
    val switch804 =
        addConfig(
            "Switch804",
            ConfigItem.configTypeBool,
            false
        )
    val switch805 =
        addConfig(
            "Switch805",
            ConfigItem.configTypeBool,
            false
        )
    val switch806 =
        addConfig(
            "Switch806",
            ConfigItem.configTypeBool,
            false
        )
    val switch807 =
        addConfig(
            "Switch807",
            ConfigItem.configTypeBool,
            false
        )
    val switch808 =
        addConfig(
            "Switch808",
            ConfigItem.configTypeBool,
            false
        )
    val switch809 =
        addConfig(
            "Switch809",
            ConfigItem.configTypeBool,
            false
        )
    val switch810 =
        addConfig(
            "Switch810",
            ConfigItem.configTypeBool,
            false
        )
    val switch811 =
        addConfig(
            "Switch811",
            ConfigItem.configTypeBool,
            false
        )
    val switch812 =
        addConfig(
            "Switch812",
            ConfigItem.configTypeBool,
            false
        )
    val switch813 =
        addConfig(
            "Switch813",
            ConfigItem.configTypeBool,
            false
        )
    val switch814 =
        addConfig(
            "Switch814",
            ConfigItem.configTypeBool,
            false
        )
    val switch815 =
        addConfig(
            "Switch815",
            ConfigItem.configTypeBool,
            false
        )
    val switch816 =
        addConfig(
            "Switch816",
            ConfigItem.configTypeBool,
            false
        )
    val switch817 =
        addConfig(
            "Switch817",
            ConfigItem.configTypeBool,
            false
        )
    val switch818 =
        addConfig(
            "Switch818",
            ConfigItem.configTypeBool,
            false
        )
    val switch819 =
        addConfig(
            "Switch819",
            ConfigItem.configTypeBool,
            false
        )
    val switch820 =
        addConfig(
            "Switch820",
            ConfigItem.configTypeBool,
            false
        )
    val switch821 =
        addConfig(
            "Switch821",
            ConfigItem.configTypeBool,
            false
        )
    val switch822 =
        addConfig(
            "Switch822",
            ConfigItem.configTypeBool,
            false
        )
    val switch823 =
        addConfig(
            "Switch823",
            ConfigItem.configTypeBool,
            false
        )
    val switch824 =
        addConfig(
            "Switch824",
            ConfigItem.configTypeBool,
            false
        )
    val switch825 =
        addConfig(
            "Switch825",
            ConfigItem.configTypeBool,
            false
        )
    val switch826 =
        addConfig(
            "Switch826",
            ConfigItem.configTypeBool,
            false
        )
    val switch827 =
        addConfig(
            "Switch827",
            ConfigItem.configTypeBool,
            false
        )
    val switch828 =
        addConfig(
            "Switch828",
            ConfigItem.configTypeBool,
            false
        )
    val switch829 =
        addConfig(
            "Switch829",
            ConfigItem.configTypeBool,
            false
        )
    val switch830 =
        addConfig(
            "Switch830",
            ConfigItem.configTypeBool,
            false
        )
    val switch831 =
        addConfig(
            "Switch831",
            ConfigItem.configTypeBool,
            false
        )
    val switch832 =
        addConfig(
            "Switch832",
            ConfigItem.configTypeBool,
            false
        )
    val switch833 =
        addConfig(
            "Switch833",
            ConfigItem.configTypeBool,
            false
        )
    val switch834 =
        addConfig(
            "Switch834",
            ConfigItem.configTypeBool,
            false
        )
    val switch835 =
        addConfig(
            "Switch835",
            ConfigItem.configTypeBool,
            false
        )
    val switch836 =
        addConfig(
            "Switch836",
            ConfigItem.configTypeBool,
            false
        )
    val switch837 =
        addConfig(
            "Switch837",
            ConfigItem.configTypeBool,
            false
        )
    val switch838 =
        addConfig(
            "Switch838",
            ConfigItem.configTypeBool,
            false
        )
    val switch839 =
        addConfig(
            "Switch839",
            ConfigItem.configTypeBool,
            false
        )
    val switch840 =
        addConfig(
            "Switch840",
            ConfigItem.configTypeBool,
            false
        )
    val switch841 =
        addConfig(
            "Switch841",
            ConfigItem.configTypeBool,
            false
        )
    val switch842 =
        addConfig(
            "Switch842",
            ConfigItem.configTypeBool,
            false
        )
    val switch843 =
        addConfig(
            "Switch843",
            ConfigItem.configTypeBool,
            false
        )
    val switch844 =
        addConfig(
            "Switch844",
            ConfigItem.configTypeBool,
            false
        )
    val switch845 =
        addConfig(
            "Switch845",
            ConfigItem.configTypeBool,
            false
        )
    val switch846 =
        addConfig(
            "Switch846",
            ConfigItem.configTypeBool,
            false
        )
    val switch847 =
        addConfig(
            "Switch847",
            ConfigItem.configTypeBool,
            false
        )
    val switch848 =
        addConfig(
            "Switch848",
            ConfigItem.configTypeBool,
            false
        )
    val switch849 =
        addConfig(
            "Switch849",
            ConfigItem.configTypeBool,
            false
        )
    val switch850 =
        addConfig(
            "Switch850",
            ConfigItem.configTypeBool,
            false
        )
    val switch851 =
        addConfig(
            "Switch851",
            ConfigItem.configTypeBool,
            false
        )
    val switch852 =
        addConfig(
            "Switch852",
            ConfigItem.configTypeBool,
            false
        )
    val switch853 =
        addConfig(
            "Switch853",
            ConfigItem.configTypeBool,
            false
        )
    val switch854 =
        addConfig(
            "Switch854",
            ConfigItem.configTypeBool,
            false
        )
    val switch855 =
        addConfig(
            "Switch855",
            ConfigItem.configTypeBool,
            false
        )
    val switch856 =
        addConfig(
            "Switch856",
            ConfigItem.configTypeBool,
            false
        )
    val switch857 =
        addConfig(
            "Switch857",
            ConfigItem.configTypeBool,
            false
        )
    val switch858 =
        addConfig(
            "Switch858",
            ConfigItem.configTypeBool,
            false
        )
    val switch859 =
        addConfig(
            "Switch859",
            ConfigItem.configTypeBool,
            false
        )
    val switch860 =
        addConfig(
            "Switch860",
            ConfigItem.configTypeBool,
            false
        )
    val switch861 =
        addConfig(
            "Switch861",
            ConfigItem.configTypeBool,
            false
        )
    val switch862 =
        addConfig(
            "Switch862",
            ConfigItem.configTypeBool,
            false
        )
    val switch863 =
        addConfig(
            "Switch863",
            ConfigItem.configTypeBool,
            false
        )
    val switch864 =
        addConfig(
            "Switch864",
            ConfigItem.configTypeBool,
            false
        )
    val switch865 =
        addConfig(
            "Switch865",
            ConfigItem.configTypeBool,
            false
        )
    val switch866 =
        addConfig(
            "Switch866",
            ConfigItem.configTypeBool,
            false
        )
    val switch867 =
        addConfig(
            "Switch867",
            ConfigItem.configTypeBool,
            false
        )
    val switch868 =
        addConfig(
            "Switch868",
            ConfigItem.configTypeBool,
            false
        )
    val switch869 =
        addConfig(
            "Switch869",
            ConfigItem.configTypeBool,
            false
        )
    val switch870 =
        addConfig(
            "Switch870",
            ConfigItem.configTypeBool,
            false
        )
    val switch871 =
        addConfig(
            "Switch871",
            ConfigItem.configTypeBool,
            false
        )
    val switch872 =
        addConfig(
            "Switch872",
            ConfigItem.configTypeBool,
            false
        )
    val switch873 =
        addConfig(
            "Switch873",
            ConfigItem.configTypeBool,
            false
        )
    val switch874 =
        addConfig(
            "Switch874",
            ConfigItem.configTypeBool,
            false
        )
    val switch875 =
        addConfig(
            "Switch875",
            ConfigItem.configTypeBool,
            false
        )
    val switch876 =
        addConfig(
            "Switch876",
            ConfigItem.configTypeBool,
            false
        )
    val switch877 =
        addConfig(
            "Switch877",
            ConfigItem.configTypeBool,
            false
        )
    val switch878 =
        addConfig(
            "Switch878",
            ConfigItem.configTypeBool,
            false
        )
    val switch879 =
        addConfig(
            "Switch879",
            ConfigItem.configTypeBool,
            false
        )
    val switch880 =
        addConfig(
            "Switch880",
            ConfigItem.configTypeBool,
            false
        )
    val switch881 =
        addConfig(
            "Switch881",
            ConfigItem.configTypeBool,
            false
        )
    val switch882 =
        addConfig(
            "Switch882",
            ConfigItem.configTypeBool,
            false
        )
    val switch883 =
        addConfig(
            "Switch883",
            ConfigItem.configTypeBool,
            false
        )
    val switch884 =
        addConfig(
            "Switch884",
            ConfigItem.configTypeBool,
            false
        )
    val switch885 =
        addConfig(
            "Switch885",
            ConfigItem.configTypeBool,
            false
        )
    val switch886 =
        addConfig(
            "Switch886",
            ConfigItem.configTypeBool,
            false
        )
    val switch887 =
        addConfig(
            "Switch887",
            ConfigItem.configTypeBool,
            false
        )
    val switch888 =
        addConfig(
            "Switch888",
            ConfigItem.configTypeBool,
            false
        )
    val switch889 =
        addConfig(
            "Switch889",
            ConfigItem.configTypeBool,
            false
        )
    val switch890 =
        addConfig(
            "Switch890",
            ConfigItem.configTypeBool,
            false
        )
    val switch891 =
        addConfig(
            "Switch891",
            ConfigItem.configTypeBool,
            false
        )
    val switch892 =
        addConfig(
            "Switch892",
            ConfigItem.configTypeBool,
            false
        )
    val switch893 =
        addConfig(
            "Switch893",
            ConfigItem.configTypeBool,
            false
        )
    val switch894 =
        addConfig(
            "Switch894",
            ConfigItem.configTypeBool,
            false
        )
    val switch895 =
        addConfig(
            "Switch895",
            ConfigItem.configTypeBool,
            false
        )
    val switch896 =
        addConfig(
            "Switch896",
            ConfigItem.configTypeBool,
            false
        )
    val switch897 =
        addConfig(
            "Switch897",
            ConfigItem.configTypeBool,
            false
        )
    val switch898 =
        addConfig(
            "Switch898",
            ConfigItem.configTypeBool,
            false
        )
    val switch899 =
        addConfig(
            "Switch899",
            ConfigItem.configTypeBool,
            false
        )
    val switch900 =
        addConfig(
            "Switch900",
            ConfigItem.configTypeBool,
            false
        )
    val switch901 =
        addConfig(
            "Switch901",
            ConfigItem.configTypeBool,
            false
        )
    val switch902 =
        addConfig(
            "Switch902",
            ConfigItem.configTypeBool,
            false
        )
    val switch903 =
        addConfig(
            "Switch903",
            ConfigItem.configTypeBool,
            false
        )
    val switch904 =
        addConfig(
            "Switch904",
            ConfigItem.configTypeBool,
            false
        )
    val switch905 =
        addConfig(
            "Switch905",
            ConfigItem.configTypeBool,
            false
        )
    val switch906 =
        addConfig(
            "Switch906",
            ConfigItem.configTypeBool,
            false
        )
    val switch907 =
        addConfig(
            "Switch907",
            ConfigItem.configTypeBool,
            false
        )
    val switch908 =
        addConfig(
            "Switch908",
            ConfigItem.configTypeBool,
            false
        )
    val switch909 =
        addConfig(
            "Switch909",
            ConfigItem.configTypeBool,
            false
        )
    val switch910 =
        addConfig(
            "Switch910",
            ConfigItem.configTypeBool,
            false
        )
    val switch911 =
        addConfig(
            "Switch911",
            ConfigItem.configTypeBool,
            false
        )
    val switch912 =
        addConfig(
            "Switch912",
            ConfigItem.configTypeBool,
            false
        )
    val switch913 =
        addConfig(
            "Switch913",
            ConfigItem.configTypeBool,
            false
        )
    val switch914 =
        addConfig(
            "Switch914",
            ConfigItem.configTypeBool,
            false
        )
    val switch915 =
        addConfig(
            "Switch915",
            ConfigItem.configTypeBool,
            false
        )
    val switch916 =
        addConfig(
            "Switch916",
            ConfigItem.configTypeBool,
            false
        )
    val switch917 =
        addConfig(
            "Switch917",
            ConfigItem.configTypeBool,
            false
        )
    val switch918 =
        addConfig(
            "Switch918",
            ConfigItem.configTypeBool,
            false
        )
    val switch919 =
        addConfig(
            "Switch919",
            ConfigItem.configTypeBool,
            false
        )
    val switch920 =
        addConfig(
            "Switch920",
            ConfigItem.configTypeBool,
            false
        )
    val switch921 =
        addConfig(
            "Switch921",
            ConfigItem.configTypeBool,
            false
        )
    val switch922 =
        addConfig(
            "Switch922",
            ConfigItem.configTypeBool,
            false
        )
    val switch923 =
        addConfig(
            "Switch923",
            ConfigItem.configTypeBool,
            false
        )
    val switch924 =
        addConfig(
            "Switch924",
            ConfigItem.configTypeBool,
            false
        )
    val switch925 =
        addConfig(
            "Switch925",
            ConfigItem.configTypeBool,
            false
        )
    val switch926 =
        addConfig(
            "Switch926",
            ConfigItem.configTypeBool,
            false
        )
    val switch927 =
        addConfig(
            "Switch927",
            ConfigItem.configTypeBool,
            false
        )
    val switch928 =
        addConfig(
            "Switch928",
            ConfigItem.configTypeBool,
            false
        )
    val switch929 =
        addConfig(
            "Switch929",
            ConfigItem.configTypeBool,
            false
        )
    val switch930 =
        addConfig(
            "Switch930",
            ConfigItem.configTypeBool,
            false
        )
    val switch931 =
        addConfig(
            "Switch931",
            ConfigItem.configTypeBool,
            false
        )
    val switch932 =
        addConfig(
            "Switch932",
            ConfigItem.configTypeBool,
            false
        )
    val switch933 =
        addConfig(
            "Switch933",
            ConfigItem.configTypeBool,
            false
        )
    val switch934 =
        addConfig(
            "Switch934",
            ConfigItem.configTypeBool,
            false
        )
    val switch935 =
        addConfig(
            "Switch935",
            ConfigItem.configTypeBool,
            false
        )
    val switch936 =
        addConfig(
            "Switch936",
            ConfigItem.configTypeBool,
            false
        )
    val switch937 =
        addConfig(
            "Switch937",
            ConfigItem.configTypeBool,
            false
        )
    val switch938 =
        addConfig(
            "Switch938",
            ConfigItem.configTypeBool,
            false
        )
    val switch939 =
        addConfig(
            "Switch939",
            ConfigItem.configTypeBool,
            false
        )
    val switch940 =
        addConfig(
            "Switch940",
            ConfigItem.configTypeBool,
            false
        )
    val switch941 =
        addConfig(
            "Switch941",
            ConfigItem.configTypeBool,
            false
        )
    val switch942 =
        addConfig(
            "Switch942",
            ConfigItem.configTypeBool,
            false
        )
    val switch943 =
        addConfig(
            "Switch943",
            ConfigItem.configTypeBool,
            false
        )
    val switch944 =
        addConfig(
            "Switch944",
            ConfigItem.configTypeBool,
            false
        )
    val switch945 =
        addConfig(
            "Switch945",
            ConfigItem.configTypeBool,
            false
        )
    val switch946 =
        addConfig(
            "Switch946",
            ConfigItem.configTypeBool,
            false
        )
    val switch947 =
        addConfig(
            "Switch947",
            ConfigItem.configTypeBool,
            false
        )
    val switch948 =
        addConfig(
            "Switch948",
            ConfigItem.configTypeBool,
            false
        )
    val switch949 =
        addConfig(
            "Switch949",
            ConfigItem.configTypeBool,
            false
        )
    val switch950 =
        addConfig(
            "Switch950",
            ConfigItem.configTypeBool,
            false
        )
    val switch951 =
        addConfig(
            "Switch951",
            ConfigItem.configTypeBool,
            false
        )
    val switch952 =
        addConfig(
            "Switch952",
            ConfigItem.configTypeBool,
            false
        )
    val switch953 =
        addConfig(
            "Switch953",
            ConfigItem.configTypeBool,
            false
        )
    val switch954 =
        addConfig(
            "Switch954",
            ConfigItem.configTypeBool,
            false
        )
    val switch955 =
        addConfig(
            "Switch955",
            ConfigItem.configTypeBool,
            false
        )
    val switch956 =
        addConfig(
            "Switch956",
            ConfigItem.configTypeBool,
            false
        )
    val switch957 =
        addConfig(
            "Switch957",
            ConfigItem.configTypeBool,
            false
        )
    val switch958 =
        addConfig(
            "Switch958",
            ConfigItem.configTypeBool,
            false
        )
    val switch959 =
        addConfig(
            "Switch959",
            ConfigItem.configTypeBool,
            false
        )
    val switch960 =
        addConfig(
            "Switch960",
            ConfigItem.configTypeBool,
            false
        )
    val switch961 =
        addConfig(
            "Switch961",
            ConfigItem.configTypeBool,
            false
        )
    val switch962 =
        addConfig(
            "Switch962",
            ConfigItem.configTypeBool,
            false
        )
    val switch963 =
        addConfig(
            "Switch963",
            ConfigItem.configTypeBool,
            false
        )
    val switch964 =
        addConfig(
            "Switch964",
            ConfigItem.configTypeBool,
            false
        )
    val switch965 =
        addConfig(
            "Switch965",
            ConfigItem.configTypeBool,
            false
        )
    val switch966 =
        addConfig(
            "Switch966",
            ConfigItem.configTypeBool,
            false
        )
    val switch967 =
        addConfig(
            "Switch967",
            ConfigItem.configTypeBool,
            false
        )
    val switch968 =
        addConfig(
            "Switch968",
            ConfigItem.configTypeBool,
            false
        )
    val switch969 =
        addConfig(
            "Switch969",
            ConfigItem.configTypeBool,
            false
        )
    val switch970 =
        addConfig(
            "Switch970",
            ConfigItem.configTypeBool,
            false
        )
    val switch971 =
        addConfig(
            "Switch971",
            ConfigItem.configTypeBool,
            false
        )
    val switch972 =
        addConfig(
            "Switch972",
            ConfigItem.configTypeBool,
            false
        )
    val switch973 =
        addConfig(
            "Switch973",
            ConfigItem.configTypeBool,
            false
        )
    val switch974 =
        addConfig(
            "Switch974",
            ConfigItem.configTypeBool,
            false
        )
    val switch975 =
        addConfig(
            "Switch975",
            ConfigItem.configTypeBool,
            false
        )
    val switch976 =
        addConfig(
            "Switch976",
            ConfigItem.configTypeBool,
            false
        )
    val switch977 =
        addConfig(
            "Switch977",
            ConfigItem.configTypeBool,
            false
        )
    val switch978 =
        addConfig(
            "Switch978",
            ConfigItem.configTypeBool,
            false
        )
    val switch979 =
        addConfig(
            "Switch979",
            ConfigItem.configTypeBool,
            false
        )
    val switch980 =
        addConfig(
            "Switch980",
            ConfigItem.configTypeBool,
            false
        )
    val switch981 =
        addConfig(
            "Switch981",
            ConfigItem.configTypeBool,
            false
        )
    val switch982 =
        addConfig(
            "Switch982",
            ConfigItem.configTypeBool,
            false
        )
    val switch983 =
        addConfig(
            "Switch983",
            ConfigItem.configTypeBool,
            false
        )
    val switch984 =
        addConfig(
            "Switch984",
            ConfigItem.configTypeBool,
            false
        )
    val switch985 =
        addConfig(
            "Switch985",
            ConfigItem.configTypeBool,
            false
        )
    val switch986 =
        addConfig(
            "Switch986",
            ConfigItem.configTypeBool,
            false
        )
    val switch987 =
        addConfig(
            "Switch987",
            ConfigItem.configTypeBool,
            false
        )
    val switch988 =
        addConfig(
            "Switch988",
            ConfigItem.configTypeBool,
            false
        )
    val switch989 =
        addConfig(
            "Switch989",
            ConfigItem.configTypeBool,
            false
        )
    val switch990 =
        addConfig(
            "Switch990",
            ConfigItem.configTypeBool,
            false
        )
    val switch991 =
        addConfig(
            "Switch991",
            ConfigItem.configTypeBool,
            false
        )
    val switch992 =
        addConfig(
            "Switch992",
            ConfigItem.configTypeBool,
            false
        )
    val switch993 =
        addConfig(
            "Switch993",
            ConfigItem.configTypeBool,
            false
        )
    val switch994 =
        addConfig(
            "Switch994",
            ConfigItem.configTypeBool,
            false
        )
    val switch995 =
        addConfig(
            "Switch995",
            ConfigItem.configTypeBool,
            false
        )
    val switch996 =
        addConfig(
            "Switch996",
            ConfigItem.configTypeBool,
            false
        )
    val switch997 =
        addConfig(
            "Switch997",
            ConfigItem.configTypeBool,
            false
        )
    val switch998 =
        addConfig(
            "Switch998",
            ConfigItem.configTypeBool,
            false
        )
    val switch999 =
        addConfig(
            "Switch999",
            ConfigItem.configTypeBool,
            false
        )
    val switch1000 =
        addConfig(
            "Switch1000",
            ConfigItem.configTypeBool,
            false
        )
    val switch1001 =
        addConfig(
            "Switch1001",
            ConfigItem.configTypeBool,
            false
        )
    val switch1002 =
        addConfig(
            "Switch1002",
            ConfigItem.configTypeBool,
            false
        )
    val switch1003 =
        addConfig(
            "Switch1003",
            ConfigItem.configTypeBool,
            false
        )
    val switch1004 =
        addConfig(
            "Switch1004",
            ConfigItem.configTypeBool,
            false
        )
    val switch1005 =
        addConfig(
            "Switch1005",
            ConfigItem.configTypeBool,
            false
        )
    val switch1006 =
        addConfig(
            "Switch1006",
            ConfigItem.configTypeBool,
            false
        )
    val switch1007 =
        addConfig(
            "Switch1007",
            ConfigItem.configTypeBool,
            false
        )
    val switch1008 =
        addConfig(
            "Switch1008",
            ConfigItem.configTypeBool,
            false
        )
    val switch1009 =
        addConfig(
            "Switch1009",
            ConfigItem.configTypeBool,
            false
        )
    val switch1010 =
        addConfig(
            "Switch1010",
            ConfigItem.configTypeBool,
            false
        )
    val switch1011 =
        addConfig(
            "Switch1011",
            ConfigItem.configTypeBool,
            false
        )
    val switch1012 =
        addConfig(
            "Switch1012",
            ConfigItem.configTypeBool,
            false
        )
    val switch1013 =
        addConfig(
            "Switch1013",
            ConfigItem.configTypeBool,
            false
        )
    val switch1014 =
        addConfig(
            "Switch1014",
            ConfigItem.configTypeBool,
            false
        )
    val switch1015 =
        addConfig(
            "Switch1015",
            ConfigItem.configTypeBool,
            false
        )
    val switch1016 =
        addConfig(
            "Switch1016",
            ConfigItem.configTypeBool,
            false
        )
    val switch1017 =
        addConfig(
            "Switch1017",
            ConfigItem.configTypeBool,
            false
        )
    val switch1018 =
        addConfig(
            "Switch1018",
            ConfigItem.configTypeBool,
            false
        )
    val switch1019 =
        addConfig(
            "Switch1019",
            ConfigItem.configTypeBool,
            false
        )
    val switch1020 =
        addConfig(
            "Switch1020",
            ConfigItem.configTypeBool,
            false
        )
    val switch1021 =
        addConfig(
            "Switch1021",
            ConfigItem.configTypeBool,
            false
        )
    val switch1022 =
        addConfig(
            "Switch1022",
            ConfigItem.configTypeBool,
            false
        )
    val switch1023 =
        addConfig(
            "Switch1023",
            ConfigItem.configTypeBool,
            false
        )
    val switch1024 =
        addConfig(
            "Switch1024",
            ConfigItem.configTypeBool,
            false
        )
    val switch1025 =
        addConfig(
            "Switch1025",
            ConfigItem.configTypeBool,
            false
        )
    val switch1026 =
        addConfig(
            "Switch1026",
            ConfigItem.configTypeBool,
            false
        )
    val switch1027 =
        addConfig(
            "Switch1027",
            ConfigItem.configTypeBool,
            false
        )
    val switch1028 =
        addConfig(
            "Switch1028",
            ConfigItem.configTypeBool,
            false
        )
    val switch1029 =
        addConfig(
            "Switch1029",
            ConfigItem.configTypeBool,
            false
        )
    val switch1030 =
        addConfig(
            "Switch1030",
            ConfigItem.configTypeBool,
            false
        )
    val switch1031 =
        addConfig(
            "Switch1031",
            ConfigItem.configTypeBool,
            false
        )
    val switch1032 =
        addConfig(
            "Switch1032",
            ConfigItem.configTypeBool,
            false
        )
    val switch1033 =
        addConfig(
            "Switch1033",
            ConfigItem.configTypeBool,
            false
        )
    val switch1034 =
        addConfig(
            "Switch1034",
            ConfigItem.configTypeBool,
            false
        )
    val switch1035 =
        addConfig(
            "Switch1035",
            ConfigItem.configTypeBool,
            false
        )
    val switch1036 =
        addConfig(
            "Switch1036",
            ConfigItem.configTypeBool,
            false
        )
    val switch1037 =
        addConfig(
            "Switch1037",
            ConfigItem.configTypeBool,
            false
        )
    val switch1038 =
        addConfig(
            "Switch1038",
            ConfigItem.configTypeBool,
            false
        )
    val switch1039 =
        addConfig(
            "Switch1039",
            ConfigItem.configTypeBool,
            false
        )
    val switch1040 =
        addConfig(
            "Switch1040",
            ConfigItem.configTypeBool,
            false
        )
    val switch1041 =
        addConfig(
            "Switch1041",
            ConfigItem.configTypeBool,
            false
        )
    val switch1042 =
        addConfig(
            "Switch1042",
            ConfigItem.configTypeBool,
            false
        )
    val switch1043 =
        addConfig(
            "Switch1043",
            ConfigItem.configTypeBool,
            false
        )
    val switch1044 =
        addConfig(
            "Switch1044",
            ConfigItem.configTypeBool,
            false
        )
    val switch1045 =
        addConfig(
            "Switch1045",
            ConfigItem.configTypeBool,
            false
        )
    val switch1046 =
        addConfig(
            "Switch1046",
            ConfigItem.configTypeBool,
            false
        )
    val switch1047 =
        addConfig(
            "Switch1047",
            ConfigItem.configTypeBool,
            false
        )
    val switch1048 =
        addConfig(
            "Switch1048",
            ConfigItem.configTypeBool,
            false
        )
    val switch1049 =
        addConfig(
            "Switch1049",
            ConfigItem.configTypeBool,
            false
        )
    val switch1050 =
        addConfig(
            "Switch1050",
            ConfigItem.configTypeBool,
            false
        )
    val switch1051 =
        addConfig(
            "Switch1051",
            ConfigItem.configTypeBool,
            false
        )
    val switch1052 =
        addConfig(
            "Switch1052",
            ConfigItem.configTypeBool,
            false
        )
    val switch1053 =
        addConfig(
            "Switch1053",
            ConfigItem.configTypeBool,
            false
        )
    val switch1054 =
        addConfig(
            "Switch1054",
            ConfigItem.configTypeBool,
            false
        )
    val switch1055 =
        addConfig(
            "Switch1055",
            ConfigItem.configTypeBool,
            false
        )
    val switch1056 =
        addConfig(
            "Switch1056",
            ConfigItem.configTypeBool,
            false
        )
    val switch1057 =
        addConfig(
            "Switch1057",
            ConfigItem.configTypeBool,
            false
        )
    val switch1058 =
        addConfig(
            "Switch1058",
            ConfigItem.configTypeBool,
            false
        )
    val switch1059 =
        addConfig(
            "Switch1059",
            ConfigItem.configTypeBool,
            false
        )
    val switch1060 =
        addConfig(
            "Switch1060",
            ConfigItem.configTypeBool,
            false
        )
    val switch1061 =
        addConfig(
            "Switch1061",
            ConfigItem.configTypeBool,
            false
        )
    val switch1062 =
        addConfig(
            "Switch1062",
            ConfigItem.configTypeBool,
            false
        )
    val switch1063 =
        addConfig(
            "Switch1063",
            ConfigItem.configTypeBool,
            false
        )
    val switch1064 =
        addConfig(
            "Switch1064",
            ConfigItem.configTypeBool,
            false
        )
    val switch1065 =
        addConfig(
            "Switch1065",
            ConfigItem.configTypeBool,
            false
        )
    val switch1066 =
        addConfig(
            "Switch1066",
            ConfigItem.configTypeBool,
            false
        )
    val switch1067 =
        addConfig(
            "Switch1067",
            ConfigItem.configTypeBool,
            false
        )
    val switch1068 =
        addConfig(
            "Switch1068",
            ConfigItem.configTypeBool,
            false
        )
    val switch1069 =
        addConfig(
            "Switch1069",
            ConfigItem.configTypeBool,
            false
        )
    val switch1070 =
        addConfig(
            "Switch1070",
            ConfigItem.configTypeBool,
            false
        )
    val switch1071 =
        addConfig(
            "Switch1071",
            ConfigItem.configTypeBool,
            false
        )
    val switch1072 =
        addConfig(
            "Switch1072",
            ConfigItem.configTypeBool,
            false
        )
    val switch1073 =
        addConfig(
            "Switch1073",
            ConfigItem.configTypeBool,
            false
        )
    val switch1074 =
        addConfig(
            "Switch1074",
            ConfigItem.configTypeBool,
            false
        )
    val switch1075 =
        addConfig(
            "Switch1075",
            ConfigItem.configTypeBool,
            false
        )
    val switch1076 =
        addConfig(
            "Switch1076",
            ConfigItem.configTypeBool,
            false
        )
    val switch1077 =
        addConfig(
            "Switch1077",
            ConfigItem.configTypeBool,
            false
        )
    val switch1078 =
        addConfig(
            "Switch1078",
            ConfigItem.configTypeBool,
            false
        )
    val switch1079 =
        addConfig(
            "Switch1079",
            ConfigItem.configTypeBool,
            false
        )
    val switch1080 =
        addConfig(
            "Switch1080",
            ConfigItem.configTypeBool,
            false
        )
    val switch1081 =
        addConfig(
            "Switch1081",
            ConfigItem.configTypeBool,
            false
        )
    val switch1082 =
        addConfig(
            "Switch1082",
            ConfigItem.configTypeBool,
            false
        )
    val switch1083 =
        addConfig(
            "Switch1083",
            ConfigItem.configTypeBool,
            false
        )
    val switch1084 =
        addConfig(
            "Switch1084",
            ConfigItem.configTypeBool,
            false
        )
    val switch1085 =
        addConfig(
            "Switch1085",
            ConfigItem.configTypeBool,
            false
        )
    val switch1086 =
        addConfig(
            "Switch1086",
            ConfigItem.configTypeBool,
            false
        )
    val switch1087 =
        addConfig(
            "Switch1087",
            ConfigItem.configTypeBool,
            false
        )
    val switch1088 =
        addConfig(
            "Switch1088",
            ConfigItem.configTypeBool,
            false
        )
    val switch1089 =
        addConfig(
            "Switch1089",
            ConfigItem.configTypeBool,
            false
        )
    val switch1090 =
        addConfig(
            "Switch1090",
            ConfigItem.configTypeBool,
            false
        )
    val switch1091 =
        addConfig(
            "Switch1091",
            ConfigItem.configTypeBool,
            false
        )
    val switch1092 =
        addConfig(
            "Switch1092",
            ConfigItem.configTypeBool,
            false
        )
    val switch1093 =
        addConfig(
            "Switch1093",
            ConfigItem.configTypeBool,
            false
        )
    val switch1094 =
        addConfig(
            "Switch1094",
            ConfigItem.configTypeBool,
            false
        )
    val switch1095 =
        addConfig(
            "Switch1095",
            ConfigItem.configTypeBool,
            false
        )
    val switch1096 =
        addConfig(
            "Switch1096",
            ConfigItem.configTypeBool,
            false
        )
    val switch1097 =
        addConfig(
            "Switch1097",
            ConfigItem.configTypeBool,
            false
        )
    val switch1098 =
        addConfig(
            "Switch1098",
            ConfigItem.configTypeBool,
            false
        )
    val switch1099 =
        addConfig(
            "Switch1099",
            ConfigItem.configTypeBool,
            false
        )
    val switch1100 =
        addConfig(
            "Switch1100",
            ConfigItem.configTypeBool,
            false
        )
    val switch1101 =
        addConfig(
            "Switch1101",
            ConfigItem.configTypeBool,
            false
        )
    val switch1102 =
        addConfig(
            "Switch1102",
            ConfigItem.configTypeBool,
            false
        )
    val switch1103 =
        addConfig(
            "Switch1103",
            ConfigItem.configTypeBool,
            false
        )
    val switch1104 =
        addConfig(
            "Switch1104",
            ConfigItem.configTypeBool,
            false
        )
    val switch1105 =
        addConfig(
            "Switch1105",
            ConfigItem.configTypeBool,
            false
        )
    val switch1106 =
        addConfig(
            "Switch1106",
            ConfigItem.configTypeBool,
            false
        )
    val switch1107 =
        addConfig(
            "Switch1107",
            ConfigItem.configTypeBool,
            false
        )
    val switch1108 =
        addConfig(
            "Switch1108",
            ConfigItem.configTypeBool,
            false
        )
    val switch1109 =
        addConfig(
            "Switch1109",
            ConfigItem.configTypeBool,
            false
        )
    val switch1110 =
        addConfig(
            "Switch1110",
            ConfigItem.configTypeBool,
            false
        )
    val switch1111 =
        addConfig(
            "Switch1111",
            ConfigItem.configTypeBool,
            false
        )
    val switch1112 =
        addConfig(
            "Switch1112",
            ConfigItem.configTypeBool,
            false
        )
    val switch1113 =
        addConfig(
            "Switch1113",
            ConfigItem.configTypeBool,
            false
        )
    val switch1114 =
        addConfig(
            "Switch1114",
            ConfigItem.configTypeBool,
            false
        )
    val switch1115 =
        addConfig(
            "Switch1115",
            ConfigItem.configTypeBool,
            false
        )
    val switch1116 =
        addConfig(
            "Switch1116",
            ConfigItem.configTypeBool,
            false
        )
    val switch1117 =
        addConfig(
            "Switch1117",
            ConfigItem.configTypeBool,
            false
        )
    val switch1118 =
        addConfig(
            "Switch1118",
            ConfigItem.configTypeBool,
            false
        )
    val switch1119 =
        addConfig(
            "Switch1119",
            ConfigItem.configTypeBool,
            false
        )
    val switch1120 =
        addConfig(
            "Switch1120",
            ConfigItem.configTypeBool,
            false
        )
    val switch1121 =
        addConfig(
            "Switch1121",
            ConfigItem.configTypeBool,
            false
        )
    val switch1122 =
        addConfig(
            "Switch1122",
            ConfigItem.configTypeBool,
            false
        )
    val switch1123 =
        addConfig(
            "Switch1123",
            ConfigItem.configTypeBool,
            false
        )
    val switch1124 =
        addConfig(
            "Switch1124",
            ConfigItem.configTypeBool,
            false
        )
    val switch1125 =
        addConfig(
            "Switch1125",
            ConfigItem.configTypeBool,
            false
        )
    val switch1126 =
        addConfig(
            "Switch1126",
            ConfigItem.configTypeBool,
            false
        )
    val switch1127 =
        addConfig(
            "Switch1127",
            ConfigItem.configTypeBool,
            false
        )
    val switch1128 =
        addConfig(
            "Switch1128",
            ConfigItem.configTypeBool,
            false
        )
    val switch1129 =
        addConfig(
            "Switch1129",
            ConfigItem.configTypeBool,
            false
        )
    val switch1130 =
        addConfig(
            "Switch1130",
            ConfigItem.configTypeBool,
            false
        )
    val switch1131 =
        addConfig(
            "Switch1131",
            ConfigItem.configTypeBool,
            false
        )
    val switch1132 =
        addConfig(
            "Switch1132",
            ConfigItem.configTypeBool,
            false
        )
    val switch1133 =
        addConfig(
            "Switch1133",
            ConfigItem.configTypeBool,
            false
        )
    val switch1134 =
        addConfig(
            "Switch1134",
            ConfigItem.configTypeBool,
            false
        )
    val switch1135 =
        addConfig(
            "Switch1135",
            ConfigItem.configTypeBool,
            false
        )
    val switch1136 =
        addConfig(
            "Switch1136",
            ConfigItem.configTypeBool,
            false
        )
    val switch1137 =
        addConfig(
            "Switch1137",
            ConfigItem.configTypeBool,
            false
        )
    val switch1138 =
        addConfig(
            "Switch1138",
            ConfigItem.configTypeBool,
            false
        )
    val switch1139 =
        addConfig(
            "Switch1139",
            ConfigItem.configTypeBool,
            false
        )
    val switch1140 =
        addConfig(
            "Switch1140",
            ConfigItem.configTypeBool,
            false
        )
    val switch1141 =
        addConfig(
            "Switch1141",
            ConfigItem.configTypeBool,
            false
        )
    val switch1142 =
        addConfig(
            "Switch1142",
            ConfigItem.configTypeBool,
            false
        )
    val switch1143 =
        addConfig(
            "Switch1143",
            ConfigItem.configTypeBool,
            false
        )
    val switch1144 =
        addConfig(
            "Switch1144",
            ConfigItem.configTypeBool,
            false
        )
    val switch1145 =
        addConfig(
            "Switch1145",
            ConfigItem.configTypeBool,
            false
        )
    val switch1146 =
        addConfig(
            "Switch1146",
            ConfigItem.configTypeBool,
            false
        )
    val switch1147 =
        addConfig(
            "Switch1147",
            ConfigItem.configTypeBool,
            false
        )
    val switch1148 =
        addConfig(
            "Switch1148",
            ConfigItem.configTypeBool,
            false
        )
    val switch1149 =
        addConfig(
            "Switch1149",
            ConfigItem.configTypeBool,
            false
        )
    val switch1150 =
        addConfig(
            "Switch1150",
            ConfigItem.configTypeBool,
            false
        )
    val switch1151 =
        addConfig(
            "Switch1151",
            ConfigItem.configTypeBool,
            false
        )
    val switch1152 =
        addConfig(
            "Switch1152",
            ConfigItem.configTypeBool,
            false
        )
    val switch1153 =
        addConfig(
            "Switch1153",
            ConfigItem.configTypeBool,
            false
        )
    val switch1154 =
        addConfig(
            "Switch1154",
            ConfigItem.configTypeBool,
            false
        )
    val switch1155 =
        addConfig(
            "Switch1155",
            ConfigItem.configTypeBool,
            false
        )
    val switch1156 =
        addConfig(
            "Switch1156",
            ConfigItem.configTypeBool,
            false
        )
    val switch1157 =
        addConfig(
            "Switch1157",
            ConfigItem.configTypeBool,
            false
        )
    val switch1158 =
        addConfig(
            "Switch1158",
            ConfigItem.configTypeBool,
            false
        )
    val switch1159 =
        addConfig(
            "Switch1159",
            ConfigItem.configTypeBool,
            false
        )
    val switch1160 =
        addConfig(
            "Switch1160",
            ConfigItem.configTypeBool,
            false
        )
    val switch1161 =
        addConfig(
            "Switch1161",
            ConfigItem.configTypeBool,
            false
        )
    val switch1162 =
        addConfig(
            "Switch1162",
            ConfigItem.configTypeBool,
            false
        )
    val switch1163 =
        addConfig(
            "Switch1163",
            ConfigItem.configTypeBool,
            false
        )
    val switch1164 =
        addConfig(
            "Switch1164",
            ConfigItem.configTypeBool,
            false
        )
    val switch1165 =
        addConfig(
            "Switch1165",
            ConfigItem.configTypeBool,
            false
        )
    val switch1166 =
        addConfig(
            "Switch1166",
            ConfigItem.configTypeBool,
            false
        )
    val switch1167 =
        addConfig(
            "Switch1167",
            ConfigItem.configTypeBool,
            false
        )
    val switch1168 =
        addConfig(
            "Switch1168",
            ConfigItem.configTypeBool,
            false
        )
    val switch1169 =
        addConfig(
            "Switch1169",
            ConfigItem.configTypeBool,
            false
        )
    val switch1170 =
        addConfig(
            "Switch1170",
            ConfigItem.configTypeBool,
            false
        )
    val switch1171 =
        addConfig(
            "Switch1171",
            ConfigItem.configTypeBool,
            false
        )
    val switch1172 =
        addConfig(
            "Switch1172",
            ConfigItem.configTypeBool,
            false
        )
    val switch1173 =
        addConfig(
            "Switch1173",
            ConfigItem.configTypeBool,
            false
        )
    val switch1174 =
        addConfig(
            "Switch1174",
            ConfigItem.configTypeBool,
            false
        )
    val switch1175 =
        addConfig(
            "Switch1175",
            ConfigItem.configTypeBool,
            false
        )
    val switch1176 =
        addConfig(
            "Switch1176",
            ConfigItem.configTypeBool,
            false
        )
    val switch1177 =
        addConfig(
            "Switch1177",
            ConfigItem.configTypeBool,
            false
        )
    val switch1178 =
        addConfig(
            "Switch1178",
            ConfigItem.configTypeBool,
            false
        )
    val switch1179 =
        addConfig(
            "Switch1179",
            ConfigItem.configTypeBool,
            false
        )
    val switch1180 =
        addConfig(
            "Switch1180",
            ConfigItem.configTypeBool,
            false
        )
    val switch1181 =
        addConfig(
            "Switch1181",
            ConfigItem.configTypeBool,
            false
        )
    val switch1182 =
        addConfig(
            "Switch1182",
            ConfigItem.configTypeBool,
            false
        )
    val switch1183 =
        addConfig(
            "Switch1183",
            ConfigItem.configTypeBool,
            false
        )
    val switch1184 =
        addConfig(
            "Switch1184",
            ConfigItem.configTypeBool,
            false
        )
    val switch1185 =
        addConfig(
            "Switch1185",
            ConfigItem.configTypeBool,
            false
        )
    val switch1186 =
        addConfig(
            "Switch1186",
            ConfigItem.configTypeBool,
            false
        )
    val switch1187 =
        addConfig(
            "Switch1187",
            ConfigItem.configTypeBool,
            false
        )
    val switch1188 =
        addConfig(
            "Switch1188",
            ConfigItem.configTypeBool,
            false
        )
    val switch1189 =
        addConfig(
            "Switch1189",
            ConfigItem.configTypeBool,
            false
        )
    val switch1190 =
        addConfig(
            "Switch1190",
            ConfigItem.configTypeBool,
            false
        )
    val switch1191 =
        addConfig(
            "Switch1191",
            ConfigItem.configTypeBool,
            false
        )
    val switch1192 =
        addConfig(
            "Switch1192",
            ConfigItem.configTypeBool,
            false
        )
    val switch1193 =
        addConfig(
            "Switch1193",
            ConfigItem.configTypeBool,
            false
        )
    val switch1194 =
        addConfig(
            "Switch1194",
            ConfigItem.configTypeBool,
            false
        )
    val switch1195 =
        addConfig(
            "Switch1195",
            ConfigItem.configTypeBool,
            false
        )
    val switch1196 =
        addConfig(
            "Switch1196",
            ConfigItem.configTypeBool,
            false
        )
    val switch1197 =
        addConfig(
            "Switch1197",
            ConfigItem.configTypeBool,
            false
        )
    val switch1198 =
        addConfig(
            "Switch1198",
            ConfigItem.configTypeBool,
            false
        )
    val switch1199 =
        addConfig(
            "Switch1199",
            ConfigItem.configTypeBool,
            false
        )
    val switch1200 =
        addConfig(
            "Switch1200",
            ConfigItem.configTypeBool,
            false
        )
    val switch1201 =
        addConfig(
            "Switch1201",
            ConfigItem.configTypeBool,
            false
        )
    val switch1202 =
        addConfig(
            "Switch1202",
            ConfigItem.configTypeBool,
            false
        )
    val switch1203 =
        addConfig(
            "Switch1203",
            ConfigItem.configTypeBool,
            false
        )
    val switch1204 =
        addConfig(
            "Switch1204",
            ConfigItem.configTypeBool,
            false
        )
    val switch1205 =
        addConfig(
            "Switch1205",
            ConfigItem.configTypeBool,
            false
        )
    val switch1206 =
        addConfig(
            "Switch1206",
            ConfigItem.configTypeBool,
            false
        )
    val switch1207 =
        addConfig(
            "Switch1207",
            ConfigItem.configTypeBool,
            false
        )
    val switch1208 =
        addConfig(
            "Switch1208",
            ConfigItem.configTypeBool,
            false
        )
    val switch1209 =
        addConfig(
            "Switch1209",
            ConfigItem.configTypeBool,
            false
        )
    val switch1210 =
        addConfig(
            "Switch1210",
            ConfigItem.configTypeBool,
            false
        )
    val switch1211 =
        addConfig(
            "Switch1211",
            ConfigItem.configTypeBool,
            false
        )
    val switch1212 =
        addConfig(
            "Switch1212",
            ConfigItem.configTypeBool,
            false
        )
    val switch1213 =
        addConfig(
            "Switch1213",
            ConfigItem.configTypeBool,
            false
        )
    val switch1214 =
        addConfig(
            "Switch1214",
            ConfigItem.configTypeBool,
            false
        )
    val switch1215 =
        addConfig(
            "Switch1215",
            ConfigItem.configTypeBool,
            false
        )
    val switch1216 =
        addConfig(
            "Switch1216",
            ConfigItem.configTypeBool,
            false
        )
    val switch1217 =
        addConfig(
            "Switch1217",
            ConfigItem.configTypeBool,
            false
        )
    val switch1218 =
        addConfig(
            "Switch1218",
            ConfigItem.configTypeBool,
            false
        )
    val switch1219 =
        addConfig(
            "Switch1219",
            ConfigItem.configTypeBool,
            false
        )
    val switch1220 =
        addConfig(
            "Switch1220",
            ConfigItem.configTypeBool,
            false
        )
    val switch1221 =
        addConfig(
            "Switch1221",
            ConfigItem.configTypeBool,
            false
        )
    val switch1222 =
        addConfig(
            "Switch1222",
            ConfigItem.configTypeBool,
            false
        )
    val switch1223 =
        addConfig(
            "Switch1223",
            ConfigItem.configTypeBool,
            false
        )
    val switch1224 =
        addConfig(
            "Switch1224",
            ConfigItem.configTypeBool,
            false
        )
    val switch1225 =
        addConfig(
            "Switch1225",
            ConfigItem.configTypeBool,
            false
        )
    val switch1226 =
        addConfig(
            "Switch1226",
            ConfigItem.configTypeBool,
            false
        )
    val switch1227 =
        addConfig(
            "Switch1227",
            ConfigItem.configTypeBool,
            false
        )
    val switch1228 =
        addConfig(
            "Switch1228",
            ConfigItem.configTypeBool,
            false
        )
    val switch1229 =
        addConfig(
            "Switch1229",
            ConfigItem.configTypeBool,
            false
        )
    val switch1230 =
        addConfig(
            "Switch1230",
            ConfigItem.configTypeBool,
            false
        )
    val switch1231 =
        addConfig(
            "Switch1231",
            ConfigItem.configTypeBool,
            false
        )
    val switch1232 =
        addConfig(
            "Switch1232",
            ConfigItem.configTypeBool,
            false
        )
    val switch1233 =
        addConfig(
            "Switch1233",
            ConfigItem.configTypeBool,
            false
        )
    val switch1234 =
        addConfig(
            "Switch1234",
            ConfigItem.configTypeBool,
            false
        )
    val switch1235 =
        addConfig(
            "Switch1235",
            ConfigItem.configTypeBool,
            false
        )
    val switch1236 =
        addConfig(
            "Switch1236",
            ConfigItem.configTypeBool,
            false
        )
    val switch1237 =
        addConfig(
            "Switch1237",
            ConfigItem.configTypeBool,
            false
        )
    val switch1238 =
        addConfig(
            "Switch1238",
            ConfigItem.configTypeBool,
            false
        )
    val switch1239 =
        addConfig(
            "Switch1239",
            ConfigItem.configTypeBool,
            false
        )
    val switch1240 =
        addConfig(
            "Switch1240",
            ConfigItem.configTypeBool,
            false
        )
    val switch1241 =
        addConfig(
            "Switch1241",
            ConfigItem.configTypeBool,
            false
        )
    val switch1242 =
        addConfig(
            "Switch1242",
            ConfigItem.configTypeBool,
            false
        )
    val switch1243 =
        addConfig(
            "Switch1243",
            ConfigItem.configTypeBool,
            false
        )
    val switch1244 =
        addConfig(
            "Switch1244",
            ConfigItem.configTypeBool,
            false
        )
    val switch1245 =
        addConfig(
            "Switch1245",
            ConfigItem.configTypeBool,
            false
        )
    val switch1246 =
        addConfig(
            "Switch1246",
            ConfigItem.configTypeBool,
            false
        )
    val switch1247 =
        addConfig(
            "Switch1247",
            ConfigItem.configTypeBool,
            false
        )
    val switch1248 =
        addConfig(
            "Switch1248",
            ConfigItem.configTypeBool,
            false
        )
    val switch1249 =
        addConfig(
            "Switch1249",
            ConfigItem.configTypeBool,
            false
        )
    val switch1250 =
        addConfig(
            "Switch1250",
            ConfigItem.configTypeBool,
            false
        )
    val switch1251 =
        addConfig(
            "Switch1251",
            ConfigItem.configTypeBool,
            false
        )
    val switch1252 =
        addConfig(
            "Switch1252",
            ConfigItem.configTypeBool,
            false
        )
    val switch1253 =
        addConfig(
            "Switch1253",
            ConfigItem.configTypeBool,
            false
        )
    val switch1254 =
        addConfig(
            "Switch1254",
            ConfigItem.configTypeBool,
            false
        )
    val switch1255 =
        addConfig(
            "Switch1255",
            ConfigItem.configTypeBool,
            false
        )
    val switch1256 =
        addConfig(
            "Switch1256",
            ConfigItem.configTypeBool,
            false
        )
    val switch1257 =
        addConfig(
            "Switch1257",
            ConfigItem.configTypeBool,
            false
        )
    val switch1258 =
        addConfig(
            "Switch1258",
            ConfigItem.configTypeBool,
            false
        )
    val switch1259 =
        addConfig(
            "Switch1259",
            ConfigItem.configTypeBool,
            false
        )
    val switch1260 =
        addConfig(
            "Switch1260",
            ConfigItem.configTypeBool,
            false
        )
    val switch1261 =
        addConfig(
            "Switch1261",
            ConfigItem.configTypeBool,
            false
        )
    val switch1262 =
        addConfig(
            "Switch1262",
            ConfigItem.configTypeBool,
            false
        )
    val switch1263 =
        addConfig(
            "Switch1263",
            ConfigItem.configTypeBool,
            false
        )
    val switch1264 =
        addConfig(
            "Switch1264",
            ConfigItem.configTypeBool,
            false
        )
    val switch1265 =
        addConfig(
            "Switch1265",
            ConfigItem.configTypeBool,
            false
        )
    val switch1266 =
        addConfig(
            "Switch1266",
            ConfigItem.configTypeBool,
            false
        )
    val switch1267 =
        addConfig(
            "Switch1267",
            ConfigItem.configTypeBool,
            false
        )
    val switch1268 =
        addConfig(
            "Switch1268",
            ConfigItem.configTypeBool,
            false
        )
    val switch1269 =
        addConfig(
            "Switch1269",
            ConfigItem.configTypeBool,
            false
        )
    val switch1270 =
        addConfig(
            "Switch1270",
            ConfigItem.configTypeBool,
            false
        )
    val switch1271 =
        addConfig(
            "Switch1271",
            ConfigItem.configTypeBool,
            false
        )
    val switch1272 =
        addConfig(
            "Switch1272",
            ConfigItem.configTypeBool,
            false
        )
    val switch1273 =
        addConfig(
            "Switch1273",
            ConfigItem.configTypeBool,
            false
        )
    val switch1274 =
        addConfig(
            "Switch1274",
            ConfigItem.configTypeBool,
            false
        )
    val switch1275 =
        addConfig(
            "Switch1275",
            ConfigItem.configTypeBool,
            false
        )
    val switch1276 =
        addConfig(
            "Switch1276",
            ConfigItem.configTypeBool,
            false
        )
    val switch1277 =
        addConfig(
            "Switch1277",
            ConfigItem.configTypeBool,
            false
        )
    val switch1278 =
        addConfig(
            "Switch1278",
            ConfigItem.configTypeBool,
            false
        )
    val switch1279 =
        addConfig(
            "Switch1279",
            ConfigItem.configTypeBool,
            false
        )
    val switch1280 =
        addConfig(
            "Switch1280",
            ConfigItem.configTypeBool,
            false
        )
    val switch1281 =
        addConfig(
            "Switch1281",
            ConfigItem.configTypeBool,
            false
        )
    val switch1282 =
        addConfig(
            "Switch1282",
            ConfigItem.configTypeBool,
            false
        )
    val switch1283 =
        addConfig(
            "Switch1283",
            ConfigItem.configTypeBool,
            false
        )
    val switch1284 =
        addConfig(
            "Switch1284",
            ConfigItem.configTypeBool,
            false
        )
    val switch1285 =
        addConfig(
            "Switch1285",
            ConfigItem.configTypeBool,
            false
        )
    val switch1286 =
        addConfig(
            "Switch1286",
            ConfigItem.configTypeBool,
            false
        )
    val switch1287 =
        addConfig(
            "Switch1287",
            ConfigItem.configTypeBool,
            false
        )
    val switch1288 =
        addConfig(
            "Switch1288",
            ConfigItem.configTypeBool,
            false
        )
    val switch1289 =
        addConfig(
            "Switch1289",
            ConfigItem.configTypeBool,
            false
        )
    val switch1290 =
        addConfig(
            "Switch1290",
            ConfigItem.configTypeBool,
            false
        )
    val switch1291 =
        addConfig(
            "Switch1291",
            ConfigItem.configTypeBool,
            false
        )
    val switch1292 =
        addConfig(
            "Switch1292",
            ConfigItem.configTypeBool,
            false
        )
    val switch1293 =
        addConfig(
            "Switch1293",
            ConfigItem.configTypeBool,
            false
        )
    val switch1294 =
        addConfig(
            "Switch1294",
            ConfigItem.configTypeBool,
            false
        )
    val switch1295 =
        addConfig(
            "Switch1295",
            ConfigItem.configTypeBool,
            false
        )
    val switch1296 =
        addConfig(
            "Switch1296",
            ConfigItem.configTypeBool,
            false
        )
    val switch1297 =
        addConfig(
            "Switch1297",
            ConfigItem.configTypeBool,
            false
        )
    val switch1298 =
        addConfig(
            "Switch1298",
            ConfigItem.configTypeBool,
            false
        )
    val switch1299 =
        addConfig(
            "Switch1299",
            ConfigItem.configTypeBool,
            false
        )
    val switch1300 =
        addConfig(
            "Switch1300",
            ConfigItem.configTypeBool,
            false
        )
    val switch1301 =
        addConfig(
            "Switch1301",
            ConfigItem.configTypeBool,
            false
        )
    val switch1302 =
        addConfig(
            "Switch1302",
            ConfigItem.configTypeBool,
            false
        )
    val switch1303 =
        addConfig(
            "Switch1303",
            ConfigItem.configTypeBool,
            false
        )
    val switch1304 =
        addConfig(
            "Switch1304",
            ConfigItem.configTypeBool,
            false
        )
    val switch1305 =
        addConfig(
            "Switch1305",
            ConfigItem.configTypeBool,
            false
        )
    val switch1306 =
        addConfig(
            "Switch1306",
            ConfigItem.configTypeBool,
            false
        )
    val switch1307 =
        addConfig(
            "Switch1307",
            ConfigItem.configTypeBool,
            false
        )
    val switch1308 =
        addConfig(
            "Switch1308",
            ConfigItem.configTypeBool,
            false
        )
    val switch1309 =
        addConfig(
            "Switch1309",
            ConfigItem.configTypeBool,
            false
        )
    val switch1310 =
        addConfig(
            "Switch1310",
            ConfigItem.configTypeBool,
            false
        )
    val switch1311 =
        addConfig(
            "Switch1311",
            ConfigItem.configTypeBool,
            false
        )
    val switch1312 =
        addConfig(
            "Switch1312",
            ConfigItem.configTypeBool,
            false
        )
    val switch1313 =
        addConfig(
            "Switch1313",
            ConfigItem.configTypeBool,
            false
        )
    val switch1314 =
        addConfig(
            "Switch1314",
            ConfigItem.configTypeBool,
            false
        )
    val switch1315 =
        addConfig(
            "Switch1315",
            ConfigItem.configTypeBool,
            false
        )
    val switch1316 =
        addConfig(
            "Switch1316",
            ConfigItem.configTypeBool,
            false
        )
    val switch1317 =
        addConfig(
            "Switch1317",
            ConfigItem.configTypeBool,
            false
        )
    val switch1318 =
        addConfig(
            "Switch1318",
            ConfigItem.configTypeBool,
            false
        )
    val switch1319 =
        addConfig(
            "Switch1319",
            ConfigItem.configTypeBool,
            false
        )
    val switch1320 =
        addConfig(
            "Switch1320",
            ConfigItem.configTypeBool,
            false
        )
    val switch1321 =
        addConfig(
            "Switch1321",
            ConfigItem.configTypeBool,
            false
        )
    val switch1322 =
        addConfig(
            "Switch1322",
            ConfigItem.configTypeBool,
            false
        )
    val switch1323 =
        addConfig(
            "Switch1323",
            ConfigItem.configTypeBool,
            false
        )
    val switch1324 =
        addConfig(
            "Switch1324",
            ConfigItem.configTypeBool,
            false
        )
    val switch1325 =
        addConfig(
            "Switch1325",
            ConfigItem.configTypeBool,
            false
        )
    val switch1326 =
        addConfig(
            "Switch1326",
            ConfigItem.configTypeBool,
            false
        )
    val switch1327 =
        addConfig(
            "Switch1327",
            ConfigItem.configTypeBool,
            false
        )
    val switch1328 =
        addConfig(
            "Switch1328",
            ConfigItem.configTypeBool,
            false
        )
    val switch1329 =
        addConfig(
            "Switch1329",
            ConfigItem.configTypeBool,
            false
        )
    val switch1330 =
        addConfig(
            "Switch1330",
            ConfigItem.configTypeBool,
            false
        )
    val switch1331 =
        addConfig(
            "Switch1331",
            ConfigItem.configTypeBool,
            false
        )
    val switch1332 =
        addConfig(
            "Switch1332",
            ConfigItem.configTypeBool,
            false
        )
    val switch1333 =
        addConfig(
            "Switch1333",
            ConfigItem.configTypeBool,
            false
        )
    val switch1334 =
        addConfig(
            "Switch1334",
            ConfigItem.configTypeBool,
            false
        )
    val switch1335 =
        addConfig(
            "Switch1335",
            ConfigItem.configTypeBool,
            false
        )
    val switch1336 =
        addConfig(
            "Switch1336",
            ConfigItem.configTypeBool,
            false
        )
    val switch1337 =
        addConfig(
            "Switch1337",
            ConfigItem.configTypeBool,
            false
        )
    val switch1338 =
        addConfig(
            "Switch1338",
            ConfigItem.configTypeBool,
            false
        )
    val switch1339 =
        addConfig(
            "Switch1339",
            ConfigItem.configTypeBool,
            false
        )
    val switch1340 =
        addConfig(
            "Switch1340",
            ConfigItem.configTypeBool,
            false
        )
    val switch1341 =
        addConfig(
            "Switch1341",
            ConfigItem.configTypeBool,
            false
        )
    val switch1342 =
        addConfig(
            "Switch1342",
            ConfigItem.configTypeBool,
            false
        )
    val switch1343 =
        addConfig(
            "Switch1343",
            ConfigItem.configTypeBool,
            false
        )
    val switch1344 =
        addConfig(
            "Switch1344",
            ConfigItem.configTypeBool,
            false
        )
    val switch1345 =
        addConfig(
            "Switch1345",
            ConfigItem.configTypeBool,
            false
        )
    val switch1346 =
        addConfig(
            "Switch1346",
            ConfigItem.configTypeBool,
            false
        )
    val switch1347 =
        addConfig(
            "Switch1347",
            ConfigItem.configTypeBool,
            false
        )
    val switch1348 =
        addConfig(
            "Switch1348",
            ConfigItem.configTypeBool,
            false
        )
    val switch1349 =
        addConfig(
            "Switch1349",
            ConfigItem.configTypeBool,
            false
        )
    val switch1350 =
        addConfig(
            "Switch1350",
            ConfigItem.configTypeBool,
            false
        )
    val switch1351 =
        addConfig(
            "Switch1351",
            ConfigItem.configTypeBool,
            false
        )
    val switch1352 =
        addConfig(
            "Switch1352",
            ConfigItem.configTypeBool,
            false
        )
    val switch1353 =
        addConfig(
            "Switch1353",
            ConfigItem.configTypeBool,
            false
        )
    val switch1354 =
        addConfig(
            "Switch1354",
            ConfigItem.configTypeBool,
            false
        )
    val switch1355 =
        addConfig(
            "Switch1355",
            ConfigItem.configTypeBool,
            false
        )
    val switch1356 =
        addConfig(
            "Switch1356",
            ConfigItem.configTypeBool,
            false
        )
    val switch1357 =
        addConfig(
            "Switch1357",
            ConfigItem.configTypeBool,
            false
        )
    val switch1358 =
        addConfig(
            "Switch1358",
            ConfigItem.configTypeBool,
            false
        )
    val switch1359 =
        addConfig(
            "Switch1359",
            ConfigItem.configTypeBool,
            false
        )
    val switch1360 =
        addConfig(
            "Switch1360",
            ConfigItem.configTypeBool,
            false
        )
    val switch1361 =
        addConfig(
            "Switch1361",
            ConfigItem.configTypeBool,
            false
        )
    val switch1362 =
        addConfig(
            "Switch1362",
            ConfigItem.configTypeBool,
            false
        )
    val switch1363 =
        addConfig(
            "Switch1363",
            ConfigItem.configTypeBool,
            false
        )
    val switch1364 =
        addConfig(
            "Switch1364",
            ConfigItem.configTypeBool,
            false
        )
    val switch1365 =
        addConfig(
            "Switch1365",
            ConfigItem.configTypeBool,
            false
        )
    val switch1366 =
        addConfig(
            "Switch1366",
            ConfigItem.configTypeBool,
            false
        )
    val switch1367 =
        addConfig(
            "Switch1367",
            ConfigItem.configTypeBool,
            false
        )
    val switch1368 =
        addConfig(
            "Switch1368",
            ConfigItem.configTypeBool,
            false
        )
    val switch1369 =
        addConfig(
            "Switch1369",
            ConfigItem.configTypeBool,
            false
        )
    val switch1370 =
        addConfig(
            "Switch1370",
            ConfigItem.configTypeBool,
            false
        )
    val switch1371 =
        addConfig(
            "Switch1371",
            ConfigItem.configTypeBool,
            false
        )
    val switch1372 =
        addConfig(
            "Switch1372",
            ConfigItem.configTypeBool,
            false
        )
    val switch1373 =
        addConfig(
            "Switch1373",
            ConfigItem.configTypeBool,
            false
        )
    val switch1374 =
        addConfig(
            "Switch1374",
            ConfigItem.configTypeBool,
            false
        )
    val switch1375 =
        addConfig(
            "Switch1375",
            ConfigItem.configTypeBool,
            false
        )
    val switch1376 =
        addConfig(
            "Switch1376",
            ConfigItem.configTypeBool,
            false
        )
    val switch1377 =
        addConfig(
            "Switch1377",
            ConfigItem.configTypeBool,
            false
        )
    val switch1378 =
        addConfig(
            "Switch1378",
            ConfigItem.configTypeBool,
            false
        )
    val switch1379 =
        addConfig(
            "Switch1379",
            ConfigItem.configTypeBool,
            false
        )
    val switch1380 =
        addConfig(
            "Switch1380",
            ConfigItem.configTypeBool,
            false
        )
    val switch1381 =
        addConfig(
            "Switch1381",
            ConfigItem.configTypeBool,
            false
        )
    val switch1382 =
        addConfig(
            "Switch1382",
            ConfigItem.configTypeBool,
            false
        )
    val switch1383 =
        addConfig(
            "Switch1383",
            ConfigItem.configTypeBool,
            false
        )
    val switch1384 =
        addConfig(
            "Switch1384",
            ConfigItem.configTypeBool,
            false
        )
    val switch1385 =
        addConfig(
            "Switch1385",
            ConfigItem.configTypeBool,
            false
        )
    val switch1386 =
        addConfig(
            "Switch1386",
            ConfigItem.configTypeBool,
            false
        )
    val switch1387 =
        addConfig(
            "Switch1387",
            ConfigItem.configTypeBool,
            false
        )
    val switch1388 =
        addConfig(
            "Switch1388",
            ConfigItem.configTypeBool,
            false
        )
    val switch1389 =
        addConfig(
            "Switch1389",
            ConfigItem.configTypeBool,
            false
        )
    val switch1390 =
        addConfig(
            "Switch1390",
            ConfigItem.configTypeBool,
            false
        )
    val switch1391 =
        addConfig(
            "Switch1391",
            ConfigItem.configTypeBool,
            false
        )
    val switch1392 =
        addConfig(
            "Switch1392",
            ConfigItem.configTypeBool,
            false
        )
    val switch1393 =
        addConfig(
            "Switch1393",
            ConfigItem.configTypeBool,
            false
        )
    val switch1394 =
        addConfig(
            "Switch1394",
            ConfigItem.configTypeBool,
            false
        )
    val switch1395 =
        addConfig(
            "Switch1395",
            ConfigItem.configTypeBool,
            false
        )
    val switch1396 =
        addConfig(
            "Switch1396",
            ConfigItem.configTypeBool,
            false
        )
    val switch1397 =
        addConfig(
            "Switch1397",
            ConfigItem.configTypeBool,
            false
        )
    val switch1398 =
        addConfig(
            "Switch1398",
            ConfigItem.configTypeBool,
            false
        )
    val switch1399 =
        addConfig(
            "Switch1399",
            ConfigItem.configTypeBool,
            false
        )
    val switch1400 =
        addConfig(
            "Switch1400",
            ConfigItem.configTypeBool,
            false
        )
    val switch1401 =
        addConfig(
            "Switch1401",
            ConfigItem.configTypeBool,
            false
        )
    val switch1402 =
        addConfig(
            "Switch1402",
            ConfigItem.configTypeBool,
            false
        )
    val switch1403 =
        addConfig(
            "Switch1403",
            ConfigItem.configTypeBool,
            false
        )
    val switch1404 =
        addConfig(
            "Switch1404",
            ConfigItem.configTypeBool,
            false
        )
    val switch1405 =
        addConfig(
            "Switch1405",
            ConfigItem.configTypeBool,
            false
        )
    val switch1406 =
        addConfig(
            "Switch1406",
            ConfigItem.configTypeBool,
            false
        )
    val switch1407 =
        addConfig(
            "Switch1407",
            ConfigItem.configTypeBool,
            false
        )
    val switch1408 =
        addConfig(
            "Switch1408",
            ConfigItem.configTypeBool,
            false
        )
    val switch1409 =
        addConfig(
            "Switch1409",
            ConfigItem.configTypeBool,
            false
        )
    val switch1410 =
        addConfig(
            "Switch1410",
            ConfigItem.configTypeBool,
            false
        )
    val switch1411 =
        addConfig(
            "Switch1411",
            ConfigItem.configTypeBool,
            false
        )
    val switch1412 =
        addConfig(
            "Switch1412",
            ConfigItem.configTypeBool,
            false
        )
    val switch1413 =
        addConfig(
            "Switch1413",
            ConfigItem.configTypeBool,
            false
        )
    val switch1414 =
        addConfig(
            "Switch1414",
            ConfigItem.configTypeBool,
            false
        )
    val switch1415 =
        addConfig(
            "Switch1415",
            ConfigItem.configTypeBool,
            false
        )
    val switch1416 =
        addConfig(
            "Switch1416",
            ConfigItem.configTypeBool,
            false
        )
    val switch1417 =
        addConfig(
            "Switch1417",
            ConfigItem.configTypeBool,
            false
        )
    val switch1418 =
        addConfig(
            "Switch1418",
            ConfigItem.configTypeBool,
            false
        )
    val switch1419 =
        addConfig(
            "Switch1419",
            ConfigItem.configTypeBool,
            false
        )
    val switch1420 =
        addConfig(
            "Switch1420",
            ConfigItem.configTypeBool,
            false
        )
    val switch1421 =
        addConfig(
            "Switch1421",
            ConfigItem.configTypeBool,
            false
        )
    val switch1422 =
        addConfig(
            "Switch1422",
            ConfigItem.configTypeBool,
            false
        )
    val switch1423 =
        addConfig(
            "Switch1423",
            ConfigItem.configTypeBool,
            false
        )
    val switch1424 =
        addConfig(
            "Switch1424",
            ConfigItem.configTypeBool,
            false
        )
    val switch1425 =
        addConfig(
            "Switch1425",
            ConfigItem.configTypeBool,
            false
        )
    val switch1426 =
        addConfig(
            "Switch1426",
            ConfigItem.configTypeBool,
            false
        )
    val switch1427 =
        addConfig(
            "Switch1427",
            ConfigItem.configTypeBool,
            false
        )
    val switch1428 =
        addConfig(
            "Switch1428",
            ConfigItem.configTypeBool,
            false
        )
    val switch1429 =
        addConfig(
            "Switch1429",
            ConfigItem.configTypeBool,
            false
        )
    val switch1430 =
        addConfig(
            "Switch1430",
            ConfigItem.configTypeBool,
            false
        )
    val switch1431 =
        addConfig(
            "Switch1431",
            ConfigItem.configTypeBool,
            false
        )
    val switch1432 =
        addConfig(
            "Switch1432",
            ConfigItem.configTypeBool,
            false
        )
    val switch1433 =
        addConfig(
            "Switch1433",
            ConfigItem.configTypeBool,
            false
        )
    val switch1434 =
        addConfig(
            "Switch1434",
            ConfigItem.configTypeBool,
            false
        )
    val switch1435 =
        addConfig(
            "Switch1435",
            ConfigItem.configTypeBool,
            false
        )
    val switch1436 =
        addConfig(
            "Switch1436",
            ConfigItem.configTypeBool,
            false
        )
    val switch1437 =
        addConfig(
            "Switch1437",
            ConfigItem.configTypeBool,
            false
        )
    val switch1438 =
        addConfig(
            "Switch1438",
            ConfigItem.configTypeBool,
            false
        )
    val switch1439 =
        addConfig(
            "Switch1439",
            ConfigItem.configTypeBool,
            false
        )
    val switch1440 =
        addConfig(
            "Switch1440",
            ConfigItem.configTypeBool,
            false
        )
    val switch1441 =
        addConfig(
            "Switch1441",
            ConfigItem.configTypeBool,
            false
        )
    val switch1442 =
        addConfig(
            "Switch1442",
            ConfigItem.configTypeBool,
            false
        )
    val switch1443 =
        addConfig(
            "Switch1443",
            ConfigItem.configTypeBool,
            false
        )
    val switch1444 =
        addConfig(
            "Switch1444",
            ConfigItem.configTypeBool,
            false
        )
    val switch1445 =
        addConfig(
            "Switch1445",
            ConfigItem.configTypeBool,
            false
        )
    val switch1446 =
        addConfig(
            "Switch1446",
            ConfigItem.configTypeBool,
            false
        )
    val switch1447 =
        addConfig(
            "Switch1447",
            ConfigItem.configTypeBool,
            false
        )
    val switch1448 =
        addConfig(
            "Switch1448",
            ConfigItem.configTypeBool,
            false
        )
    val switch1449 =
        addConfig(
            "Switch1449",
            ConfigItem.configTypeBool,
            false
        )
    val switch1450 =
        addConfig(
            "Switch1450",
            ConfigItem.configTypeBool,
            false
        )
    val switch1451 =
        addConfig(
            "Switch1451",
            ConfigItem.configTypeBool,
            false
        )
    val switch1452 =
        addConfig(
            "Switch1452",
            ConfigItem.configTypeBool,
            false
        )
    val switch1453 =
        addConfig(
            "Switch1453",
            ConfigItem.configTypeBool,
            false
        )
    val switch1454 =
        addConfig(
            "Switch1454",
            ConfigItem.configTypeBool,
            false
        )
    val switch1455 =
        addConfig(
            "Switch1455",
            ConfigItem.configTypeBool,
            false
        )
    val switch1456 =
        addConfig(
            "Switch1456",
            ConfigItem.configTypeBool,
            false
        )
    val switch1457 =
        addConfig(
            "Switch1457",
            ConfigItem.configTypeBool,
            false
        )
    val switch1458 =
        addConfig(
            "Switch1458",
            ConfigItem.configTypeBool,
            false
        )
    val switch1459 =
        addConfig(
            "Switch1459",
            ConfigItem.configTypeBool,
            false
        )
    val switch1460 =
        addConfig(
            "Switch1460",
            ConfigItem.configTypeBool,
            false
        )
    val switch1461 =
        addConfig(
            "Switch1461",
            ConfigItem.configTypeBool,
            false
        )
    val switch1462 =
        addConfig(
            "Switch1462",
            ConfigItem.configTypeBool,
            false
        )
    val switch1463 =
        addConfig(
            "Switch1463",
            ConfigItem.configTypeBool,
            false
        )
    val switch1464 =
        addConfig(
            "Switch1464",
            ConfigItem.configTypeBool,
            false
        )
    val switch1465 =
        addConfig(
            "Switch1465",
            ConfigItem.configTypeBool,
            false
        )
    val switch1466 =
        addConfig(
            "Switch1466",
            ConfigItem.configTypeBool,
            false
        )
    val switch1467 =
        addConfig(
            "Switch1467",
            ConfigItem.configTypeBool,
            false
        )
    val switch1468 =
        addConfig(
            "Switch1468",
            ConfigItem.configTypeBool,
            false
        )
    val switch1469 =
        addConfig(
            "Switch1469",
            ConfigItem.configTypeBool,
            false
        )
    val switch1470 =
        addConfig(
            "Switch1470",
            ConfigItem.configTypeBool,
            false
        )
    val switch1471 =
        addConfig(
            "Switch1471",
            ConfigItem.configTypeBool,
            false
        )
    val switch1472 =
        addConfig(
            "Switch1472",
            ConfigItem.configTypeBool,
            false
        )
    val switch1473 =
        addConfig(
            "Switch1473",
            ConfigItem.configTypeBool,
            false
        )
    val switch1474 =
        addConfig(
            "Switch1474",
            ConfigItem.configTypeBool,
            false
        )
    val switch1475 =
        addConfig(
            "Switch1475",
            ConfigItem.configTypeBool,
            false
        )
    val switch1476 =
        addConfig(
            "Switch1476",
            ConfigItem.configTypeBool,
            false
        )
    val switch1477 =
        addConfig(
            "Switch1477",
            ConfigItem.configTypeBool,
            false
        )
    val switch1478 =
        addConfig(
            "Switch1478",
            ConfigItem.configTypeBool,
            false
        )
    val switch1479 =
        addConfig(
            "Switch1479",
            ConfigItem.configTypeBool,
            false
        )
    val switch1480 =
        addConfig(
            "Switch1480",
            ConfigItem.configTypeBool,
            false
        )
    val switch1481 =
        addConfig(
            "Switch1481",
            ConfigItem.configTypeBool,
            false
        )
    val switch1482 =
        addConfig(
            "Switch1482",
            ConfigItem.configTypeBool,
            false
        )
    val switch1483 =
        addConfig(
            "Switch1483",
            ConfigItem.configTypeBool,
            false
        )
    val switch1484 =
        addConfig(
            "Switch1484",
            ConfigItem.configTypeBool,
            false
        )
    val switch1485 =
        addConfig(
            "Switch1485",
            ConfigItem.configTypeBool,
            false
        )
    val switch1486 =
        addConfig(
            "Switch1486",
            ConfigItem.configTypeBool,
            false
        )
    val switch1487 =
        addConfig(
            "Switch1487",
            ConfigItem.configTypeBool,
            false
        )
    val switch1488 =
        addConfig(
            "Switch1488",
            ConfigItem.configTypeBool,
            false
        )
    val switch1489 =
        addConfig(
            "Switch1489",
            ConfigItem.configTypeBool,
            false
        )
    val switch1490 =
        addConfig(
            "Switch1490",
            ConfigItem.configTypeBool,
            false
        )
    val switch1491 =
        addConfig(
            "Switch1491",
            ConfigItem.configTypeBool,
            false
        )
    val switch1492 =
        addConfig(
            "Switch1492",
            ConfigItem.configTypeBool,
            false
        )
    val switch1493 =
        addConfig(
            "Switch1493",
            ConfigItem.configTypeBool,
            false
        )
    val switch1494 =
        addConfig(
            "Switch1494",
            ConfigItem.configTypeBool,
            false
        )
    val switch1495 =
        addConfig(
            "Switch1495",
            ConfigItem.configTypeBool,
            false
        )
    val switch1496 =
        addConfig(
            "Switch1496",
            ConfigItem.configTypeBool,
            false
        )
    val switch1497 =
        addConfig(
            "Switch1497",
            ConfigItem.configTypeBool,
            false
        )
    val switch1498 =
        addConfig(
            "Switch1498",
            ConfigItem.configTypeBool,
            false
        )
    val switch1499 =
        addConfig(
            "Switch1499",
            ConfigItem.configTypeBool,
            false
        )
    val switch1500 =
        addConfig(
            "Switch1500",
            ConfigItem.configTypeBool,
            false
        )
    val switch1501 =
        addConfig(
            "Switch1501",
            ConfigItem.configTypeBool,
            false
        )
    val switch1502 =
        addConfig(
            "Switch1502",
            ConfigItem.configTypeBool,
            false
        )
    val switch1503 =
        addConfig(
            "Switch1503",
            ConfigItem.configTypeBool,
            false
        )
    val switch1504 =
        addConfig(
            "Switch1504",
            ConfigItem.configTypeBool,
            false
        )
    val switch1505 =
        addConfig(
            "Switch1505",
            ConfigItem.configTypeBool,
            false
        )
    val switch1506 =
        addConfig(
            "Switch1506",
            ConfigItem.configTypeBool,
            false
        )
    val switch1507 =
        addConfig(
            "Switch1507",
            ConfigItem.configTypeBool,
            false
        )
    val switch1508 =
        addConfig(
            "Switch1508",
            ConfigItem.configTypeBool,
            false
        )
    val switch1509 =
        addConfig(
            "Switch1509",
            ConfigItem.configTypeBool,
            false
        )
    val switch1510 =
        addConfig(
            "Switch1510",
            ConfigItem.configTypeBool,
            false
        )
    val switch1511 =
        addConfig(
            "Switch1511",
            ConfigItem.configTypeBool,
            false
        )
    val switch1512 =
        addConfig(
            "Switch1512",
            ConfigItem.configTypeBool,
            false
        )
    val switch1513 =
        addConfig(
            "Switch1513",
            ConfigItem.configTypeBool,
            false
        )
    val switch1514 =
        addConfig(
            "Switch1514",
            ConfigItem.configTypeBool,
            false
        )
    val switch1515 =
        addConfig(
            "Switch1515",
            ConfigItem.configTypeBool,
            false
        )
    val switch1516 =
        addConfig(
            "Switch1516",
            ConfigItem.configTypeBool,
            false
        )
    val switch1517 =
        addConfig(
            "Switch1517",
            ConfigItem.configTypeBool,
            false
        )
    val switch1518 =
        addConfig(
            "Switch1518",
            ConfigItem.configTypeBool,
            false
        )
    val switch1519 =
        addConfig(
            "Switch1519",
            ConfigItem.configTypeBool,
            false
        )
    val switch1520 =
        addConfig(
            "Switch1520",
            ConfigItem.configTypeBool,
            false
        )
    val switch1521 =
        addConfig(
            "Switch1521",
            ConfigItem.configTypeBool,
            false
        )
    val switch1522 =
        addConfig(
            "Switch1522",
            ConfigItem.configTypeBool,
            false
        )
    val switch1523 =
        addConfig(
            "Switch1523",
            ConfigItem.configTypeBool,
            false
        )
    val switch1524 =
        addConfig(
            "Switch1524",
            ConfigItem.configTypeBool,
            false
        )
    val switch1525 =
        addConfig(
            "Switch1525",
            ConfigItem.configTypeBool,
            false
        )
    val switch1526 =
        addConfig(
            "Switch1526",
            ConfigItem.configTypeBool,
            false
        )
    val switch1527 =
        addConfig(
            "Switch1527",
            ConfigItem.configTypeBool,
            false
        )
    val switch1528 =
        addConfig(
            "Switch1528",
            ConfigItem.configTypeBool,
            false
        )
    val switch1529 =
        addConfig(
            "Switch1529",
            ConfigItem.configTypeBool,
            false
        )
    val switch1530 =
        addConfig(
            "Switch1530",
            ConfigItem.configTypeBool,
            false
        )
    val switch1531 =
        addConfig(
            "Switch1531",
            ConfigItem.configTypeBool,
            false
        )
    val switch1532 =
        addConfig(
            "Switch1532",
            ConfigItem.configTypeBool,
            false
        )
    val switch1533 =
        addConfig(
            "Switch1533",
            ConfigItem.configTypeBool,
            false
        )
    val switch1534 =
        addConfig(
            "Switch1534",
            ConfigItem.configTypeBool,
            false
        )
    val switch1535 =
        addConfig(
            "Switch1535",
            ConfigItem.configTypeBool,
            false
        )
    val switch1536 =
        addConfig(
            "Switch1536",
            ConfigItem.configTypeBool,
            false
        )
    val switch1537 =
        addConfig(
            "Switch1537",
            ConfigItem.configTypeBool,
            false
        )
    val switch1538 =
        addConfig(
            "Switch1538",
            ConfigItem.configTypeBool,
            false
        )
    val switch1539 =
        addConfig(
            "Switch1539",
            ConfigItem.configTypeBool,
            false
        )
    val switch1540 =
        addConfig(
            "Switch1540",
            ConfigItem.configTypeBool,
            false
        )
    val switch1541 =
        addConfig(
            "Switch1541",
            ConfigItem.configTypeBool,
            false
        )
    val switch1542 =
        addConfig(
            "Switch1542",
            ConfigItem.configTypeBool,
            false
        )
    val switch1543 =
        addConfig(
            "Switch1543",
            ConfigItem.configTypeBool,
            false
        )
    val switch1544 =
        addConfig(
            "Switch1544",
            ConfigItem.configTypeBool,
            false
        )
    val switch1545 =
        addConfig(
            "Switch1545",
            ConfigItem.configTypeBool,
            false
        )
    val switch1546 =
        addConfig(
            "Switch1546",
            ConfigItem.configTypeBool,
            false
        )
    val switch1547 =
        addConfig(
            "Switch1547",
            ConfigItem.configTypeBool,
            false
        )
    val switch1548 =
        addConfig(
            "Switch1548",
            ConfigItem.configTypeBool,
            false
        )
    val switch1549 =
        addConfig(
            "Switch1549",
            ConfigItem.configTypeBool,
            false
        )
    val switch1550 =
        addConfig(
            "Switch1550",
            ConfigItem.configTypeBool,
            false
        )
    val switch1551 =
        addConfig(
            "Switch1551",
            ConfigItem.configTypeBool,
            false
        )
    val switch1552 =
        addConfig(
            "Switch1552",
            ConfigItem.configTypeBool,
            false
        )
    val switch1553 =
        addConfig(
            "Switch1553",
            ConfigItem.configTypeBool,
            false
        )
    val switch1554 =
        addConfig(
            "Switch1554",
            ConfigItem.configTypeBool,
            false
        )
    val switch1555 =
        addConfig(
            "Switch1555",
            ConfigItem.configTypeBool,
            false
        )
    val switch1556 =
        addConfig(
            "Switch1556",
            ConfigItem.configTypeBool,
            false
        )
    val switch1557 =
        addConfig(
            "Switch1557",
            ConfigItem.configTypeBool,
            false
        )
    val switch1558 =
        addConfig(
            "Switch1558",
            ConfigItem.configTypeBool,
            false
        )
    val switch1559 =
        addConfig(
            "Switch1559",
            ConfigItem.configTypeBool,
            false
        )
    val switch1560 =
        addConfig(
            "Switch1560",
            ConfigItem.configTypeBool,
            false
        )
    val switch1561 =
        addConfig(
            "Switch1561",
            ConfigItem.configTypeBool,
            false
        )
    val switch1562 =
        addConfig(
            "Switch1562",
            ConfigItem.configTypeBool,
            false
        )
    val switch1563 =
        addConfig(
            "Switch1563",
            ConfigItem.configTypeBool,
            false
        )
    val switch1564 =
        addConfig(
            "Switch1564",
            ConfigItem.configTypeBool,
            false
        )
    val switch1565 =
        addConfig(
            "Switch1565",
            ConfigItem.configTypeBool,
            false
        )
    val switch1566 =
        addConfig(
            "Switch1566",
            ConfigItem.configTypeBool,
            false
        )
    val switch1567 =
        addConfig(
            "Switch1567",
            ConfigItem.configTypeBool,
            false
        )
    val switch1568 =
        addConfig(
            "Switch1568",
            ConfigItem.configTypeBool,
            false
        )
    val switch1569 =
        addConfig(
            "Switch1569",
            ConfigItem.configTypeBool,
            false
        )
    val switch1570 =
        addConfig(
            "Switch1570",
            ConfigItem.configTypeBool,
            false
        )
    val switch1571 =
        addConfig(
            "Switch1571",
            ConfigItem.configTypeBool,
            false
        )
    val switch1572 =
        addConfig(
            "Switch1572",
            ConfigItem.configTypeBool,
            false
        )
    val switch1573 =
        addConfig(
            "Switch1573",
            ConfigItem.configTypeBool,
            false
        )
    val switch1574 =
        addConfig(
            "Switch1574",
            ConfigItem.configTypeBool,
            false
        )
    val switch1575 =
        addConfig(
            "Switch1575",
            ConfigItem.configTypeBool,
            false
        )
    val switch1576 =
        addConfig(
            "Switch1576",
            ConfigItem.configTypeBool,
            false
        )
    val switch1577 =
        addConfig(
            "Switch1577",
            ConfigItem.configTypeBool,
            false
        )
    val switch1578 =
        addConfig(
            "Switch1578",
            ConfigItem.configTypeBool,
            false
        )
    val switch1579 =
        addConfig(
            "Switch1579",
            ConfigItem.configTypeBool,
            false
        )
    val switch1580 =
        addConfig(
            "Switch1580",
            ConfigItem.configTypeBool,
            false
        )
    val switch1581 =
        addConfig(
            "Switch1581",
            ConfigItem.configTypeBool,
            false
        )
    val switch1582 =
        addConfig(
            "Switch1582",
            ConfigItem.configTypeBool,
            false
        )
    val switch1583 =
        addConfig(
            "Switch1583",
            ConfigItem.configTypeBool,
            false
        )
    val switch1584 =
        addConfig(
            "Switch1584",
            ConfigItem.configTypeBool,
            false
        )
    val switch1585 =
        addConfig(
            "Switch1585",
            ConfigItem.configTypeBool,
            false
        )
    val switch1586 =
        addConfig(
            "Switch1586",
            ConfigItem.configTypeBool,
            false
        )
    val switch1587 =
        addConfig(
            "Switch1587",
            ConfigItem.configTypeBool,
            false
        )
    val switch1588 =
        addConfig(
            "Switch1588",
            ConfigItem.configTypeBool,
            false
        )
    val switch1589 =
        addConfig(
            "Switch1589",
            ConfigItem.configTypeBool,
            false
        )
    val switch1590 =
        addConfig(
            "Switch1590",
            ConfigItem.configTypeBool,
            false
        )
    val switch1591 =
        addConfig(
            "Switch1591",
            ConfigItem.configTypeBool,
            false
        )
    val switch1592 =
        addConfig(
            "Switch1592",
            ConfigItem.configTypeBool,
            false
        )
    val switch1593 =
        addConfig(
            "Switch1593",
            ConfigItem.configTypeBool,
            false
        )
    val switch1594 =
        addConfig(
            "Switch1594",
            ConfigItem.configTypeBool,
            false
        )
    val switch1595 =
        addConfig(
            "Switch1595",
            ConfigItem.configTypeBool,
            false
        )
    val switch1596 =
        addConfig(
            "Switch1596",
            ConfigItem.configTypeBool,
            false
        )
    val switch1597 =
        addConfig(
            "Switch1597",
            ConfigItem.configTypeBool,
            false
        )
    val switch1598 =
        addConfig(
            "Switch1598",
            ConfigItem.configTypeBool,
            false
        )
    val switch1599 =
        addConfig(
            "Switch1599",
            ConfigItem.configTypeBool,
            false
        )
    val switch1600 =
        addConfig(
            "Switch1600",
            ConfigItem.configTypeBool,
            false
        )
    val switch1601 =
        addConfig(
            "Switch1601",
            ConfigItem.configTypeBool,
            false
        )
    val switch1602 =
        addConfig(
            "Switch1602",
            ConfigItem.configTypeBool,
            false
        )
    val switch1603 =
        addConfig(
            "Switch1603",
            ConfigItem.configTypeBool,
            false
        )
    val switch1604 =
        addConfig(
            "Switch1604",
            ConfigItem.configTypeBool,
            false
        )
    val switch1605 =
        addConfig(
            "Switch1605",
            ConfigItem.configTypeBool,
            false
        )
    val switch1606 =
        addConfig(
            "Switch1606",
            ConfigItem.configTypeBool,
            false
        )
    val switch1607 =
        addConfig(
            "Switch1607",
            ConfigItem.configTypeBool,
            false
        )
    val switch1608 =
        addConfig(
            "Switch1608",
            ConfigItem.configTypeBool,
            false
        )
    val switch1609 =
        addConfig(
            "Switch1609",
            ConfigItem.configTypeBool,
            false
        )
    val switch1610 =
        addConfig(
            "Switch1610",
            ConfigItem.configTypeBool,
            false
        )
    val switch1611 =
        addConfig(
            "Switch1611",
            ConfigItem.configTypeBool,
            false
        )
    val switch1612 =
        addConfig(
            "Switch1612",
            ConfigItem.configTypeBool,
            false
        )
    val switch1613 =
        addConfig(
            "Switch1613",
            ConfigItem.configTypeBool,
            false
        )
    val switch1614 =
        addConfig(
            "Switch1614",
            ConfigItem.configTypeBool,
            false
        )
    val switch1615 =
        addConfig(
            "Switch1615",
            ConfigItem.configTypeBool,
            false
        )
    val switch1616 =
        addConfig(
            "Switch1616",
            ConfigItem.configTypeBool,
            false
        )
    val switch1617 =
        addConfig(
            "Switch1617",
            ConfigItem.configTypeBool,
            false
        )
    val switch1618 =
        addConfig(
            "Switch1618",
            ConfigItem.configTypeBool,
            false
        )
    val switch1619 =
        addConfig(
            "Switch1619",
            ConfigItem.configTypeBool,
            false
        )
    val switch1620 =
        addConfig(
            "Switch1620",
            ConfigItem.configTypeBool,
            false
        )
    val switch1621 =
        addConfig(
            "Switch1621",
            ConfigItem.configTypeBool,
            false
        )
    val switch1622 =
        addConfig(
            "Switch1622",
            ConfigItem.configTypeBool,
            false
        )
    val switch1623 =
        addConfig(
            "Switch1623",
            ConfigItem.configTypeBool,
            false
        )
    val switch1624 =
        addConfig(
            "Switch1624",
            ConfigItem.configTypeBool,
            false
        )
    val switch1625 =
        addConfig(
            "Switch1625",
            ConfigItem.configTypeBool,
            false
        )
    val switch1626 =
        addConfig(
            "Switch1626",
            ConfigItem.configTypeBool,
            false
        )
    val switch1627 =
        addConfig(
            "Switch1627",
            ConfigItem.configTypeBool,
            false
        )
    val switch1628 =
        addConfig(
            "Switch1628",
            ConfigItem.configTypeBool,
            false
        )
    val switch1629 =
        addConfig(
            "Switch1629",
            ConfigItem.configTypeBool,
            false
        )
    val switch1630 =
        addConfig(
            "Switch1630",
            ConfigItem.configTypeBool,
            false
        )
    val switch1631 =
        addConfig(
            "Switch1631",
            ConfigItem.configTypeBool,
            false
        )
    val switch1632 =
        addConfig(
            "Switch1632",
            ConfigItem.configTypeBool,
            false
        )
    val switch1633 =
        addConfig(
            "Switch1633",
            ConfigItem.configTypeBool,
            false
        )
    val switch1634 =
        addConfig(
            "Switch1634",
            ConfigItem.configTypeBool,
            false
        )
    val switch1635 =
        addConfig(
            "Switch1635",
            ConfigItem.configTypeBool,
            false
        )
    val switch1636 =
        addConfig(
            "Switch1636",
            ConfigItem.configTypeBool,
            false
        )
    val switch1637 =
        addConfig(
            "Switch1637",
            ConfigItem.configTypeBool,
            false
        )
    val switch1638 =
        addConfig(
            "Switch1638",
            ConfigItem.configTypeBool,
            false
        )
    val switch1639 =
        addConfig(
            "Switch1639",
            ConfigItem.configTypeBool,
            false
        )
    val switch1640 =
        addConfig(
            "Switch1640",
            ConfigItem.configTypeBool,
            false
        )
    val switch1641 =
        addConfig(
            "Switch1641",
            ConfigItem.configTypeBool,
            false
        )
    val switch1642 =
        addConfig(
            "Switch1642",
            ConfigItem.configTypeBool,
            false
        )
    val switch1643 =
        addConfig(
            "Switch1643",
            ConfigItem.configTypeBool,
            false
        )
    val switch1644 =
        addConfig(
            "Switch1644",
            ConfigItem.configTypeBool,
            false
        )
    val switch1645 =
        addConfig(
            "Switch1645",
            ConfigItem.configTypeBool,
            false
        )
    val switch1646 =
        addConfig(
            "Switch1646",
            ConfigItem.configTypeBool,
            false
        )
    val switch1647 =
        addConfig(
            "Switch1647",
            ConfigItem.configTypeBool,
            false
        )
    val switch1648 =
        addConfig(
            "Switch1648",
            ConfigItem.configTypeBool,
            false
        )
    val switch1649 =
        addConfig(
            "Switch1649",
            ConfigItem.configTypeBool,
            false
        )
    val switch1650 =
        addConfig(
            "Switch1650",
            ConfigItem.configTypeBool,
            false
        )
    val switch1651 =
        addConfig(
            "Switch1651",
            ConfigItem.configTypeBool,
            false
        )
    val switch1652 =
        addConfig(
            "Switch1652",
            ConfigItem.configTypeBool,
            false
        )
    val switch1653 =
        addConfig(
            "Switch1653",
            ConfigItem.configTypeBool,
            false
        )
    val switch1654 =
        addConfig(
            "Switch1654",
            ConfigItem.configTypeBool,
            false
        )
    val switch1655 =
        addConfig(
            "Switch1655",
            ConfigItem.configTypeBool,
            false
        )
    val switch1656 =
        addConfig(
            "Switch1656",
            ConfigItem.configTypeBool,
            false
        )
    val switch1657 =
        addConfig(
            "Switch1657",
            ConfigItem.configTypeBool,
            false
        )
    val switch1658 =
        addConfig(
            "Switch1658",
            ConfigItem.configTypeBool,
            false
        )
    val switch1659 =
        addConfig(
            "Switch1659",
            ConfigItem.configTypeBool,
            false
        )
    val switch1660 =
        addConfig(
            "Switch1660",
            ConfigItem.configTypeBool,
            false
        )
    val switch1661 =
        addConfig(
            "Switch1661",
            ConfigItem.configTypeBool,
            false
        )
    val switch1662 =
        addConfig(
            "Switch1662",
            ConfigItem.configTypeBool,
            false
        )
    val switch1663 =
        addConfig(
            "Switch1663",
            ConfigItem.configTypeBool,
            false
        )
    val switch1664 =
        addConfig(
            "Switch1664",
            ConfigItem.configTypeBool,
            false
        )
    val switch1665 =
        addConfig(
            "Switch1665",
            ConfigItem.configTypeBool,
            false
        )
    val switch1666 =
        addConfig(
            "Switch1666",
            ConfigItem.configTypeBool,
            false
        )
    val switch1667 =
        addConfig(
            "Switch1667",
            ConfigItem.configTypeBool,
            false
        )
    val switch1668 =
        addConfig(
            "Switch1668",
            ConfigItem.configTypeBool,
            false
        )
    val switch1669 =
        addConfig(
            "Switch1669",
            ConfigItem.configTypeBool,
            false
        )
    val switch1670 =
        addConfig(
            "Switch1670",
            ConfigItem.configTypeBool,
            false
        )
    val switch1671 =
        addConfig(
            "Switch1671",
            ConfigItem.configTypeBool,
            false
        )
    val switch1672 =
        addConfig(
            "Switch1672",
            ConfigItem.configTypeBool,
            false
        )
    val switch1673 =
        addConfig(
            "Switch1673",
            ConfigItem.configTypeBool,
            false
        )
    val switch1674 =
        addConfig(
            "Switch1674",
            ConfigItem.configTypeBool,
            false
        )
    val switch1675 =
        addConfig(
            "Switch1675",
            ConfigItem.configTypeBool,
            false
        )
    val switch1676 =
        addConfig(
            "Switch1676",
            ConfigItem.configTypeBool,
            false
        )
    val switch1677 =
        addConfig(
            "Switch1677",
            ConfigItem.configTypeBool,
            false
        )
    val switch1678 =
        addConfig(
            "Switch1678",
            ConfigItem.configTypeBool,
            false
        )
    val switch1679 =
        addConfig(
            "Switch1679",
            ConfigItem.configTypeBool,
            false
        )
    val switch1680 =
        addConfig(
            "Switch1680",
            ConfigItem.configTypeBool,
            false
        )
    val switch1681 =
        addConfig(
            "Switch1681",
            ConfigItem.configTypeBool,
            false
        )
    val switch1682 =
        addConfig(
            "Switch1682",
            ConfigItem.configTypeBool,
            false
        )
    val switch1683 =
        addConfig(
            "Switch1683",
            ConfigItem.configTypeBool,
            false
        )
    val switch1684 =
        addConfig(
            "Switch1684",
            ConfigItem.configTypeBool,
            false
        )
    val switch1685 =
        addConfig(
            "Switch1685",
            ConfigItem.configTypeBool,
            false
        )
    val switch1686 =
        addConfig(
            "Switch1686",
            ConfigItem.configTypeBool,
            false
        )
    val switch1687 =
        addConfig(
            "Switch1687",
            ConfigItem.configTypeBool,
            false
        )
    val switch1688 =
        addConfig(
            "Switch1688",
            ConfigItem.configTypeBool,
            false
        )
    val switch1689 =
        addConfig(
            "Switch1689",
            ConfigItem.configTypeBool,
            false
        )
    val switch1690 =
        addConfig(
            "Switch1690",
            ConfigItem.configTypeBool,
            false
        )
    val switch1691 =
        addConfig(
            "Switch1691",
            ConfigItem.configTypeBool,
            false
        )
    val switch1692 =
        addConfig(
            "Switch1692",
            ConfigItem.configTypeBool,
            false
        )
    val switch1693 =
        addConfig(
            "Switch1693",
            ConfigItem.configTypeBool,
            false
        )
    val switch1694 =
        addConfig(
            "Switch1694",
            ConfigItem.configTypeBool,
            false
        )
    val switch1695 =
        addConfig(
            "Switch1695",
            ConfigItem.configTypeBool,
            false
        )
    val switch1696 =
        addConfig(
            "Switch1696",
            ConfigItem.configTypeBool,
            false
        )
    val switch1697 =
        addConfig(
            "Switch1697",
            ConfigItem.configTypeBool,
            false
        )
    val switch1698 =
        addConfig(
            "Switch1698",
            ConfigItem.configTypeBool,
            false
        )
    val switch1699 =
        addConfig(
            "Switch1699",
            ConfigItem.configTypeBool,
            false
        )
    val switch1700 =
        addConfig(
            "Switch1700",
            ConfigItem.configTypeBool,
            false
        )
    val switch1701 =
        addConfig(
            "Switch1701",
            ConfigItem.configTypeBool,
            false
        )
    val switch1702 =
        addConfig(
            "Switch1702",
            ConfigItem.configTypeBool,
            false
        )
    val switch1703 =
        addConfig(
            "Switch1703",
            ConfigItem.configTypeBool,
            false
        )
    val switch1704 =
        addConfig(
            "Switch1704",
            ConfigItem.configTypeBool,
            false
        )
    val switch1705 =
        addConfig(
            "Switch1705",
            ConfigItem.configTypeBool,
            false
        )
    val switch1706 =
        addConfig(
            "Switch1706",
            ConfigItem.configTypeBool,
            false
        )
    val switch1707 =
        addConfig(
            "Switch1707",
            ConfigItem.configTypeBool,
            false
        )
    val switch1708 =
        addConfig(
            "Switch1708",
            ConfigItem.configTypeBool,
            false
        )
    val switch1709 =
        addConfig(
            "Switch1709",
            ConfigItem.configTypeBool,
            false
        )
    val switch1710 =
        addConfig(
            "Switch1710",
            ConfigItem.configTypeBool,
            false
        )
    val switch1711 =
        addConfig(
            "Switch1711",
            ConfigItem.configTypeBool,
            false
        )
    val switch1712 =
        addConfig(
            "Switch1712",
            ConfigItem.configTypeBool,
            false
        )
    val switch1713 =
        addConfig(
            "Switch1713",
            ConfigItem.configTypeBool,
            false
        )
    val switch1714 =
        addConfig(
            "Switch1714",
            ConfigItem.configTypeBool,
            false
        )
    val switch1715 =
        addConfig(
            "Switch1715",
            ConfigItem.configTypeBool,
            false
        )
    val switch1716 =
        addConfig(
            "Switch1716",
            ConfigItem.configTypeBool,
            false
        )
    val switch1717 =
        addConfig(
            "Switch1717",
            ConfigItem.configTypeBool,
            false
        )
    val switch1718 =
        addConfig(
            "Switch1718",
            ConfigItem.configTypeBool,
            false
        )
    val switch1719 =
        addConfig(
            "Switch1719",
            ConfigItem.configTypeBool,
            false
        )
    val switch1720 =
        addConfig(
            "Switch1720",
            ConfigItem.configTypeBool,
            false
        )
    val switch1721 =
        addConfig(
            "Switch1721",
            ConfigItem.configTypeBool,
            false
        )
    val switch1722 =
        addConfig(
            "Switch1722",
            ConfigItem.configTypeBool,
            false
        )
    val switch1723 =
        addConfig(
            "Switch1723",
            ConfigItem.configTypeBool,
            false
        )
    val switch1724 =
        addConfig(
            "Switch1724",
            ConfigItem.configTypeBool,
            false
        )
    val switch1725 =
        addConfig(
            "Switch1725",
            ConfigItem.configTypeBool,
            false
        )
    val switch1726 =
        addConfig(
            "Switch1726",
            ConfigItem.configTypeBool,
            false
        )
    val switch1727 =
        addConfig(
            "Switch1727",
            ConfigItem.configTypeBool,
            false
        )
    val switch1728 =
        addConfig(
            "Switch1728",
            ConfigItem.configTypeBool,
            false
        )
    val switch1729 =
        addConfig(
            "Switch1729",
            ConfigItem.configTypeBool,
            false
        )
    val switch1730 =
        addConfig(
            "Switch1730",
            ConfigItem.configTypeBool,
            false
        )
    val switch1731 =
        addConfig(
            "Switch1731",
            ConfigItem.configTypeBool,
            false
        )
    val switch1732 =
        addConfig(
            "Switch1732",
            ConfigItem.configTypeBool,
            false
        )
    val switch1733 =
        addConfig(
            "Switch1733",
            ConfigItem.configTypeBool,
            false
        )
    val switch1734 =
        addConfig(
            "Switch1734",
            ConfigItem.configTypeBool,
            false
        )
    val switch1735 =
        addConfig(
            "Switch1735",
            ConfigItem.configTypeBool,
            false
        )
    val switch1736 =
        addConfig(
            "Switch1736",
            ConfigItem.configTypeBool,
            false
        )
    val switch1737 =
        addConfig(
            "Switch1737",
            ConfigItem.configTypeBool,
            false
        )
    val switch1738 =
        addConfig(
            "Switch1738",
            ConfigItem.configTypeBool,
            false
        )
    val switch1739 =
        addConfig(
            "Switch1739",
            ConfigItem.configTypeBool,
            false
        )
    val switch1740 =
        addConfig(
            "Switch1740",
            ConfigItem.configTypeBool,
            false
        )
    val switch1741 =
        addConfig(
            "Switch1741",
            ConfigItem.configTypeBool,
            false
        )
    val switch1742 =
        addConfig(
            "Switch1742",
            ConfigItem.configTypeBool,
            false
        )
    val switch1743 =
        addConfig(
            "Switch1743",
            ConfigItem.configTypeBool,
            false
        )
    val switch1744 =
        addConfig(
            "Switch1744",
            ConfigItem.configTypeBool,
            false
        )
    val switch1745 =
        addConfig(
            "Switch1745",
            ConfigItem.configTypeBool,
            false
        )
    val switch1746 =
        addConfig(
            "Switch1746",
            ConfigItem.configTypeBool,
            false
        )
    val switch1747 =
        addConfig(
            "Switch1747",
            ConfigItem.configTypeBool,
            false
        )
    val switch1748 =
        addConfig(
            "Switch1748",
            ConfigItem.configTypeBool,
            false
        )
    val switch1749 =
        addConfig(
            "Switch1749",
            ConfigItem.configTypeBool,
            false
        )
    val switch1750 =
        addConfig(
            "Switch1750",
            ConfigItem.configTypeBool,
            false
        )
    val switch1751 =
        addConfig(
            "Switch1751",
            ConfigItem.configTypeBool,
            false
        )
    val switch1752 =
        addConfig(
            "Switch1752",
            ConfigItem.configTypeBool,
            false
        )
    val switch1753 =
        addConfig(
            "Switch1753",
            ConfigItem.configTypeBool,
            false
        )
    val switch1754 =
        addConfig(
            "Switch1754",
            ConfigItem.configTypeBool,
            false
        )
    val switch1755 =
        addConfig(
            "Switch1755",
            ConfigItem.configTypeBool,
            false
        )
    val switch1756 =
        addConfig(
            "Switch1756",
            ConfigItem.configTypeBool,
            false
        )
    val switch1757 =
        addConfig(
            "Switch1757",
            ConfigItem.configTypeBool,
            false
        )
    val switch1758 =
        addConfig(
            "Switch1758",
            ConfigItem.configTypeBool,
            false
        )
    val switch1759 =
        addConfig(
            "Switch1759",
            ConfigItem.configTypeBool,
            false
        )
    val switch1760 =
        addConfig(
            "Switch1760",
            ConfigItem.configTypeBool,
            false
        )
    val switch1761 =
        addConfig(
            "Switch1761",
            ConfigItem.configTypeBool,
            false
        )
    val switch1762 =
        addConfig(
            "Switch1762",
            ConfigItem.configTypeBool,
            false
        )
    val switch1763 =
        addConfig(
            "Switch1763",
            ConfigItem.configTypeBool,
            false
        )
    val switch1764 =
        addConfig(
            "Switch1764",
            ConfigItem.configTypeBool,
            false
        )
    val switch1765 =
        addConfig(
            "Switch1765",
            ConfigItem.configTypeBool,
            false
        )
    val switch1766 =
        addConfig(
            "Switch1766",
            ConfigItem.configTypeBool,
            false
        )
    val switch1767 =
        addConfig(
            "Switch1767",
            ConfigItem.configTypeBool,
            false
        )
    val switch1768 =
        addConfig(
            "Switch1768",
            ConfigItem.configTypeBool,
            false
        )
    val switch1769 =
        addConfig(
            "Switch1769",
            ConfigItem.configTypeBool,
            false
        )
    val switch1770 =
        addConfig(
            "Switch1770",
            ConfigItem.configTypeBool,
            false
        )
    val switch1771 =
        addConfig(
            "Switch1771",
            ConfigItem.configTypeBool,
            false
        )
    val switch1772 =
        addConfig(
            "Switch1772",
            ConfigItem.configTypeBool,
            false
        )
    val switch1773 =
        addConfig(
            "Switch1773",
            ConfigItem.configTypeBool,
            false
        )
    val switch1774 =
        addConfig(
            "Switch1774",
            ConfigItem.configTypeBool,
            false
        )
    val switch1775 =
        addConfig(
            "Switch1775",
            ConfigItem.configTypeBool,
            false
        )
    val switch1776 =
        addConfig(
            "Switch1776",
            ConfigItem.configTypeBool,
            false
        )
    val switch1777 =
        addConfig(
            "Switch1777",
            ConfigItem.configTypeBool,
            false
        )
    val switch1778 =
        addConfig(
            "Switch1778",
            ConfigItem.configTypeBool,
            false
        )
    val switch1779 =
        addConfig(
            "Switch1779",
            ConfigItem.configTypeBool,
            false
        )
    val switch1780 =
        addConfig(
            "Switch1780",
            ConfigItem.configTypeBool,
            false
        )
    val switch1781 =
        addConfig(
            "Switch1781",
            ConfigItem.configTypeBool,
            false
        )
    val switch1782 =
        addConfig(
            "Switch1782",
            ConfigItem.configTypeBool,
            false
        )
    val switch1783 =
        addConfig(
            "Switch1783",
            ConfigItem.configTypeBool,
            false
        )
    val switch1784 =
        addConfig(
            "Switch1784",
            ConfigItem.configTypeBool,
            false
        )
    val switch1785 =
        addConfig(
            "Switch1785",
            ConfigItem.configTypeBool,
            false
        )
    val switch1786 =
        addConfig(
            "Switch1786",
            ConfigItem.configTypeBool,
            false
        )
    val switch1787 =
        addConfig(
            "Switch1787",
            ConfigItem.configTypeBool,
            false
        )
    val switch1788 =
        addConfig(
            "Switch1788",
            ConfigItem.configTypeBool,
            false
        )
    val switch1789 =
        addConfig(
            "Switch1789",
            ConfigItem.configTypeBool,
            false
        )
    val switch1790 =
        addConfig(
            "Switch1790",
            ConfigItem.configTypeBool,
            false
        )
    val switch1791 =
        addConfig(
            "Switch1791",
            ConfigItem.configTypeBool,
            false
        )
    val switch1792 =
        addConfig(
            "Switch1792",
            ConfigItem.configTypeBool,
            false
        )
    val switch1793 =
        addConfig(
            "Switch1793",
            ConfigItem.configTypeBool,
            false
        )
    val switch1794 =
        addConfig(
            "Switch1794",
            ConfigItem.configTypeBool,
            false
        )
    val switch1795 =
        addConfig(
            "Switch1795",
            ConfigItem.configTypeBool,
            false
        )
    val switch1796 =
        addConfig(
            "Switch1796",
            ConfigItem.configTypeBool,
            false
        )
    val switch1797 =
        addConfig(
            "Switch1797",
            ConfigItem.configTypeBool,
            false
        )
    val switch1798 =
        addConfig(
            "Switch1798",
            ConfigItem.configTypeBool,
            false
        )
    val switch1799 =
        addConfig(
            "Switch1799",
            ConfigItem.configTypeBool,
            false
        )
    val switch1800 =
        addConfig(
            "Switch1800",
            ConfigItem.configTypeBool,
            false
        )
    val switch1801 =
        addConfig(
            "Switch1801",
            ConfigItem.configTypeBool,
            false
        )
    val switch1802 =
        addConfig(
            "Switch1802",
            ConfigItem.configTypeBool,
            false
        )
    val switch1803 =
        addConfig(
            "Switch1803",
            ConfigItem.configTypeBool,
            false
        )
    val switch1804 =
        addConfig(
            "Switch1804",
            ConfigItem.configTypeBool,
            false
        )
    val switch1805 =
        addConfig(
            "Switch1805",
            ConfigItem.configTypeBool,
            false
        )
    val switch1806 =
        addConfig(
            "Switch1806",
            ConfigItem.configTypeBool,
            false
        )
    val switch1807 =
        addConfig(
            "Switch1807",
            ConfigItem.configTypeBool,
            false
        )
    val switch1808 =
        addConfig(
            "Switch1808",
            ConfigItem.configTypeBool,
            false
        )
    val switch1809 =
        addConfig(
            "Switch1809",
            ConfigItem.configTypeBool,
            false
        )
    val switch1810 =
        addConfig(
            "Switch1810",
            ConfigItem.configTypeBool,
            false
        )
    val switch1811 =
        addConfig(
            "Switch1811",
            ConfigItem.configTypeBool,
            false
        )
    val switch1812 =
        addConfig(
            "Switch1812",
            ConfigItem.configTypeBool,
            false
        )
    val switch1813 =
        addConfig(
            "Switch1813",
            ConfigItem.configTypeBool,
            false
        )
    val switch1814 =
        addConfig(
            "Switch1814",
            ConfigItem.configTypeBool,
            false
        )
    val switch1815 =
        addConfig(
            "Switch1815",
            ConfigItem.configTypeBool,
            false
        )
    val switch1816 =
        addConfig(
            "Switch1816",
            ConfigItem.configTypeBool,
            false
        )
    val switch1817 =
        addConfig(
            "Switch1817",
            ConfigItem.configTypeBool,
            false
        )
    val switch1818 =
        addConfig(
            "Switch1818",
            ConfigItem.configTypeBool,
            false
        )
    val switch1819 =
        addConfig(
            "Switch1819",
            ConfigItem.configTypeBool,
            false
        )
    val switch1820 =
        addConfig(
            "Switch1820",
            ConfigItem.configTypeBool,
            false
        )
    val switch1821 =
        addConfig(
            "Switch1821",
            ConfigItem.configTypeBool,
            false
        )
    val switch1822 =
        addConfig(
            "Switch1822",
            ConfigItem.configTypeBool,
            false
        )
    val switch1823 =
        addConfig(
            "Switch1823",
            ConfigItem.configTypeBool,
            false
        )
    val switch1824 =
        addConfig(
            "Switch1824",
            ConfigItem.configTypeBool,
            false
        )
    val switch1825 =
        addConfig(
            "Switch1825",
            ConfigItem.configTypeBool,
            false
        )
    val switch1826 =
        addConfig(
            "Switch1826",
            ConfigItem.configTypeBool,
            false
        )
    val switch1827 =
        addConfig(
            "Switch1827",
            ConfigItem.configTypeBool,
            false
        )
    val switch1828 =
        addConfig(
            "Switch1828",
            ConfigItem.configTypeBool,
            false
        )
    val switch1829 =
        addConfig(
            "Switch1829",
            ConfigItem.configTypeBool,
            false
        )
    val switch1830 =
        addConfig(
            "Switch1830",
            ConfigItem.configTypeBool,
            false
        )
    val switch1831 =
        addConfig(
            "Switch1831",
            ConfigItem.configTypeBool,
            false
        )
    val switch1832 =
        addConfig(
            "Switch1832",
            ConfigItem.configTypeBool,
            false
        )
    val switch1833 =
        addConfig(
            "Switch1833",
            ConfigItem.configTypeBool,
            false
        )
    val switch1834 =
        addConfig(
            "Switch1834",
            ConfigItem.configTypeBool,
            false
        )
    val switch1835 =
        addConfig(
            "Switch1835",
            ConfigItem.configTypeBool,
            false
        )
    val switch1836 =
        addConfig(
            "Switch1836",
            ConfigItem.configTypeBool,
            false
        )
    val switch1837 =
        addConfig(
            "Switch1837",
            ConfigItem.configTypeBool,
            false
        )
    val switch1838 =
        addConfig(
            "Switch1838",
            ConfigItem.configTypeBool,
            false
        )
    val switch1839 =
        addConfig(
            "Switch1839",
            ConfigItem.configTypeBool,
            false
        )
    val switch1840 =
        addConfig(
            "Switch1840",
            ConfigItem.configTypeBool,
            false
        )
    val switch1841 =
        addConfig(
            "Switch1841",
            ConfigItem.configTypeBool,
            false
        )
    val switch1842 =
        addConfig(
            "Switch1842",
            ConfigItem.configTypeBool,
            false
        )
    val switch1843 =
        addConfig(
            "Switch1843",
            ConfigItem.configTypeBool,
            false
        )
    val switch1844 =
        addConfig(
            "Switch1844",
            ConfigItem.configTypeBool,
            false
        )
    val switch1845 =
        addConfig(
            "Switch1845",
            ConfigItem.configTypeBool,
            false
        )
    val switch1846 =
        addConfig(
            "Switch1846",
            ConfigItem.configTypeBool,
            false
        )
    val switch1847 =
        addConfig(
            "Switch1847",
            ConfigItem.configTypeBool,
            false
        )
    val switch1848 =
        addConfig(
            "Switch1848",
            ConfigItem.configTypeBool,
            false
        )
    val switch1849 =
        addConfig(
            "Switch1849",
            ConfigItem.configTypeBool,
            false
        )
    val switch1850 =
        addConfig(
            "Switch1850",
            ConfigItem.configTypeBool,
            false
        )
    val switch1851 =
        addConfig(
            "Switch1851",
            ConfigItem.configTypeBool,
            false
        )
    val switch1852 =
        addConfig(
            "Switch1852",
            ConfigItem.configTypeBool,
            false
        )
    val switch1853 =
        addConfig(
            "Switch1853",
            ConfigItem.configTypeBool,
            false
        )
    val switch1854 =
        addConfig(
            "Switch1854",
            ConfigItem.configTypeBool,
            false
        )
    val switch1855 =
        addConfig(
            "Switch1855",
            ConfigItem.configTypeBool,
            false
        )
    val switch1856 =
        addConfig(
            "Switch1856",
            ConfigItem.configTypeBool,
            false
        )
    val switch1857 =
        addConfig(
            "Switch1857",
            ConfigItem.configTypeBool,
            false
        )
    val switch1858 =
        addConfig(
            "Switch1858",
            ConfigItem.configTypeBool,
            false
        )
    val switch1859 =
        addConfig(
            "Switch1859",
            ConfigItem.configTypeBool,
            false
        )
    val switch1860 =
        addConfig(
            "Switch1860",
            ConfigItem.configTypeBool,
            false
        )
    val switch1861 =
        addConfig(
            "Switch1861",
            ConfigItem.configTypeBool,
            false
        )
    val switch1862 =
        addConfig(
            "Switch1862",
            ConfigItem.configTypeBool,
            false
        )
    val switch1863 =
        addConfig(
            "Switch1863",
            ConfigItem.configTypeBool,
            false
        )
    val switch1864 =
        addConfig(
            "Switch1864",
            ConfigItem.configTypeBool,
            false
        )
    val switch1865 =
        addConfig(
            "Switch1865",
            ConfigItem.configTypeBool,
            false
        )
    val switch1866 =
        addConfig(
            "Switch1866",
            ConfigItem.configTypeBool,
            false
        )
    val switch1867 =
        addConfig(
            "Switch1867",
            ConfigItem.configTypeBool,
            false
        )
    val switch1868 =
        addConfig(
            "Switch1868",
            ConfigItem.configTypeBool,
            false
        )
    val switch1869 =
        addConfig(
            "Switch1869",
            ConfigItem.configTypeBool,
            false
        )
    val switch1870 =
        addConfig(
            "Switch1870",
            ConfigItem.configTypeBool,
            false
        )
    val switch1871 =
        addConfig(
            "Switch1871",
            ConfigItem.configTypeBool,
            false
        )
    val switch1872 =
        addConfig(
            "Switch1872",
            ConfigItem.configTypeBool,
            false
        )
    val switch1873 =
        addConfig(
            "Switch1873",
            ConfigItem.configTypeBool,
            false
        )
    val switch1874 =
        addConfig(
            "Switch1874",
            ConfigItem.configTypeBool,
            false
        )
    val switch1875 =
        addConfig(
            "Switch1875",
            ConfigItem.configTypeBool,
            false
        )
    val switch1876 =
        addConfig(
            "Switch1876",
            ConfigItem.configTypeBool,
            false
        )
    val switch1877 =
        addConfig(
            "Switch1877",
            ConfigItem.configTypeBool,
            false
        )
    val switch1878 =
        addConfig(
            "Switch1878",
            ConfigItem.configTypeBool,
            false
        )
    val switch1879 =
        addConfig(
            "Switch1879",
            ConfigItem.configTypeBool,
            false
        )
    val switch1880 =
        addConfig(
            "Switch1880",
            ConfigItem.configTypeBool,
            false
        )
    val switch1881 =
        addConfig(
            "Switch1881",
            ConfigItem.configTypeBool,
            false
        )
    val switch1882 =
        addConfig(
            "Switch1882",
            ConfigItem.configTypeBool,
            false
        )
    val switch1883 =
        addConfig(
            "Switch1883",
            ConfigItem.configTypeBool,
            false
        )
    val switch1884 =
        addConfig(
            "Switch1884",
            ConfigItem.configTypeBool,
            false
        )
    val switch1885 =
        addConfig(
            "Switch1885",
            ConfigItem.configTypeBool,
            false
        )
    val switch1886 =
        addConfig(
            "Switch1886",
            ConfigItem.configTypeBool,
            false
        )
    val switch1887 =
        addConfig(
            "Switch1887",
            ConfigItem.configTypeBool,
            false
        )
    val switch1888 =
        addConfig(
            "Switch1888",
            ConfigItem.configTypeBool,
            false
        )
    val switch1889 =
        addConfig(
            "Switch1889",
            ConfigItem.configTypeBool,
            false
        )
    val switch1890 =
        addConfig(
            "Switch1890",
            ConfigItem.configTypeBool,
            false
        )
    val switch1891 =
        addConfig(
            "Switch1891",
            ConfigItem.configTypeBool,
            false
        )
    val switch1892 =
        addConfig(
            "Switch1892",
            ConfigItem.configTypeBool,
            false
        )
    val switch1893 =
        addConfig(
            "Switch1893",
            ConfigItem.configTypeBool,
            false
        )
    val switch1894 =
        addConfig(
            "Switch1894",
            ConfigItem.configTypeBool,
            false
        )
    val switch1895 =
        addConfig(
            "Switch1895",
            ConfigItem.configTypeBool,
            false
        )
    val switch1896 =
        addConfig(
            "Switch1896",
            ConfigItem.configTypeBool,
            false
        )
    val switch1897 =
        addConfig(
            "Switch1897",
            ConfigItem.configTypeBool,
            false
        )
    val switch1898 =
        addConfig(
            "Switch1898",
            ConfigItem.configTypeBool,
            false
        )
    val switch1899 =
        addConfig(
            "Switch1899",
            ConfigItem.configTypeBool,
            false
        )
    val switch1900 =
        addConfig(
            "Switch1900",
            ConfigItem.configTypeBool,
            false
        )
    val switch1901 =
        addConfig(
            "Switch1901",
            ConfigItem.configTypeBool,
            false
        )
    val switch1902 =
        addConfig(
            "Switch1902",
            ConfigItem.configTypeBool,
            false
        )
    val switch1903 =
        addConfig(
            "Switch1903",
            ConfigItem.configTypeBool,
            false
        )
    val switch1904 =
        addConfig(
            "Switch1904",
            ConfigItem.configTypeBool,
            false
        )
    val switch1905 =
        addConfig(
            "Switch1905",
            ConfigItem.configTypeBool,
            false
        )
    val switch1906 =
        addConfig(
            "Switch1906",
            ConfigItem.configTypeBool,
            false
        )
    val switch1907 =
        addConfig(
            "Switch1907",
            ConfigItem.configTypeBool,
            false
        )
    val switch1908 =
        addConfig(
            "Switch1908",
            ConfigItem.configTypeBool,
            false
        )
    val switch1909 =
        addConfig(
            "Switch1909",
            ConfigItem.configTypeBool,
            false
        )
    val switch1910 =
        addConfig(
            "Switch1910",
            ConfigItem.configTypeBool,
            false
        )
    val switch1911 =
        addConfig(
            "Switch1911",
            ConfigItem.configTypeBool,
            false
        )
    val switch1912 =
        addConfig(
            "Switch1912",
            ConfigItem.configTypeBool,
            false
        )
    val switch1913 =
        addConfig(
            "Switch1913",
            ConfigItem.configTypeBool,
            false
        )
    val switch1914 =
        addConfig(
            "Switch1914",
            ConfigItem.configTypeBool,
            false
        )
    val switch1915 =
        addConfig(
            "Switch1915",
            ConfigItem.configTypeBool,
            false
        )
    val switch1916 =
        addConfig(
            "Switch1916",
            ConfigItem.configTypeBool,
            false
        )
    val switch1917 =
        addConfig(
            "Switch1917",
            ConfigItem.configTypeBool,
            false
        )
    val switch1918 =
        addConfig(
            "Switch1918",
            ConfigItem.configTypeBool,
            false
        )
    val switch1919 =
        addConfig(
            "Switch1919",
            ConfigItem.configTypeBool,
            false
        )
    val switch1920 =
        addConfig(
            "Switch1920",
            ConfigItem.configTypeBool,
            false
        )
    val switch1921 =
        addConfig(
            "Switch1921",
            ConfigItem.configTypeBool,
            false
        )
    val switch1922 =
        addConfig(
            "Switch1922",
            ConfigItem.configTypeBool,
            false
        )
    val switch1923 =
        addConfig(
            "Switch1923",
            ConfigItem.configTypeBool,
            false
        )
    val switch1924 =
        addConfig(
            "Switch1924",
            ConfigItem.configTypeBool,
            false
        )
    val switch1925 =
        addConfig(
            "Switch1925",
            ConfigItem.configTypeBool,
            false
        )
    val switch1926 =
        addConfig(
            "Switch1926",
            ConfigItem.configTypeBool,
            false
        )
    val switch1927 =
        addConfig(
            "Switch1927",
            ConfigItem.configTypeBool,
            false
        )
    val switch1928 =
        addConfig(
            "Switch1928",
            ConfigItem.configTypeBool,
            false
        )
    val switch1929 =
        addConfig(
            "Switch1929",
            ConfigItem.configTypeBool,
            false
        )
    val switch1930 =
        addConfig(
            "Switch1930",
            ConfigItem.configTypeBool,
            false
        )
    val switch1931 =
        addConfig(
            "Switch1931",
            ConfigItem.configTypeBool,
            false
        )
    val switch1932 =
        addConfig(
            "Switch1932",
            ConfigItem.configTypeBool,
            false
        )
    val switch1933 =
        addConfig(
            "Switch1933",
            ConfigItem.configTypeBool,
            false
        )
    val switch1934 =
        addConfig(
            "Switch1934",
            ConfigItem.configTypeBool,
            false
        )
    val switch1935 =
        addConfig(
            "Switch1935",
            ConfigItem.configTypeBool,
            false
        )
    val switch1936 =
        addConfig(
            "Switch1936",
            ConfigItem.configTypeBool,
            false
        )
    val switch1937 =
        addConfig(
            "Switch1937",
            ConfigItem.configTypeBool,
            false
        )
    val switch1938 =
        addConfig(
            "Switch1938",
            ConfigItem.configTypeBool,
            false
        )
    val switch1939 =
        addConfig(
            "Switch1939",
            ConfigItem.configTypeBool,
            false
        )
    val switch1940 =
        addConfig(
            "Switch1940",
            ConfigItem.configTypeBool,
            false
        )
    val switch1941 =
        addConfig(
            "Switch1941",
            ConfigItem.configTypeBool,
            false
        )
    val switch1942 =
        addConfig(
            "Switch1942",
            ConfigItem.configTypeBool,
            false
        )
    val switch1943 =
        addConfig(
            "Switch1943",
            ConfigItem.configTypeBool,
            false
        )
    val switch1944 =
        addConfig(
            "Switch1944",
            ConfigItem.configTypeBool,
            false
        )
    val switch1945 =
        addConfig(
            "Switch1945",
            ConfigItem.configTypeBool,
            false
        )
    val switch1946 =
        addConfig(
            "Switch1946",
            ConfigItem.configTypeBool,
            false
        )
    val switch1947 =
        addConfig(
            "Switch1947",
            ConfigItem.configTypeBool,
            false
        )
    val switch1948 =
        addConfig(
            "Switch1948",
            ConfigItem.configTypeBool,
            false
        )
    val switch1949 =
        addConfig(
            "Switch1949",
            ConfigItem.configTypeBool,
            false
        )
    val switch1950 =
        addConfig(
            "Switch1950",
            ConfigItem.configTypeBool,
            false
        )
    val switch1951 =
        addConfig(
            "Switch1951",
            ConfigItem.configTypeBool,
            false
        )
    val switch1952 =
        addConfig(
            "Switch1952",
            ConfigItem.configTypeBool,
            false
        )
    val switch1953 =
        addConfig(
            "Switch1953",
            ConfigItem.configTypeBool,
            false
        )
    val switch1954 =
        addConfig(
            "Switch1954",
            ConfigItem.configTypeBool,
            false
        )
    val switch1955 =
        addConfig(
            "Switch1955",
            ConfigItem.configTypeBool,
            false
        )
    val switch1956 =
        addConfig(
            "Switch1956",
            ConfigItem.configTypeBool,
            false
        )
    val switch1957 =
        addConfig(
            "Switch1957",
            ConfigItem.configTypeBool,
            false
        )
    val switch1958 =
        addConfig(
            "Switch1958",
            ConfigItem.configTypeBool,
            false
        )
    val switch1959 =
        addConfig(
            "Switch1959",
            ConfigItem.configTypeBool,
            false
        )
    val switch1960 =
        addConfig(
            "Switch1960",
            ConfigItem.configTypeBool,
            false
        )
    val switch1961 =
        addConfig(
            "Switch1961",
            ConfigItem.configTypeBool,
            false
        )
    val switch1962 =
        addConfig(
            "Switch1962",
            ConfigItem.configTypeBool,
            false
        )
    val switch1963 =
        addConfig(
            "Switch1963",
            ConfigItem.configTypeBool,
            false
        )
    val switch1964 =
        addConfig(
            "Switch1964",
            ConfigItem.configTypeBool,
            false
        )
    val switch1965 =
        addConfig(
            "Switch1965",
            ConfigItem.configTypeBool,
            false
        )
    val switch1966 =
        addConfig(
            "Switch1966",
            ConfigItem.configTypeBool,
            false
        )
    val switch1967 =
        addConfig(
            "Switch1967",
            ConfigItem.configTypeBool,
            false
        )
    val switch1968 =
        addConfig(
            "Switch1968",
            ConfigItem.configTypeBool,
            false
        )
    val switch1969 =
        addConfig(
            "Switch1969",
            ConfigItem.configTypeBool,
            false
        )
    val switch1970 =
        addConfig(
            "Switch1970",
            ConfigItem.configTypeBool,
            false
        )
    val switch1971 =
        addConfig(
            "Switch1971",
            ConfigItem.configTypeBool,
            false
        )
    val switch1972 =
        addConfig(
            "Switch1972",
            ConfigItem.configTypeBool,
            false
        )
    val switch1973 =
        addConfig(
            "Switch1973",
            ConfigItem.configTypeBool,
            false
        )
    val switch1974 =
        addConfig(
            "Switch1974",
            ConfigItem.configTypeBool,
            false
        )
    val switch1975 =
        addConfig(
            "Switch1975",
            ConfigItem.configTypeBool,
            false
        )
    val switch1976 =
        addConfig(
            "Switch1976",
            ConfigItem.configTypeBool,
            false
        )
    val switch1977 =
        addConfig(
            "Switch1977",
            ConfigItem.configTypeBool,
            false
        )
    val switch1978 =
        addConfig(
            "Switch1978",
            ConfigItem.configTypeBool,
            false
        )
    val switch1979 =
        addConfig(
            "Switch1979",
            ConfigItem.configTypeBool,
            false
        )
    val switch1980 =
        addConfig(
            "Switch1980",
            ConfigItem.configTypeBool,
            false
        )
    val switch1981 =
        addConfig(
            "Switch1981",
            ConfigItem.configTypeBool,
            false
        )
    val switch1982 =
        addConfig(
            "Switch1982",
            ConfigItem.configTypeBool,
            false
        )
    val switch1983 =
        addConfig(
            "Switch1983",
            ConfigItem.configTypeBool,
            false
        )
    val switch1984 =
        addConfig(
            "Switch1984",
            ConfigItem.configTypeBool,
            false
        )
    val switch1985 =
        addConfig(
            "Switch1985",
            ConfigItem.configTypeBool,
            false
        )
    val switch1986 =
        addConfig(
            "Switch1986",
            ConfigItem.configTypeBool,
            false
        )
    val switch1987 =
        addConfig(
            "Switch1987",
            ConfigItem.configTypeBool,
            false
        )
    val switch1988 =
        addConfig(
            "Switch1988",
            ConfigItem.configTypeBool,
            false
        )
    val switch1989 =
        addConfig(
            "Switch1989",
            ConfigItem.configTypeBool,
            false
        )
    val switch1990 =
        addConfig(
            "Switch1990",
            ConfigItem.configTypeBool,
            false
        )
    val switch1991 =
        addConfig(
            "Switch1991",
            ConfigItem.configTypeBool,
            false
        )
    val switch1992 =
        addConfig(
            "Switch1992",
            ConfigItem.configTypeBool,
            false
        )
    val switch1993 =
        addConfig(
            "Switch1993",
            ConfigItem.configTypeBool,
            false
        )
    val switch1994 =
        addConfig(
            "Switch1994",
            ConfigItem.configTypeBool,
            false
        )
    val switch1995 =
        addConfig(
            "Switch1995",
            ConfigItem.configTypeBool,
            false
        )
    val switch1996 =
        addConfig(
            "Switch1996",
            ConfigItem.configTypeBool,
            false
        )
    val switch1997 =
        addConfig(
            "Switch1997",
            ConfigItem.configTypeBool,
            false
        )
    val switch1998 =
        addConfig(
            "Switch1998",
            ConfigItem.configTypeBool,
            false
        )
    val switch1999 =
        addConfig(
            "Switch1999",
            ConfigItem.configTypeBool,
            false
        )
    val switch2000 =
        addConfig(
            "Switch2000",
            ConfigItem.configTypeBool,
            false
        )
    val switch2001 =
        addConfig(
            "Switch2001",
            ConfigItem.configTypeBool,
            false
        )
    val switch2002 =
        addConfig(
            "Switch2002",
            ConfigItem.configTypeBool,
            false
        )
    val switch2003 =
        addConfig(
            "Switch2003",
            ConfigItem.configTypeBool,
            false
        )
    val switch2004 =
        addConfig(
            "Switch2004",
            ConfigItem.configTypeBool,
            false
        )
    val switch2005 =
        addConfig(
            "Switch2005",
            ConfigItem.configTypeBool,
            false
        )
    val switch2006 =
        addConfig(
            "Switch2006",
            ConfigItem.configTypeBool,
            false
        )
    val switch2007 =
        addConfig(
            "Switch2007",
            ConfigItem.configTypeBool,
            false
        )
    val switch2008 =
        addConfig(
            "Switch2008",
            ConfigItem.configTypeBool,
            false
        )
    val switch2009 =
        addConfig(
            "Switch2009",
            ConfigItem.configTypeBool,
            false
        )
    val switch2010 =
        addConfig(
            "Switch2010",
            ConfigItem.configTypeBool,
            false
        )
    val switch2011 =
        addConfig(
            "Switch2011",
            ConfigItem.configTypeBool,
            false
        )
    val switch2012 =
        addConfig(
            "Switch2012",
            ConfigItem.configTypeBool,
            false
        )
    val switch2013 =
        addConfig(
            "Switch2013",
            ConfigItem.configTypeBool,
            false
        )
    val switch2014 =
        addConfig(
            "Switch2014",
            ConfigItem.configTypeBool,
            false
        )
    val switch2015 =
        addConfig(
            "Switch2015",
            ConfigItem.configTypeBool,
            false
        )
    val switch2016 =
        addConfig(
            "Switch2016",
            ConfigItem.configTypeBool,
            false
        )
    val switch2017 =
        addConfig(
            "Switch2017",
            ConfigItem.configTypeBool,
            false
        )
    val switch2018 =
        addConfig(
            "Switch2018",
            ConfigItem.configTypeBool,
            false
        )
    val switch2019 =
        addConfig(
            "Switch2019",
            ConfigItem.configTypeBool,
            false
        )
    val switch2020 =
        addConfig(
            "Switch2020",
            ConfigItem.configTypeBool,
            false
        )
    val switch2021 =
        addConfig(
            "Switch2021",
            ConfigItem.configTypeBool,
            false
        )
    val switch2022 =
        addConfig(
            "Switch2022",
            ConfigItem.configTypeBool,
            false
        )
    val switch2023 =
        addConfig(
            "Switch2023",
            ConfigItem.configTypeBool,
            false
        )
    val switch2024 =
        addConfig(
            "Switch2024",
            ConfigItem.configTypeBool,
            false
        )
    val switch2025 =
        addConfig(
            "Switch2025",
            ConfigItem.configTypeBool,
            false
        )
    val switch2026 =
        addConfig(
            "Switch2026",
            ConfigItem.configTypeBool,
            false
        )
    val switch2027 =
        addConfig(
            "Switch2027",
            ConfigItem.configTypeBool,
            false
        )
    val switch2028 =
        addConfig(
            "Switch2028",
            ConfigItem.configTypeBool,
            false
        )
    val switch2029 =
        addConfig(
            "Switch2029",
            ConfigItem.configTypeBool,
            false
        )
    val switch2030 =
        addConfig(
            "Switch2030",
            ConfigItem.configTypeBool,
            false
        )
    val switch2031 =
        addConfig(
            "Switch2031",
            ConfigItem.configTypeBool,
            false
        )
    val switch2032 =
        addConfig(
            "Switch2032",
            ConfigItem.configTypeBool,
            false
        )
    val switch2033 =
        addConfig(
            "Switch2033",
            ConfigItem.configTypeBool,
            false
        )
    val switch2034 =
        addConfig(
            "Switch2034",
            ConfigItem.configTypeBool,
            false
        )
    val switch2035 =
        addConfig(
            "Switch2035",
            ConfigItem.configTypeBool,
            false
        )
    val switch2036 =
        addConfig(
            "Switch2036",
            ConfigItem.configTypeBool,
            false
        )
    val switch2037 =
        addConfig(
            "Switch2037",
            ConfigItem.configTypeBool,
            false
        )
    val switch2038 =
        addConfig(
            "Switch2038",
            ConfigItem.configTypeBool,
            false
        )
    val switch2039 =
        addConfig(
            "Switch2039",
            ConfigItem.configTypeBool,
            false
        )
    val switch2040 =
        addConfig(
            "Switch2040",
            ConfigItem.configTypeBool,
            false
        )
    val switch2041 =
        addConfig(
            "Switch2041",
            ConfigItem.configTypeBool,
            false
        )
    val switch2042 =
        addConfig(
            "Switch2042",
            ConfigItem.configTypeBool,
            false
        )
    val switch2043 =
        addConfig(
            "Switch2043",
            ConfigItem.configTypeBool,
            false
        )
    val switch2044 =
        addConfig(
            "Switch2044",
            ConfigItem.configTypeBool,
            false
        )
    val switch2045 =
        addConfig(
            "Switch2045",
            ConfigItem.configTypeBool,
            false
        )
    val switch2046 =
        addConfig(
            "Switch2046",
            ConfigItem.configTypeBool,
            false
        )
    val switch2047 =
        addConfig(
            "Switch2047",
            ConfigItem.configTypeBool,
            false
        )
    val switch2048 =
        addConfig(
            "Switch2048",
            ConfigItem.configTypeBool,
            false
        )
    val switch2049 =
        addConfig(
            "Switch2049",
            ConfigItem.configTypeBool,
            false
        )
    val switch2050 =
        addConfig(
            "Switch2050",
            ConfigItem.configTypeBool,
            false
        )
    val switch2051 =
        addConfig(
            "Switch2051",
            ConfigItem.configTypeBool,
            false
        )
    val switch2052 =
        addConfig(
            "Switch2052",
            ConfigItem.configTypeBool,
            false
        )
    val switch2053 =
        addConfig(
            "Switch2053",
            ConfigItem.configTypeBool,
            false
        )
    val switch2054 =
        addConfig(
            "Switch2054",
            ConfigItem.configTypeBool,
            false
        )
    val switch2055 =
        addConfig(
            "Switch2055",
            ConfigItem.configTypeBool,
            false
        )
    val switch2056 =
        addConfig(
            "Switch2056",
            ConfigItem.configTypeBool,
            false
        )
    val switch2057 =
        addConfig(
            "Switch2057",
            ConfigItem.configTypeBool,
            false
        )
    val switch2058 =
        addConfig(
            "Switch2058",
            ConfigItem.configTypeBool,
            false
        )
    val switch2059 =
        addConfig(
            "Switch2059",
            ConfigItem.configTypeBool,
            false
        )
    val switch2060 =
        addConfig(
            "Switch2060",
            ConfigItem.configTypeBool,
            false
        )
    val switch2061 =
        addConfig(
            "Switch2061",
            ConfigItem.configTypeBool,
            false
        )
    val switch2062 =
        addConfig(
            "Switch2062",
            ConfigItem.configTypeBool,
            false
        )
    val switch2063 =
        addConfig(
            "Switch2063",
            ConfigItem.configTypeBool,
            false
        )
    val switch2064 =
        addConfig(
            "Switch2064",
            ConfigItem.configTypeBool,
            false
        )
    val switch2065 =
        addConfig(
            "Switch2065",
            ConfigItem.configTypeBool,
            false
        )
    val switch2066 =
        addConfig(
            "Switch2066",
            ConfigItem.configTypeBool,
            false
        )
    val switch2067 =
        addConfig(
            "Switch2067",
            ConfigItem.configTypeBool,
            false
        )
    val switch2068 =
        addConfig(
            "Switch2068",
            ConfigItem.configTypeBool,
            false
        )
    val switch2069 =
        addConfig(
            "Switch2069",
            ConfigItem.configTypeBool,
            false
        )
    val switch2070 =
        addConfig(
            "Switch2070",
            ConfigItem.configTypeBool,
            false
        )
    val switch2071 =
        addConfig(
            "Switch2071",
            ConfigItem.configTypeBool,
            false
        )
    val switch2072 =
        addConfig(
            "Switch2072",
            ConfigItem.configTypeBool,
            false
        )
    val switch2073 =
        addConfig(
            "Switch2073",
            ConfigItem.configTypeBool,
            false
        )
    val switch2074 =
        addConfig(
            "Switch2074",
            ConfigItem.configTypeBool,
            false
        )
    val switch2075 =
        addConfig(
            "Switch2075",
            ConfigItem.configTypeBool,
            false
        )
    val switch2076 =
        addConfig(
            "Switch2076",
            ConfigItem.configTypeBool,
            false
        )
    val switch2077 =
        addConfig(
            "Switch2077",
            ConfigItem.configTypeBool,
            false
        )
    val switch2078 =
        addConfig(
            "Switch2078",
            ConfigItem.configTypeBool,
            false
        )
    val switch2079 =
        addConfig(
            "Switch2079",
            ConfigItem.configTypeBool,
            false
        )
    val switch2080 =
        addConfig(
            "Switch2080",
            ConfigItem.configTypeBool,
            false
        )
    val switch2081 =
        addConfig(
            "Switch2081",
            ConfigItem.configTypeBool,
            false
        )
    val switch2082 =
        addConfig(
            "Switch2082",
            ConfigItem.configTypeBool,
            false
        )
    val switch2083 =
        addConfig(
            "Switch2083",
            ConfigItem.configTypeBool,
            false
        )
    val switch2084 =
        addConfig(
            "Switch2084",
            ConfigItem.configTypeBool,
            false
        )
    val switch2085 =
        addConfig(
            "Switch2085",
            ConfigItem.configTypeBool,
            false
        )
    val switch2086 =
        addConfig(
            "Switch2086",
            ConfigItem.configTypeBool,
            false
        )
    val switch2087 =
        addConfig(
            "Switch2087",
            ConfigItem.configTypeBool,
            false
        )
    val switch2088 =
        addConfig(
            "Switch2088",
            ConfigItem.configTypeBool,
            false
        )
    val switch2089 =
        addConfig(
            "Switch2089",
            ConfigItem.configTypeBool,
            false
        )
    val switch2090 =
        addConfig(
            "Switch2090",
            ConfigItem.configTypeBool,
            false
        )
    val switch2091 =
        addConfig(
            "Switch2091",
            ConfigItem.configTypeBool,
            false
        )
    val switch2092 =
        addConfig(
            "Switch2092",
            ConfigItem.configTypeBool,
            false
        )
    val switch2093 =
        addConfig(
            "Switch2093",
            ConfigItem.configTypeBool,
            false
        )
    val switch2094 =
        addConfig(
            "Switch2094",
            ConfigItem.configTypeBool,
            false
        )
    val switch2095 =
        addConfig(
            "Switch2095",
            ConfigItem.configTypeBool,
            false
        )
    val switch2096 =
        addConfig(
            "Switch2096",
            ConfigItem.configTypeBool,
            false
        )
    val switch2097 =
        addConfig(
            "Switch2097",
            ConfigItem.configTypeBool,
            false
        )
    val switch2098 =
        addConfig(
            "Switch2098",
            ConfigItem.configTypeBool,
            false
        )
    val switch2099 =
        addConfig(
            "Switch2099",
            ConfigItem.configTypeBool,
            false
        )
    val switch2100 =
        addConfig(
            "Switch2100",
            ConfigItem.configTypeBool,
            false
        )
    val switch2101 =
        addConfig(
            "Switch2101",
            ConfigItem.configTypeBool,
            false
        )
    val switch2102 =
        addConfig(
            "Switch2102",
            ConfigItem.configTypeBool,
            false
        )
    val switch2103 =
        addConfig(
            "Switch2103",
            ConfigItem.configTypeBool,
            false
        )
    val switch2104 =
        addConfig(
            "Switch2104",
            ConfigItem.configTypeBool,
            false
        )
    val switch2105 =
        addConfig(
            "Switch2105",
            ConfigItem.configTypeBool,
            false
        )
    val switch2106 =
        addConfig(
            "Switch2106",
            ConfigItem.configTypeBool,
            false
        )
    val switch2107 =
        addConfig(
            "Switch2107",
            ConfigItem.configTypeBool,
            false
        )
    val switch2108 =
        addConfig(
            "Switch2108",
            ConfigItem.configTypeBool,
            false
        )
    val switch2109 =
        addConfig(
            "Switch2109",
            ConfigItem.configTypeBool,
            false
        )
    val switch2110 =
        addConfig(
            "Switch2110",
            ConfigItem.configTypeBool,
            false
        )
    val switch2111 =
        addConfig(
            "Switch2111",
            ConfigItem.configTypeBool,
            false
        )
    val switch2112 =
        addConfig(
            "Switch2112",
            ConfigItem.configTypeBool,
            false
        )
    val switch2113 =
        addConfig(
            "Switch2113",
            ConfigItem.configTypeBool,
            false
        )
    val switch2114 =
        addConfig(
            "Switch2114",
            ConfigItem.configTypeBool,
            false
        )
    val switch2115 =
        addConfig(
            "Switch2115",
            ConfigItem.configTypeBool,
            false
        )
    val switch2116 =
        addConfig(
            "Switch2116",
            ConfigItem.configTypeBool,
            false
        )
    val switch2117 =
        addConfig(
            "Switch2117",
            ConfigItem.configTypeBool,
            false
        )
    val switch2118 =
        addConfig(
            "Switch2118",
            ConfigItem.configTypeBool,
            false
        )
    val switch2119 =
        addConfig(
            "Switch2119",
            ConfigItem.configTypeBool,
            false
        )
    val switch2120 =
        addConfig(
            "Switch2120",
            ConfigItem.configTypeBool,
            false
        )
    val switch2121 =
        addConfig(
            "Switch2121",
            ConfigItem.configTypeBool,
            false
        )
    val switch2122 =
        addConfig(
            "Switch2122",
            ConfigItem.configTypeBool,
            false
        )
    val switch2123 =
        addConfig(
            "Switch2123",
            ConfigItem.configTypeBool,
            false
        )
    val switch2124 =
        addConfig(
            "Switch2124",
            ConfigItem.configTypeBool,
            false
        )
    val switch2125 =
        addConfig(
            "Switch2125",
            ConfigItem.configTypeBool,
            false
        )
    val switch2126 =
        addConfig(
            "Switch2126",
            ConfigItem.configTypeBool,
            false
        )
    val switch2127 =
        addConfig(
            "Switch2127",
            ConfigItem.configTypeBool,
            false
        )
    val switch2128 =
        addConfig(
            "Switch2128",
            ConfigItem.configTypeBool,
            false
        )
    val switch2129 =
        addConfig(
            "Switch2129",
            ConfigItem.configTypeBool,
            false
        )
    val switch2130 =
        addConfig(
            "Switch2130",
            ConfigItem.configTypeBool,
            false
        )
    val switch2131 =
        addConfig(
            "Switch2131",
            ConfigItem.configTypeBool,
            false
        )
    val switch2132 =
        addConfig(
            "Switch2132",
            ConfigItem.configTypeBool,
            false
        )
    val switch2133 =
        addConfig(
            "Switch2133",
            ConfigItem.configTypeBool,
            false
        )
    val switch2134 =
        addConfig(
            "Switch2134",
            ConfigItem.configTypeBool,
            false
        )
    val switch2135 =
        addConfig(
            "Switch2135",
            ConfigItem.configTypeBool,
            false
        )
    val switch2136 =
        addConfig(
            "Switch2136",
            ConfigItem.configTypeBool,
            false
        )
    val switch2137 =
        addConfig(
            "Switch2137",
            ConfigItem.configTypeBool,
            false
        )
    val switch2138 =
        addConfig(
            "Switch2138",
            ConfigItem.configTypeBool,
            false
        )
    val switch2139 =
        addConfig(
            "Switch2139",
            ConfigItem.configTypeBool,
            false
        )
    val switch2140 =
        addConfig(
            "Switch2140",
            ConfigItem.configTypeBool,
            false
        )
    val switch2141 =
        addConfig(
            "Switch2141",
            ConfigItem.configTypeBool,
            false
        )
    val switch2142 =
        addConfig(
            "Switch2142",
            ConfigItem.configTypeBool,
            false
        )
    val switch2143 =
        addConfig(
            "Switch2143",
            ConfigItem.configTypeBool,
            false
        )
    val switch2144 =
        addConfig(
            "Switch2144",
            ConfigItem.configTypeBool,
            false
        )
    val switch2145 =
        addConfig(
            "Switch2145",
            ConfigItem.configTypeBool,
            false
        )
    val switch2146 =
        addConfig(
            "Switch2146",
            ConfigItem.configTypeBool,
            false
        )
    val switch2147 =
        addConfig(
            "Switch2147",
            ConfigItem.configTypeBool,
            false
        )
    val switch2148 =
        addConfig(
            "Switch2148",
            ConfigItem.configTypeBool,
            false
        )
    val switch2149 =
        addConfig(
            "Switch2149",
            ConfigItem.configTypeBool,
            false
        )
    val switch2150 =
        addConfig(
            "Switch2150",
            ConfigItem.configTypeBool,
            false
        )
    val switch2151 =
        addConfig(
            "Switch2151",
            ConfigItem.configTypeBool,
            false
        )
    val switch2152 =
        addConfig(
            "Switch2152",
            ConfigItem.configTypeBool,
            false
        )
    val switch2153 =
        addConfig(
            "Switch2153",
            ConfigItem.configTypeBool,
            false
        )
    val switch2154 =
        addConfig(
            "Switch2154",
            ConfigItem.configTypeBool,
            false
        )
    val switch2155 =
        addConfig(
            "Switch2155",
            ConfigItem.configTypeBool,
            false
        )
    val switch2156 =
        addConfig(
            "Switch2156",
            ConfigItem.configTypeBool,
            false
        )
    val switch2157 =
        addConfig(
            "Switch2157",
            ConfigItem.configTypeBool,
            false
        )
    val switch2158 =
        addConfig(
            "Switch2158",
            ConfigItem.configTypeBool,
            false
        )
    val switch2159 =
        addConfig(
            "Switch2159",
            ConfigItem.configTypeBool,
            false
        )
    val switch2160 =
        addConfig(
            "Switch2160",
            ConfigItem.configTypeBool,
            false
        )
    val switch2161 =
        addConfig(
            "Switch2161",
            ConfigItem.configTypeBool,
            false
        )
    val switch2162 =
        addConfig(
            "Switch2162",
            ConfigItem.configTypeBool,
            false
        )
    val switch2163 =
        addConfig(
            "Switch2163",
            ConfigItem.configTypeBool,
            false
        )
    val switch2164 =
        addConfig(
            "Switch2164",
            ConfigItem.configTypeBool,
            false
        )
    val switch2165 =
        addConfig(
            "Switch2165",
            ConfigItem.configTypeBool,
            false
        )
    val switch2166 =
        addConfig(
            "Switch2166",
            ConfigItem.configTypeBool,
            false
        )
    val switch2167 =
        addConfig(
            "Switch2167",
            ConfigItem.configTypeBool,
            false
        )
    val switch2168 =
        addConfig(
            "Switch2168",
            ConfigItem.configTypeBool,
            false
        )
    val switch2169 =
        addConfig(
            "Switch2169",
            ConfigItem.configTypeBool,
            false
        )
    val switch2170 =
        addConfig(
            "Switch2170",
            ConfigItem.configTypeBool,
            false
        )
    val switch2171 =
        addConfig(
            "Switch2171",
            ConfigItem.configTypeBool,
            false
        )
    val switch2172 =
        addConfig(
            "Switch2172",
            ConfigItem.configTypeBool,
            false
        )
    val switch2173 =
        addConfig(
            "Switch2173",
            ConfigItem.configTypeBool,
            false
        )
    val switch2174 =
        addConfig(
            "Switch2174",
            ConfigItem.configTypeBool,
            false
        )
    val switch2175 =
        addConfig(
            "Switch2175",
            ConfigItem.configTypeBool,
            false
        )
    val switch2176 =
        addConfig(
            "Switch2176",
            ConfigItem.configTypeBool,
            false
        )
    val switch2177 =
        addConfig(
            "Switch2177",
            ConfigItem.configTypeBool,
            false
        )
    val switch2178 =
        addConfig(
            "Switch2178",
            ConfigItem.configTypeBool,
            false
        )
    val switch2179 =
        addConfig(
            "Switch2179",
            ConfigItem.configTypeBool,
            false
        )
    val switch2180 =
        addConfig(
            "Switch2180",
            ConfigItem.configTypeBool,
            false
        )
    val switch2181 =
        addConfig(
            "Switch2181",
            ConfigItem.configTypeBool,
            false
        )
    val switch2182 =
        addConfig(
            "Switch2182",
            ConfigItem.configTypeBool,
            false
        )
    val switch2183 =
        addConfig(
            "Switch2183",
            ConfigItem.configTypeBool,
            false
        )
    val switch2184 =
        addConfig(
            "Switch2184",
            ConfigItem.configTypeBool,
            false
        )
    val switch2185 =
        addConfig(
            "Switch2185",
            ConfigItem.configTypeBool,
            false
        )
    val switch2186 =
        addConfig(
            "Switch2186",
            ConfigItem.configTypeBool,
            false
        )
    val switch2187 =
        addConfig(
            "Switch2187",
            ConfigItem.configTypeBool,
            false
        )
    val switch2188 =
        addConfig(
            "Switch2188",
            ConfigItem.configTypeBool,
            false
        )
    val switch2189 =
        addConfig(
            "Switch2189",
            ConfigItem.configTypeBool,
            false
        )
    val switch2190 =
        addConfig(
            "Switch2190",
            ConfigItem.configTypeBool,
            false
        )
    val switch2191 =
        addConfig(
            "Switch2191",
            ConfigItem.configTypeBool,
            false
        )
    val switch2192 =
        addConfig(
            "Switch2192",
            ConfigItem.configTypeBool,
            false
        )
    val switch2193 =
        addConfig(
            "Switch2193",
            ConfigItem.configTypeBool,
            false
        )
    val switch2194 =
        addConfig(
            "Switch2194",
            ConfigItem.configTypeBool,
            false
        )
    val switch2195 =
        addConfig(
            "Switch2195",
            ConfigItem.configTypeBool,
            false
        )
    val switch2196 =
        addConfig(
            "Switch2196",
            ConfigItem.configTypeBool,
            false
        )
    val switch2197 =
        addConfig(
            "Switch2197",
            ConfigItem.configTypeBool,
            false
        )
    val switch2198 =
        addConfig(
            "Switch2198",
            ConfigItem.configTypeBool,
            false
        )
    val switch2199 =
        addConfig(
            "Switch2199",
            ConfigItem.configTypeBool,
            false
        )
    val switch2200 =
        addConfig(
            "Switch2200",
            ConfigItem.configTypeBool,
            false
        )
    val switch2201 =
        addConfig(
            "Switch2201",
            ConfigItem.configTypeBool,
            false
        )
    val switch2202 =
        addConfig(
            "Switch2202",
            ConfigItem.configTypeBool,
            false
        )
    val switch2203 =
        addConfig(
            "Switch2203",
            ConfigItem.configTypeBool,
            false
        )
    val switch2204 =
        addConfig(
            "Switch2204",
            ConfigItem.configTypeBool,
            false
        )
    val switch2205 =
        addConfig(
            "Switch2205",
            ConfigItem.configTypeBool,
            false
        )
    val switch2206 =
        addConfig(
            "Switch2206",
            ConfigItem.configTypeBool,
            false
        )
    val switch2207 =
        addConfig(
            "Switch2207",
            ConfigItem.configTypeBool,
            false
        )
    val switch2208 =
        addConfig(
            "Switch2208",
            ConfigItem.configTypeBool,
            false
        )
    val switch2209 =
        addConfig(
            "Switch2209",
            ConfigItem.configTypeBool,
            false
        )
    val switch2210 =
        addConfig(
            "Switch2210",
            ConfigItem.configTypeBool,
            false
        )
    val switch2211 =
        addConfig(
            "Switch2211",
            ConfigItem.configTypeBool,
            false
        )
    val switch2212 =
        addConfig(
            "Switch2212",
            ConfigItem.configTypeBool,
            false
        )
    val switch2213 =
        addConfig(
            "Switch2213",
            ConfigItem.configTypeBool,
            false
        )
    val switch2214 =
        addConfig(
            "Switch2214",
            ConfigItem.configTypeBool,
            false
        )
    val switch2215 =
        addConfig(
            "Switch2215",
            ConfigItem.configTypeBool,
            false
        )
    val switch2216 =
        addConfig(
            "Switch2216",
            ConfigItem.configTypeBool,
            false
        )
    val switch2217 =
        addConfig(
            "Switch2217",
            ConfigItem.configTypeBool,
            false
        )
    val switch2218 =
        addConfig(
            "Switch2218",
            ConfigItem.configTypeBool,
            false
        )
    val switch2219 =
        addConfig(
            "Switch2219",
            ConfigItem.configTypeBool,
            false
        )
    val switch2220 =
        addConfig(
            "Switch2220",
            ConfigItem.configTypeBool,
            false
        )
    val switch2221 =
        addConfig(
            "Switch2221",
            ConfigItem.configTypeBool,
            false
        )
    val switch2222 =
        addConfig(
            "Switch2222",
            ConfigItem.configTypeBool,
            false
        )
    val switch2223 =
        addConfig(
            "Switch2223",
            ConfigItem.configTypeBool,
            false
        )
    val switch2224 =
        addConfig(
            "Switch2224",
            ConfigItem.configTypeBool,
            false
        )
    val switch2225 =
        addConfig(
            "Switch2225",
            ConfigItem.configTypeBool,
            false
        )
    val switch2226 =
        addConfig(
            "Switch2226",
            ConfigItem.configTypeBool,
            false
        )
    val switch2227 =
        addConfig(
            "Switch2227",
            ConfigItem.configTypeBool,
            false
        )
    val switch2228 =
        addConfig(
            "Switch2228",
            ConfigItem.configTypeBool,
            false
        )
    val switch2229 =
        addConfig(
            "Switch2229",
            ConfigItem.configTypeBool,
            false
        )
    val switch2230 =
        addConfig(
            "Switch2230",
            ConfigItem.configTypeBool,
            false
        )
    val switch2231 =
        addConfig(
            "Switch2231",
            ConfigItem.configTypeBool,
            false
        )
    val switch2232 =
        addConfig(
            "Switch2232",
            ConfigItem.configTypeBool,
            false
        )
    val switch2233 =
        addConfig(
            "Switch2233",
            ConfigItem.configTypeBool,
            false
        )
    val switch2234 =
        addConfig(
            "Switch2234",
            ConfigItem.configTypeBool,
            false
        )
    val switch2235 =
        addConfig(
            "Switch2235",
            ConfigItem.configTypeBool,
            false
        )
    val switch2236 =
        addConfig(
            "Switch2236",
            ConfigItem.configTypeBool,
            false
        )
    val switch2237 =
        addConfig(
            "Switch2237",
            ConfigItem.configTypeBool,
            false
        )
    val switch2238 =
        addConfig(
            "Switch2238",
            ConfigItem.configTypeBool,
            false
        )
    val switch2239 =
        addConfig(
            "Switch2239",
            ConfigItem.configTypeBool,
            false
        )
    val switch2240 =
        addConfig(
            "Switch2240",
            ConfigItem.configTypeBool,
            false
        )
    val switch2241 =
        addConfig(
            "Switch2241",
            ConfigItem.configTypeBool,
            false
        )
    val switch2242 =
        addConfig(
            "Switch2242",
            ConfigItem.configTypeBool,
            false
        )
    val switch2243 =
        addConfig(
            "Switch2243",
            ConfigItem.configTypeBool,
            false
        )
    val switch2244 =
        addConfig(
            "Switch2244",
            ConfigItem.configTypeBool,
            false
        )
    val switch2245 =
        addConfig(
            "Switch2245",
            ConfigItem.configTypeBool,
            false
        )
    val switch2246 =
        addConfig(
            "Switch2246",
            ConfigItem.configTypeBool,
            false
        )
    val switch2247 =
        addConfig(
            "Switch2247",
            ConfigItem.configTypeBool,
            false
        )
    val switch2248 =
        addConfig(
            "Switch2248",
            ConfigItem.configTypeBool,
            false
        )
    val switch2249 =
        addConfig(
            "Switch2249",
            ConfigItem.configTypeBool,
            false
        )
    val switch2250 =
        addConfig(
            "Switch2250",
            ConfigItem.configTypeBool,
            false
        )
    val switch2251 =
        addConfig(
            "Switch2251",
            ConfigItem.configTypeBool,
            false
        )
    val switch2252 =
        addConfig(
            "Switch2252",
            ConfigItem.configTypeBool,
            false
        )
    val switch2253 =
        addConfig(
            "Switch2253",
            ConfigItem.configTypeBool,
            false
        )
    val switch2254 =
        addConfig(
            "Switch2254",
            ConfigItem.configTypeBool,
            false
        )
    val switch2255 =
        addConfig(
            "Switch2255",
            ConfigItem.configTypeBool,
            false
        )
    val switch2256 =
        addConfig(
            "Switch2256",
            ConfigItem.configTypeBool,
            false
        )
    val switch2257 =
        addConfig(
            "Switch2257",
            ConfigItem.configTypeBool,
            false
        )
    val switch2258 =
        addConfig(
            "Switch2258",
            ConfigItem.configTypeBool,
            false
        )
    val switch2259 =
        addConfig(
            "Switch2259",
            ConfigItem.configTypeBool,
            false
        )
    val switch2260 =
        addConfig(
            "Switch2260",
            ConfigItem.configTypeBool,
            false
        )
    val switch2261 =
        addConfig(
            "Switch2261",
            ConfigItem.configTypeBool,
            false
        )
    val switch2262 =
        addConfig(
            "Switch2262",
            ConfigItem.configTypeBool,
            false
        )
    val switch2263 =
        addConfig(
            "Switch2263",
            ConfigItem.configTypeBool,
            false
        )
    val switch2264 =
        addConfig(
            "Switch2264",
            ConfigItem.configTypeBool,
            false
        )
    val switch2265 =
        addConfig(
            "Switch2265",
            ConfigItem.configTypeBool,
            false
        )
    val switch2266 =
        addConfig(
            "Switch2266",
            ConfigItem.configTypeBool,
            false
        )
    val switch2267 =
        addConfig(
            "Switch2267",
            ConfigItem.configTypeBool,
            false
        )
    val switch2268 =
        addConfig(
            "Switch2268",
            ConfigItem.configTypeBool,
            false
        )
    val switch2269 =
        addConfig(
            "Switch2269",
            ConfigItem.configTypeBool,
            false
        )
    val switch2270 =
        addConfig(
            "Switch2270",
            ConfigItem.configTypeBool,
            false
        )
    val switch2271 =
        addConfig(
            "Switch2271",
            ConfigItem.configTypeBool,
            false
        )
    val switch2272 =
        addConfig(
            "Switch2272",
            ConfigItem.configTypeBool,
            false
        )
    val switch2273 =
        addConfig(
            "Switch2273",
            ConfigItem.configTypeBool,
            false
        )
    val switch2274 =
        addConfig(
            "Switch2274",
            ConfigItem.configTypeBool,
            false
        )
    val switch2275 =
        addConfig(
            "Switch2275",
            ConfigItem.configTypeBool,
            false
        )
    val switch2276 =
        addConfig(
            "Switch2276",
            ConfigItem.configTypeBool,
            false
        )
    val switch2277 =
        addConfig(
            "Switch2277",
            ConfigItem.configTypeBool,
            false
        )
    val switch2278 =
        addConfig(
            "Switch2278",
            ConfigItem.configTypeBool,
            false
        )
    val switch2279 =
        addConfig(
            "Switch2279",
            ConfigItem.configTypeBool,
            false
        )
    val switch2280 =
        addConfig(
            "Switch2280",
            ConfigItem.configTypeBool,
            false
        )
    val switch2281 =
        addConfig(
            "Switch2281",
            ConfigItem.configTypeBool,
            false
        )
    val switch2282 =
        addConfig(
            "Switch2282",
            ConfigItem.configTypeBool,
            false
        )
    val switch2283 =
        addConfig(
            "Switch2283",
            ConfigItem.configTypeBool,
            false
        )
    val switch2284 =
        addConfig(
            "Switch2284",
            ConfigItem.configTypeBool,
            false
        )
    val switch2285 =
        addConfig(
            "Switch2285",
            ConfigItem.configTypeBool,
            false
        )
    val switch2286 =
        addConfig(
            "Switch2286",
            ConfigItem.configTypeBool,
            false
        )
    val switch2287 =
        addConfig(
            "Switch2287",
            ConfigItem.configTypeBool,
            false
        )
    val switch2288 =
        addConfig(
            "Switch2288",
            ConfigItem.configTypeBool,
            false
        )
    val switch2289 =
        addConfig(
            "Switch2289",
            ConfigItem.configTypeBool,
            false
        )
    val switch2290 =
        addConfig(
            "Switch2290",
            ConfigItem.configTypeBool,
            false
        )
    val switch2291 =
        addConfig(
            "Switch2291",
            ConfigItem.configTypeBool,
            false
        )
    val switch2292 =
        addConfig(
            "Switch2292",
            ConfigItem.configTypeBool,
            false
        )
    val switch2293 =
        addConfig(
            "Switch2293",
            ConfigItem.configTypeBool,
            false
        )
    val switch2294 =
        addConfig(
            "Switch2294",
            ConfigItem.configTypeBool,
            false
        )
    val switch2295 =
        addConfig(
            "Switch2295",
            ConfigItem.configTypeBool,
            false
        )
    val switch2296 =
        addConfig(
            "Switch2296",
            ConfigItem.configTypeBool,
            false
        )
    val switch2297 =
        addConfig(
            "Switch2297",
            ConfigItem.configTypeBool,
            false
        )
    val switch2298 =
        addConfig(
            "Switch2298",
            ConfigItem.configTypeBool,
            false
        )
    val switch2299 =
        addConfig(
            "Switch2299",
            ConfigItem.configTypeBool,
            false
        )
    val switch2300 =
        addConfig(
            "Switch2300",
            ConfigItem.configTypeBool,
            false
        )
    val switch2301 =
        addConfig(
            "Switch2301",
            ConfigItem.configTypeBool,
            false
        )
    val switch2302 =
        addConfig(
            "Switch2302",
            ConfigItem.configTypeBool,
            false
        )
    val switch2303 =
        addConfig(
            "Switch2303",
            ConfigItem.configTypeBool,
            false
        )
    val switch2304 =
        addConfig(
            "Switch2304",
            ConfigItem.configTypeBool,
            false
        )
    val switch2305 =
        addConfig(
            "Switch2305",
            ConfigItem.configTypeBool,
            false
        )
    val switch2306 =
        addConfig(
            "Switch2306",
            ConfigItem.configTypeBool,
            false
        )
    val switch2307 =
        addConfig(
            "Switch2307",
            ConfigItem.configTypeBool,
            false
        )
    val switch2308 =
        addConfig(
            "Switch2308",
            ConfigItem.configTypeBool,
            false
        )
    val switch2309 =
        addConfig(
            "Switch2309",
            ConfigItem.configTypeBool,
            false
        )
    val switch2310 =
        addConfig(
            "Switch2310",
            ConfigItem.configTypeBool,
            false
        )
    val switch2311 =
        addConfig(
            "Switch2311",
            ConfigItem.configTypeBool,
            false
        )
    val switch2312 =
        addConfig(
            "Switch2312",
            ConfigItem.configTypeBool,
            false
        )
    val switch2313 =
        addConfig(
            "Switch2313",
            ConfigItem.configTypeBool,
            false
        )
    val switch2314 =
        addConfig(
            "Switch2314",
            ConfigItem.configTypeBool,
            false
        )
    val switch2315 =
        addConfig(
            "Switch2315",
            ConfigItem.configTypeBool,
            false
        )
    val switch2316 =
        addConfig(
            "Switch2316",
            ConfigItem.configTypeBool,
            false
        )
    val switch2317 =
        addConfig(
            "Switch2317",
            ConfigItem.configTypeBool,
            false
        )
    val switch2318 =
        addConfig(
            "Switch2318",
            ConfigItem.configTypeBool,
            false
        )
    val switch2319 =
        addConfig(
            "Switch2319",
            ConfigItem.configTypeBool,
            false
        )
    val switch2320 =
        addConfig(
            "Switch2320",
            ConfigItem.configTypeBool,
            false
        )
    val switch2321 =
        addConfig(
            "Switch2321",
            ConfigItem.configTypeBool,
            false
        )
    val switch2322 =
        addConfig(
            "Switch2322",
            ConfigItem.configTypeBool,
            false
        )
    val switch2323 =
        addConfig(
            "Switch2323",
            ConfigItem.configTypeBool,
            false
        )
    val switch2324 =
        addConfig(
            "Switch2324",
            ConfigItem.configTypeBool,
            false
        )
    val switch2325 =
        addConfig(
            "Switch2325",
            ConfigItem.configTypeBool,
            false
        )
    val switch2326 =
        addConfig(
            "Switch2326",
            ConfigItem.configTypeBool,
            false
        )
    val switch2327 =
        addConfig(
            "Switch2327",
            ConfigItem.configTypeBool,
            false
        )
    val switch2328 =
        addConfig(
            "Switch2328",
            ConfigItem.configTypeBool,
            false
        )
    val switch2329 =
        addConfig(
            "Switch2329",
            ConfigItem.configTypeBool,
            false
        )
    val switch2330 =
        addConfig(
            "Switch2330",
            ConfigItem.configTypeBool,
            false
        )
    val switch2331 =
        addConfig(
            "Switch2331",
            ConfigItem.configTypeBool,
            false
        )
    val switch2332 =
        addConfig(
            "Switch2332",
            ConfigItem.configTypeBool,
            false
        )
    val switch2333 =
        addConfig(
            "Switch2333",
            ConfigItem.configTypeBool,
            false
        )
    val switch2334 =
        addConfig(
            "Switch2334",
            ConfigItem.configTypeBool,
            false
        )
    val switch2335 =
        addConfig(
            "Switch2335",
            ConfigItem.configTypeBool,
            false
        )
    val switch2336 =
        addConfig(
            "Switch2336",
            ConfigItem.configTypeBool,
            false
        )
    val switch2337 =
        addConfig(
            "Switch2337",
            ConfigItem.configTypeBool,
            false
        )
    val switch2338 =
        addConfig(
            "Switch2338",
            ConfigItem.configTypeBool,
            false
        )
    val switch2339 =
        addConfig(
            "Switch2339",
            ConfigItem.configTypeBool,
            false
        )
    val switch2340 =
        addConfig(
            "Switch2340",
            ConfigItem.configTypeBool,
            false
        )
    val switch2341 =
        addConfig(
            "Switch2341",
            ConfigItem.configTypeBool,
            false
        )
    val switch2342 =
        addConfig(
            "Switch2342",
            ConfigItem.configTypeBool,
            false
        )
    val switch2343 =
        addConfig(
            "Switch2343",
            ConfigItem.configTypeBool,
            false
        )
    val switch2344 =
        addConfig(
            "Switch2344",
            ConfigItem.configTypeBool,
            false
        )
    val switch2345 =
        addConfig(
            "Switch2345",
            ConfigItem.configTypeBool,
            false
        )
    val switch2346 =
        addConfig(
            "Switch2346",
            ConfigItem.configTypeBool,
            false
        )
    val switch2347 =
        addConfig(
            "Switch2347",
            ConfigItem.configTypeBool,
            false
        )
    val switch2348 =
        addConfig(
            "Switch2348",
            ConfigItem.configTypeBool,
            false
        )
    val switch2349 =
        addConfig(
            "Switch2349",
            ConfigItem.configTypeBool,
            false
        )
    val switch2350 =
        addConfig(
            "Switch2350",
            ConfigItem.configTypeBool,
            false
        )
    val switch2351 =
        addConfig(
            "Switch2351",
            ConfigItem.configTypeBool,
            false
        )
    val switch2352 =
        addConfig(
            "Switch2352",
            ConfigItem.configTypeBool,
            false
        )
    val switch2353 =
        addConfig(
            "Switch2353",
            ConfigItem.configTypeBool,
            false
        )
    val switch2354 =
        addConfig(
            "Switch2354",
            ConfigItem.configTypeBool,
            false
        )
    val switch2355 =
        addConfig(
            "Switch2355",
            ConfigItem.configTypeBool,
            false
        )
    val switch2356 =
        addConfig(
            "Switch2356",
            ConfigItem.configTypeBool,
            false
        )
    val switch2357 =
        addConfig(
            "Switch2357",
            ConfigItem.configTypeBool,
            false
        )
    val switch2358 =
        addConfig(
            "Switch2358",
            ConfigItem.configTypeBool,
            false
        )
    val switch2359 =
        addConfig(
            "Switch2359",
            ConfigItem.configTypeBool,
            false
        )
    val switch2360 =
        addConfig(
            "Switch2360",
            ConfigItem.configTypeBool,
            false
        )
    val switch2361 =
        addConfig(
            "Switch2361",
            ConfigItem.configTypeBool,
            false
        )
    val switch2362 =
        addConfig(
            "Switch2362",
            ConfigItem.configTypeBool,
            false
        )
    val switch2363 =
        addConfig(
            "Switch2363",
            ConfigItem.configTypeBool,
            false
        )
    val switch2364 =
        addConfig(
            "Switch2364",
            ConfigItem.configTypeBool,
            false
        )
    val switch2365 =
        addConfig(
            "Switch2365",
            ConfigItem.configTypeBool,
            false
        )
    val switch2366 =
        addConfig(
            "Switch2366",
            ConfigItem.configTypeBool,
            false
        )
    val switch2367 =
        addConfig(
            "Switch2367",
            ConfigItem.configTypeBool,
            false
        )
    val switch2368 =
        addConfig(
            "Switch2368",
            ConfigItem.configTypeBool,
            false
        )
    val switch2369 =
        addConfig(
            "Switch2369",
            ConfigItem.configTypeBool,
            false
        )
    val switch2370 =
        addConfig(
            "Switch2370",
            ConfigItem.configTypeBool,
            false
        )
    val switch2371 =
        addConfig(
            "Switch2371",
            ConfigItem.configTypeBool,
            false
        )
    val switch2372 =
        addConfig(
            "Switch2372",
            ConfigItem.configTypeBool,
            false
        )
    val switch2373 =
        addConfig(
            "Switch2373",
            ConfigItem.configTypeBool,
            false
        )
    val switch2374 =
        addConfig(
            "Switch2374",
            ConfigItem.configTypeBool,
            false
        )
    val switch2375 =
        addConfig(
            "Switch2375",
            ConfigItem.configTypeBool,
            false
        )
    val switch2376 =
        addConfig(
            "Switch2376",
            ConfigItem.configTypeBool,
            false
        )
    val switch2377 =
        addConfig(
            "Switch2377",
            ConfigItem.configTypeBool,
            false
        )
    val switch2378 =
        addConfig(
            "Switch2378",
            ConfigItem.configTypeBool,
            false
        )
    val switch2379 =
        addConfig(
            "Switch2379",
            ConfigItem.configTypeBool,
            false
        )
    val switch2380 =
        addConfig(
            "Switch2380",
            ConfigItem.configTypeBool,
            false
        )
    val switch2381 =
        addConfig(
            "Switch2381",
            ConfigItem.configTypeBool,
            false
        )
    val switch2382 =
        addConfig(
            "Switch2382",
            ConfigItem.configTypeBool,
            false
        )
    val switch2383 =
        addConfig(
            "Switch2383",
            ConfigItem.configTypeBool,
            false
        )
    val switch2384 =
        addConfig(
            "Switch2384",
            ConfigItem.configTypeBool,
            false
        )
    val switch2385 =
        addConfig(
            "Switch2385",
            ConfigItem.configTypeBool,
            false
        )
    val switch2386 =
        addConfig(
            "Switch2386",
            ConfigItem.configTypeBool,
            false
        )
    val switch2387 =
        addConfig(
            "Switch2387",
            ConfigItem.configTypeBool,
            false
        )
    val switch2388 =
        addConfig(
            "Switch2388",
            ConfigItem.configTypeBool,
            false
        )
    val switch2389 =
        addConfig(
            "Switch2389",
            ConfigItem.configTypeBool,
            false
        )
    val switch2390 =
        addConfig(
            "Switch2390",
            ConfigItem.configTypeBool,
            false
        )
    val switch2391 =
        addConfig(
            "Switch2391",
            ConfigItem.configTypeBool,
            false
        )
    val switch2392 =
        addConfig(
            "Switch2392",
            ConfigItem.configTypeBool,
            false
        )
    val switch2393 =
        addConfig(
            "Switch2393",
            ConfigItem.configTypeBool,
            false
        )
    val switch2394 =
        addConfig(
            "Switch2394",
            ConfigItem.configTypeBool,
            false
        )
    val switch2395 =
        addConfig(
            "Switch2395",
            ConfigItem.configTypeBool,
            false
        )
    val switch2396 =
        addConfig(
            "Switch2396",
            ConfigItem.configTypeBool,
            false
        )
    val switch2397 =
        addConfig(
            "Switch2397",
            ConfigItem.configTypeBool,
            false
        )
    val switch2398 =
        addConfig(
            "Switch2398",
            ConfigItem.configTypeBool,
            false
        )
    val switch2399 =
        addConfig(
            "Switch2399",
            ConfigItem.configTypeBool,
            false
        )
    val switch2400 =
        addConfig(
            "Switch2400",
            ConfigItem.configTypeBool,
            false
        )
    val switch2401 =
        addConfig(
            "Switch2401",
            ConfigItem.configTypeBool,
            false
        )
    val switch2402 =
        addConfig(
            "Switch2402",
            ConfigItem.configTypeBool,
            false
        )
    val switch2403 =
        addConfig(
            "Switch2403",
            ConfigItem.configTypeBool,
            false
        )
    val switch2404 =
        addConfig(
            "Switch2404",
            ConfigItem.configTypeBool,
            false
        )
    val switch2405 =
        addConfig(
            "Switch2405",
            ConfigItem.configTypeBool,
            false
        )
    val switch2406 =
        addConfig(
            "Switch2406",
            ConfigItem.configTypeBool,
            false
        )
    val switch2407 =
        addConfig(
            "Switch2407",
            ConfigItem.configTypeBool,
            false
        )
    val switch2408 =
        addConfig(
            "Switch2408",
            ConfigItem.configTypeBool,
            false
        )
    val switch2409 =
        addConfig(
            "Switch2409",
            ConfigItem.configTypeBool,
            false
        )
    val switch2410 =
        addConfig(
            "Switch2410",
            ConfigItem.configTypeBool,
            false
        )
    val switch2411 =
        addConfig(
            "Switch2411",
            ConfigItem.configTypeBool,
            false
        )
    val switch2412 =
        addConfig(
            "Switch2412",
            ConfigItem.configTypeBool,
            false
        )
    val switch2413 =
        addConfig(
            "Switch2413",
            ConfigItem.configTypeBool,
            false
        )
    val switch2414 =
        addConfig(
            "Switch2414",
            ConfigItem.configTypeBool,
            false
        )
    val switch2415 =
        addConfig(
            "Switch2415",
            ConfigItem.configTypeBool,
            false
        )
    val switch2416 =
        addConfig(
            "Switch2416",
            ConfigItem.configTypeBool,
            false
        )
    val switch2417 =
        addConfig(
            "Switch2417",
            ConfigItem.configTypeBool,
            false
        )
    val switch2418 =
        addConfig(
            "Switch2418",
            ConfigItem.configTypeBool,
            false
        )
    val switch2419 =
        addConfig(
            "Switch2419",
            ConfigItem.configTypeBool,
            false
        )
    val switch2420 =
        addConfig(
            "Switch2420",
            ConfigItem.configTypeBool,
            false
        )
    val switch2421 =
        addConfig(
            "Switch2421",
            ConfigItem.configTypeBool,
            false
        )
    val switch2422 =
        addConfig(
            "Switch2422",
            ConfigItem.configTypeBool,
            false
        )
    val switch2423 =
        addConfig(
            "Switch2423",
            ConfigItem.configTypeBool,
            false
        )
    val switch2424 =
        addConfig(
            "Switch2424",
            ConfigItem.configTypeBool,
            false
        )
    val switch2425 =
        addConfig(
            "Switch2425",
            ConfigItem.configTypeBool,
            false
        )
    val switch2426 =
        addConfig(
            "Switch2426",
            ConfigItem.configTypeBool,
            false
        )
    val switch2427 =
        addConfig(
            "Switch2427",
            ConfigItem.configTypeBool,
            false
        )
    val switch2428 =
        addConfig(
            "Switch2428",
            ConfigItem.configTypeBool,
            false
        )
    val switch2429 =
        addConfig(
            "Switch2429",
            ConfigItem.configTypeBool,
            false
        )
    val switch2430 =
        addConfig(
            "Switch2430",
            ConfigItem.configTypeBool,
            false
        )
    val switch2431 =
        addConfig(
            "Switch2431",
            ConfigItem.configTypeBool,
            false
        )
    val switch2432 =
        addConfig(
            "Switch2432",
            ConfigItem.configTypeBool,
            false
        )
    val switch2433 =
        addConfig(
            "Switch2433",
            ConfigItem.configTypeBool,
            false
        )
    val switch2434 =
        addConfig(
            "Switch2434",
            ConfigItem.configTypeBool,
            false
        )
    val switch2435 =
        addConfig(
            "Switch2435",
            ConfigItem.configTypeBool,
            false
        )
    val switch2436 =
        addConfig(
            "Switch2436",
            ConfigItem.configTypeBool,
            false
        )
    val switch2437 =
        addConfig(
            "Switch2437",
            ConfigItem.configTypeBool,
            false
        )
    val switch2438 =
        addConfig(
            "Switch2438",
            ConfigItem.configTypeBool,
            false
        )
    val switch2439 =
        addConfig(
            "Switch2439",
            ConfigItem.configTypeBool,
            false
        )
    val switch2440 =
        addConfig(
            "Switch2440",
            ConfigItem.configTypeBool,
            false
        )
    val switch2441 =
        addConfig(
            "Switch2441",
            ConfigItem.configTypeBool,
            false
        )
    val switch2442 =
        addConfig(
            "Switch2442",
            ConfigItem.configTypeBool,
            false
        )
    val switch2443 =
        addConfig(
            "Switch2443",
            ConfigItem.configTypeBool,
            false
        )
    val switch2444 =
        addConfig(
            "Switch2444",
            ConfigItem.configTypeBool,
            false
        )
    val switch2445 =
        addConfig(
            "Switch2445",
            ConfigItem.configTypeBool,
            false
        )
    val switch2446 =
        addConfig(
            "Switch2446",
            ConfigItem.configTypeBool,
            false
        )
    val switch2447 =
        addConfig(
            "Switch2447",
            ConfigItem.configTypeBool,
            false
        )
    val switch2448 =
        addConfig(
            "Switch2448",
            ConfigItem.configTypeBool,
            false
        )
    val switch2449 =
        addConfig(
            "Switch2449",
            ConfigItem.configTypeBool,
            false
        )
    val switch2450 =
        addConfig(
            "Switch2450",
            ConfigItem.configTypeBool,
            false
        )
    val switch2451 =
        addConfig(
            "Switch2451",
            ConfigItem.configTypeBool,
            false
        )
    val switch2452 =
        addConfig(
            "Switch2452",
            ConfigItem.configTypeBool,
            false
        )
    val switch2453 =
        addConfig(
            "Switch2453",
            ConfigItem.configTypeBool,
            false
        )
    val switch2454 =
        addConfig(
            "Switch2454",
            ConfigItem.configTypeBool,
            false
        )
    val switch2455 =
        addConfig(
            "Switch2455",
            ConfigItem.configTypeBool,
            false
        )
    val switch2456 =
        addConfig(
            "Switch2456",
            ConfigItem.configTypeBool,
            false
        )
    val switch2457 =
        addConfig(
            "Switch2457",
            ConfigItem.configTypeBool,
            false
        )
    val switch2458 =
        addConfig(
            "Switch2458",
            ConfigItem.configTypeBool,
            false
        )
    val switch2459 =
        addConfig(
            "Switch2459",
            ConfigItem.configTypeBool,
            false
        )
    val switch2460 =
        addConfig(
            "Switch2460",
            ConfigItem.configTypeBool,
            false
        )
    val switch2461 =
        addConfig(
            "Switch2461",
            ConfigItem.configTypeBool,
            false
        )
    val switch2462 =
        addConfig(
            "Switch2462",
            ConfigItem.configTypeBool,
            false
        )
    val switch2463 =
        addConfig(
            "Switch2463",
            ConfigItem.configTypeBool,
            false
        )
    val switch2464 =
        addConfig(
            "Switch2464",
            ConfigItem.configTypeBool,
            false
        )
    val switch2465 =
        addConfig(
            "Switch2465",
            ConfigItem.configTypeBool,
            false
        )
    val switch2466 =
        addConfig(
            "Switch2466",
            ConfigItem.configTypeBool,
            false
        )
    val switch2467 =
        addConfig(
            "Switch2467",
            ConfigItem.configTypeBool,
            false
        )
    val switch2468 =
        addConfig(
            "Switch2468",
            ConfigItem.configTypeBool,
            false
        )
    val switch2469 =
        addConfig(
            "Switch2469",
            ConfigItem.configTypeBool,
            false
        )
    val switch2470 =
        addConfig(
            "Switch2470",
            ConfigItem.configTypeBool,
            false
        )
    val switch2471 =
        addConfig(
            "Switch2471",
            ConfigItem.configTypeBool,
            false
        )
    val switch2472 =
        addConfig(
            "Switch2472",
            ConfigItem.configTypeBool,
            false
        )
    val switch2473 =
        addConfig(
            "Switch2473",
            ConfigItem.configTypeBool,
            false
        )
    val switch2474 =
        addConfig(
            "Switch2474",
            ConfigItem.configTypeBool,
            false
        )
    val switch2475 =
        addConfig(
            "Switch2475",
            ConfigItem.configTypeBool,
            false
        )
    val switch2476 =
        addConfig(
            "Switch2476",
            ConfigItem.configTypeBool,
            false
        )
    val switch2477 =
        addConfig(
            "Switch2477",
            ConfigItem.configTypeBool,
            false
        )
    val switch2478 =
        addConfig(
            "Switch2478",
            ConfigItem.configTypeBool,
            false
        )
    val switch2479 =
        addConfig(
            "Switch2479",
            ConfigItem.configTypeBool,
            false
        )
    val switch2480 =
        addConfig(
            "Switch2480",
            ConfigItem.configTypeBool,
            false
        )
    val switch2481 =
        addConfig(
            "Switch2481",
            ConfigItem.configTypeBool,
            false
        )
    val switch2482 =
        addConfig(
            "Switch2482",
            ConfigItem.configTypeBool,
            false
        )
    val switch2483 =
        addConfig(
            "Switch2483",
            ConfigItem.configTypeBool,
            false
        )
    val switch2484 =
        addConfig(
            "Switch2484",
            ConfigItem.configTypeBool,
            false
        )
    val switch2485 =
        addConfig(
            "Switch2485",
            ConfigItem.configTypeBool,
            false
        )
    val switch2486 =
        addConfig(
            "Switch2486",
            ConfigItem.configTypeBool,
            false
        )
    val switch2487 =
        addConfig(
            "Switch2487",
            ConfigItem.configTypeBool,
            false
        )
    val switch2488 =
        addConfig(
            "Switch2488",
            ConfigItem.configTypeBool,
            false
        )
    val switch2489 =
        addConfig(
            "Switch2489",
            ConfigItem.configTypeBool,
            false
        )
    val switch2490 =
        addConfig(
            "Switch2490",
            ConfigItem.configTypeBool,
            false
        )
    val switch2491 =
        addConfig(
            "Switch2491",
            ConfigItem.configTypeBool,
            false
        )
    val switch2492 =
        addConfig(
            "Switch2492",
            ConfigItem.configTypeBool,
            false
        )
    val switch2493 =
        addConfig(
            "Switch2493",
            ConfigItem.configTypeBool,
            false
        )
    val switch2494 =
        addConfig(
            "Switch2494",
            ConfigItem.configTypeBool,
            false
        )
    val switch2495 =
        addConfig(
            "Switch2495",
            ConfigItem.configTypeBool,
            false
        )
    val switch2496 =
        addConfig(
            "Switch2496",
            ConfigItem.configTypeBool,
            false
        )
    val switch2497 =
        addConfig(
            "Switch2497",
            ConfigItem.configTypeBool,
            false
        )
    val switch2498 =
        addConfig(
            "Switch2498",
            ConfigItem.configTypeBool,
            false
        )
    val switch2499 =
        addConfig(
            "Switch2499",
            ConfigItem.configTypeBool,
            false
        )
    val switch2500 =
        addConfig(
            "Switch2500",
            ConfigItem.configTypeBool,
            false
        )
    val switch2501 =
        addConfig(
            "Switch2501",
            ConfigItem.configTypeBool,
            false
        )
    val switch2502 =
        addConfig(
            "Switch2502",
            ConfigItem.configTypeBool,
            false
        )
    val switch2503 =
        addConfig(
            "Switch2503",
            ConfigItem.configTypeBool,
            false
        )
    val switch2504 =
        addConfig(
            "Switch2504",
            ConfigItem.configTypeBool,
            false
        )
    val switch2505 =
        addConfig(
            "Switch2505",
            ConfigItem.configTypeBool,
            false
        )
    val switch2506 =
        addConfig(
            "Switch2506",
            ConfigItem.configTypeBool,
            false
        )
    val switch2507 =
        addConfig(
            "Switch2507",
            ConfigItem.configTypeBool,
            false
        )
    val switch2508 =
        addConfig(
            "Switch2508",
            ConfigItem.configTypeBool,
            false
        )
    val switch2509 =
        addConfig(
            "Switch2509",
            ConfigItem.configTypeBool,
            false
        )
    val switch2510 =
        addConfig(
            "Switch2510",
            ConfigItem.configTypeBool,
            false
        )
    val switch2511 =
        addConfig(
            "Switch2511",
            ConfigItem.configTypeBool,
            false
        )
    val switch2512 =
        addConfig(
            "Switch2512",
            ConfigItem.configTypeBool,
            false
        )
    val switch2513 =
        addConfig(
            "Switch2513",
            ConfigItem.configTypeBool,
            false
        )
    val switch2514 =
        addConfig(
            "Switch2514",
            ConfigItem.configTypeBool,
            false
        )
    val switch2515 =
        addConfig(
            "Switch2515",
            ConfigItem.configTypeBool,
            false
        )
    val switch2516 =
        addConfig(
            "Switch2516",
            ConfigItem.configTypeBool,
            false
        )
    val switch2517 =
        addConfig(
            "Switch2517",
            ConfigItem.configTypeBool,
            false
        )
    val switch2518 =
        addConfig(
            "Switch2518",
            ConfigItem.configTypeBool,
            false
        )
    val switch2519 =
        addConfig(
            "Switch2519",
            ConfigItem.configTypeBool,
            false
        )
    val switch2520 =
        addConfig(
            "Switch2520",
            ConfigItem.configTypeBool,
            false
        )
    val switch2521 =
        addConfig(
            "Switch2521",
            ConfigItem.configTypeBool,
            false
        )
    val switch2522 =
        addConfig(
            "Switch2522",
            ConfigItem.configTypeBool,
            false
        )
    val switch2523 =
        addConfig(
            "Switch2523",
            ConfigItem.configTypeBool,
            false
        )
    val switch2524 =
        addConfig(
            "Switch2524",
            ConfigItem.configTypeBool,
            false
        )
    val switch2525 =
        addConfig(
            "Switch2525",
            ConfigItem.configTypeBool,
            false
        )
    val switch2526 =
        addConfig(
            "Switch2526",
            ConfigItem.configTypeBool,
            false
        )
    val switch2527 =
        addConfig(
            "Switch2527",
            ConfigItem.configTypeBool,
            false
        )
    val switch2528 =
        addConfig(
            "Switch2528",
            ConfigItem.configTypeBool,
            false
        )
    val switch2529 =
        addConfig(
            "Switch2529",
            ConfigItem.configTypeBool,
            false
        )
    val switch2530 =
        addConfig(
            "Switch2530",
            ConfigItem.configTypeBool,
            false
        )
    val switch2531 =
        addConfig(
            "Switch2531",
            ConfigItem.configTypeBool,
            false
        )
    val switch2532 =
        addConfig(
            "Switch2532",
            ConfigItem.configTypeBool,
            false
        )
    val switch2533 =
        addConfig(
            "Switch2533",
            ConfigItem.configTypeBool,
            false
        )
    val switch2534 =
        addConfig(
            "Switch2534",
            ConfigItem.configTypeBool,
            false
        )
    val switch2535 =
        addConfig(
            "Switch2535",
            ConfigItem.configTypeBool,
            false
        )
    val switch2536 =
        addConfig(
            "Switch2536",
            ConfigItem.configTypeBool,
            false
        )
    val switch2537 =
        addConfig(
            "Switch2537",
            ConfigItem.configTypeBool,
            false
        )
    val switch2538 =
        addConfig(
            "Switch2538",
            ConfigItem.configTypeBool,
            false
        )
    val switch2539 =
        addConfig(
            "Switch2539",
            ConfigItem.configTypeBool,
            false
        )
    val switch2540 =
        addConfig(
            "Switch2540",
            ConfigItem.configTypeBool,
            false
        )
    val switch2541 =
        addConfig(
            "Switch2541",
            ConfigItem.configTypeBool,
            false
        )
    val switch2542 =
        addConfig(
            "Switch2542",
            ConfigItem.configTypeBool,
            false
        )
    val switch2543 =
        addConfig(
            "Switch2543",
            ConfigItem.configTypeBool,
            false
        )
    val switch2544 =
        addConfig(
            "Switch2544",
            ConfigItem.configTypeBool,
            false
        )
    val switch2545 =
        addConfig(
            "Switch2545",
            ConfigItem.configTypeBool,
            false
        )
    val switch2546 =
        addConfig(
            "Switch2546",
            ConfigItem.configTypeBool,
            false
        )
    val switch2547 =
        addConfig(
            "Switch2547",
            ConfigItem.configTypeBool,
            false
        )
    val switch2548 =
        addConfig(
            "Switch2548",
            ConfigItem.configTypeBool,
            false
        )
    val switch2549 =
        addConfig(
            "Switch2549",
            ConfigItem.configTypeBool,
            false
        )
    val switch2550 =
        addConfig(
            "Switch2550",
            ConfigItem.configTypeBool,
            false
        )
    val switch2551 =
        addConfig(
            "Switch2551",
            ConfigItem.configTypeBool,
            false
        )
    val switch2552 =
        addConfig(
            "Switch2552",
            ConfigItem.configTypeBool,
            false
        )
    val switch2553 =
        addConfig(
            "Switch2553",
            ConfigItem.configTypeBool,
            false
        )
    val switch2554 =
        addConfig(
            "Switch2554",
            ConfigItem.configTypeBool,
            false
        )
    val switch2555 =
        addConfig(
            "Switch2555",
            ConfigItem.configTypeBool,
            false
        )
    val switch2556 =
        addConfig(
            "Switch2556",
            ConfigItem.configTypeBool,
            false
        )
    val switch2557 =
        addConfig(
            "Switch2557",
            ConfigItem.configTypeBool,
            false
        )
    val switch2558 =
        addConfig(
            "Switch2558",
            ConfigItem.configTypeBool,
            false
        )
    val switch2559 =
        addConfig(
            "Switch2559",
            ConfigItem.configTypeBool,
            false
        )
    val switch2560 =
        addConfig(
            "Switch2560",
            ConfigItem.configTypeBool,
            false
        )
    val switch2561 =
        addConfig(
            "Switch2561",
            ConfigItem.configTypeBool,
            false
        )
    val switch2562 =
        addConfig(
            "Switch2562",
            ConfigItem.configTypeBool,
            false
        )
    val switch2563 =
        addConfig(
            "Switch2563",
            ConfigItem.configTypeBool,
            false
        )
    val switch2564 =
        addConfig(
            "Switch2564",
            ConfigItem.configTypeBool,
            false
        )
    val switch2565 =
        addConfig(
            "Switch2565",
            ConfigItem.configTypeBool,
            false
        )
    val switch2566 =
        addConfig(
            "Switch2566",
            ConfigItem.configTypeBool,
            false
        )
    val switch2567 =
        addConfig(
            "Switch2567",
            ConfigItem.configTypeBool,
            false
        )
    val switch2568 =
        addConfig(
            "Switch2568",
            ConfigItem.configTypeBool,
            false
        )
    val switch2569 =
        addConfig(
            "Switch2569",
            ConfigItem.configTypeBool,
            false
        )
    val switch2570 =
        addConfig(
            "Switch2570",
            ConfigItem.configTypeBool,
            false
        )
    val switch2571 =
        addConfig(
            "Switch2571",
            ConfigItem.configTypeBool,
            false
        )
    val switch2572 =
        addConfig(
            "Switch2572",
            ConfigItem.configTypeBool,
            false
        )
    val switch2573 =
        addConfig(
            "Switch2573",
            ConfigItem.configTypeBool,
            false
        )
    val switch2574 =
        addConfig(
            "Switch2574",
            ConfigItem.configTypeBool,
            false
        )
    val switch2575 =
        addConfig(
            "Switch2575",
            ConfigItem.configTypeBool,
            false
        )
    val switch2576 =
        addConfig(
            "Switch2576",
            ConfigItem.configTypeBool,
            false
        )
    val switch2577 =
        addConfig(
            "Switch2577",
            ConfigItem.configTypeBool,
            false
        )
    val switch2578 =
        addConfig(
            "Switch2578",
            ConfigItem.configTypeBool,
            false
        )
    val switch2579 =
        addConfig(
            "Switch2579",
            ConfigItem.configTypeBool,
            false
        )
    val switch2580 =
        addConfig(
            "Switch2580",
            ConfigItem.configTypeBool,
            false
        )
    val switch2581 =
        addConfig(
            "Switch2581",
            ConfigItem.configTypeBool,
            false
        )
    val switch2582 =
        addConfig(
            "Switch2582",
            ConfigItem.configTypeBool,
            false
        )
    val switch2583 =
        addConfig(
            "Switch2583",
            ConfigItem.configTypeBool,
            false
        )
    val switch2584 =
        addConfig(
            "Switch2584",
            ConfigItem.configTypeBool,
            false
        )
    val switch2585 =
        addConfig(
            "Switch2585",
            ConfigItem.configTypeBool,
            false
        )
    val switch2586 =
        addConfig(
            "Switch2586",
            ConfigItem.configTypeBool,
            false
        )
    val switch2587 =
        addConfig(
            "Switch2587",
            ConfigItem.configTypeBool,
            false
        )
    val switch2588 =
        addConfig(
            "Switch2588",
            ConfigItem.configTypeBool,
            false
        )
    val switch2589 =
        addConfig(
            "Switch2589",
            ConfigItem.configTypeBool,
            false
        )
    val switch2590 =
        addConfig(
            "Switch2590",
            ConfigItem.configTypeBool,
            false
        )
    val switch2591 =
        addConfig(
            "Switch2591",
            ConfigItem.configTypeBool,
            false
        )
    val switch2592 =
        addConfig(
            "Switch2592",
            ConfigItem.configTypeBool,
            false
        )
    val switch2593 =
        addConfig(
            "Switch2593",
            ConfigItem.configTypeBool,
            false
        )
    val switch2594 =
        addConfig(
            "Switch2594",
            ConfigItem.configTypeBool,
            false
        )
    val switch2595 =
        addConfig(
            "Switch2595",
            ConfigItem.configTypeBool,
            false
        )
    val switch2596 =
        addConfig(
            "Switch2596",
            ConfigItem.configTypeBool,
            false
        )
    val switch2597 =
        addConfig(
            "Switch2597",
            ConfigItem.configTypeBool,
            false
        )
    val switch2598 =
        addConfig(
            "Switch2598",
            ConfigItem.configTypeBool,
            false
        )
    val switch2599 =
        addConfig(
            "Switch2599",
            ConfigItem.configTypeBool,
            false
        )
    val switch2600 =
        addConfig(
            "Switch2600",
            ConfigItem.configTypeBool,
            false
        )
    val switch2601 =
        addConfig(
            "Switch2601",
            ConfigItem.configTypeBool,
            false
        )
    val switch2602 =
        addConfig(
            "Switch2602",
            ConfigItem.configTypeBool,
            false
        )
    val switch2603 =
        addConfig(
            "Switch2603",
            ConfigItem.configTypeBool,
            false
        )
    val switch2604 =
        addConfig(
            "Switch2604",
            ConfigItem.configTypeBool,
            false
        )
    val switch2605 =
        addConfig(
            "Switch2605",
            ConfigItem.configTypeBool,
            false
        )
    val switch2606 =
        addConfig(
            "Switch2606",
            ConfigItem.configTypeBool,
            false
        )
    val switch2607 =
        addConfig(
            "Switch2607",
            ConfigItem.configTypeBool,
            false
        )
    val switch2608 =
        addConfig(
            "Switch2608",
            ConfigItem.configTypeBool,
            false
        )
    val switch2609 =
        addConfig(
            "Switch2609",
            ConfigItem.configTypeBool,
            false
        )
    val switch2610 =
        addConfig(
            "Switch2610",
            ConfigItem.configTypeBool,
            false
        )
    val switch2611 =
        addConfig(
            "Switch2611",
            ConfigItem.configTypeBool,
            false
        )
    val switch2612 =
        addConfig(
            "Switch2612",
            ConfigItem.configTypeBool,
            false
        )
    val switch2613 =
        addConfig(
            "Switch2613",
            ConfigItem.configTypeBool,
            false
        )
    val switch2614 =
        addConfig(
            "Switch2614",
            ConfigItem.configTypeBool,
            false
        )
    val switch2615 =
        addConfig(
            "Switch2615",
            ConfigItem.configTypeBool,
            false
        )
    val switch2616 =
        addConfig(
            "Switch2616",
            ConfigItem.configTypeBool,
            false
        )
    val switch2617 =
        addConfig(
            "Switch2617",
            ConfigItem.configTypeBool,
            false
        )
    val switch2618 =
        addConfig(
            "Switch2618",
            ConfigItem.configTypeBool,
            false
        )
    val switch2619 =
        addConfig(
            "Switch2619",
            ConfigItem.configTypeBool,
            false
        )
    val switch2620 =
        addConfig(
            "Switch2620",
            ConfigItem.configTypeBool,
            false
        )
    val switch2621 =
        addConfig(
            "Switch2621",
            ConfigItem.configTypeBool,
            false
        )
    val switch2622 =
        addConfig(
            "Switch2622",
            ConfigItem.configTypeBool,
            false
        )
    val switch2623 =
        addConfig(
            "Switch2623",
            ConfigItem.configTypeBool,
            false
        )
    val switch2624 =
        addConfig(
            "Switch2624",
            ConfigItem.configTypeBool,
            false
        )
    val switch2625 =
        addConfig(
            "Switch2625",
            ConfigItem.configTypeBool,
            false
        )
    val switch2626 =
        addConfig(
            "Switch2626",
            ConfigItem.configTypeBool,
            false
        )
    val switch2627 =
        addConfig(
            "Switch2627",
            ConfigItem.configTypeBool,
            false
        )
    val switch2628 =
        addConfig(
            "Switch2628",
            ConfigItem.configTypeBool,
            false
        )
    val switch2629 =
        addConfig(
            "Switch2629",
            ConfigItem.configTypeBool,
            false
        )
    val switch2630 =
        addConfig(
            "Switch2630",
            ConfigItem.configTypeBool,
            false
        )
    val switch2631 =
        addConfig(
            "Switch2631",
            ConfigItem.configTypeBool,
            false
        )
    val switch2632 =
        addConfig(
            "Switch2632",
            ConfigItem.configTypeBool,
            false
        )
    val switch2633 =
        addConfig(
            "Switch2633",
            ConfigItem.configTypeBool,
            false
        )
    val switch2634 =
        addConfig(
            "Switch2634",
            ConfigItem.configTypeBool,
            false
        )
    val switch2635 =
        addConfig(
            "Switch2635",
            ConfigItem.configTypeBool,
            false
        )
    val switch2636 =
        addConfig(
            "Switch2636",
            ConfigItem.configTypeBool,
            false
        )
    val switch2637 =
        addConfig(
            "Switch2637",
            ConfigItem.configTypeBool,
            false
        )
    val switch2638 =
        addConfig(
            "Switch2638",
            ConfigItem.configTypeBool,
            false
        )
    val switch2639 =
        addConfig(
            "Switch2639",
            ConfigItem.configTypeBool,
            false
        )
    val switch2640 =
        addConfig(
            "Switch2640",
            ConfigItem.configTypeBool,
            false
        )
    val switch2641 =
        addConfig(
            "Switch2641",
            ConfigItem.configTypeBool,
            false
        )
    val switch2642 =
        addConfig(
            "Switch2642",
            ConfigItem.configTypeBool,
            false
        )
    val switch2643 =
        addConfig(
            "Switch2643",
            ConfigItem.configTypeBool,
            false
        )
    val switch2644 =
        addConfig(
            "Switch2644",
            ConfigItem.configTypeBool,
            false
        )
    val switch2645 =
        addConfig(
            "Switch2645",
            ConfigItem.configTypeBool,
            false
        )
    val switch2646 =
        addConfig(
            "Switch2646",
            ConfigItem.configTypeBool,
            false
        )
    val switch2647 =
        addConfig(
            "Switch2647",
            ConfigItem.configTypeBool,
            false
        )
    val switch2648 =
        addConfig(
            "Switch2648",
            ConfigItem.configTypeBool,
            false
        )
    val switch2649 =
        addConfig(
            "Switch2649",
            ConfigItem.configTypeBool,
            false
        )
    val switch2650 =
        addConfig(
            "Switch2650",
            ConfigItem.configTypeBool,
            false
        )
    val switch2651 =
        addConfig(
            "Switch2651",
            ConfigItem.configTypeBool,
            false
        )
    val switch2652 =
        addConfig(
            "Switch2652",
            ConfigItem.configTypeBool,
            false
        )
    val switch2653 =
        addConfig(
            "Switch2653",
            ConfigItem.configTypeBool,
            false
        )
    val switch2654 =
        addConfig(
            "Switch2654",
            ConfigItem.configTypeBool,
            false
        )
    val switch2655 =
        addConfig(
            "Switch2655",
            ConfigItem.configTypeBool,
            false
        )
    val switch2656 =
        addConfig(
            "Switch2656",
            ConfigItem.configTypeBool,
            false
        )
    val switch2657 =
        addConfig(
            "Switch2657",
            ConfigItem.configTypeBool,
            false
        )
    val switch2658 =
        addConfig(
            "Switch2658",
            ConfigItem.configTypeBool,
            false
        )
    val switch2659 =
        addConfig(
            "Switch2659",
            ConfigItem.configTypeBool,
            false
        )
    val switch2660 =
        addConfig(
            "Switch2660",
            ConfigItem.configTypeBool,
            false
        )
    val switch2661 =
        addConfig(
            "Switch2661",
            ConfigItem.configTypeBool,
            false
        )
    val switch2662 =
        addConfig(
            "Switch2662",
            ConfigItem.configTypeBool,
            false
        )
    val switch2663 =
        addConfig(
            "Switch2663",
            ConfigItem.configTypeBool,
            false
        )
    val switch2664 =
        addConfig(
            "Switch2664",
            ConfigItem.configTypeBool,
            false
        )
    val switch2665 =
        addConfig(
            "Switch2665",
            ConfigItem.configTypeBool,
            false
        )
    val switch2666 =
        addConfig(
            "Switch2666",
            ConfigItem.configTypeBool,
            false
        )
    val switch2667 =
        addConfig(
            "Switch2667",
            ConfigItem.configTypeBool,
            false
        )
    val switch2668 =
        addConfig(
            "Switch2668",
            ConfigItem.configTypeBool,
            false
        )
    val switch2669 =
        addConfig(
            "Switch2669",
            ConfigItem.configTypeBool,
            false
        )
    val switch2670 =
        addConfig(
            "Switch2670",
            ConfigItem.configTypeBool,
            false
        )
    val switch2671 =
        addConfig(
            "Switch2671",
            ConfigItem.configTypeBool,
            false
        )
    val switch2672 =
        addConfig(
            "Switch2672",
            ConfigItem.configTypeBool,
            false
        )
    val switch2673 =
        addConfig(
            "Switch2673",
            ConfigItem.configTypeBool,
            false
        )
    val switch2674 =
        addConfig(
            "Switch2674",
            ConfigItem.configTypeBool,
            false
        )
    val switch2675 =
        addConfig(
            "Switch2675",
            ConfigItem.configTypeBool,
            false
        )
    val switch2676 =
        addConfig(
            "Switch2676",
            ConfigItem.configTypeBool,
            false
        )
    val switch2677 =
        addConfig(
            "Switch2677",
            ConfigItem.configTypeBool,
            false
        )
    val switch2678 =
        addConfig(
            "Switch2678",
            ConfigItem.configTypeBool,
            false
        )
    val switch2679 =
        addConfig(
            "Switch2679",
            ConfigItem.configTypeBool,
            false
        )
    val switch2680 =
        addConfig(
            "Switch2680",
            ConfigItem.configTypeBool,
            false
        )
    val switch2681 =
        addConfig(
            "Switch2681",
            ConfigItem.configTypeBool,
            false
        )
    val switch2682 =
        addConfig(
            "Switch2682",
            ConfigItem.configTypeBool,
            false
        )
    val switch2683 =
        addConfig(
            "Switch2683",
            ConfigItem.configTypeBool,
            false
        )
    val switch2684 =
        addConfig(
            "Switch2684",
            ConfigItem.configTypeBool,
            false
        )
    val switch2685 =
        addConfig(
            "Switch2685",
            ConfigItem.configTypeBool,
            false
        )
    val switch2686 =
        addConfig(
            "Switch2686",
            ConfigItem.configTypeBool,
            false
        )
    val switch2687 =
        addConfig(
            "Switch2687",
            ConfigItem.configTypeBool,
            false
        )
    val switch2688 =
        addConfig(
            "Switch2688",
            ConfigItem.configTypeBool,
            false
        )
    val switch2689 =
        addConfig(
            "Switch2689",
            ConfigItem.configTypeBool,
            false
        )
    val switch2690 =
        addConfig(
            "Switch2690",
            ConfigItem.configTypeBool,
            false
        )
    val switch2691 =
        addConfig(
            "Switch2691",
            ConfigItem.configTypeBool,
            false
        )
    val switch2692 =
        addConfig(
            "Switch2692",
            ConfigItem.configTypeBool,
            false
        )
    val switch2693 =
        addConfig(
            "Switch2693",
            ConfigItem.configTypeBool,
            false
        )
    val switch2694 =
        addConfig(
            "Switch2694",
            ConfigItem.configTypeBool,
            false
        )
    val switch2695 =
        addConfig(
            "Switch2695",
            ConfigItem.configTypeBool,
            false
        )
    val switch2696 =
        addConfig(
            "Switch2696",
            ConfigItem.configTypeBool,
            false
        )
    val switch2697 =
        addConfig(
            "Switch2697",
            ConfigItem.configTypeBool,
            false
        )
    val switch2698 =
        addConfig(
            "Switch2698",
            ConfigItem.configTypeBool,
            false
        )
    val switch2699 =
        addConfig(
            "Switch2699",
            ConfigItem.configTypeBool,
            false
        )
    val switch2700 =
        addConfig(
            "Switch2700",
            ConfigItem.configTypeBool,
            false
        )
    val switch2701 =
        addConfig(
            "Switch2701",
            ConfigItem.configTypeBool,
            false
        )
    val switch2702 =
        addConfig(
            "Switch2702",
            ConfigItem.configTypeBool,
            false
        )
    val switch2703 =
        addConfig(
            "Switch2703",
            ConfigItem.configTypeBool,
            false
        )
    val switch2704 =
        addConfig(
            "Switch2704",
            ConfigItem.configTypeBool,
            false
        )
    val switch2705 =
        addConfig(
            "Switch2705",
            ConfigItem.configTypeBool,
            false
        )
    val switch2706 =
        addConfig(
            "Switch2706",
            ConfigItem.configTypeBool,
            false
        )
    val switch2707 =
        addConfig(
            "Switch2707",
            ConfigItem.configTypeBool,
            false
        )
    val switch2708 =
        addConfig(
            "Switch2708",
            ConfigItem.configTypeBool,
            false
        )
    val switch2709 =
        addConfig(
            "Switch2709",
            ConfigItem.configTypeBool,
            false
        )
    val switch2710 =
        addConfig(
            "Switch2710",
            ConfigItem.configTypeBool,
            false
        )
    val switch2711 =
        addConfig(
            "Switch2711",
            ConfigItem.configTypeBool,
            false
        )
    val switch2712 =
        addConfig(
            "Switch2712",
            ConfigItem.configTypeBool,
            false
        )
    val switch2713 =
        addConfig(
            "Switch2713",
            ConfigItem.configTypeBool,
            false
        )
    val switch2714 =
        addConfig(
            "Switch2714",
            ConfigItem.configTypeBool,
            false
        )
    val switch2715 =
        addConfig(
            "Switch2715",
            ConfigItem.configTypeBool,
            false
        )
    val switch2716 =
        addConfig(
            "Switch2716",
            ConfigItem.configTypeBool,
            false
        )
    val switch2717 =
        addConfig(
            "Switch2717",
            ConfigItem.configTypeBool,
            false
        )
    val switch2718 =
        addConfig(
            "Switch2718",
            ConfigItem.configTypeBool,
            false
        )
    val switch2719 =
        addConfig(
            "Switch2719",
            ConfigItem.configTypeBool,
            false
        )
    val switch2720 =
        addConfig(
            "Switch2720",
            ConfigItem.configTypeBool,
            false
        )
    val switch2721 =
        addConfig(
            "Switch2721",
            ConfigItem.configTypeBool,
            false
        )
    val switch2722 =
        addConfig(
            "Switch2722",
            ConfigItem.configTypeBool,
            false
        )
    val switch2723 =
        addConfig(
            "Switch2723",
            ConfigItem.configTypeBool,
            false
        )
    val switch2724 =
        addConfig(
            "Switch2724",
            ConfigItem.configTypeBool,
            false
        )
    val switch2725 =
        addConfig(
            "Switch2725",
            ConfigItem.configTypeBool,
            false
        )
    val switch2726 =
        addConfig(
            "Switch2726",
            ConfigItem.configTypeBool,
            false
        )
    val switch2727 =
        addConfig(
            "Switch2727",
            ConfigItem.configTypeBool,
            false
        )
    val switch2728 =
        addConfig(
            "Switch2728",
            ConfigItem.configTypeBool,
            false
        )
    val switch2729 =
        addConfig(
            "Switch2729",
            ConfigItem.configTypeBool,
            false
        )
    val switch2730 =
        addConfig(
            "Switch2730",
            ConfigItem.configTypeBool,
            false
        )
    val switch2731 =
        addConfig(
            "Switch2731",
            ConfigItem.configTypeBool,
            false
        )
    val switch2732 =
        addConfig(
            "Switch2732",
            ConfigItem.configTypeBool,
            false
        )
    val switch2733 =
        addConfig(
            "Switch2733",
            ConfigItem.configTypeBool,
            false
        )
    val switch2734 =
        addConfig(
            "Switch2734",
            ConfigItem.configTypeBool,
            false
        )
    val switch2735 =
        addConfig(
            "Switch2735",
            ConfigItem.configTypeBool,
            false
        )
    val switch2736 =
        addConfig(
            "Switch2736",
            ConfigItem.configTypeBool,
            false
        )
    val switch2737 =
        addConfig(
            "Switch2737",
            ConfigItem.configTypeBool,
            false
        )
    val switch2738 =
        addConfig(
            "Switch2738",
            ConfigItem.configTypeBool,
            false
        )
    val switch2739 =
        addConfig(
            "Switch2739",
            ConfigItem.configTypeBool,
            false
        )
    val switch2740 =
        addConfig(
            "Switch2740",
            ConfigItem.configTypeBool,
            false
        )
    val switch2741 =
        addConfig(
            "Switch2741",
            ConfigItem.configTypeBool,
            false
        )
    val switch2742 =
        addConfig(
            "Switch2742",
            ConfigItem.configTypeBool,
            false
        )
    val switch2743 =
        addConfig(
            "Switch2743",
            ConfigItem.configTypeBool,
            false
        )
    val switch2744 =
        addConfig(
            "Switch2744",
            ConfigItem.configTypeBool,
            false
        )
    val switch2745 =
        addConfig(
            "Switch2745",
            ConfigItem.configTypeBool,
            false
        )
    val switch2746 =
        addConfig(
            "Switch2746",
            ConfigItem.configTypeBool,
            false
        )
    val switch2747 =
        addConfig(
            "Switch2747",
            ConfigItem.configTypeBool,
            false
        )
    val switch2748 =
        addConfig(
            "Switch2748",
            ConfigItem.configTypeBool,
            false
        )
    val switch2749 =
        addConfig(
            "Switch2749",
            ConfigItem.configTypeBool,
            false
        )
    val switch2750 =
        addConfig(
            "Switch2750",
            ConfigItem.configTypeBool,
            false
        )
    val switch2751 =
        addConfig(
            "Switch2751",
            ConfigItem.configTypeBool,
            false
        )
    val switch2752 =
        addConfig(
            "Switch2752",
            ConfigItem.configTypeBool,
            false
        )
    val switch2753 =
        addConfig(
            "Switch2753",
            ConfigItem.configTypeBool,
            false
        )
    val switch2754 =
        addConfig(
            "Switch2754",
            ConfigItem.configTypeBool,
            false
        )
    val switch2755 =
        addConfig(
            "Switch2755",
            ConfigItem.configTypeBool,
            false
        )
    val switch2756 =
        addConfig(
            "Switch2756",
            ConfigItem.configTypeBool,
            false
        )
    val switch2757 =
        addConfig(
            "Switch2757",
            ConfigItem.configTypeBool,
            false
        )
    val switch2758 =
        addConfig(
            "Switch2758",
            ConfigItem.configTypeBool,
            false
        )
    val switch2759 =
        addConfig(
            "Switch2759",
            ConfigItem.configTypeBool,
            false
        )
    val switch2760 =
        addConfig(
            "Switch2760",
            ConfigItem.configTypeBool,
            false
        )
    val switch2761 =
        addConfig(
            "Switch2761",
            ConfigItem.configTypeBool,
            false
        )
    val switch2762 =
        addConfig(
            "Switch2762",
            ConfigItem.configTypeBool,
            false
        )
    val switch2763 =
        addConfig(
            "Switch2763",
            ConfigItem.configTypeBool,
            false
        )
    val switch2764 =
        addConfig(
            "Switch2764",
            ConfigItem.configTypeBool,
            false
        )
    val switch2765 =
        addConfig(
            "Switch2765",
            ConfigItem.configTypeBool,
            false
        )
    val switch2766 =
        addConfig(
            "Switch2766",
            ConfigItem.configTypeBool,
            false
        )
    val switch2767 =
        addConfig(
            "Switch2767",
            ConfigItem.configTypeBool,
            false
        )
    val switch2768 =
        addConfig(
            "Switch2768",
            ConfigItem.configTypeBool,
            false
        )
    val switch2769 =
        addConfig(
            "Switch2769",
            ConfigItem.configTypeBool,
            false
        )
    val switch2770 =
        addConfig(
            "Switch2770",
            ConfigItem.configTypeBool,
            false
        )
    val switch2771 =
        addConfig(
            "Switch2771",
            ConfigItem.configTypeBool,
            false
        )
    val switch2772 =
        addConfig(
            "Switch2772",
            ConfigItem.configTypeBool,
            false
        )
    val switch2773 =
        addConfig(
            "Switch2773",
            ConfigItem.configTypeBool,
            false
        )
    val switch2774 =
        addConfig(
            "Switch2774",
            ConfigItem.configTypeBool,
            false
        )
    val switch2775 =
        addConfig(
            "Switch2775",
            ConfigItem.configTypeBool,
            false
        )
    val switch2776 =
        addConfig(
            "Switch2776",
            ConfigItem.configTypeBool,
            false
        )
    val switch2777 =
        addConfig(
            "Switch2777",
            ConfigItem.configTypeBool,
            false
        )
    val switch2778 =
        addConfig(
            "Switch2778",
            ConfigItem.configTypeBool,
            false
        )
    val switch2779 =
        addConfig(
            "Switch2779",
            ConfigItem.configTypeBool,
            false
        )
    val switch2780 =
        addConfig(
            "Switch2780",
            ConfigItem.configTypeBool,
            false
        )
    val switch2781 =
        addConfig(
            "Switch2781",
            ConfigItem.configTypeBool,
            false
        )
    val switch2782 =
        addConfig(
            "Switch2782",
            ConfigItem.configTypeBool,
            false
        )
    val switch2783 =
        addConfig(
            "Switch2783",
            ConfigItem.configTypeBool,
            false
        )
    val switch2784 =
        addConfig(
            "Switch2784",
            ConfigItem.configTypeBool,
            false
        )
    val switch2785 =
        addConfig(
            "Switch2785",
            ConfigItem.configTypeBool,
            false
        )
    val switch2786 =
        addConfig(
            "Switch2786",
            ConfigItem.configTypeBool,
            false
        )
    val switch2787 =
        addConfig(
            "Switch2787",
            ConfigItem.configTypeBool,
            false
        )
    val switch2788 =
        addConfig(
            "Switch2788",
            ConfigItem.configTypeBool,
            false
        )
    val switch2789 =
        addConfig(
            "Switch2789",
            ConfigItem.configTypeBool,
            false
        )
    val switch2790 =
        addConfig(
            "Switch2790",
            ConfigItem.configTypeBool,
            false
        )
    val switch2791 =
        addConfig(
            "Switch2791",
            ConfigItem.configTypeBool,
            false
        )
    val switch2792 =
        addConfig(
            "Switch2792",
            ConfigItem.configTypeBool,
            false
        )
    val switch2793 =
        addConfig(
            "Switch2793",
            ConfigItem.configTypeBool,
            false
        )
    val switch2794 =
        addConfig(
            "Switch2794",
            ConfigItem.configTypeBool,
            false
        )
    val switch2795 =
        addConfig(
            "Switch2795",
            ConfigItem.configTypeBool,
            false
        )
    val switch2796 =
        addConfig(
            "Switch2796",
            ConfigItem.configTypeBool,
            false
        )
    val switch2797 =
        addConfig(
            "Switch2797",
            ConfigItem.configTypeBool,
            false
        )
    val switch2798 =
        addConfig(
            "Switch2798",
            ConfigItem.configTypeBool,
            false
        )
    val switch2799 =
        addConfig(
            "Switch2799",
            ConfigItem.configTypeBool,
            false
        )
    val switch2800 =
        addConfig(
            "Switch2800",
            ConfigItem.configTypeBool,
            false
        )
    val switch2801 =
        addConfig(
            "Switch2801",
            ConfigItem.configTypeBool,
            false
        )
    val switch2802 =
        addConfig(
            "Switch2802",
            ConfigItem.configTypeBool,
            false
        )
    val switch2803 =
        addConfig(
            "Switch2803",
            ConfigItem.configTypeBool,
            false
        )
    val switch2804 =
        addConfig(
            "Switch2804",
            ConfigItem.configTypeBool,
            false
        )
    val switch2805 =
        addConfig(
            "Switch2805",
            ConfigItem.configTypeBool,
            false
        )
    val switch2806 =
        addConfig(
            "Switch2806",
            ConfigItem.configTypeBool,
            false
        )
    val switch2807 =
        addConfig(
            "Switch2807",
            ConfigItem.configTypeBool,
            false
        )
    val switch2808 =
        addConfig(
            "Switch2808",
            ConfigItem.configTypeBool,
            false
        )
    val switch2809 =
        addConfig(
            "Switch2809",
            ConfigItem.configTypeBool,
            false
        )
    val switch2810 =
        addConfig(
            "Switch2810",
            ConfigItem.configTypeBool,
            false
        )
    val switch2811 =
        addConfig(
            "Switch2811",
            ConfigItem.configTypeBool,
            false
        )
    val switch2812 =
        addConfig(
            "Switch2812",
            ConfigItem.configTypeBool,
            false
        )
    val switch2813 =
        addConfig(
            "Switch2813",
            ConfigItem.configTypeBool,
            false
        )
    val switch2814 =
        addConfig(
            "Switch2814",
            ConfigItem.configTypeBool,
            false
        )
    val switch2815 =
        addConfig(
            "Switch2815",
            ConfigItem.configTypeBool,
            false
        )
    val switch2816 =
        addConfig(
            "Switch2816",
            ConfigItem.configTypeBool,
            false
        )
    val switch2817 =
        addConfig(
            "Switch2817",
            ConfigItem.configTypeBool,
            false
        )
    val switch2818 =
        addConfig(
            "Switch2818",
            ConfigItem.configTypeBool,
            false
        )
    val switch2819 =
        addConfig(
            "Switch2819",
            ConfigItem.configTypeBool,
            false
        )
    val switch2820 =
        addConfig(
            "Switch2820",
            ConfigItem.configTypeBool,
            false
        )
    val switch2821 =
        addConfig(
            "Switch2821",
            ConfigItem.configTypeBool,
            false
        )
    val switch2822 =
        addConfig(
            "Switch2822",
            ConfigItem.configTypeBool,
            false
        )
    val switch2823 =
        addConfig(
            "Switch2823",
            ConfigItem.configTypeBool,
            false
        )
    val switch2824 =
        addConfig(
            "Switch2824",
            ConfigItem.configTypeBool,
            false
        )
    val switch2825 =
        addConfig(
            "Switch2825",
            ConfigItem.configTypeBool,
            false
        )
    val switch2826 =
        addConfig(
            "Switch2826",
            ConfigItem.configTypeBool,
            false
        )
    val switch2827 =
        addConfig(
            "Switch2827",
            ConfigItem.configTypeBool,
            false
        )
    val switch2828 =
        addConfig(
            "Switch2828",
            ConfigItem.configTypeBool,
            false
        )
    val switch2829 =
        addConfig(
            "Switch2829",
            ConfigItem.configTypeBool,
            false
        )
    val switch2830 =
        addConfig(
            "Switch2830",
            ConfigItem.configTypeBool,
            false
        )
    val switch2831 =
        addConfig(
            "Switch2831",
            ConfigItem.configTypeBool,
            false
        )
    val switch2832 =
        addConfig(
            "Switch2832",
            ConfigItem.configTypeBool,
            false
        )
    val switch2833 =
        addConfig(
            "Switch2833",
            ConfigItem.configTypeBool,
            false
        )
    val switch2834 =
        addConfig(
            "Switch2834",
            ConfigItem.configTypeBool,
            false
        )
    val switch2835 =
        addConfig(
            "Switch2835",
            ConfigItem.configTypeBool,
            false
        )
    val switch2836 =
        addConfig(
            "Switch2836",
            ConfigItem.configTypeBool,
            false
        )
    val switch2837 =
        addConfig(
            "Switch2837",
            ConfigItem.configTypeBool,
            false
        )
    val switch2838 =
        addConfig(
            "Switch2838",
            ConfigItem.configTypeBool,
            false
        )
    val switch2839 =
        addConfig(
            "Switch2839",
            ConfigItem.configTypeBool,
            false
        )
    val switch2840 =
        addConfig(
            "Switch2840",
            ConfigItem.configTypeBool,
            false
        )
    val switch2841 =
        addConfig(
            "Switch2841",
            ConfigItem.configTypeBool,
            false
        )
    val switch2842 =
        addConfig(
            "Switch2842",
            ConfigItem.configTypeBool,
            false
        )
    val switch2843 =
        addConfig(
            "Switch2843",
            ConfigItem.configTypeBool,
            false
        )
    val switch2844 =
        addConfig(
            "Switch2844",
            ConfigItem.configTypeBool,
            false
        )
    val switch2845 =
        addConfig(
            "Switch2845",
            ConfigItem.configTypeBool,
            false
        )
    val switch2846 =
        addConfig(
            "Switch2846",
            ConfigItem.configTypeBool,
            false
        )
    val switch2847 =
        addConfig(
            "Switch2847",
            ConfigItem.configTypeBool,
            false
        )
    val switch2848 =
        addConfig(
            "Switch2848",
            ConfigItem.configTypeBool,
            false
        )
    val switch2849 =
        addConfig(
            "Switch2849",
            ConfigItem.configTypeBool,
            false
        )
    val switch2850 =
        addConfig(
            "Switch2850",
            ConfigItem.configTypeBool,
            false
        )
    val switch2851 =
        addConfig(
            "Switch2851",
            ConfigItem.configTypeBool,
            false
        )
    val switch2852 =
        addConfig(
            "Switch2852",
            ConfigItem.configTypeBool,
            false
        )
    val switch2853 =
        addConfig(
            "Switch2853",
            ConfigItem.configTypeBool,
            false
        )
    val switch2854 =
        addConfig(
            "Switch2854",
            ConfigItem.configTypeBool,
            false
        )
    val switch2855 =
        addConfig(
            "Switch2855",
            ConfigItem.configTypeBool,
            false
        )
    val switch2856 =
        addConfig(
            "Switch2856",
            ConfigItem.configTypeBool,
            false
        )
    val switch2857 =
        addConfig(
            "Switch2857",
            ConfigItem.configTypeBool,
            false
        )
    val switch2858 =
        addConfig(
            "Switch2858",
            ConfigItem.configTypeBool,
            false
        )
    val switch2859 =
        addConfig(
            "Switch2859",
            ConfigItem.configTypeBool,
            false
        )
    val switch2860 =
        addConfig(
            "Switch2860",
            ConfigItem.configTypeBool,
            false
        )
    val switch2861 =
        addConfig(
            "Switch2861",
            ConfigItem.configTypeBool,
            false
        )
    val switch2862 =
        addConfig(
            "Switch2862",
            ConfigItem.configTypeBool,
            false
        )
    val switch2863 =
        addConfig(
            "Switch2863",
            ConfigItem.configTypeBool,
            false
        )
    val switch2864 =
        addConfig(
            "Switch2864",
            ConfigItem.configTypeBool,
            false
        )
    val switch2865 =
        addConfig(
            "Switch2865",
            ConfigItem.configTypeBool,
            false
        )
    val switch2866 =
        addConfig(
            "Switch2866",
            ConfigItem.configTypeBool,
            false
        )
    val switch2867 =
        addConfig(
            "Switch2867",
            ConfigItem.configTypeBool,
            false
        )
    val switch2868 =
        addConfig(
            "Switch2868",
            ConfigItem.configTypeBool,
            false
        )
    val switch2869 =
        addConfig(
            "Switch2869",
            ConfigItem.configTypeBool,
            false
        )
    val switch2870 =
        addConfig(
            "Switch2870",
            ConfigItem.configTypeBool,
            false
        )
    val switch2871 =
        addConfig(
            "Switch2871",
            ConfigItem.configTypeBool,
            false
        )
    val switch2872 =
        addConfig(
            "Switch2872",
            ConfigItem.configTypeBool,
            false
        )
    val switch2873 =
        addConfig(
            "Switch2873",
            ConfigItem.configTypeBool,
            false
        )
    val switch2874 =
        addConfig(
            "Switch2874",
            ConfigItem.configTypeBool,
            false
        )
    val switch2875 =
        addConfig(
            "Switch2875",
            ConfigItem.configTypeBool,
            false
        )
    val switch2876 =
        addConfig(
            "Switch2876",
            ConfigItem.configTypeBool,
            false
        )
    val switch2877 =
        addConfig(
            "Switch2877",
            ConfigItem.configTypeBool,
            false
        )
    val switch2878 =
        addConfig(
            "Switch2878",
            ConfigItem.configTypeBool,
            false
        )
    val switch2879 =
        addConfig(
            "Switch2879",
            ConfigItem.configTypeBool,
            false
        )
    val switch2880 =
        addConfig(
            "Switch2880",
            ConfigItem.configTypeBool,
            false
        )
    val switch2881 =
        addConfig(
            "Switch2881",
            ConfigItem.configTypeBool,
            false
        )
    val switch2882 =
        addConfig(
            "Switch2882",
            ConfigItem.configTypeBool,
            false
        )
    val switch2883 =
        addConfig(
            "Switch2883",
            ConfigItem.configTypeBool,
            false
        )
    val switch2884 =
        addConfig(
            "Switch2884",
            ConfigItem.configTypeBool,
            false
        )
    val switch2885 =
        addConfig(
            "Switch2885",
            ConfigItem.configTypeBool,
            false
        )
    val switch2886 =
        addConfig(
            "Switch2886",
            ConfigItem.configTypeBool,
            false
        )
    val switch2887 =
        addConfig(
            "Switch2887",
            ConfigItem.configTypeBool,
            false
        )
    val switch2888 =
        addConfig(
            "Switch2888",
            ConfigItem.configTypeBool,
            false
        )
    val switch2889 =
        addConfig(
            "Switch2889",
            ConfigItem.configTypeBool,
            false
        )
    val switch2890 =
        addConfig(
            "Switch2890",
            ConfigItem.configTypeBool,
            false
        )
    val switch2891 =
        addConfig(
            "Switch2891",
            ConfigItem.configTypeBool,
            false
        )
    val switch2892 =
        addConfig(
            "Switch2892",
            ConfigItem.configTypeBool,
            false
        )
    val switch2893 =
        addConfig(
            "Switch2893",
            ConfigItem.configTypeBool,
            false
        )
    val switch2894 =
        addConfig(
            "Switch2894",
            ConfigItem.configTypeBool,
            false
        )
    val switch2895 =
        addConfig(
            "Switch2895",
            ConfigItem.configTypeBool,
            false
        )
    val switch2896 =
        addConfig(
            "Switch2896",
            ConfigItem.configTypeBool,
            false
        )
    val switch2897 =
        addConfig(
            "Switch2897",
            ConfigItem.configTypeBool,
            false
        )
    val switch2898 =
        addConfig(
            "Switch2898",
            ConfigItem.configTypeBool,
            false
        )
    val switch2899 =
        addConfig(
            "Switch2899",
            ConfigItem.configTypeBool,
            false
        )
    val switch2900 =
        addConfig(
            "Switch2900",
            ConfigItem.configTypeBool,
            false
        )
    val switch2901 =
        addConfig(
            "Switch2901",
            ConfigItem.configTypeBool,
            false
        )
    val switch2902 =
        addConfig(
            "Switch2902",
            ConfigItem.configTypeBool,
            false
        )
    val switch2903 =
        addConfig(
            "Switch2903",
            ConfigItem.configTypeBool,
            false
        )
    val switch2904 =
        addConfig(
            "Switch2904",
            ConfigItem.configTypeBool,
            false
        )
    val switch2905 =
        addConfig(
            "Switch2905",
            ConfigItem.configTypeBool,
            false
        )
    val switch2906 =
        addConfig(
            "Switch2906",
            ConfigItem.configTypeBool,
            false
        )
    val switch2907 =
        addConfig(
            "Switch2907",
            ConfigItem.configTypeBool,
            false
        )
    val switch2908 =
        addConfig(
            "Switch2908",
            ConfigItem.configTypeBool,
            false
        )
    val switch2909 =
        addConfig(
            "Switch2909",
            ConfigItem.configTypeBool,
            false
        )
    val switch2910 =
        addConfig(
            "Switch2910",
            ConfigItem.configTypeBool,
            false
        )
    val switch2911 =
        addConfig(
            "Switch2911",
            ConfigItem.configTypeBool,
            false
        )
    val switch2912 =
        addConfig(
            "Switch2912",
            ConfigItem.configTypeBool,
            false
        )
    val switch2913 =
        addConfig(
            "Switch2913",
            ConfigItem.configTypeBool,
            false
        )
    val switch2914 =
        addConfig(
            "Switch2914",
            ConfigItem.configTypeBool,
            false
        )
    val switch2915 =
        addConfig(
            "Switch2915",
            ConfigItem.configTypeBool,
            false
        )
    val switch2916 =
        addConfig(
            "Switch2916",
            ConfigItem.configTypeBool,
            false
        )
    val switch2917 =
        addConfig(
            "Switch2917",
            ConfigItem.configTypeBool,
            false
        )
    val switch2918 =
        addConfig(
            "Switch2918",
            ConfigItem.configTypeBool,
            false
        )
    val switch2919 =
        addConfig(
            "Switch2919",
            ConfigItem.configTypeBool,
            false
        )
    val switch2920 =
        addConfig(
            "Switch2920",
            ConfigItem.configTypeBool,
            false
        )
    val switch2921 =
        addConfig(
            "Switch2921",
            ConfigItem.configTypeBool,
            false
        )
    val switch2922 =
        addConfig(
            "Switch2922",
            ConfigItem.configTypeBool,
            false
        )
    val switch2923 =
        addConfig(
            "Switch2923",
            ConfigItem.configTypeBool,
            false
        )
    val switch2924 =
        addConfig(
            "Switch2924",
            ConfigItem.configTypeBool,
            false
        )
    val switch2925 =
        addConfig(
            "Switch2925",
            ConfigItem.configTypeBool,
            false
        )
    val switch2926 =
        addConfig(
            "Switch2926",
            ConfigItem.configTypeBool,
            false
        )
    val switch2927 =
        addConfig(
            "Switch2927",
            ConfigItem.configTypeBool,
            false
        )
    val switch2928 =
        addConfig(
            "Switch2928",
            ConfigItem.configTypeBool,
            false
        )
    val switch2929 =
        addConfig(
            "Switch2929",
            ConfigItem.configTypeBool,
            false
        )
    val switch2930 =
        addConfig(
            "Switch2930",
            ConfigItem.configTypeBool,
            false
        )
    val switch2931 =
        addConfig(
            "Switch2931",
            ConfigItem.configTypeBool,
            false
        )
    val switch2932 =
        addConfig(
            "Switch2932",
            ConfigItem.configTypeBool,
            false
        )
    val switch2933 =
        addConfig(
            "Switch2933",
            ConfigItem.configTypeBool,
            false
        )
    val switch2934 =
        addConfig(
            "Switch2934",
            ConfigItem.configTypeBool,
            false
        )
    val switch2935 =
        addConfig(
            "Switch2935",
            ConfigItem.configTypeBool,
            false
        )
    val switch2936 =
        addConfig(
            "Switch2936",
            ConfigItem.configTypeBool,
            false
        )
    val switch2937 =
        addConfig(
            "Switch2937",
            ConfigItem.configTypeBool,
            false
        )
    val switch2938 =
        addConfig(
            "Switch2938",
            ConfigItem.configTypeBool,
            false
        )
    val switch2939 =
        addConfig(
            "Switch2939",
            ConfigItem.configTypeBool,
            false
        )
    val switch2940 =
        addConfig(
            "Switch2940",
            ConfigItem.configTypeBool,
            false
        )
    val switch2941 =
        addConfig(
            "Switch2941",
            ConfigItem.configTypeBool,
            false
        )
    val switch2942 =
        addConfig(
            "Switch2942",
            ConfigItem.configTypeBool,
            false
        )
    val switch2943 =
        addConfig(
            "Switch2943",
            ConfigItem.configTypeBool,
            false
        )
    val switch2944 =
        addConfig(
            "Switch2944",
            ConfigItem.configTypeBool,
            false
        )
    val switch2945 =
        addConfig(
            "Switch2945",
            ConfigItem.configTypeBool,
            false
        )
    val switch2946 =
        addConfig(
            "Switch2946",
            ConfigItem.configTypeBool,
            false
        )
    val switch2947 =
        addConfig(
            "Switch2947",
            ConfigItem.configTypeBool,
            false
        )
    val switch2948 =
        addConfig(
            "Switch2948",
            ConfigItem.configTypeBool,
            false
        )
    val switch2949 =
        addConfig(
            "Switch2949",
            ConfigItem.configTypeBool,
            false
        )
    val switch2950 =
        addConfig(
            "Switch2950",
            ConfigItem.configTypeBool,
            false
        )
    val switch2951 =
        addConfig(
            "Switch2951",
            ConfigItem.configTypeBool,
            false
        )
    val switch2952 =
        addConfig(
            "Switch2952",
            ConfigItem.configTypeBool,
            false
        )
    val switch2953 =
        addConfig(
            "Switch2953",
            ConfigItem.configTypeBool,
            false
        )
    val switch2954 =
        addConfig(
            "Switch2954",
            ConfigItem.configTypeBool,
            false
        )
    val switch2955 =
        addConfig(
            "Switch2955",
            ConfigItem.configTypeBool,
            false
        )
    val switch2956 =
        addConfig(
            "Switch2956",
            ConfigItem.configTypeBool,
            false
        )
    val switch2957 =
        addConfig(
            "Switch2957",
            ConfigItem.configTypeBool,
            false
        )
    val switch2958 =
        addConfig(
            "Switch2958",
            ConfigItem.configTypeBool,
            false
        )
    val switch2959 =
        addConfig(
            "Switch2959",
            ConfigItem.configTypeBool,
            false
        )
    val switch2960 =
        addConfig(
            "Switch2960",
            ConfigItem.configTypeBool,
            false
        )
    val switch2961 =
        addConfig(
            "Switch2961",
            ConfigItem.configTypeBool,
            false
        )
    val switch2962 =
        addConfig(
            "Switch2962",
            ConfigItem.configTypeBool,
            false
        )
    val switch2963 =
        addConfig(
            "Switch2963",
            ConfigItem.configTypeBool,
            false
        )
    val switch2964 =
        addConfig(
            "Switch2964",
            ConfigItem.configTypeBool,
            false
        )
    val switch2965 =
        addConfig(
            "Switch2965",
            ConfigItem.configTypeBool,
            false
        )
    val switch2966 =
        addConfig(
            "Switch2966",
            ConfigItem.configTypeBool,
            false
        )
    val switch2967 =
        addConfig(
            "Switch2967",
            ConfigItem.configTypeBool,
            false
        )
    val switch2968 =
        addConfig(
            "Switch2968",
            ConfigItem.configTypeBool,
            false
        )
    val switch2969 =
        addConfig(
            "Switch2969",
            ConfigItem.configTypeBool,
            false
        )
    val switch2970 =
        addConfig(
            "Switch2970",
            ConfigItem.configTypeBool,
            false
        )
    val switch2971 =
        addConfig(
            "Switch2971",
            ConfigItem.configTypeBool,
            false
        )
    val switch2972 =
        addConfig(
            "Switch2972",
            ConfigItem.configTypeBool,
            false
        )
    val switch2973 =
        addConfig(
            "Switch2973",
            ConfigItem.configTypeBool,
            false
        )
    val switch2974 =
        addConfig(
            "Switch2974",
            ConfigItem.configTypeBool,
            false
        )
    val switch2975 =
        addConfig(
            "Switch2975",
            ConfigItem.configTypeBool,
            false
        )
    val switch2976 =
        addConfig(
            "Switch2976",
            ConfigItem.configTypeBool,
            false
        )
    val switch2977 =
        addConfig(
            "Switch2977",
            ConfigItem.configTypeBool,
            false
        )
    val switch2978 =
        addConfig(
            "Switch2978",
            ConfigItem.configTypeBool,
            false
        )
    val switch2979 =
        addConfig(
            "Switch2979",
            ConfigItem.configTypeBool,
            false
        )
    val switch2980 =
        addConfig(
            "Switch2980",
            ConfigItem.configTypeBool,
            false
        )
    val switch2981 =
        addConfig(
            "Switch2981",
            ConfigItem.configTypeBool,
            false
        )
    val switch2982 =
        addConfig(
            "Switch2982",
            ConfigItem.configTypeBool,
            false
        )
    val switch2983 =
        addConfig(
            "Switch2983",
            ConfigItem.configTypeBool,
            false
        )
    val switch2984 =
        addConfig(
            "Switch2984",
            ConfigItem.configTypeBool,
            false
        )
    val switch2985 =
        addConfig(
            "Switch2985",
            ConfigItem.configTypeBool,
            false
        )
    val switch2986 =
        addConfig(
            "Switch2986",
            ConfigItem.configTypeBool,
            false
        )
    val switch2987 =
        addConfig(
            "Switch2987",
            ConfigItem.configTypeBool,
            false
        )
    val switch2988 =
        addConfig(
            "Switch2988",
            ConfigItem.configTypeBool,
            false
        )
    val switch2989 =
        addConfig(
            "Switch2989",
            ConfigItem.configTypeBool,
            false
        )
    val switch2990 =
        addConfig(
            "Switch2990",
            ConfigItem.configTypeBool,
            false
        )
    val switch2991 =
        addConfig(
            "Switch2991",
            ConfigItem.configTypeBool,
            false
        )
    val switch2992 =
        addConfig(
            "Switch2992",
            ConfigItem.configTypeBool,
            false
        )
    val switch2993 =
        addConfig(
            "Switch2993",
            ConfigItem.configTypeBool,
            false
        )
    val switch2994 =
        addConfig(
            "Switch2994",
            ConfigItem.configTypeBool,
            false
        )
    val switch2995 =
        addConfig(
            "Switch2995",
            ConfigItem.configTypeBool,
            false
        )
    val switch2996 =
        addConfig(
            "Switch2996",
            ConfigItem.configTypeBool,
            false
        )
    val switch2997 =
        addConfig(
            "Switch2997",
            ConfigItem.configTypeBool,
            false
        )
    val switch2998 =
        addConfig(
            "Switch2998",
            ConfigItem.configTypeBool,
            false
        )
    val switch2999 =
        addConfig(
            "Switch2999",
            ConfigItem.configTypeBool,
            false
        )
    val switch3000 =
        addConfig(
            "Switch3000",
            ConfigItem.configTypeBool,
            false
        )
    val switch3001 =
        addConfig(
            "Switch3001",
            ConfigItem.configTypeBool,
            false
        )
    val switch3002 =
        addConfig(
            "Switch3002",
            ConfigItem.configTypeBool,
            false
        )
    val switch3003 =
        addConfig(
            "Switch3003",
            ConfigItem.configTypeBool,
            false
        )
    val switch3004 =
        addConfig(
            "Switch3004",
            ConfigItem.configTypeBool,
            false
        )
    val switch3005 =
        addConfig(
            "Switch3005",
            ConfigItem.configTypeBool,
            false
        )
    val switch3006 =
        addConfig(
            "Switch3006",
            ConfigItem.configTypeBool,
            false
        )
    val switch3007 =
        addConfig(
            "Switch3007",
            ConfigItem.configTypeBool,
            false
        )
    val switch3008 =
        addConfig(
            "Switch3008",
            ConfigItem.configTypeBool,
            false
        )
    val switch3009 =
        addConfig(
            "Switch3009",
            ConfigItem.configTypeBool,
            false
        )
    val switch3010 =
        addConfig(
            "Switch3010",
            ConfigItem.configTypeBool,
            false
        )
    val switch3011 =
        addConfig(
            "Switch3011",
            ConfigItem.configTypeBool,
            false
        )
    val switch3012 =
        addConfig(
            "Switch3012",
            ConfigItem.configTypeBool,
            false
        )
    val switch3013 =
        addConfig(
            "Switch3013",
            ConfigItem.configTypeBool,
            false
        )
    val switch3014 =
        addConfig(
            "Switch3014",
            ConfigItem.configTypeBool,
            false
        )
    val switch3015 =
        addConfig(
            "Switch3015",
            ConfigItem.configTypeBool,
            false
        )
    val switch3016 =
        addConfig(
            "Switch3016",
            ConfigItem.configTypeBool,
            false
        )
    val switch3017 =
        addConfig(
            "Switch3017",
            ConfigItem.configTypeBool,
            false
        )
    val switch3018 =
        addConfig(
            "Switch3018",
            ConfigItem.configTypeBool,
            false
        )
    val switch3019 =
        addConfig(
            "Switch3019",
            ConfigItem.configTypeBool,
            false
        )
    val switch3020 =
        addConfig(
            "Switch3020",
            ConfigItem.configTypeBool,
            false
        )
    val switch3021 =
        addConfig(
            "Switch3021",
            ConfigItem.configTypeBool,
            false
        )
    val switch3022 =
        addConfig(
            "Switch3022",
            ConfigItem.configTypeBool,
            false
        )
    val switch3023 =
        addConfig(
            "Switch3023",
            ConfigItem.configTypeBool,
            false
        )
    val switch3024 =
        addConfig(
            "Switch3024",
            ConfigItem.configTypeBool,
            false
        )
    val switch3025 =
        addConfig(
            "Switch3025",
            ConfigItem.configTypeBool,
            false
        )
    val switch3026 =
        addConfig(
            "Switch3026",
            ConfigItem.configTypeBool,
            false
        )
    val switch3027 =
        addConfig(
            "Switch3027",
            ConfigItem.configTypeBool,
            false
        )
    val switch3028 =
        addConfig(
            "Switch3028",
            ConfigItem.configTypeBool,
            false
        )
    val switch3029 =
        addConfig(
            "Switch3029",
            ConfigItem.configTypeBool,
            false
        )
    val switch3030 =
        addConfig(
            "Switch3030",
            ConfigItem.configTypeBool,
            false
        )
    val switch3031 =
        addConfig(
            "Switch3031",
            ConfigItem.configTypeBool,
            false
        )
    val switch3032 =
        addConfig(
            "Switch3032",
            ConfigItem.configTypeBool,
            false
        )
    val switch3033 =
        addConfig(
            "Switch3033",
            ConfigItem.configTypeBool,
            false
        )
    val switch3034 =
        addConfig(
            "Switch3034",
            ConfigItem.configTypeBool,
            false
        )
    val switch3035 =
        addConfig(
            "Switch3035",
            ConfigItem.configTypeBool,
            false
        )
    val switch3036 =
        addConfig(
            "Switch3036",
            ConfigItem.configTypeBool,
            false
        )
    val switch3037 =
        addConfig(
            "Switch3037",
            ConfigItem.configTypeBool,
            false
        )
    val switch3038 =
        addConfig(
            "Switch3038",
            ConfigItem.configTypeBool,
            false
        )
    val switch3039 =
        addConfig(
            "Switch3039",
            ConfigItem.configTypeBool,
            false
        )
    val switch3040 =
        addConfig(
            "Switch3040",
            ConfigItem.configTypeBool,
            false
        )
    val switch3041 =
        addConfig(
            "Switch3041",
            ConfigItem.configTypeBool,
            false
        )
    val switch3042 =
        addConfig(
            "Switch3042",
            ConfigItem.configTypeBool,
            false
        )
    val switch3043 =
        addConfig(
            "Switch3043",
            ConfigItem.configTypeBool,
            false
        )
    val switch3044 =
        addConfig(
            "Switch3044",
            ConfigItem.configTypeBool,
            false
        )
    val switch3045 =
        addConfig(
            "Switch3045",
            ConfigItem.configTypeBool,
            false
        )
    val switch3046 =
        addConfig(
            "Switch3046",
            ConfigItem.configTypeBool,
            false
        )
    val switch3047 =
        addConfig(
            "Switch3047",
            ConfigItem.configTypeBool,
            false
        )
    val switch3048 =
        addConfig(
            "Switch3048",
            ConfigItem.configTypeBool,
            false
        )
    val switch3049 =
        addConfig(
            "Switch3049",
            ConfigItem.configTypeBool,
            false
        )
    val switch3050 =
        addConfig(
            "Switch3050",
            ConfigItem.configTypeBool,
            false
        )
    val switch3051 =
        addConfig(
            "Switch3051",
            ConfigItem.configTypeBool,
            false
        )
    val switch3052 =
        addConfig(
            "Switch3052",
            ConfigItem.configTypeBool,
            false
        )
    val switch3053 =
        addConfig(
            "Switch3053",
            ConfigItem.configTypeBool,
            false
        )
    val switch3054 =
        addConfig(
            "Switch3054",
            ConfigItem.configTypeBool,
            false
        )
    val switch3055 =
        addConfig(
            "Switch3055",
            ConfigItem.configTypeBool,
            false
        )
    val switch3056 =
        addConfig(
            "Switch3056",
            ConfigItem.configTypeBool,
            false
        )
    val switch3057 =
        addConfig(
            "Switch3057",
            ConfigItem.configTypeBool,
            false
        )
    val switch3058 =
        addConfig(
            "Switch3058",
            ConfigItem.configTypeBool,
            false
        )
    val switch3059 =
        addConfig(
            "Switch3059",
            ConfigItem.configTypeBool,
            false
        )
    val switch3060 =
        addConfig(
            "Switch3060",
            ConfigItem.configTypeBool,
            false
        )
    val switch3061 =
        addConfig(
            "Switch3061",
            ConfigItem.configTypeBool,
            false
        )
    val switch3062 =
        addConfig(
            "Switch3062",
            ConfigItem.configTypeBool,
            false
        )
    val switch3063 =
        addConfig(
            "Switch3063",
            ConfigItem.configTypeBool,
            false
        )
    val switch3064 =
        addConfig(
            "Switch3064",
            ConfigItem.configTypeBool,
            false
        )
    val switch3065 =
        addConfig(
            "Switch3065",
            ConfigItem.configTypeBool,
            false
        )
    val switch3066 =
        addConfig(
            "Switch3066",
            ConfigItem.configTypeBool,
            false
        )
    val switch3067 =
        addConfig(
            "Switch3067",
            ConfigItem.configTypeBool,
            false
        )
    val switch3068 =
        addConfig(
            "Switch3068",
            ConfigItem.configTypeBool,
            false
        )
    val switch3069 =
        addConfig(
            "Switch3069",
            ConfigItem.configTypeBool,
            false
        )
    val switch3070 =
        addConfig(
            "Switch3070",
            ConfigItem.configTypeBool,
            false
        )
    val switch3071 =
        addConfig(
            "Switch3071",
            ConfigItem.configTypeBool,
            false
        )
    val switch3072 =
        addConfig(
            "Switch3072",
            ConfigItem.configTypeBool,
            false
        )
    val switch3073 =
        addConfig(
            "Switch3073",
            ConfigItem.configTypeBool,
            false
        )
    val switch3074 =
        addConfig(
            "Switch3074",
            ConfigItem.configTypeBool,
            false
        )
    val switch3075 =
        addConfig(
            "Switch3075",
            ConfigItem.configTypeBool,
            false
        )
    val switch3076 =
        addConfig(
            "Switch3076",
            ConfigItem.configTypeBool,
            false
        )
    val switch3077 =
        addConfig(
            "Switch3077",
            ConfigItem.configTypeBool,
            false
        )
    val switch3078 =
        addConfig(
            "Switch3078",
            ConfigItem.configTypeBool,
            false
        )
    val switch3079 =
        addConfig(
            "Switch3079",
            ConfigItem.configTypeBool,
            false
        )
    val switch3080 =
        addConfig(
            "Switch3080",
            ConfigItem.configTypeBool,
            false
        )
    val switch3081 =
        addConfig(
            "Switch3081",
            ConfigItem.configTypeBool,
            false
        )
    val switch3082 =
        addConfig(
            "Switch3082",
            ConfigItem.configTypeBool,
            false
        )
    val switch3083 =
        addConfig(
            "Switch3083",
            ConfigItem.configTypeBool,
            false
        )
    val switch3084 =
        addConfig(
            "Switch3084",
            ConfigItem.configTypeBool,
            false
        )
    val switch3085 =
        addConfig(
            "Switch3085",
            ConfigItem.configTypeBool,
            false
        )
    val switch3086 =
        addConfig(
            "Switch3086",
            ConfigItem.configTypeBool,
            false
        )
    val switch3087 =
        addConfig(
            "Switch3087",
            ConfigItem.configTypeBool,
            false
        )
    val switch3088 =
        addConfig(
            "Switch3088",
            ConfigItem.configTypeBool,
            false
        )
    val switch3089 =
        addConfig(
            "Switch3089",
            ConfigItem.configTypeBool,
            false
        )
    val switch3090 =
        addConfig(
            "Switch3090",
            ConfigItem.configTypeBool,
            false
        )
    val switch3091 =
        addConfig(
            "Switch3091",
            ConfigItem.configTypeBool,
            false
        )
    val switch3092 =
        addConfig(
            "Switch3092",
            ConfigItem.configTypeBool,
            false
        )
    val switch3093 =
        addConfig(
            "Switch3093",
            ConfigItem.configTypeBool,
            false
        )
    val switch3094 =
        addConfig(
            "Switch3094",
            ConfigItem.configTypeBool,
            false
        )
    val switch3095 =
        addConfig(
            "Switch3095",
            ConfigItem.configTypeBool,
            false
        )
    val switch3096 =
        addConfig(
            "Switch3096",
            ConfigItem.configTypeBool,
            false
        )
    val switch3097 =
        addConfig(
            "Switch3097",
            ConfigItem.configTypeBool,
            false
        )
    val switch3098 =
        addConfig(
            "Switch3098",
            ConfigItem.configTypeBool,
            false
        )
    val switch3099 =
        addConfig(
            "Switch3099",
            ConfigItem.configTypeBool,
            false
        )
    val switch3100 =
        addConfig(
            "Switch3100",
            ConfigItem.configTypeBool,
            false
        )
    val switch3101 =
        addConfig(
            "Switch3101",
            ConfigItem.configTypeBool,
            false
        )
    val switch3102 =
        addConfig(
            "Switch3102",
            ConfigItem.configTypeBool,
            false
        )
    val switch3103 =
        addConfig(
            "Switch3103",
            ConfigItem.configTypeBool,
            false
        )
    val switch3104 =
        addConfig(
            "Switch3104",
            ConfigItem.configTypeBool,
            false
        )
    val switch3105 =
        addConfig(
            "Switch3105",
            ConfigItem.configTypeBool,
            false
        )
    val switch3106 =
        addConfig(
            "Switch3106",
            ConfigItem.configTypeBool,
            false
        )
    val switch3107 =
        addConfig(
            "Switch3107",
            ConfigItem.configTypeBool,
            false
        )
    val switch3108 =
        addConfig(
            "Switch3108",
            ConfigItem.configTypeBool,
            false
        )
    val switch3109 =
        addConfig(
            "Switch3109",
            ConfigItem.configTypeBool,
            false
        )
    val switch3110 =
        addConfig(
            "Switch3110",
            ConfigItem.configTypeBool,
            false
        )
    val switch3111 =
        addConfig(
            "Switch3111",
            ConfigItem.configTypeBool,
            false
        )
    val switch3112 =
        addConfig(
            "Switch3112",
            ConfigItem.configTypeBool,
            false
        )
    val switch3113 =
        addConfig(
            "Switch3113",
            ConfigItem.configTypeBool,
            false
        )
    val switch3114 =
        addConfig(
            "Switch3114",
            ConfigItem.configTypeBool,
            false
        )
    val switch3115 =
        addConfig(
            "Switch3115",
            ConfigItem.configTypeBool,
            false
        )
    val switch3116 =
        addConfig(
            "Switch3116",
            ConfigItem.configTypeBool,
            false
        )
    val switch3117 =
        addConfig(
            "Switch3117",
            ConfigItem.configTypeBool,
            false
        )
    val switch3118 =
        addConfig(
            "Switch3118",
            ConfigItem.configTypeBool,
            false
        )
    val switch3119 =
        addConfig(
            "Switch3119",
            ConfigItem.configTypeBool,
            false
        )
    val switch3120 =
        addConfig(
            "Switch3120",
            ConfigItem.configTypeBool,
            false
        )
    val switch3121 =
        addConfig(
            "Switch3121",
            ConfigItem.configTypeBool,
            false
        )
    val switch3122 =
        addConfig(
            "Switch3122",
            ConfigItem.configTypeBool,
            false
        )
    val switch3123 =
        addConfig(
            "Switch3123",
            ConfigItem.configTypeBool,
            false
        )
    val switch3124 =
        addConfig(
            "Switch3124",
            ConfigItem.configTypeBool,
            false
        )
    val switch3125 =
        addConfig(
            "Switch3125",
            ConfigItem.configTypeBool,
            false
        )
    val switch3126 =
        addConfig(
            "Switch3126",
            ConfigItem.configTypeBool,
            false
        )
    val switch3127 =
        addConfig(
            "Switch3127",
            ConfigItem.configTypeBool,
            false
        )
    val switch3128 =
        addConfig(
            "Switch3128",
            ConfigItem.configTypeBool,
            false
        )
    val switch3129 =
        addConfig(
            "Switch3129",
            ConfigItem.configTypeBool,
            false
        )
    val switch3130 =
        addConfig(
            "Switch3130",
            ConfigItem.configTypeBool,
            false
        )
    val switch3131 =
        addConfig(
            "Switch3131",
            ConfigItem.configTypeBool,
            false
        )
    val switch3132 =
        addConfig(
            "Switch3132",
            ConfigItem.configTypeBool,
            false
        )
    val switch3133 =
        addConfig(
            "Switch3133",
            ConfigItem.configTypeBool,
            false
        )
    val switch3134 =
        addConfig(
            "Switch3134",
            ConfigItem.configTypeBool,
            false
        )
    val switch3135 =
        addConfig(
            "Switch3135",
            ConfigItem.configTypeBool,
            false
        )
    val switch3136 =
        addConfig(
            "Switch3136",
            ConfigItem.configTypeBool,
            false
        )
    val switch3137 =
        addConfig(
            "Switch3137",
            ConfigItem.configTypeBool,
            false
        )
    val switch3138 =
        addConfig(
            "Switch3138",
            ConfigItem.configTypeBool,
            false
        )
    val switch3139 =
        addConfig(
            "Switch3139",
            ConfigItem.configTypeBool,
            false
        )
    val switch3140 =
        addConfig(
            "Switch3140",
            ConfigItem.configTypeBool,
            false
        )
    val switch3141 =
        addConfig(
            "Switch3141",
            ConfigItem.configTypeBool,
            false
        )
    val switch3142 =
        addConfig(
            "Switch3142",
            ConfigItem.configTypeBool,
            false
        )
    val switch3143 =
        addConfig(
            "Switch3143",
            ConfigItem.configTypeBool,
            false
        )
    val switch3144 =
        addConfig(
            "Switch3144",
            ConfigItem.configTypeBool,
            false
        )
    val switch3145 =
        addConfig(
            "Switch3145",
            ConfigItem.configTypeBool,
            false
        )
    val switch3146 =
        addConfig(
            "Switch3146",
            ConfigItem.configTypeBool,
            false
        )
    val switch3147 =
        addConfig(
            "Switch3147",
            ConfigItem.configTypeBool,
            false
        )
    val switch3148 =
        addConfig(
            "Switch3148",
            ConfigItem.configTypeBool,
            false
        )
    val switch3149 =
        addConfig(
            "Switch3149",
            ConfigItem.configTypeBool,
            false
        )
    val switch3150 =
        addConfig(
            "Switch3150",
            ConfigItem.configTypeBool,
            false
        )
    val switch3151 =
        addConfig(
            "Switch3151",
            ConfigItem.configTypeBool,
            false
        )
    val switch3152 =
        addConfig(
            "Switch3152",
            ConfigItem.configTypeBool,
            false
        )
    val switch3153 =
        addConfig(
            "Switch3153",
            ConfigItem.configTypeBool,
            false
        )
    val switch3154 =
        addConfig(
            "Switch3154",
            ConfigItem.configTypeBool,
            false
        )
    val switch3155 =
        addConfig(
            "Switch3155",
            ConfigItem.configTypeBool,
            false
        )
    val switch3156 =
        addConfig(
            "Switch3156",
            ConfigItem.configTypeBool,
            false
        )
    val switch3157 =
        addConfig(
            "Switch3157",
            ConfigItem.configTypeBool,
            false
        )
    val switch3158 =
        addConfig(
            "Switch3158",
            ConfigItem.configTypeBool,
            false
        )
    val switch3159 =
        addConfig(
            "Switch3159",
            ConfigItem.configTypeBool,
            false
        )
    val switch3160 =
        addConfig(
            "Switch3160",
            ConfigItem.configTypeBool,
            false
        )
    val switch3161 =
        addConfig(
            "Switch3161",
            ConfigItem.configTypeBool,
            false
        )
    val switch3162 =
        addConfig(
            "Switch3162",
            ConfigItem.configTypeBool,
            false
        )
    val switch3163 =
        addConfig(
            "Switch3163",
            ConfigItem.configTypeBool,
            false
        )
    val switch3164 =
        addConfig(
            "Switch3164",
            ConfigItem.configTypeBool,
            false
        )
    val switch3165 =
        addConfig(
            "Switch3165",
            ConfigItem.configTypeBool,
            false
        )
    val switch3166 =
        addConfig(
            "Switch3166",
            ConfigItem.configTypeBool,
            false
        )
    val switch3167 =
        addConfig(
            "Switch3167",
            ConfigItem.configTypeBool,
            false
        )
    val switch3168 =
        addConfig(
            "Switch3168",
            ConfigItem.configTypeBool,
            false
        )
    val switch3169 =
        addConfig(
            "Switch3169",
            ConfigItem.configTypeBool,
            false
        )
    val switch3170 =
        addConfig(
            "Switch3170",
            ConfigItem.configTypeBool,
            false
        )
    val switch3171 =
        addConfig(
            "Switch3171",
            ConfigItem.configTypeBool,
            false
        )
    val switch3172 =
        addConfig(
            "Switch3172",
            ConfigItem.configTypeBool,
            false
        )
    val switch3173 =
        addConfig(
            "Switch3173",
            ConfigItem.configTypeBool,
            false
        )
    val switch3174 =
        addConfig(
            "Switch3174",
            ConfigItem.configTypeBool,
            false
        )
    val switch3175 =
        addConfig(
            "Switch3175",
            ConfigItem.configTypeBool,
            false
        )
    val switch3176 =
        addConfig(
            "Switch3176",
            ConfigItem.configTypeBool,
            false
        )
    val switch3177 =
        addConfig(
            "Switch3177",
            ConfigItem.configTypeBool,
            false
        )
    val switch3178 =
        addConfig(
            "Switch3178",
            ConfigItem.configTypeBool,
            false
        )
    val switch3179 =
        addConfig(
            "Switch3179",
            ConfigItem.configTypeBool,
            false
        )
    val switch3180 =
        addConfig(
            "Switch3180",
            ConfigItem.configTypeBool,
            false
        )
    val switch3181 =
        addConfig(
            "Switch3181",
            ConfigItem.configTypeBool,
            false
        )
    val switch3182 =
        addConfig(
            "Switch3182",
            ConfigItem.configTypeBool,
            false
        )
    val switch3183 =
        addConfig(
            "Switch3183",
            ConfigItem.configTypeBool,
            false
        )
    val switch3184 =
        addConfig(
            "Switch3184",
            ConfigItem.configTypeBool,
            false
        )
    val switch3185 =
        addConfig(
            "Switch3185",
            ConfigItem.configTypeBool,
            false
        )
    val switch3186 =
        addConfig(
            "Switch3186",
            ConfigItem.configTypeBool,
            false
        )
    val switch3187 =
        addConfig(
            "Switch3187",
            ConfigItem.configTypeBool,
            false
        )
    val switch3188 =
        addConfig(
            "Switch3188",
            ConfigItem.configTypeBool,
            false
        )
    val switch3189 =
        addConfig(
            "Switch3189",
            ConfigItem.configTypeBool,
            false
        )
    val switch3190 =
        addConfig(
            "Switch3190",
            ConfigItem.configTypeBool,
            false
        )
    val switch3191 =
        addConfig(
            "Switch3191",
            ConfigItem.configTypeBool,
            false
        )
    val switch3192 =
        addConfig(
            "Switch3192",
            ConfigItem.configTypeBool,
            false
        )
    val switch3193 =
        addConfig(
            "Switch3193",
            ConfigItem.configTypeBool,
            false
        )
    val switch3194 =
        addConfig(
            "Switch3194",
            ConfigItem.configTypeBool,
            false
        )
    val switch3195 =
        addConfig(
            "Switch3195",
            ConfigItem.configTypeBool,
            false
        )
    val switch3196 =
        addConfig(
            "Switch3196",
            ConfigItem.configTypeBool,
            false
        )
    val switch3197 =
        addConfig(
            "Switch3197",
            ConfigItem.configTypeBool,
            false
        )
    val switch3198 =
        addConfig(
            "Switch3198",
            ConfigItem.configTypeBool,
            false
        )
    val switch3199 =
        addConfig(
            "Switch3199",
            ConfigItem.configTypeBool,
            false
        )
    val switch3200 =
        addConfig(
            "Switch3200",
            ConfigItem.configTypeBool,
            false
        )
    val switch3201 =
        addConfig(
            "Switch3201",
            ConfigItem.configTypeBool,
            false
        )
    val switch3202 =
        addConfig(
            "Switch3202",
            ConfigItem.configTypeBool,
            false
        )
    val switch3203 =
        addConfig(
            "Switch3203",
            ConfigItem.configTypeBool,
            false
        )
    val switch3204 =
        addConfig(
            "Switch3204",
            ConfigItem.configTypeBool,
            false
        )
    val switch3205 =
        addConfig(
            "Switch3205",
            ConfigItem.configTypeBool,
            false
        )
    val switch3206 =
        addConfig(
            "Switch3206",
            ConfigItem.configTypeBool,
            false
        )
    val switch3207 =
        addConfig(
            "Switch3207",
            ConfigItem.configTypeBool,
            false
        )
    val switch3208 =
        addConfig(
            "Switch3208",
            ConfigItem.configTypeBool,
            false
        )
    val switch3209 =
        addConfig(
            "Switch3209",
            ConfigItem.configTypeBool,
            false
        )
    val switch3210 =
        addConfig(
            "Switch3210",
            ConfigItem.configTypeBool,
            false
        )
    val switch3211 =
        addConfig(
            "Switch3211",
            ConfigItem.configTypeBool,
            false
        )
    val switch3212 =
        addConfig(
            "Switch3212",
            ConfigItem.configTypeBool,
            false
        )
    val switch3213 =
        addConfig(
            "Switch3213",
            ConfigItem.configTypeBool,
            false
        )
    val switch3214 =
        addConfig(
            "Switch3214",
            ConfigItem.configTypeBool,
            false
        )
    val switch3215 =
        addConfig(
            "Switch3215",
            ConfigItem.configTypeBool,
            false
        )
    val switch3216 =
        addConfig(
            "Switch3216",
            ConfigItem.configTypeBool,
            false
        )
    val switch3217 =
        addConfig(
            "Switch3217",
            ConfigItem.configTypeBool,
            false
        )
    val switch3218 =
        addConfig(
            "Switch3218",
            ConfigItem.configTypeBool,
            false
        )
    val switch3219 =
        addConfig(
            "Switch3219",
            ConfigItem.configTypeBool,
            false
        )
    val switch3220 =
        addConfig(
            "Switch3220",
            ConfigItem.configTypeBool,
            false
        )
    val switch3221 =
        addConfig(
            "Switch3221",
            ConfigItem.configTypeBool,
            false
        )
    val switch3222 =
        addConfig(
            "Switch3222",
            ConfigItem.configTypeBool,
            false
        )
    val switch3223 =
        addConfig(
            "Switch3223",
            ConfigItem.configTypeBool,
            false
        )
    val switch3224 =
        addConfig(
            "Switch3224",
            ConfigItem.configTypeBool,
            false
        )
    val switch3225 =
        addConfig(
            "Switch3225",
            ConfigItem.configTypeBool,
            false
        )
    val switch3226 =
        addConfig(
            "Switch3226",
            ConfigItem.configTypeBool,
            false
        )
    val switch3227 =
        addConfig(
            "Switch3227",
            ConfigItem.configTypeBool,
            false
        )
    val switch3228 =
        addConfig(
            "Switch3228",
            ConfigItem.configTypeBool,
            false
        )
    val switch3229 =
        addConfig(
            "Switch3229",
            ConfigItem.configTypeBool,
            false
        )
    val switch3230 =
        addConfig(
            "Switch3230",
            ConfigItem.configTypeBool,
            false
        )
    val switch3231 =
        addConfig(
            "Switch3231",
            ConfigItem.configTypeBool,
            false
        )
    val switch3232 =
        addConfig(
            "Switch3232",
            ConfigItem.configTypeBool,
            false
        )
    val switch3233 =
        addConfig(
            "Switch3233",
            ConfigItem.configTypeBool,
            false
        )
    val switch3234 =
        addConfig(
            "Switch3234",
            ConfigItem.configTypeBool,
            false
        )
    val switch3235 =
        addConfig(
            "Switch3235",
            ConfigItem.configTypeBool,
            false
        )
    val switch3236 =
        addConfig(
            "Switch3236",
            ConfigItem.configTypeBool,
            false
        )
    val switch3237 =
        addConfig(
            "Switch3237",
            ConfigItem.configTypeBool,
            false
        )
    val switch3238 =
        addConfig(
            "Switch3238",
            ConfigItem.configTypeBool,
            false
        )
    val switch3239 =
        addConfig(
            "Switch3239",
            ConfigItem.configTypeBool,
            false
        )
    val switch3240 =
        addConfig(
            "Switch3240",
            ConfigItem.configTypeBool,
            false
        )
    val switch3241 =
        addConfig(
            "Switch3241",
            ConfigItem.configTypeBool,
            false
        )
    val switch3242 =
        addConfig(
            "Switch3242",
            ConfigItem.configTypeBool,
            false
        )
    val switch3243 =
        addConfig(
            "Switch3243",
            ConfigItem.configTypeBool,
            false
        )
    val switch3244 =
        addConfig(
            "Switch3244",
            ConfigItem.configTypeBool,
            false
        )
    val switch3245 =
        addConfig(
            "Switch3245",
            ConfigItem.configTypeBool,
            false
        )
    val switch3246 =
        addConfig(
            "Switch3246",
            ConfigItem.configTypeBool,
            false
        )
    val switch3247 =
        addConfig(
            "Switch3247",
            ConfigItem.configTypeBool,
            false
        )
    val switch3248 =
        addConfig(
            "Switch3248",
            ConfigItem.configTypeBool,
            false
        )
    val switch3249 =
        addConfig(
            "Switch3249",
            ConfigItem.configTypeBool,
            false
        )
    val switch3250 =
        addConfig(
            "Switch3250",
            ConfigItem.configTypeBool,
            false
        )
    val switch3251 =
        addConfig(
            "Switch3251",
            ConfigItem.configTypeBool,
            false
        )
    val switch3252 =
        addConfig(
            "Switch3252",
            ConfigItem.configTypeBool,
            false
        )
    val switch3253 =
        addConfig(
            "Switch3253",
            ConfigItem.configTypeBool,
            false
        )
    val switch3254 =
        addConfig(
            "Switch3254",
            ConfigItem.configTypeBool,
            false
        )
    val switch3255 =
        addConfig(
            "Switch3255",
            ConfigItem.configTypeBool,
            false
        )
    val switch3256 =
        addConfig(
            "Switch3256",
            ConfigItem.configTypeBool,
            false
        )
    val switch3257 =
        addConfig(
            "Switch3257",
            ConfigItem.configTypeBool,
            false
        )
    val switch3258 =
        addConfig(
            "Switch3258",
            ConfigItem.configTypeBool,
            false
        )
    val switch3259 =
        addConfig(
            "Switch3259",
            ConfigItem.configTypeBool,
            false
        )
    val switch3260 =
        addConfig(
            "Switch3260",
            ConfigItem.configTypeBool,
            false
        )
    val switch3261 =
        addConfig(
            "Switch3261",
            ConfigItem.configTypeBool,
            false
        )
    val switch3262 =
        addConfig(
            "Switch3262",
            ConfigItem.configTypeBool,
            false
        )
    val switch3263 =
        addConfig(
            "Switch3263",
            ConfigItem.configTypeBool,
            false
        )
    val switch3264 =
        addConfig(
            "Switch3264",
            ConfigItem.configTypeBool,
            false
        )
    val switch3265 =
        addConfig(
            "Switch3265",
            ConfigItem.configTypeBool,
            false
        )
    val switch3266 =
        addConfig(
            "Switch3266",
            ConfigItem.configTypeBool,
            false
        )
    val switch3267 =
        addConfig(
            "Switch3267",
            ConfigItem.configTypeBool,
            false
        )
    val switch3268 =
        addConfig(
            "Switch3268",
            ConfigItem.configTypeBool,
            false
        )
    val switch3269 =
        addConfig(
            "Switch3269",
            ConfigItem.configTypeBool,
            false
        )
    val switch3270 =
        addConfig(
            "Switch3270",
            ConfigItem.configTypeBool,
            false
        )
    val switch3271 =
        addConfig(
            "Switch3271",
            ConfigItem.configTypeBool,
            false
        )
    val switch3272 =
        addConfig(
            "Switch3272",
            ConfigItem.configTypeBool,
            false
        )
    val switch3273 =
        addConfig(
            "Switch3273",
            ConfigItem.configTypeBool,
            false
        )
    val switch3274 =
        addConfig(
            "Switch3274",
            ConfigItem.configTypeBool,
            false
        )
    val switch3275 =
        addConfig(
            "Switch3275",
            ConfigItem.configTypeBool,
            false
        )
    val switch3276 =
        addConfig(
            "Switch3276",
            ConfigItem.configTypeBool,
            false
        )
    val switch3277 =
        addConfig(
            "Switch3277",
            ConfigItem.configTypeBool,
            false
        )
    val switch3278 =
        addConfig(
            "Switch3278",
            ConfigItem.configTypeBool,
            false
        )
    val switch3279 =
        addConfig(
            "Switch3279",
            ConfigItem.configTypeBool,
            false
        )
    val switch3280 =
        addConfig(
            "Switch3280",
            ConfigItem.configTypeBool,
            false
        )
    val switch3281 =
        addConfig(
            "Switch3281",
            ConfigItem.configTypeBool,
            false
        )
    val switch3282 =
        addConfig(
            "Switch3282",
            ConfigItem.configTypeBool,
            false
        )
    val switch3283 =
        addConfig(
            "Switch3283",
            ConfigItem.configTypeBool,
            false
        )
    val switch3284 =
        addConfig(
            "Switch3284",
            ConfigItem.configTypeBool,
            false
        )
    val switch3285 =
        addConfig(
            "Switch3285",
            ConfigItem.configTypeBool,
            false
        )
    val switch3286 =
        addConfig(
            "Switch3286",
            ConfigItem.configTypeBool,
            false
        )
    val switch3287 =
        addConfig(
            "Switch3287",
            ConfigItem.configTypeBool,
            false
        )
    val switch3288 =
        addConfig(
            "Switch3288",
            ConfigItem.configTypeBool,
            false
        )
    val switch3289 =
        addConfig(
            "Switch3289",
            ConfigItem.configTypeBool,
            false
        )
    val switch3290 =
        addConfig(
            "Switch3290",
            ConfigItem.configTypeBool,
            false
        )
    val switch3291 =
        addConfig(
            "Switch3291",
            ConfigItem.configTypeBool,
            false
        )
    val switch3292 =
        addConfig(
            "Switch3292",
            ConfigItem.configTypeBool,
            false
        )
    val switch3293 =
        addConfig(
            "Switch3293",
            ConfigItem.configTypeBool,
            false
        )
    val switch3294 =
        addConfig(
            "Switch3294",
            ConfigItem.configTypeBool,
            false
        )
    val switch3295 =
        addConfig(
            "Switch3295",
            ConfigItem.configTypeBool,
            false
        )
    val switch3296 =
        addConfig(
            "Switch3296",
            ConfigItem.configTypeBool,
            false
        )
    val switch3297 =
        addConfig(
            "Switch3297",
            ConfigItem.configTypeBool,
            false
        )
    val switch3298 =
        addConfig(
            "Switch3298",
            ConfigItem.configTypeBool,
            false
        )
    val switch3299 =
        addConfig(
            "Switch3299",
            ConfigItem.configTypeBool,
            false
        )
    val switch3300 =
        addConfig(
            "Switch3300",
            ConfigItem.configTypeBool,
            false
        )
    val switch3301 =
        addConfig(
            "Switch3301",
            ConfigItem.configTypeBool,
            false
        )
    val switch3302 =
        addConfig(
            "Switch3302",
            ConfigItem.configTypeBool,
            false
        )
    val switch3303 =
        addConfig(
            "Switch3303",
            ConfigItem.configTypeBool,
            false
        )
    val switch3304 =
        addConfig(
            "Switch3304",
            ConfigItem.configTypeBool,
            false
        )
    val switch3305 =
        addConfig(
            "Switch3305",
            ConfigItem.configTypeBool,
            false
        )
    val switch3306 =
        addConfig(
            "Switch3306",
            ConfigItem.configTypeBool,
            false
        )
    val switch3307 =
        addConfig(
            "Switch3307",
            ConfigItem.configTypeBool,
            false
        )
    val switch3308 =
        addConfig(
            "Switch3308",
            ConfigItem.configTypeBool,
            false
        )
    val switch3309 =
        addConfig(
            "Switch3309",
            ConfigItem.configTypeBool,
            false
        )
    val switch3310 =
        addConfig(
            "Switch3310",
            ConfigItem.configTypeBool,
            false
        )
    val switch3311 =
        addConfig(
            "Switch3311",
            ConfigItem.configTypeBool,
            false
        )
    val switch3312 =
        addConfig(
            "Switch3312",
            ConfigItem.configTypeBool,
            false
        )
    val switch3313 =
        addConfig(
            "Switch3313",
            ConfigItem.configTypeBool,
            false
        )
    val switch3314 =
        addConfig(
            "Switch3314",
            ConfigItem.configTypeBool,
            false
        )
    val switch3315 =
        addConfig(
            "Switch3315",
            ConfigItem.configTypeBool,
            false
        )
    val switch3316 =
        addConfig(
            "Switch3316",
            ConfigItem.configTypeBool,
            false
        )
    val switch3317 =
        addConfig(
            "Switch3317",
            ConfigItem.configTypeBool,
            false
        )
    val switch3318 =
        addConfig(
            "Switch3318",
            ConfigItem.configTypeBool,
            false
        )
    val switch3319 =
        addConfig(
            "Switch3319",
            ConfigItem.configTypeBool,
            false
        )
    val switch3320 =
        addConfig(
            "Switch3320",
            ConfigItem.configTypeBool,
            false
        )
    val switch3321 =
        addConfig(
            "Switch3321",
            ConfigItem.configTypeBool,
            false
        )
    val switch3322 =
        addConfig(
            "Switch3322",
            ConfigItem.configTypeBool,
            false
        )
    val switch3323 =
        addConfig(
            "Switch3323",
            ConfigItem.configTypeBool,
            false
        )
    val switch3324 =
        addConfig(
            "Switch3324",
            ConfigItem.configTypeBool,
            false
        )
    val switch3325 =
        addConfig(
            "Switch3325",
            ConfigItem.configTypeBool,
            false
        )
    val switch3326 =
        addConfig(
            "Switch3326",
            ConfigItem.configTypeBool,
            false
        )
    val switch3327 =
        addConfig(
            "Switch3327",
            ConfigItem.configTypeBool,
            false
        )
    val switch3328 =
        addConfig(
            "Switch3328",
            ConfigItem.configTypeBool,
            false
        )
    val switch3329 =
        addConfig(
            "Switch3329",
            ConfigItem.configTypeBool,
            false
        )
    val switch3330 =
        addConfig(
            "Switch3330",
            ConfigItem.configTypeBool,
            false
        )
    val switch3331 =
        addConfig(
            "Switch3331",
            ConfigItem.configTypeBool,
            false
        )
    val switch3332 =
        addConfig(
            "Switch3332",
            ConfigItem.configTypeBool,
            false
        )
    val switch3333 =
        addConfig(
            "Switch3333",
            ConfigItem.configTypeBool,
            false
        )
    val switch3334 =
        addConfig(
            "Switch3334",
            ConfigItem.configTypeBool,
            false
        )
    val switch3335 =
        addConfig(
            "Switch3335",
            ConfigItem.configTypeBool,
            false
        )
    val switch3336 =
        addConfig(
            "Switch3336",
            ConfigItem.configTypeBool,
            false
        )
    val switch3337 =
        addConfig(
            "Switch3337",
            ConfigItem.configTypeBool,
            false
        )
    val switch3338 =
        addConfig(
            "Switch3338",
            ConfigItem.configTypeBool,
            false
        )
    val switch3339 =
        addConfig(
            "Switch3339",
            ConfigItem.configTypeBool,
            false
        )
    val switch3340 =
        addConfig(
            "Switch3340",
            ConfigItem.configTypeBool,
            false
        )
    val switch3341 =
        addConfig(
            "Switch3341",
            ConfigItem.configTypeBool,
            false
        )
    val switch3342 =
        addConfig(
            "Switch3342",
            ConfigItem.configTypeBool,
            false
        )
    val switch3343 =
        addConfig(
            "Switch3343",
            ConfigItem.configTypeBool,
            false
        )
    val switch3344 =
        addConfig(
            "Switch3344",
            ConfigItem.configTypeBool,
            false
        )
    val switch3345 =
        addConfig(
            "Switch3345",
            ConfigItem.configTypeBool,
            false
        )
    val switch3346 =
        addConfig(
            "Switch3346",
            ConfigItem.configTypeBool,
            false
        )
    val switch3347 =
        addConfig(
            "Switch3347",
            ConfigItem.configTypeBool,
            false
        )
    val switch3348 =
        addConfig(
            "Switch3348",
            ConfigItem.configTypeBool,
            false
        )
    val switch3349 =
        addConfig(
            "Switch3349",
            ConfigItem.configTypeBool,
            false
        )
    val switch3350 =
        addConfig(
            "Switch3350",
            ConfigItem.configTypeBool,
            false
        )
    val switch3351 =
        addConfig(
            "Switch3351",
            ConfigItem.configTypeBool,
            false
        )
    val switch3352 =
        addConfig(
            "Switch3352",
            ConfigItem.configTypeBool,
            false
        )
    val switch3353 =
        addConfig(
            "Switch3353",
            ConfigItem.configTypeBool,
            false
        )
    val switch3354 =
        addConfig(
            "Switch3354",
            ConfigItem.configTypeBool,
            false
        )
    val switch3355 =
        addConfig(
            "Switch3355",
            ConfigItem.configTypeBool,
            false
        )
    val switch3356 =
        addConfig(
            "Switch3356",
            ConfigItem.configTypeBool,
            false
        )
    val switch3357 =
        addConfig(
            "Switch3357",
            ConfigItem.configTypeBool,
            false
        )
    val switch3358 =
        addConfig(
            "Switch3358",
            ConfigItem.configTypeBool,
            false
        )
    val switch3359 =
        addConfig(
            "Switch3359",
            ConfigItem.configTypeBool,
            false
        )
    val switch3360 =
        addConfig(
            "Switch3360",
            ConfigItem.configTypeBool,
            false
        )
    val switch3361 =
        addConfig(
            "Switch3361",
            ConfigItem.configTypeBool,
            false
        )
    val switch3362 =
        addConfig(
            "Switch3362",
            ConfigItem.configTypeBool,
            false
        )
    val switch3363 =
        addConfig(
            "Switch3363",
            ConfigItem.configTypeBool,
            false
        )
    val switch3364 =
        addConfig(
            "Switch3364",
            ConfigItem.configTypeBool,
            false
        )
    val switch3365 =
        addConfig(
            "Switch3365",
            ConfigItem.configTypeBool,
            false
        )
    val switch3366 =
        addConfig(
            "Switch3366",
            ConfigItem.configTypeBool,
            false
        )
    val switch3367 =
        addConfig(
            "Switch3367",
            ConfigItem.configTypeBool,
            false
        )
    val switch3368 =
        addConfig(
            "Switch3368",
            ConfigItem.configTypeBool,
            false
        )
    val switch3369 =
        addConfig(
            "Switch3369",
            ConfigItem.configTypeBool,
            false
        )
    val switch3370 =
        addConfig(
            "Switch3370",
            ConfigItem.configTypeBool,
            false
        )
    val switch3371 =
        addConfig(
            "Switch3371",
            ConfigItem.configTypeBool,
            false
        )
    val switch3372 =
        addConfig(
            "Switch3372",
            ConfigItem.configTypeBool,
            false
        )
    val switch3373 =
        addConfig(
            "Switch3373",
            ConfigItem.configTypeBool,
            false
        )
    val switch3374 =
        addConfig(
            "Switch3374",
            ConfigItem.configTypeBool,
            false
        )
    val switch3375 =
        addConfig(
            "Switch3375",
            ConfigItem.configTypeBool,
            false
        )
    val switch3376 =
        addConfig(
            "Switch3376",
            ConfigItem.configTypeBool,
            false
        )
    val switch3377 =
        addConfig(
            "Switch3377",
            ConfigItem.configTypeBool,
            false
        )
    val switch3378 =
        addConfig(
            "Switch3378",
            ConfigItem.configTypeBool,
            false
        )
    val switch3379 =
        addConfig(
            "Switch3379",
            ConfigItem.configTypeBool,
            false
        )
    val switch3380 =
        addConfig(
            "Switch3380",
            ConfigItem.configTypeBool,
            false
        )
    val switch3381 =
        addConfig(
            "Switch3381",
            ConfigItem.configTypeBool,
            false
        )
    val switch3382 =
        addConfig(
            "Switch3382",
            ConfigItem.configTypeBool,
            false
        )
    val switch3383 =
        addConfig(
            "Switch3383",
            ConfigItem.configTypeBool,
            false
        )
    val switch3384 =
        addConfig(
            "Switch3384",
            ConfigItem.configTypeBool,
            false
        )
    val switch3385 =
        addConfig(
            "Switch3385",
            ConfigItem.configTypeBool,
            false
        )
    val switch3386 =
        addConfig(
            "Switch3386",
            ConfigItem.configTypeBool,
            false
        )
    val switch3387 =
        addConfig(
            "Switch3387",
            ConfigItem.configTypeBool,
            false
        )
    val switch3388 =
        addConfig(
            "Switch3388",
            ConfigItem.configTypeBool,
            false
        )
    val switch3389 =
        addConfig(
            "Switch3389",
            ConfigItem.configTypeBool,
            false
        )
    val switch3390 =
        addConfig(
            "Switch3390",
            ConfigItem.configTypeBool,
            false
        )
    val switch3391 =
        addConfig(
            "Switch3391",
            ConfigItem.configTypeBool,
            false
        )
    val switch3392 =
        addConfig(
            "Switch3392",
            ConfigItem.configTypeBool,
            false
        )
    val switch3393 =
        addConfig(
            "Switch3393",
            ConfigItem.configTypeBool,
            false
        )
    val switch3394 =
        addConfig(
            "Switch3394",
            ConfigItem.configTypeBool,
            false
        )
    val switch3395 =
        addConfig(
            "Switch3395",
            ConfigItem.configTypeBool,
            false
        )
    val switch3396 =
        addConfig(
            "Switch3396",
            ConfigItem.configTypeBool,
            false
        )
    val switch3397 =
        addConfig(
            "Switch3397",
            ConfigItem.configTypeBool,
            false
        )
    val switch3398 =
        addConfig(
            "Switch3398",
            ConfigItem.configTypeBool,
            false
        )
    val switch3399 =
        addConfig(
            "Switch3399",
            ConfigItem.configTypeBool,
            false
        )
    val switch3400 =
        addConfig(
            "Switch3400",
            ConfigItem.configTypeBool,
            false
        )
    val switch3401 =
        addConfig(
            "Switch3401",
            ConfigItem.configTypeBool,
            false
        )
    val switch3402 =
        addConfig(
            "Switch3402",
            ConfigItem.configTypeBool,
            false
        )
    val switch3403 =
        addConfig(
            "Switch3403",
            ConfigItem.configTypeBool,
            false
        )
    val switch3404 =
        addConfig(
            "Switch3404",
            ConfigItem.configTypeBool,
            false
        )
    val switch3405 =
        addConfig(
            "Switch3405",
            ConfigItem.configTypeBool,
            false
        )
    val switch3406 =
        addConfig(
            "Switch3406",
            ConfigItem.configTypeBool,
            false
        )
    val switch3407 =
        addConfig(
            "Switch3407",
            ConfigItem.configTypeBool,
            false
        )
    val switch3408 =
        addConfig(
            "Switch3408",
            ConfigItem.configTypeBool,
            false
        )
    val switch3409 =
        addConfig(
            "Switch3409",
            ConfigItem.configTypeBool,
            false
        )
    val switch3410 =
        addConfig(
            "Switch3410",
            ConfigItem.configTypeBool,
            false
        )
    val switch3411 =
        addConfig(
            "Switch3411",
            ConfigItem.configTypeBool,
            false
        )
    val switch3412 =
        addConfig(
            "Switch3412",
            ConfigItem.configTypeBool,
            false
        )
    val switch3413 =
        addConfig(
            "Switch3413",
            ConfigItem.configTypeBool,
            false
        )
    val switch3414 =
        addConfig(
            "Switch3414",
            ConfigItem.configTypeBool,
            false
        )
    val switch3415 =
        addConfig(
            "Switch3415",
            ConfigItem.configTypeBool,
            false
        )
    val switch3416 =
        addConfig(
            "Switch3416",
            ConfigItem.configTypeBool,
            false
        )
    val switch3417 =
        addConfig(
            "Switch3417",
            ConfigItem.configTypeBool,
            false
        )
    val switch3418 =
        addConfig(
            "Switch3418",
            ConfigItem.configTypeBool,
            false
        )
    val switch3419 =
        addConfig(
            "Switch3419",
            ConfigItem.configTypeBool,
            false
        )
    val switch3420 =
        addConfig(
            "Switch3420",
            ConfigItem.configTypeBool,
            false
        )
    val switch3421 =
        addConfig(
            "Switch3421",
            ConfigItem.configTypeBool,
            false
        )
    val switch3422 =
        addConfig(
            "Switch3422",
            ConfigItem.configTypeBool,
            false
        )
    val switch3423 =
        addConfig(
            "Switch3423",
            ConfigItem.configTypeBool,
            false
        )
    val switch3424 =
        addConfig(
            "Switch3424",
            ConfigItem.configTypeBool,
            false
        )
    val switch3425 =
        addConfig(
            "Switch3425",
            ConfigItem.configTypeBool,
            false
        )
    val switch3426 =
        addConfig(
            "Switch3426",
            ConfigItem.configTypeBool,
            false
        )
    val switch3427 =
        addConfig(
            "Switch3427",
            ConfigItem.configTypeBool,
            false
        )
    val switch3428 =
        addConfig(
            "Switch3428",
            ConfigItem.configTypeBool,
            false
        )
    val switch3429 =
        addConfig(
            "Switch3429",
            ConfigItem.configTypeBool,
            false
        )
    val switch3430 =
        addConfig(
            "Switch3430",
            ConfigItem.configTypeBool,
            false
        )
    val switch3431 =
        addConfig(
            "Switch3431",
            ConfigItem.configTypeBool,
            false
        )
    val switch3432 =
        addConfig(
            "Switch3432",
            ConfigItem.configTypeBool,
            false
        )
    val switch3433 =
        addConfig(
            "Switch3433",
            ConfigItem.configTypeBool,
            false
        )
    val switch3434 =
        addConfig(
            "Switch3434",
            ConfigItem.configTypeBool,
            false
        )
    val switch3435 =
        addConfig(
            "Switch3435",
            ConfigItem.configTypeBool,
            false
        )
    val switch3436 =
        addConfig(
            "Switch3436",
            ConfigItem.configTypeBool,
            false
        )
    val switch3437 =
        addConfig(
            "Switch3437",
            ConfigItem.configTypeBool,
            false
        )
    val switch3438 =
        addConfig(
            "Switch3438",
            ConfigItem.configTypeBool,
            false
        )
    val switch3439 =
        addConfig(
            "Switch3439",
            ConfigItem.configTypeBool,
            false
        )
    val switch3440 =
        addConfig(
            "Switch3440",
            ConfigItem.configTypeBool,
            false
        )
    val switch3441 =
        addConfig(
            "Switch3441",
            ConfigItem.configTypeBool,
            false
        )
    val switch3442 =
        addConfig(
            "Switch3442",
            ConfigItem.configTypeBool,
            false
        )
    val switch3443 =
        addConfig(
            "Switch3443",
            ConfigItem.configTypeBool,
            false
        )
    val switch3444 =
        addConfig(
            "Switch3444",
            ConfigItem.configTypeBool,
            false
        )
    val switch3445 =
        addConfig(
            "Switch3445",
            ConfigItem.configTypeBool,
            false
        )
    val switch3446 =
        addConfig(
            "Switch3446",
            ConfigItem.configTypeBool,
            false
        )
    val switch3447 =
        addConfig(
            "Switch3447",
            ConfigItem.configTypeBool,
            false
        )
    val switch3448 =
        addConfig(
            "Switch3448",
            ConfigItem.configTypeBool,
            false
        )
    val switch3449 =
        addConfig(
            "Switch3449",
            ConfigItem.configTypeBool,
            false
        )
    val switch3450 =
        addConfig(
            "Switch3450",
            ConfigItem.configTypeBool,
            false
        )
    val switch3451 =
        addConfig(
            "Switch3451",
            ConfigItem.configTypeBool,
            false
        )
    val switch3452 =
        addConfig(
            "Switch3452",
            ConfigItem.configTypeBool,
            false
        )
    val switch3453 =
        addConfig(
            "Switch3453",
            ConfigItem.configTypeBool,
            false
        )
    val switch3454 =
        addConfig(
            "Switch3454",
            ConfigItem.configTypeBool,
            false
        )
    val switch3455 =
        addConfig(
            "Switch3455",
            ConfigItem.configTypeBool,
            false
        )
    val switch3456 =
        addConfig(
            "Switch3456",
            ConfigItem.configTypeBool,
            false
        )
    val switch3457 =
        addConfig(
            "Switch3457",
            ConfigItem.configTypeBool,
            false
        )
    val switch3458 =
        addConfig(
            "Switch3458",
            ConfigItem.configTypeBool,
            false
        )
    val switch3459 =
        addConfig(
            "Switch3459",
            ConfigItem.configTypeBool,
            false
        )
    val switch3460 =
        addConfig(
            "Switch3460",
            ConfigItem.configTypeBool,
            false
        )
    val switch3461 =
        addConfig(
            "Switch3461",
            ConfigItem.configTypeBool,
            false
        )
    val switch3462 =
        addConfig(
            "Switch3462",
            ConfigItem.configTypeBool,
            false
        )
    val switch3463 =
        addConfig(
            "Switch3463",
            ConfigItem.configTypeBool,
            false
        )
    val switch3464 =
        addConfig(
            "Switch3464",
            ConfigItem.configTypeBool,
            false
        )
    val switch3465 =
        addConfig(
            "Switch3465",
            ConfigItem.configTypeBool,
            false
        )
    val switch3466 =
        addConfig(
            "Switch3466",
            ConfigItem.configTypeBool,
            false
        )
    val switch3467 =
        addConfig(
            "Switch3467",
            ConfigItem.configTypeBool,
            false
        )
    val switch3468 =
        addConfig(
            "Switch3468",
            ConfigItem.configTypeBool,
            false
        )
    val switch3469 =
        addConfig(
            "Switch3469",
            ConfigItem.configTypeBool,
            false
        )
    val switch3470 =
        addConfig(
            "Switch3470",
            ConfigItem.configTypeBool,
            false
        )
    val switch3471 =
        addConfig(
            "Switch3471",
            ConfigItem.configTypeBool,
            false
        )
    val switch3472 =
        addConfig(
            "Switch3472",
            ConfigItem.configTypeBool,
            false
        )
    val switch3473 =
        addConfig(
            "Switch3473",
            ConfigItem.configTypeBool,
            false
        )
    val switch3474 =
        addConfig(
            "Switch3474",
            ConfigItem.configTypeBool,
            false
        )
    val switch3475 =
        addConfig(
            "Switch3475",
            ConfigItem.configTypeBool,
            false
        )
    val switch3476 =
        addConfig(
            "Switch3476",
            ConfigItem.configTypeBool,
            false
        )
    val switch3477 =
        addConfig(
            "Switch3477",
            ConfigItem.configTypeBool,
            false
        )
    val switch3478 =
        addConfig(
            "Switch3478",
            ConfigItem.configTypeBool,
            false
        )
    val switch3479 =
        addConfig(
            "Switch3479",
            ConfigItem.configTypeBool,
            false
        )
    val switch3480 =
        addConfig(
            "Switch3480",
            ConfigItem.configTypeBool,
            false
        )
    val switch3481 =
        addConfig(
            "Switch3481",
            ConfigItem.configTypeBool,
            false
        )
    val switch3482 =
        addConfig(
            "Switch3482",
            ConfigItem.configTypeBool,
            false
        )
    val switch3483 =
        addConfig(
            "Switch3483",
            ConfigItem.configTypeBool,
            false
        )
    val switch3484 =
        addConfig(
            "Switch3484",
            ConfigItem.configTypeBool,
            false
        )
    val switch3485 =
        addConfig(
            "Switch3485",
            ConfigItem.configTypeBool,
            false
        )
    val switch3486 =
        addConfig(
            "Switch3486",
            ConfigItem.configTypeBool,
            false
        )
    val switch3487 =
        addConfig(
            "Switch3487",
            ConfigItem.configTypeBool,
            false
        )
    val switch3488 =
        addConfig(
            "Switch3488",
            ConfigItem.configTypeBool,
            false
        )
    val switch3489 =
        addConfig(
            "Switch3489",
            ConfigItem.configTypeBool,
            false
        )
    val switch3490 =
        addConfig(
            "Switch3490",
            ConfigItem.configTypeBool,
            false
        )
    val switch3491 =
        addConfig(
            "Switch3491",
            ConfigItem.configTypeBool,
            false
        )
    val switch3492 =
        addConfig(
            "Switch3492",
            ConfigItem.configTypeBool,
            false
        )
    val switch3493 =
        addConfig(
            "Switch3493",
            ConfigItem.configTypeBool,
            false
        )
    val switch3494 =
        addConfig(
            "Switch3494",
            ConfigItem.configTypeBool,
            false
        )
    val switch3495 =
        addConfig(
            "Switch3495",
            ConfigItem.configTypeBool,
            false
        )
    val switch3496 =
        addConfig(
            "Switch3496",
            ConfigItem.configTypeBool,
            false
        )
    val switch3497 =
        addConfig(
            "Switch3497",
            ConfigItem.configTypeBool,
            false
        )
    val switch3498 =
        addConfig(
            "Switch3498",
            ConfigItem.configTypeBool,
            false
        )
    val switch3499 =
        addConfig(
            "Switch3499",
            ConfigItem.configTypeBool,
            false
        )
    val switch3500 =
        addConfig(
            "Switch3500",
            ConfigItem.configTypeBool,
            false
        )
    val switch3501 =
        addConfig(
            "Switch3501",
            ConfigItem.configTypeBool,
            false
        )
    val switch3502 =
        addConfig(
            "Switch3502",
            ConfigItem.configTypeBool,
            false
        )
    val switch3503 =
        addConfig(
            "Switch3503",
            ConfigItem.configTypeBool,
            false
        )
    val switch3504 =
        addConfig(
            "Switch3504",
            ConfigItem.configTypeBool,
            false
        )
    val switch3505 =
        addConfig(
            "Switch3505",
            ConfigItem.configTypeBool,
            false
        )
    val switch3506 =
        addConfig(
            "Switch3506",
            ConfigItem.configTypeBool,
            false
        )
    val switch3507 =
        addConfig(
            "Switch3507",
            ConfigItem.configTypeBool,
            false
        )
    val switch3508 =
        addConfig(
            "Switch3508",
            ConfigItem.configTypeBool,
            false
        )
    val switch3509 =
        addConfig(
            "Switch3509",
            ConfigItem.configTypeBool,
            false
        )
    val switch3510 =
        addConfig(
            "Switch3510",
            ConfigItem.configTypeBool,
            false
        )
    val switch3511 =
        addConfig(
            "Switch3511",
            ConfigItem.configTypeBool,
            false
        )
    val switch3512 =
        addConfig(
            "Switch3512",
            ConfigItem.configTypeBool,
            false
        )
    val switch3513 =
        addConfig(
            "Switch3513",
            ConfigItem.configTypeBool,
            false
        )
    val switch3514 =
        addConfig(
            "Switch3514",
            ConfigItem.configTypeBool,
            false
        )
    val switch3515 =
        addConfig(
            "Switch3515",
            ConfigItem.configTypeBool,
            false
        )
    val switch3516 =
        addConfig(
            "Switch3516",
            ConfigItem.configTypeBool,
            false
        )
    val switch3517 =
        addConfig(
            "Switch3517",
            ConfigItem.configTypeBool,
            false
        )
    val switch3518 =
        addConfig(
            "Switch3518",
            ConfigItem.configTypeBool,
            false
        )
    val switch3519 =
        addConfig(
            "Switch3519",
            ConfigItem.configTypeBool,
            false
        )
    val switch3520 =
        addConfig(
            "Switch3520",
            ConfigItem.configTypeBool,
            false
        )
    val switch3521 =
        addConfig(
            "Switch3521",
            ConfigItem.configTypeBool,
            false
        )
    val switch3522 =
        addConfig(
            "Switch3522",
            ConfigItem.configTypeBool,
            false
        )
    val switch3523 =
        addConfig(
            "Switch3523",
            ConfigItem.configTypeBool,
            false
        )
    val switch3524 =
        addConfig(
            "Switch3524",
            ConfigItem.configTypeBool,
            false
        )
    val switch3525 =
        addConfig(
            "Switch3525",
            ConfigItem.configTypeBool,
            false
        )
    val switch3526 =
        addConfig(
            "Switch3526",
            ConfigItem.configTypeBool,
            false
        )
    val switch3527 =
        addConfig(
            "Switch3527",
            ConfigItem.configTypeBool,
            false
        )
    val switch3528 =
        addConfig(
            "Switch3528",
            ConfigItem.configTypeBool,
            false
        )
    val switch3529 =
        addConfig(
            "Switch3529",
            ConfigItem.configTypeBool,
            false
        )
    val switch3530 =
        addConfig(
            "Switch3530",
            ConfigItem.configTypeBool,
            false
        )
    val switch3531 =
        addConfig(
            "Switch3531",
            ConfigItem.configTypeBool,
            false
        )
    val switch3532 =
        addConfig(
            "Switch3532",
            ConfigItem.configTypeBool,
            false
        )
    val switch3533 =
        addConfig(
            "Switch3533",
            ConfigItem.configTypeBool,
            false
        )
    val switch3534 =
        addConfig(
            "Switch3534",
            ConfigItem.configTypeBool,
            false
        )
    val switch3535 =
        addConfig(
            "Switch3535",
            ConfigItem.configTypeBool,
            false
        )
    val switch3536 =
        addConfig(
            "Switch3536",
            ConfigItem.configTypeBool,
            false
        )
    val switch3537 =
        addConfig(
            "Switch3537",
            ConfigItem.configTypeBool,
            false
        )
    val switch3538 =
        addConfig(
            "Switch3538",
            ConfigItem.configTypeBool,
            false
        )
    val switch3539 =
        addConfig(
            "Switch3539",
            ConfigItem.configTypeBool,
            false
        )
    val switch3540 =
        addConfig(
            "Switch3540",
            ConfigItem.configTypeBool,
            false
        )
    val switch3541 =
        addConfig(
            "Switch3541",
            ConfigItem.configTypeBool,
            false
        )
    val switch3542 =
        addConfig(
            "Switch3542",
            ConfigItem.configTypeBool,
            false
        )
    val switch3543 =
        addConfig(
            "Switch3543",
            ConfigItem.configTypeBool,
            false
        )
    val switch3544 =
        addConfig(
            "Switch3544",
            ConfigItem.configTypeBool,
            false
        )
    val switch3545 =
        addConfig(
            "Switch3545",
            ConfigItem.configTypeBool,
            false
        )
    val switch3546 =
        addConfig(
            "Switch3546",
            ConfigItem.configTypeBool,
            false
        )
    val switch3547 =
        addConfig(
            "Switch3547",
            ConfigItem.configTypeBool,
            false
        )
    val switch3548 =
        addConfig(
            "Switch3548",
            ConfigItem.configTypeBool,
            false
        )
    val switch3549 =
        addConfig(
            "Switch3549",
            ConfigItem.configTypeBool,
            false
        )
    val switch3550 =
        addConfig(
            "Switch3550",
            ConfigItem.configTypeBool,
            false
        )
    val switch3551 =
        addConfig(
            "Switch3551",
            ConfigItem.configTypeBool,
            false
        )
    val switch3552 =
        addConfig(
            "Switch3552",
            ConfigItem.configTypeBool,
            false
        )
    val switch3553 =
        addConfig(
            "Switch3553",
            ConfigItem.configTypeBool,
            false
        )
    val switch3554 =
        addConfig(
            "Switch3554",
            ConfigItem.configTypeBool,
            false
        )
    val switch3555 =
        addConfig(
            "Switch3555",
            ConfigItem.configTypeBool,
            false
        )
    val switch3556 =
        addConfig(
            "Switch3556",
            ConfigItem.configTypeBool,
            false
        )
    val switch3557 =
        addConfig(
            "Switch3557",
            ConfigItem.configTypeBool,
            false
        )
    val switch3558 =
        addConfig(
            "Switch3558",
            ConfigItem.configTypeBool,
            false
        )
    val switch3559 =
        addConfig(
            "Switch3559",
            ConfigItem.configTypeBool,
            false
        )
    val switch3560 =
        addConfig(
            "Switch3560",
            ConfigItem.configTypeBool,
            false
        )
    val switch3561 =
        addConfig(
            "Switch3561",
            ConfigItem.configTypeBool,
            false
        )
    val switch3562 =
        addConfig(
            "Switch3562",
            ConfigItem.configTypeBool,
            false
        )
    val switch3563 =
        addConfig(
            "Switch3563",
            ConfigItem.configTypeBool,
            false
        )
    val switch3564 =
        addConfig(
            "Switch3564",
            ConfigItem.configTypeBool,
            false
        )
    val switch3565 =
        addConfig(
            "Switch3565",
            ConfigItem.configTypeBool,
            false
        )
    val switch3566 =
        addConfig(
            "Switch3566",
            ConfigItem.configTypeBool,
            false
        )
    val switch3567 =
        addConfig(
            "Switch3567",
            ConfigItem.configTypeBool,
            false
        )
    val switch3568 =
        addConfig(
            "Switch3568",
            ConfigItem.configTypeBool,
            false
        )
    val switch3569 =
        addConfig(
            "Switch3569",
            ConfigItem.configTypeBool,
            false
        )
    val switch3570 =
        addConfig(
            "Switch3570",
            ConfigItem.configTypeBool,
            false
        )
    val switch3571 =
        addConfig(
            "Switch3571",
            ConfigItem.configTypeBool,
            false
        )
    val switch3572 =
        addConfig(
            "Switch3572",
            ConfigItem.configTypeBool,
            false
        )
    val switch3573 =
        addConfig(
            "Switch3573",
            ConfigItem.configTypeBool,
            false
        )
    val switch3574 =
        addConfig(
            "Switch3574",
            ConfigItem.configTypeBool,
            false
        )
    val switch3575 =
        addConfig(
            "Switch3575",
            ConfigItem.configTypeBool,
            false
        )
    val switch3576 =
        addConfig(
            "Switch3576",
            ConfigItem.configTypeBool,
            false
        )
    val switch3577 =
        addConfig(
            "Switch3577",
            ConfigItem.configTypeBool,
            false
        )
    val switch3578 =
        addConfig(
            "Switch3578",
            ConfigItem.configTypeBool,
            false
        )
    val switch3579 =
        addConfig(
            "Switch3579",
            ConfigItem.configTypeBool,
            false
        )
    val switch3580 =
        addConfig(
            "Switch3580",
            ConfigItem.configTypeBool,
            false
        )
    val switch3581 =
        addConfig(
            "Switch3581",
            ConfigItem.configTypeBool,
            false
        )
    val switch3582 =
        addConfig(
            "Switch3582",
            ConfigItem.configTypeBool,
            false
        )
    val switch3583 =
        addConfig(
            "Switch3583",
            ConfigItem.configTypeBool,
            false
        )
    val switch3584 =
        addConfig(
            "Switch3584",
            ConfigItem.configTypeBool,
            false
        )
    val switch3585 =
        addConfig(
            "Switch3585",
            ConfigItem.configTypeBool,
            false
        )
    val switch3586 =
        addConfig(
            "Switch3586",
            ConfigItem.configTypeBool,
            false
        )
    val switch3587 =
        addConfig(
            "Switch3587",
            ConfigItem.configTypeBool,
            false
        )
    val switch3588 =
        addConfig(
            "Switch3588",
            ConfigItem.configTypeBool,
            false
        )
    val switch3589 =
        addConfig(
            "Switch3589",
            ConfigItem.configTypeBool,
            false
        )
    val switch3590 =
        addConfig(
            "Switch3590",
            ConfigItem.configTypeBool,
            false
        )
    val switch3591 =
        addConfig(
            "Switch3591",
            ConfigItem.configTypeBool,
            false
        )
    val switch3592 =
        addConfig(
            "Switch3592",
            ConfigItem.configTypeBool,
            false
        )
    val switch3593 =
        addConfig(
            "Switch3593",
            ConfigItem.configTypeBool,
            false
        )
    val switch3594 =
        addConfig(
            "Switch3594",
            ConfigItem.configTypeBool,
            false
        )
    val switch3595 =
        addConfig(
            "Switch3595",
            ConfigItem.configTypeBool,
            false
        )
    val switch3596 =
        addConfig(
            "Switch3596",
            ConfigItem.configTypeBool,
            false
        )
    val switch3597 =
        addConfig(
            "Switch3597",
            ConfigItem.configTypeBool,
            false
        )
    val switch3598 =
        addConfig(
            "Switch3598",
            ConfigItem.configTypeBool,
            false
        )
    val switch3599 =
        addConfig(
            "Switch3599",
            ConfigItem.configTypeBool,
            false
        )
    val switch3600 =
        addConfig(
            "Switch3600",
            ConfigItem.configTypeBool,
            false
        )
    val switch3601 =
        addConfig(
            "Switch3601",
            ConfigItem.configTypeBool,
            false
        )
    val switch3602 =
        addConfig(
            "Switch3602",
            ConfigItem.configTypeBool,
            false
        )
    val switch3603 =
        addConfig(
            "Switch3603",
            ConfigItem.configTypeBool,
            false
        )
    val switch3604 =
        addConfig(
            "Switch3604",
            ConfigItem.configTypeBool,
            false
        )
    val switch3605 =
        addConfig(
            "Switch3605",
            ConfigItem.configTypeBool,
            false
        )
    val switch3606 =
        addConfig(
            "Switch3606",
            ConfigItem.configTypeBool,
            false
        )
    val switch3607 =
        addConfig(
            "Switch3607",
            ConfigItem.configTypeBool,
            false
        )
    val switch3608 =
        addConfig(
            "Switch3608",
            ConfigItem.configTypeBool,
            false
        )
    val switch3609 =
        addConfig(
            "Switch3609",
            ConfigItem.configTypeBool,
            false
        )
    val switch3610 =
        addConfig(
            "Switch3610",
            ConfigItem.configTypeBool,
            false
        )
    val switch3611 =
        addConfig(
            "Switch3611",
            ConfigItem.configTypeBool,
            false
        )
    val switch3612 =
        addConfig(
            "Switch3612",
            ConfigItem.configTypeBool,
            false
        )
    val switch3613 =
        addConfig(
            "Switch3613",
            ConfigItem.configTypeBool,
            false
        )
    val switch3614 =
        addConfig(
            "Switch3614",
            ConfigItem.configTypeBool,
            false
        )
    val switch3615 =
        addConfig(
            "Switch3615",
            ConfigItem.configTypeBool,
            false
        )
    val switch3616 =
        addConfig(
            "Switch3616",
            ConfigItem.configTypeBool,
            false
        )
    val switch3617 =
        addConfig(
            "Switch3617",
            ConfigItem.configTypeBool,
            false
        )
    val switch3618 =
        addConfig(
            "Switch3618",
            ConfigItem.configTypeBool,
            false
        )
    val switch3619 =
        addConfig(
            "Switch3619",
            ConfigItem.configTypeBool,
            false
        )
    val switch3620 =
        addConfig(
            "Switch3620",
            ConfigItem.configTypeBool,
            false
        )
    val switch3621 =
        addConfig(
            "Switch3621",
            ConfigItem.configTypeBool,
            false
        )
    val switch3622 =
        addConfig(
            "Switch3622",
            ConfigItem.configTypeBool,
            false
        )
    val switch3623 =
        addConfig(
            "Switch3623",
            ConfigItem.configTypeBool,
            false
        )
    val switch3624 =
        addConfig(
            "Switch3624",
            ConfigItem.configTypeBool,
            false
        )
    val switch3625 =
        addConfig(
            "Switch3625",
            ConfigItem.configTypeBool,
            false
        )
    val switch3626 =
        addConfig(
            "Switch3626",
            ConfigItem.configTypeBool,
            false
        )
    val switch3627 =
        addConfig(
            "Switch3627",
            ConfigItem.configTypeBool,
            false
        )
    val switch3628 =
        addConfig(
            "Switch3628",
            ConfigItem.configTypeBool,
            false
        )
    val switch3629 =
        addConfig(
            "Switch3629",
            ConfigItem.configTypeBool,
            false
        )
    val switch3630 =
        addConfig(
            "Switch3630",
            ConfigItem.configTypeBool,
            false
        )
    val switch3631 =
        addConfig(
            "Switch3631",
            ConfigItem.configTypeBool,
            false
        )
    val switch3632 =
        addConfig(
            "Switch3632",
            ConfigItem.configTypeBool,
            false
        )
    val switch3633 =
        addConfig(
            "Switch3633",
            ConfigItem.configTypeBool,
            false
        )
    val switch3634 =
        addConfig(
            "Switch3634",
            ConfigItem.configTypeBool,
            false
        )
    val switch3635 =
        addConfig(
            "Switch3635",
            ConfigItem.configTypeBool,
            false
        )
    val switch3636 =
        addConfig(
            "Switch3636",
            ConfigItem.configTypeBool,
            false
        )
    val switch3637 =
        addConfig(
            "Switch3637",
            ConfigItem.configTypeBool,
            false
        )
    val switch3638 =
        addConfig(
            "Switch3638",
            ConfigItem.configTypeBool,
            false
        )
    val switch3639 =
        addConfig(
            "Switch3639",
            ConfigItem.configTypeBool,
            false
        )
    val switch3640 =
        addConfig(
            "Switch3640",
            ConfigItem.configTypeBool,
            false
        )
    val switch3641 =
        addConfig(
            "Switch3641",
            ConfigItem.configTypeBool,
            false
        )
    val switch3642 =
        addConfig(
            "Switch3642",
            ConfigItem.configTypeBool,
            false
        )
    val switch3643 =
        addConfig(
            "Switch3643",
            ConfigItem.configTypeBool,
            false
        )
    val switch3644 =
        addConfig(
            "Switch3644",
            ConfigItem.configTypeBool,
            false
        )
    val switch3645 =
        addConfig(
            "Switch3645",
            ConfigItem.configTypeBool,
            false
        )
    val switch3646 =
        addConfig(
            "Switch3646",
            ConfigItem.configTypeBool,
            false
        )
    val switch3647 =
        addConfig(
            "Switch3647",
            ConfigItem.configTypeBool,
            false
        )
    val switch3648 =
        addConfig(
            "Switch3648",
            ConfigItem.configTypeBool,
            false
        )
    val switch3649 =
        addConfig(
            "Switch3649",
            ConfigItem.configTypeBool,
            false
        )
    val switch3650 =
        addConfig(
            "Switch3650",
            ConfigItem.configTypeBool,
            false
        )
    val switch3651 =
        addConfig(
            "Switch3651",
            ConfigItem.configTypeBool,
            false
        )
    val switch3652 =
        addConfig(
            "Switch3652",
            ConfigItem.configTypeBool,
            false
        )
    val switch3653 =
        addConfig(
            "Switch3653",
            ConfigItem.configTypeBool,
            false
        )
    val switch3654 =
        addConfig(
            "Switch3654",
            ConfigItem.configTypeBool,
            false
        )
    val switch3655 =
        addConfig(
            "Switch3655",
            ConfigItem.configTypeBool,
            false
        )
    val switch3656 =
        addConfig(
            "Switch3656",
            ConfigItem.configTypeBool,
            false
        )
    val switch3657 =
        addConfig(
            "Switch3657",
            ConfigItem.configTypeBool,
            false
        )
    val switch3658 =
        addConfig(
            "Switch3658",
            ConfigItem.configTypeBool,
            false
        )
    val switch3659 =
        addConfig(
            "Switch3659",
            ConfigItem.configTypeBool,
            false
        )
    val switch3660 =
        addConfig(
            "Switch3660",
            ConfigItem.configTypeBool,
            false
        )
    val switch3661 =
        addConfig(
            "Switch3661",
            ConfigItem.configTypeBool,
            false
        )
    val switch3662 =
        addConfig(
            "Switch3662",
            ConfigItem.configTypeBool,
            false
        )
    val switch3663 =
        addConfig(
            "Switch3663",
            ConfigItem.configTypeBool,
            false
        )
    val switch3664 =
        addConfig(
            "Switch3664",
            ConfigItem.configTypeBool,
            false
        )
    val switch3665 =
        addConfig(
            "Switch3665",
            ConfigItem.configTypeBool,
            false
        )
    val switch3666 =
        addConfig(
            "Switch3666",
            ConfigItem.configTypeBool,
            false
        )
    val switch3667 =
        addConfig(
            "Switch3667",
            ConfigItem.configTypeBool,
            false
        )
    val switch3668 =
        addConfig(
            "Switch3668",
            ConfigItem.configTypeBool,
            false
        )
    val switch3669 =
        addConfig(
            "Switch3669",
            ConfigItem.configTypeBool,
            false
        )
    val switch3670 =
        addConfig(
            "Switch3670",
            ConfigItem.configTypeBool,
            false
        )
    val switch3671 =
        addConfig(
            "Switch3671",
            ConfigItem.configTypeBool,
            false
        )
    val switch3672 =
        addConfig(
            "Switch3672",
            ConfigItem.configTypeBool,
            false
        )
    val switch3673 =
        addConfig(
            "Switch3673",
            ConfigItem.configTypeBool,
            false
        )
    val switch3674 =
        addConfig(
            "Switch3674",
            ConfigItem.configTypeBool,
            false
        )
    val switch3675 =
        addConfig(
            "Switch3675",
            ConfigItem.configTypeBool,
            false
        )
    val switch3676 =
        addConfig(
            "Switch3676",
            ConfigItem.configTypeBool,
            false
        )
    val switch3677 =
        addConfig(
            "Switch3677",
            ConfigItem.configTypeBool,
            false
        )
    val switch3678 =
        addConfig(
            "Switch3678",
            ConfigItem.configTypeBool,
            false
        )
    val switch3679 =
        addConfig(
            "Switch3679",
            ConfigItem.configTypeBool,
            false
        )
    val switch3680 =
        addConfig(
            "Switch3680",
            ConfigItem.configTypeBool,
            false
        )
    val switch3681 =
        addConfig(
            "Switch3681",
            ConfigItem.configTypeBool,
            false
        )
    val switch3682 =
        addConfig(
            "Switch3682",
            ConfigItem.configTypeBool,
            false
        )
    val switch3683 =
        addConfig(
            "Switch3683",
            ConfigItem.configTypeBool,
            false
        )
    val switch3684 =
        addConfig(
            "Switch3684",
            ConfigItem.configTypeBool,
            false
        )
    val switch3685 =
        addConfig(
            "Switch3685",
            ConfigItem.configTypeBool,
            false
        )
    val switch3686 =
        addConfig(
            "Switch3686",
            ConfigItem.configTypeBool,
            false
        )
    val switch3687 =
        addConfig(
            "Switch3687",
            ConfigItem.configTypeBool,
            false
        )
    val switch3688 =
        addConfig(
            "Switch3688",
            ConfigItem.configTypeBool,
            false
        )
    val switch3689 =
        addConfig(
            "Switch3689",
            ConfigItem.configTypeBool,
            false
        )
    val switch3690 =
        addConfig(
            "Switch3690",
            ConfigItem.configTypeBool,
            false
        )
    val switch3691 =
        addConfig(
            "Switch3691",
            ConfigItem.configTypeBool,
            false
        )
    val switch3692 =
        addConfig(
            "Switch3692",
            ConfigItem.configTypeBool,
            false
        )
    val switch3693 =
        addConfig(
            "Switch3693",
            ConfigItem.configTypeBool,
            false
        )
    val switch3694 =
        addConfig(
            "Switch3694",
            ConfigItem.configTypeBool,
            false
        )
    val switch3695 =
        addConfig(
            "Switch3695",
            ConfigItem.configTypeBool,
            false
        )
    val switch3696 =
        addConfig(
            "Switch3696",
            ConfigItem.configTypeBool,
            false
        )
    val switch3697 =
        addConfig(
            "Switch3697",
            ConfigItem.configTypeBool,
            false
        )
    val switch3698 =
        addConfig(
            "Switch3698",
            ConfigItem.configTypeBool,
            false
        )
    val switch3699 =
        addConfig(
            "Switch3699",
            ConfigItem.configTypeBool,
            false
        )
    val switch3700 =
        addConfig(
            "Switch3700",
            ConfigItem.configTypeBool,
            false
        )
    val switch3701 =
        addConfig(
            "Switch3701",
            ConfigItem.configTypeBool,
            false
        )
    val switch3702 =
        addConfig(
            "Switch3702",
            ConfigItem.configTypeBool,
            false
        )
    val switch3703 =
        addConfig(
            "Switch3703",
            ConfigItem.configTypeBool,
            false
        )
    val switch3704 =
        addConfig(
            "Switch3704",
            ConfigItem.configTypeBool,
            false
        )
    val switch3705 =
        addConfig(
            "Switch3705",
            ConfigItem.configTypeBool,
            false
        )
    val switch3706 =
        addConfig(
            "Switch3706",
            ConfigItem.configTypeBool,
            false
        )
    val switch3707 =
        addConfig(
            "Switch3707",
            ConfigItem.configTypeBool,
            false
        )
    val switch3708 =
        addConfig(
            "Switch3708",
            ConfigItem.configTypeBool,
            false
        )
    val switch3709 =
        addConfig(
            "Switch3709",
            ConfigItem.configTypeBool,
            false
        )
    val switch3710 =
        addConfig(
            "Switch3710",
            ConfigItem.configTypeBool,
            false
        )
    val switch3711 =
        addConfig(
            "Switch3711",
            ConfigItem.configTypeBool,
            false
        )
    val switch3712 =
        addConfig(
            "Switch3712",
            ConfigItem.configTypeBool,
            false
        )
    val switch3713 =
        addConfig(
            "Switch3713",
            ConfigItem.configTypeBool,
            false
        )
    val switch3714 =
        addConfig(
            "Switch3714",
            ConfigItem.configTypeBool,
            false
        )
    val switch3715 =
        addConfig(
            "Switch3715",
            ConfigItem.configTypeBool,
            false
        )
    val switch3716 =
        addConfig(
            "Switch3716",
            ConfigItem.configTypeBool,
            false
        )
    val switch3717 =
        addConfig(
            "Switch3717",
            ConfigItem.configTypeBool,
            false
        )
    val switch3718 =
        addConfig(
            "Switch3718",
            ConfigItem.configTypeBool,
            false
        )
    val switch3719 =
        addConfig(
            "Switch3719",
            ConfigItem.configTypeBool,
            false
        )
    val switch3720 =
        addConfig(
            "Switch3720",
            ConfigItem.configTypeBool,
            false
        )
    val switch3721 =
        addConfig(
            "Switch3721",
            ConfigItem.configTypeBool,
            false
        )
    val switch3722 =
        addConfig(
            "Switch3722",
            ConfigItem.configTypeBool,
            false
        )
    val switch3723 =
        addConfig(
            "Switch3723",
            ConfigItem.configTypeBool,
            false
        )
    val switch3724 =
        addConfig(
            "Switch3724",
            ConfigItem.configTypeBool,
            false
        )
    val switch3725 =
        addConfig(
            "Switch3725",
            ConfigItem.configTypeBool,
            false
        )
    val switch3726 =
        addConfig(
            "Switch3726",
            ConfigItem.configTypeBool,
            false
        )
    val switch3727 =
        addConfig(
            "Switch3727",
            ConfigItem.configTypeBool,
            false
        )
    val switch3728 =
        addConfig(
            "Switch3728",
            ConfigItem.configTypeBool,
            false
        )
    val switch3729 =
        addConfig(
            "Switch3729",
            ConfigItem.configTypeBool,
            false
        )
    val switch3730 =
        addConfig(
            "Switch3730",
            ConfigItem.configTypeBool,
            false
        )
    val switch3731 =
        addConfig(
            "Switch3731",
            ConfigItem.configTypeBool,
            false
        )
    val switch3732 =
        addConfig(
            "Switch3732",
            ConfigItem.configTypeBool,
            false
        )
    val switch3733 =
        addConfig(
            "Switch3733",
            ConfigItem.configTypeBool,
            false
        )
    val switch3734 =
        addConfig(
            "Switch3734",
            ConfigItem.configTypeBool,
            false
        )
    val switch3735 =
        addConfig(
            "Switch3735",
            ConfigItem.configTypeBool,
            false
        )
    val switch3736 =
        addConfig(
            "Switch3736",
            ConfigItem.configTypeBool,
            false
        )
    val switch3737 =
        addConfig(
            "Switch3737",
            ConfigItem.configTypeBool,
            false
        )
    val switch3738 =
        addConfig(
            "Switch3738",
            ConfigItem.configTypeBool,
            false
        )
    val switch3739 =
        addConfig(
            "Switch3739",
            ConfigItem.configTypeBool,
            false
        )
    val switch3740 =
        addConfig(
            "Switch3740",
            ConfigItem.configTypeBool,
            false
        )
    val switch3741 =
        addConfig(
            "Switch3741",
            ConfigItem.configTypeBool,
            false
        )
    val switch3742 =
        addConfig(
            "Switch3742",
            ConfigItem.configTypeBool,
            false
        )
    val switch3743 =
        addConfig(
            "Switch3743",
            ConfigItem.configTypeBool,
            false
        )
    val switch3744 =
        addConfig(
            "Switch3744",
            ConfigItem.configTypeBool,
            false
        )
    val switch3745 =
        addConfig(
            "Switch3745",
            ConfigItem.configTypeBool,
            false
        )
    val switch3746 =
        addConfig(
            "Switch3746",
            ConfigItem.configTypeBool,
            false
        )
    val switch3747 =
        addConfig(
            "Switch3747",
            ConfigItem.configTypeBool,
            false
        )
    val switch3748 =
        addConfig(
            "Switch3748",
            ConfigItem.configTypeBool,
            false
        )
    val switch3749 =
        addConfig(
            "Switch3749",
            ConfigItem.configTypeBool,
            false
        )
    val switch3750 =
        addConfig(
            "Switch3750",
            ConfigItem.configTypeBool,
            false
        )
    val switch3751 =
        addConfig(
            "Switch3751",
            ConfigItem.configTypeBool,
            false
        )
    val switch3752 =
        addConfig(
            "Switch3752",
            ConfigItem.configTypeBool,
            false
        )
    val switch3753 =
        addConfig(
            "Switch3753",
            ConfigItem.configTypeBool,
            false
        )
    val switch3754 =
        addConfig(
            "Switch3754",
            ConfigItem.configTypeBool,
            false
        )
    val switch3755 =
        addConfig(
            "Switch3755",
            ConfigItem.configTypeBool,
            false
        )
    val switch3756 =
        addConfig(
            "Switch3756",
            ConfigItem.configTypeBool,
            false
        )
    val switch3757 =
        addConfig(
            "Switch3757",
            ConfigItem.configTypeBool,
            false
        )
    val switch3758 =
        addConfig(
            "Switch3758",
            ConfigItem.configTypeBool,
            false
        )
    val switch3759 =
        addConfig(
            "Switch3759",
            ConfigItem.configTypeBool,
            false
        )
    val switch3760 =
        addConfig(
            "Switch3760",
            ConfigItem.configTypeBool,
            false
        )
    val switch3761 =
        addConfig(
            "Switch3761",
            ConfigItem.configTypeBool,
            false
        )
    val switch3762 =
        addConfig(
            "Switch3762",
            ConfigItem.configTypeBool,
            false
        )
    val switch3763 =
        addConfig(
            "Switch3763",
            ConfigItem.configTypeBool,
            false
        )
    val switch3764 =
        addConfig(
            "Switch3764",
            ConfigItem.configTypeBool,
            false
        )
    val switch3765 =
        addConfig(
            "Switch3765",
            ConfigItem.configTypeBool,
            false
        )
    val switch3766 =
        addConfig(
            "Switch3766",
            ConfigItem.configTypeBool,
            false
        )
    val switch3767 =
        addConfig(
            "Switch3767",
            ConfigItem.configTypeBool,
            false
        )
    val switch3768 =
        addConfig(
            "Switch3768",
            ConfigItem.configTypeBool,
            false
        )
    val switch3769 =
        addConfig(
            "Switch3769",
            ConfigItem.configTypeBool,
            false
        )
    val switch3770 =
        addConfig(
            "Switch3770",
            ConfigItem.configTypeBool,
            false
        )
    val switch3771 =
        addConfig(
            "Switch3771",
            ConfigItem.configTypeBool,
            false
        )
    val switch3772 =
        addConfig(
            "Switch3772",
            ConfigItem.configTypeBool,
            false
        )
    val switch3773 =
        addConfig(
            "Switch3773",
            ConfigItem.configTypeBool,
            false
        )
    val switch3774 =
        addConfig(
            "Switch3774",
            ConfigItem.configTypeBool,
            false
        )
    val switch3775 =
        addConfig(
            "Switch3775",
            ConfigItem.configTypeBool,
            false
        )
    val switch3776 =
        addConfig(
            "Switch3776",
            ConfigItem.configTypeBool,
            false
        )
    val switch3777 =
        addConfig(
            "Switch3777",
            ConfigItem.configTypeBool,
            false
        )
    val switch3778 =
        addConfig(
            "Switch3778",
            ConfigItem.configTypeBool,
            false
        )
    val switch3779 =
        addConfig(
            "Switch3779",
            ConfigItem.configTypeBool,
            false
        )
    val switch3780 =
        addConfig(
            "Switch3780",
            ConfigItem.configTypeBool,
            false
        )
    val switch3781 =
        addConfig(
            "Switch3781",
            ConfigItem.configTypeBool,
            false
        )
    val switch3782 =
        addConfig(
            "Switch3782",
            ConfigItem.configTypeBool,
            false
        )
    val switch3783 =
        addConfig(
            "Switch3783",
            ConfigItem.configTypeBool,
            false
        )
    val switch3784 =
        addConfig(
            "Switch3784",
            ConfigItem.configTypeBool,
            false
        )
    val switch3785 =
        addConfig(
            "Switch3785",
            ConfigItem.configTypeBool,
            false
        )
    val switch3786 =
        addConfig(
            "Switch3786",
            ConfigItem.configTypeBool,
            false
        )
    val switch3787 =
        addConfig(
            "Switch3787",
            ConfigItem.configTypeBool,
            false
        )
    val switch3788 =
        addConfig(
            "Switch3788",
            ConfigItem.configTypeBool,
            false
        )
    val switch3789 =
        addConfig(
            "Switch3789",
            ConfigItem.configTypeBool,
            false
        )
    val switch3790 =
        addConfig(
            "Switch3790",
            ConfigItem.configTypeBool,
            false
        )
    val switch3791 =
        addConfig(
            "Switch3791",
            ConfigItem.configTypeBool,
            false
        )
    val switch3792 =
        addConfig(
            "Switch3792",
            ConfigItem.configTypeBool,
            false
        )
    val switch3793 =
        addConfig(
            "Switch3793",
            ConfigItem.configTypeBool,
            false
        )
    val switch3794 =
        addConfig(
            "Switch3794",
            ConfigItem.configTypeBool,
            false
        )
    val switch3795 =
        addConfig(
            "Switch3795",
            ConfigItem.configTypeBool,
            false
        )
    val switch3796 =
        addConfig(
            "Switch3796",
            ConfigItem.configTypeBool,
            false
        )
    val switch3797 =
        addConfig(
            "Switch3797",
            ConfigItem.configTypeBool,
            false
        )
    val switch3798 =
        addConfig(
            "Switch3798",
            ConfigItem.configTypeBool,
            false
        )
    val switch3799 =
        addConfig(
            "Switch3799",
            ConfigItem.configTypeBool,
            false
        )
    val switch3800 =
        addConfig(
            "Switch3800",
            ConfigItem.configTypeBool,
            false
        )
    val switch3801 =
        addConfig(
            "Switch3801",
            ConfigItem.configTypeBool,
            false
        )
    val switch3802 =
        addConfig(
            "Switch3802",
            ConfigItem.configTypeBool,
            false
        )
    val switch3803 =
        addConfig(
            "Switch3803",
            ConfigItem.configTypeBool,
            false
        )
    val switch3804 =
        addConfig(
            "Switch3804",
            ConfigItem.configTypeBool,
            false
        )
    val switch3805 =
        addConfig(
            "Switch3805",
            ConfigItem.configTypeBool,
            false
        )
    val switch3806 =
        addConfig(
            "Switch3806",
            ConfigItem.configTypeBool,
            false
        )
    val switch3807 =
        addConfig(
            "Switch3807",
            ConfigItem.configTypeBool,
            false
        )
    val switch3808 =
        addConfig(
            "Switch3808",
            ConfigItem.configTypeBool,
            false
        )
    val switch3809 =
        addConfig(
            "Switch3809",
            ConfigItem.configTypeBool,
            false
        )
    val switch3810 =
        addConfig(
            "Switch3810",
            ConfigItem.configTypeBool,
            false
        )
    val switch3811 =
        addConfig(
            "Switch3811",
            ConfigItem.configTypeBool,
            false
        )
    val switch3812 =
        addConfig(
            "Switch3812",
            ConfigItem.configTypeBool,
            false
        )
    val switch3813 =
        addConfig(
            "Switch3813",
            ConfigItem.configTypeBool,
            false
        )
    val switch3814 =
        addConfig(
            "Switch3814",
            ConfigItem.configTypeBool,
            false
        )
    val switch3815 =
        addConfig(
            "Switch3815",
            ConfigItem.configTypeBool,
            false
        )
    val switch3816 =
        addConfig(
            "Switch3816",
            ConfigItem.configTypeBool,
            false
        )
    val switch3817 =
        addConfig(
            "Switch3817",
            ConfigItem.configTypeBool,
            false
        )
    val switch3818 =
        addConfig(
            "Switch3818",
            ConfigItem.configTypeBool,
            false
        )
    val switch3819 =
        addConfig(
            "Switch3819",
            ConfigItem.configTypeBool,
            false
        )
    val switch3820 =
        addConfig(
            "Switch3820",
            ConfigItem.configTypeBool,
            false
        )
    val switch3821 =
        addConfig(
            "Switch3821",
            ConfigItem.configTypeBool,
            false
        )
    val switch3822 =
        addConfig(
            "Switch3822",
            ConfigItem.configTypeBool,
            false
        )
    val switch3823 =
        addConfig(
            "Switch3823",
            ConfigItem.configTypeBool,
            false
        )
    val switch3824 =
        addConfig(
            "Switch3824",
            ConfigItem.configTypeBool,
            false
        )
    val switch3825 =
        addConfig(
            "Switch3825",
            ConfigItem.configTypeBool,
            false
        )
    val switch3826 =
        addConfig(
            "Switch3826",
            ConfigItem.configTypeBool,
            false
        )
    val switch3827 =
        addConfig(
            "Switch3827",
            ConfigItem.configTypeBool,
            false
        )
    val switch3828 =
        addConfig(
            "Switch3828",
            ConfigItem.configTypeBool,
            false
        )
    val switch3829 =
        addConfig(
            "Switch3829",
            ConfigItem.configTypeBool,
            false
        )
    val switch3830 =
        addConfig(
            "Switch3830",
            ConfigItem.configTypeBool,
            false
        )
    val switch3831 =
        addConfig(
            "Switch3831",
            ConfigItem.configTypeBool,
            false
        )
    val switch3832 =
        addConfig(
            "Switch3832",
            ConfigItem.configTypeBool,
            false
        )
    val switch3833 =
        addConfig(
            "Switch3833",
            ConfigItem.configTypeBool,
            false
        )
    val switch3834 =
        addConfig(
            "Switch3834",
            ConfigItem.configTypeBool,
            false
        )
    val switch3835 =
        addConfig(
            "Switch3835",
            ConfigItem.configTypeBool,
            false
        )
    val switch3836 =
        addConfig(
            "Switch3836",
            ConfigItem.configTypeBool,
            false
        )
    val switch3837 =
        addConfig(
            "Switch3837",
            ConfigItem.configTypeBool,
            false
        )
    val switch3838 =
        addConfig(
            "Switch3838",
            ConfigItem.configTypeBool,
            false
        )
    val switch3839 =
        addConfig(
            "Switch3839",
            ConfigItem.configTypeBool,
            false
        )
    val switch3840 =
        addConfig(
            "Switch3840",
            ConfigItem.configTypeBool,
            false
        )
    val switch3841 =
        addConfig(
            "Switch3841",
            ConfigItem.configTypeBool,
            false
        )
    val switch3842 =
        addConfig(
            "Switch3842",
            ConfigItem.configTypeBool,
            false
        )
    val switch3843 =
        addConfig(
            "Switch3843",
            ConfigItem.configTypeBool,
            false
        )
    val switch3844 =
        addConfig(
            "Switch3844",
            ConfigItem.configTypeBool,
            false
        )
    val switch3845 =
        addConfig(
            "Switch3845",
            ConfigItem.configTypeBool,
            false
        )
    val switch3846 =
        addConfig(
            "Switch3846",
            ConfigItem.configTypeBool,
            false
        )
    val switch3847 =
        addConfig(
            "Switch3847",
            ConfigItem.configTypeBool,
            false
        )
    val switch3848 =
        addConfig(
            "Switch3848",
            ConfigItem.configTypeBool,
            false
        )
    val switch3849 =
        addConfig(
            "Switch3849",
            ConfigItem.configTypeBool,
            false
        )
    val switch3850 =
        addConfig(
            "Switch3850",
            ConfigItem.configTypeBool,
            false
        )
    val switch3851 =
        addConfig(
            "Switch3851",
            ConfigItem.configTypeBool,
            false
        )
    val switch3852 =
        addConfig(
            "Switch3852",
            ConfigItem.configTypeBool,
            false
        )
    val switch3853 =
        addConfig(
            "Switch3853",
            ConfigItem.configTypeBool,
            false
        )
    val switch3854 =
        addConfig(
            "Switch3854",
            ConfigItem.configTypeBool,
            false
        )
    val switch3855 =
        addConfig(
            "Switch3855",
            ConfigItem.configTypeBool,
            false
        )
    val switch3856 =
        addConfig(
            "Switch3856",
            ConfigItem.configTypeBool,
            false
        )
    val switch3857 =
        addConfig(
            "Switch3857",
            ConfigItem.configTypeBool,
            false
        )
    val switch3858 =
        addConfig(
            "Switch3858",
            ConfigItem.configTypeBool,
            false
        )
    val switch3859 =
        addConfig(
            "Switch3859",
            ConfigItem.configTypeBool,
            false
        )
    val switch3860 =
        addConfig(
            "Switch3860",
            ConfigItem.configTypeBool,
            false
        )
    val switch3861 =
        addConfig(
            "Switch3861",
            ConfigItem.configTypeBool,
            false
        )
    val switch3862 =
        addConfig(
            "Switch3862",
            ConfigItem.configTypeBool,
            false
        )
    val switch3863 =
        addConfig(
            "Switch3863",
            ConfigItem.configTypeBool,
            false
        )
    val switch3864 =
        addConfig(
            "Switch3864",
            ConfigItem.configTypeBool,
            false
        )
    val switch3865 =
        addConfig(
            "Switch3865",
            ConfigItem.configTypeBool,
            false
        )
    val switch3866 =
        addConfig(
            "Switch3866",
            ConfigItem.configTypeBool,
            false
        )
    val switch3867 =
        addConfig(
            "Switch3867",
            ConfigItem.configTypeBool,
            false
        )
    val switch3868 =
        addConfig(
            "Switch3868",
            ConfigItem.configTypeBool,
            false
        )
    val switch3869 =
        addConfig(
            "Switch3869",
            ConfigItem.configTypeBool,
            false
        )
    val switch3870 =
        addConfig(
            "Switch3870",
            ConfigItem.configTypeBool,
            false
        )
    val switch3871 =
        addConfig(
            "Switch3871",
            ConfigItem.configTypeBool,
            false
        )
    val switch3872 =
        addConfig(
            "Switch3872",
            ConfigItem.configTypeBool,
            false
        )
    val switch3873 =
        addConfig(
            "Switch3873",
            ConfigItem.configTypeBool,
            false
        )
    val switch3874 =
        addConfig(
            "Switch3874",
            ConfigItem.configTypeBool,
            false
        )
    val switch3875 =
        addConfig(
            "Switch3875",
            ConfigItem.configTypeBool,
            false
        )
    val switch3876 =
        addConfig(
            "Switch3876",
            ConfigItem.configTypeBool,
            false
        )
    val switch3877 =
        addConfig(
            "Switch3877",
            ConfigItem.configTypeBool,
            false
        )
    val switch3878 =
        addConfig(
            "Switch3878",
            ConfigItem.configTypeBool,
            false
        )
    val switch3879 =
        addConfig(
            "Switch3879",
            ConfigItem.configTypeBool,
            false
        )
    val switch3880 =
        addConfig(
            "Switch3880",
            ConfigItem.configTypeBool,
            false
        )
    val switch3881 =
        addConfig(
            "Switch3881",
            ConfigItem.configTypeBool,
            false
        )
    val switch3882 =
        addConfig(
            "Switch3882",
            ConfigItem.configTypeBool,
            false
        )
    val switch3883 =
        addConfig(
            "Switch3883",
            ConfigItem.configTypeBool,
            false
        )
    val switch3884 =
        addConfig(
            "Switch3884",
            ConfigItem.configTypeBool,
            false
        )
    val switch3885 =
        addConfig(
            "Switch3885",
            ConfigItem.configTypeBool,
            false
        )
    val switch3886 =
        addConfig(
            "Switch3886",
            ConfigItem.configTypeBool,
            false
        )
    val switch3887 =
        addConfig(
            "Switch3887",
            ConfigItem.configTypeBool,
            false
        )
    val switch3888 =
        addConfig(
            "Switch3888",
            ConfigItem.configTypeBool,
            false
        )
    val switch3889 =
        addConfig(
            "Switch3889",
            ConfigItem.configTypeBool,
            false
        )
    val switch3890 =
        addConfig(
            "Switch3890",
            ConfigItem.configTypeBool,
            false
        )
    val switch3891 =
        addConfig(
            "Switch3891",
            ConfigItem.configTypeBool,
            false
        )
    val switch3892 =
        addConfig(
            "Switch3892",
            ConfigItem.configTypeBool,
            false
        )
    val switch3893 =
        addConfig(
            "Switch3893",
            ConfigItem.configTypeBool,
            false
        )
    val switch3894 =
        addConfig(
            "Switch3894",
            ConfigItem.configTypeBool,
            false
        )
    val switch3895 =
        addConfig(
            "Switch3895",
            ConfigItem.configTypeBool,
            false
        )
    val switch3896 =
        addConfig(
            "Switch3896",
            ConfigItem.configTypeBool,
            false
        )
    val switch3897 =
        addConfig(
            "Switch3897",
            ConfigItem.configTypeBool,
            false
        )
    val switch3898 =
        addConfig(
            "Switch3898",
            ConfigItem.configTypeBool,
            false
        )
    val switch3899 =
        addConfig(
            "Switch3899",
            ConfigItem.configTypeBool,
            false
        )
    val switch3900 =
        addConfig(
            "Switch3900",
            ConfigItem.configTypeBool,
            false
        )
    val switch3901 =
        addConfig(
            "Switch3901",
            ConfigItem.configTypeBool,
            false
        )
    val switch3902 =
        addConfig(
            "Switch3902",
            ConfigItem.configTypeBool,
            false
        )
    val switch3903 =
        addConfig(
            "Switch3903",
            ConfigItem.configTypeBool,
            false
        )
    val switch3904 =
        addConfig(
            "Switch3904",
            ConfigItem.configTypeBool,
            false
        )
    val switch3905 =
        addConfig(
            "Switch3905",
            ConfigItem.configTypeBool,
            false
        )
    val switch3906 =
        addConfig(
            "Switch3906",
            ConfigItem.configTypeBool,
            false
        )
    val switch3907 =
        addConfig(
            "Switch3907",
            ConfigItem.configTypeBool,
            false
        )
    val switch3908 =
        addConfig(
            "Switch3908",
            ConfigItem.configTypeBool,
            false
        )
    val switch3909 =
        addConfig(
            "Switch3909",
            ConfigItem.configTypeBool,
            false
        )
    val switch3910 =
        addConfig(
            "Switch3910",
            ConfigItem.configTypeBool,
            false
        )
    val switch3911 =
        addConfig(
            "Switch3911",
            ConfigItem.configTypeBool,
            false
        )
    val switch3912 =
        addConfig(
            "Switch3912",
            ConfigItem.configTypeBool,
            false
        )
    val switch3913 =
        addConfig(
            "Switch3913",
            ConfigItem.configTypeBool,
            false
        )
    val switch3914 =
        addConfig(
            "Switch3914",
            ConfigItem.configTypeBool,
            false
        )
    val switch3915 =
        addConfig(
            "Switch3915",
            ConfigItem.configTypeBool,
            false
        )
    val switch3916 =
        addConfig(
            "Switch3916",
            ConfigItem.configTypeBool,
            false
        )
    val switch3917 =
        addConfig(
            "Switch3917",
            ConfigItem.configTypeBool,
            false
        )
    val switch3918 =
        addConfig(
            "Switch3918",
            ConfigItem.configTypeBool,
            false
        )
    val switch3919 =
        addConfig(
            "Switch3919",
            ConfigItem.configTypeBool,
            false
        )
    val switch3920 =
        addConfig(
            "Switch3920",
            ConfigItem.configTypeBool,
            false
        )
    val switch3921 =
        addConfig(
            "Switch3921",
            ConfigItem.configTypeBool,
            false
        )
    val switch3922 =
        addConfig(
            "Switch3922",
            ConfigItem.configTypeBool,
            false
        )
    val switch3923 =
        addConfig(
            "Switch3923",
            ConfigItem.configTypeBool,
            false
        )
    val switch3924 =
        addConfig(
            "Switch3924",
            ConfigItem.configTypeBool,
            false
        )
    val switch3925 =
        addConfig(
            "Switch3925",
            ConfigItem.configTypeBool,
            false
        )
    val switch3926 =
        addConfig(
            "Switch3926",
            ConfigItem.configTypeBool,
            false
        )
    val switch3927 =
        addConfig(
            "Switch3927",
            ConfigItem.configTypeBool,
            false
        )
    val switch3928 =
        addConfig(
            "Switch3928",
            ConfigItem.configTypeBool,
            false
        )
    val switch3929 =
        addConfig(
            "Switch3929",
            ConfigItem.configTypeBool,
            false
        )
    val switch3930 =
        addConfig(
            "Switch3930",
            ConfigItem.configTypeBool,
            false
        )
    val switch3931 =
        addConfig(
            "Switch3931",
            ConfigItem.configTypeBool,
            false
        )
    val switch3932 =
        addConfig(
            "Switch3932",
            ConfigItem.configTypeBool,
            false
        )
    val switch3933 =
        addConfig(
            "Switch3933",
            ConfigItem.configTypeBool,
            false
        )
    val switch3934 =
        addConfig(
            "Switch3934",
            ConfigItem.configTypeBool,
            false
        )
    val switch3935 =
        addConfig(
            "Switch3935",
            ConfigItem.configTypeBool,
            false
        )
    val switch3936 =
        addConfig(
            "Switch3936",
            ConfigItem.configTypeBool,
            false
        )
    val switch3937 =
        addConfig(
            "Switch3937",
            ConfigItem.configTypeBool,
            false
        )
    val switch3938 =
        addConfig(
            "Switch3938",
            ConfigItem.configTypeBool,
            false
        )
    val switch3939 =
        addConfig(
            "Switch3939",
            ConfigItem.configTypeBool,
            false
        )
    val switch3940 =
        addConfig(
            "Switch3940",
            ConfigItem.configTypeBool,
            false
        )
    val switch3941 =
        addConfig(
            "Switch3941",
            ConfigItem.configTypeBool,
            false
        )
    val switch3942 =
        addConfig(
            "Switch3942",
            ConfigItem.configTypeBool,
            false
        )
    val switch3943 =
        addConfig(
            "Switch3943",
            ConfigItem.configTypeBool,
            false
        )
    val switch3944 =
        addConfig(
            "Switch3944",
            ConfigItem.configTypeBool,
            false
        )
    val switch3945 =
        addConfig(
            "Switch3945",
            ConfigItem.configTypeBool,
            false
        )
    val switch3946 =
        addConfig(
            "Switch3946",
            ConfigItem.configTypeBool,
            false
        )
    val switch3947 =
        addConfig(
            "Switch3947",
            ConfigItem.configTypeBool,
            false
        )
    val switch3948 =
        addConfig(
            "Switch3948",
            ConfigItem.configTypeBool,
            false
        )
    val switch3949 =
        addConfig(
            "Switch3949",
            ConfigItem.configTypeBool,
            false
        )
    val switch3950 =
        addConfig(
            "Switch3950",
            ConfigItem.configTypeBool,
            false
        )
    val switch3951 =
        addConfig(
            "Switch3951",
            ConfigItem.configTypeBool,
            false
        )
    val switch3952 =
        addConfig(
            "Switch3952",
            ConfigItem.configTypeBool,
            false
        )
    val switch3953 =
        addConfig(
            "Switch3953",
            ConfigItem.configTypeBool,
            false
        )
    val switch3954 =
        addConfig(
            "Switch3954",
            ConfigItem.configTypeBool,
            false
        )
    val switch3955 =
        addConfig(
            "Switch3955",
            ConfigItem.configTypeBool,
            false
        )
    val switch3956 =
        addConfig(
            "Switch3956",
            ConfigItem.configTypeBool,
            false
        )
    val switch3957 =
        addConfig(
            "Switch3957",
            ConfigItem.configTypeBool,
            false
        )
    val switch3958 =
        addConfig(
            "Switch3958",
            ConfigItem.configTypeBool,
            false
        )
    val switch3959 =
        addConfig(
            "Switch3959",
            ConfigItem.configTypeBool,
            false
        )
    val switch3960 =
        addConfig(
            "Switch3960",
            ConfigItem.configTypeBool,
            false
        )
    val switch3961 =
        addConfig(
            "Switch3961",
            ConfigItem.configTypeBool,
            false
        )
    val switch3962 =
        addConfig(
            "Switch3962",
            ConfigItem.configTypeBool,
            false
        )
    val switch3963 =
        addConfig(
            "Switch3963",
            ConfigItem.configTypeBool,
            false
        )
    val switch3964 =
        addConfig(
            "Switch3964",
            ConfigItem.configTypeBool,
            false
        )
    val switch3965 =
        addConfig(
            "Switch3965",
            ConfigItem.configTypeBool,
            false
        )
    val switch3966 =
        addConfig(
            "Switch3966",
            ConfigItem.configTypeBool,
            false
        )
    val switch3967 =
        addConfig(
            "Switch3967",
            ConfigItem.configTypeBool,
            false
        )
    val switch3968 =
        addConfig(
            "Switch3968",
            ConfigItem.configTypeBool,
            false
        )
    val switch3969 =
        addConfig(
            "Switch3969",
            ConfigItem.configTypeBool,
            false
        )
    val switch3970 =
        addConfig(
            "Switch3970",
            ConfigItem.configTypeBool,
            false
        )
    val switch3971 =
        addConfig(
            "Switch3971",
            ConfigItem.configTypeBool,
            false
        )
    val switch3972 =
        addConfig(
            "Switch3972",
            ConfigItem.configTypeBool,
            false
        )
    val switch3973 =
        addConfig(
            "Switch3973",
            ConfigItem.configTypeBool,
            false
        )
    val switch3974 =
        addConfig(
            "Switch3974",
            ConfigItem.configTypeBool,
            false
        )
    val switch3975 =
        addConfig(
            "Switch3975",
            ConfigItem.configTypeBool,
            false
        )
    val switch3976 =
        addConfig(
            "Switch3976",
            ConfigItem.configTypeBool,
            false
        )
    val switch3977 =
        addConfig(
            "Switch3977",
            ConfigItem.configTypeBool,
            false
        )
    val switch3978 =
        addConfig(
            "Switch3978",
            ConfigItem.configTypeBool,
            false
        )
    val switch3979 =
        addConfig(
            "Switch3979",
            ConfigItem.configTypeBool,
            false
        )
    val switch3980 =
        addConfig(
            "Switch3980",
            ConfigItem.configTypeBool,
            false
        )
    val switch3981 =
        addConfig(
            "Switch3981",
            ConfigItem.configTypeBool,
            false
        )
    val switch3982 =
        addConfig(
            "Switch3982",
            ConfigItem.configTypeBool,
            false
        )
    val switch3983 =
        addConfig(
            "Switch3983",
            ConfigItem.configTypeBool,
            false
        )
    val switch3984 =
        addConfig(
            "Switch3984",
            ConfigItem.configTypeBool,
            false
        )
    val switch3985 =
        addConfig(
            "Switch3985",
            ConfigItem.configTypeBool,
            false
        )
    val switch3986 =
        addConfig(
            "Switch3986",
            ConfigItem.configTypeBool,
            false
        )
    val switch3987 =
        addConfig(
            "Switch3987",
            ConfigItem.configTypeBool,
            false
        )
    val switch3988 =
        addConfig(
            "Switch3988",
            ConfigItem.configTypeBool,
            false
        )
    val switch3989 =
        addConfig(
            "Switch3989",
            ConfigItem.configTypeBool,
            false
        )
    val switch3990 =
        addConfig(
            "Switch3990",
            ConfigItem.configTypeBool,
            false
        )
    val switch3991 =
        addConfig(
            "Switch3991",
            ConfigItem.configTypeBool,
            false
        )
    val switch3992 =
        addConfig(
            "Switch3992",
            ConfigItem.configTypeBool,
            false
        )
    val switch3993 =
        addConfig(
            "Switch3993",
            ConfigItem.configTypeBool,
            false
        )
    val switch3994 =
        addConfig(
            "Switch3994",
            ConfigItem.configTypeBool,
            false
        )
    val switch3995 =
        addConfig(
            "Switch3995",
            ConfigItem.configTypeBool,
            false
        )
    val switch3996 =
        addConfig(
            "Switch3996",
            ConfigItem.configTypeBool,
            false
        )
    val switch3997 =
        addConfig(
            "Switch3997",
            ConfigItem.configTypeBool,
            false
        )
    val switch3998 =
        addConfig(
            "Switch3998",
            ConfigItem.configTypeBool,
            false
        )
    val switch3999 =
        addConfig(
            "Switch3999",
            ConfigItem.configTypeBool,
            false
        )
    val switch4000 =
        addConfig(
            "Switch4000",
            ConfigItem.configTypeBool,
            false
        )
    val switch4001 =
        addConfig(
            "Switch4001",
            ConfigItem.configTypeBool,
            false
        )
    val switch4002 =
        addConfig(
            "Switch4002",
            ConfigItem.configTypeBool,
            false
        )
    val switch4003 =
        addConfig(
            "Switch4003",
            ConfigItem.configTypeBool,
            false
        )
    val switch4004 =
        addConfig(
            "Switch4004",
            ConfigItem.configTypeBool,
            false
        )
    val switch4005 =
        addConfig(
            "Switch4005",
            ConfigItem.configTypeBool,
            false
        )
    val switch4006 =
        addConfig(
            "Switch4006",
            ConfigItem.configTypeBool,
            false
        )
    val switch4007 =
        addConfig(
            "Switch4007",
            ConfigItem.configTypeBool,
            false
        )
    val switch4008 =
        addConfig(
            "Switch4008",
            ConfigItem.configTypeBool,
            false
        )
    val switch4009 =
        addConfig(
            "Switch4009",
            ConfigItem.configTypeBool,
            false
        )
    val switch4010 =
        addConfig(
            "Switch4010",
            ConfigItem.configTypeBool,
            false
        )
    val switch4011 =
        addConfig(
            "Switch4011",
            ConfigItem.configTypeBool,
            false
        )
    val switch4012 =
        addConfig(
            "Switch4012",
            ConfigItem.configTypeBool,
            false
        )
    val switch4013 =
        addConfig(
            "Switch4013",
            ConfigItem.configTypeBool,
            false
        )
    val switch4014 =
        addConfig(
            "Switch4014",
            ConfigItem.configTypeBool,
            false
        )
    val switch4015 =
        addConfig(
            "Switch4015",
            ConfigItem.configTypeBool,
            false
        )
    val switch4016 =
        addConfig(
            "Switch4016",
            ConfigItem.configTypeBool,
            false
        )
    val switch4017 =
        addConfig(
            "Switch4017",
            ConfigItem.configTypeBool,
            false
        )
    val switch4018 =
        addConfig(
            "Switch4018",
            ConfigItem.configTypeBool,
            false
        )
    val switch4019 =
        addConfig(
            "Switch4019",
            ConfigItem.configTypeBool,
            false
        )
    val switch4020 =
        addConfig(
            "Switch4020",
            ConfigItem.configTypeBool,
            false
        )
    val switch4021 =
        addConfig(
            "Switch4021",
            ConfigItem.configTypeBool,
            false
        )
    val switch4022 =
        addConfig(
            "Switch4022",
            ConfigItem.configTypeBool,
            false
        )
    val switch4023 =
        addConfig(
            "Switch4023",
            ConfigItem.configTypeBool,
            false
        )
    val switch4024 =
        addConfig(
            "Switch4024",
            ConfigItem.configTypeBool,
            false
        )
    val switch4025 =
        addConfig(
            "Switch4025",
            ConfigItem.configTypeBool,
            false
        )
    val switch4026 =
        addConfig(
            "Switch4026",
            ConfigItem.configTypeBool,
            false
        )
    val switch4027 =
        addConfig(
            "Switch4027",
            ConfigItem.configTypeBool,
            false
        )
    val switch4028 =
        addConfig(
            "Switch4028",
            ConfigItem.configTypeBool,
            false
        )
    val switch4029 =
        addConfig(
            "Switch4029",
            ConfigItem.configTypeBool,
            false
        )
    val switch4030 =
        addConfig(
            "Switch4030",
            ConfigItem.configTypeBool,
            false
        )
    val switch4031 =
        addConfig(
            "Switch4031",
            ConfigItem.configTypeBool,
            false
        )
    val switch4032 =
        addConfig(
            "Switch4032",
            ConfigItem.configTypeBool,
            false
        )
    val switch4033 =
        addConfig(
            "Switch4033",
            ConfigItem.configTypeBool,
            false
        )
    val switch4034 =
        addConfig(
            "Switch4034",
            ConfigItem.configTypeBool,
            false
        )
    val switch4035 =
        addConfig(
            "Switch4035",
            ConfigItem.configTypeBool,
            false
        )
    val switch4036 =
        addConfig(
            "Switch4036",
            ConfigItem.configTypeBool,
            false
        )
    val switch4037 =
        addConfig(
            "Switch4037",
            ConfigItem.configTypeBool,
            false
        )
    val switch4038 =
        addConfig(
            "Switch4038",
            ConfigItem.configTypeBool,
            false
        )
    val switch4039 =
        addConfig(
            "Switch4039",
            ConfigItem.configTypeBool,
            false
        )
    val switch4040 =
        addConfig(
            "Switch4040",
            ConfigItem.configTypeBool,
            false
        )
    val switch4041 =
        addConfig(
            "Switch4041",
            ConfigItem.configTypeBool,
            false
        )
    val switch4042 =
        addConfig(
            "Switch4042",
            ConfigItem.configTypeBool,
            false
        )
    val switch4043 =
        addConfig(
            "Switch4043",
            ConfigItem.configTypeBool,
            false
        )
    val switch4044 =
        addConfig(
            "Switch4044",
            ConfigItem.configTypeBool,
            false
        )
    val switch4045 =
        addConfig(
            "Switch4045",
            ConfigItem.configTypeBool,
            false
        )
    val switch4046 =
        addConfig(
            "Switch4046",
            ConfigItem.configTypeBool,
            false
        )
    val switch4047 =
        addConfig(
            "Switch4047",
            ConfigItem.configTypeBool,
            false
        )
    val switch4048 =
        addConfig(
            "Switch4048",
            ConfigItem.configTypeBool,
            false
        )
    val switch4049 =
        addConfig(
            "Switch4049",
            ConfigItem.configTypeBool,
            false
        )
    val switch4050 =
        addConfig(
            "Switch4050",
            ConfigItem.configTypeBool,
            false
        )
    val switch4051 =
        addConfig(
            "Switch4051",
            ConfigItem.configTypeBool,
            false
        )
    val switch4052 =
        addConfig(
            "Switch4052",
            ConfigItem.configTypeBool,
            false
        )
    val switch4053 =
        addConfig(
            "Switch4053",
            ConfigItem.configTypeBool,
            false
        )
    val switch4054 =
        addConfig(
            "Switch4054",
            ConfigItem.configTypeBool,
            false
        )
    val switch4055 =
        addConfig(
            "Switch4055",
            ConfigItem.configTypeBool,
            false
        )
    val switch4056 =
        addConfig(
            "Switch4056",
            ConfigItem.configTypeBool,
            false
        )
    val switch4057 =
        addConfig(
            "Switch4057",
            ConfigItem.configTypeBool,
            false
        )
    val switch4058 =
        addConfig(
            "Switch4058",
            ConfigItem.configTypeBool,
            false
        )
    val switch4059 =
        addConfig(
            "Switch4059",
            ConfigItem.configTypeBool,
            false
        )
    val switch4060 =
        addConfig(
            "Switch4060",
            ConfigItem.configTypeBool,
            false
        )
    val switch4061 =
        addConfig(
            "Switch4061",
            ConfigItem.configTypeBool,
            false
        )
    val switch4062 =
        addConfig(
            "Switch4062",
            ConfigItem.configTypeBool,
            false
        )
    val switch4063 =
        addConfig(
            "Switch4063",
            ConfigItem.configTypeBool,
            false
        )
    val switch4064 =
        addConfig(
            "Switch4064",
            ConfigItem.configTypeBool,
            false
        )
    val switch4065 =
        addConfig(
            "Switch4065",
            ConfigItem.configTypeBool,
            false
        )
    val switch4066 =
        addConfig(
            "Switch4066",
            ConfigItem.configTypeBool,
            false
        )
    val switch4067 =
        addConfig(
            "Switch4067",
            ConfigItem.configTypeBool,
            false
        )
    val switch4068 =
        addConfig(
            "Switch4068",
            ConfigItem.configTypeBool,
            false
        )
    val switch4069 =
        addConfig(
            "Switch4069",
            ConfigItem.configTypeBool,
            false
        )
    val switch4070 =
        addConfig(
            "Switch4070",
            ConfigItem.configTypeBool,
            false
        )
    val switch4071 =
        addConfig(
            "Switch4071",
            ConfigItem.configTypeBool,
            false
        )
    val switch4072 =
        addConfig(
            "Switch4072",
            ConfigItem.configTypeBool,
            false
        )
    val switch4073 =
        addConfig(
            "Switch4073",
            ConfigItem.configTypeBool,
            false
        )
    val switch4074 =
        addConfig(
            "Switch4074",
            ConfigItem.configTypeBool,
            false
        )
    val switch4075 =
        addConfig(
            "Switch4075",
            ConfigItem.configTypeBool,
            false
        )
    val switch4076 =
        addConfig(
            "Switch4076",
            ConfigItem.configTypeBool,
            false
        )
    val switch4077 =
        addConfig(
            "Switch4077",
            ConfigItem.configTypeBool,
            false
        )
    val switch4078 =
        addConfig(
            "Switch4078",
            ConfigItem.configTypeBool,
            false
        )
    val switch4079 =
        addConfig(
            "Switch4079",
            ConfigItem.configTypeBool,
            false
        )
    val switch4080 =
        addConfig(
            "Switch4080",
            ConfigItem.configTypeBool,
            false
        )
    val switch4081 =
        addConfig(
            "Switch4081",
            ConfigItem.configTypeBool,
            false
        )
    val switch4082 =
        addConfig(
            "Switch4082",
            ConfigItem.configTypeBool,
            false
        )
    val switch4083 =
        addConfig(
            "Switch4083",
            ConfigItem.configTypeBool,
            false
        )
    val switch4084 =
        addConfig(
            "Switch4084",
            ConfigItem.configTypeBool,
            false
        )
    val switch4085 =
        addConfig(
            "Switch4085",
            ConfigItem.configTypeBool,
            false
        )
    val switch4086 =
        addConfig(
            "Switch4086",
            ConfigItem.configTypeBool,
            false
        )
    val switch4087 =
        addConfig(
            "Switch4087",
            ConfigItem.configTypeBool,
            false
        )
    val switch4088 =
        addConfig(
            "Switch4088",
            ConfigItem.configTypeBool,
            false
        )
    val switch4089 =
        addConfig(
            "Switch4089",
            ConfigItem.configTypeBool,
            false
        )
    val switch4090 =
        addConfig(
            "Switch4090",
            ConfigItem.configTypeBool,
            false
        )
    val switch4091 =
        addConfig(
            "Switch4091",
            ConfigItem.configTypeBool,
            false
        )
    val switch4092 =
        addConfig(
            "Switch4092",
            ConfigItem.configTypeBool,
            false
        )
    val switch4093 =
        addConfig(
            "Switch4093",
            ConfigItem.configTypeBool,
            false
        )
    val switch4094 =
        addConfig(
            "Switch4094",
            ConfigItem.configTypeBool,
            false
        )
    val switch4095 =
        addConfig(
            "Switch4095",
            ConfigItem.configTypeBool,
            false
        )
    val switch4096 =
        addConfig(
            "Switch4096",
            ConfigItem.configTypeBool,
            false
        )
    val switch4097 =
        addConfig(
            "Switch4097",
            ConfigItem.configTypeBool,
            false
        )
    val switch4098 =
        addConfig(
            "Switch4098",
            ConfigItem.configTypeBool,
            false
        )
    val switch4099 =
        addConfig(
            "Switch4099",
            ConfigItem.configTypeBool,
            false
        )
    val switch4100 =
        addConfig(
            "Switch4100",
            ConfigItem.configTypeBool,
            false
        )
    val switch4101 =
        addConfig(
            "Switch4101",
            ConfigItem.configTypeBool,
            false
        )
    val switch4102 =
        addConfig(
            "Switch4102",
            ConfigItem.configTypeBool,
            false
        )
    val switch4103 =
        addConfig(
            "Switch4103",
            ConfigItem.configTypeBool,
            false
        )
    val switch4104 =
        addConfig(
            "Switch4104",
            ConfigItem.configTypeBool,
            false
        )
    val switch4105 =
        addConfig(
            "Switch4105",
            ConfigItem.configTypeBool,
            false
        )
    val switch4106 =
        addConfig(
            "Switch4106",
            ConfigItem.configTypeBool,
            false
        )
    val switch4107 =
        addConfig(
            "Switch4107",
            ConfigItem.configTypeBool,
            false
        )
    val switch4108 =
        addConfig(
            "Switch4108",
            ConfigItem.configTypeBool,
            false
        )
    val switch4109 =
        addConfig(
            "Switch4109",
            ConfigItem.configTypeBool,
            false
        )
    val switch4110 =
        addConfig(
            "Switch4110",
            ConfigItem.configTypeBool,
            false
        )
    val switch4111 =
        addConfig(
            "Switch4111",
            ConfigItem.configTypeBool,
            false
        )
    val switch4112 =
        addConfig(
            "Switch4112",
            ConfigItem.configTypeBool,
            false
        )
    val switch4113 =
        addConfig(
            "Switch4113",
            ConfigItem.configTypeBool,
            false
        )
    val switch4114 =
        addConfig(
            "Switch4114",
            ConfigItem.configTypeBool,
            false
        )
    val switch4115 =
        addConfig(
            "Switch4115",
            ConfigItem.configTypeBool,
            false
        )
    val switch4116 =
        addConfig(
            "Switch4116",
            ConfigItem.configTypeBool,
            false
        )
    val switch4117 =
        addConfig(
            "Switch4117",
            ConfigItem.configTypeBool,
            false
        )
    val switch4118 =
        addConfig(
            "Switch4118",
            ConfigItem.configTypeBool,
            false
        )
    val switch4119 =
        addConfig(
            "Switch4119",
            ConfigItem.configTypeBool,
            false
        )
    val switch4120 =
        addConfig(
            "Switch4120",
            ConfigItem.configTypeBool,
            false
        )
    val switch4121 =
        addConfig(
            "Switch4121",
            ConfigItem.configTypeBool,
            false
        )
    val switch4122 =
        addConfig(
            "Switch4122",
            ConfigItem.configTypeBool,
            false
        )
    val switch4123 =
        addConfig(
            "Switch4123",
            ConfigItem.configTypeBool,
            false
        )
    val switch4124 =
        addConfig(
            "Switch4124",
            ConfigItem.configTypeBool,
            false
        )
    val switch4125 =
        addConfig(
            "Switch4125",
            ConfigItem.configTypeBool,
            false
        )
    val switch4126 =
        addConfig(
            "Switch4126",
            ConfigItem.configTypeBool,
            false
        )
    val switch4127 =
        addConfig(
            "Switch4127",
            ConfigItem.configTypeBool,
            false
        )
    val switch4128 =
        addConfig(
            "Switch4128",
            ConfigItem.configTypeBool,
            false
        )
    val switch4129 =
        addConfig(
            "Switch4129",
            ConfigItem.configTypeBool,
            false
        )
    val switch4130 =
        addConfig(
            "Switch4130",
            ConfigItem.configTypeBool,
            false
        )
    val switch4131 =
        addConfig(
            "Switch4131",
            ConfigItem.configTypeBool,
            false
        )
    val switch4132 =
        addConfig(
            "Switch4132",
            ConfigItem.configTypeBool,
            false
        )
    val switch4133 =
        addConfig(
            "Switch4133",
            ConfigItem.configTypeBool,
            false
        )
    val switch4134 =
        addConfig(
            "Switch4134",
            ConfigItem.configTypeBool,
            false
        )
    val switch4135 =
        addConfig(
            "Switch4135",
            ConfigItem.configTypeBool,
            false
        )
    val switch4136 =
        addConfig(
            "Switch4136",
            ConfigItem.configTypeBool,
            false
        )
    val switch4137 =
        addConfig(
            "Switch4137",
            ConfigItem.configTypeBool,
            false
        )
    val switch4138 =
        addConfig(
            "Switch4138",
            ConfigItem.configTypeBool,
            false
        )
    val switch4139 =
        addConfig(
            "Switch4139",
            ConfigItem.configTypeBool,
            false
        )
    val switch4140 =
        addConfig(
            "Switch4140",
            ConfigItem.configTypeBool,
            false
        )
    val switch4141 =
        addConfig(
            "Switch4141",
            ConfigItem.configTypeBool,
            false
        )
    val switch4142 =
        addConfig(
            "Switch4142",
            ConfigItem.configTypeBool,
            false
        )
    val switch4143 =
        addConfig(
            "Switch4143",
            ConfigItem.configTypeBool,
            false
        )
    val switch4144 =
        addConfig(
            "Switch4144",
            ConfigItem.configTypeBool,
            false
        )
    val switch4145 =
        addConfig(
            "Switch4145",
            ConfigItem.configTypeBool,
            false
        )
    val switch4146 =
        addConfig(
            "Switch4146",
            ConfigItem.configTypeBool,
            false
        )
    val switch4147 =
        addConfig(
            "Switch4147",
            ConfigItem.configTypeBool,
            false
        )
    val switch4148 =
        addConfig(
            "Switch4148",
            ConfigItem.configTypeBool,
            false
        )
    val switch4149 =
        addConfig(
            "Switch4149",
            ConfigItem.configTypeBool,
            false
        )
    val switch4150 =
        addConfig(
            "Switch4150",
            ConfigItem.configTypeBool,
            false
        )
    val switch4151 =
        addConfig(
            "Switch4151",
            ConfigItem.configTypeBool,
            false
        )
    val switch4152 =
        addConfig(
            "Switch4152",
            ConfigItem.configTypeBool,
            false
        )
    val switch4153 =
        addConfig(
            "Switch4153",
            ConfigItem.configTypeBool,
            false
        )
    val switch4154 =
        addConfig(
            "Switch4154",
            ConfigItem.configTypeBool,
            false
        )
    val switch4155 =
        addConfig(
            "Switch4155",
            ConfigItem.configTypeBool,
            false
        )
    val switch4156 =
        addConfig(
            "Switch4156",
            ConfigItem.configTypeBool,
            false
        )
    val switch4157 =
        addConfig(
            "Switch4157",
            ConfigItem.configTypeBool,
            false
        )
    val switch4158 =
        addConfig(
            "Switch4158",
            ConfigItem.configTypeBool,
            false
        )
    val switch4159 =
        addConfig(
            "Switch4159",
            ConfigItem.configTypeBool,
            false
        )
    val switch4160 =
        addConfig(
            "Switch4160",
            ConfigItem.configTypeBool,
            false
        )
    val switch4161 =
        addConfig(
            "Switch4161",
            ConfigItem.configTypeBool,
            false
        )
    val switch4162 =
        addConfig(
            "Switch4162",
            ConfigItem.configTypeBool,
            false
        )
    val switch4163 =
        addConfig(
            "Switch4163",
            ConfigItem.configTypeBool,
            false
        )
    val switch4164 =
        addConfig(
            "Switch4164",
            ConfigItem.configTypeBool,
            false
        )
    val switch4165 =
        addConfig(
            "Switch4165",
            ConfigItem.configTypeBool,
            false
        )
    val switch4166 =
        addConfig(
            "Switch4166",
            ConfigItem.configTypeBool,
            false
        )
    val switch4167 =
        addConfig(
            "Switch4167",
            ConfigItem.configTypeBool,
            false
        )
    val switch4168 =
        addConfig(
            "Switch4168",
            ConfigItem.configTypeBool,
            false
        )
    val switch4169 =
        addConfig(
            "Switch4169",
            ConfigItem.configTypeBool,
            false
        )
    val switch4170 =
        addConfig(
            "Switch4170",
            ConfigItem.configTypeBool,
            false
        )
    val switch4171 =
        addConfig(
            "Switch4171",
            ConfigItem.configTypeBool,
            false
        )
    val switch4172 =
        addConfig(
            "Switch4172",
            ConfigItem.configTypeBool,
            false
        )
    val switch4173 =
        addConfig(
            "Switch4173",
            ConfigItem.configTypeBool,
            false
        )
    val switch4174 =
        addConfig(
            "Switch4174",
            ConfigItem.configTypeBool,
            false
        )
    val switch4175 =
        addConfig(
            "Switch4175",
            ConfigItem.configTypeBool,
            false
        )
    val switch4176 =
        addConfig(
            "Switch4176",
            ConfigItem.configTypeBool,
            false
        )
    val switch4177 =
        addConfig(
            "Switch4177",
            ConfigItem.configTypeBool,
            false
        )
    val switch4178 =
        addConfig(
            "Switch4178",
            ConfigItem.configTypeBool,
            false
        )
    val switch4179 =
        addConfig(
            "Switch4179",
            ConfigItem.configTypeBool,
            false
        )
    val switch4180 =
        addConfig(
            "Switch4180",
            ConfigItem.configTypeBool,
            false
        )
    val switch4181 =
        addConfig(
            "Switch4181",
            ConfigItem.configTypeBool,
            false
        )
    val switch4182 =
        addConfig(
            "Switch4182",
            ConfigItem.configTypeBool,
            false
        )
    val switch4183 =
        addConfig(
            "Switch4183",
            ConfigItem.configTypeBool,
            false
        )
    val switch4184 =
        addConfig(
            "Switch4184",
            ConfigItem.configTypeBool,
            false
        )
    val switch4185 =
        addConfig(
            "Switch4185",
            ConfigItem.configTypeBool,
            false
        )
    val switch4186 =
        addConfig(
            "Switch4186",
            ConfigItem.configTypeBool,
            false
        )
    val switch4187 =
        addConfig(
            "Switch4187",
            ConfigItem.configTypeBool,
            false
        )
    val switch4188 =
        addConfig(
            "Switch4188",
            ConfigItem.configTypeBool,
            false
        )
    val switch4189 =
        addConfig(
            "Switch4189",
            ConfigItem.configTypeBool,
            false
        )
    val switch4190 =
        addConfig(
            "Switch4190",
            ConfigItem.configTypeBool,
            false
        )
    val switch4191 =
        addConfig(
            "Switch4191",
            ConfigItem.configTypeBool,
            false
        )
    val switch4192 =
        addConfig(
            "Switch4192",
            ConfigItem.configTypeBool,
            false
        )
    val switch4193 =
        addConfig(
            "Switch4193",
            ConfigItem.configTypeBool,
            false
        )
    val switch4194 =
        addConfig(
            "Switch4194",
            ConfigItem.configTypeBool,
            false
        )
    val switch4195 =
        addConfig(
            "Switch4195",
            ConfigItem.configTypeBool,
            false
        )
    val switch4196 =
        addConfig(
            "Switch4196",
            ConfigItem.configTypeBool,
            false
        )
    val switch4197 =
        addConfig(
            "Switch4197",
            ConfigItem.configTypeBool,
            false
        )
    val switch4198 =
        addConfig(
            "Switch4198",
            ConfigItem.configTypeBool,
            false
        )
    val switch4199 =
        addConfig(
            "Switch4199",
            ConfigItem.configTypeBool,
            false
        )
    val switch4200 =
        addConfig(
            "Switch4200",
            ConfigItem.configTypeBool,
            false
        )
    val switch4201 =
        addConfig(
            "Switch4201",
            ConfigItem.configTypeBool,
            false
        )
    val switch4202 =
        addConfig(
            "Switch4202",
            ConfigItem.configTypeBool,
            false
        )
    val switch4203 =
        addConfig(
            "Switch4203",
            ConfigItem.configTypeBool,
            false
        )
    val switch4204 =
        addConfig(
            "Switch4204",
            ConfigItem.configTypeBool,
            false
        )
    val switch4205 =
        addConfig(
            "Switch4205",
            ConfigItem.configTypeBool,
            false
        )
    val switch4206 =
        addConfig(
            "Switch4206",
            ConfigItem.configTypeBool,
            false
        )
    val switch4207 =
        addConfig(
            "Switch4207",
            ConfigItem.configTypeBool,
            false
        )
    val switch4208 =
        addConfig(
            "Switch4208",
            ConfigItem.configTypeBool,
            false
        )
    val switch4209 =
        addConfig(
            "Switch4209",
            ConfigItem.configTypeBool,
            false
        )
    val switch4210 =
        addConfig(
            "Switch4210",
            ConfigItem.configTypeBool,
            false
        )
    val switch4211 =
        addConfig(
            "Switch4211",
            ConfigItem.configTypeBool,
            false
        )
    val switch4212 =
        addConfig(
            "Switch4212",
            ConfigItem.configTypeBool,
            false
        )
    val switch4213 =
        addConfig(
            "Switch4213",
            ConfigItem.configTypeBool,
            false
        )
    val switch4214 =
        addConfig(
            "Switch4214",
            ConfigItem.configTypeBool,
            false
        )
    val switch4215 =
        addConfig(
            "Switch4215",
            ConfigItem.configTypeBool,
            false
        )
    val switch4216 =
        addConfig(
            "Switch4216",
            ConfigItem.configTypeBool,
            false
        )
    val switch4217 =
        addConfig(
            "Switch4217",
            ConfigItem.configTypeBool,
            false
        )
    val switch4218 =
        addConfig(
            "Switch4218",
            ConfigItem.configTypeBool,
            false
        )
    val switch4219 =
        addConfig(
            "Switch4219",
            ConfigItem.configTypeBool,
            false
        )
    val switch4220 =
        addConfig(
            "Switch4220",
            ConfigItem.configTypeBool,
            false
        )
    val switch4221 =
        addConfig(
            "Switch4221",
            ConfigItem.configTypeBool,
            false
        )
    val switch4222 =
        addConfig(
            "Switch4222",
            ConfigItem.configTypeBool,
            false
        )
    val switch4223 =
        addConfig(
            "Switch4223",
            ConfigItem.configTypeBool,
            false
        )
    val switch4224 =
        addConfig(
            "Switch4224",
            ConfigItem.configTypeBool,
            false
        )
    val switch4225 =
        addConfig(
            "Switch4225",
            ConfigItem.configTypeBool,
            false
        )
    val switch4226 =
        addConfig(
            "Switch4226",
            ConfigItem.configTypeBool,
            false
        )
    val switch4227 =
        addConfig(
            "Switch4227",
            ConfigItem.configTypeBool,
            false
        )
    val switch4228 =
        addConfig(
            "Switch4228",
            ConfigItem.configTypeBool,
            false
        )
    val switch4229 =
        addConfig(
            "Switch4229",
            ConfigItem.configTypeBool,
            false
        )
    val switch4230 =
        addConfig(
            "Switch4230",
            ConfigItem.configTypeBool,
            false
        )
    val switch4231 =
        addConfig(
            "Switch4231",
            ConfigItem.configTypeBool,
            false
        )
    val switch4232 =
        addConfig(
            "Switch4232",
            ConfigItem.configTypeBool,
            false
        )
    val switch4233 =
        addConfig(
            "Switch4233",
            ConfigItem.configTypeBool,
            false
        )
    val switch4234 =
        addConfig(
            "Switch4234",
            ConfigItem.configTypeBool,
            false
        )
    val switch4235 =
        addConfig(
            "Switch4235",
            ConfigItem.configTypeBool,
            false
        )
    val switch4236 =
        addConfig(
            "Switch4236",
            ConfigItem.configTypeBool,
            false
        )
    val switch4237 =
        addConfig(
            "Switch4237",
            ConfigItem.configTypeBool,
            false
        )
    val switch4238 =
        addConfig(
            "Switch4238",
            ConfigItem.configTypeBool,
            false
        )
    val switch4239 =
        addConfig(
            "Switch4239",
            ConfigItem.configTypeBool,
            false
        )
    val switch4240 =
        addConfig(
            "Switch4240",
            ConfigItem.configTypeBool,
            false
        )
    val switch4241 =
        addConfig(
            "Switch4241",
            ConfigItem.configTypeBool,
            false
        )
    val switch4242 =
        addConfig(
            "Switch4242",
            ConfigItem.configTypeBool,
            false
        )
    val switch4243 =
        addConfig(
            "Switch4243",
            ConfigItem.configTypeBool,
            false
        )
    val switch4244 =
        addConfig(
            "Switch4244",
            ConfigItem.configTypeBool,
            false
        )
    val switch4245 =
        addConfig(
            "Switch4245",
            ConfigItem.configTypeBool,
            false
        )
    val switch4246 =
        addConfig(
            "Switch4246",
            ConfigItem.configTypeBool,
            false
        )
    val switch4247 =
        addConfig(
            "Switch4247",
            ConfigItem.configTypeBool,
            false
        )
    val switch4248 =
        addConfig(
            "Switch4248",
            ConfigItem.configTypeBool,
            false
        )
    val switch4249 =
        addConfig(
            "Switch4249",
            ConfigItem.configTypeBool,
            false
        )
    val switch4250 =
        addConfig(
            "Switch4250",
            ConfigItem.configTypeBool,
            false
        )
    val switch4251 =
        addConfig(
            "Switch4251",
            ConfigItem.configTypeBool,
            false
        )
    val switch4252 =
        addConfig(
            "Switch4252",
            ConfigItem.configTypeBool,
            false
        )
    val switch4253 =
        addConfig(
            "Switch4253",
            ConfigItem.configTypeBool,
            false
        )
    val switch4254 =
        addConfig(
            "Switch4254",
            ConfigItem.configTypeBool,
            false
        )
    val switch4255 =
        addConfig(
            "Switch4255",
            ConfigItem.configTypeBool,
            false
        )
    val switch4256 =
        addConfig(
            "Switch4256",
            ConfigItem.configTypeBool,
            false
        )
    val switch4257 =
        addConfig(
            "Switch4257",
            ConfigItem.configTypeBool,
            false
        )
    val switch4258 =
        addConfig(
            "Switch4258",
            ConfigItem.configTypeBool,
            false
        )
    val switch4259 =
        addConfig(
            "Switch4259",
            ConfigItem.configTypeBool,
            false
        )
    val switch4260 =
        addConfig(
            "Switch4260",
            ConfigItem.configTypeBool,
            false
        )
    val switch4261 =
        addConfig(
            "Switch4261",
            ConfigItem.configTypeBool,
            false
        )
    val switch4262 =
        addConfig(
            "Switch4262",
            ConfigItem.configTypeBool,
            false
        )
    val switch4263 =
        addConfig(
            "Switch4263",
            ConfigItem.configTypeBool,
            false
        )
    val switch4264 =
        addConfig(
            "Switch4264",
            ConfigItem.configTypeBool,
            false
        )
    val switch4265 =
        addConfig(
            "Switch4265",
            ConfigItem.configTypeBool,
            false
        )
    val switch4266 =
        addConfig(
            "Switch4266",
            ConfigItem.configTypeBool,
            false
        )
    val switch4267 =
        addConfig(
            "Switch4267",
            ConfigItem.configTypeBool,
            false
        )
    val switch4268 =
        addConfig(
            "Switch4268",
            ConfigItem.configTypeBool,
            false
        )
    val switch4269 =
        addConfig(
            "Switch4269",
            ConfigItem.configTypeBool,
            false
        )
    val switch4270 =
        addConfig(
            "Switch4270",
            ConfigItem.configTypeBool,
            false
        )
    val switch4271 =
        addConfig(
            "Switch4271",
            ConfigItem.configTypeBool,
            false
        )
    val switch4272 =
        addConfig(
            "Switch4272",
            ConfigItem.configTypeBool,
            false
        )
    val switch4273 =
        addConfig(
            "Switch4273",
            ConfigItem.configTypeBool,
            false
        )
    val switch4274 =
        addConfig(
            "Switch4274",
            ConfigItem.configTypeBool,
            false
        )
    val switch4275 =
        addConfig(
            "Switch4275",
            ConfigItem.configTypeBool,
            false
        )
    val switch4276 =
        addConfig(
            "Switch4276",
            ConfigItem.configTypeBool,
            false
        )
    val switch4277 =
        addConfig(
            "Switch4277",
            ConfigItem.configTypeBool,
            false
        )
    val switch4278 =
        addConfig(
            "Switch4278",
            ConfigItem.configTypeBool,
            false
        )
    val switch4279 =
        addConfig(
            "Switch4279",
            ConfigItem.configTypeBool,
            false
        )
    val switch4280 =
        addConfig(
            "Switch4280",
            ConfigItem.configTypeBool,
            false
        )
    val switch4281 =
        addConfig(
            "Switch4281",
            ConfigItem.configTypeBool,
            false
        )
    val switch4282 =
        addConfig(
            "Switch4282",
            ConfigItem.configTypeBool,
            false
        )
    val switch4283 =
        addConfig(
            "Switch4283",
            ConfigItem.configTypeBool,
            false
        )
    val switch4284 =
        addConfig(
            "Switch4284",
            ConfigItem.configTypeBool,
            false
        )
    val switch4285 =
        addConfig(
            "Switch4285",
            ConfigItem.configTypeBool,
            false
        )
    val switch4286 =
        addConfig(
            "Switch4286",
            ConfigItem.configTypeBool,
            false
        )
    val switch4287 =
        addConfig(
            "Switch4287",
            ConfigItem.configTypeBool,
            false
        )
    val switch4288 =
        addConfig(
            "Switch4288",
            ConfigItem.configTypeBool,
            false
        )
    val switch4289 =
        addConfig(
            "Switch4289",
            ConfigItem.configTypeBool,
            false
        )
    val switch4290 =
        addConfig(
            "Switch4290",
            ConfigItem.configTypeBool,
            false
        )
    val switch4291 =
        addConfig(
            "Switch4291",
            ConfigItem.configTypeBool,
            false
        )
    val switch4292 =
        addConfig(
            "Switch4292",
            ConfigItem.configTypeBool,
            false
        )
    val switch4293 =
        addConfig(
            "Switch4293",
            ConfigItem.configTypeBool,
            false
        )
    val switch4294 =
        addConfig(
            "Switch4294",
            ConfigItem.configTypeBool,
            false
        )
    val switch4295 =
        addConfig(
            "Switch4295",
            ConfigItem.configTypeBool,
            false
        )
    val switch4296 =
        addConfig(
            "Switch4296",
            ConfigItem.configTypeBool,
            false
        )
    val switch4297 =
        addConfig(
            "Switch4297",
            ConfigItem.configTypeBool,
            false
        )
    val switch4298 =
        addConfig(
            "Switch4298",
            ConfigItem.configTypeBool,
            false
        )
    val switch4299 =
        addConfig(
            "Switch4299",
            ConfigItem.configTypeBool,
            false
        )
    val switch4300 =
        addConfig(
            "Switch4300",
            ConfigItem.configTypeBool,
            false
        )
    val switch4301 =
        addConfig(
            "Switch4301",
            ConfigItem.configTypeBool,
            false
        )
    val switch4302 =
        addConfig(
            "Switch4302",
            ConfigItem.configTypeBool,
            false
        )
    val switch4303 =
        addConfig(
            "Switch4303",
            ConfigItem.configTypeBool,
            false
        )
    val switch4304 =
        addConfig(
            "Switch4304",
            ConfigItem.configTypeBool,
            false
        )
    val switch4305 =
        addConfig(
            "Switch4305",
            ConfigItem.configTypeBool,
            false
        )
    val switch4306 =
        addConfig(
            "Switch4306",
            ConfigItem.configTypeBool,
            false
        )
    val switch4307 =
        addConfig(
            "Switch4307",
            ConfigItem.configTypeBool,
            false
        )
    val switch4308 =
        addConfig(
            "Switch4308",
            ConfigItem.configTypeBool,
            false
        )
    val switch4309 =
        addConfig(
            "Switch4309",
            ConfigItem.configTypeBool,
            false
        )
    val switch4310 =
        addConfig(
            "Switch4310",
            ConfigItem.configTypeBool,
            false
        )
    val switch4311 =
        addConfig(
            "Switch4311",
            ConfigItem.configTypeBool,
            false
        )
    val switch4312 =
        addConfig(
            "Switch4312",
            ConfigItem.configTypeBool,
            false
        )
    val switch4313 =
        addConfig(
            "Switch4313",
            ConfigItem.configTypeBool,
            false
        )
    val switch4314 =
        addConfig(
            "Switch4314",
            ConfigItem.configTypeBool,
            false
        )
    val switch4315 =
        addConfig(
            "Switch4315",
            ConfigItem.configTypeBool,
            false
        )
    val switch4316 =
        addConfig(
            "Switch4316",
            ConfigItem.configTypeBool,
            false
        )
    val switch4317 =
        addConfig(
            "Switch4317",
            ConfigItem.configTypeBool,
            false
        )
    val switch4318 =
        addConfig(
            "Switch4318",
            ConfigItem.configTypeBool,
            false
        )
    val switch4319 =
        addConfig(
            "Switch4319",
            ConfigItem.configTypeBool,
            false
        )
    val switch4320 =
        addConfig(
            "Switch4320",
            ConfigItem.configTypeBool,
            false
        )
    val switch4321 =
        addConfig(
            "Switch4321",
            ConfigItem.configTypeBool,
            false
        )
    val switch4322 =
        addConfig(
            "Switch4322",
            ConfigItem.configTypeBool,
            false
        )
    val switch4323 =
        addConfig(
            "Switch4323",
            ConfigItem.configTypeBool,
            false
        )
    val switch4324 =
        addConfig(
            "Switch4324",
            ConfigItem.configTypeBool,
            false
        )
    val switch4325 =
        addConfig(
            "Switch4325",
            ConfigItem.configTypeBool,
            false
        )
    val switch4326 =
        addConfig(
            "Switch4326",
            ConfigItem.configTypeBool,
            false
        )
    val switch4327 =
        addConfig(
            "Switch4327",
            ConfigItem.configTypeBool,
            false
        )
    val switch4328 =
        addConfig(
            "Switch4328",
            ConfigItem.configTypeBool,
            false
        )
    val switch4329 =
        addConfig(
            "Switch4329",
            ConfigItem.configTypeBool,
            false
        )
    val switch4330 =
        addConfig(
            "Switch4330",
            ConfigItem.configTypeBool,
            false
        )
    val switch4331 =
        addConfig(
            "Switch4331",
            ConfigItem.configTypeBool,
            false
        )
    val switch4332 =
        addConfig(
            "Switch4332",
            ConfigItem.configTypeBool,
            false
        )
    val switch4333 =
        addConfig(
            "Switch4333",
            ConfigItem.configTypeBool,
            false
        )
    val switch4334 =
        addConfig(
            "Switch4334",
            ConfigItem.configTypeBool,
            false
        )
    val switch4335 =
        addConfig(
            "Switch4335",
            ConfigItem.configTypeBool,
            false
        )
    val switch4336 =
        addConfig(
            "Switch4336",
            ConfigItem.configTypeBool,
            false
        )
    val switch4337 =
        addConfig(
            "Switch4337",
            ConfigItem.configTypeBool,
            false
        )
    val switch4338 =
        addConfig(
            "Switch4338",
            ConfigItem.configTypeBool,
            false
        )
    val switch4339 =
        addConfig(
            "Switch4339",
            ConfigItem.configTypeBool,
            false
        )
    val switch4340 =
        addConfig(
            "Switch4340",
            ConfigItem.configTypeBool,
            false
        )
    val switch4341 =
        addConfig(
            "Switch4341",
            ConfigItem.configTypeBool,
            false
        )
    val switch4342 =
        addConfig(
            "Switch4342",
            ConfigItem.configTypeBool,
            false
        )
    val switch4343 =
        addConfig(
            "Switch4343",
            ConfigItem.configTypeBool,
            false
        )
    val switch4344 =
        addConfig(
            "Switch4344",
            ConfigItem.configTypeBool,
            false
        )
    val switch4345 =
        addConfig(
            "Switch4345",
            ConfigItem.configTypeBool,
            false
        )
    val switch4346 =
        addConfig(
            "Switch4346",
            ConfigItem.configTypeBool,
            false
        )
    val switch4347 =
        addConfig(
            "Switch4347",
            ConfigItem.configTypeBool,
            false
        )
    val switch4348 =
        addConfig(
            "Switch4348",
            ConfigItem.configTypeBool,
            false
        )
    val switch4349 =
        addConfig(
            "Switch4349",
            ConfigItem.configTypeBool,
            false
        )
    val switch4350 =
        addConfig(
            "Switch4350",
            ConfigItem.configTypeBool,
            false
        )
    val switch4351 =
        addConfig(
            "Switch4351",
            ConfigItem.configTypeBool,
            false
        )
    val switch4352 =
        addConfig(
            "Switch4352",
            ConfigItem.configTypeBool,
            false
        )
    val switch4353 =
        addConfig(
            "Switch4353",
            ConfigItem.configTypeBool,
            false
        )
    val switch4354 =
        addConfig(
            "Switch4354",
            ConfigItem.configTypeBool,
            false
        )
    val switch4355 =
        addConfig(
            "Switch4355",
            ConfigItem.configTypeBool,
            false
        )
    val switch4356 =
        addConfig(
            "Switch4356",
            ConfigItem.configTypeBool,
            false
        )
    val switch4357 =
        addConfig(
            "Switch4357",
            ConfigItem.configTypeBool,
            false
        )
    val switch4358 =
        addConfig(
            "Switch4358",
            ConfigItem.configTypeBool,
            false
        )
    val switch4359 =
        addConfig(
            "Switch4359",
            ConfigItem.configTypeBool,
            false
        )
    val switch4360 =
        addConfig(
            "Switch4360",
            ConfigItem.configTypeBool,
            false
        )
    val switch4361 =
        addConfig(
            "Switch4361",
            ConfigItem.configTypeBool,
            false
        )
    val switch4362 =
        addConfig(
            "Switch4362",
            ConfigItem.configTypeBool,
            false
        )
    val switch4363 =
        addConfig(
            "Switch4363",
            ConfigItem.configTypeBool,
            false
        )
    val switch4364 =
        addConfig(
            "Switch4364",
            ConfigItem.configTypeBool,
            false
        )
    val switch4365 =
        addConfig(
            "Switch4365",
            ConfigItem.configTypeBool,
            false
        )
    val switch4366 =
        addConfig(
            "Switch4366",
            ConfigItem.configTypeBool,
            false
        )
    val switch4367 =
        addConfig(
            "Switch4367",
            ConfigItem.configTypeBool,
            false
        )
    val switch4368 =
        addConfig(
            "Switch4368",
            ConfigItem.configTypeBool,
            false
        )
    val switch4369 =
        addConfig(
            "Switch4369",
            ConfigItem.configTypeBool,
            false
        )
    val switch4370 =
        addConfig(
            "Switch4370",
            ConfigItem.configTypeBool,
            false
        )
    val switch4371 =
        addConfig(
            "Switch4371",
            ConfigItem.configTypeBool,
            false
        )
    val switch4372 =
        addConfig(
            "Switch4372",
            ConfigItem.configTypeBool,
            false
        )
    val switch4373 =
        addConfig(
            "Switch4373",
            ConfigItem.configTypeBool,
            false
        )
    val switch4374 =
        addConfig(
            "Switch4374",
            ConfigItem.configTypeBool,
            false
        )
    val switch4375 =
        addConfig(
            "Switch4375",
            ConfigItem.configTypeBool,
            false
        )
    val switch4376 =
        addConfig(
            "Switch4376",
            ConfigItem.configTypeBool,
            false
        )
    val switch4377 =
        addConfig(
            "Switch4377",
            ConfigItem.configTypeBool,
            false
        )
    val switch4378 =
        addConfig(
            "Switch4378",
            ConfigItem.configTypeBool,
            false
        )
    val switch4379 =
        addConfig(
            "Switch4379",
            ConfigItem.configTypeBool,
            false
        )
    val switch4380 =
        addConfig(
            "Switch4380",
            ConfigItem.configTypeBool,
            false
        )
    val switch4381 =
        addConfig(
            "Switch4381",
            ConfigItem.configTypeBool,
            false
        )
    val switch4382 =
        addConfig(
            "Switch4382",
            ConfigItem.configTypeBool,
            false
        )
    val switch4383 =
        addConfig(
            "Switch4383",
            ConfigItem.configTypeBool,
            false
        )
    val switch4384 =
        addConfig(
            "Switch4384",
            ConfigItem.configTypeBool,
            false
        )
    val switch4385 =
        addConfig(
            "Switch4385",
            ConfigItem.configTypeBool,
            false
        )
    val switch4386 =
        addConfig(
            "Switch4386",
            ConfigItem.configTypeBool,
            false
        )
    val switch4387 =
        addConfig(
            "Switch4387",
            ConfigItem.configTypeBool,
            false
        )
    val switch4388 =
        addConfig(
            "Switch4388",
            ConfigItem.configTypeBool,
            false
        )
    val switch4389 =
        addConfig(
            "Switch4389",
            ConfigItem.configTypeBool,
            false
        )
    val switch4390 =
        addConfig(
            "Switch4390",
            ConfigItem.configTypeBool,
            false
        )
    val switch4391 =
        addConfig(
            "Switch4391",
            ConfigItem.configTypeBool,
            false
        )
    val switch4392 =
        addConfig(
            "Switch4392",
            ConfigItem.configTypeBool,
            false
        )
    val switch4393 =
        addConfig(
            "Switch4393",
            ConfigItem.configTypeBool,
            false
        )
    val switch4394 =
        addConfig(
            "Switch4394",
            ConfigItem.configTypeBool,
            false
        )
    val switch4395 =
        addConfig(
            "Switch4395",
            ConfigItem.configTypeBool,
            false
        )
    val switch4396 =
        addConfig(
            "Switch4396",
            ConfigItem.configTypeBool,
            false
        )
    val switch4397 =
        addConfig(
            "Switch4397",
            ConfigItem.configTypeBool,
            false
        )
    val switch4398 =
        addConfig(
            "Switch4398",
            ConfigItem.configTypeBool,
            false
        )
    val switch4399 =
        addConfig(
            "Switch4399",
            ConfigItem.configTypeBool,
            false
        )
    val switch4400 =
        addConfig(
            "Switch4400",
            ConfigItem.configTypeBool,
            false
        )
    val switch4401 =
        addConfig(
            "Switch4401",
            ConfigItem.configTypeBool,
            false
        )
    val switch4402 =
        addConfig(
            "Switch4402",
            ConfigItem.configTypeBool,
            false
        )
    val switch4403 =
        addConfig(
            "Switch4403",
            ConfigItem.configTypeBool,
            false
        )
    val switch4404 =
        addConfig(
            "Switch4404",
            ConfigItem.configTypeBool,
            false
        )
    val switch4405 =
        addConfig(
            "Switch4405",
            ConfigItem.configTypeBool,
            false
        )
    val switch4406 =
        addConfig(
            "Switch4406",
            ConfigItem.configTypeBool,
            false
        )
    val switch4407 =
        addConfig(
            "Switch4407",
            ConfigItem.configTypeBool,
            false
        )
    val switch4408 =
        addConfig(
            "Switch4408",
            ConfigItem.configTypeBool,
            false
        )
    val switch4409 =
        addConfig(
            "Switch4409",
            ConfigItem.configTypeBool,
            false
        )
    val switch4410 =
        addConfig(
            "Switch4410",
            ConfigItem.configTypeBool,
            false
        )
    val switch4411 =
        addConfig(
            "Switch4411",
            ConfigItem.configTypeBool,
            false
        )
    val switch4412 =
        addConfig(
            "Switch4412",
            ConfigItem.configTypeBool,
            false
        )
    val switch4413 =
        addConfig(
            "Switch4413",
            ConfigItem.configTypeBool,
            false
        )
    val switch4414 =
        addConfig(
            "Switch4414",
            ConfigItem.configTypeBool,
            false
        )
    val switch4415 =
        addConfig(
            "Switch4415",
            ConfigItem.configTypeBool,
            false
        )
    val switch4416 =
        addConfig(
            "Switch4416",
            ConfigItem.configTypeBool,
            false
        )
    val switch4417 =
        addConfig(
            "Switch4417",
            ConfigItem.configTypeBool,
            false
        )
    val switch4418 =
        addConfig(
            "Switch4418",
            ConfigItem.configTypeBool,
            false
        )
    val switch4419 =
        addConfig(
            "Switch4419",
            ConfigItem.configTypeBool,
            false
        )
    val switch4420 =
        addConfig(
            "Switch4420",
            ConfigItem.configTypeBool,
            false
        )
    val switch4421 =
        addConfig(
            "Switch4421",
            ConfigItem.configTypeBool,
            false
        )
    val switch4422 =
        addConfig(
            "Switch4422",
            ConfigItem.configTypeBool,
            false
        )
    val switch4423 =
        addConfig(
            "Switch4423",
            ConfigItem.configTypeBool,
            false
        )
    val switch4424 =
        addConfig(
            "Switch4424",
            ConfigItem.configTypeBool,
            false
        )
    val switch4425 =
        addConfig(
            "Switch4425",
            ConfigItem.configTypeBool,
            false
        )
    val switch4426 =
        addConfig(
            "Switch4426",
            ConfigItem.configTypeBool,
            false
        )
    val switch4427 =
        addConfig(
            "Switch4427",
            ConfigItem.configTypeBool,
            false
        )
    val switch4428 =
        addConfig(
            "Switch4428",
            ConfigItem.configTypeBool,
            false
        )
    val switch4429 =
        addConfig(
            "Switch4429",
            ConfigItem.configTypeBool,
            false
        )
    val switch4430 =
        addConfig(
            "Switch4430",
            ConfigItem.configTypeBool,
            false
        )
    val switch4431 =
        addConfig(
            "Switch4431",
            ConfigItem.configTypeBool,
            false
        )
    val switch4432 =
        addConfig(
            "Switch4432",
            ConfigItem.configTypeBool,
            false
        )
    val switch4433 =
        addConfig(
            "Switch4433",
            ConfigItem.configTypeBool,
            false
        )
    val switch4434 =
        addConfig(
            "Switch4434",
            ConfigItem.configTypeBool,
            false
        )
    val switch4435 =
        addConfig(
            "Switch4435",
            ConfigItem.configTypeBool,
            false
        )
    val switch4436 =
        addConfig(
            "Switch4436",
            ConfigItem.configTypeBool,
            false
        )
    val switch4437 =
        addConfig(
            "Switch4437",
            ConfigItem.configTypeBool,
            false
        )
    val switch4438 =
        addConfig(
            "Switch4438",
            ConfigItem.configTypeBool,
            false
        )
    val switch4439 =
        addConfig(
            "Switch4439",
            ConfigItem.configTypeBool,
            false
        )
    val switch4440 =
        addConfig(
            "Switch4440",
            ConfigItem.configTypeBool,
            false
        )
    val switch4441 =
        addConfig(
            "Switch4441",
            ConfigItem.configTypeBool,
            false
        )
    val switch4442 =
        addConfig(
            "Switch4442",
            ConfigItem.configTypeBool,
            false
        )
    val switch4443 =
        addConfig(
            "Switch4443",
            ConfigItem.configTypeBool,
            false
        )
    val switch4444 =
        addConfig(
            "Switch4444",
            ConfigItem.configTypeBool,
            false
        )
    val switch4445 =
        addConfig(
            "Switch4445",
            ConfigItem.configTypeBool,
            false
        )
    val switch4446 =
        addConfig(
            "Switch4446",
            ConfigItem.configTypeBool,
            false
        )
    val switch4447 =
        addConfig(
            "Switch4447",
            ConfigItem.configTypeBool,
            false
        )
    val switch4448 =
        addConfig(
            "Switch4448",
            ConfigItem.configTypeBool,
            false
        )
    val switch4449 =
        addConfig(
            "Switch4449",
            ConfigItem.configTypeBool,
            false
        )
    val switch4450 =
        addConfig(
            "Switch4450",
            ConfigItem.configTypeBool,
            false
        )
    val switch4451 =
        addConfig(
            "Switch4451",
            ConfigItem.configTypeBool,
            false
        )
    val switch4452 =
        addConfig(
            "Switch4452",
            ConfigItem.configTypeBool,
            false
        )
    val switch4453 =
        addConfig(
            "Switch4453",
            ConfigItem.configTypeBool,
            false
        )
    val switch4454 =
        addConfig(
            "Switch4454",
            ConfigItem.configTypeBool,
            false
        )
    val switch4455 =
        addConfig(
            "Switch4455",
            ConfigItem.configTypeBool,
            false
        )
    val switch4456 =
        addConfig(
            "Switch4456",
            ConfigItem.configTypeBool,
            false
        )
    val switch4457 =
        addConfig(
            "Switch4457",
            ConfigItem.configTypeBool,
            false
        )
    val switch4458 =
        addConfig(
            "Switch4458",
            ConfigItem.configTypeBool,
            false
        )
    val switch4459 =
        addConfig(
            "Switch4459",
            ConfigItem.configTypeBool,
            false
        )
    val switch4460 =
        addConfig(
            "Switch4460",
            ConfigItem.configTypeBool,
            false
        )
    val switch4461 =
        addConfig(
            "Switch4461",
            ConfigItem.configTypeBool,
            false
        )
    val switch4462 =
        addConfig(
            "Switch4462",
            ConfigItem.configTypeBool,
            false
        )
    val switch4463 =
        addConfig(
            "Switch4463",
            ConfigItem.configTypeBool,
            false
        )
    val switch4464 =
        addConfig(
            "Switch4464",
            ConfigItem.configTypeBool,
            false
        )
    val switch4465 =
        addConfig(
            "Switch4465",
            ConfigItem.configTypeBool,
            false
        )
    val switch4466 =
        addConfig(
            "Switch4466",
            ConfigItem.configTypeBool,
            false
        )
    val switch4467 =
        addConfig(
            "Switch4467",
            ConfigItem.configTypeBool,
            false
        )
    val switch4468 =
        addConfig(
            "Switch4468",
            ConfigItem.configTypeBool,
            false
        )
    val switch4469 =
        addConfig(
            "Switch4469",
            ConfigItem.configTypeBool,
            false
        )
    val switch4470 =
        addConfig(
            "Switch4470",
            ConfigItem.configTypeBool,
            false
        )
    val switch4471 =
        addConfig(
            "Switch4471",
            ConfigItem.configTypeBool,
            false
        )
    val switch4472 =
        addConfig(
            "Switch4472",
            ConfigItem.configTypeBool,
            false
        )
    val switch4473 =
        addConfig(
            "Switch4473",
            ConfigItem.configTypeBool,
            false
        )
    val switch4474 =
        addConfig(
            "Switch4474",
            ConfigItem.configTypeBool,
            false
        )
    val switch4475 =
        addConfig(
            "Switch4475",
            ConfigItem.configTypeBool,
            false
        )
    val switch4476 =
        addConfig(
            "Switch4476",
            ConfigItem.configTypeBool,
            false
        )
    val switch4477 =
        addConfig(
            "Switch4477",
            ConfigItem.configTypeBool,
            false
        )
    val switch4478 =
        addConfig(
            "Switch4478",
            ConfigItem.configTypeBool,
            false
        )
    val switch4479 =
        addConfig(
            "Switch4479",
            ConfigItem.configTypeBool,
            false
        )
    val switch4480 =
        addConfig(
            "Switch4480",
            ConfigItem.configTypeBool,
            false
        )
    val switch4481 =
        addConfig(
            "Switch4481",
            ConfigItem.configTypeBool,
            false
        )
    val switch4482 =
        addConfig(
            "Switch4482",
            ConfigItem.configTypeBool,
            false
        )
    val switch4483 =
        addConfig(
            "Switch4483",
            ConfigItem.configTypeBool,
            false
        )
    val switch4484 =
        addConfig(
            "Switch4484",
            ConfigItem.configTypeBool,
            false
        )
    val switch4485 =
        addConfig(
            "Switch4485",
            ConfigItem.configTypeBool,
            false
        )
    val switch4486 =
        addConfig(
            "Switch4486",
            ConfigItem.configTypeBool,
            false
        )
    val switch4487 =
        addConfig(
            "Switch4487",
            ConfigItem.configTypeBool,
            false
        )
    val switch4488 =
        addConfig(
            "Switch4488",
            ConfigItem.configTypeBool,
            false
        )
    val switch4489 =
        addConfig(
            "Switch4489",
            ConfigItem.configTypeBool,
            false
        )
    val switch4490 =
        addConfig(
            "Switch4490",
            ConfigItem.configTypeBool,
            false
        )
    val switch4491 =
        addConfig(
            "Switch4491",
            ConfigItem.configTypeBool,
            false
        )
    val switch4492 =
        addConfig(
            "Switch4492",
            ConfigItem.configTypeBool,
            false
        )
    val switch4493 =
        addConfig(
            "Switch4493",
            ConfigItem.configTypeBool,
            false
        )
    val switch4494 =
        addConfig(
            "Switch4494",
            ConfigItem.configTypeBool,
            false
        )
    val switch4495 =
        addConfig(
            "Switch4495",
            ConfigItem.configTypeBool,
            false
        )
    val switch4496 =
        addConfig(
            "Switch4496",
            ConfigItem.configTypeBool,
            false
        )
    val switch4497 =
        addConfig(
            "Switch4497",
            ConfigItem.configTypeBool,
            false
        )
    val switch4498 =
        addConfig(
            "Switch4498",
            ConfigItem.configTypeBool,
            false
        )
    val switch4499 =
        addConfig(
            "Switch4499",
            ConfigItem.configTypeBool,
            false
        )
    val switch4500 =
        addConfig(
            "Switch4500",
            ConfigItem.configTypeBool,
            false
        )
    val switch4501 =
        addConfig(
            "Switch4501",
            ConfigItem.configTypeBool,
            false
        )
    val switch4502 =
        addConfig(
            "Switch4502",
            ConfigItem.configTypeBool,
            false
        )
    val switch4503 =
        addConfig(
            "Switch4503",
            ConfigItem.configTypeBool,
            false
        )
    val switch4504 =
        addConfig(
            "Switch4504",
            ConfigItem.configTypeBool,
            false
        )
    val switch4505 =
        addConfig(
            "Switch4505",
            ConfigItem.configTypeBool,
            false
        )
    val switch4506 =
        addConfig(
            "Switch4506",
            ConfigItem.configTypeBool,
            false
        )
    val switch4507 =
        addConfig(
            "Switch4507",
            ConfigItem.configTypeBool,
            false
        )
    val switch4508 =
        addConfig(
            "Switch4508",
            ConfigItem.configTypeBool,
            false
        )
    val switch4509 =
        addConfig(
            "Switch4509",
            ConfigItem.configTypeBool,
            false
        )
    val switch4510 =
        addConfig(
            "Switch4510",
            ConfigItem.configTypeBool,
            false
        )
    val switch4511 =
        addConfig(
            "Switch4511",
            ConfigItem.configTypeBool,
            false
        )
    val switch4512 =
        addConfig(
            "Switch4512",
            ConfigItem.configTypeBool,
            false
        )
    val switch4513 =
        addConfig(
            "Switch4513",
            ConfigItem.configTypeBool,
            false
        )
    val switch4514 =
        addConfig(
            "Switch4514",
            ConfigItem.configTypeBool,
            false
        )
    val switch4515 =
        addConfig(
            "Switch4515",
            ConfigItem.configTypeBool,
            false
        )
    val switch4516 =
        addConfig(
            "Switch4516",
            ConfigItem.configTypeBool,
            false
        )
    val switch4517 =
        addConfig(
            "Switch4517",
            ConfigItem.configTypeBool,
            false
        )
    val switch4518 =
        addConfig(
            "Switch4518",
            ConfigItem.configTypeBool,
            false
        )
    val switch4519 =
        addConfig(
            "Switch4519",
            ConfigItem.configTypeBool,
            false
        )
    val switch4520 =
        addConfig(
            "Switch4520",
            ConfigItem.configTypeBool,
            false
        )
    val switch4521 =
        addConfig(
            "Switch4521",
            ConfigItem.configTypeBool,
            false
        )
    val switch4522 =
        addConfig(
            "Switch4522",
            ConfigItem.configTypeBool,
            false
        )
    val switch4523 =
        addConfig(
            "Switch4523",
            ConfigItem.configTypeBool,
            false
        )
    val switch4524 =
        addConfig(
            "Switch4524",
            ConfigItem.configTypeBool,
            false
        )
    val switch4525 =
        addConfig(
            "Switch4525",
            ConfigItem.configTypeBool,
            false
        )
    val switch4526 =
        addConfig(
            "Switch4526",
            ConfigItem.configTypeBool,
            false
        )
    val switch4527 =
        addConfig(
            "Switch4527",
            ConfigItem.configTypeBool,
            false
        )
    val switch4528 =
        addConfig(
            "Switch4528",
            ConfigItem.configTypeBool,
            false
        )
    val switch4529 =
        addConfig(
            "Switch4529",
            ConfigItem.configTypeBool,
            false
        )
    val switch4530 =
        addConfig(
            "Switch4530",
            ConfigItem.configTypeBool,
            false
        )
    val switch4531 =
        addConfig(
            "Switch4531",
            ConfigItem.configTypeBool,
            false
        )
    val switch4532 =
        addConfig(
            "Switch4532",
            ConfigItem.configTypeBool,
            false
        )
    val switch4533 =
        addConfig(
            "Switch4533",
            ConfigItem.configTypeBool,
            false
        )
    val switch4534 =
        addConfig(
            "Switch4534",
            ConfigItem.configTypeBool,
            false
        )
    val switch4535 =
        addConfig(
            "Switch4535",
            ConfigItem.configTypeBool,
            false
        )
    val switch4536 =
        addConfig(
            "Switch4536",
            ConfigItem.configTypeBool,
            false
        )
    val switch4537 =
        addConfig(
            "Switch4537",
            ConfigItem.configTypeBool,
            false
        )
    val switch4538 =
        addConfig(
            "Switch4538",
            ConfigItem.configTypeBool,
            false
        )
    val switch4539 =
        addConfig(
            "Switch4539",
            ConfigItem.configTypeBool,
            false
        )
    val switch4540 =
        addConfig(
            "Switch4540",
            ConfigItem.configTypeBool,
            false
        )
    val switch4541 =
        addConfig(
            "Switch4541",
            ConfigItem.configTypeBool,
            false
        )
    val switch4542 =
        addConfig(
            "Switch4542",
            ConfigItem.configTypeBool,
            false
        )
    val switch4543 =
        addConfig(
            "Switch4543",
            ConfigItem.configTypeBool,
            false
        )
    val switch4544 =
        addConfig(
            "Switch4544",
            ConfigItem.configTypeBool,
            false
        )
    val switch4545 =
        addConfig(
            "Switch4545",
            ConfigItem.configTypeBool,
            false
        )
    val switch4546 =
        addConfig(
            "Switch4546",
            ConfigItem.configTypeBool,
            false
        )
    val switch4547 =
        addConfig(
            "Switch4547",
            ConfigItem.configTypeBool,
            false
        )
    val switch4548 =
        addConfig(
            "Switch4548",
            ConfigItem.configTypeBool,
            false
        )
    val switch4549 =
        addConfig(
            "Switch4549",
            ConfigItem.configTypeBool,
            false
        )
    val switch4550 =
        addConfig(
            "Switch4550",
            ConfigItem.configTypeBool,
            false
        )
    val switch4551 =
        addConfig(
            "Switch4551",
            ConfigItem.configTypeBool,
            false
        )
    val switch4552 =
        addConfig(
            "Switch4552",
            ConfigItem.configTypeBool,
            false
        )
    val switch4553 =
        addConfig(
            "Switch4553",
            ConfigItem.configTypeBool,
            false
        )
    val switch4554 =
        addConfig(
            "Switch4554",
            ConfigItem.configTypeBool,
            false
        )
    val switch4555 =
        addConfig(
            "Switch4555",
            ConfigItem.configTypeBool,
            false
        )
    val switch4556 =
        addConfig(
            "Switch4556",
            ConfigItem.configTypeBool,
            false
        )
    val switch4557 =
        addConfig(
            "Switch4557",
            ConfigItem.configTypeBool,
            false
        )
    val switch4558 =
        addConfig(
            "Switch4558",
            ConfigItem.configTypeBool,
            false
        )
    val switch4559 =
        addConfig(
            "Switch4559",
            ConfigItem.configTypeBool,
            false
        )
    val switch4560 =
        addConfig(
            "Switch4560",
            ConfigItem.configTypeBool,
            false
        )
    val switch4561 =
        addConfig(
            "Switch4561",
            ConfigItem.configTypeBool,
            false
        )
    val switch4562 =
        addConfig(
            "Switch4562",
            ConfigItem.configTypeBool,
            false
        )
    val switch4563 =
        addConfig(
            "Switch4563",
            ConfigItem.configTypeBool,
            false
        )
    val switch4564 =
        addConfig(
            "Switch4564",
            ConfigItem.configTypeBool,
            false
        )
    val switch4565 =
        addConfig(
            "Switch4565",
            ConfigItem.configTypeBool,
            false
        )
    val switch4566 =
        addConfig(
            "Switch4566",
            ConfigItem.configTypeBool,
            false
        )
    val switch4567 =
        addConfig(
            "Switch4567",
            ConfigItem.configTypeBool,
            false
        )
    val switch4568 =
        addConfig(
            "Switch4568",
            ConfigItem.configTypeBool,
            false
        )
    val switch4569 =
        addConfig(
            "Switch4569",
            ConfigItem.configTypeBool,
            false
        )
    val switch4570 =
        addConfig(
            "Switch4570",
            ConfigItem.configTypeBool,
            false
        )
    val switch4571 =
        addConfig(
            "Switch4571",
            ConfigItem.configTypeBool,
            false
        )
    val switch4572 =
        addConfig(
            "Switch4572",
            ConfigItem.configTypeBool,
            false
        )
    val switch4573 =
        addConfig(
            "Switch4573",
            ConfigItem.configTypeBool,
            false
        )
    val switch4574 =
        addConfig(
            "Switch4574",
            ConfigItem.configTypeBool,
            false
        )
    val switch4575 =
        addConfig(
            "Switch4575",
            ConfigItem.configTypeBool,
            false
        )
    val switch4576 =
        addConfig(
            "Switch4576",
            ConfigItem.configTypeBool,
            false
        )
    val switch4577 =
        addConfig(
            "Switch4577",
            ConfigItem.configTypeBool,
            false
        )
    val switch4578 =
        addConfig(
            "Switch4578",
            ConfigItem.configTypeBool,
            false
        )
    val switch4579 =
        addConfig(
            "Switch4579",
            ConfigItem.configTypeBool,
            false
        )
    val switch4580 =
        addConfig(
            "Switch4580",
            ConfigItem.configTypeBool,
            false
        )
    val switch4581 =
        addConfig(
            "Switch4581",
            ConfigItem.configTypeBool,
            false
        )
    val switch4582 =
        addConfig(
            "Switch4582",
            ConfigItem.configTypeBool,
            false
        )
    val switch4583 =
        addConfig(
            "Switch4583",
            ConfigItem.configTypeBool,
            false
        )
    val switch4584 =
        addConfig(
            "Switch4584",
            ConfigItem.configTypeBool,
            false
        )
    val switch4585 =
        addConfig(
            "Switch4585",
            ConfigItem.configTypeBool,
            false
        )
    val switch4586 =
        addConfig(
            "Switch4586",
            ConfigItem.configTypeBool,
            false
        )
    val switch4587 =
        addConfig(
            "Switch4587",
            ConfigItem.configTypeBool,
            false
        )
    val switch4588 =
        addConfig(
            "Switch4588",
            ConfigItem.configTypeBool,
            false
        )
    val switch4589 =
        addConfig(
            "Switch4589",
            ConfigItem.configTypeBool,
            false
        )
    val switch4590 =
        addConfig(
            "Switch4590",
            ConfigItem.configTypeBool,
            false
        )
    val switch4591 =
        addConfig(
            "Switch4591",
            ConfigItem.configTypeBool,
            false
        )
    val switch4592 =
        addConfig(
            "Switch4592",
            ConfigItem.configTypeBool,
            false
        )
    val switch4593 =
        addConfig(
            "Switch4593",
            ConfigItem.configTypeBool,
            false
        )
    val switch4594 =
        addConfig(
            "Switch4594",
            ConfigItem.configTypeBool,
            false
        )
    val switch4595 =
        addConfig(
            "Switch4595",
            ConfigItem.configTypeBool,
            false
        )
    val switch4596 =
        addConfig(
            "Switch4596",
            ConfigItem.configTypeBool,
            false
        )
    val switch4597 =
        addConfig(
            "Switch4597",
            ConfigItem.configTypeBool,
            false
        )
    val switch4598 =
        addConfig(
            "Switch4598",
            ConfigItem.configTypeBool,
            false
        )
    val switch4599 =
        addConfig(
            "Switch4599",
            ConfigItem.configTypeBool,
            false
        )
    val switch4600 =
        addConfig(
            "Switch4600",
            ConfigItem.configTypeBool,
            false
        )
    val switch4601 =
        addConfig(
            "Switch4601",
            ConfigItem.configTypeBool,
            false
        )
    val switch4602 =
        addConfig(
            "Switch4602",
            ConfigItem.configTypeBool,
            false
        )
    val switch4603 =
        addConfig(
            "Switch4603",
            ConfigItem.configTypeBool,
            false
        )
    val switch4604 =
        addConfig(
            "Switch4604",
            ConfigItem.configTypeBool,
            false
        )
    val switch4605 =
        addConfig(
            "Switch4605",
            ConfigItem.configTypeBool,
            false
        )
    val switch4606 =
        addConfig(
            "Switch4606",
            ConfigItem.configTypeBool,
            false
        )
    val switch4607 =
        addConfig(
            "Switch4607",
            ConfigItem.configTypeBool,
            false
        )
    val switch4608 =
        addConfig(
            "Switch4608",
            ConfigItem.configTypeBool,
            false
        )
    val switch4609 =
        addConfig(
            "Switch4609",
            ConfigItem.configTypeBool,
            false
        )
    val switch4610 =
        addConfig(
            "Switch4610",
            ConfigItem.configTypeBool,
            false
        )
    val switch4611 =
        addConfig(
            "Switch4611",
            ConfigItem.configTypeBool,
            false
        )
    val switch4612 =
        addConfig(
            "Switch4612",
            ConfigItem.configTypeBool,
            false
        )
    val switch4613 =
        addConfig(
            "Switch4613",
            ConfigItem.configTypeBool,
            false
        )
    val switch4614 =
        addConfig(
            "Switch4614",
            ConfigItem.configTypeBool,
            false
        )
    val switch4615 =
        addConfig(
            "Switch4615",
            ConfigItem.configTypeBool,
            false
        )
    val switch4616 =
        addConfig(
            "Switch4616",
            ConfigItem.configTypeBool,
            false
        )
    val switch4617 =
        addConfig(
            "Switch4617",
            ConfigItem.configTypeBool,
            false
        )
    val switch4618 =
        addConfig(
            "Switch4618",
            ConfigItem.configTypeBool,
            false
        )
    val switch4619 =
        addConfig(
            "Switch4619",
            ConfigItem.configTypeBool,
            false
        )
    val switch4620 =
        addConfig(
            "Switch4620",
            ConfigItem.configTypeBool,
            false
        )
    val switch4621 =
        addConfig(
            "Switch4621",
            ConfigItem.configTypeBool,
            false
        )
    val switch4622 =
        addConfig(
            "Switch4622",
            ConfigItem.configTypeBool,
            false
        )
    val switch4623 =
        addConfig(
            "Switch4623",
            ConfigItem.configTypeBool,
            false
        )
    val switch4624 =
        addConfig(
            "Switch4624",
            ConfigItem.configTypeBool,
            false
        )
    val switch4625 =
        addConfig(
            "Switch4625",
            ConfigItem.configTypeBool,
            false
        )
    val switch4626 =
        addConfig(
            "Switch4626",
            ConfigItem.configTypeBool,
            false
        )
    val switch4627 =
        addConfig(
            "Switch4627",
            ConfigItem.configTypeBool,
            false
        )
    val switch4628 =
        addConfig(
            "Switch4628",
            ConfigItem.configTypeBool,
            false
        )
    val switch4629 =
        addConfig(
            "Switch4629",
            ConfigItem.configTypeBool,
            false
        )
    val switch4630 =
        addConfig(
            "Switch4630",
            ConfigItem.configTypeBool,
            false
        )
    val switch4631 =
        addConfig(
            "Switch4631",
            ConfigItem.configTypeBool,
            false
        )
    val switch4632 =
        addConfig(
            "Switch4632",
            ConfigItem.configTypeBool,
            false
        )
    val switch4633 =
        addConfig(
            "Switch4633",
            ConfigItem.configTypeBool,
            false
        )
    val switch4634 =
        addConfig(
            "Switch4634",
            ConfigItem.configTypeBool,
            false
        )
    val switch4635 =
        addConfig(
            "Switch4635",
            ConfigItem.configTypeBool,
            false
        )
    val switch4636 =
        addConfig(
            "Switch4636",
            ConfigItem.configTypeBool,
            false
        )
    val switch4637 =
        addConfig(
            "Switch4637",
            ConfigItem.configTypeBool,
            false
        )
    val switch4638 =
        addConfig(
            "Switch4638",
            ConfigItem.configTypeBool,
            false
        )
    val switch4639 =
        addConfig(
            "Switch4639",
            ConfigItem.configTypeBool,
            false
        )
    val switch4640 =
        addConfig(
            "Switch4640",
            ConfigItem.configTypeBool,
            false
        )
    val switch4641 =
        addConfig(
            "Switch4641",
            ConfigItem.configTypeBool,
            false
        )
    val switch4642 =
        addConfig(
            "Switch4642",
            ConfigItem.configTypeBool,
            false
        )
    val switch4643 =
        addConfig(
            "Switch4643",
            ConfigItem.configTypeBool,
            false
        )
    val switch4644 =
        addConfig(
            "Switch4644",
            ConfigItem.configTypeBool,
            false
        )
    val switch4645 =
        addConfig(
            "Switch4645",
            ConfigItem.configTypeBool,
            false
        )
    val switch4646 =
        addConfig(
            "Switch4646",
            ConfigItem.configTypeBool,
            false
        )
    val switch4647 =
        addConfig(
            "Switch4647",
            ConfigItem.configTypeBool,
            false
        )
    val switch4648 =
        addConfig(
            "Switch4648",
            ConfigItem.configTypeBool,
            false
        )
    val switch4649 =
        addConfig(
            "Switch4649",
            ConfigItem.configTypeBool,
            false
        )
    val switch4650 =
        addConfig(
            "Switch4650",
            ConfigItem.configTypeBool,
            false
        )
    val switch4651 =
        addConfig(
            "Switch4651",
            ConfigItem.configTypeBool,
            false
        )
    val switch4652 =
        addConfig(
            "Switch4652",
            ConfigItem.configTypeBool,
            false
        )
    val switch4653 =
        addConfig(
            "Switch4653",
            ConfigItem.configTypeBool,
            false
        )
    val switch4654 =
        addConfig(
            "Switch4654",
            ConfigItem.configTypeBool,
            false
        )
    val switch4655 =
        addConfig(
            "Switch4655",
            ConfigItem.configTypeBool,
            false
        )
    val switch4656 =
        addConfig(
            "Switch4656",
            ConfigItem.configTypeBool,
            false
        )
    val switch4657 =
        addConfig(
            "Switch4657",
            ConfigItem.configTypeBool,
            false
        )
    val switch4658 =
        addConfig(
            "Switch4658",
            ConfigItem.configTypeBool,
            false
        )
    val switch4659 =
        addConfig(
            "Switch4659",
            ConfigItem.configTypeBool,
            false
        )
    val switch4660 =
        addConfig(
            "Switch4660",
            ConfigItem.configTypeBool,
            false
        )
    val switch4661 =
        addConfig(
            "Switch4661",
            ConfigItem.configTypeBool,
            false
        )
    val switch4662 =
        addConfig(
            "Switch4662",
            ConfigItem.configTypeBool,
            false
        )
    val switch4663 =
        addConfig(
            "Switch4663",
            ConfigItem.configTypeBool,
            false
        )
    val switch4664 =
        addConfig(
            "Switch4664",
            ConfigItem.configTypeBool,
            false
        )
    val switch4665 =
        addConfig(
            "Switch4665",
            ConfigItem.configTypeBool,
            false
        )
    val switch4666 =
        addConfig(
            "Switch4666",
            ConfigItem.configTypeBool,
            false
        )
    val switch4667 =
        addConfig(
            "Switch4667",
            ConfigItem.configTypeBool,
            false
        )
    val switch4668 =
        addConfig(
            "Switch4668",
            ConfigItem.configTypeBool,
            false
        )
    val switch4669 =
        addConfig(
            "Switch4669",
            ConfigItem.configTypeBool,
            false
        )
    val switch4670 =
        addConfig(
            "Switch4670",
            ConfigItem.configTypeBool,
            false
        )
    val switch4671 =
        addConfig(
            "Switch4671",
            ConfigItem.configTypeBool,
            false
        )
    val switch4672 =
        addConfig(
            "Switch4672",
            ConfigItem.configTypeBool,
            false
        )
    val switch4673 =
        addConfig(
            "Switch4673",
            ConfigItem.configTypeBool,
            false
        )
    val switch4674 =
        addConfig(
            "Switch4674",
            ConfigItem.configTypeBool,
            false
        )
    val switch4675 =
        addConfig(
            "Switch4675",
            ConfigItem.configTypeBool,
            false
        )
    val switch4676 =
        addConfig(
            "Switch4676",
            ConfigItem.configTypeBool,
            false
        )
    val switch4677 =
        addConfig(
            "Switch4677",
            ConfigItem.configTypeBool,
            false
        )
    val switch4678 =
        addConfig(
            "Switch4678",
            ConfigItem.configTypeBool,
            false
        )
    val switch4679 =
        addConfig(
            "Switch4679",
            ConfigItem.configTypeBool,
            false
        )
    val switch4680 =
        addConfig(
            "Switch4680",
            ConfigItem.configTypeBool,
            false
        )
    val switch4681 =
        addConfig(
            "Switch4681",
            ConfigItem.configTypeBool,
            false
        )
    val switch4682 =
        addConfig(
            "Switch4682",
            ConfigItem.configTypeBool,
            false
        )
    val switch4683 =
        addConfig(
            "Switch4683",
            ConfigItem.configTypeBool,
            false
        )
    val switch4684 =
        addConfig(
            "Switch4684",
            ConfigItem.configTypeBool,
            false
        )
    val switch4685 =
        addConfig(
            "Switch4685",
            ConfigItem.configTypeBool,
            false
        )
    val switch4686 =
        addConfig(
            "Switch4686",
            ConfigItem.configTypeBool,
            false
        )
    val switch4687 =
        addConfig(
            "Switch4687",
            ConfigItem.configTypeBool,
            false
        )
    val switch4688 =
        addConfig(
            "Switch4688",
            ConfigItem.configTypeBool,
            false
        )
    val switch4689 =
        addConfig(
            "Switch4689",
            ConfigItem.configTypeBool,
            false
        )
    val switch4690 =
        addConfig(
            "Switch4690",
            ConfigItem.configTypeBool,
            false
        )
    val switch4691 =
        addConfig(
            "Switch4691",
            ConfigItem.configTypeBool,
            false
        )
    val switch4692 =
        addConfig(
            "Switch4692",
            ConfigItem.configTypeBool,
            false
        )
    val switch4693 =
        addConfig(
            "Switch4693",
            ConfigItem.configTypeBool,
            false
        )
    val switch4694 =
        addConfig(
            "Switch4694",
            ConfigItem.configTypeBool,
            false
        )
    val switch4695 =
        addConfig(
            "Switch4695",
            ConfigItem.configTypeBool,
            false
        )
    val switch4696 =
        addConfig(
            "Switch4696",
            ConfigItem.configTypeBool,
            false
        )
    val switch4697 =
        addConfig(
            "Switch4697",
            ConfigItem.configTypeBool,
            false
        )
    val switch4698 =
        addConfig(
            "Switch4698",
            ConfigItem.configTypeBool,
            false
        )
    val switch4699 =
        addConfig(
            "Switch4699",
            ConfigItem.configTypeBool,
            false
        )
    val switch4700 =
        addConfig(
            "Switch4700",
            ConfigItem.configTypeBool,
            false
        )
    val switch4701 =
        addConfig(
            "Switch4701",
            ConfigItem.configTypeBool,
            false
        )
    val switch4702 =
        addConfig(
            "Switch4702",
            ConfigItem.configTypeBool,
            false
        )
    val switch4703 =
        addConfig(
            "Switch4703",
            ConfigItem.configTypeBool,
            false
        )
    val switch4704 =
        addConfig(
            "Switch4704",
            ConfigItem.configTypeBool,
            false
        )
    val switch4705 =
        addConfig(
            "Switch4705",
            ConfigItem.configTypeBool,
            false
        )
    val switch4706 =
        addConfig(
            "Switch4706",
            ConfigItem.configTypeBool,
            false
        )
    val switch4707 =
        addConfig(
            "Switch4707",
            ConfigItem.configTypeBool,
            false
        )
    val switch4708 =
        addConfig(
            "Switch4708",
            ConfigItem.configTypeBool,
            false
        )
    val switch4709 =
        addConfig(
            "Switch4709",
            ConfigItem.configTypeBool,
            false
        )
    val switch4710 =
        addConfig(
            "Switch4710",
            ConfigItem.configTypeBool,
            false
        )
    val switch4711 =
        addConfig(
            "Switch4711",
            ConfigItem.configTypeBool,
            false
        )
    val switch4712 =
        addConfig(
            "Switch4712",
            ConfigItem.configTypeBool,
            false
        )
    val switch4713 =
        addConfig(
            "Switch4713",
            ConfigItem.configTypeBool,
            false
        )
    val switch4714 =
        addConfig(
            "Switch4714",
            ConfigItem.configTypeBool,
            false
        )
    val switch4715 =
        addConfig(
            "Switch4715",
            ConfigItem.configTypeBool,
            false
        )
    val switch4716 =
        addConfig(
            "Switch4716",
            ConfigItem.configTypeBool,
            false
        )
    val switch4717 =
        addConfig(
            "Switch4717",
            ConfigItem.configTypeBool,
            false
        )
    val switch4718 =
        addConfig(
            "Switch4718",
            ConfigItem.configTypeBool,
            false
        )
    val switch4719 =
        addConfig(
            "Switch4719",
            ConfigItem.configTypeBool,
            false
        )
    val switch4720 =
        addConfig(
            "Switch4720",
            ConfigItem.configTypeBool,
            false
        )
    val switch4721 =
        addConfig(
            "Switch4721",
            ConfigItem.configTypeBool,
            false
        )
    val switch4722 =
        addConfig(
            "Switch4722",
            ConfigItem.configTypeBool,
            false
        )
    val switch4723 =
        addConfig(
            "Switch4723",
            ConfigItem.configTypeBool,
            false
        )
    val switch4724 =
        addConfig(
            "Switch4724",
            ConfigItem.configTypeBool,
            false
        )
    val switch4725 =
        addConfig(
            "Switch4725",
            ConfigItem.configTypeBool,
            false
        )
    val switch4726 =
        addConfig(
            "Switch4726",
            ConfigItem.configTypeBool,
            false
        )
    val switch4727 =
        addConfig(
            "Switch4727",
            ConfigItem.configTypeBool,
            false
        )
    val switch4728 =
        addConfig(
            "Switch4728",
            ConfigItem.configTypeBool,
            false
        )
    val switch4729 =
        addConfig(
            "Switch4729",
            ConfigItem.configTypeBool,
            false
        )
    val switch4730 =
        addConfig(
            "Switch4730",
            ConfigItem.configTypeBool,
            false
        )
    val switch4731 =
        addConfig(
            "Switch4731",
            ConfigItem.configTypeBool,
            false
        )
    val switch4732 =
        addConfig(
            "Switch4732",
            ConfigItem.configTypeBool,
            false
        )
    val switch4733 =
        addConfig(
            "Switch4733",
            ConfigItem.configTypeBool,
            false
        )
    val switch4734 =
        addConfig(
            "Switch4734",
            ConfigItem.configTypeBool,
            false
        )
    val switch4735 =
        addConfig(
            "Switch4735",
            ConfigItem.configTypeBool,
            false
        )
    val switch4736 =
        addConfig(
            "Switch4736",
            ConfigItem.configTypeBool,
            false
        )
    val switch4737 =
        addConfig(
            "Switch4737",
            ConfigItem.configTypeBool,
            false
        )
    val switch4738 =
        addConfig(
            "Switch4738",
            ConfigItem.configTypeBool,
            false
        )
    val switch4739 =
        addConfig(
            "Switch4739",
            ConfigItem.configTypeBool,
            false
        )
    val switch4740 =
        addConfig(
            "Switch4740",
            ConfigItem.configTypeBool,
            false
        )
    val switch4741 =
        addConfig(
            "Switch4741",
            ConfigItem.configTypeBool,
            false
        )
    val switch4742 =
        addConfig(
            "Switch4742",
            ConfigItem.configTypeBool,
            false
        )
    val switch4743 =
        addConfig(
            "Switch4743",
            ConfigItem.configTypeBool,
            false
        )
    val switch4744 =
        addConfig(
            "Switch4744",
            ConfigItem.configTypeBool,
            false
        )
    val switch4745 =
        addConfig(
            "Switch4745",
            ConfigItem.configTypeBool,
            false
        )
    val switch4746 =
        addConfig(
            "Switch4746",
            ConfigItem.configTypeBool,
            false
        )
    val switch4747 =
        addConfig(
            "Switch4747",
            ConfigItem.configTypeBool,
            false
        )
    val switch4748 =
        addConfig(
            "Switch4748",
            ConfigItem.configTypeBool,
            false
        )
    val switch4749 =
        addConfig(
            "Switch4749",
            ConfigItem.configTypeBool,
            false
        )
    val switch4750 =
        addConfig(
            "Switch4750",
            ConfigItem.configTypeBool,
            false
        )
    val switch4751 =
        addConfig(
            "Switch4751",
            ConfigItem.configTypeBool,
            false
        )
    val switch4752 =
        addConfig(
            "Switch4752",
            ConfigItem.configTypeBool,
            false
        )
    val switch4753 =
        addConfig(
            "Switch4753",
            ConfigItem.configTypeBool,
            false
        )
    val switch4754 =
        addConfig(
            "Switch4754",
            ConfigItem.configTypeBool,
            false
        )
    val switch4755 =
        addConfig(
            "Switch4755",
            ConfigItem.configTypeBool,
            false
        )
    val switch4756 =
        addConfig(
            "Switch4756",
            ConfigItem.configTypeBool,
            false
        )
    val switch4757 =
        addConfig(
            "Switch4757",
            ConfigItem.configTypeBool,
            false
        )
    val switch4758 =
        addConfig(
            "Switch4758",
            ConfigItem.configTypeBool,
            false
        )
    val switch4759 =
        addConfig(
            "Switch4759",
            ConfigItem.configTypeBool,
            false
        )
    val switch4760 =
        addConfig(
            "Switch4760",
            ConfigItem.configTypeBool,
            false
        )
    val switch4761 =
        addConfig(
            "Switch4761",
            ConfigItem.configTypeBool,
            false
        )
    val switch4762 =
        addConfig(
            "Switch4762",
            ConfigItem.configTypeBool,
            false
        )
    val switch4763 =
        addConfig(
            "Switch4763",
            ConfigItem.configTypeBool,
            false
        )
    val switch4764 =
        addConfig(
            "Switch4764",
            ConfigItem.configTypeBool,
            false
        )
    val switch4765 =
        addConfig(
            "Switch4765",
            ConfigItem.configTypeBool,
            false
        )
    val switch4766 =
        addConfig(
            "Switch4766",
            ConfigItem.configTypeBool,
            false
        )
    val switch4767 =
        addConfig(
            "Switch4767",
            ConfigItem.configTypeBool,
            false
        )
    val switch4768 =
        addConfig(
            "Switch4768",
            ConfigItem.configTypeBool,
            false
        )
    val switch4769 =
        addConfig(
            "Switch4769",
            ConfigItem.configTypeBool,
            false
        )
    val switch4770 =
        addConfig(
            "Switch4770",
            ConfigItem.configTypeBool,
            false
        )
    val switch4771 =
        addConfig(
            "Switch4771",
            ConfigItem.configTypeBool,
            false
        )
    val switch4772 =
        addConfig(
            "Switch4772",
            ConfigItem.configTypeBool,
            false
        )
    val switch4773 =
        addConfig(
            "Switch4773",
            ConfigItem.configTypeBool,
            false
        )
    val switch4774 =
        addConfig(
            "Switch4774",
            ConfigItem.configTypeBool,
            false
        )
    val switch4775 =
        addConfig(
            "Switch4775",
            ConfigItem.configTypeBool,
            false
        )
    val switch4776 =
        addConfig(
            "Switch4776",
            ConfigItem.configTypeBool,
            false
        )
    val switch4777 =
        addConfig(
            "Switch4777",
            ConfigItem.configTypeBool,
            false
        )
    val switch4778 =
        addConfig(
            "Switch4778",
            ConfigItem.configTypeBool,
            false
        )
    val switch4779 =
        addConfig(
            "Switch4779",
            ConfigItem.configTypeBool,
            false
        )
    val switch4780 =
        addConfig(
            "Switch4780",
            ConfigItem.configTypeBool,
            false
        )
    val switch4781 =
        addConfig(
            "Switch4781",
            ConfigItem.configTypeBool,
            false
        )
    val switch4782 =
        addConfig(
            "Switch4782",
            ConfigItem.configTypeBool,
            false
        )
    val switch4783 =
        addConfig(
            "Switch4783",
            ConfigItem.configTypeBool,
            false
        )
    val switch4784 =
        addConfig(
            "Switch4784",
            ConfigItem.configTypeBool,
            false
        )
    val switch4785 =
        addConfig(
            "Switch4785",
            ConfigItem.configTypeBool,
            false
        )
    val switch4786 =
        addConfig(
            "Switch4786",
            ConfigItem.configTypeBool,
            false
        )
    val switch4787 =
        addConfig(
            "Switch4787",
            ConfigItem.configTypeBool,
            false
        )
    val switch4788 =
        addConfig(
            "Switch4788",
            ConfigItem.configTypeBool,
            false
        )
    val switch4789 =
        addConfig(
            "Switch4789",
            ConfigItem.configTypeBool,
            false
        )
    val switch4790 =
        addConfig(
            "Switch4790",
            ConfigItem.configTypeBool,
            false
        )
    val switch4791 =
        addConfig(
            "Switch4791",
            ConfigItem.configTypeBool,
            false
        )
    val switch4792 =
        addConfig(
            "Switch4792",
            ConfigItem.configTypeBool,
            false
        )
    val switch4793 =
        addConfig(
            "Switch4793",
            ConfigItem.configTypeBool,
            false
        )
    val switch4794 =
        addConfig(
            "Switch4794",
            ConfigItem.configTypeBool,
            false
        )
    val switch4795 =
        addConfig(
            "Switch4795",
            ConfigItem.configTypeBool,
            false
        )
    val switch4796 =
        addConfig(
            "Switch4796",
            ConfigItem.configTypeBool,
            false
        )
    val switch4797 =
        addConfig(
            "Switch4797",
            ConfigItem.configTypeBool,
            false
        )
    val switch4798 =
        addConfig(
            "Switch4798",
            ConfigItem.configTypeBool,
            false
        )
    val switch4799 =
        addConfig(
            "Switch4799",
            ConfigItem.configTypeBool,
            false
        )
    val switch4800 =
        addConfig(
            "Switch4800",
            ConfigItem.configTypeBool,
            false
        )
    val switch4801 =
        addConfig(
            "Switch4801",
            ConfigItem.configTypeBool,
            false
        )
    val switch4802 =
        addConfig(
            "Switch4802",
            ConfigItem.configTypeBool,
            false
        )
    val switch4803 =
        addConfig(
            "Switch4803",
            ConfigItem.configTypeBool,
            false
        )
    val switch4804 =
        addConfig(
            "Switch4804",
            ConfigItem.configTypeBool,
            false
        )
    val switch4805 =
        addConfig(
            "Switch4805",
            ConfigItem.configTypeBool,
            false
        )
    val switch4806 =
        addConfig(
            "Switch4806",
            ConfigItem.configTypeBool,
            false
        )
    val switch4807 =
        addConfig(
            "Switch4807",
            ConfigItem.configTypeBool,
            false
        )
    val switch4808 =
        addConfig(
            "Switch4808",
            ConfigItem.configTypeBool,
            false
        )
    val switch4809 =
        addConfig(
            "Switch4809",
            ConfigItem.configTypeBool,
            false
        )
    val switch4810 =
        addConfig(
            "Switch4810",
            ConfigItem.configTypeBool,
            false
        )
    val switch4811 =
        addConfig(
            "Switch4811",
            ConfigItem.configTypeBool,
            false
        )
    val switch4812 =
        addConfig(
            "Switch4812",
            ConfigItem.configTypeBool,
            false
        )
    val switch4813 =
        addConfig(
            "Switch4813",
            ConfigItem.configTypeBool,
            false
        )
    val switch4814 =
        addConfig(
            "Switch4814",
            ConfigItem.configTypeBool,
            false
        )
    val switch4815 =
        addConfig(
            "Switch4815",
            ConfigItem.configTypeBool,
            false
        )
    val switch4816 =
        addConfig(
            "Switch4816",
            ConfigItem.configTypeBool,
            false
        )
    val switch4817 =
        addConfig(
            "Switch4817",
            ConfigItem.configTypeBool,
            false
        )
    val switch4818 =
        addConfig(
            "Switch4818",
            ConfigItem.configTypeBool,
            false
        )
    val switch4819 =
        addConfig(
            "Switch4819",
            ConfigItem.configTypeBool,
            false
        )
    val switch4820 =
        addConfig(
            "Switch4820",
            ConfigItem.configTypeBool,
            false
        )
    val switch4821 =
        addConfig(
            "Switch4821",
            ConfigItem.configTypeBool,
            false
        )
    val switch4822 =
        addConfig(
            "Switch4822",
            ConfigItem.configTypeBool,
            false
        )
    val switch4823 =
        addConfig(
            "Switch4823",
            ConfigItem.configTypeBool,
            false
        )
    val switch4824 =
        addConfig(
            "Switch4824",
            ConfigItem.configTypeBool,
            false
        )
    val switch4825 =
        addConfig(
            "Switch4825",
            ConfigItem.configTypeBool,
            false
        )
    val switch4826 =
        addConfig(
            "Switch4826",
            ConfigItem.configTypeBool,
            false
        )
    val switch4827 =
        addConfig(
            "Switch4827",
            ConfigItem.configTypeBool,
            false
        )
    val switch4828 =
        addConfig(
            "Switch4828",
            ConfigItem.configTypeBool,
            false
        )
    val switch4829 =
        addConfig(
            "Switch4829",
            ConfigItem.configTypeBool,
            false
        )
    val switch4830 =
        addConfig(
            "Switch4830",
            ConfigItem.configTypeBool,
            false
        )
    val switch4831 =
        addConfig(
            "Switch4831",
            ConfigItem.configTypeBool,
            false
        )
    val switch4832 =
        addConfig(
            "Switch4832",
            ConfigItem.configTypeBool,
            false
        )
    val switch4833 =
        addConfig(
            "Switch4833",
            ConfigItem.configTypeBool,
            false
        )
    val switch4834 =
        addConfig(
            "Switch4834",
            ConfigItem.configTypeBool,
            false
        )
    val switch4835 =
        addConfig(
            "Switch4835",
            ConfigItem.configTypeBool,
            false
        )
    val switch4836 =
        addConfig(
            "Switch4836",
            ConfigItem.configTypeBool,
            false
        )
    val switch4837 =
        addConfig(
            "Switch4837",
            ConfigItem.configTypeBool,
            false
        )
    val switch4838 =
        addConfig(
            "Switch4838",
            ConfigItem.configTypeBool,
            false
        )
    val switch4839 =
        addConfig(
            "Switch4839",
            ConfigItem.configTypeBool,
            false
        )
    val switch4840 =
        addConfig(
            "Switch4840",
            ConfigItem.configTypeBool,
            false
        )
    val switch4841 =
        addConfig(
            "Switch4841",
            ConfigItem.configTypeBool,
            false
        )
    val switch4842 =
        addConfig(
            "Switch4842",
            ConfigItem.configTypeBool,
            false
        )
    val switch4843 =
        addConfig(
            "Switch4843",
            ConfigItem.configTypeBool,
            false
        )
    val switch4844 =
        addConfig(
            "Switch4844",
            ConfigItem.configTypeBool,
            false
        )
    val switch4845 =
        addConfig(
            "Switch4845",
            ConfigItem.configTypeBool,
            false
        )
    val switch4846 =
        addConfig(
            "Switch4846",
            ConfigItem.configTypeBool,
            false
        )
    val switch4847 =
        addConfig(
            "Switch4847",
            ConfigItem.configTypeBool,
            false
        )
    val switch4848 =
        addConfig(
            "Switch4848",
            ConfigItem.configTypeBool,
            false
        )
    val switch4849 =
        addConfig(
            "Switch4849",
            ConfigItem.configTypeBool,
            false
        )
    val switch4850 =
        addConfig(
            "Switch4850",
            ConfigItem.configTypeBool,
            false
        )
    val switch4851 =
        addConfig(
            "Switch4851",
            ConfigItem.configTypeBool,
            false
        )
    val switch4852 =
        addConfig(
            "Switch4852",
            ConfigItem.configTypeBool,
            false
        )
    val switch4853 =
        addConfig(
            "Switch4853",
            ConfigItem.configTypeBool,
            false
        )
    val switch4854 =
        addConfig(
            "Switch4854",
            ConfigItem.configTypeBool,
            false
        )
    val switch4855 =
        addConfig(
            "Switch4855",
            ConfigItem.configTypeBool,
            false
        )
    val switch4856 =
        addConfig(
            "Switch4856",
            ConfigItem.configTypeBool,
            false
        )
    val switch4857 =
        addConfig(
            "Switch4857",
            ConfigItem.configTypeBool,
            false
        )
    val switch4858 =
        addConfig(
            "Switch4858",
            ConfigItem.configTypeBool,
            false
        )
    val switch4859 =
        addConfig(
            "Switch4859",
            ConfigItem.configTypeBool,
            false
        )
    val switch4860 =
        addConfig(
            "Switch4860",
            ConfigItem.configTypeBool,
            false
        )
    val switch4861 =
        addConfig(
            "Switch4861",
            ConfigItem.configTypeBool,
            false
        )
    val switch4862 =
        addConfig(
            "Switch4862",
            ConfigItem.configTypeBool,
            false
        )
    val switch4863 =
        addConfig(
            "Switch4863",
            ConfigItem.configTypeBool,
            false
        )
    val switch4864 =
        addConfig(
            "Switch4864",
            ConfigItem.configTypeBool,
            false
        )
    val switch4865 =
        addConfig(
            "Switch4865",
            ConfigItem.configTypeBool,
            false
        )
    val switch4866 =
        addConfig(
            "Switch4866",
            ConfigItem.configTypeBool,
            false
        )
    val switch4867 =
        addConfig(
            "Switch4867",
            ConfigItem.configTypeBool,
            false
        )
    val switch4868 =
        addConfig(
            "Switch4868",
            ConfigItem.configTypeBool,
            false
        )
    val switch4869 =
        addConfig(
            "Switch4869",
            ConfigItem.configTypeBool,
            false
        )
    val switch4870 =
        addConfig(
            "Switch4870",
            ConfigItem.configTypeBool,
            false
        )
    val switch4871 =
        addConfig(
            "Switch4871",
            ConfigItem.configTypeBool,
            false
        )
    val switch4872 =
        addConfig(
            "Switch4872",
            ConfigItem.configTypeBool,
            false
        )
    val switch4873 =
        addConfig(
            "Switch4873",
            ConfigItem.configTypeBool,
            false
        )
    val switch4874 =
        addConfig(
            "Switch4874",
            ConfigItem.configTypeBool,
            false
        )
    val switch4875 =
        addConfig(
            "Switch4875",
            ConfigItem.configTypeBool,
            false
        )
    val switch4876 =
        addConfig(
            "Switch4876",
            ConfigItem.configTypeBool,
            false
        )
    val switch4877 =
        addConfig(
            "Switch4877",
            ConfigItem.configTypeBool,
            false
        )
    val switch4878 =
        addConfig(
            "Switch4878",
            ConfigItem.configTypeBool,
            false
        )
    val switch4879 =
        addConfig(
            "Switch4879",
            ConfigItem.configTypeBool,
            false
        )
    val switch4880 =
        addConfig(
            "Switch4880",
            ConfigItem.configTypeBool,
            false
        )
    val switch4881 =
        addConfig(
            "Switch4881",
            ConfigItem.configTypeBool,
            false
        )
    val switch4882 =
        addConfig(
            "Switch4882",
            ConfigItem.configTypeBool,
            false
        )
    val switch4883 =
        addConfig(
            "Switch4883",
            ConfigItem.configTypeBool,
            false
        )
    val switch4884 =
        addConfig(
            "Switch4884",
            ConfigItem.configTypeBool,
            false
        )
    val switch4885 =
        addConfig(
            "Switch4885",
            ConfigItem.configTypeBool,
            false
        )
    val switch4886 =
        addConfig(
            "Switch4886",
            ConfigItem.configTypeBool,
            false
        )
    val switch4887 =
        addConfig(
            "Switch4887",
            ConfigItem.configTypeBool,
            false
        )
    val switch4888 =
        addConfig(
            "Switch4888",
            ConfigItem.configTypeBool,
            false
        )
    val switch4889 =
        addConfig(
            "Switch4889",
            ConfigItem.configTypeBool,
            false
        )
    val switch4890 =
        addConfig(
            "Switch4890",
            ConfigItem.configTypeBool,
            false
        )
    val switch4891 =
        addConfig(
            "Switch4891",
            ConfigItem.configTypeBool,
            false
        )
    val switch4892 =
        addConfig(
            "Switch4892",
            ConfigItem.configTypeBool,
            false
        )
    val switch4893 =
        addConfig(
            "Switch4893",
            ConfigItem.configTypeBool,
            false
        )
    val switch4894 =
        addConfig(
            "Switch4894",
            ConfigItem.configTypeBool,
            false
        )
    val switch4895 =
        addConfig(
            "Switch4895",
            ConfigItem.configTypeBool,
            false
        )
    val switch4896 =
        addConfig(
            "Switch4896",
            ConfigItem.configTypeBool,
            false
        )
    val switch4897 =
        addConfig(
            "Switch4897",
            ConfigItem.configTypeBool,
            false
        )
    val switch4898 =
        addConfig(
            "Switch4898",
            ConfigItem.configTypeBool,
            false
        )
    val switch4899 =
        addConfig(
            "Switch4899",
            ConfigItem.configTypeBool,
            false
        )
    val switch4900 =
        addConfig(
            "Switch4900",
            ConfigItem.configTypeBool,
            false
        )
    val switch4901 =
        addConfig(
            "Switch4901",
            ConfigItem.configTypeBool,
            false
        )
    val switch4902 =
        addConfig(
            "Switch4902",
            ConfigItem.configTypeBool,
            false
        )
    val switch4903 =
        addConfig(
            "Switch4903",
            ConfigItem.configTypeBool,
            false
        )
    val switch4904 =
        addConfig(
            "Switch4904",
            ConfigItem.configTypeBool,
            false
        )
    val switch4905 =
        addConfig(
            "Switch4905",
            ConfigItem.configTypeBool,
            false
        )
    val switch4906 =
        addConfig(
            "Switch4906",
            ConfigItem.configTypeBool,
            false
        )
    val switch4907 =
        addConfig(
            "Switch4907",
            ConfigItem.configTypeBool,
            false
        )
    val switch4908 =
        addConfig(
            "Switch4908",
            ConfigItem.configTypeBool,
            false
        )
    val switch4909 =
        addConfig(
            "Switch4909",
            ConfigItem.configTypeBool,
            false
        )
    val switch4910 =
        addConfig(
            "Switch4910",
            ConfigItem.configTypeBool,
            false
        )
    val switch4911 =
        addConfig(
            "Switch4911",
            ConfigItem.configTypeBool,
            false
        )
    val switch4912 =
        addConfig(
            "Switch4912",
            ConfigItem.configTypeBool,
            false
        )
    val switch4913 =
        addConfig(
            "Switch4913",
            ConfigItem.configTypeBool,
            false
        )
    val switch4914 =
        addConfig(
            "Switch4914",
            ConfigItem.configTypeBool,
            false
        )
    val switch4915 =
        addConfig(
            "Switch4915",
            ConfigItem.configTypeBool,
            false
        )
    val switch4916 =
        addConfig(
            "Switch4916",
            ConfigItem.configTypeBool,
            false
        )
    val switch4917 =
        addConfig(
            "Switch4917",
            ConfigItem.configTypeBool,
            false
        )
    val switch4918 =
        addConfig(
            "Switch4918",
            ConfigItem.configTypeBool,
            false
        )
    val switch4919 =
        addConfig(
            "Switch4919",
            ConfigItem.configTypeBool,
            false
        )
    val switch4920 =
        addConfig(
            "Switch4920",
            ConfigItem.configTypeBool,
            false
        )
    val switch4921 =
        addConfig(
            "Switch4921",
            ConfigItem.configTypeBool,
            false
        )
    val switch4922 =
        addConfig(
            "Switch4922",
            ConfigItem.configTypeBool,
            false
        )
    val switch4923 =
        addConfig(
            "Switch4923",
            ConfigItem.configTypeBool,
            false
        )
    val switch4924 =
        addConfig(
            "Switch4924",
            ConfigItem.configTypeBool,
            false
        )
    val switch4925 =
        addConfig(
            "Switch4925",
            ConfigItem.configTypeBool,
            false
        )
    val switch4926 =
        addConfig(
            "Switch4926",
            ConfigItem.configTypeBool,
            false
        )
    val switch4927 =
        addConfig(
            "Switch4927",
            ConfigItem.configTypeBool,
            false
        )
    val switch4928 =
        addConfig(
            "Switch4928",
            ConfigItem.configTypeBool,
            false
        )
    val switch4929 =
        addConfig(
            "Switch4929",
            ConfigItem.configTypeBool,
            false
        )
    val switch4930 =
        addConfig(
            "Switch4930",
            ConfigItem.configTypeBool,
            false
        )
    val switch4931 =
        addConfig(
            "Switch4931",
            ConfigItem.configTypeBool,
            false
        )
    val switch4932 =
        addConfig(
            "Switch4932",
            ConfigItem.configTypeBool,
            false
        )
    val switch4933 =
        addConfig(
            "Switch4933",
            ConfigItem.configTypeBool,
            false
        )
    val switch4934 =
        addConfig(
            "Switch4934",
            ConfigItem.configTypeBool,
            false
        )
    val switch4935 =
        addConfig(
            "Switch4935",
            ConfigItem.configTypeBool,
            false
        )
    val switch4936 =
        addConfig(
            "Switch4936",
            ConfigItem.configTypeBool,
            false
        )
    val switch4937 =
        addConfig(
            "Switch4937",
            ConfigItem.configTypeBool,
            false
        )
    val switch4938 =
        addConfig(
            "Switch4938",
            ConfigItem.configTypeBool,
            false
        )
    val switch4939 =
        addConfig(
            "Switch4939",
            ConfigItem.configTypeBool,
            false
        )
    val switch4940 =
        addConfig(
            "Switch4940",
            ConfigItem.configTypeBool,
            false
        )
    val switch4941 =
        addConfig(
            "Switch4941",
            ConfigItem.configTypeBool,
            false
        )
    val switch4942 =
        addConfig(
            "Switch4942",
            ConfigItem.configTypeBool,
            false
        )
    val switch4943 =
        addConfig(
            "Switch4943",
            ConfigItem.configTypeBool,
            false
        )
    val switch4944 =
        addConfig(
            "Switch4944",
            ConfigItem.configTypeBool,
            false
        )
    val switch4945 =
        addConfig(
            "Switch4945",
            ConfigItem.configTypeBool,
            false
        )
    val switch4946 =
        addConfig(
            "Switch4946",
            ConfigItem.configTypeBool,
            false
        )
    val switch4947 =
        addConfig(
            "Switch4947",
            ConfigItem.configTypeBool,
            false
        )
    val switch4948 =
        addConfig(
            "Switch4948",
            ConfigItem.configTypeBool,
            false
        )
    val switch4949 =
        addConfig(
            "Switch4949",
            ConfigItem.configTypeBool,
            false
        )
    val switch4950 =
        addConfig(
            "Switch4950",
            ConfigItem.configTypeBool,
            false
        )
    val switch4951 =
        addConfig(
            "Switch4951",
            ConfigItem.configTypeBool,
            false
        )
    val switch4952 =
        addConfig(
            "Switch4952",
            ConfigItem.configTypeBool,
            false
        )
    val switch4953 =
        addConfig(
            "Switch4953",
            ConfigItem.configTypeBool,
            false
        )
    val switch4954 =
        addConfig(
            "Switch4954",
            ConfigItem.configTypeBool,
            false
        )
    val switch4955 =
        addConfig(
            "Switch4955",
            ConfigItem.configTypeBool,
            false
        )
    val switch4956 =
        addConfig(
            "Switch4956",
            ConfigItem.configTypeBool,
            false
        )
    val switch4957 =
        addConfig(
            "Switch4957",
            ConfigItem.configTypeBool,
            false
        )
    val switch4958 =
        addConfig(
            "Switch4958",
            ConfigItem.configTypeBool,
            false
        )
    val switch4959 =
        addConfig(
            "Switch4959",
            ConfigItem.configTypeBool,
            false
        )
    val switch4960 =
        addConfig(
            "Switch4960",
            ConfigItem.configTypeBool,
            false
        )
    val switch4961 =
        addConfig(
            "Switch4961",
            ConfigItem.configTypeBool,
            false
        )
    val switch4962 =
        addConfig(
            "Switch4962",
            ConfigItem.configTypeBool,
            false
        )
    val switch4963 =
        addConfig(
            "Switch4963",
            ConfigItem.configTypeBool,
            false
        )
    val switch4964 =
        addConfig(
            "Switch4964",
            ConfigItem.configTypeBool,
            false
        )
    val switch4965 =
        addConfig(
            "Switch4965",
            ConfigItem.configTypeBool,
            false
        )
    val switch4966 =
        addConfig(
            "Switch4966",
            ConfigItem.configTypeBool,
            false
        )
    val switch4967 =
        addConfig(
            "Switch4967",
            ConfigItem.configTypeBool,
            false
        )
    val switch4968 =
        addConfig(
            "Switch4968",
            ConfigItem.configTypeBool,
            false
        )
    val switch4969 =
        addConfig(
            "Switch4969",
            ConfigItem.configTypeBool,
            false
        )
    val switch4970 =
        addConfig(
            "Switch4970",
            ConfigItem.configTypeBool,
            false
        )
    val switch4971 =
        addConfig(
            "Switch4971",
            ConfigItem.configTypeBool,
            false
        )
    val switch4972 =
        addConfig(
            "Switch4972",
            ConfigItem.configTypeBool,
            false
        )
    val switch4973 =
        addConfig(
            "Switch4973",
            ConfigItem.configTypeBool,
            false
        )
    val switch4974 =
        addConfig(
            "Switch4974",
            ConfigItem.configTypeBool,
            false
        )
    val switch4975 =
        addConfig(
            "Switch4975",
            ConfigItem.configTypeBool,
            false
        )
    val switch4976 =
        addConfig(
            "Switch4976",
            ConfigItem.configTypeBool,
            false
        )
    val switch4977 =
        addConfig(
            "Switch4977",
            ConfigItem.configTypeBool,
            false
        )
    val switch4978 =
        addConfig(
            "Switch4978",
            ConfigItem.configTypeBool,
            false
        )
    val switch4979 =
        addConfig(
            "Switch4979",
            ConfigItem.configTypeBool,
            false
        )
    val switch4980 =
        addConfig(
            "Switch4980",
            ConfigItem.configTypeBool,
            false
        )
    val switch4981 =
        addConfig(
            "Switch4981",
            ConfigItem.configTypeBool,
            false
        )
    val switch4982 =
        addConfig(
            "Switch4982",
            ConfigItem.configTypeBool,
            false
        )
    val switch4983 =
        addConfig(
            "Switch4983",
            ConfigItem.configTypeBool,
            false
        )
    val switch4984 =
        addConfig(
            "Switch4984",
            ConfigItem.configTypeBool,
            false
        )
    val switch4985 =
        addConfig(
            "Switch4985",
            ConfigItem.configTypeBool,
            false
        )
    val switch4986 =
        addConfig(
            "Switch4986",
            ConfigItem.configTypeBool,
            false
        )
    val switch4987 =
        addConfig(
            "Switch4987",
            ConfigItem.configTypeBool,
            false
        )
    val switch4988 =
        addConfig(
            "Switch4988",
            ConfigItem.configTypeBool,
            false
        )
    val switch4989 =
        addConfig(
            "Switch4989",
            ConfigItem.configTypeBool,
            false
        )
    val switch4990 =
        addConfig(
            "Switch4990",
            ConfigItem.configTypeBool,
            false
        )
    val switch4991 =
        addConfig(
            "Switch4991",
            ConfigItem.configTypeBool,
            false
        )
    val switch4992 =
        addConfig(
            "Switch4992",
            ConfigItem.configTypeBool,
            false
        )
    val switch4993 =
        addConfig(
            "Switch4993",
            ConfigItem.configTypeBool,
            false
        )
    val switch4994 =
        addConfig(
            "Switch4994",
            ConfigItem.configTypeBool,
            false
        )
    val switch4995 =
        addConfig(
            "Switch4995",
            ConfigItem.configTypeBool,
            false
        )
    val switch4996 =
        addConfig(
            "Switch4996",
            ConfigItem.configTypeBool,
            false
        )
    val switch4997 =
        addConfig(
            "Switch4997",
            ConfigItem.configTypeBool,
            false
        )
    val switch4998 =
        addConfig(
            "Switch4998",
            ConfigItem.configTypeBool,
            false
        )
    val switch4999 =
        addConfig(
            "Switch4999",
            ConfigItem.configTypeBool,
            false
        )
    val switch5000 =
        addConfig(
            "Switch5000",
            ConfigItem.configTypeBool,
            false
        )
    val switch5001 =
        addConfig(
            "Switch5001",
            ConfigItem.configTypeBool,
            false
        )
    val switch5002 =
        addConfig(
            "Switch5002",
            ConfigItem.configTypeBool,
            false
        )
    val switch5003 =
        addConfig(
            "Switch5003",
            ConfigItem.configTypeBool,
            false
        )
    val switch5004 =
        addConfig(
            "Switch5004",
            ConfigItem.configTypeBool,
            false
        )
    val switch5005 =
        addConfig(
            "Switch5005",
            ConfigItem.configTypeBool,
            false
        )
    val switch5006 =
        addConfig(
            "Switch5006",
            ConfigItem.configTypeBool,
            false
        )
    val switch5007 =
        addConfig(
            "Switch5007",
            ConfigItem.configTypeBool,
            false
        )
    val switch5008 =
        addConfig(
            "Switch5008",
            ConfigItem.configTypeBool,
            false
        )
    val switch5009 =
        addConfig(
            "Switch5009",
            ConfigItem.configTypeBool,
            false
        )
    val switch5010 =
        addConfig(
            "Switch5010",
            ConfigItem.configTypeBool,
            false
        )
    val switch5011 =
        addConfig(
            "Switch5011",
            ConfigItem.configTypeBool,
            false
        )
    val switch5012 =
        addConfig(
            "Switch5012",
            ConfigItem.configTypeBool,
            false
        )
    val switch5013 =
        addConfig(
            "Switch5013",
            ConfigItem.configTypeBool,
            false
        )
    val switch5014 =
        addConfig(
            "Switch5014",
            ConfigItem.configTypeBool,
            false
        )
    val switch5015 =
        addConfig(
            "Switch5015",
            ConfigItem.configTypeBool,
            false
        )
    val switch5016 =
        addConfig(
            "Switch5016",
            ConfigItem.configTypeBool,
            false
        )
    val switch5017 =
        addConfig(
            "Switch5017",
            ConfigItem.configTypeBool,
            false
        )
    val switch5018 =
        addConfig(
            "Switch5018",
            ConfigItem.configTypeBool,
            false
        )
    val switch5019 =
        addConfig(
            "Switch5019",
            ConfigItem.configTypeBool,
            false
        )
    val switch5020 =
        addConfig(
            "Switch5020",
            ConfigItem.configTypeBool,
            false
        )
    val switch5021 =
        addConfig(
            "Switch5021",
            ConfigItem.configTypeBool,
            false
        )
    val switch5022 =
        addConfig(
            "Switch5022",
            ConfigItem.configTypeBool,
            false
        )
    val switch5023 =
        addConfig(
            "Switch5023",
            ConfigItem.configTypeBool,
            false
        )
    val switch5024 =
        addConfig(
            "Switch5024",
            ConfigItem.configTypeBool,
            false
        )
    val switch5025 =
        addConfig(
            "Switch5025",
            ConfigItem.configTypeBool,
            false
        )
    val switch5026 =
        addConfig(
            "Switch5026",
            ConfigItem.configTypeBool,
            false
        )
    val switch5027 =
        addConfig(
            "Switch5027",
            ConfigItem.configTypeBool,
            false
        )
    val switch5028 =
        addConfig(
            "Switch5028",
            ConfigItem.configTypeBool,
            false
        )
    val switch5029 =
        addConfig(
            "Switch5029",
            ConfigItem.configTypeBool,
            false
        )
    val switch5030 =
        addConfig(
            "Switch5030",
            ConfigItem.configTypeBool,
            false
        )
    val switch5031 =
        addConfig(
            "Switch5031",
            ConfigItem.configTypeBool,
            false
        )
    val switch5032 =
        addConfig(
            "Switch5032",
            ConfigItem.configTypeBool,
            false
        )
    val switch5033 =
        addConfig(
            "Switch5033",
            ConfigItem.configTypeBool,
            false
        )
    val switch5034 =
        addConfig(
            "Switch5034",
            ConfigItem.configTypeBool,
            false
        )
    val switch5035 =
        addConfig(
            "Switch5035",
            ConfigItem.configTypeBool,
            false
        )
    val switch5036 =
        addConfig(
            "Switch5036",
            ConfigItem.configTypeBool,
            false
        )
    val switch5037 =
        addConfig(
            "Switch5037",
            ConfigItem.configTypeBool,
            false
        )
    val switch5038 =
        addConfig(
            "Switch5038",
            ConfigItem.configTypeBool,
            false
        )
    val switch5039 =
        addConfig(
            "Switch5039",
            ConfigItem.configTypeBool,
            false
        )
    val switch5040 =
        addConfig(
            "Switch5040",
            ConfigItem.configTypeBool,
            false
        )
    val switch5041 =
        addConfig(
            "Switch5041",
            ConfigItem.configTypeBool,
            false
        )
    val switch5042 =
        addConfig(
            "Switch5042",
            ConfigItem.configTypeBool,
            false
        )
    val switch5043 =
        addConfig(
            "Switch5043",
            ConfigItem.configTypeBool,
            false
        )
    val switch5044 =
        addConfig(
            "Switch5044",
            ConfigItem.configTypeBool,
            false
        )
    val switch5045 =
        addConfig(
            "Switch5045",
            ConfigItem.configTypeBool,
            false
        )
    val switch5046 =
        addConfig(
            "Switch5046",
            ConfigItem.configTypeBool,
            false
        )
    val switch5047 =
        addConfig(
            "Switch5047",
            ConfigItem.configTypeBool,
            false
        )
    val switch5048 =
        addConfig(
            "Switch5048",
            ConfigItem.configTypeBool,
            false
        )
    val switch5049 =
        addConfig(
            "Switch5049",
            ConfigItem.configTypeBool,
            false
        )
    val switch5050 =
        addConfig(
            "Switch5050",
            ConfigItem.configTypeBool,
            false
        )
    val switch5051 =
        addConfig(
            "Switch5051",
            ConfigItem.configTypeBool,
            false
        )
    val switch5052 =
        addConfig(
            "Switch5052",
            ConfigItem.configTypeBool,
            false
        )
    val switch5053 =
        addConfig(
            "Switch5053",
            ConfigItem.configTypeBool,
            false
        )
    val switch5054 =
        addConfig(
            "Switch5054",
            ConfigItem.configTypeBool,
            false
        )
    val switch5055 =
        addConfig(
            "Switch5055",
            ConfigItem.configTypeBool,
            false
        )
    val switch5056 =
        addConfig(
            "Switch5056",
            ConfigItem.configTypeBool,
            false
        )
    val switch5057 =
        addConfig(
            "Switch5057",
            ConfigItem.configTypeBool,
            false
        )
    val switch5058 =
        addConfig(
            "Switch5058",
            ConfigItem.configTypeBool,
            false
        )
    val switch5059 =
        addConfig(
            "Switch5059",
            ConfigItem.configTypeBool,
            false
        )
    val switch5060 =
        addConfig(
            "Switch5060",
            ConfigItem.configTypeBool,
            false
        )
    val switch5061 =
        addConfig(
            "Switch5061",
            ConfigItem.configTypeBool,
            false
        )
    val switch5062 =
        addConfig(
            "Switch5062",
            ConfigItem.configTypeBool,
            false
        )
    val switch5063 =
        addConfig(
            "Switch5063",
            ConfigItem.configTypeBool,
            false
        )
    val switch5064 =
        addConfig(
            "Switch5064",
            ConfigItem.configTypeBool,
            false
        )
    val switch5065 =
        addConfig(
            "Switch5065",
            ConfigItem.configTypeBool,
            false
        )
    val switch5066 =
        addConfig(
            "Switch5066",
            ConfigItem.configTypeBool,
            false
        )
    val switch5067 =
        addConfig(
            "Switch5067",
            ConfigItem.configTypeBool,
            false
        )
    val switch5068 =
        addConfig(
            "Switch5068",
            ConfigItem.configTypeBool,
            false
        )
    val switch5069 =
        addConfig(
            "Switch5069",
            ConfigItem.configTypeBool,
            false
        )
    val switch5070 =
        addConfig(
            "Switch5070",
            ConfigItem.configTypeBool,
            false
        )
    val switch5071 =
        addConfig(
            "Switch5071",
            ConfigItem.configTypeBool,
            false
        )
    val switch5072 =
        addConfig(
            "Switch5072",
            ConfigItem.configTypeBool,
            false
        )
    val switch5073 =
        addConfig(
            "Switch5073",
            ConfigItem.configTypeBool,
            false
        )
    val switch5074 =
        addConfig(
            "Switch5074",
            ConfigItem.configTypeBool,
            false
        )
    val switch5075 =
        addConfig(
            "Switch5075",
            ConfigItem.configTypeBool,
            false
        )
    val switch5076 =
        addConfig(
            "Switch5076",
            ConfigItem.configTypeBool,
            false
        )
    val switch5077 =
        addConfig(
            "Switch5077",
            ConfigItem.configTypeBool,
            false
        )
    val switch5078 =
        addConfig(
            "Switch5078",
            ConfigItem.configTypeBool,
            false
        )
    val switch5079 =
        addConfig(
            "Switch5079",
            ConfigItem.configTypeBool,
            false
        )
    val switch5080 =
        addConfig(
            "Switch5080",
            ConfigItem.configTypeBool,
            false
        )
    val switch5081 =
        addConfig(
            "Switch5081",
            ConfigItem.configTypeBool,
            false
        )
    val switch5082 =
        addConfig(
            "Switch5082",
            ConfigItem.configTypeBool,
            false
        )
    val switch5083 =
        addConfig(
            "Switch5083",
            ConfigItem.configTypeBool,
            false
        )
    val switch5084 =
        addConfig(
            "Switch5084",
            ConfigItem.configTypeBool,
            false
        )
    val switch5085 =
        addConfig(
            "Switch5085",
            ConfigItem.configTypeBool,
            false
        )
    val switch5086 =
        addConfig(
            "Switch5086",
            ConfigItem.configTypeBool,
            false
        )
    val switch5087 =
        addConfig(
            "Switch5087",
            ConfigItem.configTypeBool,
            false
        )
    val switch5088 =
        addConfig(
            "Switch5088",
            ConfigItem.configTypeBool,
            false
        )
    val switch5089 =
        addConfig(
            "Switch5089",
            ConfigItem.configTypeBool,
            false
        )
    val switch5090 =
        addConfig(
            "Switch5090",
            ConfigItem.configTypeBool,
            false
        )
    val switch5091 =
        addConfig(
            "Switch5091",
            ConfigItem.configTypeBool,
            false
        )
    val switch5092 =
        addConfig(
            "Switch5092",
            ConfigItem.configTypeBool,
            false
        )
    val switch5093 =
        addConfig(
            "Switch5093",
            ConfigItem.configTypeBool,
            false
        )
    val switch5094 =
        addConfig(
            "Switch5094",
            ConfigItem.configTypeBool,
            false
        )
    val switch5095 =
        addConfig(
            "Switch5095",
            ConfigItem.configTypeBool,
            false
        )
    val switch5096 =
        addConfig(
            "Switch5096",
            ConfigItem.configTypeBool,
            false
        )
    val switch5097 =
        addConfig(
            "Switch5097",
            ConfigItem.configTypeBool,
            false
        )
    val switch5098 =
        addConfig(
            "Switch5098",
            ConfigItem.configTypeBool,
            false
        )
    val switch5099 =
        addConfig(
            "Switch5099",
            ConfigItem.configTypeBool,
            false
        )
    val switch5100 =
        addConfig(
            "Switch5100",
            ConfigItem.configTypeBool,
            false
        )
    val switch5101 =
        addConfig(
            "Switch5101",
            ConfigItem.configTypeBool,
            false
        )
    val switch5102 =
        addConfig(
            "Switch5102",
            ConfigItem.configTypeBool,
            false
        )
    val switch5103 =
        addConfig(
            "Switch5103",
            ConfigItem.configTypeBool,
            false
        )
    val switch5104 =
        addConfig(
            "Switch5104",
            ConfigItem.configTypeBool,
            false
        )
    val switch5105 =
        addConfig(
            "Switch5105",
            ConfigItem.configTypeBool,
            false
        )
    val switch5106 =
        addConfig(
            "Switch5106",
            ConfigItem.configTypeBool,
            false
        )
    val switch5107 =
        addConfig(
            "Switch5107",
            ConfigItem.configTypeBool,
            false
        )
    val switch5108 =
        addConfig(
            "Switch5108",
            ConfigItem.configTypeBool,
            false
        )
    val switch5109 =
        addConfig(
            "Switch5109",
            ConfigItem.configTypeBool,
            false
        )
    val switch5110 =
        addConfig(
            "Switch5110",
            ConfigItem.configTypeBool,
            false
        )
    val switch5111 =
        addConfig(
            "Switch5111",
            ConfigItem.configTypeBool,
            false
        )
    val switch5112 =
        addConfig(
            "Switch5112",
            ConfigItem.configTypeBool,
            false
        )
    val switch5113 =
        addConfig(
            "Switch5113",
            ConfigItem.configTypeBool,
            false
        )
    val switch5114 =
        addConfig(
            "Switch5114",
            ConfigItem.configTypeBool,
            false
        )
    val switch5115 =
        addConfig(
            "Switch5115",
            ConfigItem.configTypeBool,
            false
        )
    val switch5116 =
        addConfig(
            "Switch5116",
            ConfigItem.configTypeBool,
            false
        )
    val switch5117 =
        addConfig(
            "Switch5117",
            ConfigItem.configTypeBool,
            false
        )
    val switch5118 =
        addConfig(
            "Switch5118",
            ConfigItem.configTypeBool,
            false
        )
    val switch5119 =
        addConfig(
            "Switch5119",
            ConfigItem.configTypeBool,
            false
        )
    val switch5120 =
        addConfig(
            "Switch5120",
            ConfigItem.configTypeBool,
            false
        )
    val switch5121 =
        addConfig(
            "Switch5121",
            ConfigItem.configTypeBool,
            false
        )
    val switch5122 =
        addConfig(
            "Switch5122",
            ConfigItem.configTypeBool,
            false
        )
    val switch5123 =
        addConfig(
            "Switch5123",
            ConfigItem.configTypeBool,
            false
        )
    val switch5124 =
        addConfig(
            "Switch5124",
            ConfigItem.configTypeBool,
            false
        )
    val switch5125 =
        addConfig(
            "Switch5125",
            ConfigItem.configTypeBool,
            false
        )
    val switch5126 =
        addConfig(
            "Switch5126",
            ConfigItem.configTypeBool,
            false
        )
    val switch5127 =
        addConfig(
            "Switch5127",
            ConfigItem.configTypeBool,
            false
        )
    val switch5128 =
        addConfig(
            "Switch5128",
            ConfigItem.configTypeBool,
            false
        )
    val switch5129 =
        addConfig(
            "Switch5129",
            ConfigItem.configTypeBool,
            false
        )
    val switch5130 =
        addConfig(
            "Switch5130",
            ConfigItem.configTypeBool,
            false
        )
    val switch5131 =
        addConfig(
            "Switch5131",
            ConfigItem.configTypeBool,
            false
        )
    val switch5132 =
        addConfig(
            "Switch5132",
            ConfigItem.configTypeBool,
            false
        )
    val switch5133 =
        addConfig(
            "Switch5133",
            ConfigItem.configTypeBool,
            false
        )
    val switch5134 =
        addConfig(
            "Switch5134",
            ConfigItem.configTypeBool,
            false
        )
    val switch5135 =
        addConfig(
            "Switch5135",
            ConfigItem.configTypeBool,
            false
        )
    val switch5136 =
        addConfig(
            "Switch5136",
            ConfigItem.configTypeBool,
            false
        )
    val switch5137 =
        addConfig(
            "Switch5137",
            ConfigItem.configTypeBool,
            false
        )
    val switch5138 =
        addConfig(
            "Switch5138",
            ConfigItem.configTypeBool,
            false
        )
    val switch5139 =
        addConfig(
            "Switch5139",
            ConfigItem.configTypeBool,
            false
        )
    val switch5140 =
        addConfig(
            "Switch5140",
            ConfigItem.configTypeBool,
            false
        )
    val switch5141 =
        addConfig(
            "Switch5141",
            ConfigItem.configTypeBool,
            false
        )
    val switch5142 =
        addConfig(
            "Switch5142",
            ConfigItem.configTypeBool,
            false
        )
    val switch5143 =
        addConfig(
            "Switch5143",
            ConfigItem.configTypeBool,
            false
        )
    val switch5144 =
        addConfig(
            "Switch5144",
            ConfigItem.configTypeBool,
            false
        )
    val switch5145 =
        addConfig(
            "Switch5145",
            ConfigItem.configTypeBool,
            false
        )
    val switch5146 =
        addConfig(
            "Switch5146",
            ConfigItem.configTypeBool,
            false
        )
    val switch5147 =
        addConfig(
            "Switch5147",
            ConfigItem.configTypeBool,
            false
        )
    val switch5148 =
        addConfig(
            "Switch5148",
            ConfigItem.configTypeBool,
            false
        )
    val switch5149 =
        addConfig(
            "Switch5149",
            ConfigItem.configTypeBool,
            false
        )
    val switch5150 =
        addConfig(
            "Switch5150",
            ConfigItem.configTypeBool,
            false
        )
    val switch5151 =
        addConfig(
            "Switch5151",
            ConfigItem.configTypeBool,
            false
        )
    val switch5152 =
        addConfig(
            "Switch5152",
            ConfigItem.configTypeBool,
            false
        )
    val switch5153 =
        addConfig(
            "Switch5153",
            ConfigItem.configTypeBool,
            false
        )
    val switch5154 =
        addConfig(
            "Switch5154",
            ConfigItem.configTypeBool,
            false
        )
    val switch5155 =
        addConfig(
            "Switch5155",
            ConfigItem.configTypeBool,
            false
        )
    val switch5156 =
        addConfig(
            "Switch5156",
            ConfigItem.configTypeBool,
            false
        )
    val switch5157 =
        addConfig(
            "Switch5157",
            ConfigItem.configTypeBool,
            false
        )
    val switch5158 =
        addConfig(
            "Switch5158",
            ConfigItem.configTypeBool,
            false
        )
    val switch5159 =
        addConfig(
            "Switch5159",
            ConfigItem.configTypeBool,
            false
        )
    val switch5160 =
        addConfig(
            "Switch5160",
            ConfigItem.configTypeBool,
            false
        )
    val switch5161 =
        addConfig(
            "Switch5161",
            ConfigItem.configTypeBool,
            false
        )
    val switch5162 =
        addConfig(
            "Switch5162",
            ConfigItem.configTypeBool,
            false
        )
    val switch5163 =
        addConfig(
            "Switch5163",
            ConfigItem.configTypeBool,
            false
        )
    val switch5164 =
        addConfig(
            "Switch5164",
            ConfigItem.configTypeBool,
            false
        )
    val switch5165 =
        addConfig(
            "Switch5165",
            ConfigItem.configTypeBool,
            false
        )
    val switch5166 =
        addConfig(
            "Switch5166",
            ConfigItem.configTypeBool,
            false
        )
    val switch5167 =
        addConfig(
            "Switch5167",
            ConfigItem.configTypeBool,
            false
        )
    val switch5168 =
        addConfig(
            "Switch5168",
            ConfigItem.configTypeBool,
            false
        )
    val switch5169 =
        addConfig(
            "Switch5169",
            ConfigItem.configTypeBool,
            false
        )
    val switch5170 =
        addConfig(
            "Switch5170",
            ConfigItem.configTypeBool,
            false
        )
    val switch5171 =
        addConfig(
            "Switch5171",
            ConfigItem.configTypeBool,
            false
        )
    val switch5172 =
        addConfig(
            "Switch5172",
            ConfigItem.configTypeBool,
            false
        )
    val switch5173 =
        addConfig(
            "Switch5173",
            ConfigItem.configTypeBool,
            false
        )
    val switch5174 =
        addConfig(
            "Switch5174",
            ConfigItem.configTypeBool,
            false
        )
    val switch5175 =
        addConfig(
            "Switch5175",
            ConfigItem.configTypeBool,
            false
        )
    val switch5176 =
        addConfig(
            "Switch5176",
            ConfigItem.configTypeBool,
            false
        )
    val switch5177 =
        addConfig(
            "Switch5177",
            ConfigItem.configTypeBool,
            false
        )
    val switch5178 =
        addConfig(
            "Switch5178",
            ConfigItem.configTypeBool,
            false
        )
    val switch5179 =
        addConfig(
            "Switch5179",
            ConfigItem.configTypeBool,
            false
        )
    val switch5180 =
        addConfig(
            "Switch5180",
            ConfigItem.configTypeBool,
            false
        )
    val switch5181 =
        addConfig(
            "Switch5181",
            ConfigItem.configTypeBool,
            false
        )
    val switch5182 =
        addConfig(
            "Switch5182",
            ConfigItem.configTypeBool,
            false
        )
    val switch5183 =
        addConfig(
            "Switch5183",
            ConfigItem.configTypeBool,
            false
        )
    val switch5184 =
        addConfig(
            "Switch5184",
            ConfigItem.configTypeBool,
            false
        )
    val switch5185 =
        addConfig(
            "Switch5185",
            ConfigItem.configTypeBool,
            false
        )
    val switch5186 =
        addConfig(
            "Switch5186",
            ConfigItem.configTypeBool,
            false
        )
    val switch5187 =
        addConfig(
            "Switch5187",
            ConfigItem.configTypeBool,
            false
        )
    val switch5188 =
        addConfig(
            "Switch5188",
            ConfigItem.configTypeBool,
            false
        )
    val switch5189 =
        addConfig(
            "Switch5189",
            ConfigItem.configTypeBool,
            false
        )
    val switch5190 =
        addConfig(
            "Switch5190",
            ConfigItem.configTypeBool,
            false
        )
    val switch5191 =
        addConfig(
            "Switch5191",
            ConfigItem.configTypeBool,
            false
        )
    val switch5192 =
        addConfig(
            "Switch5192",
            ConfigItem.configTypeBool,
            false
        )
    val switch5193 =
        addConfig(
            "Switch5193",
            ConfigItem.configTypeBool,
            false
        )
    val switch5194 =
        addConfig(
            "Switch5194",
            ConfigItem.configTypeBool,
            false
        )
    val switch5195 =
        addConfig(
            "Switch5195",
            ConfigItem.configTypeBool,
            false
        )
    val switch5196 =
        addConfig(
            "Switch5196",
            ConfigItem.configTypeBool,
            false
        )
    val switch5197 =
        addConfig(
            "Switch5197",
            ConfigItem.configTypeBool,
            false
        )
    val switch5198 =
        addConfig(
            "Switch5198",
            ConfigItem.configTypeBool,
            false
        )
    val switch5199 =
        addConfig(
            "Switch5199",
            ConfigItem.configTypeBool,
            false
        )
    val switch5200 =
        addConfig(
            "Switch5200",
            ConfigItem.configTypeBool,
            false
        )
    val switch5201 =
        addConfig(
            "Switch5201",
            ConfigItem.configTypeBool,
            false
        )
    val switch5202 =
        addConfig(
            "Switch5202",
            ConfigItem.configTypeBool,
            false
        )
    val switch5203 =
        addConfig(
            "Switch5203",
            ConfigItem.configTypeBool,
            false
        )
    val switch5204 =
        addConfig(
            "Switch5204",
            ConfigItem.configTypeBool,
            false
        )
    val switch5205 =
        addConfig(
            "Switch5205",
            ConfigItem.configTypeBool,
            false
        )
    val switch5206 =
        addConfig(
            "Switch5206",
            ConfigItem.configTypeBool,
            false
        )
    val switch5207 =
        addConfig(
            "Switch5207",
            ConfigItem.configTypeBool,
            false
        )
    val switch5208 =
        addConfig(
            "Switch5208",
            ConfigItem.configTypeBool,
            false
        )
    val switch5209 =
        addConfig(
            "Switch5209",
            ConfigItem.configTypeBool,
            false
        )
    val switch5210 =
        addConfig(
            "Switch5210",
            ConfigItem.configTypeBool,
            false
        )
    val switch5211 =
        addConfig(
            "Switch5211",
            ConfigItem.configTypeBool,
            false
        )
    val switch5212 =
        addConfig(
            "Switch5212",
            ConfigItem.configTypeBool,
            false
        )
    val switch5213 =
        addConfig(
            "Switch5213",
            ConfigItem.configTypeBool,
            false
        )
    val switch5214 =
        addConfig(
            "Switch5214",
            ConfigItem.configTypeBool,
            false
        )
    val switch5215 =
        addConfig(
            "Switch5215",
            ConfigItem.configTypeBool,
            false
        )
    val switch5216 =
        addConfig(
            "Switch5216",
            ConfigItem.configTypeBool,
            false
        )
    val switch5217 =
        addConfig(
            "Switch5217",
            ConfigItem.configTypeBool,
            false
        )
    val switch5218 =
        addConfig(
            "Switch5218",
            ConfigItem.configTypeBool,
            false
        )
    val switch5219 =
        addConfig(
            "Switch5219",
            ConfigItem.configTypeBool,
            false
        )
    val switch5220 =
        addConfig(
            "Switch5220",
            ConfigItem.configTypeBool,
            false
        )
    val switch5221 =
        addConfig(
            "Switch5221",
            ConfigItem.configTypeBool,
            false
        )
    val switch5222 =
        addConfig(
            "Switch5222",
            ConfigItem.configTypeBool,
            false
        )
    val switch5223 =
        addConfig(
            "Switch5223",
            ConfigItem.configTypeBool,
            false
        )
    val switch5224 =
        addConfig(
            "Switch5224",
            ConfigItem.configTypeBool,
            false
        )
    val switch5225 =
        addConfig(
            "Switch5225",
            ConfigItem.configTypeBool,
            false
        )
    val switch5226 =
        addConfig(
            "Switch5226",
            ConfigItem.configTypeBool,
            false
        )
    val switch5227 =
        addConfig(
            "Switch5227",
            ConfigItem.configTypeBool,
            false
        )
    val switch5228 =
        addConfig(
            "Switch5228",
            ConfigItem.configTypeBool,
            false
        )
    val switch5229 =
        addConfig(
            "Switch5229",
            ConfigItem.configTypeBool,
            false
        )
    val switch5230 =
        addConfig(
            "Switch5230",
            ConfigItem.configTypeBool,
            false
        )
    val switch5231 =
        addConfig(
            "Switch5231",
            ConfigItem.configTypeBool,
            false
        )
    val switch5232 =
        addConfig(
            "Switch5232",
            ConfigItem.configTypeBool,
            false
        )
    val switch5233 =
        addConfig(
            "Switch5233",
            ConfigItem.configTypeBool,
            false
        )
    val switch5234 =
        addConfig(
            "Switch5234",
            ConfigItem.configTypeBool,
            false
        )
    val switch5235 =
        addConfig(
            "Switch5235",
            ConfigItem.configTypeBool,
            false
        )
    val switch5236 =
        addConfig(
            "Switch5236",
            ConfigItem.configTypeBool,
            false
        )
    val switch5237 =
        addConfig(
            "Switch5237",
            ConfigItem.configTypeBool,
            false
        )
    val switch5238 =
        addConfig(
            "Switch5238",
            ConfigItem.configTypeBool,
            false
        )
    val switch5239 =
        addConfig(
            "Switch5239",
            ConfigItem.configTypeBool,
            false
        )
    val switch5240 =
        addConfig(
            "Switch5240",
            ConfigItem.configTypeBool,
            false
        )
    val switch5241 =
        addConfig(
            "Switch5241",
            ConfigItem.configTypeBool,
            false
        )
    val switch5242 =
        addConfig(
            "Switch5242",
            ConfigItem.configTypeBool,
            false
        )
    val switch5243 =
        addConfig(
            "Switch5243",
            ConfigItem.configTypeBool,
            false
        )
    val switch5244 =
        addConfig(
            "Switch5244",
            ConfigItem.configTypeBool,
            false
        )
    val switch5245 =
        addConfig(
            "Switch5245",
            ConfigItem.configTypeBool,
            false
        )
    val switch5246 =
        addConfig(
            "Switch5246",
            ConfigItem.configTypeBool,
            false
        )
    val switch5247 =
        addConfig(
            "Switch5247",
            ConfigItem.configTypeBool,
            false
        )
    val switch5248 =
        addConfig(
            "Switch5248",
            ConfigItem.configTypeBool,
            false
        )
    val switch5249 =
        addConfig(
            "Switch5249",
            ConfigItem.configTypeBool,
            false
        )
    val switch5250 =
        addConfig(
            "Switch5250",
            ConfigItem.configTypeBool,
            false
        )
    val switch5251 =
        addConfig(
            "Switch5251",
            ConfigItem.configTypeBool,
            false
        )
    val switch5252 =
        addConfig(
            "Switch5252",
            ConfigItem.configTypeBool,
            false
        )
    val switch5253 =
        addConfig(
            "Switch5253",
            ConfigItem.configTypeBool,
            false
        )
    val switch5254 =
        addConfig(
            "Switch5254",
            ConfigItem.configTypeBool,
            false
        )
    val switch5255 =
        addConfig(
            "Switch5255",
            ConfigItem.configTypeBool,
            false
        )
    val switch5256 =
        addConfig(
            "Switch5256",
            ConfigItem.configTypeBool,
            false
        )
    val switch5257 =
        addConfig(
            "Switch5257",
            ConfigItem.configTypeBool,
            false
        )
    val switch5258 =
        addConfig(
            "Switch5258",
            ConfigItem.configTypeBool,
            false
        )
    val switch5259 =
        addConfig(
            "Switch5259",
            ConfigItem.configTypeBool,
            false
        )
    val switch5260 =
        addConfig(
            "Switch5260",
            ConfigItem.configTypeBool,
            false
        )
    val switch5261 =
        addConfig(
            "Switch5261",
            ConfigItem.configTypeBool,
            false
        )
    val switch5262 =
        addConfig(
            "Switch5262",
            ConfigItem.configTypeBool,
            false
        )
    val switch5263 =
        addConfig(
            "Switch5263",
            ConfigItem.configTypeBool,
            false
        )
    val switch5264 =
        addConfig(
            "Switch5264",
            ConfigItem.configTypeBool,
            false
        )
    val switch5265 =
        addConfig(
            "Switch5265",
            ConfigItem.configTypeBool,
            false
        )
    val switch5266 =
        addConfig(
            "Switch5266",
            ConfigItem.configTypeBool,
            false
        )
    val switch5267 =
        addConfig(
            "Switch5267",
            ConfigItem.configTypeBool,
            false
        )
    val switch5268 =
        addConfig(
            "Switch5268",
            ConfigItem.configTypeBool,
            false
        )
    val switch5269 =
        addConfig(
            "Switch5269",
            ConfigItem.configTypeBool,
            false
        )
    val switch5270 =
        addConfig(
            "Switch5270",
            ConfigItem.configTypeBool,
            false
        )
    val switch5271 =
        addConfig(
            "Switch5271",
            ConfigItem.configTypeBool,
            false
        )
    val switch5272 =
        addConfig(
            "Switch5272",
            ConfigItem.configTypeBool,
            false
        )
    val switch5273 =
        addConfig(
            "Switch5273",
            ConfigItem.configTypeBool,
            false
        )
    val switch5274 =
        addConfig(
            "Switch5274",
            ConfigItem.configTypeBool,
            false
        )
    val switch5275 =
        addConfig(
            "Switch5275",
            ConfigItem.configTypeBool,
            false
        )
    val switch5276 =
        addConfig(
            "Switch5276",
            ConfigItem.configTypeBool,
            false
        )
    val switch5277 =
        addConfig(
            "Switch5277",
            ConfigItem.configTypeBool,
            false
        )
    val switch5278 =
        addConfig(
            "Switch5278",
            ConfigItem.configTypeBool,
            false
        )
    val switch5279 =
        addConfig(
            "Switch5279",
            ConfigItem.configTypeBool,
            false
        )
    val switch5280 =
        addConfig(
            "Switch5280",
            ConfigItem.configTypeBool,
            false
        )
    val switch5281 =
        addConfig(
            "Switch5281",
            ConfigItem.configTypeBool,
            false
        )
    val switch5282 =
        addConfig(
            "Switch5282",
            ConfigItem.configTypeBool,
            false
        )
    val switch5283 =
        addConfig(
            "Switch5283",
            ConfigItem.configTypeBool,
            false
        )
    val switch5284 =
        addConfig(
            "Switch5284",
            ConfigItem.configTypeBool,
            false
        )
    val switch5285 =
        addConfig(
            "Switch5285",
            ConfigItem.configTypeBool,
            false
        )
    val switch5286 =
        addConfig(
            "Switch5286",
            ConfigItem.configTypeBool,
            false
        )
    val switch5287 =
        addConfig(
            "Switch5287",
            ConfigItem.configTypeBool,
            false
        )
    val switch5288 =
        addConfig(
            "Switch5288",
            ConfigItem.configTypeBool,
            false
        )
    val switch5289 =
        addConfig(
            "Switch5289",
            ConfigItem.configTypeBool,
            false
        )
    val switch5290 =
        addConfig(
            "Switch5290",
            ConfigItem.configTypeBool,
            false
        )
    val switch5291 =
        addConfig(
            "Switch5291",
            ConfigItem.configTypeBool,
            false
        )
    val switch5292 =
        addConfig(
            "Switch5292",
            ConfigItem.configTypeBool,
            false
        )
    val switch5293 =
        addConfig(
            "Switch5293",
            ConfigItem.configTypeBool,
            false
        )
    val switch5294 =
        addConfig(
            "Switch5294",
            ConfigItem.configTypeBool,
            false
        )
    val switch5295 =
        addConfig(
            "Switch5295",
            ConfigItem.configTypeBool,
            false
        )
    val switch5296 =
        addConfig(
            "Switch5296",
            ConfigItem.configTypeBool,
            false
        )
    val switch5297 =
        addConfig(
            "Switch5297",
            ConfigItem.configTypeBool,
            false
        )
    val switch5298 =
        addConfig(
            "Switch5298",
            ConfigItem.configTypeBool,
            false
        )
    val switch5299 =
        addConfig(
            "Switch5299",
            ConfigItem.configTypeBool,
            false
        )
    val switch5300 =
        addConfig(
            "Switch5300",
            ConfigItem.configTypeBool,
            false
        )
    val switch5301 =
        addConfig(
            "Switch5301",
            ConfigItem.configTypeBool,
            false
        )
    val switch5302 =
        addConfig(
            "Switch5302",
            ConfigItem.configTypeBool,
            false
        )
    val switch5303 =
        addConfig(
            "Switch5303",
            ConfigItem.configTypeBool,
            false
        )
    val switch5304 =
        addConfig(
            "Switch5304",
            ConfigItem.configTypeBool,
            false
        )
    val switch5305 =
        addConfig(
            "Switch5305",
            ConfigItem.configTypeBool,
            false
        )
    val switch5306 =
        addConfig(
            "Switch5306",
            ConfigItem.configTypeBool,
            false
        )
    val switch5307 =
        addConfig(
            "Switch5307",
            ConfigItem.configTypeBool,
            false
        )
    val switch5308 =
        addConfig(
            "Switch5308",
            ConfigItem.configTypeBool,
            false
        )
    val switch5309 =
        addConfig(
            "Switch5309",
            ConfigItem.configTypeBool,
            false
        )
    val switch5310 =
        addConfig(
            "Switch5310",
            ConfigItem.configTypeBool,
            false
        )
    val switch5311 =
        addConfig(
            "Switch5311",
            ConfigItem.configTypeBool,
            false
        )
    val switch5312 =
        addConfig(
            "Switch5312",
            ConfigItem.configTypeBool,
            false
        )
    val switch5313 =
        addConfig(
            "Switch5313",
            ConfigItem.configTypeBool,
            false
        )
    val switch5314 =
        addConfig(
            "Switch5314",
            ConfigItem.configTypeBool,
            false
        )
    val switch5315 =
        addConfig(
            "Switch5315",
            ConfigItem.configTypeBool,
            false
        )
    val switch5316 =
        addConfig(
            "Switch5316",
            ConfigItem.configTypeBool,
            false
        )
    val switch5317 =
        addConfig(
            "Switch5317",
            ConfigItem.configTypeBool,
            false
        )
    val switch5318 =
        addConfig(
            "Switch5318",
            ConfigItem.configTypeBool,
            false
        )
    val switch5319 =
        addConfig(
            "Switch5319",
            ConfigItem.configTypeBool,
            false
        )
    val switch5320 =
        addConfig(
            "Switch5320",
            ConfigItem.configTypeBool,
            false
        )
    val switch5321 =
        addConfig(
            "Switch5321",
            ConfigItem.configTypeBool,
            false
        )
    val switch5322 =
        addConfig(
            "Switch5322",
            ConfigItem.configTypeBool,
            false
        )
    val switch5323 =
        addConfig(
            "Switch5323",
            ConfigItem.configTypeBool,
            false
        )
    val switch5324 =
        addConfig(
            "Switch5324",
            ConfigItem.configTypeBool,
            false
        )
    val switch5325 =
        addConfig(
            "Switch5325",
            ConfigItem.configTypeBool,
            false
        )
    val switch5326 =
        addConfig(
            "Switch5326",
            ConfigItem.configTypeBool,
            false
        )
    val switch5327 =
        addConfig(
            "Switch5327",
            ConfigItem.configTypeBool,
            false
        )
    val switch5328 =
        addConfig(
            "Switch5328",
            ConfigItem.configTypeBool,
            false
        )
    val switch5329 =
        addConfig(
            "Switch5329",
            ConfigItem.configTypeBool,
            false
        )
    val switch5330 =
        addConfig(
            "Switch5330",
            ConfigItem.configTypeBool,
            false
        )
    val switch5331 =
        addConfig(
            "Switch5331",
            ConfigItem.configTypeBool,
            false
        )
    val switch5332 =
        addConfig(
            "Switch5332",
            ConfigItem.configTypeBool,
            false
        )
    val switch5333 =
        addConfig(
            "Switch5333",
            ConfigItem.configTypeBool,
            false
        )
    val switch5334 =
        addConfig(
            "Switch5334",
            ConfigItem.configTypeBool,
            false
        )
    val switch5335 =
        addConfig(
            "Switch5335",
            ConfigItem.configTypeBool,
            false
        )
    val switch5336 =
        addConfig(
            "Switch5336",
            ConfigItem.configTypeBool,
            false
        )
    val switch5337 =
        addConfig(
            "Switch5337",
            ConfigItem.configTypeBool,
            false
        )
    val switch5338 =
        addConfig(
            "Switch5338",
            ConfigItem.configTypeBool,
            false
        )
    val switch5339 =
        addConfig(
            "Switch5339",
            ConfigItem.configTypeBool,
            false
        )
    val switch5340 =
        addConfig(
            "Switch5340",
            ConfigItem.configTypeBool,
            false
        )
    val switch5341 =
        addConfig(
            "Switch5341",
            ConfigItem.configTypeBool,
            false
        )
    val switch5342 =
        addConfig(
            "Switch5342",
            ConfigItem.configTypeBool,
            false
        )
    val switch5343 =
        addConfig(
            "Switch5343",
            ConfigItem.configTypeBool,
            false
        )
    val switch5344 =
        addConfig(
            "Switch5344",
            ConfigItem.configTypeBool,
            false
        )
    val switch5345 =
        addConfig(
            "Switch5345",
            ConfigItem.configTypeBool,
            false
        )
    val switch5346 =
        addConfig(
            "Switch5346",
            ConfigItem.configTypeBool,
            false
        )
    val switch5347 =
        addConfig(
            "Switch5347",
            ConfigItem.configTypeBool,
            false
        )
    val switch5348 =
        addConfig(
            "Switch5348",
            ConfigItem.configTypeBool,
            false
        )
    val switch5349 =
        addConfig(
            "Switch5349",
            ConfigItem.configTypeBool,
            false
        )
    val switch5350 =
        addConfig(
            "Switch5350",
            ConfigItem.configTypeBool,
            false
        )
    val switch5351 =
        addConfig(
            "Switch5351",
            ConfigItem.configTypeBool,
            false
        )
    val switch5352 =
        addConfig(
            "Switch5352",
            ConfigItem.configTypeBool,
            false
        )
    val switch5353 =
        addConfig(
            "Switch5353",
            ConfigItem.configTypeBool,
            false
        )
    val switch5354 =
        addConfig(
            "Switch5354",
            ConfigItem.configTypeBool,
            false
        )
    val switch5355 =
        addConfig(
            "Switch5355",
            ConfigItem.configTypeBool,
            false
        )
    val switch5356 =
        addConfig(
            "Switch5356",
            ConfigItem.configTypeBool,
            false
        )
    val switch5357 =
        addConfig(
            "Switch5357",
            ConfigItem.configTypeBool,
            false
        )
    val switch5358 =
        addConfig(
            "Switch5358",
            ConfigItem.configTypeBool,
            false
        )
    val switch5359 =
        addConfig(
            "Switch5359",
            ConfigItem.configTypeBool,
            false
        )
    val switch5360 =
        addConfig(
            "Switch5360",
            ConfigItem.configTypeBool,
            false
        )
    val switch5361 =
        addConfig(
            "Switch5361",
            ConfigItem.configTypeBool,
            false
        )
    val switch5362 =
        addConfig(
            "Switch5362",
            ConfigItem.configTypeBool,
            false
        )
    val switch5363 =
        addConfig(
            "Switch5363",
            ConfigItem.configTypeBool,
            false
        )
    val switch5364 =
        addConfig(
            "Switch5364",
            ConfigItem.configTypeBool,
            false
        )
    val switch5365 =
        addConfig(
            "Switch5365",
            ConfigItem.configTypeBool,
            false
        )
    val switch5366 =
        addConfig(
            "Switch5366",
            ConfigItem.configTypeBool,
            false
        )
    val switch5367 =
        addConfig(
            "Switch5367",
            ConfigItem.configTypeBool,
            false
        )
    val switch5368 =
        addConfig(
            "Switch5368",
            ConfigItem.configTypeBool,
            false
        )
    val switch5369 =
        addConfig(
            "Switch5369",
            ConfigItem.configTypeBool,
            false
        )
    val switch5370 =
        addConfig(
            "Switch5370",
            ConfigItem.configTypeBool,
            false
        )
    val switch5371 =
        addConfig(
            "Switch5371",
            ConfigItem.configTypeBool,
            false
        )
    val switch5372 =
        addConfig(
            "Switch5372",
            ConfigItem.configTypeBool,
            false
        )
    val switch5373 =
        addConfig(
            "Switch5373",
            ConfigItem.configTypeBool,
            false
        )
    val switch5374 =
        addConfig(
            "Switch5374",
            ConfigItem.configTypeBool,
            false
        )
    val switch5375 =
        addConfig(
            "Switch5375",
            ConfigItem.configTypeBool,
            false
        )
    val switch5376 =
        addConfig(
            "Switch5376",
            ConfigItem.configTypeBool,
            false
        )
    val switch5377 =
        addConfig(
            "Switch5377",
            ConfigItem.configTypeBool,
            false
        )
    val switch5378 =
        addConfig(
            "Switch5378",
            ConfigItem.configTypeBool,
            false
        )
    val switch5379 =
        addConfig(
            "Switch5379",
            ConfigItem.configTypeBool,
            false
        )
    val switch5380 =
        addConfig(
            "Switch5380",
            ConfigItem.configTypeBool,
            false
        )
    val switch5381 =
        addConfig(
            "Switch5381",
            ConfigItem.configTypeBool,
            false
        )
    val switch5382 =
        addConfig(
            "Switch5382",
            ConfigItem.configTypeBool,
            false
        )
    val switch5383 =
        addConfig(
            "Switch5383",
            ConfigItem.configTypeBool,
            false
        )
    val switch5384 =
        addConfig(
            "Switch5384",
            ConfigItem.configTypeBool,
            false
        )
    val switch5385 =
        addConfig(
            "Switch5385",
            ConfigItem.configTypeBool,
            false
        )
    val switch5386 =
        addConfig(
            "Switch5386",
            ConfigItem.configTypeBool,
            false
        )
    val switch5387 =
        addConfig(
            "Switch5387",
            ConfigItem.configTypeBool,
            false
        )
    val switch5388 =
        addConfig(
            "Switch5388",
            ConfigItem.configTypeBool,
            false
        )
    val switch5389 =
        addConfig(
            "Switch5389",
            ConfigItem.configTypeBool,
            false
        )
    val switch5390 =
        addConfig(
            "Switch5390",
            ConfigItem.configTypeBool,
            false
        )
    val switch5391 =
        addConfig(
            "Switch5391",
            ConfigItem.configTypeBool,
            false
        )
    val switch5392 =
        addConfig(
            "Switch5392",
            ConfigItem.configTypeBool,
            false
        )
    val switch5393 =
        addConfig(
            "Switch5393",
            ConfigItem.configTypeBool,
            false
        )
    val switch5394 =
        addConfig(
            "Switch5394",
            ConfigItem.configTypeBool,
            false
        )
    val switch5395 =
        addConfig(
            "Switch5395",
            ConfigItem.configTypeBool,
            false
        )
    val switch5396 =
        addConfig(
            "Switch5396",
            ConfigItem.configTypeBool,
            false
        )
    val switch5397 =
        addConfig(
            "Switch5397",
            ConfigItem.configTypeBool,
            false
        )
    val switch5398 =
        addConfig(
            "Switch5398",
            ConfigItem.configTypeBool,
            false
        )
    val switch5399 =
        addConfig(
            "Switch5399",
            ConfigItem.configTypeBool,
            false
        )
    val switch5400 =
        addConfig(
            "Switch5400",
            ConfigItem.configTypeBool,
            false
        )
    val switch5401 =
        addConfig(
            "Switch5401",
            ConfigItem.configTypeBool,
            false
        )
    val switch5402 =
        addConfig(
            "Switch5402",
            ConfigItem.configTypeBool,
            false
        )
    val switch5403 =
        addConfig(
            "Switch5403",
            ConfigItem.configTypeBool,
            false
        )
    val switch5404 =
        addConfig(
            "Switch5404",
            ConfigItem.configTypeBool,
            false
        )
    val switch5405 =
        addConfig(
            "Switch5405",
            ConfigItem.configTypeBool,
            false
        )
    val switch5406 =
        addConfig(
            "Switch5406",
            ConfigItem.configTypeBool,
            false
        )
    val switch5407 =
        addConfig(
            "Switch5407",
            ConfigItem.configTypeBool,
            false
        )
    val switch5408 =
        addConfig(
            "Switch5408",
            ConfigItem.configTypeBool,
            false
        )
    val switch5409 =
        addConfig(
            "Switch5409",
            ConfigItem.configTypeBool,
            false
        )
    val switch5410 =
        addConfig(
            "Switch5410",
            ConfigItem.configTypeBool,
            false
        )
    val switch5411 =
        addConfig(
            "Switch5411",
            ConfigItem.configTypeBool,
            false
        )
    val switch5412 =
        addConfig(
            "Switch5412",
            ConfigItem.configTypeBool,
            false
        )
    val switch5413 =
        addConfig(
            "Switch5413",
            ConfigItem.configTypeBool,
            false
        )
    val switch5414 =
        addConfig(
            "Switch5414",
            ConfigItem.configTypeBool,
            false
        )
    val switch5415 =
        addConfig(
            "Switch5415",
            ConfigItem.configTypeBool,
            false
        )
    val switch5416 =
        addConfig(
            "Switch5416",
            ConfigItem.configTypeBool,
            false
        )
    val switch5417 =
        addConfig(
            "Switch5417",
            ConfigItem.configTypeBool,
            false
        )
    val switch5418 =
        addConfig(
            "Switch5418",
            ConfigItem.configTypeBool,
            false
        )
    val switch5419 =
        addConfig(
            "Switch5419",
            ConfigItem.configTypeBool,
            false
        )
    val switch5420 =
        addConfig(
            "Switch5420",
            ConfigItem.configTypeBool,
            false
        )
    val switch5421 =
        addConfig(
            "Switch5421",
            ConfigItem.configTypeBool,
            false
        )
    val switch5422 =
        addConfig(
            "Switch5422",
            ConfigItem.configTypeBool,
            false
        )
    val switch5423 =
        addConfig(
            "Switch5423",
            ConfigItem.configTypeBool,
            false
        )
    val switch5424 =
        addConfig(
            "Switch5424",
            ConfigItem.configTypeBool,
            false
        )
    val switch5425 =
        addConfig(
            "Switch5425",
            ConfigItem.configTypeBool,
            false
        )
    val switch5426 =
        addConfig(
            "Switch5426",
            ConfigItem.configTypeBool,
            false
        )
    val switch5427 =
        addConfig(
            "Switch5427",
            ConfigItem.configTypeBool,
            false
        )
    val switch5428 =
        addConfig(
            "Switch5428",
            ConfigItem.configTypeBool,
            false
        )
    val switch5429 =
        addConfig(
            "Switch5429",
            ConfigItem.configTypeBool,
            false
        )
    val switch5430 =
        addConfig(
            "Switch5430",
            ConfigItem.configTypeBool,
            false
        )
    val switch5431 =
        addConfig(
            "Switch5431",
            ConfigItem.configTypeBool,
            false
        )
    val switch5432 =
        addConfig(
            "Switch5432",
            ConfigItem.configTypeBool,
            false
        )
    val switch5433 =
        addConfig(
            "Switch5433",
            ConfigItem.configTypeBool,
            false
        )
    val switch5434 =
        addConfig(
            "Switch5434",
            ConfigItem.configTypeBool,
            false
        )
    val switch5435 =
        addConfig(
            "Switch5435",
            ConfigItem.configTypeBool,
            false
        )
    val switch5436 =
        addConfig(
            "Switch5436",
            ConfigItem.configTypeBool,
            false
        )
    val switch5437 =
        addConfig(
            "Switch5437",
            ConfigItem.configTypeBool,
            false
        )
    val switch5438 =
        addConfig(
            "Switch5438",
            ConfigItem.configTypeBool,
            false
        )
    val switch5439 =
        addConfig(
            "Switch5439",
            ConfigItem.configTypeBool,
            false
        )
    val switch5440 =
        addConfig(
            "Switch5440",
            ConfigItem.configTypeBool,
            false
        )
    val switch5441 =
        addConfig(
            "Switch5441",
            ConfigItem.configTypeBool,
            false
        )
    val switch5442 =
        addConfig(
            "Switch5442",
            ConfigItem.configTypeBool,
            false
        )
    val switch5443 =
        addConfig(
            "Switch5443",
            ConfigItem.configTypeBool,
            false
        )
    val switch5444 =
        addConfig(
            "Switch5444",
            ConfigItem.configTypeBool,
            false
        )
    val switch5445 =
        addConfig(
            "Switch5445",
            ConfigItem.configTypeBool,
            false
        )
    val switch5446 =
        addConfig(
            "Switch5446",
            ConfigItem.configTypeBool,
            false
        )
    val switch5447 =
        addConfig(
            "Switch5447",
            ConfigItem.configTypeBool,
            false
        )
    val switch5448 =
        addConfig(
            "Switch5448",
            ConfigItem.configTypeBool,
            false
        )
    val switch5449 =
        addConfig(
            "Switch5449",
            ConfigItem.configTypeBool,
            false
        )
    val switch5450 =
        addConfig(
            "Switch5450",
            ConfigItem.configTypeBool,
            false
        )
    val switch5451 =
        addConfig(
            "Switch5451",
            ConfigItem.configTypeBool,
            false
        )
    val switch5452 =
        addConfig(
            "Switch5452",
            ConfigItem.configTypeBool,
            false
        )
    val switch5453 =
        addConfig(
            "Switch5453",
            ConfigItem.configTypeBool,
            false
        )
    val switch5454 =
        addConfig(
            "Switch5454",
            ConfigItem.configTypeBool,
            false
        )
    val switch5455 =
        addConfig(
            "Switch5455",
            ConfigItem.configTypeBool,
            false
        )
    val switch5456 =
        addConfig(
            "Switch5456",
            ConfigItem.configTypeBool,
            false
        )
    val switch5457 =
        addConfig(
            "Switch5457",
            ConfigItem.configTypeBool,
            false
        )
    val switch5458 =
        addConfig(
            "Switch5458",
            ConfigItem.configTypeBool,
            false
        )
    val switch5459 =
        addConfig(
            "Switch5459",
            ConfigItem.configTypeBool,
            false
        )
    val switch5460 =
        addConfig(
            "Switch5460",
            ConfigItem.configTypeBool,
            false
        )
    val switch5461 =
        addConfig(
            "Switch5461",
            ConfigItem.configTypeBool,
            false
        )
    val switch5462 =
        addConfig(
            "Switch5462",
            ConfigItem.configTypeBool,
            false
        )
    val switch5463 =
        addConfig(
            "Switch5463",
            ConfigItem.configTypeBool,
            false
        )
    val switch5464 =
        addConfig(
            "Switch5464",
            ConfigItem.configTypeBool,
            false
        )
    val switch5465 =
        addConfig(
            "Switch5465",
            ConfigItem.configTypeBool,
            false
        )
    val switch5466 =
        addConfig(
            "Switch5466",
            ConfigItem.configTypeBool,
            false
        )
    val switch5467 =
        addConfig(
            "Switch5467",
            ConfigItem.configTypeBool,
            false
        )
    val switch5468 =
        addConfig(
            "Switch5468",
            ConfigItem.configTypeBool,
            false
        )
    val switch5469 =
        addConfig(
            "Switch5469",
            ConfigItem.configTypeBool,
            false
        )
    val switch5470 =
        addConfig(
            "Switch5470",
            ConfigItem.configTypeBool,
            false
        )
    val switch5471 =
        addConfig(
            "Switch5471",
            ConfigItem.configTypeBool,
            false
        )
    val switch5472 =
        addConfig(
            "Switch5472",
            ConfigItem.configTypeBool,
            false
        )
    val switch5473 =
        addConfig(
            "Switch5473",
            ConfigItem.configTypeBool,
            false
        )
    val switch5474 =
        addConfig(
            "Switch5474",
            ConfigItem.configTypeBool,
            false
        )
    val switch5475 =
        addConfig(
            "Switch5475",
            ConfigItem.configTypeBool,
            false
        )
    val switch5476 =
        addConfig(
            "Switch5476",
            ConfigItem.configTypeBool,
            false
        )
    val switch5477 =
        addConfig(
            "Switch5477",
            ConfigItem.configTypeBool,
            false
        )
    val switch5478 =
        addConfig(
            "Switch5478",
            ConfigItem.configTypeBool,
            false
        )
    val switch5479 =
        addConfig(
            "Switch5479",
            ConfigItem.configTypeBool,
            false
        )
    val switch5480 =
        addConfig(
            "Switch5480",
            ConfigItem.configTypeBool,
            false
        )
    val switch5481 =
        addConfig(
            "Switch5481",
            ConfigItem.configTypeBool,
            false
        )
    val switch5482 =
        addConfig(
            "Switch5482",
            ConfigItem.configTypeBool,
            false
        )
    val switch5483 =
        addConfig(
            "Switch5483",
            ConfigItem.configTypeBool,
            false
        )
    val switch5484 =
        addConfig(
            "Switch5484",
            ConfigItem.configTypeBool,
            false
        )
    val switch5485 =
        addConfig(
            "Switch5485",
            ConfigItem.configTypeBool,
            false
        )
    val switch5486 =
        addConfig(
            "Switch5486",
            ConfigItem.configTypeBool,
            false
        )
    val switch5487 =
        addConfig(
            "Switch5487",
            ConfigItem.configTypeBool,
            false
        )
    val switch5488 =
        addConfig(
            "Switch5488",
            ConfigItem.configTypeBool,
            false
        )
    val switch5489 =
        addConfig(
            "Switch5489",
            ConfigItem.configTypeBool,
            false
        )
    val switch5490 =
        addConfig(
            "Switch5490",
            ConfigItem.configTypeBool,
            false
        )
    val switch5491 =
        addConfig(
            "Switch5491",
            ConfigItem.configTypeBool,
            false
        )
    val switch5492 =
        addConfig(
            "Switch5492",
            ConfigItem.configTypeBool,
            false
        )
    val switch5493 =
        addConfig(
            "Switch5493",
            ConfigItem.configTypeBool,
            false
        )
    val switch5494 =
        addConfig(
            "Switch5494",
            ConfigItem.configTypeBool,
            false
        )
    val switch5495 =
        addConfig(
            "Switch5495",
            ConfigItem.configTypeBool,
            false
        )
    val switch5496 =
        addConfig(
            "Switch5496",
            ConfigItem.configTypeBool,
            false
        )
    val switch5497 =
        addConfig(
            "Switch5497",
            ConfigItem.configTypeBool,
            false
        )
    val switch5498 =
        addConfig(
            "Switch5498",
            ConfigItem.configTypeBool,
            false
        )
    val switch5499 =
        addConfig(
            "Switch5499",
            ConfigItem.configTypeBool,
            false
        )
    val switch5500 =
        addConfig(
            "Switch5500",
            ConfigItem.configTypeBool,
            false
        )
    val switch5501 =
        addConfig(
            "Switch5501",
            ConfigItem.configTypeBool,
            false
        )
    val switch5502 =
        addConfig(
            "Switch5502",
            ConfigItem.configTypeBool,
            false
        )
    val switch5503 =
        addConfig(
            "Switch5503",
            ConfigItem.configTypeBool,
            false
        )
    val switch5504 =
        addConfig(
            "Switch5504",
            ConfigItem.configTypeBool,
            false
        )
    val switch5505 =
        addConfig(
            "Switch5505",
            ConfigItem.configTypeBool,
            false
        )
    val switch5506 =
        addConfig(
            "Switch5506",
            ConfigItem.configTypeBool,
            false
        )
    val switch5507 =
        addConfig(
            "Switch5507",
            ConfigItem.configTypeBool,
            false
        )
    val switch5508 =
        addConfig(
            "Switch5508",
            ConfigItem.configTypeBool,
            false
        )
    val switch5509 =
        addConfig(
            "Switch5509",
            ConfigItem.configTypeBool,
            false
        )
    val switch5510 =
        addConfig(
            "Switch5510",
            ConfigItem.configTypeBool,
            false
        )
    val switch5511 =
        addConfig(
            "Switch5511",
            ConfigItem.configTypeBool,
            false
        )
    val switch5512 =
        addConfig(
            "Switch5512",
            ConfigItem.configTypeBool,
            false
        )
    val switch5513 =
        addConfig(
            "Switch5513",
            ConfigItem.configTypeBool,
            false
        )
    val switch5514 =
        addConfig(
            "Switch5514",
            ConfigItem.configTypeBool,
            false
        )
    val switch5515 =
        addConfig(
            "Switch5515",
            ConfigItem.configTypeBool,
            false
        )
    val switch5516 =
        addConfig(
            "Switch5516",
            ConfigItem.configTypeBool,
            false
        )
    val switch5517 =
        addConfig(
            "Switch5517",
            ConfigItem.configTypeBool,
            false
        )
    val switch5518 =
        addConfig(
            "Switch5518",
            ConfigItem.configTypeBool,
            false
        )
    val switch5519 =
        addConfig(
            "Switch5519",
            ConfigItem.configTypeBool,
            false
        )
    val switch5520 =
        addConfig(
            "Switch5520",
            ConfigItem.configTypeBool,
            false
        )
    val switch5521 =
        addConfig(
            "Switch5521",
            ConfigItem.configTypeBool,
            false
        )
    val switch5522 =
        addConfig(
            "Switch5522",
            ConfigItem.configTypeBool,
            false
        )
    val switch5523 =
        addConfig(
            "Switch5523",
            ConfigItem.configTypeBool,
            false
        )
    val switch5524 =
        addConfig(
            "Switch5524",
            ConfigItem.configTypeBool,
            false
        )
    val switch5525 =
        addConfig(
            "Switch5525",
            ConfigItem.configTypeBool,
            false
        )
    val switch5526 =
        addConfig(
            "Switch5526",
            ConfigItem.configTypeBool,
            false
        )
    val switch5527 =
        addConfig(
            "Switch5527",
            ConfigItem.configTypeBool,
            false
        )
    val switch5528 =
        addConfig(
            "Switch5528",
            ConfigItem.configTypeBool,
            false
        )
    val switch5529 =
        addConfig(
            "Switch5529",
            ConfigItem.configTypeBool,
            false
        )
    val switch5530 =
        addConfig(
            "Switch5530",
            ConfigItem.configTypeBool,
            false
        )
    val switch5531 =
        addConfig(
            "Switch5531",
            ConfigItem.configTypeBool,
            false
        )
    val switch5532 =
        addConfig(
            "Switch5532",
            ConfigItem.configTypeBool,
            false
        )
    val switch5533 =
        addConfig(
            "Switch5533",
            ConfigItem.configTypeBool,
            false
        )
    val switch5534 =
        addConfig(
            "Switch5534",
            ConfigItem.configTypeBool,
            false
        )
    val switch5535 =
        addConfig(
            "Switch5535",
            ConfigItem.configTypeBool,
            false
        )
    val switch5536 =
        addConfig(
            "Switch5536",
            ConfigItem.configTypeBool,
            false
        )
    val switch5537 =
        addConfig(
            "Switch5537",
            ConfigItem.configTypeBool,
            false
        )
    val switch5538 =
        addConfig(
            "Switch5538",
            ConfigItem.configTypeBool,
            false
        )
    val switch5539 =
        addConfig(
            "Switch5539",
            ConfigItem.configTypeBool,
            false
        )
    val switch5540 =
        addConfig(
            "Switch5540",
            ConfigItem.configTypeBool,
            false
        )
    val switch5541 =
        addConfig(
            "Switch5541",
            ConfigItem.configTypeBool,
            false
        )
    val switch5542 =
        addConfig(
            "Switch5542",
            ConfigItem.configTypeBool,
            false
        )
    val switch5543 =
        addConfig(
            "Switch5543",
            ConfigItem.configTypeBool,
            false
        )
    val switch5544 =
        addConfig(
            "Switch5544",
            ConfigItem.configTypeBool,
            false
        )
    val switch5545 =
        addConfig(
            "Switch5545",
            ConfigItem.configTypeBool,
            false
        )
    val switch5546 =
        addConfig(
            "Switch5546",
            ConfigItem.configTypeBool,
            false
        )
    val switch5547 =
        addConfig(
            "Switch5547",
            ConfigItem.configTypeBool,
            false
        )
    val switch5548 =
        addConfig(
            "Switch5548",
            ConfigItem.configTypeBool,
            false
        )
    val switch5549 =
        addConfig(
            "Switch5549",
            ConfigItem.configTypeBool,
            false
        )
    val switch5550 =
        addConfig(
            "Switch5550",
            ConfigItem.configTypeBool,
            false
        )
    val switch5551 =
        addConfig(
            "Switch5551",
            ConfigItem.configTypeBool,
            false
        )
    val switch5552 =
        addConfig(
            "Switch5552",
            ConfigItem.configTypeBool,
            false
        )
    val switch5553 =
        addConfig(
            "Switch5553",
            ConfigItem.configTypeBool,
            false
        )
    val switch5554 =
        addConfig(
            "Switch5554",
            ConfigItem.configTypeBool,
            false
        )
    val switch5555 =
        addConfig(
            "Switch5555",
            ConfigItem.configTypeBool,
            false
        )
    val switch5556 =
        addConfig(
            "Switch5556",
            ConfigItem.configTypeBool,
            false
        )
    val switch5557 =
        addConfig(
            "Switch5557",
            ConfigItem.configTypeBool,
            false
        )
    val switch5558 =
        addConfig(
            "Switch5558",
            ConfigItem.configTypeBool,
            false
        )
    val switch5559 =
        addConfig(
            "Switch5559",
            ConfigItem.configTypeBool,
            false
        )
    val switch5560 =
        addConfig(
            "Switch5560",
            ConfigItem.configTypeBool,
            false
        )
    val switch5561 =
        addConfig(
            "Switch5561",
            ConfigItem.configTypeBool,
            false
        )
    val switch5562 =
        addConfig(
            "Switch5562",
            ConfigItem.configTypeBool,
            false
        )
    val switch5563 =
        addConfig(
            "Switch5563",
            ConfigItem.configTypeBool,
            false
        )
    val switch5564 =
        addConfig(
            "Switch5564",
            ConfigItem.configTypeBool,
            false
        )
    val switch5565 =
        addConfig(
            "Switch5565",
            ConfigItem.configTypeBool,
            false
        )
    val switch5566 =
        addConfig(
            "Switch5566",
            ConfigItem.configTypeBool,
            false
        )
    val switch5567 =
        addConfig(
            "Switch5567",
            ConfigItem.configTypeBool,
            false
        )
    val switch5568 =
        addConfig(
            "Switch5568",
            ConfigItem.configTypeBool,
            false
        )
    val switch5569 =
        addConfig(
            "Switch5569",
            ConfigItem.configTypeBool,
            false
        )
    val switch5570 =
        addConfig(
            "Switch5570",
            ConfigItem.configTypeBool,
            false
        )
    val switch5571 =
        addConfig(
            "Switch5571",
            ConfigItem.configTypeBool,
            false
        )
    val switch5572 =
        addConfig(
            "Switch5572",
            ConfigItem.configTypeBool,
            false
        )
    val switch5573 =
        addConfig(
            "Switch5573",
            ConfigItem.configTypeBool,
            false
        )
    val switch5574 =
        addConfig(
            "Switch5574",
            ConfigItem.configTypeBool,
            false
        )
    val switch5575 =
        addConfig(
            "Switch5575",
            ConfigItem.configTypeBool,
            false
        )
    val switch5576 =
        addConfig(
            "Switch5576",
            ConfigItem.configTypeBool,
            false
        )
    val switch5577 =
        addConfig(
            "Switch5577",
            ConfigItem.configTypeBool,
            false
        )
    val switch5578 =
        addConfig(
            "Switch5578",
            ConfigItem.configTypeBool,
            false
        )
    val switch5579 =
        addConfig(
            "Switch5579",
            ConfigItem.configTypeBool,
            false
        )
    val switch5580 =
        addConfig(
            "Switch5580",
            ConfigItem.configTypeBool,
            false
        )
    val switch5581 =
        addConfig(
            "Switch5581",
            ConfigItem.configTypeBool,
            false
        )
    val switch5582 =
        addConfig(
            "Switch5582",
            ConfigItem.configTypeBool,
            false
        )
    val switch5583 =
        addConfig(
            "Switch5583",
            ConfigItem.configTypeBool,
            false
        )
    val switch5584 =
        addConfig(
            "Switch5584",
            ConfigItem.configTypeBool,
            false
        )
    val switch5585 =
        addConfig(
            "Switch5585",
            ConfigItem.configTypeBool,
            false
        )
    val switch5586 =
        addConfig(
            "Switch5586",
            ConfigItem.configTypeBool,
            false
        )
    val switch5587 =
        addConfig(
            "Switch5587",
            ConfigItem.configTypeBool,
            false
        )
    val switch5588 =
        addConfig(
            "Switch5588",
            ConfigItem.configTypeBool,
            false
        )
    val switch5589 =
        addConfig(
            "Switch5589",
            ConfigItem.configTypeBool,
            false
        )
    val switch5590 =
        addConfig(
            "Switch5590",
            ConfigItem.configTypeBool,
            false
        )
    val switch5591 =
        addConfig(
            "Switch5591",
            ConfigItem.configTypeBool,
            false
        )
    val switch5592 =
        addConfig(
            "Switch5592",
            ConfigItem.configTypeBool,
            false
        )
    val switch5593 =
        addConfig(
            "Switch5593",
            ConfigItem.configTypeBool,
            false
        )
    val switch5594 =
        addConfig(
            "Switch5594",
            ConfigItem.configTypeBool,
            false
        )
    val switch5595 =
        addConfig(
            "Switch5595",
            ConfigItem.configTypeBool,
            false
        )
    val switch5596 =
        addConfig(
            "Switch5596",
            ConfigItem.configTypeBool,
            false
        )
    val switch5597 =
        addConfig(
            "Switch5597",
            ConfigItem.configTypeBool,
            false
        )
    val switch5598 =
        addConfig(
            "Switch5598",
            ConfigItem.configTypeBool,
            false
        )
    val switch5599 =
        addConfig(
            "Switch5599",
            ConfigItem.configTypeBool,
            false
        )
    val switch5600 =
        addConfig(
            "Switch5600",
            ConfigItem.configTypeBool,
            false
        )
    val switch5601 =
        addConfig(
            "Switch5601",
            ConfigItem.configTypeBool,
            false
        )
    val switch5602 =
        addConfig(
            "Switch5602",
            ConfigItem.configTypeBool,
            false
        )
    val switch5603 =
        addConfig(
            "Switch5603",
            ConfigItem.configTypeBool,
            false
        )
    val switch5604 =
        addConfig(
            "Switch5604",
            ConfigItem.configTypeBool,
            false
        )
    val switch5605 =
        addConfig(
            "Switch5605",
            ConfigItem.configTypeBool,
            false
        )
    val switch5606 =
        addConfig(
            "Switch5606",
            ConfigItem.configTypeBool,
            false
        )
    val switch5607 =
        addConfig(
            "Switch5607",
            ConfigItem.configTypeBool,
            false
        )
    val switch5608 =
        addConfig(
            "Switch5608",
            ConfigItem.configTypeBool,
            false
        )
    val switch5609 =
        addConfig(
            "Switch5609",
            ConfigItem.configTypeBool,
            false
        )
    val switch5610 =
        addConfig(
            "Switch5610",
            ConfigItem.configTypeBool,
            false
        )
    val switch5611 =
        addConfig(
            "Switch5611",
            ConfigItem.configTypeBool,
            false
        )
    val switch5612 =
        addConfig(
            "Switch5612",
            ConfigItem.configTypeBool,
            false
        )
    val switch5613 =
        addConfig(
            "Switch5613",
            ConfigItem.configTypeBool,
            false
        )
    val switch5614 =
        addConfig(
            "Switch5614",
            ConfigItem.configTypeBool,
            false
        )
    val switch5615 =
        addConfig(
            "Switch5615",
            ConfigItem.configTypeBool,
            false
        )
    val switch5616 =
        addConfig(
            "Switch5616",
            ConfigItem.configTypeBool,
            false
        )
    val switch5617 =
        addConfig(
            "Switch5617",
            ConfigItem.configTypeBool,
            false
        )
    val switch5618 =
        addConfig(
            "Switch5618",
            ConfigItem.configTypeBool,
            false
        )
    val switch5619 =
        addConfig(
            "Switch5619",
            ConfigItem.configTypeBool,
            false
        )
    val switch5620 =
        addConfig(
            "Switch5620",
            ConfigItem.configTypeBool,
            false
        )
    val switch5621 =
        addConfig(
            "Switch5621",
            ConfigItem.configTypeBool,
            false
        )
    val switch5622 =
        addConfig(
            "Switch5622",
            ConfigItem.configTypeBool,
            false
        )
    val switch5623 =
        addConfig(
            "Switch5623",
            ConfigItem.configTypeBool,
            false
        )
    val switch5624 =
        addConfig(
            "Switch5624",
            ConfigItem.configTypeBool,
            false
        )
    val switch5625 =
        addConfig(
            "Switch5625",
            ConfigItem.configTypeBool,
            false
        )
    val switch5626 =
        addConfig(
            "Switch5626",
            ConfigItem.configTypeBool,
            false
        )
    val switch5627 =
        addConfig(
            "Switch5627",
            ConfigItem.configTypeBool,
            false
        )
    val switch5628 =
        addConfig(
            "Switch5628",
            ConfigItem.configTypeBool,
            false
        )
    val switch5629 =
        addConfig(
            "Switch5629",
            ConfigItem.configTypeBool,
            false
        )
    val switch5630 =
        addConfig(
            "Switch5630",
            ConfigItem.configTypeBool,
            false
        )
    val switch5631 =
        addConfig(
            "Switch5631",
            ConfigItem.configTypeBool,
            false
        )
    val switch5632 =
        addConfig(
            "Switch5632",
            ConfigItem.configTypeBool,
            false
        )
    val switch5633 =
        addConfig(
            "Switch5633",
            ConfigItem.configTypeBool,
            false
        )
    val switch5634 =
        addConfig(
            "Switch5634",
            ConfigItem.configTypeBool,
            false
        )
    val switch5635 =
        addConfig(
            "Switch5635",
            ConfigItem.configTypeBool,
            false
        )
    val switch5636 =
        addConfig(
            "Switch5636",
            ConfigItem.configTypeBool,
            false
        )
    val switch5637 =
        addConfig(
            "Switch5637",
            ConfigItem.configTypeBool,
            false
        )
    val switch5638 =
        addConfig(
            "Switch5638",
            ConfigItem.configTypeBool,
            false
        )
    val switch5639 =
        addConfig(
            "Switch5639",
            ConfigItem.configTypeBool,
            false
        )
    val switch5640 =
        addConfig(
            "Switch5640",
            ConfigItem.configTypeBool,
            false
        )
    val switch5641 =
        addConfig(
            "Switch5641",
            ConfigItem.configTypeBool,
            false
        )
    val switch5642 =
        addConfig(
            "Switch5642",
            ConfigItem.configTypeBool,
            false
        )
    val switch5643 =
        addConfig(
            "Switch5643",
            ConfigItem.configTypeBool,
            false
        )
    val switch5644 =
        addConfig(
            "Switch5644",
            ConfigItem.configTypeBool,
            false
        )
    val switch5645 =
        addConfig(
            "Switch5645",
            ConfigItem.configTypeBool,
            false
        )
    val switch5646 =
        addConfig(
            "Switch5646",
            ConfigItem.configTypeBool,
            false
        )
    val switch5647 =
        addConfig(
            "Switch5647",
            ConfigItem.configTypeBool,
            false
        )
    val switch5648 =
        addConfig(
            "Switch5648",
            ConfigItem.configTypeBool,
            false
        )
    val switch5649 =
        addConfig(
            "Switch5649",
            ConfigItem.configTypeBool,
            false
        )
    val switch5650 =
        addConfig(
            "Switch5650",
            ConfigItem.configTypeBool,
            false
        )
    val switch5651 =
        addConfig(
            "Switch5651",
            ConfigItem.configTypeBool,
            false
        )
    val switch5652 =
        addConfig(
            "Switch5652",
            ConfigItem.configTypeBool,
            false
        )
    val switch5653 =
        addConfig(
            "Switch5653",
            ConfigItem.configTypeBool,
            false
        )
    val switch5654 =
        addConfig(
            "Switch5654",
            ConfigItem.configTypeBool,
            false
        )
    val switch5655 =
        addConfig(
            "Switch5655",
            ConfigItem.configTypeBool,
            false
        )
    val switch5656 =
        addConfig(
            "Switch5656",
            ConfigItem.configTypeBool,
            false
        )
    val switch5657 =
        addConfig(
            "Switch5657",
            ConfigItem.configTypeBool,
            false
        )
    val switch5658 =
        addConfig(
            "Switch5658",
            ConfigItem.configTypeBool,
            false
        )
    val switch5659 =
        addConfig(
            "Switch5659",
            ConfigItem.configTypeBool,
            false
        )
    val switch5660 =
        addConfig(
            "Switch5660",
            ConfigItem.configTypeBool,
            false
        )
    val switch5661 =
        addConfig(
            "Switch5661",
            ConfigItem.configTypeBool,
            false
        )
    val switch5662 =
        addConfig(
            "Switch5662",
            ConfigItem.configTypeBool,
            false
        )
    val switch5663 =
        addConfig(
            "Switch5663",
            ConfigItem.configTypeBool,
            false
        )
    val switch5664 =
        addConfig(
            "Switch5664",
            ConfigItem.configTypeBool,
            false
        )
    val switch5665 =
        addConfig(
            "Switch5665",
            ConfigItem.configTypeBool,
            false
        )
    val switch5666 =
        addConfig(
            "Switch5666",
            ConfigItem.configTypeBool,
            false
        )
    val switch5667 =
        addConfig(
            "Switch5667",
            ConfigItem.configTypeBool,
            false
        )
    val switch5668 =
        addConfig(
            "Switch5668",
            ConfigItem.configTypeBool,
            false
        )
    val switch5669 =
        addConfig(
            "Switch5669",
            ConfigItem.configTypeBool,
            false
        )
    val switch5670 =
        addConfig(
            "Switch5670",
            ConfigItem.configTypeBool,
            false
        )
    val switch5671 =
        addConfig(
            "Switch5671",
            ConfigItem.configTypeBool,
            false
        )
    val switch5672 =
        addConfig(
            "Switch5672",
            ConfigItem.configTypeBool,
            false
        )
    val switch5673 =
        addConfig(
            "Switch5673",
            ConfigItem.configTypeBool,
            false
        )
    val switch5674 =
        addConfig(
            "Switch5674",
            ConfigItem.configTypeBool,
            false
        )
    val switch5675 =
        addConfig(
            "Switch5675",
            ConfigItem.configTypeBool,
            false
        )
    val switch5676 =
        addConfig(
            "Switch5676",
            ConfigItem.configTypeBool,
            false
        )
    val switch5677 =
        addConfig(
            "Switch5677",
            ConfigItem.configTypeBool,
            false
        )
    val switch5678 =
        addConfig(
            "Switch5678",
            ConfigItem.configTypeBool,
            false
        )
    val switch5679 =
        addConfig(
            "Switch5679",
            ConfigItem.configTypeBool,
            false
        )
    val switch5680 =
        addConfig(
            "Switch5680",
            ConfigItem.configTypeBool,
            false
        )
    val switch5681 =
        addConfig(
            "Switch5681",
            ConfigItem.configTypeBool,
            false
        )
    val switch5682 =
        addConfig(
            "Switch5682",
            ConfigItem.configTypeBool,
            false
        )
    val switch5683 =
        addConfig(
            "Switch5683",
            ConfigItem.configTypeBool,
            false
        )
    val switch5684 =
        addConfig(
            "Switch5684",
            ConfigItem.configTypeBool,
            false
        )
    val switch5685 =
        addConfig(
            "Switch5685",
            ConfigItem.configTypeBool,
            false
        )
    val switch5686 =
        addConfig(
            "Switch5686",
            ConfigItem.configTypeBool,
            false
        )
    val switch5687 =
        addConfig(
            "Switch5687",
            ConfigItem.configTypeBool,
            false
        )
    val switch5688 =
        addConfig(
            "Switch5688",
            ConfigItem.configTypeBool,
            false
        )
    val switch5689 =
        addConfig(
            "Switch5689",
            ConfigItem.configTypeBool,
            false
        )
    val switch5690 =
        addConfig(
            "Switch5690",
            ConfigItem.configTypeBool,
            false
        )
    val switch5691 =
        addConfig(
            "Switch5691",
            ConfigItem.configTypeBool,
            false
        )
    val switch5692 =
        addConfig(
            "Switch5692",
            ConfigItem.configTypeBool,
            false
        )
    val switch5693 =
        addConfig(
            "Switch5693",
            ConfigItem.configTypeBool,
            false
        )
    val switch5694 =
        addConfig(
            "Switch5694",
            ConfigItem.configTypeBool,
            false
        )
    val switch5695 =
        addConfig(
            "Switch5695",
            ConfigItem.configTypeBool,
            false
        )
    val switch5696 =
        addConfig(
            "Switch5696",
            ConfigItem.configTypeBool,
            false
        )
    val switch5697 =
        addConfig(
            "Switch5697",
            ConfigItem.configTypeBool,
            false
        )
    val switch5698 =
        addConfig(
            "Switch5698",
            ConfigItem.configTypeBool,
            false
        )
    val switch5699 =
        addConfig(
            "Switch5699",
            ConfigItem.configTypeBool,
            false
        )
    val switch5700 =
        addConfig(
            "Switch5700",
            ConfigItem.configTypeBool,
            false
        )
    val switch5701 =
        addConfig(
            "Switch5701",
            ConfigItem.configTypeBool,
            false
        )
    val switch5702 =
        addConfig(
            "Switch5702",
            ConfigItem.configTypeBool,
            false
        )
    val switch5703 =
        addConfig(
            "Switch5703",
            ConfigItem.configTypeBool,
            false
        )
    val switch5704 =
        addConfig(
            "Switch5704",
            ConfigItem.configTypeBool,
            false
        )
    val switch5705 =
        addConfig(
            "Switch5705",
            ConfigItem.configTypeBool,
            false
        )
    val switch5706 =
        addConfig(
            "Switch5706",
            ConfigItem.configTypeBool,
            false
        )
    val switch5707 =
        addConfig(
            "Switch5707",
            ConfigItem.configTypeBool,
            false
        )
    val switch5708 =
        addConfig(
            "Switch5708",
            ConfigItem.configTypeBool,
            false
        )
    val switch5709 =
        addConfig(
            "Switch5709",
            ConfigItem.configTypeBool,
            false
        )
    val switch5710 =
        addConfig(
            "Switch5710",
            ConfigItem.configTypeBool,
            false
        )
    val switch5711 =
        addConfig(
            "Switch5711",
            ConfigItem.configTypeBool,
            false
        )
    val switch5712 =
        addConfig(
            "Switch5712",
            ConfigItem.configTypeBool,
            false
        )
    val switch5713 =
        addConfig(
            "Switch5713",
            ConfigItem.configTypeBool,
            false
        )
    val switch5714 =
        addConfig(
            "Switch5714",
            ConfigItem.configTypeBool,
            false
        )
    val switch5715 =
        addConfig(
            "Switch5715",
            ConfigItem.configTypeBool,
            false
        )
    val switch5716 =
        addConfig(
            "Switch5716",
            ConfigItem.configTypeBool,
            false
        )
    val switch5717 =
        addConfig(
            "Switch5717",
            ConfigItem.configTypeBool,
            false
        )
    val switch5718 =
        addConfig(
            "Switch5718",
            ConfigItem.configTypeBool,
            false
        )
    val switch5719 =
        addConfig(
            "Switch5719",
            ConfigItem.configTypeBool,
            false
        )
    val switch5720 =
        addConfig(
            "Switch5720",
            ConfigItem.configTypeBool,
            false
        )
    val switch5721 =
        addConfig(
            "Switch5721",
            ConfigItem.configTypeBool,
            false
        )
    val switch5722 =
        addConfig(
            "Switch5722",
            ConfigItem.configTypeBool,
            false
        )
    val switch5723 =
        addConfig(
            "Switch5723",
            ConfigItem.configTypeBool,
            false
        )
    val switch5724 =
        addConfig(
            "Switch5724",
            ConfigItem.configTypeBool,
            false
        )
    val switch5725 =
        addConfig(
            "Switch5725",
            ConfigItem.configTypeBool,
            false
        )
    val switch5726 =
        addConfig(
            "Switch5726",
            ConfigItem.configTypeBool,
            false
        )
    val switch5727 =
        addConfig(
            "Switch5727",
            ConfigItem.configTypeBool,
            false
        )
    val switch5728 =
        addConfig(
            "Switch5728",
            ConfigItem.configTypeBool,
            false
        )
    val switch5729 =
        addConfig(
            "Switch5729",
            ConfigItem.configTypeBool,
            false
        )
    val switch5730 =
        addConfig(
            "Switch5730",
            ConfigItem.configTypeBool,
            false
        )
    val switch5731 =
        addConfig(
            "Switch5731",
            ConfigItem.configTypeBool,
            false
        )
    val switch5732 =
        addConfig(
            "Switch5732",
            ConfigItem.configTypeBool,
            false
        )
    val switch5733 =
        addConfig(
            "Switch5733",
            ConfigItem.configTypeBool,
            false
        )
    val switch5734 =
        addConfig(
            "Switch5734",
            ConfigItem.configTypeBool,
            false
        )
    val switch5735 =
        addConfig(
            "Switch5735",
            ConfigItem.configTypeBool,
            false
        )
    val switch5736 =
        addConfig(
            "Switch5736",
            ConfigItem.configTypeBool,
            false
        )
    val switch5737 =
        addConfig(
            "Switch5737",
            ConfigItem.configTypeBool,
            false
        )
    val switch5738 =
        addConfig(
            "Switch5738",
            ConfigItem.configTypeBool,
            false
        )
    val switch5739 =
        addConfig(
            "Switch5739",
            ConfigItem.configTypeBool,
            false
        )
    val switch5740 =
        addConfig(
            "Switch5740",
            ConfigItem.configTypeBool,
            false
        )
    val switch5741 =
        addConfig(
            "Switch5741",
            ConfigItem.configTypeBool,
            false
        )
    val switch5742 =
        addConfig(
            "Switch5742",
            ConfigItem.configTypeBool,
            false
        )
    val switch5743 =
        addConfig(
            "Switch5743",
            ConfigItem.configTypeBool,
            false
        )
    val switch5744 =
        addConfig(
            "Switch5744",
            ConfigItem.configTypeBool,
            false
        )
    val switch5745 =
        addConfig(
            "Switch5745",
            ConfigItem.configTypeBool,
            false
        )
    val switch5746 =
        addConfig(
            "Switch5746",
            ConfigItem.configTypeBool,
            false
        )
    val switch5747 =
        addConfig(
            "Switch5747",
            ConfigItem.configTypeBool,
            false
        )
    val switch5748 =
        addConfig(
            "Switch5748",
            ConfigItem.configTypeBool,
            false
        )
    val switch5749 =
        addConfig(
            "Switch5749",
            ConfigItem.configTypeBool,
            false
        )
    val switch5750 =
        addConfig(
            "Switch5750",
            ConfigItem.configTypeBool,
            false
        )
    val switch5751 =
        addConfig(
            "Switch5751",
            ConfigItem.configTypeBool,
            false
        )
    val switch5752 =
        addConfig(
            "Switch5752",
            ConfigItem.configTypeBool,
            false
        )
    val switch5753 =
        addConfig(
            "Switch5753",
            ConfigItem.configTypeBool,
            false
        )
    val switch5754 =
        addConfig(
            "Switch5754",
            ConfigItem.configTypeBool,
            false
        )
    val switch5755 =
        addConfig(
            "Switch5755",
            ConfigItem.configTypeBool,
            false
        )
    val switch5756 =
        addConfig(
            "Switch5756",
            ConfigItem.configTypeBool,
            false
        )
    val switch5757 =
        addConfig(
            "Switch5757",
            ConfigItem.configTypeBool,
            false
        )
    val switch5758 =
        addConfig(
            "Switch5758",
            ConfigItem.configTypeBool,
            false
        )
    val switch5759 =
        addConfig(
            "Switch5759",
            ConfigItem.configTypeBool,
            false
        )
    val switch5760 =
        addConfig(
            "Switch5760",
            ConfigItem.configTypeBool,
            false
        )
    val switch5761 =
        addConfig(
            "Switch5761",
            ConfigItem.configTypeBool,
            false
        )
    val switch5762 =
        addConfig(
            "Switch5762",
            ConfigItem.configTypeBool,
            false
        )
    val switch5763 =
        addConfig(
            "Switch5763",
            ConfigItem.configTypeBool,
            false
        )
    val switch5764 =
        addConfig(
            "Switch5764",
            ConfigItem.configTypeBool,
            false
        )
    val switch5765 =
        addConfig(
            "Switch5765",
            ConfigItem.configTypeBool,
            false
        )
    val switch5766 =
        addConfig(
            "Switch5766",
            ConfigItem.configTypeBool,
            false
        )
    val switch5767 =
        addConfig(
            "Switch5767",
            ConfigItem.configTypeBool,
            false
        )
    val switch5768 =
        addConfig(
            "Switch5768",
            ConfigItem.configTypeBool,
            false
        )
    val switch5769 =
        addConfig(
            "Switch5769",
            ConfigItem.configTypeBool,
            false
        )
    val switch5770 =
        addConfig(
            "Switch5770",
            ConfigItem.configTypeBool,
            false
        )
    val switch5771 =
        addConfig(
            "Switch5771",
            ConfigItem.configTypeBool,
            false
        )
    val switch5772 =
        addConfig(
            "Switch5772",
            ConfigItem.configTypeBool,
            false
        )
    val switch5773 =
        addConfig(
            "Switch5773",
            ConfigItem.configTypeBool,
            false
        )
    val switch5774 =
        addConfig(
            "Switch5774",
            ConfigItem.configTypeBool,
            false
        )
    val switch5775 =
        addConfig(
            "Switch5775",
            ConfigItem.configTypeBool,
            false
        )
    val switch5776 =
        addConfig(
            "Switch5776",
            ConfigItem.configTypeBool,
            false
        )
    val switch5777 =
        addConfig(
            "Switch5777",
            ConfigItem.configTypeBool,
            false
        )
    val switch5778 =
        addConfig(
            "Switch5778",
            ConfigItem.configTypeBool,
            false
        )
    val switch5779 =
        addConfig(
            "Switch5779",
            ConfigItem.configTypeBool,
            false
        )
    val switch5780 =
        addConfig(
            "Switch5780",
            ConfigItem.configTypeBool,
            false
        )
    val switch5781 =
        addConfig(
            "Switch5781",
            ConfigItem.configTypeBool,
            false
        )
    val switch5782 =
        addConfig(
            "Switch5782",
            ConfigItem.configTypeBool,
            false
        )
    val switch5783 =
        addConfig(
            "Switch5783",
            ConfigItem.configTypeBool,
            false
        )
    val switch5784 =
        addConfig(
            "Switch5784",
            ConfigItem.configTypeBool,
            false
        )
    val switch5785 =
        addConfig(
            "Switch5785",
            ConfigItem.configTypeBool,
            false
        )
    val switch5786 =
        addConfig(
            "Switch5786",
            ConfigItem.configTypeBool,
            false
        )
    val switch5787 =
        addConfig(
            "Switch5787",
            ConfigItem.configTypeBool,
            false
        )
    val switch5788 =
        addConfig(
            "Switch5788",
            ConfigItem.configTypeBool,
            false
        )
    val switch5789 =
        addConfig(
            "Switch5789",
            ConfigItem.configTypeBool,
            false
        )
    val switch5790 =
        addConfig(
            "Switch5790",
            ConfigItem.configTypeBool,
            false
        )
    val switch5791 =
        addConfig(
            "Switch5791",
            ConfigItem.configTypeBool,
            false
        )
    val switch5792 =
        addConfig(
            "Switch5792",
            ConfigItem.configTypeBool,
            false
        )
    val switch5793 =
        addConfig(
            "Switch5793",
            ConfigItem.configTypeBool,
            false
        )
    val switch5794 =
        addConfig(
            "Switch5794",
            ConfigItem.configTypeBool,
            false
        )
    val switch5795 =
        addConfig(
            "Switch5795",
            ConfigItem.configTypeBool,
            false
        )
    val switch5796 =
        addConfig(
            "Switch5796",
            ConfigItem.configTypeBool,
            false
        )
    val switch5797 =
        addConfig(
            "Switch5797",
            ConfigItem.configTypeBool,
            false
        )
    val switch5798 =
        addConfig(
            "Switch5798",
            ConfigItem.configTypeBool,
            false
        )
    val switch5799 =
        addConfig(
            "Switch5799",
            ConfigItem.configTypeBool,
            false
        )
    val switch5800 =
        addConfig(
            "Switch5800",
            ConfigItem.configTypeBool,
            false
        )
    val switch5801 =
        addConfig(
            "Switch5801",
            ConfigItem.configTypeBool,
            false
        )
    val switch5802 =
        addConfig(
            "Switch5802",
            ConfigItem.configTypeBool,
            false
        )
    val switch5803 =
        addConfig(
            "Switch5803",
            ConfigItem.configTypeBool,
            false
        )
    val switch5804 =
        addConfig(
            "Switch5804",
            ConfigItem.configTypeBool,
            false
        )
    val switch5805 =
        addConfig(
            "Switch5805",
            ConfigItem.configTypeBool,
            false
        )
    val switch5806 =
        addConfig(
            "Switch5806",
            ConfigItem.configTypeBool,
            false
        )
    val switch5807 =
        addConfig(
            "Switch5807",
            ConfigItem.configTypeBool,
            false
        )
    val switch5808 =
        addConfig(
            "Switch5808",
            ConfigItem.configTypeBool,
            false
        )
    val switch5809 =
        addConfig(
            "Switch5809",
            ConfigItem.configTypeBool,
            false
        )
    val switch5810 =
        addConfig(
            "Switch5810",
            ConfigItem.configTypeBool,
            false
        )
    val switch5811 =
        addConfig(
            "Switch5811",
            ConfigItem.configTypeBool,
            false
        )
    val switch5812 =
        addConfig(
            "Switch5812",
            ConfigItem.configTypeBool,
            false
        )
    val switch5813 =
        addConfig(
            "Switch5813",
            ConfigItem.configTypeBool,
            false
        )
    val switch5814 =
        addConfig(
            "Switch5814",
            ConfigItem.configTypeBool,
            false
        )
    val switch5815 =
        addConfig(
            "Switch5815",
            ConfigItem.configTypeBool,
            false
        )
    val switch5816 =
        addConfig(
            "Switch5816",
            ConfigItem.configTypeBool,
            false
        )
    val switch5817 =
        addConfig(
            "Switch5817",
            ConfigItem.configTypeBool,
            false
        )
    val switch5818 =
        addConfig(
            "Switch5818",
            ConfigItem.configTypeBool,
            false
        )
    val switch5819 =
        addConfig(
            "Switch5819",
            ConfigItem.configTypeBool,
            false
        )
    val switch5820 =
        addConfig(
            "Switch5820",
            ConfigItem.configTypeBool,
            false
        )
    val switch5821 =
        addConfig(
            "Switch5821",
            ConfigItem.configTypeBool,
            false
        )
    val switch5822 =
        addConfig(
            "Switch5822",
            ConfigItem.configTypeBool,
            false
        )
    val switch5823 =
        addConfig(
            "Switch5823",
            ConfigItem.configTypeBool,
            false
        )
    val switch5824 =
        addConfig(
            "Switch5824",
            ConfigItem.configTypeBool,
            false
        )
    val switch5825 =
        addConfig(
            "Switch5825",
            ConfigItem.configTypeBool,
            false
        )
    val switch5826 =
        addConfig(
            "Switch5826",
            ConfigItem.configTypeBool,
            false
        )
    val switch5827 =
        addConfig(
            "Switch5827",
            ConfigItem.configTypeBool,
            false
        )
    val switch5828 =
        addConfig(
            "Switch5828",
            ConfigItem.configTypeBool,
            false
        )
    val switch5829 =
        addConfig(
            "Switch5829",
            ConfigItem.configTypeBool,
            false
        )
    val switch5830 =
        addConfig(
            "Switch5830",
            ConfigItem.configTypeBool,
            false
        )
    val switch5831 =
        addConfig(
            "Switch5831",
            ConfigItem.configTypeBool,
            false
        )
    val switch5832 =
        addConfig(
            "Switch5832",
            ConfigItem.configTypeBool,
            false
        )
    val switch5833 =
        addConfig(
            "Switch5833",
            ConfigItem.configTypeBool,
            false
        )
    val switch5834 =
        addConfig(
            "Switch5834",
            ConfigItem.configTypeBool,
            false
        )
    val switch5835 =
        addConfig(
            "Switch5835",
            ConfigItem.configTypeBool,
            false
        )
    val switch5836 =
        addConfig(
            "Switch5836",
            ConfigItem.configTypeBool,
            false
        )
    val switch5837 =
        addConfig(
            "Switch5837",
            ConfigItem.configTypeBool,
            false
        )
    val switch5838 =
        addConfig(
            "Switch5838",
            ConfigItem.configTypeBool,
            false
        )
    val switch5839 =
        addConfig(
            "Switch5839",
            ConfigItem.configTypeBool,
            false
        )
    val switch5840 =
        addConfig(
            "Switch5840",
            ConfigItem.configTypeBool,
            false
        )
    val switch5841 =
        addConfig(
            "Switch5841",
            ConfigItem.configTypeBool,
            false
        )
    val switch5842 =
        addConfig(
            "Switch5842",
            ConfigItem.configTypeBool,
            false
        )
    val switch5843 =
        addConfig(
            "Switch5843",
            ConfigItem.configTypeBool,
            false
        )
    val switch5844 =
        addConfig(
            "Switch5844",
            ConfigItem.configTypeBool,
            false
        )
    val switch5845 =
        addConfig(
            "Switch5845",
            ConfigItem.configTypeBool,
            false
        )
    val switch5846 =
        addConfig(
            "Switch5846",
            ConfigItem.configTypeBool,
            false
        )
    val switch5847 =
        addConfig(
            "Switch5847",
            ConfigItem.configTypeBool,
            false
        )
    val switch5848 =
        addConfig(
            "Switch5848",
            ConfigItem.configTypeBool,
            false
        )
    val switch5849 =
        addConfig(
            "Switch5849",
            ConfigItem.configTypeBool,
            false
        )
    val switch5850 =
        addConfig(
            "Switch5850",
            ConfigItem.configTypeBool,
            false
        )
    val switch5851 =
        addConfig(
            "Switch5851",
            ConfigItem.configTypeBool,
            false
        )
    val switch5852 =
        addConfig(
            "Switch5852",
            ConfigItem.configTypeBool,
            false
        )
    val switch5853 =
        addConfig(
            "Switch5853",
            ConfigItem.configTypeBool,
            false
        )
    val switch5854 =
        addConfig(
            "Switch5854",
            ConfigItem.configTypeBool,
            false
        )
    val switch5855 =
        addConfig(
            "Switch5855",
            ConfigItem.configTypeBool,
            false
        )
    val switch5856 =
        addConfig(
            "Switch5856",
            ConfigItem.configTypeBool,
            false
        )
    val switch5857 =
        addConfig(
            "Switch5857",
            ConfigItem.configTypeBool,
            false
        )
    val switch5858 =
        addConfig(
            "Switch5858",
            ConfigItem.configTypeBool,
            false
        )
    val switch5859 =
        addConfig(
            "Switch5859",
            ConfigItem.configTypeBool,
            false
        )
    val switch5860 =
        addConfig(
            "Switch5860",
            ConfigItem.configTypeBool,
            false
        )
    val switch5861 =
        addConfig(
            "Switch5861",
            ConfigItem.configTypeBool,
            false
        )
    val switch5862 =
        addConfig(
            "Switch5862",
            ConfigItem.configTypeBool,
            false
        )
    val switch5863 =
        addConfig(
            "Switch5863",
            ConfigItem.configTypeBool,
            false
        )
    val switch5864 =
        addConfig(
            "Switch5864",
            ConfigItem.configTypeBool,
            false
        )
    val switch5865 =
        addConfig(
            "Switch5865",
            ConfigItem.configTypeBool,
            false
        )
    val switch5866 =
        addConfig(
            "Switch5866",
            ConfigItem.configTypeBool,
            false
        )
    val switch5867 =
        addConfig(
            "Switch5867",
            ConfigItem.configTypeBool,
            false
        )
    val switch5868 =
        addConfig(
            "Switch5868",
            ConfigItem.configTypeBool,
            false
        )
    val switch5869 =
        addConfig(
            "Switch5869",
            ConfigItem.configTypeBool,
            false
        )
    val switch5870 =
        addConfig(
            "Switch5870",
            ConfigItem.configTypeBool,
            false
        )
    val switch5871 =
        addConfig(
            "Switch5871",
            ConfigItem.configTypeBool,
            false
        )
    val switch5872 =
        addConfig(
            "Switch5872",
            ConfigItem.configTypeBool,
            false
        )
    val switch5873 =
        addConfig(
            "Switch5873",
            ConfigItem.configTypeBool,
            false
        )
    val switch5874 =
        addConfig(
            "Switch5874",
            ConfigItem.configTypeBool,
            false
        )
    val switch5875 =
        addConfig(
            "Switch5875",
            ConfigItem.configTypeBool,
            false
        )
    val switch5876 =
        addConfig(
            "Switch5876",
            ConfigItem.configTypeBool,
            false
        )
    val switch5877 =
        addConfig(
            "Switch5877",
            ConfigItem.configTypeBool,
            false
        )
    val switch5878 =
        addConfig(
            "Switch5878",
            ConfigItem.configTypeBool,
            false
        )
    val switch5879 =
        addConfig(
            "Switch5879",
            ConfigItem.configTypeBool,
            false
        )
    val switch5880 =
        addConfig(
            "Switch5880",
            ConfigItem.configTypeBool,
            false
        )
    val switch5881 =
        addConfig(
            "Switch5881",
            ConfigItem.configTypeBool,
            false
        )
    val switch5882 =
        addConfig(
            "Switch5882",
            ConfigItem.configTypeBool,
            false
        )
    val switch5883 =
        addConfig(
            "Switch5883",
            ConfigItem.configTypeBool,
            false
        )
    val switch5884 =
        addConfig(
            "Switch5884",
            ConfigItem.configTypeBool,
            false
        )
    val switch5885 =
        addConfig(
            "Switch5885",
            ConfigItem.configTypeBool,
            false
        )
    val switch5886 =
        addConfig(
            "Switch5886",
            ConfigItem.configTypeBool,
            false
        )
    val switch5887 =
        addConfig(
            "Switch5887",
            ConfigItem.configTypeBool,
            false
        )
    val switch5888 =
        addConfig(
            "Switch5888",
            ConfigItem.configTypeBool,
            false
        )
    val switch5889 =
        addConfig(
            "Switch5889",
            ConfigItem.configTypeBool,
            false
        )
    val switch5890 =
        addConfig(
            "Switch5890",
            ConfigItem.configTypeBool,
            false
        )
    val switch5891 =
        addConfig(
            "Switch5891",
            ConfigItem.configTypeBool,
            false
        )
    val switch5892 =
        addConfig(
            "Switch5892",
            ConfigItem.configTypeBool,
            false
        )
    val switch5893 =
        addConfig(
            "Switch5893",
            ConfigItem.configTypeBool,
            false
        )
    val switch5894 =
        addConfig(
            "Switch5894",
            ConfigItem.configTypeBool,
            false
        )
    val switch5895 =
        addConfig(
            "Switch5895",
            ConfigItem.configTypeBool,
            false
        )
    val switch5896 =
        addConfig(
            "Switch5896",
            ConfigItem.configTypeBool,
            false
        )
    val switch5897 =
        addConfig(
            "Switch5897",
            ConfigItem.configTypeBool,
            false
        )
    val switch5898 =
        addConfig(
            "Switch5898",
            ConfigItem.configTypeBool,
            false
        )
    val switch5899 =
        addConfig(
            "Switch5899",
            ConfigItem.configTypeBool,
            false
        )
    val switch5900 =
        addConfig(
            "Switch5900",
            ConfigItem.configTypeBool,
            false
        )
    val switch5901 =
        addConfig(
            "Switch5901",
            ConfigItem.configTypeBool,
            false
        )
    val switch5902 =
        addConfig(
            "Switch5902",
            ConfigItem.configTypeBool,
            false
        )
    val switch5903 =
        addConfig(
            "Switch5903",
            ConfigItem.configTypeBool,
            false
        )
    val switch5904 =
        addConfig(
            "Switch5904",
            ConfigItem.configTypeBool,
            false
        )
    val switch5905 =
        addConfig(
            "Switch5905",
            ConfigItem.configTypeBool,
            false
        )
    val switch5906 =
        addConfig(
            "Switch5906",
            ConfigItem.configTypeBool,
            false
        )
    val switch5907 =
        addConfig(
            "Switch5907",
            ConfigItem.configTypeBool,
            false
        )
    val switch5908 =
        addConfig(
            "Switch5908",
            ConfigItem.configTypeBool,
            false
        )
    val switch5909 =
        addConfig(
            "Switch5909",
            ConfigItem.configTypeBool,
            false
        )
    val switch5910 =
        addConfig(
            "Switch5910",
            ConfigItem.configTypeBool,
            false
        )
    val switch5911 =
        addConfig(
            "Switch5911",
            ConfigItem.configTypeBool,
            false
        )
    val switch5912 =
        addConfig(
            "Switch5912",
            ConfigItem.configTypeBool,
            false
        )
    val switch5913 =
        addConfig(
            "Switch5913",
            ConfigItem.configTypeBool,
            false
        )
    val switch5914 =
        addConfig(
            "Switch5914",
            ConfigItem.configTypeBool,
            false
        )
    val switch5915 =
        addConfig(
            "Switch5915",
            ConfigItem.configTypeBool,
            false
        )
    val switch5916 =
        addConfig(
            "Switch5916",
            ConfigItem.configTypeBool,
            false
        )
    val switch5917 =
        addConfig(
            "Switch5917",
            ConfigItem.configTypeBool,
            false
        )
    val switch5918 =
        addConfig(
            "Switch5918",
            ConfigItem.configTypeBool,
            false
        )
    val switch5919 =
        addConfig(
            "Switch5919",
            ConfigItem.configTypeBool,
            false
        )
    val switch5920 =
        addConfig(
            "Switch5920",
            ConfigItem.configTypeBool,
            false
        )
    val switch5921 =
        addConfig(
            "Switch5921",
            ConfigItem.configTypeBool,
            false
        )
    val switch5922 =
        addConfig(
            "Switch5922",
            ConfigItem.configTypeBool,
            false
        )
    val switch5923 =
        addConfig(
            "Switch5923",
            ConfigItem.configTypeBool,
            false
        )
    val switch5924 =
        addConfig(
            "Switch5924",
            ConfigItem.configTypeBool,
            false
        )
    val switch5925 =
        addConfig(
            "Switch5925",
            ConfigItem.configTypeBool,
            false
        )
    val switch5926 =
        addConfig(
            "Switch5926",
            ConfigItem.configTypeBool,
            false
        )
    val switch5927 =
        addConfig(
            "Switch5927",
            ConfigItem.configTypeBool,
            false
        )
    val switch5928 =
        addConfig(
            "Switch5928",
            ConfigItem.configTypeBool,
            false
        )
    val switch5929 =
        addConfig(
            "Switch5929",
            ConfigItem.configTypeBool,
            false
        )
    val switch5930 =
        addConfig(
            "Switch5930",
            ConfigItem.configTypeBool,
            false
        )
    val switch5931 =
        addConfig(
            "Switch5931",
            ConfigItem.configTypeBool,
            false
        )
    val switch5932 =
        addConfig(
            "Switch5932",
            ConfigItem.configTypeBool,
            false
        )
    val switch5933 =
        addConfig(
            "Switch5933",
            ConfigItem.configTypeBool,
            false
        )
    val switch5934 =
        addConfig(
            "Switch5934",
            ConfigItem.configTypeBool,
            false
        )
    val switch5935 =
        addConfig(
            "Switch5935",
            ConfigItem.configTypeBool,
            false
        )
    val switch5936 =
        addConfig(
            "Switch5936",
            ConfigItem.configTypeBool,
            false
        )
    val switch5937 =
        addConfig(
            "Switch5937",
            ConfigItem.configTypeBool,
            false
        )
    val switch5938 =
        addConfig(
            "Switch5938",
            ConfigItem.configTypeBool,
            false
        )
    val switch5939 =
        addConfig(
            "Switch5939",
            ConfigItem.configTypeBool,
            false
        )
    val switch5940 =
        addConfig(
            "Switch5940",
            ConfigItem.configTypeBool,
            false
        )
    val switch5941 =
        addConfig(
            "Switch5941",
            ConfigItem.configTypeBool,
            false
        )
    val switch5942 =
        addConfig(
            "Switch5942",
            ConfigItem.configTypeBool,
            false
        )
    val switch5943 =
        addConfig(
            "Switch5943",
            ConfigItem.configTypeBool,
            false
        )
    val switch5944 =
        addConfig(
            "Switch5944",
            ConfigItem.configTypeBool,
            false
        )
    val switch5945 =
        addConfig(
            "Switch5945",
            ConfigItem.configTypeBool,
            false
        )
    val switch5946 =
        addConfig(
            "Switch5946",
            ConfigItem.configTypeBool,
            false
        )
    val switch5947 =
        addConfig(
            "Switch5947",
            ConfigItem.configTypeBool,
            false
        )
    val switch5948 =
        addConfig(
            "Switch5948",
            ConfigItem.configTypeBool,
            false
        )
    val switch5949 =
        addConfig(
            "Switch5949",
            ConfigItem.configTypeBool,
            false
        )
    val switch5950 =
        addConfig(
            "Switch5950",
            ConfigItem.configTypeBool,
            false
        )
    val switch5951 =
        addConfig(
            "Switch5951",
            ConfigItem.configTypeBool,
            false
        )
    val switch5952 =
        addConfig(
            "Switch5952",
            ConfigItem.configTypeBool,
            false
        )
    val switch5953 =
        addConfig(
            "Switch5953",
            ConfigItem.configTypeBool,
            false
        )
    val switch5954 =
        addConfig(
            "Switch5954",
            ConfigItem.configTypeBool,
            false
        )
    val switch5955 =
        addConfig(
            "Switch5955",
            ConfigItem.configTypeBool,
            false
        )
    val switch5956 =
        addConfig(
            "Switch5956",
            ConfigItem.configTypeBool,
            false
        )
    val switch5957 =
        addConfig(
            "Switch5957",
            ConfigItem.configTypeBool,
            false
        )
    val switch5958 =
        addConfig(
            "Switch5958",
            ConfigItem.configTypeBool,
            false
        )
    val switch5959 =
        addConfig(
            "Switch5959",
            ConfigItem.configTypeBool,
            false
        )
    val switch5960 =
        addConfig(
            "Switch5960",
            ConfigItem.configTypeBool,
            false
        )
    val switch5961 =
        addConfig(
            "Switch5961",
            ConfigItem.configTypeBool,
            false
        )
    val switch5962 =
        addConfig(
            "Switch5962",
            ConfigItem.configTypeBool,
            false
        )
    val switch5963 =
        addConfig(
            "Switch5963",
            ConfigItem.configTypeBool,
            false
        )
    val switch5964 =
        addConfig(
            "Switch5964",
            ConfigItem.configTypeBool,
            false
        )
    val switch5965 =
        addConfig(
            "Switch5965",
            ConfigItem.configTypeBool,
            false
        )
    val switch5966 =
        addConfig(
            "Switch5966",
            ConfigItem.configTypeBool,
            false
        )
    val switch5967 =
        addConfig(
            "Switch5967",
            ConfigItem.configTypeBool,
            false
        )
    val switch5968 =
        addConfig(
            "Switch5968",
            ConfigItem.configTypeBool,
            false
        )
    val switch5969 =
        addConfig(
            "Switch5969",
            ConfigItem.configTypeBool,
            false
        )
    val switch5970 =
        addConfig(
            "Switch5970",
            ConfigItem.configTypeBool,
            false
        )
    val switch5971 =
        addConfig(
            "Switch5971",
            ConfigItem.configTypeBool,
            false
        )
    val switch5972 =
        addConfig(
            "Switch5972",
            ConfigItem.configTypeBool,
            false
        )
    val switch5973 =
        addConfig(
            "Switch5973",
            ConfigItem.configTypeBool,
            false
        )
    val switch5974 =
        addConfig(
            "Switch5974",
            ConfigItem.configTypeBool,
            false
        )
    val switch5975 =
        addConfig(
            "Switch5975",
            ConfigItem.configTypeBool,
            false
        )
    val switch5976 =
        addConfig(
            "Switch5976",
            ConfigItem.configTypeBool,
            false
        )
    val switch5977 =
        addConfig(
            "Switch5977",
            ConfigItem.configTypeBool,
            false
        )
    val switch5978 =
        addConfig(
            "Switch5978",
            ConfigItem.configTypeBool,
            false
        )
    val switch5979 =
        addConfig(
            "Switch5979",
            ConfigItem.configTypeBool,
            false
        )
    val switch5980 =
        addConfig(
            "Switch5980",
            ConfigItem.configTypeBool,
            false
        )
    val switch5981 =
        addConfig(
            "Switch5981",
            ConfigItem.configTypeBool,
            false
        )
    val switch5982 =
        addConfig(
            "Switch5982",
            ConfigItem.configTypeBool,
            false
        )
    val switch5983 =
        addConfig(
            "Switch5983",
            ConfigItem.configTypeBool,
            false
        )
    val switch5984 =
        addConfig(
            "Switch5984",
            ConfigItem.configTypeBool,
            false
        )
    val switch5985 =
        addConfig(
            "Switch5985",
            ConfigItem.configTypeBool,
            false
        )
    val switch5986 =
        addConfig(
            "Switch5986",
            ConfigItem.configTypeBool,
            false
        )
    val switch5987 =
        addConfig(
            "Switch5987",
            ConfigItem.configTypeBool,
            false
        )
    val switch5988 =
        addConfig(
            "Switch5988",
            ConfigItem.configTypeBool,
            false
        )
    val switch5989 =
        addConfig(
            "Switch5989",
            ConfigItem.configTypeBool,
            false
        )
    val switch5990 =
        addConfig(
            "Switch5990",
            ConfigItem.configTypeBool,
            false
        )
    val switch5991 =
        addConfig(
            "Switch5991",
            ConfigItem.configTypeBool,
            false
        )
    val switch5992 =
        addConfig(
            "Switch5992",
            ConfigItem.configTypeBool,
            false
        )
    val switch5993 =
        addConfig(
            "Switch5993",
            ConfigItem.configTypeBool,
            false
        )
    val switch5994 =
        addConfig(
            "Switch5994",
            ConfigItem.configTypeBool,
            false
        )
    val switch5995 =
        addConfig(
            "Switch5995",
            ConfigItem.configTypeBool,
            false
        )
    val switch5996 =
        addConfig(
            "Switch5996",
            ConfigItem.configTypeBool,
            false
        )
    val switch5997 =
        addConfig(
            "Switch5997",
            ConfigItem.configTypeBool,
            false
        )
    val switch5998 =
        addConfig(
            "Switch5998",
            ConfigItem.configTypeBool,
            false
        )
    val switch5999 =
        addConfig(
            "Switch5999",
            ConfigItem.configTypeBool,
            false
        )
    val switch6000 =
        addConfig(
            "Switch6000",
            ConfigItem.configTypeBool,
            false
        )

    private fun addConfig(
        k: String,
        t: Int,
        d: Any?
    ): ConfigItem {
        val a =
            ConfigItem(
                k,
                t,
                d
            )
        configs.add(
            a
        )
        return a
    }

    private fun addConfig(
        k: String,
        t: ConfigItem,
        d: Int,
        e: Any?
    ): ConfigItem {
        val a =
            ConfigItemKeyLinked(
                k,
                t,
                d,
                e,
            )
        configs.add(
            a
        )
        return a
    }

    private fun checkMigrate() {
        // Split the legacy setting once, preserving independently saved choices.
        if (preferences.contains("DoubleTapAction")) {
            val legacyAction =
                preferences.getInt("DoubleTapAction", DoubleTap.DOUBLE_TAP_ACTION_NONE)
            val editor = preferences.edit()
            if (!preferences.contains(doubleTapActionIncoming.key)) {
                editor.putInt(
                    doubleTapActionIncoming.key,
                    if (legacyAction == DoubleTap.DOUBLE_TAP_ACTION_EDIT) DoubleTap.DOUBLE_TAP_ACTION_NONE else legacyAction
                )
            }
            if (!preferences.contains(doubleTapActionOutgoing.key)) {
                editor.putInt(doubleTapActionOutgoing.key, legacyAction)
            }
            editor.apply()
        }
    }

    fun loadConfig(
        force: Boolean
    ) {
        synchronized(
            sync
        ) {
            if (configLoaded && !force) {
                return
            }
            checkMigrate()
            for (i in configs.indices) {
                val o =
                    configs[i]
                if (o.type == ConfigItem.configTypeBool) {
                    o.value =
                        preferences.getBoolean(
                            o.key,
                            o.defaultValue as Boolean
                        )
                }
                if (o.type == ConfigItem.configTypeInt) {
                    o.value =
                        preferences.getInt(
                            o.key,
                            o.defaultValue as Int
                        )
                }
                if (o.type == ConfigItem.configTypeLong) {
                    o.value =
                        preferences.getLong(
                            o.key,
                            (o.defaultValue as Long)
                        )
                }
                if (o.type == ConfigItem.configTypeFloat) {
                    o.value =
                        preferences.getFloat(
                            o.key,
                            (o.defaultValue as Float)
                        )
                }
                if (o.type == ConfigItem.configTypeString) {
                    o.value =
                        preferences.getString(
                            o.key,
                            o.defaultValue as String
                        )
                }
                if (o.type == ConfigItem.configTypeSetInt) {
                    val ss =
                        preferences.getStringSet(
                            o.key,
                            HashSet()
                        )
                    val si =
                        HashSet<Int>()
                    for (s in ss!!) {
                        si.add(
                            s.toInt()
                        )
                    }
                    o.value =
                        si
                }
                if (o.type == ConfigItem.configTypeMapIntInt) {
                    val cv =
                        preferences.getString(
                            o.key,
                            ""
                        )
                    // Log.e("NC", String.format("Getting pref %s val %s", o.key, cv));
                    if (cv!!.isEmpty()) {
                        o.value =
                            HashMap<Int, Int>()
                    } else {
                        try {
                            val data =
                                Base64.decode(
                                    cv,
                                    Base64.DEFAULT
                                )
                            val ois =
                                ObjectInputStream(
                                    ByteArrayInputStream(
                                        data
                                    )
                                )
                            o.value =
                                ois.readObject() as HashMap<*, *>
                            if (o.value == null) {
                                o.value =
                                    HashMap<Int, Int>()
                            }
                            ois.close()
                        } catch (e: Exception) {
                            o.value =
                                HashMap<Int, Int>()
                        }
                    }
                }
                if (o.type == ConfigItem.configTypeBoolLinkInt) {
                    o as ConfigItemKeyLinked
                    o.changedFromKeyLinked(o.keyLinked.Int())
                }
            }
            configLoaded =
                true
        }
    }

    init {
        loadConfig(
            false
        )
    }
}
