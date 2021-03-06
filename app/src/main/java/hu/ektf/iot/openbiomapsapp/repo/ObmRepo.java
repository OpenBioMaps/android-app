package hu.ektf.iot.openbiomapsapp.repo;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import retrofit.client.Response;
import rx.Completable;
import rx.Observable;

public abstract class ObmRepo {

    public abstract Completable setUrl(String url);

    public abstract boolean isLoggedIn();

    public abstract Observable<TokenResponse> login(String username, String password);

    public abstract Completable logout();

    public abstract TokenResponse refreshToken(String refreshToken);

    public abstract Observable<List<Form>> loadFormList();

    public abstract Observable<List<FormControl>> loadForm(int formId);

    public abstract Observable<List<FormControl>> loadForm(int formId, Integer formControlId);

    public abstract List<FormData> getSavedFormData();

    public abstract Observable<List<FormData>> getSavedFormDataAsync();

    public abstract FormData getSavedFormData(int id);

    public abstract FormData getSavedFormDataByState(FormData.State state);

    public abstract Completable saveData(FormData formData);

    public abstract Completable deleteData(FormData formData);

    public abstract Response uploadData(int formId, String columns, String valuesJson);

}
