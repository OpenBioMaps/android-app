package hu.ektf.iot.openbiomapsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import hu.ektf.iot.openbiomapsapp.database.BioMapsContentProvider;
import timber.log.Timber;

public abstract class BaseApplication extends Application {

    public static final String ACCOUNT_TYPE = "openbiomaps.org";
    public static final String ACCOUNT_NAME = "default";
    private Account account;

    private void setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupLogging();
        createSyncAccount(this);
    }

    public void requestSync() {
        Timber.i("requestSync method was called. Sync will be requested");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, BioMapsContentProvider.AUTHORITY, bundle);
    }

    private void createSyncAccount(Context context) {
        account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(account, null, null)) {
            Timber.i("Account was created successfully");
        } else {
            Timber.i("Account was not created!");
        }
    }
}
