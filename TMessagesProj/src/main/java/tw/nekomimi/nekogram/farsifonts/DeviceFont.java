package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.LocaleController;

/* renamed from: com.hanista.mobogram.mobo.i.c */
public class DeviceFont implements Font {
    public int m1170a() {
        return 11;
    }

    public String m1171b() {
        return LocaleController.getString("DeviceFont", C0338R.string.DeviceFont);
    }

    public Typeface m1172c() {
        return m1173d();
    }

    public Typeface m1173d() {
        return null;
    }
}
