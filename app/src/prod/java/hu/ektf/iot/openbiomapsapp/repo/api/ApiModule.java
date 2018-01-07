package hu.ektf.iot.openbiomapsapp.repo.api;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.BuildConfig;
import hu.ektf.iot.openbiomapsapp.repo.database.StorageHelper;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module
public class ApiModule {

    @Provides
    BioMapsApplication bindApplication(Application application) {
        return (BioMapsApplication) application;
    }

    @Provides
    @Singleton
    ObmApi provideObmApi(DynamicEndpoint endpoint, OkHttpClient okHttpClient) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();
        return restAdapter.create(ObmApi.class);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Context context, Authenticator authenticator, Interceptor authInterceptor) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(authInterceptor);
        okHttpClient.setAuthenticator(authenticator);

        // TODO Extract to separate interceptor
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(context);
            okHttpClient.networkInterceptors().add(new StethoInterceptor());
        }

        return okHttpClient;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    Authenticator provideAuthenticator(TokenAuthenticator authenticator) {
        return authenticator;
    }

    @Provides
    @Singleton
    Interceptor provideAuthInterceptor(StorageHelper storage) {
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
}
