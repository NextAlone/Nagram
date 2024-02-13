package xyz.nextalone.nagram

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.LocaleController
import org.telegram.messenger.R
import tw.nekomimi.nekogram.config.ConfigItem
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream


object NaConfig {
    const val TAG =
        "NextAlone"
    val preferences: SharedPreferences =
        ApplicationLoader.applicationContext.getSharedPreferences(
            "nkmrcfg",
            Context.MODE_PRIVATE
        )
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
    val doubleTapAction =
        addConfig(
            "DoubleTapAction",
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
                "NekoX",
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
                "channelLabel",
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
                "Great",
                R.string.Great
            )
        )
    val CustomPoor =
        addConfig(
            "CustomPoor",
            ConfigItem.configTypeString,
            LocaleController.getString(
                "Pool",
                R.string.Poor
            )

        )
    val customEditedMessage =
        addConfig(
            "CustomEditedMessage",
            ConfigItem.configTypeString,
            ""
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
        get() = externalStickerCache.String().let { if (it.isBlank()) return null else return Uri.parse(it) }
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

    fun loadConfig(
        force: Boolean
    ) {
        synchronized(
            sync
        ) {
            if (configLoaded && !force) {
                return
            }
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
