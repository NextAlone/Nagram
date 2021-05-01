package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.i */
public class IranSansBoldFont implements Font {
    private Typeface f1130a;

    public int m1189a() {
        return 5;
    }

    public String m1190b() {
        return LocaleController.getString("IranSansBold", C0338R.string.IranSansBold);
    }

    public Typeface m1191c() {
        return m1192d();
    }

    public Typeface m1192d() {
        if (this.f1130a == null) {
            this.f1130a = AndroidUtilities.getTypeface("fonts/iransans_bold.ttf");
        }
        return this.f1130a;
    }
}
