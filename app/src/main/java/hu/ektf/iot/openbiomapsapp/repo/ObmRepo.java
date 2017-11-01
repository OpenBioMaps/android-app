package hu.ektf.iot.openbiomapsapp.repo;

import android.content.Context;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import retrofit.client.Response;
import rx.Completable;
import rx.Observable;

public abstract class ObmRepo {

    public ObmRepo(Context context) {
    }

    public abstract boolean isLoggedIn();

    public abstract Observable<TokenResponse> login(String username, String password);

    public abstract TokenResponse refreshToken(String refreshToken);

    public abstract Observable<List<Form>> loadFormList();

    public abstract Observable<List<FormControl>> loadForm(int formId);

    public abstract List<FormData> getSavedFormData();

    public abstract Observable<List<FormData>> getSavedFormDataAsync();

    public abstract FormData getSavedFormDataByState(FormData.State state);

    public abstract Completable saveData(FormData formData);

    public abstract Completable deleteData(FormData formData);

    public abstract Response uploadData(int formId, String columns, String valuesJson);

}
