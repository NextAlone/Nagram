package tw.nekomimi.nekogram;

import android.app.Activity;

import org.telegram.messenger.FileLog;

public class ExternalGcm {

    interface Interface {
        boolean checkPlayServices();

        void initPlayServices();

        void sendRegistrationToServer();

        void checkUpdate(Activity ctx);
    }

    static class NoImpl implements Interface {

        @Override
        public boolean checkPlayServices() {
            return false;
        }

        @Override
        public void initPlayServices() {
        }

        @Override
        public void sendRegistrationToServer() {
        }

        @Override
        public void checkUpdate(Activity ctx) {
        }

    }

    private static Interface impl;

    static {
        try {
            impl = (Interface) Class.forName("tw.nekomimi.nekogram.GcmImpl").newInstance();
        } catch (ClassNotFoundException e) {
            impl = new NoImpl();
        } catch (Exception e) {
            impl = new NoImpl();
            FileLog.e(e);
        }
    }

    public static void initPlayServices() {
        impl.initPlayServices();
    }

    public static boolean checkPlayServices() {
        return impl.checkPlayServices();
    }

    public static void sendRegistrationToServer() {
        impl.sendRegistrationToServer();
    }


    public static void checkUpdate(Activity ctx) {
        impl.checkUpdate(ctx);
    }

}
