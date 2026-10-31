package tw.nekomimi.nekogram.settings.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.LayoutHelper;

import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.config.cell.ConfigCellCustom;
import xyz.nextalone.nagram.NaConfig;

@SuppressLint("ViewConstructor")
public class ActionBarPreviewCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

    public static final int ITEM_TYPE = ConfigCellCustom.CUSTOM_ITEM_ActionBarPreview;

    private final int currentAccount;
    private final ActionBar actionBar;
    private final ActionBarMenuItem downloadsItem;
    private final GradientDrawable cardBackground;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusDrawable;
    private Drawable premiumStar;
    private Drawable currentStatusDrawable;

    private final Runnable refreshRunnable = this::updatePreview;

    private final SharedPreferences.OnSharedPreferenceChangeListener preferencesListener = (preferences, key) -> {
        if (isPreviewSetting(key)) {
            removeCallbacks(refreshRunnable);
            post(refreshRunnable);
        }
    };

    private boolean isPreviewSetting(String key) {
        return key != null && (
                key.equals(NaConfig.INSTANCE.getCenterActionBarTitle().getKey())
                        || key.equals(NaConfig.INSTANCE.getUseSystemFontInTitle().getKey())
                        || key.equals(NaConfig.INSTANCE.getCustomTitle().getKey())
                        || key.equals(NaConfig.INSTANCE.getCustomTitleUserName().getKey())
                        || key.equals(NaConfig.INSTANCE.getAlwaysShowDownloadIcon().getKey())
                        || key.equals(NekoConfig.actionBarDecoration.getKey())
                        || key.equals(NekoConfig.newYear.getKey())
                        || key.equals(NekoConfig.disableAppBarShadow.getKey())
        );
    }

    public ActionBarPreviewCell(Context context) {
        this(context, UserConfig.selectedAccount);
    }

    public ActionBarPreviewCell(Context context, int currentAccount) {
        super(context);
        this.currentAccount = currentAccount;
        setWillNotDraw(false);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        statusDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(null, AndroidUtilities.dp(26.0f));
        statusDrawable.center = true;

        actionBar = new ActionBar(context);
        actionBar.setOccupyStatusBar(false);
        actionBar.setSupportsHolidayImage(true);
        actionBar.setUseContainerForTitles();
        ActionBarMenu menu = actionBar.createMenu();
        downloadsItem = menu.addItem(1, R.drawable.msg_download);
        menu.addItem(0, R.drawable.ic_ab_other);

        cardBackground = new GradientDrawable();
        cardBackground.setCornerRadius(AndroidUtilities.dp(16.0f));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionBar.setClipToOutline(true);
        }
        actionBar.setBackground(cardBackground);

        addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 56.0f, Gravity.CENTER, 14.0f, 14.0f, 14.0f, 14.0f));

        updateColors();
        updatePreview();
    }

    public void updatePreview() {
        if (NekoConfig.newYear.Bool()) {
            if (Theme.dialogs_holidayDrawable == null && getContext() != null) {
                Theme.dialogs_holidayDrawable = ContextCompat.getDrawable(getContext(), R.drawable.newyear);
            }
        } else {
            if (Theme.getEventType() != 0) {
                Theme.dialogs_holidayDrawable = null;
            }
        }
        updateTitle();
        updateCentered(NaConfig.INSTANCE.getCenterActionBarTitle().Bool());
        updateShadow();
        updateDownloadsIcon(NaConfig.INSTANCE.getAlwaysShowDownloadIcon().Bool());
        if (actionBar != null) {
            actionBar.invalidate();
            if (actionBar.getTitleTextView() != null) {
                actionBar.getTitleTextView().invalidate();
            }
            if (actionBar.getTitlesContainer() != null) {
                actionBar.getTitlesContainer().invalidate();
            }
        }
        invalidate();
    }

    public void updateDownloadsIcon(boolean show) {
        if (downloadsItem != null) {
            downloadsItem.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void updateCentered(boolean center) {
        if (actionBar != null) {
            actionBar.centerTitle(center);
            actionBar.requestLayout();
        }
    }

    public void updateShadow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && actionBar != null) {
            actionBar.setElevation(NekoConfig.disableAppBarShadow.Bool() ? 0.0f : AndroidUtilities.dp(2.0f));
        }
    }

    public void updateTitle() {
        updateTitle(false);
    }

    public void updateTitle(boolean animated) {
        if (actionBar == null) {
            return;
        }
        updateStatus(animated);
        String title = NaConfig.INSTANCE.getCustomTitle().String();
        if (NaConfig.INSTANCE.getCustomTitleUserName().Bool()) {
            TLRPC.User self = UserConfig.getInstance(currentAccount).getCurrentUser();
            if (self != null && self.first_name != null) {
                title = self.first_name;
            }
        }
        if (title.equals(LocaleController.getString(R.string.NekoX)) && !NaConfig.INSTANCE.getUseSystemFontInTitle().Bool()) {
            Drawable logoDrawable = ContextCompat.getDrawable(getContext(), R.drawable.nagram_logo_2);
            if (logoDrawable != null) {
                logoDrawable = logoDrawable.mutate();
                logoDrawable.setBounds(0, AndroidUtilities.dp(2), logoDrawable.getIntrinsicWidth(), AndroidUtilities.dp(2) + logoDrawable.getIntrinsicHeight());
                logoDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_telegram_color_dialogsLogo), PorterDuff.Mode.MULTIPLY));
                SpannableStringBuilder ssb = new SpannableStringBuilder(title);
                ssb.setSpan(new ImageSpan(logoDrawable), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                actionBar.setTitle(ssb, currentStatusDrawable);
                return;
            }
        }
        actionBar.setTitle(title, currentStatusDrawable);
    }

    public void updateStatus(boolean animated) {
        TLRPC.User currentUser = UserConfig.getInstance(currentAccount).getCurrentUser();
        currentStatusDrawable = null;
        if (currentUser != null) {
            Long emojiStatusDocumentId = UserObject.getEmojiStatusDocumentId(currentUser);
            if (emojiStatusDocumentId != null) {
                final boolean isCollectible = currentUser.emoji_status instanceof TLRPC.TL_emojiStatusCollectible;
                statusDrawable.set(emojiStatusDocumentId, animated);
                statusDrawable.setParticles(isCollectible, animated);
                statusDrawable.setColor(Theme.getColor(Theme.key_profile_verifiedBackground));
                currentStatusDrawable = statusDrawable;
            } else if (currentUser.premium || MessagesController.getInstance(currentAccount).isPremiumUser(currentUser)) {
                if (premiumStar == null) {
                    Drawable star = ContextCompat.getDrawable(getContext(), R.drawable.msg_premium_liststar);
                    if (star != null) {
                        star = star.mutate();
                        premiumStar = new AnimatedEmojiDrawable.WrapSizeDrawable(star, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f)) {
                            @Override
                            public void draw(@NonNull Canvas canvas) {
                                canvas.save();
                                canvas.translate(0.0f, AndroidUtilities.dp(1.0f));
                                super.draw(canvas);
                                canvas.restore();
                            }
                        };
                    }
                }
                if (premiumStar != null) {
                    premiumStar.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_verifiedBackground), PorterDuff.Mode.MULTIPLY));
                }
                statusDrawable.set(premiumStar, animated);
                statusDrawable.setParticles(false, animated);
                statusDrawable.setColor(Theme.getColor(Theme.key_profile_verifiedBackground));
                currentStatusDrawable = statusDrawable;
            } else {
                statusDrawable.set((Drawable) null, animated);
                statusDrawable.setParticles(false, animated);
            }
        } else {
            statusDrawable.set((Drawable) null, animated);
            statusDrawable.setParticles(false, animated);
        }
    }

    public void updateColors() {
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int bg = Theme.getColor(Theme.key_windowBackgroundWhite);
        int textColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
        int cardColor = Theme.blendOver(bg, Theme.multAlpha(textColor, 0.06f));
        int strokeColor = Theme.blendOver(bg, Theme.multAlpha(textColor, 0.12f));

        if (cardBackground != null) {
            cardBackground.setColor(cardColor);
            cardBackground.setStroke(AndroidUtilities.dp(1), strokeColor);
        }
        if (actionBar != null) {
            actionBar.setItemsColor(textColor, false);
            actionBar.setTitleColor(textColor);
        }
        if (premiumStar != null) {
            premiumStar.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_verifiedBackground), PorterDuff.Mode.MULTIPLY));
        }
        if (statusDrawable != null) {
            statusDrawable.setColor(Theme.getColor(Theme.key_profile_verifiedBackground));
        }
        updateTitle(false);
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (statusDrawable != null) {
            statusDrawable.attach();
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.userEmojiStatusUpdated);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NaConfig.INSTANCE.getPreferences().registerOnSharedPreferenceChangeListener(preferencesListener);
        NekoConfig.preferences.registerOnSharedPreferenceChangeListener(preferencesListener);
        updatePreview();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (statusDrawable != null) {
            statusDrawable.detach();
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.userEmojiStatusUpdated);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NaConfig.INSTANCE.getPreferences().unregisterOnSharedPreferenceChangeListener(preferencesListener);
        NekoConfig.preferences.unregisterOnSharedPreferenceChangeListener(preferencesListener);
        removeCallbacks(refreshRunnable);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetNewTheme) {
            updateColors();
        } else if (account == currentAccount) {
            if (id == NotificationCenter.userEmojiStatusUpdated) {
                if (args != null && args.length > 0 && args[0] instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) args[0];
                    if (user.id != UserConfig.getInstance(currentAccount).getClientUserId()) {
                        return;
                    }
                }
                updateTitle(true);
            } else if (id == NotificationCenter.currentUserPremiumStatusChanged) {
                updateTitle(true);
            } else if (id == NotificationCenter.updateInterfaces) {
                if (args != null && args.length > 0 && args[0] instanceof Integer) {
                    int mask = (Integer) args[0];
                    if ((mask & MessagesController.UPDATE_MASK_EMOJI_STATUS) != 0
                            || (mask & MessagesController.UPDATE_MASK_NAME) != 0
                            || (mask & MessagesController.UPDATE_MASK_STATUS) != 0) {
                        updateTitle(true);
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(84.0f), View.MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
}
