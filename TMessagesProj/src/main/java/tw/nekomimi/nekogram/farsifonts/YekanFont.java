package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.o */
public class YekanFont implements Font {
    private Typeface f1137a;

    public int m1214a() {
        return 6;
    }

    public String m1215b() {
        return LocaleController.getString("Yekan", C0338R.string.Yekan);
    }

    public Typeface m1216c() {
        return m1217d();
    }

    public Typeface m1217d() {
        if (this.f1137a == null) {
            this.f1137a = AndroidUtilities.getTypeface("fonts/byekan.ttf");
        }
        return this.f1137a;
    }
}
