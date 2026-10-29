package tw.nekomimi.nekogram.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.FilterTabsView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tw.nekomimi.nekogram.NekoConfig;
import xyz.nextalone.nagram.NaConfig;

@SuppressLint("ViewConstructor")
public class FilterTabsPreviewCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

    private final int currentAccount;
    private final int counterSeed;
    private final FilterTabsView filterTabsView;
    private final Map<Integer, Integer> idsWithCounters = new HashMap<>();
    private final BlurredBackgroundSourceColor blurredBackgroundSourceColor;

    private final Runnable refreshRunnable = this::refreshPreview;

    private void refreshPreview() {
        updateTabs(true);
        if (filterTabsView != null) {
            filterTabsView.invalidate();
        }
        invalidate();
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener preferencesListener = (preferences, key) -> {
        if (isPreviewSetting(key)) {
            removeCallbacks(refreshRunnable);
            post(refreshRunnable);
        }
    };

    private boolean isPreviewSetting(String key) {
        return key != null && (
                key.equals(NekoConfig.hideAllTab.getKey())
                        || key.equals(NekoConfig.tabsTitleType.getKey())
                        || key.equals(NaConfig.INSTANCE.getTabStyle().getKey())
                        || key.equals(NaConfig.INSTANCE.getTabStyleStroke().getKey())
                        || key.equals(NaConfig.INSTANCE.getIgnoreFolderCount().getKey())
                        || key.equals(NekoConfig.ignoreMutedCount.getKey())
        );
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public FilterTabsPreviewCell(Context context) {
        this(context, UserConfig.selectedAccount);
    }

    public FilterTabsPreviewCell(Context context, int currentAccount) {
        super(context);
        this.currentAccount = currentAccount;
        this.counterSeed = Utilities.random.nextInt(40) + 20;
        setWillNotDraw(false);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        blurredBackgroundSourceColor = new BlurredBackgroundSourceColor();
        blurredBackgroundSourceColor.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceColor);

        filterTabsView = new FilterTabsView(context, null);
        filterTabsView.setPadding(0, AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f));

        BlurredBackgroundDrawable blurredBackgroundDrawableCreate = blurredBackgroundDrawableViewFactory.create(filterTabsView, BlurredBackgroundProviderImpl.topPanel(null));
        blurredBackgroundDrawableCreate.setRadius(AndroidUtilities.dp(18.0f));
        blurredBackgroundDrawableCreate.setPadding(AndroidUtilities.dp(6.666f));
        filterTabsView.setBlurredBackground(blurredBackgroundDrawableCreate);

        filterTabsView.setColors(Theme.key_actionBarTabLine, Theme.key_actionBarTabActiveText, Theme.key_actionBarTabUnactiveText, Theme.key_actionBarTabSelector, Theme.key_actionBarDefault);
        filterTabsView.setDelegate(new FilterTabsView.FilterTabsViewDelegate() {
            @Override
            public void onPageSelected(FilterTabsView.Tab tab, boolean forward) {
            }

            @Override
            public void onPageScrolled(float progress) {
            }

            @Override
            public void onSamePageSelected() {
            }

            @Override
            public int getTabCounter(int tabId) {
                if (NaConfig.INSTANCE.getIgnoreFolderCount().Bool()) {
                    return 0;
                }
                Integer count = idsWithCounters.get(tabId);
                return count != null ? count : 0;
            }

            @Override
            public boolean didSelectTab(FilterTabsView.TabView tabView, boolean selected) {
                return false;
            }

            @Override
            public boolean isTabMenuVisible() {
                return false;
            }

            @Override
            public void onDeletePressed(int id) {
            }

            @Override
            public void onPageReorder(int fromId, int toId) {
            }

            @Override
            public boolean canPerformActions() {
                return false;
            }
        });

        addView(filterTabsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 50.0f, Gravity.CENTER, 12.0f, 0.0f, 12.0f, 0.0f));
        updateTabs(false);
    }

    public void updateTabs(boolean animated) {
        filterTabsView.resetTabId();
        filterTabsView.removeTabs();
        idsWithCounters.clear();

        ArrayList<MessagesController.DialogFilter> dialogFilters = MessagesController.getInstance(currentAccount).getDialogFilters();
        int count = 0;
        int firstTabId = -1;

        if (dialogFilters != null) {
            for (int i = 0; i < dialogFilters.size(); i++) {
                MessagesController.DialogFilter dialogFilter = dialogFilters.get(i);
                if (dialogFilter.isDefault() && NekoConfig.hideAllTab.Bool()) {
                    continue;
                }
                if (count == 0 || count == 1 || count == 3) {
                    idsWithCounters.put(dialogFilter.id, counterSeed / (count + 1));
                }
                if (firstTabId == -1) {
                    firstTabId = dialogFilter.id;
                }
                if (dialogFilter.isDefault()) {
                    filterTabsView.addTab(dialogFilter.id, 0, LocaleController.getString(R.string.FilterAllChats), "\uD83D\uDCAC", null, false, true, dialogFilter.locked);
                } else {
                    String emoticon = dialogFilter.emoticon != null ? dialogFilter.emoticon : "\uD83D\uDCC1";
                    filterTabsView.addTab(dialogFilter.id, dialogFilter.localId, dialogFilter.name, emoticon, dialogFilter.entities, dialogFilter.title_noanimate, false, dialogFilter.locked);
                }
                count++;
            }
        }

        if (count < 6) {
            int[] dummyIds = {100, 101, 102, 103};
            String[] dummyTitles = {
                    LocaleController.getString(R.string.FilterContacts),
                    LocaleController.getString(R.string.FilterGroups),
                    LocaleController.getString(R.string.FilterChannels),
                    LocaleController.getString(R.string.FilterBots)
            };
            String[] dummyEmoticons = {"\uD83D\uDC64", "\uD83D\uDC65", "\uD83D\uDCE2", "\uD83E\uDD16"};

            int totalSize = dialogFilters != null ? dialogFilters.size() : 0;
            for (int i = 0; i < dummyIds.length && count < 6; i++) {
                int total = totalSize + i;
                if (total == 0 || total == 1 || total == 3) {
                    idsWithCounters.put(dummyIds[i], counterSeed / (total + 1));
                }
                if (firstTabId == -1) {
                    firstTabId = dummyIds[i];
                }
                filterTabsView.addTab(dummyIds[i], dummyIds[i], dummyTitles[i], dummyEmoticons[i], null, false, false, false);
                count++;
            }
        }

        filterTabsView.finishAddingTabs(animated);
        if (firstTabId != -1) {
            filterTabsView.selectTabWithId(firstTabId, 1.0f);
        } else {
            filterTabsView.selectFirstTab();
        }
    }

    public void updateColors() {
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        if (blurredBackgroundSourceColor != null) {
            blurredBackgroundSourceColor.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        if (filterTabsView != null) {
            filterTabsView.setColors(Theme.key_actionBarTabLine, Theme.key_actionBarTabActiveText, Theme.key_actionBarTabUnactiveText, Theme.key_actionBarTabSelector, Theme.key_actionBarDefault);
            filterTabsView.updateColors();
        }
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.dialogFiltersUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NekoConfig.preferences.registerOnSharedPreferenceChangeListener(preferencesListener);
        updateTabs(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.dialogFiltersUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        NekoConfig.preferences.unregisterOnSharedPreferenceChangeListener(preferencesListener);
        removeCallbacks(refreshRunnable);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogFiltersUpdated) {
            updateTabs(true);
        } else if (id == NotificationCenter.didSetNewTheme) {
            updateColors();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), View.MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
}
