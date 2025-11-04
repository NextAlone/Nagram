package tw.nekomimi.nekogram.transtale.popupwrapper;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupSwipeBackLayout;

import tw.nekomimi.nekogram.DialogConfig;

public class ShareTargetPopupWrapper {

    public ActionBarPopupWindow.ActionBarPopupWindowLayout windowLayout;
    private final long dialogId;
    private final ActionBarMenuSubItem defaultItem;
    private final ActionBarMenuSubItem enableItem;
    private final ActionBarMenuSubItem disableItem;

    public ShareTargetPopupWrapper(BaseFragment fragment, PopupSwipeBackLayout swipeBackLayout, long dialogId, Theme.ResourcesProvider resourcesProvider) {
        Context context = fragment.getParentActivity();
        windowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, resourcesProvider);
        windowLayout.setFitItems(true);
        this.dialogId = dialogId;

        if (swipeBackLayout != null) {
            var backItem = ActionBarMenuItem.addItem(windowLayout, R.drawable.msg_arrow_back, LocaleController.getString(R.string.Back), false, resourcesProvider);
            backItem.setOnClickListener(view -> swipeBackLayout.closeForeground());
        }

        defaultItem = ActionBarMenuItem.addItem(windowLayout, 0, LocaleController.getString(R.string.Default), true, resourcesProvider);

        defaultItem.setOnClickListener(view -> {
            DialogConfig.removeShareTargetConfig(dialogId);
            SharedConfig.rebuildDirectShare();
            updateItems();
        });
        defaultItem.setAlpha(1.0f);

        enableItem = ActionBarMenuItem.addItem(windowLayout, 0, LocaleController.getString(R.string.Enable), true, resourcesProvider);
        enableItem.setChecked(DialogConfig.hasCustomForumTabsConfig(dialogId) && DialogConfig.isCustomForumTabsEnable(dialogId));
        enableItem.setOnClickListener(view -> {
            DialogConfig.setShareTargetEnable(dialogId, true);
            SharedConfig.rebuildDirectShare();
            updateItems();
        });
        enableItem.setAlpha(1.0f);

        disableItem = ActionBarMenuItem.addItem(windowLayout, 0, LocaleController.getString(R.string.Disable), true, resourcesProvider);
        disableItem.setChecked(DialogConfig.hasCustomForumTabsConfig(dialogId) && !DialogConfig.isCustomForumTabsEnable(dialogId));
        disableItem.setOnClickListener(view -> {
            DialogConfig.setShareTargetEnable(dialogId, false);
            SharedConfig.rebuildDirectShare();
            updateItems();
        });
        disableItem.setAlpha(1.0f);
        updateItems();

        View gap = new FrameLayout(context);
        gap.setBackgroundColor(Theme.getColor(Theme.key_graySection, resourcesProvider));
        gap.setTag(R.id.fit_width_tag, 1);
        windowLayout.addView(gap, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 8));

        TextView textView = new TextView(context);
        textView.setTag(R.id.fit_width_tag, 1);
        textView.setPadding(AndroidUtilities.dp(13), AndroidUtilities.dp(8), AndroidUtilities.dp(13), AndroidUtilities.dp(8));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
        textView.setText(LocaleController.getString(R.string.DirectShareInfo));
        windowLayout.addView(textView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    public void updateItems() {
        defaultItem.setChecked(!DialogConfig.hasShareTargetConfig(dialogId));
        enableItem.setChecked(DialogConfig.hasShareTargetConfig(dialogId) && DialogConfig.isShareTargetEnable(dialogId));
        disableItem.setChecked(DialogConfig.hasShareTargetConfig(dialogId) && !DialogConfig.isShareTargetEnable(dialogId));
    }
}
