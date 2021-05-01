package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.j */
public class IranSansFont implements Font {
    private Typeface f1131a;

    public int m1193a() {
        return 3;
    }

    public String m1194b() {
        return LocaleController.getString("IranSans", C0338R.string.IranSans);
    }

    public Typeface m1195c() {
        return m1196d();
    }

    public Typeface m1196d() {
        if (this.f1131a == null) {
            this.f1131a = AndroidUtilities.getTypeface("fonts/iransans.ttf");
        }
        return this.f1131a;
    }
}
