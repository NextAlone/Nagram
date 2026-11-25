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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Components.ItemOptions;

import tw.nekomimi.nekogram.helpers.AyuFilter;
import tw.nekomimi.nekogram.settings.RegexFiltersSettingActivity;


public class RegexFilterPopup {
    public static void show(RegexFiltersSettingActivity fragment, View anchorView, float touchedX, float touchedY, int filterIdx) {
        if (fragment.getFragmentView() == null) {
            return;
        }

        ItemOptions options = ItemOptions.makeOptions(fragment, anchorView);
        options.add(R.drawable.msg_edit, LocaleController.getString(R.string.Edit), () -> {
            fragment.presentFragment(new RegexFilterEditActivity(filterIdx));
        });
        options.add(R.drawable.msg_delete, LocaleController.getString(R.string.Delete), () -> {
            AyuFilter.removeFilter(filterIdx);
            fragment.onResume();
        });
        options.show();
    }
}
