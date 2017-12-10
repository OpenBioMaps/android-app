package hu.ektf.iot.openbiomapsapp.util;

import android.location.Location;

import java.util.Locale;


public class GeometryConverter {
    public static String LocationToString(Location loc) throws Exception {
        if (loc == null) throw new IllegalArgumentException("Location should not be null");
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        String geometryString = String.format(Locale.ENGLISH, "POINT(%f %f)", latitude, longitude);
        return geometryString;
    }
}
