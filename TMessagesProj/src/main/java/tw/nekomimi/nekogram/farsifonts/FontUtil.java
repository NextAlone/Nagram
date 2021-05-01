package com.hanista.mobogram.mobo.p008i;

import com.hanista.mobogram.mobo.p004e.SettingManager;
import com.hanista.mobogram.mobo.p020s.AdvanceTheme;
import com.hanista.mobogram.mobo.p020s.ThemeUtil;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.hanista.mobogram.mobo.i.f */
public class FontUtil {
    private static Font f1127a;

    public static Font m1176a() {
        if (f1127a != null) {
            return f1127a;
        }
        int c = ThemeUtil.m2490b() ? AdvanceTheme.f2506q : FontUtil.m1179c();
        for (Font font : FontUtil.m1178b()) {
            if (font.m1158a() == c) {
                f1127a = font;
                break;
            }
        }
        if (c == -1) {
            f1127a = new CustomFont();
        }
        if (f1127a == null) {
            f1127a = new MainFont();
        }
        return f1127a;
    }

    public static void m1177a(int i) {
        if (ThemeUtil.m2490b()) {
            AdvanceTheme.m2285b(i);
        } else {
            new SettingManager().m942a("currentFont", i);
        }
        f1127a = null;
    }

    public static List<Font> m1178b() {
        List<Font> arrayList = new ArrayList();
        arrayList.add(new MainFont());
        arrayList.add(new DeviceFont());
        arrayList.add(new IranSansLightFont());
        arrayList.add(new IranSansFont());
        arrayList.add(new IranSansMediumFont());
        arrayList.add(new IranSansBoldFont());
        arrayList.add(new YekanFont());
        arrayList.add(new HomaFont());
        arrayList.add(new HandwriteFont());
        arrayList.add(new MorvaridFont());
        arrayList.add(new AfsanehFont());
        return arrayList;
    }

    public static int m1179c() {
        return new SettingManager().m941a("currentFont");
    }

    public static void m1180d() {
        f1127a = null;
    }
}
