package hu.ektf.iot.openbiomapsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

import hu.ektf.iot.openbiomapsapp.database.BioMapsContentProvider;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import hu.ektf.iot.openbiomapsapp.repo.ObmClient;
import hu.ektf.iot.openbiomapsapp.repo.ObmClientImpl;
import hu.ektf.iot.openbiomapsapp.upload.BioMapsService;
import hu.ektf.iot.openbiomapsapp.upload.DynamicEndpoint;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import timber.log.Timber;

/**
 * Created by szugyi on 20/11/15.
 */
public class BioMapsApplication extends Application {
    public static final String ACCOUNT_TYPE = "openbiomaps.org";
    public static final String ACCOUNT_NAME = "default";
    private Account account;

    private ObmClient repo;
    private StorageHelper storage;
    private DynamicEndpoint dynamicEndpoint;
    private BioMapsService mapsService;

    private void setupRetrofit() {
        // TODO: Will we handle dynamic endpoint?
        dynamicEndpoint = new DynamicEndpoint();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(getAuthInterceptor());
        okHttpClient.setAuthenticator(getOAuthAuthenticator());

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //dynamicEndpoint)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(getRetrofitLogLevel())
                .build();
        mapsService = restAdapter.create(BioMapsService.class);
    }

    private Interceptor getAuthInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                String accessToken = storage.getAccessToken();
                if (TextUtils.isEmpty(accessToken)) {
                    return chain.proceed(originalRequest);
                }

                Request authorisedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + accessToken)
                        .build();

                return chain.proceed(authorisedRequest);
            }
        };
    }

    private Authenticator getOAuthAuthenticator() {
        return new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) throws IOException {
                String refreshToken = storage.getRefreshToken();
                if (TextUtils.isEmpty(refreshToken)) {
                    return null;
                }

                try {
                    TokenResponse tokenResponse = repo.refreshToken(refreshToken);
                    return response.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + tokenResponse.getAccessToken())
                            .build();
                } catch (Throwable throwable) {
                    Timber.e(throwable);
                    return null;
                }
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                return null;
            }
        };
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
        repo = new ObmClientImpl(this);
        storage = new StorageHelper(this);
        setupRetrofit();
        setupLogging();
        createSyncAccount(this);
    }

    public BioMapsService getMapsService() {
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
