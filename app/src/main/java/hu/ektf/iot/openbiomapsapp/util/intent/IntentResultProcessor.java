package hu.ektf.iot.openbiomapsapp.util.intent;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

public final class IntentResultProcessor {

    private IntentResultProcessor() {
        // utility class
    }

    @Nullable
    public static LatLng getLatLngFromPickedPlace(Context context, Intent result) {
        Place place = PlacePicker.getPlace(context, result);
        return place.getLatLng();
    }
}
