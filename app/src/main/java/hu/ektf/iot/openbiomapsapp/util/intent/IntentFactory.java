package hu.ektf.iot.openbiomapsapp.util.intent;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

public final class IntentFactory {
    public static final int REQUEST_CODE_PICK_PLACE = 1000;

    private IntentFactory() {
        // utility class
    }

    @NonNull
    public static Intent createPickPlaceIntent(Activity activity) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        return builder.build(activity);
    }
}
