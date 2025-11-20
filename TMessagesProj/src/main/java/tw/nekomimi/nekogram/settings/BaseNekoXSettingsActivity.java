package tw.nekomimi.nekogram.settings;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.inset.WindowInsetsStateHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;
import tw.nekomimi.nekogram.config.cell.*;

public class BaseNekoXSettingsActivity extends BaseFragment {
    private final WindowInsetsStateHolder windowInsetsStateHolder = new WindowInsetsStateHolder(this::checkInsets);
    protected BlurredRecyclerView listView;
    protected LinearLayoutManager layoutManager;
    protected UndoView tooltip;
    protected HashMap<String, Integer> rowMap = new HashMap<>(20);
    protected HashMap<Integer, String> rowMapReverse = new HashMap<>(20);
    protected HashMap<Integer, ConfigItem> rowConfigMapReverse = new HashMap<>(20);

    private void checkInsets() {
        listView.setPadding(0, 0, 0, windowInsetsStateHolder.getCurrentNavigationBarInset());
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tooltip.getLayoutParams();
        layoutParams.bottomMargin = windowInsetsStateHolder.getCurrentNavigationBarInset();
        tooltip.setLayoutParams(layoutParams);
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(getTitle());
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ViewCompat.setOnApplyWindowInsetsListener(fragmentView, (v, insets) -> {
            windowInsetsStateHolder.setInsets(insets);
            return WindowInsetsCompat.CONSUMED;
        });
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new BlurredRecyclerView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setClipToPadding(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        tooltip = new UndoView(context);
        frameLayout.addView(tooltip, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bulletin.addDelegate(this, new Bulletin.Delegate() {
            @Override
            public int getBottomOffset(int tag) {
                return windowInsetsStateHolder.getCurrentNavigationBarInset();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Bulletin.removeDelegate(this);
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    protected void updateRows() {
    }

    public int getBaseGuid() {
        return 10000;
    }

    public int getDrawable() {
        return 0;
    }

    public String getTitle() {
        return "";
    }

    protected void addRowsToMap(CellGroup cellGroup) {
        rowMap.clear();
        rowMapReverse.clear();
        rowConfigMapReverse.clear();
        String key;
        ConfigItem config;
        for (int i = 0; i < cellGroup.rows.size(); i++) {
            config = getBindConfig(cellGroup.rows.get(i));
            key = getRowKey(cellGroup.rows.get(i));
            if (key == null) key = String.valueOf(i);
            rowMap.put(key, i);
            rowMapReverse.put(i, key);
            rowConfigMapReverse.put(i, config);
        }
    }

    protected String getRowKey(int position) {
        if (rowMapReverse.containsKey(position)) {
            return rowMapReverse.get(position);
        }
        return String.valueOf(position);
    }

    protected String getRowValue(int position) {
        ConfigItem config = rowConfigMapReverse.get(position);
        if (config != null) return config.String();
        return null;
    }

    protected ConfigItem getBindConfig(AbstractConfigCell row) {
        if (row instanceof ConfigCellTextCheck) {
            return ((ConfigCellTextCheck) row).getBindConfig();
        } else if (row instanceof ConfigCellSelectBox) {
            return ((ConfigCellSelectBox) row).getBindConfig();
        } else if (row instanceof ConfigCellTextDetail) {
            return ((ConfigCellTextDetail) row).getBindConfig();
        } else if (row instanceof ConfigCellTextInput) {
            return ((ConfigCellTextInput) row).getBindConfig();
        } else if (row instanceof ConfigCellAutoTextCheck) {
            return ((ConfigCellAutoTextCheck) row).getBindConfig();
        }
        return null;
    }

    protected String getRowKey(AbstractConfigCell row) {
        if (row instanceof WithKey) {
            return ((WithKey) row).getKey();
        } else if (row instanceof ConfigCellTextCheck) {
            return ((ConfigCellTextCheck) row).getKey();
        } else if (row instanceof ConfigCellSelectBox) {
            return ((ConfigCellSelectBox) row).getKey();
        } else if (row instanceof ConfigCellTextDetail) {
            return ((ConfigCellTextDetail) row).getKey();
        } else if (row instanceof ConfigCellTextInput) {
            return ((ConfigCellTextInput) row).getKey();
        } else if (row instanceof ConfigCellCustom) {
            return ((ConfigCellCustom) row).getKey();
        } else if (row instanceof ConfigCellAutoTextCheck) {
            return ((ConfigCellAutoTextCheck) row).getKey();
        }
        return null;
    }

    protected void createLongClickDialog(Context context, BaseFragment fragment, String prefix,  int position) {
        String key = getRowKey(position);
        String value = getRowValue(position);
        ArrayList<CharSequence> itemsArray = new ArrayList<>();
        itemsArray.add(LocaleController.getString(R.string.CopyLink));
        if (value != null) {
            itemsArray.add(LocaleController.getString(R.string.BackupSettings));
        }
        CharSequence[] items = itemsArray.toArray(new CharSequence[0]);
        showDialog(new AlertDialog.Builder(context)
                .setItems(
                        items,
                        (dialogInterface, i) -> {
                            switch (i) {
                                case 0:
                                    AndroidUtilities.addToClipboard(String.format(Locale.getDefault(), "https://%s/nasettings/%s?r=%s", getMessagesController().linkPrefix, prefix, key));
                                    BulletinFactory.of(fragment).createCopyLinkBulletin().show();
                                    break;
                                case 1:
                                    AndroidUtilities.addToClipboard(String.format(Locale.getDefault(), "https://%s/nasettings/%s?r=%s&v=%s", getMessagesController().linkPrefix, prefix, key, value));
                                    BulletinFactory.of(fragment).createCopyLinkBulletin().show();
                                    break;
                            }
                        })
                .create());
    }

    public void importToRow(String key, String value, Runnable unknown) {
        int position = -1;
        try {
            position = Integer.parseInt(key);
        } catch (NumberFormatException exception) {
            Integer temp = rowMap.get(key);
            if (temp != null) position = temp;
        }
        ConfigItem config = rowConfigMapReverse.get(position);
        Context context = getParentActivity();
        if (context != null && config != null) {
            Object new_value = config.checkConfigFromString(value);
            if (new_value == null) {
                scrollToRow(key, unknown);
                return;
            }
            var builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString(R.string.ImportSettings));
            builder.setMessage(LocaleController.getString(R.string.ImportSettingsAlert));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), (dialogInter, i) -> scrollToRow(key, unknown));
            builder.setPositiveButton(LocaleController.getString(R.string.Import), (dialogInter, i) -> {
                config.changed(new_value);
                config.saveConfig();
                updateRows();
                scrollToRow(key, unknown);
            });
            builder.show();
        } else {
            scrollToRow(key, unknown);
        }
    }

    public void scrollToRow(String key, Runnable unknown) {
        int position = -1;
        try {
            position = Integer.parseInt(key);
        } catch (NumberFormatException exception) {
            Integer temp = rowMap.get(key);
            if (temp != null) position = temp;
        }
        if (position > -1 && listView != null && layoutManager != null) {
            int finalPosition = position;
            listView.highlightRow(() -> {
                layoutManager.scrollToPositionWithOffset(finalPosition, AndroidUtilities.dp(60));
                return finalPosition;
            });
        } else if (unknown != null) {
            unknown.run();
        }
    }

    public HashMap<Integer, String> getRowMapReverse() {
        return rowMapReverse;
    }
}
