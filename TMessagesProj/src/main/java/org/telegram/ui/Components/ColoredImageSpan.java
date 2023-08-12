package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.Theme;

public class ColoredImageSpan extends ReplacementSpan {

    int drawableColor;
    Drawable drawable;

    boolean usePaintColor = true;
    int colorKey;
    private int topOffset = 0;
    private float translateX;

    private int size;
    private int sizeWidth;

    public static final int ALIGN_DEFAULT = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2;
    private final int verticalAlignment;
    private float scale = 1f;
    private Runnable checkColorDelegate;

    public ColoredImageSpan(int imageRes) {
        this(imageRes, ALIGN_DEFAULT);
    }

    public ColoredImageSpan(Drawable drawable) {
        this(drawable, ALIGN_DEFAULT);
    }

    public ColoredImageSpan(int imageRes, int verticalAlignment) {
        this(ContextCompat.getDrawable(ApplicationLoader.applicationContext, imageRes).mutate(), verticalAlignment);
    }

    public ColoredImageSpan(Drawable drawable, int verticalAlignment) {
        this.drawable = drawable;
        this.usePaintColor = true;
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.setColorFilter(new PorterDuffColorFilter(drawableColor, PorterDuff.Mode.SRC_IN));
        }
        this.verticalAlignment = verticalAlignment;
    }

    public ColoredImageSpan(Drawable drawable, int drawableColor, int verticalAlignment) {
        this.drawable = drawable;
        this.drawableColor = drawableColor;
        this.usePaintColor = true;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.setColorFilter(new PorterDuffColorFilter(drawableColor, PorterDuff.Mode.SRC_IN));
        this.verticalAlignment = verticalAlignment;
    }

    public void setSize(int size) {
        this.size = size;
        drawable.setBounds(0, 0, size, size);
    }
    public void setTranslateX(float tx) {
        translateX = tx;
    }

    public void setWidth(int width) {
        sizeWidth = width;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence charSequence, int i, int i1, @Nullable Paint.FontMetricsInt fontMetricsInt) {
        if (sizeWidth != 0)
            return (int) (scale * sizeWidth);
        return (int) (scale * (size != 0 ? size : drawable.getIntrinsicWidth()));
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        int color;
        if (checkColorDelegate != null) {
            checkColorDelegate.run();
        } else {
            if (usePaintColor) {
                color = paint.getColor();
            } else {
                color = Theme.getColor(colorKey);
            }
            if (drawableColor != color) {
                drawableColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(drawableColor, PorterDuff.Mode.MULTIPLY));
            }
        }

        canvas.save();
        int transY = bottom - (drawable != null ? drawable.getBounds().bottom : bottom);
        if (verticalAlignment == ALIGN_BASELINE) {
//            transY -= paint.getFontMetricsInt().descent;
        } else if (verticalAlignment == ALIGN_CENTER) {
            transY = top + (bottom - top) / 2 - (drawable != null ? drawable.getBounds().height() / 2 : 0);
        } else if (verticalAlignment == ALIGN_DEFAULT) {
            int lineHeight = bottom - top;
            int drawableHeight = size != 0 ? size : drawable.getIntrinsicHeight();
            int padding = (lineHeight - drawableHeight) / 2;
            transY = top + padding + AndroidUtilities.dp(topOffset);
        }
        canvas.translate(x + translateX, transY);
        if (drawable != null) {
            if (scale != 1f) {
                canvas.scale(scale, scale, 0, drawable.getBounds().centerY());
            }
            drawable.draw(canvas);
        }
        canvas.restore();
    }

    public void setColorKey(int colorKey) {
        this.colorKey = colorKey;
        usePaintColor = colorKey < 0;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
    }

    public void setCheckColorDelegate(Runnable checkColorDelegate) {
        this.checkColorDelegate = checkColorDelegate;
    }

    public void setScale(float v) {
        scale = v;
    }
}
