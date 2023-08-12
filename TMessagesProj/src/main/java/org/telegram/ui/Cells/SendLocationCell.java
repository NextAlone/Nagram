/*
 * This is the source code of Telegram for Android v. 5.x.x
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.Components.ShareLocationDrawable;

public class SendLocationCell extends FrameLayout {

    private int currentAccount = UserConfig.selectedAccount;
    private SimpleTextView accurateTextView;
    private SimpleTextView titleTextView;
    private ImageView imageView;
    private long dialogId;
    private RectF rect;
    private boolean live;
    private final Theme.ResourcesProvider resourcesProvider;

    private Runnable invalidateRunnable = new Runnable() {
        @Override
        public void run() {
            checkText();
            invalidate((int) rect.left - 5, (int) rect.top - 5, (int) rect.right + 5, (int) rect.bottom + 5);
            AndroidUtilities.runOnUIThread(invalidateRunnable, 1000);
        }
    };

    public SendLocationCell(Context context, boolean live, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        this.live = live;

        imageView = new ImageView(context);

        setBackground(Theme.AdaptiveRipple.rect());

        imageView.setTag(live ? Theme.key_location_sendLiveLocationBackground + Theme.key_location_sendLiveLocationIcon : Theme.key_location_sendLocationBackground + Theme.key_location_sendLocationIcon);
        Drawable circle = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42), getThemedColor(live ? Theme.key_location_sendLiveLocationBackground : Theme.key_location_sendLocationBackground), getThemedColor(live ? Theme.key_location_sendLiveLocationBackground : Theme.key_location_sendLocationBackground));
        if (live) {
            rect = new RectF();
            Drawable drawable = new ShareLocationDrawable(context, 4);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_sendLiveLocationIcon), PorterDuff.Mode.SRC_IN));
            CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42), AndroidUtilities.dp(42));
            imageView.setBackgroundDrawable(combinedDrawable);
            AndroidUtilities.runOnUIThread(invalidateRunnable, 1000);
            setWillNotDraw(false);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_location_sendLocationIcon), PorterDuff.Mode.SRC_IN));
            CombinedDrawable combinedDrawable = new CombinedDrawable(circle, drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42), AndroidUtilities.dp(42));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24), AndroidUtilities.dp(24));
            imageView.setBackgroundDrawable(combinedDrawable);
        }
        addView(imageView, LayoutHelper.createFrame(42, 42, Gravity.TOP | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT), LocaleController.isRTL ? 0 : 15, 12, LocaleController.isRTL ? 15 : 0, 0));

        titleTextView = new SimpleTextView(context);
        titleTextView.setTextSize(16);
        titleTextView.setTag(live ? Theme.key_location_sendLiveLocationText : Theme.key_location_sendLocationText);
        titleTextView.setTextColor(getThemedColor(live ? Theme.key_location_sendLiveLocationText : Theme.key_location_sendLocationText));
        titleTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(titleTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, Gravity.TOP | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT), LocaleController.isRTL ? 16 : 73, 12, LocaleController.isRTL ? 73 : 16, 0));

        accurateTextView = new SimpleTextView(context);
        accurateTextView.setTextSize(14);
        accurateTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
        accurateTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        addView(accurateTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, Gravity.TOP | (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT), LocaleController.isRTL ? 16 : 73, 37, LocaleController.isRTL ? 73 : 16, 0));
    }

    private ImageView getImageView() {
        return imageView;
    }

    public void setHasLocation(boolean value) {
        LocationController.SharingLocationInfo info = LocationController.getInstance(currentAccount).getSharingLocationInfo(dialogId);
        if (info == null) {
            titleTextView.setAlpha(value ? 1.0f : 0.5f);
            accurateTextView.setAlpha(value ? 1.0f : 0.5f);
            imageView.setAlpha(value ? 1.0f : 0.5f);
        }
        if (live) {
            checkText();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(invalidateRunnable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (rect != null) {
            AndroidUtilities.runOnUIThread(invalidateRunnable, 1000);
        }
    }

    public void setText(String title, String text) {
        titleTextView.setText(title);
        accurateTextView.setText(text);
    }

    public void setDialogId(long did) {
        dialogId = did;
        if (live) {
            checkText();
        }
    }

    private void checkText() {
        LocationController.SharingLocationInfo info = LocationController.getInstance(currentAccount).getSharingLocationInfo(dialogId);
        if (info != null) {
            setText(LocaleController.getString("StopLiveLocation", R.string.StopLiveLocation), LocaleController.formatLocationUpdateDate(info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date));
        } else {
            setText(LocaleController.getString("SendLiveLocation", R.string.SendLiveLocation), LocaleController.getString("SendLiveLocationInfo", R.string.SendLiveLocationInfo));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LocationController.SharingLocationInfo currentInfo = LocationController.getInstance(currentAccount).getSharingLocationInfo(dialogId);
        if (currentInfo == null) {
            return;
        }
        int currentTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
        if (currentInfo.stopTime < currentTime) {
            return;
        }

        float progress = Math.abs(currentInfo.stopTime - currentTime) / (float) currentInfo.period;
        if (LocaleController.isRTL) {
            rect.set(AndroidUtilities.dp(13), AndroidUtilities.dp(18), AndroidUtilities.dp(43), AndroidUtilities.dp(48));
        } else {
            rect.set(getMeasuredWidth() - AndroidUtilities.dp(43), AndroidUtilities.dp(18), getMeasuredWidth() - AndroidUtilities.dp(13), AndroidUtilities.dp(48));
        }

        int color = getThemedColor(Theme.key_location_liveLocationProgress);
        Theme.chat_radialProgress2Paint.setColor(color);
        Theme.chat_livePaint.setColor(color);

        canvas.drawArc(rect, -90, -360 * progress, false, Theme.chat_radialProgress2Paint);

        String text = LocaleController.formatLocationLeftTime(Math.abs(currentInfo.stopTime - currentTime));

        float size = Theme.chat_livePaint.measureText(text);

        canvas.drawText(text, rect.centerX() - size / 2, AndroidUtilities.dp(37), Theme.chat_livePaint);
    }

    private int getThemedColor(int key) {
        return Theme.getColor(key, resourcesProvider);
    }
}
