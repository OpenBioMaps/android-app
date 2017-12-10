package hu.ektf.iot.openbiomapsapp.upload;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface BioMapsService {
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
    Observable<List<Form>> getForms(@Path("project") String project,
                                    @Field("scope") String scope);

    @FormUrlEncoded
    @POST("/projects/{project}/pds.php")
    Observable<List<FormControl>> getForm(@Path("project") String project,
                                          @Field("scope") String scope,
                                          @Field("value") Integer formId);

    @FormUrlEncoded
    @POST("/projects/{project}/pds.php")
    Response putData(@Path("project") String project,
                     @Field("scope") String scope,
                     @Field("put_api_form") Integer formId,
                     @Field("value") String columns,
                     @Field("api_form_data") String values);
}
