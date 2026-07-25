package org.telegram.messenger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.telegram.tgnet.TLRPC;

import java.lang.reflect.Field;
import java.util.concurrent.CopyOnWriteArraySet;

@RunWith(RobolectricTestRunner.class)
@Config(application = android.app.Application.class)
public class UserConfigAccountLimitTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();
        ApplicationLoader.applicationContext = context;
        resetState();
    }

    @After
    public void tearDown() throws Exception {
        resetState();
    }

    @Test
    public void defaultLimitUnlocksExtendedSlotsOnFifthAttempt() {
        activateAccounts(UserConfig.MAX_ACCOUNT_DEFAULT_COUNT, false);

        assertEquals(128, UserConfig.MAX_ACCOUNT_COUNT);
        assertEquals(8, UserConfig.getMaxAccountCount());
        for (int i = 0; i < 4; i++) {
            assertEquals(-1, UserConfig.requestAccountSlot());
            assertFalse(SharedConfig.isExtendedAccountLimitUnlocked());
        }

        assertEquals(8, UserConfig.requestAccountSlot());
        assertTrue(SharedConfig.isExtendedAccountLimitUnlocked());
        assertEquals(8, UserConfig.requestAccountSlot());
    }

    @Test
    public void premiumLimitRemainsTenBeforeHiddenUnlock() {
        activateAccounts(UserConfig.MAX_ACCOUNT_PREMIUM_COUNT, true);

        assertEquals(10, UserConfig.getMaxAccountCount());
        for (int i = 0; i < 4; i++) {
            assertEquals(-1, UserConfig.requestAccountSlot());
        }

        assertEquals(10, UserConfig.requestAccountSlot());
    }

    private void activateAccounts(int count, boolean premium) {
        for (int account = 0; account < count; account++) {
            TLRPC.TL_user user = new TLRPC.TL_user();
            user.id = account + 1;
            user.premium = premium && account == 0;
            UserConfig.getInstance(account).setCurrentUser(user);
            SharedConfig.activeAccounts.add(account);
        }
    }

    private void resetState() throws Exception {
        SharedConfig.activeAccounts = new CopyOnWriteArraySet<>();
        clearSparseArray(UserConfig.class, "Instance");
        clearSparseArray(AccountInstance.class, "Instance");
        setStaticField(UserConfig.class, "extendedAccountUnlockAttempts", 0);
        setStaticField(SharedConfig.class, "extendedAccountLimitUnlocked", false);
        context.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit()
                .remove("extended_account_limit_unlocked")
                .commit();
    }

    private static void clearSparseArray(Class<?> owner, String fieldName) throws Exception {
        Field field = owner.getDeclaredField(fieldName);
        field.setAccessible(true);
        ((SparseArray<?>) field.get(null)).clear();
    }

    private static void setStaticField(Class<?> owner, String fieldName, Object value) throws Exception {
        Field field = owner.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
