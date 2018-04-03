package hu.ektf.iot.openbiomapsapp.repo.api;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.Proxy;

import javax.inject.Inject;
import javax.inject.Provider;

import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepo;
import hu.ektf.iot.openbiomapsapp.repo.database.StorageHelper;
import hu.ektf.iot.openbiomapsapp.screen.LoginActivity;
import timber.log.Timber;

public class TokenAuthenticator implements Authenticator {
    private final Context context;
    private final StorageHelper storage;
    private final Provider<ObmRepo> repoProvider;

    @Inject
    public TokenAuthenticator(Context context, StorageHelper storage, Provider<ObmRepo> repoProvider) {
        this.context = context;
        this.storage = storage;
        this.repoProvider = repoProvider;
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        String refreshToken = storage.getRefreshToken();

        if (TextUtils.isEmpty(refreshToken)) {
            return null;
        }

        try {
            ObmRepo repo = repoProvider.get();
            TokenResponse tokenResponse = repo.refreshToken(refreshToken);
            return response.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + tokenResponse.getAccessToken())
                    .build();
        } catch (Throwable throwable) {
            Timber.e(throwable);
            storage.clearTokens();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return null;
        }
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        return null;
    }
}
