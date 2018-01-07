package hu.ektf.iot.openbiomapsapp.repo.api;

import android.text.TextUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Endpoint;

@Singleton
public class DynamicEndpoint implements Endpoint {
    private String url;

    @Inject
    public DynamicEndpoint() {

    }

    public void setUrl(String url) {
        if (!TextUtils.equals(this.url, url)) {
            this.url = url;
        }
    }

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public String getUrl() {
        if (url == null) {
            throw new IllegalStateException("Url not set.");
        }
        return url;
    }
}
