package hu.ektf.iot.openbiomapsapp;

import android.app.Application;

import retrofit.RestAdapter;

/**
 * Created by szugyi on 20/11/15.
 */
public class BioMapsApplication extends Application {
    public final static String END_POINT = "http://openbiomaps.org/pds";
    private BioMapsService mapsService;

    @Override
    public void onCreate() {
        super.onCreate();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        mapsService = restAdapter.create(BioMapsService.class);
    }

    public BioMapsService getMapsService(){
        return mapsService;
    }
}
