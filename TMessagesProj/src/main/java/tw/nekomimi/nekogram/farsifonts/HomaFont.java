package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.h */
public class HomaFont implements Font {
    private Typeface f1129a;

    public int m1185a() {
        return 7;
    }

    public String m1186b() {
        return LocaleController.getString("Homa", C0338R.string.Homa);
    }

    public Typeface m1187c() {
        return m1188d();
    }

    public Typeface m1188d() {
        if (this.f1129a == null) {
            this.f1129a = AndroidUtilities.getTypeface("fonts/hama.ttf");
        }
        return this.f1129a;
    }
}
