/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package tw.nekomimi.nekogram.proxy;

import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.hutool.core.util.StrUtil;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.UIUtil;

public class SubSettingsActivity extends BaseFragment {

    private EditText[] inputFields;

    private EditText urlsField;
    private EditTextBoldCursor remarksField;

    private ScrollView scrollView;
    private LinearLayout linearLayout2;
    private LinearLayout inputFieldsContainer;

    private SubInfo subInfo;

    private boolean ignoreOnTextChange;

    private static int done_button = 1;
    private static int menu_delete = 2;

    public SubSettingsActivity() {
        super();
        subInfo = new SubInfo();
        subInfo.id = 0L;
    }

    public SubSettingsActivity(SubInfo subInfo) {
        super();
        this.subInfo = subInfo;
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
    }

    @Override
    public View createView(Context context) {
        actionBar.setTitle(LocaleController.getString("ProxySubDetails", R.string.ProxySubDetails));
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == done_button) {

                    if (getParentActivity() == null) {
                        return;
                    }

                    if (StrUtil.isBlank(urlsField.getText())) {

                        urlsField.requestFocus();
                        AndroidUtilities.showKeyboard(urlsField);

                        return;

                    }

                    if (StrUtil.isBlank(remarksField.getText())) {

                        remarksField.requestFocus();
                        AndroidUtilities.showKeyboard(remarksField);

                        return;

                    }

                    subInfo.urls = ArraysKt.toList(urlsField.getText().toString().split("\n"));
                    subInfo.name = remarksField.getText().toString();

                    doGetProxies();

                } else if (id == menu_delete) {

                    AlertUtil.showConfirm(getParentActivity(),
                            LocaleController.getString("SubscriptionDelete", R.string.SubscriptionDelete),
                            R.drawable.baseline_delete_24, LocaleController.getString("Delete", R.string.Delete),
                            true, () -> {

                                AlertDialog pro = AlertUtil.showProgress(getParentActivity());

                                pro.show();

                                UIUtil.runOnIoDispatcher(() -> {

                                    SubManager.getSubList().remove(subInfo);

                                    SharedConfig.reloadProxyList();

                                    UIUtil.runOnUIThread(() -> {

                                        pro.dismiss();

                                        finishFragment();

                                    });

                                });

                            });


                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();

        if (subInfo.id != 0) {

            menu.addItem(menu_delete, R.drawable.baseline_delete_24, AndroidUtilities.dp(56)).setContentDescription(LocaleController.getString("Delete", R.string.Delete));

        }

        menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56)).setContentDescription(LocaleController.getString("Done", R.string.Done));

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout2, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        inputFieldsContainer = new LinearLayout(context);
        inputFieldsContainer.setOrientation(LinearLayout.VERTICAL);
        inputFieldsContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // bring to front for transitions
            inputFieldsContainer.setElevation(AndroidUtilities.dp(1f));
            inputFieldsContainer.setOutlineProvider(null);
        }
        linearLayout2.addView(inputFieldsContainer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        inputFields = new EditText[2];

        urlsField = new EditText(context);
        inputFields[0] = urlsField;
        urlsField.setImeOptions(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        urlsField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        urlsField.setHint(LocaleController.getString("SubscriptionUrls", R.string.SubscriptionUrls));
        urlsField.setText(CollectionsKt.joinToString(subInfo.urls, "\n", "", "", -1, "", null));
        urlsField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        urlsField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        urlsField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        urlsField.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        urlsField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        urlsField.setSingleLine(false);
        urlsField.setMinLines(6);

        inputFieldsContainer.addView(urlsField, LayoutHelper.createLinear(-1, -2, 17, 0, 17, 0));

        FrameLayout container = new FrameLayout(context);

        remarksField = mkCursor();
        inputFields[1] = remarksField;
        remarksField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        remarksField.setHintText(LocaleController.getString("ProxyRemarks", R.string.ProxyRemarks));
        remarksField.setText(subInfo.name);
        remarksField.setSelection(remarksField.length());

        container.addView(remarksField, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 17, 0, 17, 0));

        inputFieldsContainer.addView((View) remarksField.getParent(), LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 64));

        return fragmentView;

    }

    EditTextBoldCursor mkCursor() {

        EditTextBoldCursor cursor = new EditTextBoldCursor(getParentActivity());
        cursor.setSingleLine(true);
        cursor.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        cursor.setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        cursor.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        cursor.setBackground(null);
        cursor.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        cursor.setCursorSize(AndroidUtilities.dp(20));
        cursor.setCursorWidth(1.5f);
        cursor.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        cursor.setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        cursor.setTransformHintToHeader(true);
        cursor.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
        return cursor;

    }

    public void doGetProxies() {

        AlertDialog pro = AlertUtil.showProgress(getParentActivity(), LocaleController.getString("SubscriptionUpdating", R.string.SubscriptionUpdating));

        AtomicBoolean canceled = new AtomicBoolean();

        pro.setOnCancelListener((it) -> {

            canceled.set(true);

        });

        pro.show();

        UIUtil.runOnIoDispatcher(() -> {

            try {

                subInfo.proxies = subInfo.reloadProxies();
                subInfo.lastFetch = System.currentTimeMillis();

            } catch (IOException allTriesFailed) {

                if (canceled.get()) return;

                UIUtil.runOnUIThread(pro::dismiss);

                AlertUtil.showSimpleAlert(getParentActivity(), "tries failed: " + allTriesFailed.toString().trim());

                return;

            }

            if (subInfo.id == 0) subInfo.id = SubManager.getCount() + 10;

            do {

                try {
                    SubManager.getSubList().update(subInfo, true);
                    break;
                } catch (Exception ignored) {
                }
                subInfo.id ++;

            } while (true);

            SharedConfig.reloadProxyList();

            UIUtil.runOnUIThread(() -> {

                pro.dismiss();

                finishFragment();

            });

        });

    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        final ThemeDescription.ThemeDescriptionDelegate delegate = () -> {
            if (inputFields != null) {
                for (int i = 0; i < inputFields.length; i++) {
                    inputFields[i].setText(Theme.getColor(Theme.key_windowBackgroundWhiteInputField));
                }
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(inputFieldsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, delegate, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, delegate, Theme.key_windowBackgroundWhiteGrayText2));

        if (inputFields != null) {
            for (int a = 0; a < inputFields.length; a++) {
                arrayList.add(new ThemeDescription(inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
                arrayList.add(new ThemeDescription(inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
                arrayList.add(new ThemeDescription(inputFields[a], ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_windowBackgroundWhiteInputField));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_windowBackgroundWhiteInputFieldActivated));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_windowBackgroundWhiteRedText3));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }

        return arrayList;
    }
}
