package hu.ektf.iot.openbiomapsapp.logging;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class CrashlyticsLogTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        Crashlytics.log(priority, tag, message);

        if (t != null) {
            Crashlytics.logException(t);
        }
    }

    @Override
    protected final boolean isLoggable(String tag, int priority) {
        return !(Log.DEBUG == priority || Log.VERBOSE == priority);
    }
}

