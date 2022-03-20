package xyz.nextalone.nagram.helper

import org.telegram.messenger.LocaleController
import org.telegram.messenger.R

object DoubleTap {
    @JvmField
    var doubleTapActionMap: MutableMap<Int, String> =
        HashMap()
    const val DOUBLE_TAP_ACTION_NONE =
        0
    const val DOUBLE_TAP_ACTION_REACTION =
        1
//    const val DOUBLE_TAP_ACTION_TRANSLATE =
//        2
    const val DOUBLE_TAP_ACTION_REPLY =
        2
    const val DOUBLE_TAP_ACTION_SAVE =
        3
    const val DOUBLE_TAP_ACTION_REPEAT =
        4
    const val DOUBLE_TAP_ACTION_REPEAT_AS_COPY =
        5
    const val DOUBLE_TAP_ACTION_EDIT =
        6

    init {
        doubleTapActionMap[DOUBLE_TAP_ACTION_NONE] =
            LocaleController.getString(
                "Disable",
                R.string.Disable
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_REACTION] =
            LocaleController.getString(
                "Reactions",
                R.string.Reactions
            )
//        doubleTapActionMap[DOUBLE_TAP_ACTION_TRANSLATE] =
//            LocaleController.getString(
//                "Translate",
//                R.string.Translate
//            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_REPLY] =
            LocaleController.getString(
                "Reply",
                R.string.Reply
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_SAVE] =
            LocaleController.getString(
                "Save",
                R.string.Save
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_REPEAT] =
            LocaleController.getString(
                "Repeat",
                R.string.Repeat
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_REPEAT_AS_COPY] =
            LocaleController.getString(
                "RepeatAsCopy",
                R.string.RepeatAsCopy
            )
        doubleTapActionMap[DOUBLE_TAP_ACTION_EDIT] =
            LocaleController.getString(
                "Edit",
                R.string.Edit
            )
    }
}