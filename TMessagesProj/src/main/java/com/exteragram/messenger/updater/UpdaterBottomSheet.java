/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @immat0x1, 2022.

 cherrygram dev kys

*/

package com.exteragram.messenger.updater;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ExteraUtils;
import com.exteragram.messenger.components.TextCheckWithIconCell;

public class UpdaterBottomSheet extends BottomSheet {

    private RLottieImageView imageView;
    private TextView changelogTextView;

    private boolean animationInProgress;

    private boolean isTranslated = false;
    private CharSequence translatedC;

    public UpdaterBottomSheet(Context context, boolean available, String... args) {
        // args = {version, changelog, size, downloadUrl, uploadDate}
        super(context, false);
        setOpenNoDelay(true);
        fixNavigationBar();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        FrameLayout header = new FrameLayout(context);
        linearLayout.addView(header, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 21, 10, 0, 10));

        if (available) {
            imageView = new RLottieImageView(context);
            imageView.setOnClickListener(v -> {
                if (!imageView.isPlaying() && imageView.getAnimatedDrawable() != null) {
                    imageView.getAnimatedDrawable().setCurrentFrame(0);
                    imageView.playAnimation();
                }
            });
            imageView.setAnimation(R.raw.etg_raccoon, 60, 60, new int[]{0x000000, 0x000000});
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            header.addView(imageView, LayoutHelper.createFrame(60, 60, Gravity.LEFT | Gravity.CENTER_VERTICAL));
        }

        TextView nameView = new TextView(context);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        nameView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        nameView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        nameView.setText(available ? LocaleController.getString("UpdateAvailable", R.string.UpdateAvailable) : ExteraUtils.getAppName());
        header.addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 30, Gravity.LEFT, available ? 75 : 0, 5, 0, 0));

        TextView timeView = new TextView(context);
        timeView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        timeView.setTypeface(AndroidUtilities.getTypeface("fonts/rregular.ttf"));
        timeView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        timeView.setText(available ? args[4] : LocaleController.getString("LastCheck", R.string.LastCheck) + ": " + LocaleController.formatDateTime(ExteraConfig.lastUpdateCheckTime / 1000));
        header.addView(timeView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, Gravity.LEFT, available ? 75 : 0, 35, 0, 0));

        TextCell version = new TextCell(context);
        version.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 100, 0));
        if (available) {
            version.setTextAndValueAndIcon(LocaleController.getString("Version", R.string.Version), args[0].replaceAll("v|-beta", ""), R.drawable.msg_info, true);
        } else {
            version.setTextAndValueAndIcon(LocaleController.getString("CurrentVersion", R.string.CurrentVersion), BuildVars.BUILD_VERSION_STRING, R.drawable.msg_info, true);
        }
        version.setOnClickListener(v -> copyText(version.getTextView().getText() + ": " + version.getValueTextView().getText()));
        linearLayout.addView(version);


        if (available) {
            TextCell size = new TextCell(context);
            size.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 100, 0));
            size.setTextAndValueAndIcon(LocaleController.getString("UpdateSize", R.string.UpdateSize), args[2], R.drawable.msg_sendfile, true);
            size.setOnClickListener(v -> copyText(size.getTextView().getText() + ": " + size.getValueTextView().getText()));
            linearLayout.addView(size);
            
            TextCell changelog = new TextCell(context);
            changelog.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 100, 0));
            changelog.setTextAndIcon(LocaleController.getString("Changelog", R.string.Changelog), R.drawable.msg_log, false);
            changelog.setOnClickListener(v -> copyText(changelog.getTextView().getText() + "\n" + (isTranslated ? translatedC : AndroidUtilities.replaceTags(args[1]))));
            linearLayout.addView(changelog);

            changelogTextView = new TextView(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
                }
            };
            changelogTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            changelogTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            changelogTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
            changelogTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
            changelogTextView.setText(AndroidUtilities.replaceTags(args[1]));
            changelogTextView.setPadding(AndroidUtilities.dp(21), 0, AndroidUtilities.dp(21), AndroidUtilities.dp(10));
            changelogTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            changelogTextView.setOnClickListener(v -> {
                UpdaterUtils.translate(AndroidUtilities.replaceTags(args[1]), (String translated) -> {
                    translatedC = translated;
                    animateChangelog(isTranslated ? AndroidUtilities.replaceTags(args[1]) : translatedC);
                    isTranslated ^= true;
                }, () -> {});
            });
            linearLayout.addView(changelogTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    
            TextView doneButton = new TextView(context);
            doneButton.setLines(1);
            doneButton.setSingleLine(true);
            doneButton.setEllipsize(TextUtils.TruncateAt.END);
            doneButton.setGravity(Gravity.CENTER);
            doneButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            doneButton.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_featuredStickers_addButton), 6));
            doneButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            doneButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            doneButton.setText(LocaleController.getString("AppUpdateDownloadNow", R.string.AppUpdateDownloadNow));
            doneButton.setOnClickListener(v -> {
                UpdaterUtils.downloadApk(context, args[3], "exteraGram " + args[0]);
                dismiss();
            });
            linearLayout.addView(doneButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, 0, 16, 15, 16, 5));
    
            TextView scheduleButton = new TextView(context);
            scheduleButton.setLines(1);
            scheduleButton.setSingleLine(true);
            scheduleButton.setEllipsize(TextUtils.TruncateAt.END);
            scheduleButton.setGravity(Gravity.CENTER);
            scheduleButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
            scheduleButton.setBackground(null);
            scheduleButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            scheduleButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            scheduleButton.setText(LocaleController.getString("AppUpdateRemindMeLater", R.string.AppUpdateRemindMeLater));
            scheduleButton.setOnClickListener(v -> {
                ExteraConfig.scheduleUpdate();
                dismiss();
            });
            linearLayout.addView(scheduleButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, 0, 16, 0, 16, 0));
        } else {
            final String btype = BuildVars.isBetaApp() ? LocaleController.getString("BTBeta", R.string.BTBeta) : LocaleController.getString("BTRelease", R.string.BTRelease);
            TextCell buildType = new TextCell(context);
            buildType.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 100, 0));
            buildType.setTextAndValueAndIcon(LocaleController.getString("BuildType", R.string.BuildType), btype, R.drawable.msg_customize, true);
            buildType.setOnClickListener(v -> copyText(buildType.getTextView().getText() + ": " + buildType.getValueTextView().getText()));
            linearLayout.addView(buildType);
    
            TextCheckWithIconCell checkOnLaunch = new TextCheckWithIconCell(context);
            checkOnLaunch.setEnabled(true, null);
            checkOnLaunch.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 100, 0));
            checkOnLaunch.setTextAndCheckAndIcon(LocaleController.getString("CheckOnLaunch", R.string.CheckOnLaunch), R.drawable.msg_timeredit, ExteraConfig.checkUpdatesOnLaunch, true);
            checkOnLaunch.setOnClickListener(v -> {
                checkOnLaunch.setChecked(!checkOnLaunch.isChecked());
                ExteraConfig.toggleCheckUpdatesOnLaunch();
            });
            linearLayout.addView(checkOnLaunch);
    
            TextCell clearUpdates = new TextCell(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
                }
            };
            clearUpdates.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 100, 0));
            clearUpdates.setTextAndIcon(LocaleController.getString("ClearUpdatesCache", R.string.ClearUpdatesCache), R.drawable.msg_clear, false);
            clearUpdates.setOnClickListener(v -> {
                if (UpdaterUtils.getOtaDirSize().replaceAll("[^0-9]+", "").equals("0")) {
                    BulletinFactory.of(getContainer(), null).createErrorBulletin(LocaleController.getString("NothingToClear", R.string.NothingToClear)).show();
                } else {
                    BulletinFactory.of(getContainer(), null).createErrorBulletin(LocaleController.formatString("ClearedUpdatesCache", R.string.ClearedUpdatesCache, UpdaterUtils.getOtaDirSize())).show();
                    UpdaterUtils.cleanOtaDir();
                }
            });
            linearLayout.addView(clearUpdates);
    
            TextView checkUpdatesButton = new TextView(context);
            checkUpdatesButton.setLines(1);
            checkUpdatesButton.setSingleLine(true);
            checkUpdatesButton.setEllipsize(TextUtils.TruncateAt.END);
            checkUpdatesButton.setGravity(Gravity.CENTER);
            checkUpdatesButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            checkUpdatesButton.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_featuredStickers_addButton), 6));
            checkUpdatesButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            checkUpdatesButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            checkUpdatesButton.setText(LocaleController.getString("CheckForUpdates", R.string.CheckForUpdates));
            checkUpdatesButton.setOnClickListener(v -> {
                UpdaterUtils.checkUpdates(context, true, () -> {
                    BulletinFactory.of(getContainer(), null).createErrorBulletin(LocaleController.getString("NoUpdates", R.string.NoUpdates)).show();
                    timeView.setText(LocaleController.getString("LastCheck", R.string.LastCheck) + ": " + LocaleController.formatDateTime(ExteraConfig.lastUpdateCheckTime / 1000));
                }, () -> dismiss());
            });
            linearLayout.addView(checkUpdatesButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, 0, 16, 15, 16, 16));
        }

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    private void animateChangelog(CharSequence text) {
        changelogTextView.setText(text);
        animationInProgress = true;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(changelogTextView, View.ALPHA, 0.0f, 1.0f),
                ObjectAnimator.ofFloat(changelogTextView, View.TRANSLATION_Y, AndroidUtilities.dp(10), 0)
        );
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationInProgress = false;
            }
        });
        animatorSet.start();
    }

    private void copyText(CharSequence text) {
        AndroidUtilities.addToClipboard(text);
        BulletinFactory.of(getContainer(), null).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
    }

    @Override
    public void show() {
        super.show();
        if (imageView != null) {
            imageView.playAnimation();
        }
    }
}
