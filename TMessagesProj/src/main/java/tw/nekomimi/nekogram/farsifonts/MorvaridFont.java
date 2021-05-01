package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.n */
public class MorvaridFont implements Font {
    private Typeface f1136a;

    public int m1210a() {
        return 9;
    }

    public String m1211b() {
        return LocaleController.getString("Morvarid", C0338R.string.Morvarid);
    }

    public Typeface m1212c() {
        return m1213d();
    }

    public Typeface m1213d() {
        if (this.f1136a == null) {
            this.f1136a = AndroidUtilities.getTypeface("fonts/morvarid.ttf");
        }
        return this.f1136a;
    }
}
