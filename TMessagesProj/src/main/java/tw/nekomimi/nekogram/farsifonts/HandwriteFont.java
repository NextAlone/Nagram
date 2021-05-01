package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.g */
public class HandwriteFont implements Font {
    private Typeface f1128a;

    public int m1181a() {
        return 8;
    }

    public String m1182b() {
        return LocaleController.getString("Handwrite", C0338R.string.Handwrite);
    }

    public Typeface m1183c() {
        return m1184d();
    }

    public Typeface m1184d() {
        if (this.f1128a == null) {
            this.f1128a = AndroidUtilities.getTypeface("fonts/dastnevis.ttf");
        }
        return this.f1128a;
    }
}
