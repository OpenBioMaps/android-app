package hu.ektf.iot.openbiomapsapp.view.input;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.util.GpsHelper;
import hu.ektf.iot.openbiomapsapp.util.intent.IntentFactory;
import hu.ektf.iot.openbiomapsapp.util.intent.IntentRequester;
import hu.ektf.iot.openbiomapsapp.util.intent.IntentResultProcessor;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@EViewGroup(R.layout.input_point)
public class PointInputView extends TextInputView {
    private static final String POINT_FORMAT = "point(%f %f)";

    @ViewById
    ProgressBar progress;

    private GpsHelper gpsHelper;

    public PointInputView(@NonNull Context context) {
        super(context);
        gpsHelper = new GpsHelper(context);
        gpsHelper.onResume();
    }

    @Override
    public void bind(FormControl control) {
        super.bind(control);
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        progress.setVisibility(View.GONE);
        gpsHelper.setExternalListener(null);
        gpsHelper.onPause();
    }

    @Click
    void locationClicked() {
        Location currentLocation = GpsHelper.getLocation();

        if (currentLocation != null) {
            setValue(currentLocation);
            return;
        }

        progress.setVisibility(View.VISIBLE);
        gpsHelper.setExternalListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setValue(location);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }

    @Click
    void mapClicked() {
        try {
            new IntentRequester(getContext()).startIntentForResult(
                    IntentFactory.createPickPlaceIntent((Activity) getContext()), IntentFactory.REQUEST_CODE_PICK_PLACE)
                    .observeOn(Schedulers.computation())
                    .filter(result -> result.getResultCode() == Activity.RESULT_OK)
                    .flatMapSingle(result -> {
                        LatLng latLng = IntentResultProcessor.getLatLngFromPickedPlace(getContext(),
                                result.getIntentData());

                        if (latLng != null) {
                            return Single.just(latLng);
                        } else {
                            return Single.error(new IllegalArgumentException("Place not found"));
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setValue, Timber::e);
        } catch (Exception e) {
            //TODO Handle exception properly
            Timber.e(e, "Place picking failed");
        }
    }

    private int getValue() {
        try {
            return Integer.parseInt(input.getText().toString());
        } catch (NumberFormatException ex) {
            Timber.e(ex, "Could not parse number input");
        }

        return 0;
    }

    private void setValue(Location location) {
        setValue(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void setValue(LatLng latLng) {
        input.setText(String.format(Locale.UK, POINT_FORMAT, latLng.latitude, latLng.longitude));
    }
}
