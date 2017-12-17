package hu.ektf.iot.openbiomapsapp.util;



import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * This class is responsible for keeping track of the location changes and initializing the current location
 * to the last known one when the object is first instantiated.
 */
public class GpsHelper {
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final long UPDATE_TIME = 500;
    private static final int UPDATE_DISTANCE = 0;

    private static Location location;

    private static void setLocation(Location newLocation) {
        GpsHelper.location = newLocation;
    }

    /**
     * Get the location which is currently the best one we know.
     *
     * @return The current location.
     */
    public static Location getLocation() {
        return GpsHelper.location;
    }

    private LocationManager locationManager;
    private LocationListener internalLocationListener = new InternalLocationListener();
    private LocationListener externalListener = null;

    public void setExternalListener(LocationListener ll) {
        this.externalListener = ll;
    }

    /**
     * Create an instance which is listening for location updates and saves them in a static variable,
     * so it can be accessed from anywhere.
     *
     * @param context The context to be used to access the LOCATION_SERVICE.
     */
    public GpsHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (getLocation() == null) {
            String[] providers = new String[]{LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER,
                    LocationManager.PASSIVE_PROVIDER};

            int i = 0;
            while (i < providers.length) {
                setLastKnownLocation(providers[i]);
                i++;
            }
        }
    }

    public void onResume() {
        String[] providers = locationManager.getAllProviders().toArray(new String[0]);
        registerProviders(providers);
    }

    public void onPause() {
        unregisterProviders();
    }

    private void setLastKnownLocation(String providerName) {
        Location lastKnownLocation = locationManager
                .getLastKnownLocation(providerName);
        if (lastKnownLocation != null) {
            if (isBetterLocation(lastKnownLocation, getLocation())) {
                location = lastKnownLocation;
            }
            return;
        }

    }

    private void registerProviders(String[] providers) {
        for (int i = 0; i < providers.length; i++) {
            locationManager.requestLocationUpdates(providers[i], UPDATE_TIME, UPDATE_DISTANCE, internalLocationListener);
        }
    }

    /**
     * Unregisters the locationListener of the class from location updates. Must be called if
     * location updates are no more required by the app.
     */
    private void unregisterProviders() {
        locationManager.removeUpdates(internalLocationListener);
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    private static boolean isBetterLocation(Location location, Location currentBestLocation) {
        //Timber.v("isBetterLocation called");
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        //Timber.v("isSameProvider called");
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private class InternalLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Location currentLocation = getLocation();
            if (externalListener != null) {
                externalListener.onLocationChanged(location);
            }

            if (isBetterLocation(location, currentLocation)) {
                setLocation(location);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }
}
