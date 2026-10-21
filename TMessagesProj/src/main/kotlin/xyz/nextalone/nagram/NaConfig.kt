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
