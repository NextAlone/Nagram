package com.hanista.mobogram.mobo.p008i;

import android.graphics.Typeface;
import com.hanista.mobogram.C0338R;
import com.hanista.mobogram.messenger.AndroidUtilities;
import com.hanista.mobogram.messenger.LocaleController;
import com.hanista.mobogram.mobo.MoboConstants;
import java.io.File;

/* renamed from: com.hanista.mobogram.mobo.i.b */
public class CustomFont implements Font {
    private Typeface f1121a;

    public int m1166a() {
        return -1;
    }

    public String m1167b() {
        return LocaleController.getString("Custom", C0338R.string.Custom);
    }

    public Typeface m1168c() {
        return m1169d();
    }

    public Typeface m1169d() {
        if (this.f1121a == null) {
            File file = new File(new File(new File(MoboConstants.m1381b(), MoboConstants.f1325R), "Font"), "customfont.ttf");
            if (file.exists()) {
                this.f1121a = AndroidUtilities.getTypefaceFromFile(file.getAbsolutePath());
            } else {
                FontUtil.m1177a(1);
                FontUtil.m1180d();
                this.f1121a = FontUtil.m1176a().m1161d();
            }
        }
        return this.f1121a;
    }
}
