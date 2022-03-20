package xyz.nextalone.nagram;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

import java.util.HashMap;
import java.util.Map;

public class DoubleTapConfig {
    public static final int DOUBLE_TAP_ACTION_NONE = 0;
    public static final int DOUBLE_TAP_ACTION_REACTION = 1;
    //public static final int DOUBLE_TAP_ACTION_TRANSLATE = 2;
    public static final int DOUBLE_TAP_ACTION_REPLY = 2;
    public static final int DOUBLE_TAP_ACTION_SAVE = 3;
    public static final int DOUBLE_TAP_ACTION_REPEAT = 4;
    public static final int DOUBLE_TAP_ACTION_REPEATASCOPY = 5;
    public static final int DOUBLE_TAP_ACTION_EDIT = 6;

    public Map<Integer, String> doubleTapActionMap = new HashMap<>();

    public DoubleTapConfig() {
        doubleTapActionMap.put(DOUBLE_TAP_ACTION_NONE, LocaleController.getString("Disable", R.string.Disable));
        doubleTapActionMap.put(DOUBLE_TAP_ACTION_REACTION, LocaleController.getString("Reactions", R.string.Reactions));
        //doubleTapActionMap.put(DOUBLE_TAP_ACTION_TRANSLATE, LocaleController.getString("Translate", R.string.Translate));
        doubleTapActionMap.put(DOUBLE_TAP_ACTION_REPLY, LocaleController.getString("Reply", R.string.Reply));
        doubleTapActionMap.put(DOUBLE_TAP_ACTION_SAVE, LocaleController.getString("Save", R.string.Save));
        doubleTapActionMap.put(DOUBLE_TAP_ACTION_REPEAT, LocaleController.getString("Repeat", R.string.Repeat));
        doubleTapActionMap.put(DOUBLE_TAP_ACTION_REPEATASCOPY, LocaleController.getString("RepeatasCopy", R.string.RepeatasCopy));
        doubleTapActionMap.put(DOUBLE_TAP_ACTION_EDIT, LocaleController.getString("Edit", R.string.Edit));
    }
}
