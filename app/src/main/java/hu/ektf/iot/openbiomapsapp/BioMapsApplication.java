package hu.ektf.iot.openbiomapsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import hu.ektf.iot.openbiomapsapp.di.DaggerAppComponent;
import hu.ektf.iot.openbiomapsapp.util.CrashlyticsLogTree;
import hu.ektf.iot.openbiomapsapp.sync.StubContentProvider;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.SilentLogger;
import timber.log.Timber;

public class BioMapsApplication extends DaggerApplication {
    public static final String ACCOUNT_TYPE = "openbiomaps.org";
    public static final String ACCOUNT_NAME = "default";
    private Account account;

    private void setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsLogTree());
        }
    }

    private void setupFabric() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(new Fabric.Builder(this)
                    .logger(new SilentLogger())
                    .kits(new Crashlytics())
                    .build());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupLogging();
        setupFabric();
        createSyncAccount(this);
    }

    public void requestSync() {
        Timber.i("requestSync method was called. Sync will be requested");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, StubContentProvider.AUTHORITY, bundle);
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

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
