package tw.nekomimi.nekogram.settings;

import static tw.nekomimi.nekogram.utils.UpdateUtil.channelUsernameTips;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.FilledTabsView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.DocumentSelectActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PeerColorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import kotlin.text.StringsKt;
import tw.nekomimi.nekogram.DatacenterActivity;
import tw.nekomimi.nekogram.helpers.CloudSettingsHelper;
import tw.nekomimi.nekogram.helpers.PasscodeHelper;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.EnvUtil;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.GsonUtil;
import tw.nekomimi.nekogram.utils.ShareUtil;

public class NekoSettingsActivity extends BaseFragment {
    public static final int PAGE_TYPE = 0;
    public static final int PAGE_ABOUT = 1;

    private FrameLayout contentView;
    private PeerColorActivity.ColoredActionBar colorBar;

    private Page typePage;
    private Page abountPage;

    private ViewPagerFixed viewPager;

    private ImageView backButton;
    private ImageView syncButton;

    private FrameLayout actionBarContainer;
    private FilledTabsView tabsView;

    private boolean startAtAbout;

    public NekoSettingsActivity startOnAbout() {
        this.startAtAbout = true;
        return this;
    }

    @Override
    public View createView(Context context) {
        typePage = new Page(context, PAGE_TYPE);
        abountPage = new Page(context, PAGE_ABOUT);

        actionBar.setCastShadows(false);
        actionBar.setVisibility(View.GONE);
        actionBar.setAllowOverlayTitle(false);

        FrameLayout frameLayout = getFrameLayout(context);

        colorBar = new PeerColorActivity.ColoredActionBar(context, resourceProvider) {
            @Override
            protected void onUpdateColor() {
                updateActionBarButtonsColor();
                if (tabsView != null) {
                    tabsView.setBackgroundColor(getTabsViewBackgroundColor());
                }
            }

            private int lastBtnColor = 0;
            public void updateActionBarButtonsColor() {
                final int btnColor = getActionBarButtonColor();
                if (lastBtnColor != btnColor) {
                    if (backButton != null) {
                        lastBtnColor = btnColor;
                        backButton.setColorFilter(new PorterDuffColorFilter(btnColor, PorterDuff.Mode.SRC_IN));
                    }
                    if (syncButton != null) {
                        lastBtnColor = btnColor;
                        syncButton.setColorFilter(new PorterDuffColorFilter(btnColor, PorterDuff.Mode.SRC_IN));
                    }
                }
            }
        };
        frameLayout.addView(colorBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.FILL_HORIZONTAL));

        viewPager = new ViewPagerFixed(context) {
            @Override
            protected void onTabAnimationUpdate(boolean manual) {
                tabsView.setSelected(viewPager.getPositionAnimated());
            }
        };
        viewPager.setAdapter(new ViewPagerFixed.Adapter() {
            @Override
            public int getItemCount() {
                return 2;
            }

            @Override
            public View createView(int viewType) {
                if (viewType == PAGE_TYPE) return typePage;
                if (viewType == PAGE_ABOUT) return abountPage;
                return null;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }

            @Override
            public void bindView(View view, int position, int viewType) {

            }
        });
        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));

        actionBarContainer = new FrameLayout(context);
        frameLayout.addView(actionBarContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.FILL_HORIZONTAL));

        tabsView = new FilledTabsView(context);
        tabsView.setTabs(LocaleController.getString("Categories", R.string.Categories), LocaleController.getString("About", R.string.About));
        tabsView.onTabSelected(tab -> {
            if (viewPager != null) {
                viewPager.scrollToPosition(tab);
            }
        });
        actionBarContainer.addView(tabsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 40, Gravity.CENTER));

        if (startAtAbout) {
            viewPager.setPosition(1);
            if (tabsView != null) {
                tabsView.setSelected(1);
            }
        }

        backButton = new ImageView(context);
        backButton.setScaleType(ImageView.ScaleType.CENTER);
        backButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarWhiteSelector), Theme.RIPPLE_MASK_CIRCLE_20DP));
        backButton.setImageResource(R.drawable.ic_ab_back);
        backButton.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        backButton.setOnClickListener(v -> {
            if (onBackPressed()) {
                finishFragment();
            }
        });
        actionBarContainer.addView(backButton, LayoutHelper.createFrame(54, 54, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        syncButton = new ImageView(context);
        syncButton.setScaleType(ImageView.ScaleType.CENTER);
        syncButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarWhiteSelector), Theme.RIPPLE_MASK_CIRCLE_20DP));
        syncButton.setImageResource(R.drawable.cloud_sync);
        syncButton.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        syncButton.setOnClickListener(v -> CloudSettingsHelper.getInstance().showDialog(NekoSettingsActivity.this));
        actionBarContainer.addView(syncButton, LayoutHelper.createFrame(54, 54, Gravity.RIGHT | Gravity.CENTER_VERTICAL));

        fragmentView = contentView = frameLayout;

        return contentView;
    }

    private @NonNull FrameLayout getFrameLayout(Context context) {
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (actionBarContainer != null) {
                    actionBarContainer.getLayoutParams().height = ActionBar.getCurrentActionBarHeight();
                    ((MarginLayoutParams) actionBarContainer.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        frameLayout.setFitsSystemWindows(true);
        return frameLayout;
    }

    private class Page extends FrameLayout {

        private static final int VIEW_TYPE_HEADER = 1;
        private static final int VIEW_TYPE_BOTTOM = 2;
        private static final int VIEW_TYPE_TEXT = 3;
        private static final int VIEW_TYPE_TEXT_LINK = 4;

        private final RecyclerListView listView;
        private final RecyclerView.Adapter listAdapter;
        private final int type;

        private int rowCount;
        private int generalRow = -1;
        private int accountRow = -1;
        private int chatRow = -1;
        private int passcodeRow = -1;
        private int experimentRow = -1;
        private int categories2Row = -1;

        private int importRow = -1;
        private int importSettingsRow = -1;
        private int exportSettingsRow = -1;

        private int channelRow = -1;
        private int channelTipsRow = -1;
        private int sourceCodeRow = -1;
        private int translationRow = -1;
        private int datacenterStatusRow = -1;
        private int actionBarHeight;

        public Page(Context context, int type) {
            super(context);
            this.type = type;

            listView = new RecyclerListView(context);
            listView.setVerticalScrollBarEnabled(false);
            listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
            listView.setAdapter(listAdapter = new RecyclerListView.SelectionAdapter() {
                @Override
                public int getItemCount() {
                    return rowCount;
                }

                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = null;
                    switch (viewType) {
                        case VIEW_TYPE_HEADER:
                            view = new HeaderCell(getContext());
                            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            break;
                        case VIEW_TYPE_BOTTOM:
                            view = new ShadowSectionCell(getContext());
                            break;
                        case VIEW_TYPE_TEXT:
                            view = new TextCell(getContext());
                            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            break;
                        case VIEW_TYPE_TEXT_LINK:
                            view = new TextSettingsCell(getContext());
                            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            break;
                    }
                    //noinspection ConstantConditions
                    view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                    return new RecyclerListView.Holder(view);
                }

                @Override
                public boolean isEnabled(RecyclerView.ViewHolder holder) {
                    int type = holder.getItemViewType();
                    return type == VIEW_TYPE_TEXT || type == VIEW_TYPE_TEXT_LINK;
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    switch (holder.getItemViewType()) {
                        case VIEW_TYPE_HEADER: {
                            HeaderCell headerCell = (HeaderCell) holder.itemView;
                            if (position == importRow) {
                                headerCell.setText(LocaleController.getString("NekoSettings", R.string.NekoSettings));
                            }
                            break;
                        }
                        case VIEW_TYPE_BOTTOM: {
                            if (position == categories2Row) {
                                holder.itemView.setBackground(Theme.getThemedDrawable(getContext(), R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            }
                            break;
                        }
                        case VIEW_TYPE_TEXT: {
                            TextCell textCell = (TextCell) holder.itemView;
                            if (position == chatRow) {
                                textCell.setTextAndIcon(LocaleController.getString("Chat", R.string.Chat), R.drawable.msg_discussion, true);
                            } else if (position == generalRow) {
                                textCell.setTextAndIcon(LocaleController.getString("General", R.string.General), R.drawable.msg_theme, true);
                            } else if (position == passcodeRow) {
                                textCell.setTextAndIcon(LocaleController.getString("PasscodeNeko", R.string.PasscodeNeko), R.drawable.msg_permissions, true);
                            } else if (position == experimentRow) {
                                textCell.setTextAndIcon(LocaleController.getString("Experiment", R.string.Experiment), R.drawable.msg_fave, true);
                            } else if (position == accountRow) {
                                textCell.setTextAndIcon(LocaleController.getString("Account", R.string.Account), R.drawable.msg_contacts, true);
                            }
                            break;
                        }
                        case VIEW_TYPE_TEXT_LINK: {
                            TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                            if (position == channelRow) {
                                textCell.setTextAndValue(LocaleController.getString("OfficialChannel", R.string.OfficialChannel), "@nagram_channel", true);
                            } else if (position == channelTipsRow) {
                                textCell.setTextAndValue(LocaleController.getString("TipsChannel", R.string.TipsChannel), "@" + channelUsernameTips, true);
                            } else if (position == sourceCodeRow) {
                                textCell.setText(LocaleController.getString("SourceCode", R.string.SourceCode), true);
                            } else if (position == translationRow) {
                                textCell.setText(LocaleController.getString("TransSite", R.string.TransSite), true);
                            } else if (position == datacenterStatusRow) {
                                textCell.setText(LocaleController.getString("DatacenterStatus", R.string.DatacenterStatus), true);
                            } else if (position == importSettingsRow) {
                                textCell.setText(LocaleController.getString("ImportSettings", R.string.ImportSettings), true);
                            } else if (position == exportSettingsRow) {
                                textCell.setText(LocaleController.getString("BackupSettings", R.string.BackupSettings), true);
                            }
                            break;
                        }
                    }
                }

                @Override
                public int getItemViewType(int position) {
                    if (position == categories2Row) {
                        return VIEW_TYPE_BOTTOM;
                    } else if (position == importRow) {
                        return VIEW_TYPE_HEADER;
                    } else if (position == chatRow || position == accountRow || position == generalRow || position == passcodeRow || position == experimentRow) {
                        return VIEW_TYPE_TEXT;
                    }
                    return VIEW_TYPE_TEXT_LINK;
                }
            });
            listView.setOnItemClickListener((view, position, x, y) -> {
                if (position == chatRow) {
                    presentFragment(new NekoChatSettingsActivity());
                } else if (position == generalRow) {
                    presentFragment(new NekoGeneralSettingsActivity());
                } else if (position == accountRow) {
                    presentFragment(new NekoAccountSettingsActivity());
                } else if (position == passcodeRow) {
                    presentFragment(new NekoPasscodeSettingsActivity());
                } else if (position == experimentRow) {
                    presentFragment(new NekoExperimentalSettingsActivity());
                } else if (position == channelRow) {
                    MessagesController.getInstance(currentAccount).openByUserName("nagram_channel", NekoSettingsActivity.this, 1);
                } else if (position == channelTipsRow) {
                    MessagesController.getInstance(currentAccount).openByUserName(channelUsernameTips, NekoSettingsActivity.this, 1);
                } else if (position == translationRow) {
                    Browser.openUrl(getParentActivity(), "https://xtaolabs.crowdin.com/nagram");
                } else if (position == sourceCodeRow) {
                    Browser.openUrl(getParentActivity(), "https://github.com/NextAlone/Nagram");
                } else if (position == datacenterStatusRow) {
                    presentFragment(new DatacenterActivity(0));
                } else if (position == importSettingsRow) {
                    DocumentSelectActivity activity = getDocumentSelectActivity(getParentActivity());
                    if (activity != null) {
                        presentFragment(activity);
                    }
                } else if (position == exportSettingsRow) {
                    backupSettings();
                }
            });

            updateRows();

            setWillNotDraw(false);
        }

        private void updateRows() {
            rowCount = 0;
            if (type == PAGE_TYPE) {
                generalRow = rowCount++;
                accountRow = rowCount++;
                chatRow = rowCount++;
                if (!PasscodeHelper.isSettingsHidden()) {
                    passcodeRow = rowCount++;
                } else {
                    passcodeRow = -1;
                }
                experimentRow = rowCount++;
                categories2Row = rowCount++;
                importRow = rowCount++;
                importSettingsRow = rowCount++;
                exportSettingsRow = rowCount++;
            } else {
                channelRow = rowCount++;
                channelTipsRow = rowCount++;
                sourceCodeRow = rowCount++;
                translationRow = rowCount++;
                datacenterStatusRow = rowCount++;
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (getParentLayout() != null) {
                getParentLayout().drawHeaderShadow(canvas, actionBarHeight);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            actionBarHeight = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
            ((MarginLayoutParams) listView.getLayoutParams()).topMargin = actionBarHeight;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void backupSettings() {

        try {
            File cacheFile = new File(EnvUtil.getShareCachePath(), new Date().toLocaleString() + ".nekox-settings.json");
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

        return configJson.toString(indentSpaces);
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
                LocaleController.getString("ImportSettingsAlert", R.string.ImportSettingsAlert),
                R.drawable.baseline_security_24,
                LocaleController.getString("Import", R.string.Import),
                true,
                () -> importSettingsConfirmed(context, settingsFile));

    }

    public static void importSettingsConfirmed(Context context, File settingsFile) {

        try {
            JsonObject configJson = GsonUtil.toJsonObject(FileUtil.readUtf8String(settingsFile));
            importSettings(configJson);

            AlertDialog restart = new AlertDialog(context, 0);
            restart.setTitle(LocaleController.getString("NekoX", R.string.NekoX));
            restart.setMessage(LocaleController.getString("RestartAppToTakeEffect", R.string.RestartAppToTakeEffect));
            restart.setPositiveButton(LocaleController.getString("OK", R.string.OK), (__, ___) -> ProcessPhoenix.triggerRebirth(context, new Intent(context, LaunchActivity.class)));
            restart.show();
        } catch (Exception e) {
            AlertUtil.showSimpleAlert(context, e);
        }

    }

    @SuppressLint("ApplySharedPref")
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

    }

}
