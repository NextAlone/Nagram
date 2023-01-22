package tw.nekomimi.nekogram.location;


import android.location.Location;
import android.util.Pair;

import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

public class NekoLocation {
    public final static Set<Integer> recent = Collections.synchronizedSet(Collections.newSetFromMap(new Cache<>()));

    public static void transform(Location location) {
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        if (recent.contains(new Pair<>(latitude, longitude).hashCode())) return;

        final Pair<Double, Double> trans = GeodeticTransform.transform(latitude, longitude);
        location.setLatitude(trans.first);
        location.setLongitude(trans.second);

        recent.add(trans.hashCode());

        if (BuildVars.LOGS_ENABLED) {
            FileLog.d(String.format(Locale.US, "%.4f,%.4f => %.4f,%.4f", latitude, longitude, trans.first, trans.second));
        }
    }

    static class Cache<K, V> extends LinkedHashMap<K, V> {

        private static final int KMaxEntries = 128;
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(final Entry<K, V> eldest) {
            return (size() > KMaxEntries);
        }
    }
}
