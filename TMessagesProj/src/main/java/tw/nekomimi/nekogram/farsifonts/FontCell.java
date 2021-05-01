package com.hanista.mobogram.mobo.p008i;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.vision.face.Face;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.messenger.exoplayer.C0700C;
import com.hanista.mobogram.messenger.volley.DefaultRetryPolicy;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import com.hanista.mobogram.tgnet.TLRPC;
import com.hanista.mobogram.ui.ActionBar.Theme;
import com.hanista.mobogram.ui.Components.LayoutHelper;

/* renamed from: com.hanista.mobogram.mobo.i.e */
public class FontCell extends FrameLayout {
    private static Paint f1122d;
    private TextView f1123a;
    private TextView f1124b;
    private ImageView f1125c;
    private boolean f1126e;

    public FontCell(Context context) {
        int i = 3;
        super(context);
        if (f1122d == null) {
            f1122d = new Paint();
            f1122d.setColor(-2500135);
            f1122d.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.f1123a = new TextView(context);
        this.f1123a.setTextColor(Theme.STICKERS_SHEET_TITLE_TEXT_COLOR);
        this.f1123a.setTextSize(1, 16.0f);
        this.f1123a.setLines(1);
        this.f1123a.setMaxLines(1);
        this.f1123a.setSingleLine(true);
        this.f1123a.setEllipsize(TruncateAt.END);
        this.f1123a.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.f1123a, LayoutHelper.createFrame(-1, Face.UNCOMPUTED_PROBABILITY, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.f1124b = new TextView(context);
        this.f1124b.setTextColor(ThemeUtil.m2485a().m2289c());
        this.f1124b.setTextSize(1, 16.0f);
        this.f1124b.setLines(1);
        this.f1124b.setMaxLines(1);
        this.f1124b.setSingleLine(true);
        this.f1124b.setEllipsize(TruncateAt.END);
        this.f1124b.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.f1124b, LayoutHelper.createFrame(-2, Face.UNCOMPUTED_PROBABILITY, (LocaleController.isRTL ? 3 : 5) | 48, 17.0f, 0.0f, 17.0f, 0.0f));
        this.f1125c = new ImageView(context);
        this.f1125c.setScaleType(ScaleType.CENTER);
        this.f1125c.setVisibility(4);
        View view = this.f1125c;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 16, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    private void m1174a() {
        if (ThemeUtil.m2490b()) {
            int i = AdvanceTheme.f2497h;
            int i2 = AdvanceTheme.f2498i;
            int i3 = AdvanceTheme.f2494e;
            int i4 = AdvanceTheme.f2499j;
            if ((getTag() != null ? getTag().toString() : TtmlNode.ANONYMOUS_REGION_ID).contains("Profile")) {
                int i5 = AdvanceTheme.aA;
                setBackgroundColor(i5);
                if (i5 != -1) {
                    f1122d.setColor(i5);
                }
                this.f1123a.setTextColor(AdvanceTheme.aB);
                if (i5 != -1) {
                    this.f1124b.setTextColor(0);
                    return;
                }
                return;
            }
            setBackgroundColor(i);
            this.f1123a.setTextColor(i3);
            f1122d.setColor(i2);
            this.f1124b.setTextColor(i4);
        }
    }

    public void m1175a(String str, Font font, boolean z) {
        this.f1123a.setTypeface(font.m1161d());
        this.f1123a.setText(str);
        this.f1124b.setVisibility(4);
        this.f1125c.setVisibility(4);
        this.f1126e = z;
        setWillNotDraw(!z);
    }

    protected void onDraw(Canvas canvas) {
        if (this.f1126e) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), f1122d);
        }
    }

    protected void onMeasure(int i, int i2) {
        m1174a();
        setMeasuredDimension(MeasureSpec.getSize(i), (this.f1126e ? 1 : 0) + AndroidUtilities.dp(48.0f));
        int measuredWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        int i3 = measuredWidth / 2;
        if (this.f1125c.getVisibility() == 0) {
            this.f1125c.measure(MeasureSpec.makeMeasureSpec(i3, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
        }
        if (this.f1124b.getVisibility() == 0) {
            this.f1124b.measure(MeasureSpec.makeMeasureSpec(i3, TLRPC.MESSAGE_FLAG_MEGAGROUP), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
            measuredWidth = (measuredWidth - this.f1124b.getMeasuredWidth()) - AndroidUtilities.dp(8.0f);
        }
        this.f1123a.measure(MeasureSpec.makeMeasureSpec(measuredWidth, C0700C.ENCODING_PCM_32BIT), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C0700C.ENCODING_PCM_32BIT));
    }

    public void setTextColor(int i) {
        this.f1123a.setTextColor(i);
        m1174a();
    }
}
