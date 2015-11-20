package hu.ektf.iot.openbiomapsapp;

import retrofit.RestAdapter;
import timber.log.Timber;

/**
 * Created by szugyi on 20/11/15.
 */
public class DebugBioMapsApplication extends BioMapsApplication {

    @Override
    protected RestAdapter.LogLevel getRetrofitLogLevel() {
        return RestAdapter.LogLevel.FULL;
    }

    @Override
    protected void setupLogging() {
        Timber.plant(new Timber.DebugTree());
    }
}
