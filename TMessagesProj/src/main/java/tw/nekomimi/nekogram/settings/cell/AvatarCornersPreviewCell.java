package tw.nekomimi.nekogram.settings.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

import tw.nekomimi.nekogram.config.cell.ConfigCellCustom;
import xyz.nextalone.nagram.NaConfig;
import xyz.nextalone.nagram.helper.AvatarCornersHelper;

@SuppressLint("ViewConstructor")
public class AvatarCornersPreviewCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

    public static final int ITEM_TYPE = ConfigCellCustom.CUSTOM_ITEM_AvatarCorners;

    private final int currentAccount;
    private final Runnable onStop;
    private final SeekBarView bar;
    private final FrameLayout preview;
    private final GradientDrawable cardBackground;
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint brightMockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final AvatarDrawable avatarDrawable;
    private final ImageReceiver avatarImageReceiver;

    private final Runnable refreshRunnable = this::updatePreview;

    private final SharedPreferences.OnSharedPreferenceChangeListener preferencesListener = (preferences, key) -> {
        if (key != null && key.equals(NaConfig.INSTANCE.getAvatarCorners().getKey())) {
            removeCallbacks(refreshRunnable);
            post(refreshRunnable);
        }
    };

    public AvatarCornersPreviewCell(Context context) {
        this(context, UserConfig.selectedAccount, null);
    }

    public AvatarCornersPreviewCell(Context context, int currentAccount) {
        this(context, currentAccount, null);
    }

    public AvatarCornersPreviewCell(Context context, int currentAccount, Runnable onStop) {
        super(context);
        this.currentAccount = currentAccount;
        this.onStop = onStop;
        setWillNotDraw(false);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

        textPaint.setTextSize(AndroidUtilities.dp(14));

        cardBackground = new GradientDrawable();
        cardBackground.setCornerRadius(AndroidUtilities.dp(16.0f));

        preview = new FrameLayout(context) {
            @Override
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                drawPreview(canvas);
            }
        };
        preview.setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            preview.setClipToOutline(true);
        }
        preview.setBackground(cardBackground);
        addView(preview, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 84.0f, Gravity.TOP | Gravity.LEFT, 21.0f, 16.0f, 21.0f, 0.0f));

        avatarDrawable = new AvatarDrawable();
        avatarImageReceiver = new ImageReceiver(preview);
        avatarImageReceiver.setRoundRadiusEnabled(true);
        avatarImageReceiver.setParentView(preview);
        avatarImageReceiver.setInvalidateAll(true);
        avatarImageReceiver.setAllowLoadingOnAttachedOnly(false);
        avatarImageReceiver.setDelegate((imageReceiver, set, thumb, memCache) -> {
            if (preview != null) {
                preview.invalidate();
            }
        });

        bar = new SeekBarView(context);
        bar.setReportChanges(true);
        bar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
            @Override
            public void onSeekBarDrag(boolean stop, float progress) {
                int value = Math.round(progress * AvatarCornersHelper.DEFAULT_PERCENT);
                if (value != NaConfig.INSTANCE.getAvatarCorners().Int()) {
                    NaConfig.INSTANCE.getAvatarCorners().setConfigInt(value);
                    invalidate();
                    if (preview != null) {
                        preview.invalidate();
                    }
                }
                if (stop && onStop != null) {
                    onStop.run();
                }
            }

            @Override
            public void onSeekBarPressed(boolean pressed) {}
        });
        boolean isRtl = LocaleController.isRTL;
        addView(bar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, isRtl ? Gravity.RIGHT | Gravity.TOP : Gravity.LEFT | Gravity.TOP, isRtl ? 60 : 12, 123, isRtl ? 12 : 60, 5));

        updateColors();
        updateUser();
        updatePreview();
    }

    private void drawPreview(Canvas canvas) {
        float fDp = AndroidUtilities.dp(1.0f);
        float fDp2 = AndroidUtilities.dp(15.0f);
        float fDp3 = AndroidUtilities.dp(83.0f);
        int iDp = AndroidUtilities.dp(56.0f);
        float width = preview.getWidth();
        RectF rectF = AndroidUtilities.rectTmp;
        float fDp4 = AndroidUtilities.dp(12.0f) + fDp;
        boolean isRtl = LocaleController.isRTL;
        float avatarX = isRtl ? width - fDp2 - iDp : fDp2;
        rectF.set(avatarX, fDp4, avatarX + iDp, fDp4 + iDp);

        if (avatarImageReceiver != null) {
            avatarImageReceiver.setImageCoords(rectF.left, rectF.top, rectF.width(), rectF.height());
            int radius = Math.round(AvatarCornersHelper.radius(rectF.width()));
            avatarImageReceiver.setRoundRadius(radius);
            avatarImageReceiver.draw(canvas);
        }

        float fDp5 = AndroidUtilities.dp(4.0f);
        float linesStart = AndroidUtilities.dp(6.0f) + fDp3;
        if (isRtl) {
            rectF.set(width - linesStart - (0.4f * width), AndroidUtilities.dp(16.0f) + fDp, width - linesStart, AndroidUtilities.dp(24.33f) + fDp);
            canvas.drawRoundRect(rectF, fDp5, fDp5, brightMockPaint);

            rectF.set(width - linesStart - (0.5f * width), AndroidUtilities.dp(38.0f) + fDp, width - linesStart, AndroidUtilities.dp(46.33f) + fDp);
            canvas.drawRoundRect(rectF, fDp5, fDp5, mockPaint);

            rectF.set(width - linesStart - (0.36f * width), AndroidUtilities.dp(56.0f) + fDp, width - linesStart, AndroidUtilities.dp(64.33f) + fDp);
            canvas.drawRoundRect(rectF, fDp5, fDp5, mockPaint);

            rectF.set(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f) + fDp, AndroidUtilities.dp(16.0f) + AndroidUtilities.dp(43.0f), fDp + AndroidUtilities.dp(24.33f));
            canvas.drawRoundRect(rectF, fDp5, fDp5, mockPaint);
        } else {
            rectF.set(linesStart, AndroidUtilities.dp(16.0f) + fDp, linesStart + (0.4f * width), AndroidUtilities.dp(24.33f) + fDp);
            canvas.drawRoundRect(rectF, fDp5, fDp5, brightMockPaint);

            rectF.set(linesStart, AndroidUtilities.dp(38.0f) + fDp, linesStart + (0.5f * width), AndroidUtilities.dp(46.33f) + fDp);
            canvas.drawRoundRect(rectF, fDp5, fDp5, mockPaint);

            rectF.set(linesStart, AndroidUtilities.dp(56.0f) + fDp, linesStart + (0.36f * width), AndroidUtilities.dp(64.33f) + fDp);
            canvas.drawRoundRect(rectF, fDp5, fDp5, mockPaint);

            rectF.set((width - AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(43.0f), AndroidUtilities.dp(16.0f) + fDp, width - AndroidUtilities.dp(16.0f), fDp + AndroidUtilities.dp(24.33f));
            canvas.drawRoundRect(rectF, fDp5, fDp5, mockPaint);
        }
    }

    public void updateUser() {
        TLRPC.User currentUser = UserConfig.getInstance(currentAccount).getCurrentUser();
        if (currentUser != null && avatarImageReceiver != null) {
            avatarDrawable.setInfo(currentAccount, currentUser);
            avatarImageReceiver.setForUserOrChat(currentUser, avatarDrawable);
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
        brightMockPaint.setColor(Theme.blendOver(cardColor, Theme.multAlpha(textColor, 0.20f)));
        mockPaint.setColor(Theme.blendOver(cardColor, Theme.multAlpha(textColor, 0.10f)));
        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        if (bar != null) {
            bar.updateColors();
        }
        invalidate();
        if (preview != null) {
            preview.invalidate();
        }
    }

    public void updatePreview() {
        if (bar != null) {
            bar.setProgress(NaConfig.INSTANCE.getAvatarCorners().Int() / (float) AvatarCornersHelper.DEFAULT_PERCENT);
        }
        updateUser();
        invalidate();
        if (preview != null) {
            preview.invalidate();
        }
    }

    public void updateSliderStyle() {
        if (bar != null) {
            bar.updateColors();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (avatarImageReceiver != null) {
            avatarImageReceiver.onAttachedToWindow();
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NaConfig.INSTANCE.getPreferences().registerOnSharedPreferenceChangeListener(preferencesListener);
        updateColors();
        updatePreview();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (avatarImageReceiver != null) {
            avatarImageReceiver.onDetachedFromWindow();
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NaConfig.INSTANCE.getPreferences().unregisterOnSharedPreferenceChangeListener(preferencesListener);
        removeCallbacks(refreshRunnable);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetNewTheme) {
            updateColors();
        } else if (id == NotificationCenter.updateInterfaces && account == currentAccount) {
            if (args != null && args.length > 0 && args[0] instanceof Integer) {
                int mask = (Integer) args[0];
                if ((mask & MessagesController.UPDATE_MASK_AVATAR) != 0 || (mask & MessagesController.UPDATE_MASK_NAME) != 0) {
                    updateUser();
                    if (preview != null) {
                        preview.invalidate();
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(170.0f), View.MeasureSpec.EXACTLY)
        );
        bar.setProgress(NaConfig.INSTANCE.getAvatarCorners().Int() / (float) AvatarCornersHelper.DEFAULT_PERCENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean isRtl = LocaleController.isRTL;
        if (isRtl) {
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(LocaleController.getString(R.string.AvatarCorners), getMeasuredWidth() - AndroidUtilities.dp(21), AndroidUtilities.dp(120), textPaint);

            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(NaConfig.INSTANCE.getAvatarCorners().Int() + "%", AndroidUtilities.dp(16), AndroidUtilities.dp(147), textPaint);
        } else {
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(LocaleController.getString(R.string.AvatarCorners), AndroidUtilities.dp(21), AndroidUtilities.dp(120), textPaint);

            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(NaConfig.INSTANCE.getAvatarCorners().Int() + "%", getMeasuredWidth() - AndroidUtilities.dp(16), AndroidUtilities.dp(147), textPaint);
        }

        canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (bar != null) {
            bar.invalidate();
        }
        if (preview != null) {
            preview.invalidate();
        }
    }
}
