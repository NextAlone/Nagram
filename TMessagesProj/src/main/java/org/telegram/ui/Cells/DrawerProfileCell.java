/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SnowflakesEffect;

import tw.nekomimi.nekogram.NekoConfig;

public class DrawerProfileCell extends FrameLayout {

    private BackupImageView avatarImageView;
    private BackupImageView avatarBackgroundView;
    private TextView nameTextView;
    private TextView phoneTextView;
    private ImageView shadowView;
    private ImageView arrowView;

    private Rect srcRect = new Rect();
    private Rect destRect = new Rect();
    private Paint paint = new Paint();
    private Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Integer currentColor;
    private Integer currentMoonColor;
    private SnowflakesEffect snowflakesEffect;
    private boolean accountsShown;
    private int darkThemeBackgroundColor;

    private TLRPC.User user;

    public DrawerProfileCell(Context context) {
        super(context);

        avatarBackgroundView = new BackupImageView(context);
        avatarBackgroundView.setVisibility(INVISIBLE);
        addView(avatarBackgroundView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        shadowView = new ImageView(context);
        shadowView.setVisibility(INVISIBLE);
        shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
        shadowView.setImageResource(R.drawable.bottom_shadow);
        addView(shadowView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 70, Gravity.LEFT | Gravity.BOTTOM));

        avatarImageView = new BackupImageView(context);
        avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32));
        addView(avatarImageView, LayoutHelper.createFrame(64, 64, Gravity.LEFT | Gravity.BOTTOM, 16, 0, 0, 67));

        nameTextView = new TextView(context);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setLines(1);
        nameTextView.setMaxLines(1);
        nameTextView.setSingleLine(true);
        nameTextView.setGravity(Gravity.LEFT);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM, 16, 0, 76, 28));

        phoneTextView = new TextView(context);
        phoneTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        phoneTextView.setLines(1);
        phoneTextView.setMaxLines(1);
        phoneTextView.setSingleLine(true);
        phoneTextView.setGravity(Gravity.LEFT);
        addView(phoneTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM, 16, 0, 76, 9));

        arrowView = new ImageView(context);
        arrowView.setScaleType(ImageView.ScaleType.CENTER);
        arrowView.setImageResource(R.drawable.menu_expand);
        addView(arrowView, LayoutHelper.createFrame(59, 59, Gravity.RIGHT | Gravity.BOTTOM));
        setArrowState(false);

        if (Theme.getEventType() == 0) {
            snowflakesEffect = new SnowflakesEffect();
            snowflakesEffect.setColorKey(Theme.key_chats_menuName);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148) + AndroidUtilities.statusBarHeight, MeasureSpec.EXACTLY));
        } else {
            try {
                super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148), MeasureSpec.EXACTLY));
            } catch (Exception e) {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(148));
                FileLog.e(e);
            }
        }
    }

    private boolean useAdb() {

        return NekoConfig.avatarAsDrawerBackground && ImageLocation.isUserHasPhoto(user);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable backgroundDrawable = Theme.getCachedWallpaper();
        String backgroundKey = applyBackground(false);
        boolean useImageBackground = !backgroundKey.equals(Theme.key_chats_menuTopBackground) && Theme.isCustomTheme() && !Theme.isPatternWallpaper() && backgroundDrawable != null && !(backgroundDrawable instanceof ColorDrawable) && !(backgroundDrawable instanceof GradientDrawable);
        boolean drawCatsShadow = false;
        int color;
        if (!useAdb() && !useImageBackground && Theme.hasThemeKey(Theme.key_chats_menuTopShadowCats)) {
            color = Theme.getColor(Theme.key_chats_menuTopShadowCats);
            drawCatsShadow = true;
        } else {
            if (Theme.hasThemeKey(Theme.key_chats_menuTopShadow)) {
                color = Theme.getColor(Theme.key_chats_menuTopShadow);
            } else {
                color = Theme.getServiceMessageColor() | 0xff000000;
            }
        }
        if (currentColor == null || currentColor != color) {
            currentColor = color;
            shadowView.getDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
        nameTextView.setTextColor(Theme.getColor(Theme.key_chats_menuName));
        phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuName));

        if (useAdb() || useImageBackground) {
            if (shadowView.getVisibility() != VISIBLE) {
                shadowView.setVisibility(VISIBLE);
            }
            if (backgroundDrawable instanceof ColorDrawable || backgroundDrawable instanceof GradientDrawable) {
                backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                backgroundDrawable.draw(canvas);
            } else if (backgroundDrawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
                float scaleX = (float) getMeasuredWidth() / (float) bitmap.getWidth();
                float scaleY = (float) getMeasuredHeight() / (float) bitmap.getHeight();
                float scale = Math.max(scaleX, scaleY);
                int width = (int) (getMeasuredWidth() / scale);
                int height = (int) (getMeasuredHeight() / scale);
                int x = (bitmap.getWidth() - width) / 2;
                int y = (bitmap.getHeight() - height) / 2;
                srcRect.set(x, y, x + width, y + height);
                destRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                try {
                    canvas.drawBitmap(bitmap, srcRect, destRect, paint);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        } else {
            int visibility = drawCatsShadow? VISIBLE : INVISIBLE;
            if (shadowView.getVisibility() != visibility) {
                shadowView.setVisibility(visibility);
            }
            super.onDraw(canvas);
        }

        if (snowflakesEffect != null) {
            snowflakesEffect.onDraw(this, canvas);
        }
    }

    public boolean isInAvatar(float x, float y) {
        return x >= avatarImageView.getLeft() && x <= avatarImageView.getRight() && y >= avatarImageView.getTop() && y <= avatarImageView.getBottom();
    }

    public boolean hasAvatar() {
        return avatarImageView.getImageReceiver().hasNotThumb();
    }

    public boolean isAccountsShown() {
        return accountsShown;
    }

    public void setAccountsShown(boolean value, boolean animated) {
        if (accountsShown == value) {
            return;
        }
        accountsShown = value;
        setArrowState(animated);
    }

    public void setUser(TLRPC.User user, boolean accounts) {
        if (user == null) {
            return;
        }
        this.user = user;
        accountsShown = accounts;
        setArrowState(false);
        nameTextView.setText(UserObject.getUserName(user));
        if (!NekoConfig.hidePhone) {
            phoneTextView.setText(PhoneFormat.getInstance().format("+" + user.phone));
        } else if (!TextUtils.isEmpty(user.username)) {
            phoneTextView.setText("@" + user.username);
        } else {
            phoneTextView.setText(LocaleController.getString("MobileHidden",R.string.MobileHidden));
        }
        AvatarDrawable avatarDrawable = new AvatarDrawable(user);
        avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        avatarImageView.setImage(ImageLocation.getForUser(user, false), "50_50", avatarDrawable, user);
        if (useAdb()) {
            avatarBackgroundView.setImage(ImageLocation.getForUser(user, true), "512_512", avatarDrawable, user);
            avatarBackgroundView.setVisibility(VISIBLE);
            avatarImageView.setVisibility(INVISIBLE);
        } else {
            avatarBackgroundView.setVisibility(INVISIBLE);
            avatarImageView.setVisibility(VISIBLE);
        }

        applyBackground(true);
    }

    public String applyBackground(boolean force) {
        String currentTag = (String) getTag();
        String backgroundKey = Theme.hasThemeKey(Theme.key_chats_menuTopBackground) && Theme.getColor(Theme.key_chats_menuTopBackground) != 0 ? Theme.key_chats_menuTopBackground : Theme.key_chats_menuTopBackgroundCats;
        if (force || !backgroundKey.equals(currentTag)) {
            setBackgroundColor(Theme.getColor(backgroundKey));
            setTag(backgroundKey);
        }
        return backgroundKey;
    }

    public void updateColors() {
        if (snowflakesEffect != null) {
            snowflakesEffect.updateColors();
        }
    }

    private void setArrowState(boolean animated) {
        final float rotation = accountsShown ? 180.0f : 0.0f;
        if (animated) {
            arrowView.animate().rotation(rotation).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
        } else {
            arrowView.animate().cancel();
            arrowView.setRotation(rotation);
        }
        arrowView.setContentDescription(accountsShown ? LocaleController.getString("AccDescrHideAccounts", R.string.AccDescrHideAccounts) : LocaleController.getString("AccDescrShowAccounts", R.string.AccDescrShowAccounts));
    }
}
