package hu.ektf.iot.openbiomapsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import hu.ektf.iot.openbiomapsapp.upload.BioMapsServiceInterface;
import hu.ektf.iot.openbiomapsapp.upload.FileMapCreator;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import timber.log.Timber;

/**
 * Created by szugyi on 20/11/15.
 */
public class BioMapsApplication extends Application {
    public static final String ACCOUNT_TYPE = "openbiomaps.org";
    public static final String ACCOUNT = "default";
    private Account account;

    // TODO Make it dynamic
    public final static String END_POINT = "http://openbiomaps.org/pds";
    private BioMapsServiceInterface mapsService;

    private void setupRetrofit() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
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
        account = createSyncAccount(this);
    }

    public BioMapsServiceInterface getMapsService() {
        return mapsService;
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    private Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
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
        return newAccount;
    }

    // TODO remove after implementing upload
    public void testService() {
        ArrayList<String> files = new ArrayList<String>();
        files.add("/storage/external_SD/sample.jpg");
        files.add("/storage/external_SD/sample2.jpg");
        Map<String, TypedFile> fileMap = FileMapCreator.createFileMap(files);

        getMapsService().uploadNote("abc123", "PFS", "mapp", "Android test", "2015.11.20 15:11:00", "POINT(3.14 4.13)", fileMap, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Timber.i("Upload test", "SUCCESS");
                Timber.i("Upload test", s);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Upload test", "ERROR");
                error.printStackTrace();
            }
        });
    }
}
