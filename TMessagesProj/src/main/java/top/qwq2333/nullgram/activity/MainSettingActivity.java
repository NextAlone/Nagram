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

package top.qwq2333.nullgram.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonObject;
import com.jakewharton.processphoenix.ProcessPhoenix;
import java.io.File;
import java.util.ArrayList;
import org.json.JSONException;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DocumentSelectActivity;
import org.telegram.ui.LaunchActivity;
import top.qwq2333.nullgram.config.ConfigManager;
import top.qwq2333.nullgram.utils.AlertUtil;
import top.qwq2333.nullgram.utils.Defines;
import top.qwq2333.nullgram.utils.FileUtils;
import top.qwq2333.nullgram.utils.JsonUtils;
import top.qwq2333.nullgram.utils.LogUtilsKt;
import top.qwq2333.nullgram.utils.ShareUtil;

@SuppressLint("NotifyDataSetChanged")
public class MainSettingActivity extends BaseFragment {

    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private int rowCount;

    private int categoriesRow;
    private int generalRow;
    private int chatRow;
    private int experimentRow;
    private int categories2Row;

    private int aboutRow;
    private int channelRow;
    private int websiteRow;
    private int sourceCodeRow;
    private int licenseRow;
    private int about2Row;
    private int updateRow;


    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        updateRows();

        return true;
    }

    private static final int backup_settings = 1;
    private static final int import_settings = 2;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("NullSettings", R.string.NullSettings));
        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem otherMenu = menu.addItem(0, R.drawable.ic_ab_other);
        otherMenu.addSubItem(backup_settings,
            LocaleController.getString("BackupSettings", R.string.BackupSettings));
        otherMenu.addSubItem(import_settings,
            LocaleController.getString("ImportSettings", R.string.ImportSettings));

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == backup_settings) {
                    backupSettings();
                } else if (id == import_settings) {
                    try {
                        if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission(
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                            getParentActivity().requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);
                            return;
                        }
                    } catch (Throwable ignore) {
                    }
                    DocumentSelectActivity fragment = new DocumentSelectActivity(false);
                    fragment.setMaxSelectedFiles(1);
                    fragment.setAllowPhoto(false);
                    fragment.setDelegate(
                        new DocumentSelectActivity.DocumentSelectActivityDelegate() {
                            @Override
                            public void didSelectFiles(DocumentSelectActivity activity,
                                ArrayList<String> files, String caption, boolean notify,
                                int scheduleDate) {
                                activity.finishFragment();
                                importSettings(getParentActivity(), new File(files.get(0)));
                            }

                            @Override
                            public void didSelectPhotos(
                                ArrayList<SendMessagesHelper.SendingMediaInfo> photos,
                                boolean notify, int scheduleDate) {
                            }

                            @Override
                            public void startDocumentSelectActivity() {
                            }
                        });
                    presentFragment(fragment);
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setLayoutManager(
            new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);
        ((DefaultItemAnimator) listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(listView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == chatRow) {
                presentFragment(new ChatSettingActivity());
            } else if (position == generalRow) {
                presentFragment(new GeneralSettingActivity());
            } else if (position == experimentRow) {
                presentFragment(new ExperimentSettingActivity());
            } else if (position == channelRow) {
                MessagesController.getInstance(currentAccount).openByUserName(
                    LocaleController.getString("OfficialChannelName", R.string.OfficialChannelName),
                    this, 1);
            } else if (position == websiteRow) {
                Browser.openUrl(getParentActivity(), "https://qwq2333.top");
            } else if (position == sourceCodeRow) {
                Browser.openUrl(getParentActivity(), "https://github.com/qwq233/Nullgram");
            } else if (position == licenseRow) {
                presentFragment(new LicenseActivity());
            } else if (position == updateRow) {
                Browser.openUrl(context, "tg://update");
            }
        });
        listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            private int pressCount = 0;

            @Override
            public boolean onItemClick(View view, int position) {
                if (position == experimentRow) {
                    pressCount++;
                    if (pressCount >= 2) {
                        ConfigManager.toggleBoolean(Defines.showHiddenSettings);
                        AndroidUtilities.shakeView(view, 2, 0);
                        return true;
                    }
                }
                return false;
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private void updateRows() {
        rowCount = 0;
        categoriesRow = rowCount++;
        generalRow = rowCount++;
        chatRow = rowCount++;
        experimentRow = rowCount++;
        categories2Row = rowCount++;

        aboutRow = rowCount++;
        channelRow = rowCount++;
        websiteRow = rowCount++;
        sourceCodeRow = rowCount++;
        licenseRow = rowCount++;
        about2Row = rowCount++;

        updateRow = rowCount++;

        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    break;
                }
                case 2: {
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == chatRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Chat", R.string.Chat),
                            R.drawable.menu_chats, true);
                    } else if (position == generalRow) {
                        textCell.setTextAndIcon(
                            LocaleController.getString("General", R.string.General),
                            R.drawable.msg_theme, true);
                    } else if (position == experimentRow) {
                        textCell.setTextAndIcon(
                            LocaleController.getString("Experiment", R.string.Experiment),
                            R.drawable.msg_fave, true);
                    }
                    break;
                }
                case 3: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == channelRow) {
                        textCell.setTextAndValue(
                            LocaleController.getString("OfficialChannel", R.string.OfficialChannel),
                            "@" + LocaleController.getString("OfficialChannelName",
                                R.string.OfficialChannelName), true);
                    } else if (position == websiteRow) {
                        textCell.setTextAndValue(
                            LocaleController.getString("OfficialSite", R.string.OfficialSite),
                            "qwq2333.top", true);
                    } else if (position == sourceCodeRow) {
                        textCell.setTextAndValue(
                            LocaleController.getString("ViewSourceCode", R.string.ViewSourceCode),
                            "GitHub", true);
                    } else if (position == licenseRow) {
                        textCell.setText(
                            LocaleController.getString("OpenSource", R.string.OpenSource), true);
                    }
                    break;
                }
                case 4: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == categoriesRow) {
                        headerCell.setText(
                            LocaleController.getString("Categories", R.string.Categories));
                    } else if (position == aboutRow) {
                        headerCell.setText(LocaleController.getString("About", R.string.About));
                    }
                    break;
                }
                case 5: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == updateRow) {
                        textCell.setTextAndValue(
                            LocaleController.getString("CheckUpdate", R.string.CheckUpdate),
                            "Click Me", true);
                    }
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 2 || type == 3 || type == 5;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 2:
                    view = new TextCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 7:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider,
                        Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(
                new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == updateRow) {
                return 5;
            } else if (position == categories2Row || position == about2Row) {
                return 1;
            } else if (position > categoriesRow && position < categories2Row) {
                return 2;
            } else if (position >= channelRow && position < about2Row) {
                return 3;
            } else if (position == categoriesRow || position == aboutRow) {
                return 4;
            }
            return 2;
        }
    }

    private void backupSettings() {

        try {
            File cacheFile = new File(ApplicationLoader.applicationContext.getCacheDir(),
                DateFormat.format("yyyyMMdd", System.currentTimeMillis())
                    + "-nullgram-settings.json");
            FileUtils.writeUtf8String(ConfigManager.exportConfigurationToJson(), cacheFile);
            ShareUtil.shareFile(getParentActivity(), cacheFile);
        } catch (JSONException e) {
            AlertUtil.showSimpleAlert(getParentActivity(), e);
        } catch (Exception e) {
            LogUtilsKt.e(e);
        }

    }

    public static void importSettings(Context context, File settingsFile) {

        AlertUtil.showConfirm(context,
            LocaleController.getString("ImportSettingsAlert", R.string.ImportSettingsAlert),
            R.drawable.baseline_security_24, LocaleController.getString("Import", R.string.Import),
            true, () -> importSettingsConfirmed(context, settingsFile));

    }

    public static void importSettingsConfirmed(Context context, File settingsFile) {

        try {
            JsonObject configJson = JsonUtils.toJsonObject(FileUtils.readUtf8String(settingsFile));
            ConfigManager.importSettings(configJson);

            AlertDialog restart = new AlertDialog(context, 0);
            restart.setTitle(LocaleController.getString("AppName", R.string.AppName));
            restart.setMessage(LocaleController.getString("RestartAppToTakeEffect",
                R.string.RestartAppToTakeEffect));
            restart.setPositiveButton(LocaleController.getString("OK", R.string.OK), (__, ___) -> {
                ProcessPhoenix.triggerRebirth(context, new Intent(context, LaunchActivity.class));
            });
            restart.show();
        } catch (Exception e) {
            AlertUtil.showSimpleAlert(context, e);
        }

    }
}
