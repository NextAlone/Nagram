/*
 * Nagram - Inline Bot Rules edit / view activity.
 *
 * 列表式编辑页：继承 BaseNekoSettingsActivity，复用其 BlurContentView /
 * BlurredRecyclerView / BaseListAdapter 等基础设施。
 *
 *  - REMOTE: 只读查看（输入禁用，无 Done / Delete 菜单）。
 *  - LOCAL : 完整编辑（Done 保存 / Delete 删除，含正则语法校验）。
 */

package tw.nekomimi.nekogram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.SimpleActionBarMenu;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import tw.nekomimi.nekogram.helpers.InlineBotRulesRepository;
import tw.nekomimi.nekogram.helpers.InlineBotRulesRepository.RuleItem;
import tw.nekomimi.nekogram.helpers.InlineBotRulesRepository.Source;
import tw.nekomimi.nekogram.settings.BaseNekoSettingsActivity;
import tw.nekomimi.nekogram.utils.LocaleUtil;

public class InlineBotRulesEditActivity extends BaseNekoSettingsActivity {

    private static final int done_button = 1;
    private static final int delete_button = 2;

    private static final int TYPE_USERNAME_INPUT = 100;
    private static final int TYPE_REGEX_INPUT = 101;

    public interface Delegate {
        void onChanged();
    }

    private final Source source;
    private final int localIndex;
    private final RuleItem initial;

    private Delegate delegate;

    private View doneButton;

    private CharSequence usernameDraft = "";
    private CharSequence regexDraft = "";
    private CharSequence errorMessage = "";

    private int usernameHeaderRow;
    private int usernameInputRow;
    private int usernameShadowRow;
    private int regexHeaderRow;
    private int regexInputRow;
    private int helpInfoRow;
    private int errorInfoRow;

    public InlineBotRulesEditActivity() {
        this.source = Source.LOCAL;
        this.localIndex = -1;
        this.initial = null;
    }

    public InlineBotRulesEditActivity(RuleItem item) {
        this.source = item.source;
        this.localIndex = item.localIndex;
        this.initial = item;
        if (item.username != null) {
            this.usernameDraft = item.username;
        }
        if (item.rules != null && !item.rules.isEmpty()) {
            this.regexDraft = TextUtils.join("\n", item.rules);
        }
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    private boolean isReadOnly() {
        return source == Source.REMOTE;
    }

    private boolean isCreate() {
        return source == Source.LOCAL && localIndex == -1;
    }

    @Override
    protected String getActionBarTitle() {
        if (isReadOnly()) {
            return LocaleController.getString(R.string.InlineBotRulesView);
        } else if (isCreate()) {
            return LocaleController.getString(R.string.InlineBotRulesAdd);
        } else {
            return LocaleController.getString(R.string.InlineBotRulesEdit);
        }
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
                    onSaveClicked();
                } else if (id == delete_button) {
                    onDeleteClicked();
                }
            }
        });

        if (!isReadOnly()) {
            SimpleActionBarMenu menu = simpleActionBar.createMenu();
            doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_ab_done, AndroidUtilities.dp(56));
            doneButton.setContentDescription(LocaleController.getString(R.string.Done));
            if (!isCreate()) {
                View deleteBtn = menu.addItemWithWidth(delete_button, R.drawable.msg_delete, AndroidUtilities.dp(56));
                deleteBtn.setContentDescription(LocaleController.getString(R.string.Delete));
            }
            updateDoneEnabled();
        }

        return root;
    }

    @Override
    protected void updateRows() {
        super.updateRows();
        usernameHeaderRow = addRow();
        usernameInputRow = addRow();
        usernameShadowRow = addRow();
        regexHeaderRow = addRow();
        regexInputRow = addRow();
        helpInfoRow = addRow();
        errorInfoRow = addRow();
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        // 输入行不可点击；其余行也不需要交互。
    }

    private void updateDoneEnabled() {
        if (doneButton == null) return;
        boolean ok = !TextUtils.isEmpty(usernameDraft) && !TextUtils.isEmpty(regexDraft);
        doneButton.setEnabled(ok);
        doneButton.setAlpha(ok ? 1f : 0.5f);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearError() {
        if (!TextUtils.isEmpty(errorMessage)) {
            errorMessage = "";
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(errorInfoRow);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showError(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            errorMessage = LocaleUtil.INSTANCE.htmlToString("<b>" + msg + "</b>");
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(errorInfoRow);
            }
        }
        BulletinFactory.of(InlineBotRulesEditActivity.this)
                .createSimpleBulletin(R.raw.error,
                        LocaleController.getString(R.string.InlineBotRulesSaveError))
                .show();
    }

    private void onSaveClicked() {
        if (isReadOnly()) {
            finishFragment();
            return;
        }

        String username = usernameDraft.toString().trim();
        if (TextUtils.isEmpty(username)) {
            showError(LocaleController.getString(R.string.InlineBotRulesUsernameRequired));
            return;
        }

        String raw = regexDraft.toString();
        ArrayList<String> rules = new ArrayList<>();
        for (String line : raw.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (!TextUtils.isEmpty(trimmed)) rules.add(trimmed);
        }
        if (rules.isEmpty()) {
            showError(LocaleController.getString(R.string.InlineBotRulesRegexRequired));
            return;
        }

        for (String r : rules) {
            try {
                Pattern.compile(r);
            } catch (PatternSyntaxException e) {
                String errorText = e.getMessage();
                if (!TextUtils.isEmpty(errorText)) {
                    errorText = errorText.replace(r, "");
                }
                showError(errorText);
                return;
            }
        }

        boolean ok;
        if (isCreate()) {
            ok = InlineBotRulesRepository.addLocal(username, rules);
        } else {
            ok = InlineBotRulesRepository.updateLocal(localIndex, username, rules);
        }

        if (!ok) {
            showError(LocaleController.getString(R.string.InlineBotRulesSaveError));
            return;
        }

        if (delegate != null) delegate.onChanged();
        finishFragment();
    }

    private void onDeleteClicked() {
        if (isReadOnly() || isCreate()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.InlineBotRulesDeleteTitle));
        builder.setMessage(LocaleController.getString(R.string.InlineBotRulesDeleteMessage));
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), (dialog, which) -> {
            InlineBotRulesRepository.removeLocal(localIndex);
            if (delegate != null) delegate.onChanged();
            finishFragment();
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog dialog = builder.create();
        showDialog(dialog);
        android.widget.TextView btn = (android.widget.TextView) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btn != null) {
            btn.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    private static abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    private class ListAdapter extends BaseListAdapter {

        ListAdapter(Context context) {
            super(context);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            // 全部 cell 不参与 RecyclerListView 的点击/长按。
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == usernameHeaderRow || position == regexHeaderRow) {
                return TYPE_HEADER;
            }
            if (position == usernameShadowRow) {
                return TYPE_SHADOW;
            }
            if (position == helpInfoRow || position == errorInfoRow) {
                return TYPE_INFO_PRIVACY;
            }
            if (position == usernameInputRow) {
                return TYPE_USERNAME_INPUT;
            }
            if (position == regexInputRow) {
                return TYPE_REGEX_INPUT;
            }
            return TYPE_SHADOW;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_USERNAME_INPUT) {
                return new RecyclerListView.Holder(createUsernameCell(mContext));
            }
            if (viewType == TYPE_REGEX_INPUT) {
                return new RecyclerListView.Holder(createRegexCell(mContext));
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean partial, boolean divider) {
            int type = holder.getItemViewType();
            if (type == TYPE_HEADER) {
                HeaderCell cell = (HeaderCell) holder.itemView;
                if (position == usernameHeaderRow) {
                    cell.setText(LocaleController.getString(R.string.InlineBotRulesUsernameLabel));
                } else if (position == regexHeaderRow) {
                    cell.setText(LocaleController.getString(R.string.InlineBotRulesRegexLabel));
                }
            } else if (type == TYPE_INFO_PRIVACY) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                if (position == helpInfoRow) {
                    cell.setText(LocaleUtil.INSTANCE.htmlToString(
                            LocaleController.getString(R.string.InlineBotRulesEditDescription)));
                    cell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                } else if (position == errorInfoRow) {
                    cell.setText(errorMessage);
                    cell.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                }
            }
        }
    }

    private View createUsernameCell(Context context) {
        FrameLayout wrapper = new FrameLayout(context);
        wrapper.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        EditTextBoldCursor field = new EditTextBoldCursor(context);
        field.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        field.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        field.setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        field.setHintText(LocaleController.getString(R.string.InlineBotRulesUsernameHint));
        field.setSingleLine(true);
        field.setBackground(null);
        field.setEnabled(!isReadOnly());
        field.setFocusable(!isReadOnly());
        field.setText(usernameDraft);
        field.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                usernameDraft = s.toString();
                updateDoneEnabled();
                clearError();
            }
        });
        wrapper.addView(field, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL, 21, 12, 21, 12));

        wrapper.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return wrapper;
    }

    private View createRegexCell(Context context) {
        LinearLayout wrapper = new LinearLayout(context);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        EditTextBoldCursor field = new EditTextBoldCursor(context);
        field.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        field.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        field.setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        field.setHintText(LocaleController.getString(R.string.InlineBotRulesRegexHint));
        field.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        field.setBackground(null);
        field.setEnabled(!isReadOnly());
        field.setFocusable(!isReadOnly());
        field.setText(regexDraft);
        field.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                regexDraft = s.toString();
                updateDoneEnabled();
                clearError();
            }
        });
        wrapper.addView(field, LayoutHelper.createLinear(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL, 21, 12, 21, 12));

        wrapper.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return wrapper;
    }
}
