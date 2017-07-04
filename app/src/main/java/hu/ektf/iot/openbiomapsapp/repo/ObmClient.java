package hu.ektf.iot.openbiomapsapp.repo;

import android.content.Context;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.BioMapsApplication;
import hu.ektf.iot.openbiomapsapp.helper.StorageHelper;
import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import hu.ektf.iot.openbiomapsapp.upload.BioMapsService;
import retrofit.client.Response;
import rx.Observable;

public abstract class ObmClient {

    protected final BioMapsService service;
    protected final StorageHelper storage;

    public ObmClient(Context context) {
        this.service = ((BioMapsApplication) context.getApplicationContext()).getMapsService();
        this.storage = new StorageHelper(context);
    }

    public abstract Observable<TokenResponse> login(String username, String password);

    public abstract TokenResponse refreshToken(String refreshToken);

    public abstract Observable<List<Form>> loadFormList();

    public abstract Observable<List<FormControl>> loadForm(int formId);

    public abstract Observable<Response> putData(int formId, String columns, String valuesJson);
}
