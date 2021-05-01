package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.a */
public class AfsanehFont implements Font {
    private Typeface f1120a;

    public int m1162a() {
        return 10;
    }

    public String m1163b() {
        return LocaleController.getString("Afsaneh", C0338R.string.Afsaneh);
    }

    public Typeface m1164c() {
        return m1165d();
    }

    public Typeface m1165d() {
        if (this.f1120a == null) {
            this.f1120a = AndroidUtilities.getTypeface("fonts/afsaneh.ttf");
        }
        return this.f1120a;
    }
}
