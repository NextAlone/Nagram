package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.m */
public class MainFont implements Font {
    private Typeface f1134a;
    private Typeface f1135b;

    public int m1205a() {
        return 1;
    }

    public String m1206b() {
        return LocaleController.getString("DefaultFont", C0338R.string.DefaultFont);
    }

    public Typeface m1207c() {
        if (this.f1134a == null) {
            this.f1134a = AndroidUtilities.getTypeface("fonts/rmedium.ttf");
        }
        return this.f1134a;
    }

    public Typeface m1208d() {
        return null;
    }

    public Typeface m1209e() {
        if (this.f1135b == null) {
            this.f1135b = AndroidUtilities.getTypeface("fonts/ritalic.ttf");
        }
        return this.f1135b;
    }
}
