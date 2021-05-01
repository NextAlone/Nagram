package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.k */
public class IranSansLightFont implements Font {
    private Typeface f1132a;

    public int m1197a() {
        return 2;
    }

    public String m1198b() {
        return LocaleController.getString("IranSansLight", C0338R.string.IranSansLight);
    }

    public Typeface m1199c() {
        return m1200d();
    }

    public Typeface m1200d() {
        if (this.f1132a == null) {
            this.f1132a = AndroidUtilities.getTypeface("fonts/iransans_light.ttf");
        }
        return this.f1132a;
    }
}
