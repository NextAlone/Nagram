package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.l */
public class IranSansMediumFont implements Font {
    private Typeface f1133a;

    public int m1201a() {
        return 4;
    }

    public String m1202b() {
        return LocaleController.getString("IranSansMedium", C0338R.string.IranSansMedium);
    }

    public Typeface m1203c() {
        return m1204d();
    }

    public Typeface m1204d() {
        if (this.f1133a == null) {
            this.f1133a = AndroidUtilities.getTypeface("fonts/iransans_medium.ttf");
        }
        return this.f1133a;
    }
}
