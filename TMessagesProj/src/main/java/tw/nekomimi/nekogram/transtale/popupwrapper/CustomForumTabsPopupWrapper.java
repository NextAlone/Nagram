package tw.nekomimi.nekogram.transtale.popupwrapper;

import android.content.Context;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.PopupSwipeBackLayout;

import tw.nekomimi.nekogram.DialogConfig;

public class CustomForumTabsPopupWrapper {

    public ActionBarPopupWindow.ActionBarPopupWindowLayout windowLayout;
    private final long dialogId;
    private final ActionBarMenuSubItem defaultItem;
    private final ActionBarMenuSubItem tabsItem;
    private final ActionBarMenuSubItem listItem;
    private final ActionBarMenuSubItem sidebarItem;

    public CustomForumTabsPopupWrapper(BaseFragment fragment, PopupSwipeBackLayout swipeBackLayout, long dialogId, Theme.ResourcesProvider resourcesProvider) {
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
            DialogConfig.removeCustomForumTabsConfig(dialogId);
            updateItems();
        });
        defaultItem.setAlpha(1.0f);

        tabsItem = ActionBarMenuItem.addItem(windowLayout, 0, LocaleController.getString(R.string.TopicsLayoutTabs), true, resourcesProvider);
        tabsItem.setOnClickListener(view -> {
            DialogConfig.setCustomForumTabsMode(dialogId, DialogConfig.FORUM_LAYOUT_TABS);
            updateItems();
        });
        tabsItem.setAlpha(1.0f);

        listItem = ActionBarMenuItem.addItem(windowLayout, 0, LocaleController.getString(R.string.TopicsLayoutList), true, resourcesProvider);
        listItem.setOnClickListener(view -> {
            DialogConfig.setCustomForumTabsMode(dialogId, DialogConfig.FORUM_LAYOUT_LIST);
            updateItems();
        });
        listItem.setAlpha(1.0f);

        sidebarItem = ActionBarMenuItem.addItem(windowLayout, 0, LocaleController.getString(R.string.TopicsSidebar), true, resourcesProvider);
        sidebarItem.setOnClickListener(view -> {
            DialogConfig.setCustomForumTabsMode(dialogId, DialogConfig.FORUM_LAYOUT_SIDEBAR);
            updateItems();
        });
        sidebarItem.setAlpha(1.0f);
        updateItems();
    }

    public void updateItems() {
        final int mode = DialogConfig.getCustomForumTabsMode(dialogId);
        defaultItem.setChecked(mode == DialogConfig.FORUM_LAYOUT_DEFAULT);
        tabsItem.setChecked(mode == DialogConfig.FORUM_LAYOUT_TABS);
        listItem.setChecked(mode == DialogConfig.FORUM_LAYOUT_LIST);
        sidebarItem.setChecked(mode == DialogConfig.FORUM_LAYOUT_SIDEBAR);
    }
}
