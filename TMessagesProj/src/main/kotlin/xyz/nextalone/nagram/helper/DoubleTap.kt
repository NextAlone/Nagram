package xyz.nextalone.nagram.helper

import org.telegram.messenger.LocaleController
import org.telegram.messenger.R

object DoubleTap {
    @JvmField
    var doubleTapActionMap: MutableMap<Int, String> =
        HashMap()
    const val DOUBLE_TAP_ACTION_NONE =
        0
    const val DOUBLE_TAP_ACTION_SEND_REACTIONS =
        1
    const val DOUBLE_TAP_ACTION_SHOW_REACTIONS =
        2
    const val DOUBLE_TAP_ACTION_TRANSLATE =
        3
    const val DOUBLE_TAP_ACTION_REPLY =
        4
    const val DOUBLE_TAP_ACTION_SAVE =
        5
    const val DOUBLE_TAP_ACTION_REPEAT =
        6
    const val DOUBLE_TAP_ACTION_REPEAT_AS_COPY =
        7
    const val DOUBLE_TAP_ACTION_EDIT =
        8

    init {
        doubleTapActionMap[DOUBLE_TAP_ACTION_NONE] =
            LocaleController.getString(
                R.string.Disable
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_SEND_REACTIONS] =
            LocaleController.getString(
                R.string.SendReactions
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_SHOW_REACTIONS] =
            LocaleController.getString(
                R.string.ShowReactions
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_TRANSLATE] =
            LocaleController.getString(
                R.string.TranslateMessage
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_REPLY] =
            LocaleController.getString(
                R.string.Reply
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_SAVE] =
            LocaleController.getString(
                R.string.AddToSavedMessages
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_REPEAT] =
            LocaleController.getString(
                R.string.Repeat
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_REPEAT_AS_COPY] =
            LocaleController.getString(
                R.string.RepeatAsCopy
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_EDIT] =
            LocaleController.getString(
                R.string.Edit
            )
    }
}