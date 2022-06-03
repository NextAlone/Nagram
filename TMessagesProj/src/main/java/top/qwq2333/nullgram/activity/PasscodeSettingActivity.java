package top.qwq2333.nullgram.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PasscodeActivity;

import java.util.ArrayList;
import java.util.Locale;

import top.qwq2333.nullgram.helpers.PasscodeHelper;

public class PasscodeSettingActivity extends BaseActivity {
    private boolean passcodeSet;

    private int showInSettingsRow;
    private int showInSettings2Row;

    private int accountsStartRow;
    private int accountsEndRow;

    private int panicCodeRow;
    private int setPanicCodeRow;
    private int removePanicCodeRow;
    private int panicCode2Row;

    private int clearPasscodesRow;
    private int clearPasscodes2Row;

    private final ArrayList<Integer> accounts = new ArrayList<>();

    @Override
    public boolean onFragmentCreate() {
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            var u = AccountInstance.getInstance(a).getUserConfig().getCurrentUser();
            if (u != null) {
                accounts.add(a);
            }
        }
        return super.onFragmentCreate();
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (!passcodeSet) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("PasscodeNeeded", R.string.PasscodeNeeded)).show();
            return;
        }
        if (position > accountsStartRow && position < accountsEndRow) {
            var account = accounts.get(position - accountsStartRow - 1);
            var builder = new AlertDialog.Builder(getParentActivity());

            var linearLayout = new LinearLayout(getParentActivity());
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            if (PasscodeHelper.hasPasscodeForAccount(account)) {
                TextCheckCell hideAccount = new TextCheckCell(getParentActivity(), 23, true);
                hideAccount.setTextAndCheck(LocaleController.getString("PasscodeHideAccount", R.string.PasscodeHideAccount), PasscodeHelper.isAccountHidden(account), false);
                hideAccount.setOnClickListener(view13 -> {
                    boolean hide = !hideAccount.isChecked();
                    PasscodeHelper.setHideAccount(account, hide);
                    hideAccount.setChecked(hide);
                    getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                });
                hideAccount.setBackground(Theme.getSelectorDrawable(false));
                linearLayout.addView(hideAccount, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            }

            TextCheckCell allowPanic = new TextCheckCell(getParentActivity(), 23, true);
            allowPanic.setTextAndCheck(LocaleController.getString("PasscodeAllowPanic", R.string.PasscodeAllowPanic), PasscodeHelper.isAccountAllowPanic(account), false);
            allowPanic.setOnClickListener(view13 -> {
                boolean hide = !allowPanic.isChecked();
                PasscodeHelper.setAccountAllowPanic(account, hide);
                allowPanic.setChecked(hide);
            });
            allowPanic.setBackground(Theme.getSelectorDrawable(false));
            linearLayout.addView(allowPanic, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            AlertDialog.AlertDialogCell editPasscode = new AlertDialog.AlertDialogCell(getParentActivity(), null);
            editPasscode.setTextAndIcon(PasscodeHelper.hasPasscodeForAccount(account) ? LocaleController.getString("PasscodeEdit", R.string.PasscodeEdit) : LocaleController.getString("PasscodeSet", R.string.PasscodeSet), 0);
            editPasscode.setOnClickListener(view1 -> {
                builder.getDismissRunnable().run();
                presentFragment(new PasscodeActivity(PasscodeActivity.TYPE_SETUP_CODE, account));
            });
            editPasscode.setBackground(Theme.getSelectorDrawable(false));
            linearLayout.addView(editPasscode, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

            if (PasscodeHelper.hasPasscodeForAccount(account)) {
                AlertDialog.AlertDialogCell removePasscode = new AlertDialog.AlertDialogCell(getParentActivity(), null);
                removePasscode.setTextAndIcon(LocaleController.getString("PasscodeRemove", R.string.PasscodeRemove), 0);
                removePasscode.setOnClickListener(view12 -> {
                    AlertDialog alertDialog = new AlertDialog.Builder(getParentActivity())
                        .setTitle(LocaleController.getString(R.string.PasscodeRemove))
                        .setMessage(LocaleController.getString(R.string.PasscodeRemoveConfirmMessage))
                        .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
                        .setPositiveButton(LocaleController.getString(R.string.DisablePasscodeTurnOff), (dialog, which) -> {
                            var hidden = PasscodeHelper.isAccountHidden(account);
                            PasscodeHelper.removePasscodeForAccount(account);
                            listAdapter.notifyItemChanged(position);
                            if (hidden) {
                                getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                            }
                        }).create();
                    showDialog(alertDialog);
                    ((TextView) alertDialog.getButton(Dialog.BUTTON_POSITIVE)).setTextColor(Theme.getColor(Theme.key_dialogTextRed));
                });
                removePasscode.setBackground(Theme.getSelectorDrawable(false));
                linearLayout.addView(removePasscode, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            }

            builder.setView(linearLayout);
            showDialog(builder.create());
        } else if (position == clearPasscodesRow) {
            PasscodeHelper.clearAll();
            finishFragment();
        } else if (position == setPanicCodeRow) {
            presentFragment(new PasscodeActivity(PasscodeActivity.TYPE_SETUP_CODE, Integer.MAX_VALUE));
        } else if (position == removePanicCodeRow) {
            AlertDialog alertDialog = new AlertDialog.Builder(getParentActivity())
                .setTitle(LocaleController.getString(R.string.PasscodePanicCodeRemove))
                .setMessage(LocaleController.getString(R.string.PasscodePanicCodeRemoveConfirmMessage))
                .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
                .setPositiveButton(LocaleController.getString(R.string.DisablePasscodeTurnOff), (dialog, which) -> {
                    PasscodeHelper.removePasscodeForAccount(Integer.MAX_VALUE);
                    listAdapter.notifyItemChanged(setPanicCodeRow);
                    listAdapter.notifyItemRemoved(removePanicCodeRow);
                    updateRows();
                }).create();
            showDialog(alertDialog);
            ((TextView) alertDialog.getButton(Dialog.BUTTON_POSITIVE)).setTextColor(Theme.getColor(Theme.key_dialogTextRed));
        } else if (position == showInSettingsRow) {
            PasscodeHelper.setHideSettings(!PasscodeHelper.isSettingsHidden());
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!PasscodeHelper.isSettingsHidden());
            }
        }
    }

    @Override
    protected boolean onItemLongClick(View view, int position, float x, float y) {
        return false;
    }

    @Override
    protected String getKey() {
        return PasscodeHelper.getSettingsKey();
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    protected String getActionBarTitle() {
        return LocaleController.getString("Passcode1", R.string.Passcode1);
    }

    @Override
    public void onResume() {
        passcodeSet = SharedConfig.passcodeHash.length() > 0;
        if (!passcodeSet) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("PasscodeNeeded", R.string.PasscodeNeeded)).show();
        }
        updateRows();
        super.onResume();
    }

    @Override
    protected void updateRows() {
        super.updateRows();

        showInSettingsRow = rowCount++;
        showInSettings2Row = rowCount++;

        accountsStartRow = rowCount++;
        rowCount += accounts.size();
        accountsEndRow = rowCount++;

        panicCodeRow = rowCount++;
        setPanicCodeRow = rowCount++;
        if (!PasscodeHelper.hasPanicCode()) {
            removePanicCodeRow = -1;
        } else {
            removePanicCodeRow = rowCount++;
        }
        panicCode2Row = rowCount++;

        clearPasscodesRow = -1;
        clearPasscodes2Row = -1;
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    if (position == clearPasscodes2Row) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
                case 2: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setCanDisable(true);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position == setPanicCodeRow) {
                        textCell.setText(PasscodeHelper.hasPanicCode() ? LocaleController.getString("PasscodePanicCodeEdit", R.string.PasscodePanicCodeEdit) : LocaleController.getString("PasscodePanicCodeSet", R.string.PasscodePanicCodeSet), removePanicCodeRow != -1);
                    } else if (position == clearPasscodesRow) {
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                        textCell.setText("Clear passcodes", false);
                    } else if (position == removePanicCodeRow) {
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                        textCell.setText(LocaleController.getString("PasscodePanicCodeRemove", R.string.PasscodePanicCodeRemove), false);
                    }
                    break;
                }
                case 3: {
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    textCell.setEnabled(passcodeSet, null);
                    if (position == showInSettingsRow) {
                        textCell.setTextAndCheck(LocaleController.getString("PasscodeShowInSettings", R.string.PasscodeShowInSettings), !PasscodeHelper.isSettingsHidden(), false);
                    }
                    break;
                }
                case 4: {
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    cell.setEnabled(passcodeSet, null);
                    if (position == accountsStartRow) {
                        cell.setText(LocaleController.getString("Account", R.string.Account));
                    } else if (position == panicCodeRow) {
                        cell.setText(LocaleController.getString("PasscodePanicCode", R.string.PasscodePanicCode));
                    }
                    break;
                }
                case 7: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    cell.setEnabled(passcodeSet, null);
                    cell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    if (position == accountsEndRow) {
                        cell.setText(LocaleController.getString("PasscodeAbout", R.string.PasscodeAbout));
                    } else if (position == panicCode2Row) {
                        cell.setText(LocaleController.getString("PasscodePanicCodeAbout", R.string.PasscodePanicCodeAbout));
                        if (clearPasscodesRow == -1) {
                            cell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        }
                    } else if (position == showInSettings2Row) {
                        var link = String.format(Locale.ENGLISH, "https://t.me/nullsettings/%s", PasscodeHelper.getSettingsKey());
                        var stringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.getString("PasscodeShowInSettingsAbout", R.string.PasscodeShowInSettingsAbout)));
                        stringBuilder.append("\n").append(link);
                        stringBuilder.setSpan(new URLSpanNoUnderline(null) {
                            @Override
                            public void onClick(@NonNull View view) {
                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ApplicationLoader.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                android.content.ClipData clip = android.content.ClipData.newPlainText("label", link);
                                clipboard.setPrimaryClip(clip);
                                BulletinFactory.of(PasscodeSettingActivity.this).createCopyLinkBulletin().show();
                            }
                        }, stringBuilder.length() - link.length(), stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        cell.setText(stringBuilder);
                    }
                    break;
                }
                case 11: {
                    AccountCell cell = (AccountCell) holder.itemView;
                    cell.setEnabled(passcodeSet);
                    int account = accounts.get(position - accountsStartRow - 1);
                    cell.setAccount(account, PasscodeHelper.hasPasscodeForAccount(account), position + 1 != accountsEndRow);
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return passcodeSet && super.isEnabled(holder);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == clearPasscodes2Row) {
                return 1;
            } else if (position == clearPasscodesRow || position == setPanicCodeRow || position == removePanicCodeRow) {
                return 2;
            } else if (position == showInSettingsRow) {
                return 3;
            } else if (position == accountsStartRow || position == panicCodeRow) {
                return 4;
            } else if (position == showInSettings2Row || position == accountsEndRow || position == panicCode2Row) {
                return 7;
            } else if (position > accountsStartRow && position < accountsEndRow) {
                return 11;
            }
            return 2;
        }
    }


    public static class AccountCell extends FrameLayout {

        private final TextView textView;
        private final BackupImageView imageView;
        private final ImageView checkImageView;
        private final AvatarDrawable avatarDrawable;
        private boolean needDivider;

        private int accountNumber;

        public AccountCell(Context context) {
            super(context);

            avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12));

            imageView = new BackupImageView(context);
            imageView.setRoundRadius(AndroidUtilities.dp(18));
            addView(imageView, LayoutHelper.createFrame(36, 36, Gravity.LEFT | Gravity.TOP, 16, 10, 0, 0));

            textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 68, 0, 56, 0));

            checkImageView = new ImageView(context);
            checkImageView.setImageResource(R.drawable.account_check);
            checkImageView.setScaleType(ImageView.ScaleType.CENTER);
            checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_switchTrackChecked), PorterDuff.Mode.MULTIPLY));
            addView(checkImageView, LayoutHelper.createFrame(40, LayoutHelper.MATCH_PARENT, Gravity.RIGHT | Gravity.TOP, 0, 0, 6, 0));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0 : AndroidUtilities.dp(68), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            imageView.setAlpha(enabled ? 1.0f : 0.5f);
            textView.setAlpha(enabled ? 1.0f : 0.5f);
            checkImageView.setAlpha(enabled ? 1.0f : 0.5f);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
        }

        public void setAccount(int account, boolean check, boolean divider) {
            accountNumber = account;
            TLRPC.User user = UserConfig.getInstance(accountNumber).getCurrentUser();
            avatarDrawable.setInfo(user);
            textView.setText(ContactsController.formatName(user.first_name, user.last_name));
            imageView.getImageReceiver().setCurrentAccount(account);
            imageView.setForUserOrChat(user, avatarDrawable);
            checkImageView.setVisibility(check ? VISIBLE : INVISIBLE);
            needDivider = divider;
            setWillNotDraw(!divider);
        }

        public int getAccountNumber() {
            return accountNumber;
        }
    }


}
