/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package tw.nekomimi.nekogram.ui;

import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

import tw.nekomimi.nekogram.helpers.AyuFilter;
import tw.nekomimi.nekogram.settings.RegexFiltersSettingActivity;


public class RegexFilterPopup {
    public static void show(RegexFiltersSettingActivity fragment, View anchorView, float touchedX, float touchedY, int filterIdx) {
        if (fragment.getFragmentView() == null) {
            return;
        }

        var layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(fragment.getContext());
        var popupWindow = new ActionBarPopupWindow(layout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        var windowLayout = createPopupLayout(layout, popupWindow, fragment, filterIdx);

        popupWindow.setPauseNotifications(true);
        popupWindow.setDismissAnimationDuration(220);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        popupWindow.setFocusable(true);
        windowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST));
        popupWindow.setInputMethodMode(ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED);
        popupWindow.getContentView().setFocusableInTouchMode(true);

        float x = touchedX, y = touchedY;
        View view = anchorView;
        while (view != fragment.getFragmentView()) {
            if (view.getParent() == null) {
                return;
            }
            x += view.getX();
            y += view.getY();
            view = (View) view.getParent();
        }
        x -= windowLayout.getMeasuredWidth() / 2f;
        y -= windowLayout.getMeasuredHeight() / 2f;
        popupWindow.showAtLocation(fragment.getFragmentView(), 0, (int) x, (int) y);
        popupWindow.dimBehind();
    }

    private static ActionBarPopupWindow.ActionBarPopupWindowLayout createPopupLayout(ActionBarPopupWindow.ActionBarPopupWindowLayout layout, ActionBarPopupWindow popupWindow, RegexFiltersSettingActivity fragment, int filterIdx) {
        layout.setFitItems(true);

        var editBtn = ActionBarMenuItem.addItem(layout, R.drawable.msg_edit, "Edit", false, fragment.getResourceProvider());
        editBtn.setOnClickListener(view -> {
            fragment.presentFragment(new RegexFilterEditActivity(filterIdx));
            popupWindow.dismiss();
        });

        var deleteBtn = ActionBarMenuItem.addItem(layout, R.drawable.msg_delete, "Delete", false, fragment.getResourceProvider());
        deleteBtn.setOnClickListener(view -> {
            AyuFilter.removeFilter(filterIdx);
            fragment.onResume();
            popupWindow.dismiss();
        });
        var deleteBtnColor = Theme.getColor(Theme.key_text_RedBold);
        deleteBtn.setColors(deleteBtnColor, deleteBtnColor);

        return layout;
    }
}
