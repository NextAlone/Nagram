package tw.nekomimi.nekogram.utils;
import java.lang.Thread;

public class NaState {
        private static boolean allowReadPacket = false; // 是否允许发送已读状态
        private static int allowReadPacketTimeout = 0; // 超时时间（单位：秒）

        /**
         * 获取当前是否允许发送已读状态。
         * @return 是否允许发送。
         */
        public static boolean getAllowReadPacket() {
            return allowReadPacket;
        }

        /**
         * 设置是否允许发送已读状态。
         * @param allow 是否允许。
         * @param timeout 超时时间（单位：秒）。
         */
        public static void setAllowReadPacket(boolean allow, int timeout) {
            allowReadPacket = allow;
            allowReadPacketTimeout = timeout;

            if (timeout > 0) {
                new Thread(() -> {
                    try {
                        Thread.sleep(timeout * 1000L);
                    } catch (InterruptedException ignored) {}
                    allowReadPacket = false;
                }).start();
            }
        }
    }

