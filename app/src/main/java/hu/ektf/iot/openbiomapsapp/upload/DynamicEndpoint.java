package hu.ektf.iot.openbiomapsapp.upload;

import android.text.TextUtils;

import retrofit.Endpoint;


public class DynamicEndpoint implements Endpoint {
    private String url;

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
