package hu.ektf.iot.openbiomapsapp.repo;

import android.content.Context;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Action1;

public class ObmClientImpl extends ObmClient {

    private static final String CLIENT_ID = "mobile";
    private static final String CLIENT_SECRET = "123";
    private static final String GRANT_TYPE = "password";
    private static final String SCOPE = "get_form_list get_form_data put_data";

    public ObmClientImpl(Context context) {
        super(context);
    }

    @Override
    public Observable<TokenResponse> login(String username, String password) {
        return service.login(username, password, CLIENT_ID, CLIENT_SECRET, GRANT_TYPE, SCOPE)
                .doOnNext(new Action1<TokenResponse>() {
                    @Override
                    public void call(TokenResponse tokenResponse) {
                        storage.setAccessToken(tokenResponse.getAccessToken());
                        storage.setRefreshToken(tokenResponse.getRefreshToken());
                    }
                });
    }

    public TokenResponse refreshToken(String refreshToken) {
        TokenResponse tokenResponse = service.refreshToken(refreshToken, CLIENT_ID, CLIENT_SECRET, GRANT_TYPE, SCOPE);
        storage.setAccessToken(tokenResponse.getAccessToken());
        storage.setRefreshToken(tokenResponse.getRefreshToken());
        return tokenResponse;
    }

    @Override
    public Observable<List<Form>> loadFormList() {
        return service.getForms("get_form_list");
    }

    @Override
    public Observable<List<FormControl>> loadForm(int formId) {
        return service.getForm("get_form_data", formId);
    }

    @Override
    public Observable<Response> putData(int formId, String columns, String values) {
        return service.putData("put_data", formId, columns, values);
    }
}
