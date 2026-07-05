package tw.nekomimi.nekogram.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ProfileActivity;

import java.util.ArrayList;

import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;
import tw.nekomimi.nekogram.config.cell.AbstractConfigCell;
import tw.nekomimi.nekogram.config.cell.ConfigCellDivider;
import tw.nekomimi.nekogram.config.cell.ConfigCellHeader;
import tw.nekomimi.nekogram.config.cell.ConfigCellSelectBox;
import tw.nekomimi.nekogram.config.cell.ConfigCellText;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextCheck;
import tw.nekomimi.nekogram.config.cell.ConfigCellTextDetail;
import tw.nekomimi.nekogram.config.cell.WithOnClick;
import tw.nekomimi.nekogram.utils.AlertUtil;

@SuppressLint("RtlHardcoded")
public class NekoDebugSettingsActivity extends BaseNekoXSettingsActivity {

    private final CellGroup a = cellGroup = new CellGroup(this);

    private final AbstractConfigCell headerDebug = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString("DebugMenu", R.string.DebugMenu)));

    // 版本信息：显示当前版本号，点击复制
    private final AbstractConfigCell versionInfoRow = cellGroup.appendCell(new ConfigCellTextDetail(
            new VersionInfoConfigItem(),
            (view, position) -> {
                AndroidUtilities.addToClipboard(AndroidUtilities.getBuildVersionInfo());
                AlertUtil.showToast(LocaleController.getString(R.string.TextCopied));
            },
            AndroidUtilities.getBuildVersionInfo()));

    // 启用/关闭日志
    private final AbstractConfigCell logsEnabledRow = cellGroup.appendCell(new ConfigCellTextCheck(
            new LogsEnabledConfigItem(),
            null,
            BuildVars.LOGS_ENABLED ? LocaleController.getString(R.string.DebugMenuDisableLogs) : LocaleController.getString(R.string.DebugMenuEnableLogs)));

    // 切换版本
    private final AbstractConfigCell switchVersionRow = cellGroup.appendCell(new ConfigCellText("SwitchVersion", () -> {
        if (getParentActivity() == null) return;
        Browser.openUrl(getParentActivity(), "https://github.com/NextAlone/Nagram/releases");
    }));

    // 检查更新
    private final AbstractConfigCell checkUpdateRow = cellGroup.appendCell(new ConfigCellText("CheckUpdate", () -> {
        if (getParentActivity() == null) return;
        Browser.openUrl(getParentActivity(), "tg://update");
    }));

    // 自动更新通道
    private final String[] autoUpdateChannelOptions = new String[]{
            LocaleController.getString(R.string.AutoCheckUpdateOFF),
            LocaleController.getString(R.string.AutoCheckUpdateStable),
            LocaleController.getString(R.string.AutoCheckUpdateRc),
            LocaleController.getString(R.string.AutoCheckUpdatePreview),
    };
    private final AbstractConfigCell autoUpdateChannelRow = cellGroup.appendCell(new ConfigCellSelectBox(
            "AutoCheckUpdateSwitch",
            new AutoUpdateChannelConfigItem(),
            autoUpdateChannelOptions,
            null));

    private final AbstractConfigCell divider0 = cellGroup.appendCell(new ConfigCellDivider());

    // 仅在开启日志时显示日志相关子菜单
    private final AbstractConfigCell logsHeaderRow = cellGroup.appendCell(new ConfigCellHeader(LocaleController.getString(R.string.SettingsDebug)));
    private final AbstractConfigCell sendLogsRow = cellGroup.appendCell(new ConfigCellText("DebugSendLogs", () -> {
        if (getParentActivity() == null) return;
        ProfileActivity.sendLogs(getParentActivity(), false);
    }));
    private final AbstractConfigCell sendLastLogsRow = cellGroup.appendCell(new ConfigCellText("DebugSendLastLogs", () -> {
        if (getParentActivity() == null) return;
        ProfileActivity.sendLogs(getParentActivity(), true);
    }));
    private final AbstractConfigCell clearLogsRow = cellGroup.appendCell(new ConfigCellText("DebugClearLogs", FileLog::cleanupLogs));
    private final AbstractConfigCell divider1 = cellGroup.appendCell(new ConfigCellDivider());

    public NekoDebugSettingsActivity() {
        updateRows();
    }

    @SuppressLint("NewApi")
    @Override
    public View createView(Context context) {
        var superView = super.createView(context);

        listAdapter = new ListAdapter(context);

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((view, position, x, y) -> {
            AbstractConfigCell cell = cellGroup.rows.get(position);
            if (cell instanceof ConfigCellTextCheck) {
                ((ConfigCellTextCheck) cell).onClick((TextCheckCell) view);
                // 日志开关切换后立即刷新并按需显示/隐藏日志子菜单
                if (position == cellGroup.rows.indexOf(logsEnabledRow)) {
                    updateRows();
                }
            } else if (cell instanceof ConfigCellTextDetail) {
                RecyclerListView.OnItemClickListener o = ((ConfigCellTextDetail) cell).onItemClickListener;
                if (o != null) {
                    try {
                        o.onItemClick(view, position);
                    } catch (Exception ignored) {}
                }
            } else if (cell instanceof WithOnClick) {
                ((WithOnClick) cell).onClick();
            } else if (cell instanceof ConfigCellSelectBox) {
                ((ConfigCellSelectBox) cell).onClick(view);
            }
        });
        listView.setOnItemLongClickListener((view, position, x, y) -> {
            var holder = listView.findViewHolderForAdapterPosition(position);
            if (holder != null && listAdapter.isEnabled(holder)) {
                createLongClickDialog(context, NekoDebugSettingsActivity.this, "debug", position);
                return true;
            }
            return false;
        });

        cellGroup.setListAdapter(listView, listAdapter);

        return superView;
    }

    @Override
    public int getBaseGuid() {
        return 13000;
    }

    @Override
    public int getDrawable() {
        return R.drawable.msg_info;
    }

    @Override
    public String getTitle() {
        return LocaleController.getString(R.string.DebugMenu);
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        return themeDescriptions;
    }

    @Override
    protected void setCanNotChange() {
        super.setCanNotChange();

        cellGroup.rows.remove(logsHeaderRow);
        cellGroup.rows.remove(sendLogsRow);
        cellGroup.rows.remove(sendLastLogsRow);
        cellGroup.rows.remove(clearLogsRow);
        cellGroup.rows.remove(divider1);

        if (BuildVars.LOGS_ENABLED) {
            int idx = cellGroup.rows.indexOf(divider0);
            cellGroup.rows.add(++idx, logsHeaderRow);
            cellGroup.rows.add(++idx, sendLogsRow);
            cellGroup.rows.add(++idx, sendLastLogsRow);
            cellGroup.rows.add(++idx, clearLogsRow);
            cellGroup.rows.add(++idx, divider1);
        }

        addRowsToMap();
    }

    //impl ListAdapter
    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean partial, boolean divider) {
            AbstractConfigCell cell = cellGroup.rows.get(position);
            if (cell != null) {
                cell.onBindViewHolder(holder);
            }
        }
    }

    private static class VersionInfoConfigItem extends ConfigItem {
        VersionInfoConfigItem() {
            super("VersionInfo", configTypeString, "");
            value = AndroidUtilities.getBuildVersionInfo();
        }

        @Override
        public void saveConfig() {}
    }

    private static class LogsEnabledConfigItem extends ConfigItem {
        LogsEnabledConfigItem() {
            super("DebugMenuEnableLogs", configTypeBool, false);
            value = BuildVars.LOGS_ENABLED;
        }

        @Override
        public void saveConfig() {
            BuildVars.LOGS_ENABLED = BuildVars.DEBUG_VERSION = BuildVars.DEBUG_PRIVATE_VERSION = (boolean) value;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).apply();
        }
    }

    private static class AutoUpdateChannelConfigItem extends ConfigItem {
        AutoUpdateChannelConfigItem() {
            super("AutoCheckUpdateSwitch", configTypeInt, 2);
            value = NekoXConfig.autoUpdateReleaseChannel;
        }

        @Override
        public void saveConfig() {
            NekoXConfig.setAutoUpdateReleaseChannel((int) value);
        }
    }
}
