/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package tw.nekomimi.nekogram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.SimpleActionBarMenu;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import tw.nekomimi.nekogram.helpers.AyuFilter;
import tw.nekomimi.nekogram.settings.BaseNekoSettingsActivity;
import tw.nekomimi.nekogram.utils.LocaleUtil;


/**
 * 正则过滤器编辑页。
 * <p>
 * 继承 {@link BaseNekoSettingsActivity} 使用 RecyclerListView 承载内容，
 * 正则过长时整页可以滚动，而不是像之前那样被固定高度的 LinearLayout 截断。
 */
public class RegexFilterEditActivity extends BaseNekoSettingsActivity {

    private static final int done_button = 1;

    private static final int TYPE_REGEX_INPUT = 100;

    private final int filterIdx;
    private final AyuFilter.FilterModel filterModel;
    private boolean caseInsensitive;

    private View doneButton;
    private EditTextBoldCursor regexField;

    private CharSequence regexDraft = "";
    private CharSequence errorMessage = "";

    private int regexHeaderRow;
    private int regexInputRow;
    private int regexShadowRow;
    private int caseInsensitiveRow;
    private int endShadowRow;
    private int helpInfoRow;
    private int errorInfoRow;

    public RegexFilterEditActivity() {
        filterIdx = -1;
        filterModel = null;
        caseInsensitive = true;
    }

    public RegexFilterEditActivity(int filterIdx) {
        this.filterIdx = filterIdx; // use -1 to CREATE, not EDIT
        this.filterModel = AyuFilter.getRegexFilters().get(filterIdx);
        this.caseInsensitive = filterModel.caseInsensitive;
        if (!TextUtils.isEmpty(filterModel.regex)) {
            this.regexDraft = filterModel.regex;
        }
    }

    @Override
    protected String getActionBarTitle() {
        return LocaleController.getString(filterIdx == -1 ? R.string.RegexFiltersAdd : R.string.RegexFiltersEdit);
    }

    @Override
    public View createView(Context context) {
        View root = super.createView(context);

        simpleActionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == done_button) {
                    onDoneClicked();
                }
            }
        });

        SimpleActionBarMenu menu = simpleActionBar.createMenu();
        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_ab_done, AndroidUtilities.dp(56));
        doneButton.setContentDescription(LocaleController.getString(R.string.Done));
        updateDoneEnabled();

        return root;
    }

    @Override
    protected void updateRows() {
        super.updateRows();
        regexHeaderRow = addRow();
        regexInputRow = addRow();
        regexShadowRow = addRow();
        caseInsensitiveRow = addRow();
        endShadowRow = addRow();
        helpInfoRow = addRow();
        errorInfoRow = addRow();
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == caseInsensitiveRow) {
            TextCheckCell cell = (TextCheckCell) view;
            boolean checked = !cell.isChecked();
            cell.setChecked(checked);
            caseInsensitive = checked;
        }
    }

    private void updateDoneEnabled() {
        if (doneButton == null) {
            return;
        }
        boolean enabled = !TextUtils.isEmpty(regexDraft);
        doneButton.setEnabled(enabled);
        doneButton.setAlpha(enabled ? 1f : 0.5f);
    }

    private void focusRegexField() {
        if (regexField == null) {
            return;
        }
        regexField.post(() -> {
            if (regexField != null) {
                regexField.requestFocus();
                AndroidUtilities.showKeyboard(regexField);
            }
        });
    }

    private void clearError() {
        if (!TextUtils.isEmpty(errorMessage)) {
            errorMessage = "";
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(errorInfoRow);
            }
        }
    }

    private void showError(CharSequence message) {
        errorMessage = message;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(errorInfoRow);
        }
    }

    private void onDoneClicked() {
        String text = regexDraft.toString();

        if (TextUtils.isEmpty(text)) {
            return;
        }

        try {
            Pattern.compile(text);
        } catch (PatternSyntaxException e) {
            var errorText = e.getMessage();
            if (!TextUtils.isEmpty(errorText)) {
                errorText = errorText.replace(text, "");
            }

            showError(LocaleUtil.INSTANCE.htmlToString("<b>" + errorText + "</b>"));
            BulletinFactory.of(RegexFilterEditActivity.this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.RegexFiltersAddError)).show();
            return;
        }

        // 复杂度检查需要实测匹配耗时，放在后台线程执行，避免卡住界面
        new Thread(() -> {
            var pathological = AyuFilter.detectPathologicalRegex(text, caseInsensitive);
            AndroidUtilities.runOnUIThread(() -> {
                if (pathological != null) {
                    var message = LocaleController.getString("timeout".equals(pathological)
                            ? R.string.RegexFiltersTooComplexTimeout
                            : R.string.RegexFiltersTooComplex);
                    showError(LocaleUtil.INSTANCE.htmlToString("<b>" + message + "</b>"));
                    BulletinFactory.of(RegexFilterEditActivity.this).createSimpleBulletin(R.raw.error, message).show();
                    return;
                }

                if (filterIdx == -1) {
                    AyuFilter.addFilter(text, caseInsensitive);
                } else {
                    AyuFilter.editFilter(filterIdx, text, caseInsensitive);
                }

                finishFragment();
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean animations = preferences.getBoolean("view_animations", true);
        if (!animations) {
            focusRegexField();
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            focusRegexField();
        }
    }

    private class ListAdapter extends BaseListAdapter {

        ListAdapter(Context context) {
            super(context);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            // 只有“不区分大小写”一行可点击，输入框由 EditText 自己处理焦点与点击
            return holder.getItemViewType() == TYPE_CHECK;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == regexHeaderRow) {
                return TYPE_HEADER;
            }
            if (position == regexShadowRow || position == endShadowRow) {
                return TYPE_SHADOW;
            }
            if (position == caseInsensitiveRow) {
                return TYPE_CHECK;
            }
            if (position == helpInfoRow || position == errorInfoRow) {
                return TYPE_INFO_PRIVACY;
            }
            if (position == regexInputRow) {
                return TYPE_REGEX_INPUT;
            }
            return TYPE_SHADOW;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_REGEX_INPUT) {
                return new RecyclerListView.Holder(createRegexInputCell(mContext));
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean partial, boolean divider) {
            switch (holder.getItemViewType()) {
                case TYPE_HEADER:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString(R.string.RegexFiltersHeader));
                    break;
                case TYPE_CHECK: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    cell.setTextAndCheck(LocaleController.getString(R.string.RegexFiltersCaseInsensitive), caseInsensitive, divider);
                    break;
                }
                case TYPE_INFO_PRIVACY: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == helpInfoRow) {
                        cell.setText(LocaleUtil.INSTANCE.htmlToString(LocaleController.getString(R.string.RegexFiltersAddDescription)));
                        cell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
                    } else if (position == errorInfoRow) {
                        cell.setText(errorMessage);
                        cell.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                    }
                    break;
                }
            }
        }
    }

    private View createRegexInputCell(Context context) {
        FrameLayout wrapper = new FrameLayout(context);
        wrapper.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        regexField = new EditTextBoldCursor(context);
        regexField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        regexField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        regexField.setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        regexField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        regexField.setBackground(null);
        regexField.setText(regexDraft);
        regexField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                regexDraft = s.toString();
                updateDoneEnabled();
                clearError();
            }
        });
        wrapper.addView(regexField, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 21, 12, 21, 12));

        wrapper.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return wrapper;
    }
}
