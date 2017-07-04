package hu.ektf.iot.openbiomapsapp.model.response;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private Integer expiresIn;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("scope")
    private String scope;
    @SerializedName("refresh_token")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
