package tw.nekomimi.nekogram.config.cell;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.util.function.Function;

import tw.nekomimi.nekogram.config.CellGroup;
import tw.nekomimi.nekogram.config.ConfigItem;

public class ConfigCellTextInput extends AbstractConfigCell {
    private final ConfigItem bindConfig;
    private final String hint;
    private final String title;
    private final Runnable onClickCustom;
    private final Function<String, String> inputChecker;

    public ConfigCellTextInput(String customTitle, ConfigItem bind, String hint, Runnable customOnClick) {
        this(customTitle, bind, hint, customOnClick, null);
    }

    // default: customTitle=null customOnClick=null
    public ConfigCellTextInput(String customTitle, ConfigItem bind, String hint, Runnable customOnClick, Function<String, String> inputChecker) {
        this.bindConfig = bind;
        if (hint == null) {
            this.hint = "";
        } else {
            this.hint = hint;
        }
        if (customTitle == null) {
            title = LocaleController.getString(bindConfig.getKey());
        } else {
            title = customTitle;
        }
        this.onClickCustom = customOnClick;
        this.inputChecker = inputChecker;
    }

    public int getType() {
        return CellGroup.ITEM_TYPE_TEXT_SETTINGS_CELL;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder) {
        TextSettingsCell cell = (TextSettingsCell) holder.itemView;
        cell.setTextAndValue(title, bindConfig.String(), cellGroup.needSetDivider(this));
    }

    public void onClick() {
        if (onClickCustom != null) {
            try {
                onClickCustom.run();
            } catch (Exception e) {
            }
            return;
        }

        Context context = cellGroup.thisFragment.getParentActivity();
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(bindConfig.getKey()));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        EditTextBoldCursor editText = new EditTextBoldCursor(context);
        editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        editText.setHint(hint);
        editText.setText(bindConfig.String());
        linearLayout.addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(10), 0));

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (d, v) -> {
            String newV = editText.getText().toString();
            if (this.inputChecker != null)
                newV = this.inputChecker.apply(newV);
            bindConfig.setConfigString(newV);

            //refresh
            cellGroup.listAdapter.notifyItemChanged(cellGroup.rows.indexOf(this));
            builder.getDismissRunnable().run();
            cellGroup.thisFragment.getParentLayout().rebuildAllFragmentViews(false, false);

            cellGroup.runCallback(bindConfig.getKey(), newV);
        });
        builder.setView(linearLayout);
        cellGroup.thisFragment.showDialog(builder.create());
    }
}

