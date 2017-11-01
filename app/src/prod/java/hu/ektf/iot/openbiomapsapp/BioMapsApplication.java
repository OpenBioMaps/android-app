package hu.ektf.iot.openbiomapsapp;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.text.TextUtils;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

import hu.ektf.iot.openbiomapsapp.database.AppDatabase;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepo;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepoImpl;
import hu.ektf.iot.openbiomapsapp.screen.LoginActivity;
import hu.ektf.iot.openbiomapsapp.upload.BioMapsService;
import hu.ektf.iot.openbiomapsapp.upload.DynamicEndpoint;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import timber.log.Timber;

public class BioMapsApplication extends BaseApplication {

    private ObmRepo repo;
    private StorageHelper storage;
    private DynamicEndpoint dynamicEndpoint;
    private BioMapsService mapsService;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        setupRetrofit();

        repo = new ObmRepoImpl(this);
        storage = new StorageHelper(this);
        database = Room.databaseBuilder(this,
                AppDatabase.class, "OBM-database").build();
    }

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

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            okHttpClient.networkInterceptors().add(new StethoInterceptor());
        }
    }

    private Interceptor getAuthInterceptor() {
        return chain -> {
            Request originalRequest = chain.request();

            String accessToken = storage.getAccessToken();
            if (TextUtils.isEmpty(accessToken)) {
                return chain.proceed(originalRequest);
            }

            Request authorisedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            return chain.proceed(authorisedRequest);
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
                    storage.clearTokens();
                    Intent intent = new Intent(BioMapsApplication.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
        return BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;
    }

    public BioMapsService getMapsService() {
        return mapsService;
    }

    public DynamicEndpoint getDynamicEndpoint() {
        return dynamicEndpoint;
    }

    public AppDatabase getAppDatabase() {
        return database;
    }
}
