package hu.ektf.iot.openbiomapsapp.helper;

import android.location.Location;

/**
 * Created by Pádi on 2015. 11. 26..
 */
public class GeometryConverter {
    public static String LocationToString(Location loc) throws Exception {
        if(loc == null) throw new Exception("LocationCannotBeNull");
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        String geometryString = "POINT: " + String.valueOf(latitude) + "," + String.valueOf(longitude);
        return geometryString;
    }
}
