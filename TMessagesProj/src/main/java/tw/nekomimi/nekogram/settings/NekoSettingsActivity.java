package tw.nekomimi.nekogram.settings;

import static tw.nekomimi.nekogram.utils.UpdateUtil.channelUsername;
import static tw.nekomimi.nekogram.utils.UpdateUtil.channelUsernameTips;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.DocumentSelectActivity;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import kotlin.text.StringsKt;
import tw.nekomimi.nekogram.DatacenterActivity;
import tw.nekomimi.nekogram.helpers.AppRestartHelper;
import tw.nekomimi.nekogram.helpers.CloudSettingsHelper;
import tw.nekomimi.nekogram.helpers.PasscodeHelper;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.EnvUtil;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.GsonUtil;
import tw.nekomimi.nekogram.utils.ShareUtil;
import xyz.nextalone.nagram.NkmrConfig;
import xyz.nextalone.nagram.network.NetworkLogActivity;

public class NekoSettingsActivity extends BaseNekoSettingsActivity {

    private int categoriesRow;
    private int generalRow;
    private int accountRow;
    private int chatRow;
    private int passcodeRow;
    private int experimentRow;
    private int debugRow;
    private int categories2Row;

    private int aboutRow;
    private int channelRow;
    private int channelTipsRow;
    private int sourceCodeRow;
    private int translationRow;
    private int datacenterRow;
    private int networkLogRow;
    private int about2Row;

    private int settingsRow;
    private int importSettingsRow;
    private int exportSettingsRow;
    private int resetSettingsRow;
    private int settings2Row;

    @Override
    public View createView(Context context) {
        View fragmentView = super.createView(context);

        actionBar.createMenu()
                .addItem(0, R.drawable.cloud_sync)
                .setOnClickListener(v -> CloudSettingsHelper.getInstance().showDialog(NekoSettingsActivity.this));

        return fragmentView;
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == generalRow) {
            presentFragment(new NekoGeneralSettingsActivity());
        } else if (position == accountRow) {
            presentFragment(new NekoAccountSettingsActivity());
        } else if (position == chatRow) {
            presentFragment(new NekoChatSettingsActivity());
        } else if (position == passcodeRow) {
            presentFragment(new NekoPasscodeSettingsActivity());
        } else if (position == experimentRow) {
            presentFragment(new NekoExperimentalSettingsActivity());
        } else if (position == debugRow) {
            presentFragment(new NekoDebugSettingsActivity());
        } else if (position == channelRow) {
            getMessagesController().openByUserName(channelUsername, this, 1);
        } else if (position == channelTipsRow) {
            getMessagesController().openByUserName(channelUsernameTips, this, 1);
        } else if (position == sourceCodeRow) {
            Browser.openUrl(getParentActivity(), "https://github.com/NextAlone/Nagram");
        } else if (position == translationRow) {
            Browser.openUrl(getParentActivity(), "https://xtaolabs.crowdin.com/nagram");
        } else if (position == datacenterRow) {
            presentFragment(new DatacenterActivity(0));
        } else if (position == networkLogRow) {
            presentFragment(new NetworkLogActivity());
        }  else if (position == importSettingsRow) {
            DocumentSelectActivity activity = getDocumentSelectActivity(getParentActivity());
            presentFragment(activity);
        } else if (position == resetSettingsRow) {
            AlertUtil.showConfirm(getParentActivity(),
                    LocaleController.getString(R.string.ResetSettingsAlert),
                    R.drawable.msg_reset,
                    LocaleController.getString(R.string.Reset),
                    true,
                    () -> {
                        ApplicationLoader.applicationContext.getSharedPreferences("nekocloud", Activity.MODE_PRIVATE).edit().clear().commit();
                        ApplicationLoader.applicationContext.getSharedPreferences("nekox_config", Activity.MODE_PRIVATE).edit().clear().commit();
                        NkmrConfig.clear();
                        AppRestartHelper.triggerRebirth();
                    });
        } else if (position == exportSettingsRow) {
            backupSettings();
        }
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    protected String getActionBarTitle() {
        return LocaleController.getString(R.string.N_Config);
    }

    @Override
    protected String getKey() {
        return "about";
    }

    @Override
    protected void updateRows() {
        super.updateRows();

        categoriesRow = addRow("categories");
        generalRow = addRow("general");
        accountRow = addRow("account");
        chatRow = addRow("chat");
        if (!PasscodeHelper.isSettingsHidden()) {
            passcodeRow = addRow("passcode");
        } else {
            passcodeRow = -1;
        }
        experimentRow = addRow("experiment");
        debugRow = addRow("debug");
        categories2Row = addRow();

        aboutRow = addRow("about");
        channelRow = addRow("channel");
        channelTipsRow = addRow("channelTips");
        sourceCodeRow = addRow("sourceCode");
        translationRow = addRow("translation");
        datacenterRow = addRow("datacenter");
        networkLogRow = addRow("networkLog");
        about2Row = addRow();

        settingsRow = addRow("settings");
        importSettingsRow = addRow("importSettings");
        exportSettingsRow = addRow("exportSettings");
        resetSettingsRow = addRow("resetSettings");
        settings2Row = addRow();
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean partial, boolean divider) {
            switch (holder.getItemViewType()) {
                case TYPE_SETTINGS: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == channelRow) {
                        textCell.setTextAndValue(LocaleController.getString(R.string.OfficialChannel), "@" + channelUsername, divider);
                    } else if (position == channelTipsRow) {
                        textCell.setTextAndValue(LocaleController.getString(R.string.TipsChannel), "@" + channelUsernameTips, divider);
                    } else if (position == sourceCodeRow) {
                        textCell.setText(LocaleController.getString(R.string.SourceCode), divider);
                    } else if (position == translationRow) {
                        textCell.setText(LocaleController.getString(R.string.TransSite), divider);
                    } else if (position == datacenterRow) {
                        textCell.setText(LocaleController.getString(R.string.DatacenterStatus), divider);
                    } else if (position == networkLogRow) {
                        textCell.setText(LocaleController.getString(R.string.NetworkLog), divider);
                    } else if (position == importSettingsRow) {
                        textCell.setText(LocaleController.getString(R.string.ImportSettings), divider);
                    } else if (position == exportSettingsRow) {
                        textCell.setText(LocaleController.getString(R.string.BackupSettings), divider);
                    } else if (position == resetSettingsRow) {
                        textCell.setText(LocaleController.getString(R.string.ResetSettings), divider);
                    }
                    break;
                }
                case TYPE_HEADER: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == categoriesRow) {
                        headerCell.setText(LocaleController.getString(R.string.Categories));
                    } else if (position == aboutRow) {
                        headerCell.setText(LocaleController.getString(R.string.About));
                    } else if (position == settingsRow) {
                        headerCell.setText(LocaleController.getString(R.string.N_Config));
                    }
                    break;
                }
                case TYPE_TEXT: {
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == generalRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.General), R.drawable.msg_media, divider);
                    } else if (position == accountRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.Account), R.drawable.msg_contacts, divider);
                    } else if (position == chatRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.Chat), R.drawable.msg_discussion, divider);
                    } else if (position == passcodeRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.PasscodeNeko), R.drawable.msg_secret, divider);
                    } else if (position == experimentRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.Experiment), R.drawable.msg_fave, divider);
                    } else if (position == debugRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.DebugMenu), R.drawable.msg_info, divider);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == categories2Row || position == about2Row || position == settings2Row) {
                return TYPE_SHADOW;
            } else if (position == categoriesRow || position == aboutRow || position == settingsRow) {
                return TYPE_HEADER;
            } else if (position > categoriesRow && position < categories2Row) {
                return TYPE_TEXT;
            } else if (position >= channelRow && position < about2Row) {
                return TYPE_SETTINGS;
            } else if (position >= importSettingsRow && position < settings2Row) {
                return TYPE_SETTINGS;
            }
            return TYPE_SETTINGS;
        }
    }

    private void backupSettings() {
        try {
            DateFormat formatter = DateFormat.getDateTimeInstance();
            File cacheFile = new File(EnvUtil.getShareCachePath(), formatter.format(new Date()) + ".nekox-settings.json");
            FileUtil.writeUtf8String(backupSettingsJson(), cacheFile);
            ShareUtil.shareFile(getParentActivity(), cacheFile);
        } catch (JSONException e) {
            AlertUtil.showSimpleAlert(getParentActivity(), e);
        }

    }

    public static String backupSettingsJson() throws JSONException {
        return backupSettingsJson(4);
    }

    public static String backupSettingsJson(int indentSpaces) throws JSONException {

        JSONObject configJson = new JSONObject();

        ArrayList<String> userconfig = new ArrayList<>();
        userconfig.add("saveIncomingPhotos");
        userconfig.add("passcodeHash");
        userconfig.add("passcodeType");
        userconfig.add("passcodeHash");
        userconfig.add("autoLockIn");
        userconfig.add("useFingerprint");
        spToJSON("userconfing", configJson, userconfig::contains);

        ArrayList<String> mainconfig = new ArrayList<>();
        mainconfig.add("saveToGallery");
        mainconfig.add("autoplayGifs");
        mainconfig.add("autoplayVideo");
        mainconfig.add("mapPreviewType");
        mainconfig.add("raiseToSpeak");
        mainconfig.add("customTabs");
        mainconfig.add("directShare");
        mainconfig.add("shuffleMusic");
        mainconfig.add("playOrderReversed");
        mainconfig.add("inappCamera");
        mainconfig.add("repeatMode");
        mainconfig.add("fontSize");
        mainconfig.add("bubbleRadius");
        mainconfig.add("ivFontSize");
        mainconfig.add("allowBigEmoji");
        mainconfig.add("streamMedia");
        mainconfig.add("saveStreamMedia");
        mainconfig.add("smoothKeyboard");
        mainconfig.add("pauseMusicOnRecord");
        mainconfig.add("streamAllVideo");
        mainconfig.add("streamMkv");
        mainconfig.add("suggestStickers");
        mainconfig.add("sortContactsByName");
        mainconfig.add("sortFilesByName");
        mainconfig.add("noSoundHintShowed");
        mainconfig.add("directShareHash");
        mainconfig.add("useThreeLinesLayout");
        mainconfig.add("archiveHidden");
        mainconfig.add("distanceSystemType");
        mainconfig.add("loopStickers");
        mainconfig.add("keepMedia");
        mainconfig.add("noStatusBar");
        mainconfig.add("lastKeepMediaCheckTime");
        mainconfig.add("searchMessagesAsListHintShows");
        mainconfig.add("searchMessagesAsListUsed");
        mainconfig.add("stickersReorderingHintUsed");
        mainconfig.add("textSelectionHintShows");
        mainconfig.add("scheduledOrNoSoundHintShows");
        mainconfig.add("lockRecordAudioVideoHint");
        mainconfig.add("disableVoiceAudioEffects");
        mainconfig.add("chatSwipeAction");

        mainconfig.add("theme");
        mainconfig.add("selectedAutoNightType");
        mainconfig.add("autoNightScheduleByLocation");
        mainconfig.add("autoNightBrighnessThreshold");
        mainconfig.add("autoNightDayStartTime");
        mainconfig.add("autoNightDayEndTime");
        mainconfig.add("autoNightSunriseTime");
        mainconfig.add("autoNightCityName");
        mainconfig.add("autoNightSunsetTime");
        mainconfig.add("autoNightLocationLatitude3");
        mainconfig.add("autoNightLocationLongitude3");
        mainconfig.add("autoNightLastSunCheckDay");

        mainconfig.add("lang_code");

        spToJSON("mainconfig", configJson, mainconfig::contains);
        spToJSON("themeconfig", configJson, null);

        spToJSON("nkmrcfg", configJson, null);
        spToJSON("nekodialogconfig", configJson, null);

        return indentSpaces > 0 ? configJson.toString(indentSpaces): configJson.toString();
    }

    private static void spToJSON(String sp, JSONObject object, Function<String, Boolean> filter) throws JSONException {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(sp, Activity.MODE_PRIVATE);
        JSONObject jsonConfig = new JSONObject();
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            String key = entry.getKey();
            if (filter != null && !filter.apply(key)) continue;
            if (entry.getValue() instanceof Long) {
                key = key + "_long";
            } else if (entry.getValue() instanceof Float) {
                key = key + "_float";
            }
            jsonConfig.put(key, entry.getValue());
        }
        object.put(sp, jsonConfig);
    }

    private DocumentSelectActivity getDocumentSelectActivity(Activity parent) {
        DocumentSelectActivity fragment = new DocumentSelectActivity(false);
        fragment.setMaxSelectedFiles(1);
        fragment.setAllowPhoto(false);
        fragment.setDelegate(new DocumentSelectActivity.DocumentSelectActivityDelegate() {
            @Override
            public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files, String caption, boolean notify, int scheduleDate) {
                activity.finishFragment();
                importSettings(parent, new File(files.get(0)));
            }

            @Override
            public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
            }

            @Override
            public void startDocumentSelectActivity() {
            }
        });
        return fragment;
    }

    public static void importSettings(Context context, File settingsFile) {

        AlertUtil.showConfirm(context,
                LocaleController.getString(R.string.ImportSettingsAlert),
                R.drawable.baseline_security_24,
                LocaleController.getString(R.string.Import),
                true,
                () -> importSettingsConfirmed(context, settingsFile));

    }

    public static void importSettingsConfirmed(Context context, File settingsFile) {

        try {
            JsonObject configJson = GsonUtil.toJsonObject(FileUtil.readUtf8String(settingsFile));
            importSettings(configJson);

            AlertDialog restart = new AlertDialog(context, 0);
            restart.setTitle(LocaleController.getString(R.string.NekoX));
            restart.setMessage(LocaleController.getString(R.string.RestartAppToTakeEffect));
            restart.setPositiveButton(LocaleController.getString(R.string.OK), (__, ___) -> AppRestartHelper.triggerRebirth());
            restart.show();
        } catch (Exception e) {
            AlertUtil.showSimpleAlert(context, e);
        }

    }

    public static void importSettings(JsonObject configJson) throws JSONException {
        for (Map.Entry<String, JsonElement> element : configJson.entrySet()) {
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences(element.getKey(), Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            for (Map.Entry<String, JsonElement> config : ((JsonObject) element.getValue()).entrySet()) {
                String key = config.getKey();
                JsonPrimitive value = (JsonPrimitive) config.getValue();
                if (value.isBoolean()) {
                    editor.putBoolean(key, value.getAsBoolean());
                } else if (value.isNumber()) {
                    boolean isLong = false;
                    boolean isFloat = false;
                    if (key.endsWith("_long")) {
                        key = StringsKt.substringBeforeLast(key, "_long", key);
                        isLong = true;
                    } else if (key.endsWith("_float")) {
                        key = StringsKt.substringBeforeLast(key, "_float", key);
                        isFloat = true;
                    }
                    if (isLong) {
                        editor.putLong(key, value.getAsLong());
                    } else if (isFloat) {
                        editor.putFloat(key, value.getAsFloat());
                    } else {
                        editor.putInt(key, value.getAsInt());
                    }
                } else {
                    editor.putString(key, value.getAsString());
                }
            }
            editor.commit();
        }
        NkmrConfig.compact();
    }
}
