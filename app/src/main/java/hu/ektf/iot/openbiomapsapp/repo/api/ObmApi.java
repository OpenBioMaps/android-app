package hu.ektf.iot.openbiomapsapp.repo.api;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.response.FormControlResponse;
import hu.ektf.iot.openbiomapsapp.model.response.FormResponse;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface ObmApi {
    String PARAM_FILE_ARRAY_FORMAT = "m_file0[%d]";

    @FormUrlEncoded
    @POST("/oauth/token.php")
    Observable<TokenResponse> login(@Field("username") String username,
                                    @Field("password") String password,
                                    @Field("client_id") String clientId,
                                    @Field("client_secret") String clientSecret,
                                    @Field("grant_type") String grantType,
                                    @Field("scope") String scope);

    @FormUrlEncoded
    @POST("/oauth/token.php")
    TokenResponse refreshToken(@Field("refresh_token") String refreshToken,
                               @Field("client_id") String clientId,
                               @Field("client_secret") String clientSecret,
                               @Field("grant_type") String grantType,
                               @Field("scope") String scope);

    @FormUrlEncoded
    @POST("/projects/{project}/pds.php")
    Observable<FormResponse> getForms(@Path("project") String project,
                                            @Field("scope") String scope);

    @FormUrlEncoded
    @POST("/projects/{project}/pds.php")
    Observable<FormControlResponse> getForm(@Path("project") String project,
                                            @Field("scope") String scope,
                                            @Field("value") Integer formId);

    @FormUrlEncoded
    @POST("/projects/{project}/pds.php")
    Response putData(@Path("project") String project,
                     @Field("scope") String scope,
                     @Field("form_id") Integer formId,
                     @Field("header") String columns,
                     @Field("data") String values);
}
