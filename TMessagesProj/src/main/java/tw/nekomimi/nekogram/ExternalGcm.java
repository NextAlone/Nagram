package tw.nekomimi.nekogram;

public class ExternalGcm {

    public static Interface INSTANCE; static {

        try {

            INSTANCE = (Interface) Class.forName("tw.nekomimi.nekogram.GcmImpl").newInstance();

        } catch (Exception ex) {
        }

    }

    public interface Interface {

        void initPlayServices();
        void sendRegistrationToServer();

    }

}
