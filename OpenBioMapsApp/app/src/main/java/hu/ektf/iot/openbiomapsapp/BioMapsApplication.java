package hu.ektf.iot.openbiomapsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import hu.ektf.iot.openbiomapsapp.database.BioMapsContentProvider;
import hu.ektf.iot.openbiomapsapp.upload.BioMapsServiceInterface;
import hu.ektf.iot.openbiomapsapp.upload.DynamicEndpoint;
import retrofit.RestAdapter;
import timber.log.Timber;

/**
 * Created by szugyi on 20/11/15.
 */
public class BioMapsApplication extends Application {
    public static final String ACCOUNT_TYPE = "openbiomaps.org";
    public static final String ACCOUNT_NAME = "default";
    private Account account;

    private DynamicEndpoint dynamicEndpoint;
    private BioMapsServiceInterface mapsService;

    private void setupRetrofit() {
        dynamicEndpoint = new DynamicEndpoint();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(dynamicEndpoint)
                .setLogLevel(getRetrofitLogLevel())
                .build();
        mapsService = restAdapter.create(BioMapsServiceInterface.class);
    }

    protected RestAdapter.LogLevel getRetrofitLogLevel() {
        return RestAdapter.LogLevel.NONE;
    }

    protected void setupLogging() {
        // Placeholder for debug application
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupRetrofit();
        setupLogging();
        createSyncAccount(this);
    }

    public BioMapsServiceInterface getMapsService() {
        return mapsService;
    }

    public DynamicEndpoint getDynamicEndpoint() {
        return dynamicEndpoint;
    }

    public Account getAccount() {
        return account;
    }

    public void requestSync() {
        Timber.i("requestSync method was called. Sync will be requested");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, BioMapsContentProvider.AUTHORITY, bundle);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    private void createSyncAccount(Context context) {
        // Create the account type and default account
        account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(account, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            Timber.i("Account was created successfully");
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Timber.i("Account was not created!");
        }
    }
}
