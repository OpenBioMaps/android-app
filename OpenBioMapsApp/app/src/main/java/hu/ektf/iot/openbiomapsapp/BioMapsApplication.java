package hu.ektf.iot.openbiomapsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.database.BioMapsContentObserver;
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

    public static final String DEFAULT_END_POINT = "http://openbiomaps.org/pds";
    private DynamicEndpoint dynamicEndpoint;
    private BioMapsServiceInterface mapsService;

    private void setupRetrofit() {
        dynamicEndpoint = new DynamicEndpoint();
        dynamicEndpoint.setUrl(DEFAULT_END_POINT);
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

    protected void registerContentObserver() {
        BioMapsContentObserver observer = new BioMapsContentObserver(account, null);
        getContentResolver().registerContentObserver(BioMapsContentProvider.CONTENT_URI, true, observer);
        getContentResolver().setSyncAutomatically(account, BioMapsContentProvider.AUTHORITY, true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupRetrofit();
        setupLogging();
        if(!isSyncProcess(this))
        {
            createSyncAccount(this);
            registerContentObserver();
        }
    }

    public BioMapsServiceInterface getMapsService() {
        return mapsService;
    }

    public DynamicEndpoint getDynamicEndpoint(){
        return dynamicEndpoint;
    }

    public Account getAccount() {
        return account;
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

    // TODO Is it a good idea?
    private boolean isSyncProcess(Context context)
    {
        Context applicationContext = context.getApplicationContext();
        long myPid = (long) android.os.Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) applicationContext.getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses();
        if (runningAppProcesses != null && runningAppProcesses.size() != 0)
        {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses)
            {
                if (((long) runningAppProcessInfo.pid) == myPid && "hu.ektf.iot.openbiomapsapp:sync".equals(runningAppProcessInfo.processName))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
