package hu.ektf.iot.openbiomapsapp.repo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.model.response.TokenResponse;
import retrofit.client.Response;
import rx.Completable;
import rx.Observable;

public class ObmRepoImpl extends ObmRepo {

    @Inject
    public ObmRepoImpl() {
    }

    @Override
    public Completable setUrl(String url) {
        return Completable.complete();
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public Observable<TokenResponse> login(String username, String password) {
        return Observable.just(new TokenResponse());
    }

    @Override
    public Completable logout() {
        return Completable.complete();
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public Observable<List<Form>> loadFormList() {
        List<Form> forms = new ArrayList<>();
        Form form = new Form();
        form.setId(1);
        form.setVisibility("Test Form");
        forms.add(form);
        form = new Form();
        form.setId(2);
        form.setVisibility("Cool form");
        forms.add(form);
        form = new Form();
        form.setId(3);
        form.setVisibility("The best form evaaar");
        forms.add(form);
        return Observable.just(forms);
    }

    @Override
    public Observable<List<FormControl>> loadForm(int formId) {
        List<FormControl> controls = new ArrayList<>();
        FormControl control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setColumn("TEXT");
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setColumn("AUTOCOMPLETE");
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setColumn("BOOLEAN");
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setColumn("DATE");
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setColumn("NUMERIC");
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setColumn("POINT");
        control.setShortName("Here comes a point");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setColumn("TEXT2");
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setColumn("AUTOCOMPLETE2");
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setColumn("BOOLEAN2");
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setColumn("DATE2");
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setColumn("NUMERIC2");
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setColumn("POINT2");
        control.setShortName("Here comes a point");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setColumn("TEXT3");
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setColumn("AUTOCOMPLETE3");
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setColumn("BOOLEAN3");
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setColumn("DATE3");
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setColumn("NUMERIC3");
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setColumn("POINT3");
        control.setShortName("Here comes a point");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setColumn("TEXT4");
        control.setShortName("Test Text");
        controls.add(control);

        return Observable.just(controls);
    }

    @Override
    public Observable<List<FormControl>> loadForm(int formId, Integer formControlId) {
        return loadForm(formId);
    }

    @Override
    public List<FormData> getSavedFormData() {
        return new ArrayList<>();
    }

    @Override
    public Observable<List<FormData>> getSavedFormDataAsync() {
        return Observable.just(new ArrayList<>());
    }

    @Override
    public FormData getSavedFormData(int id) {
        return null;
    }

    @Override
    public FormData getSavedFormDataByState(FormData.State state) {
        return null;
    }

    @Override
    public Completable saveData(FormData formData) {
        return Completable.complete();
    }

    @Override
    public Completable deleteData(FormData formData) {
        return Completable.complete();
    }

    @Override
    public Response uploadData(int formId, String columns, String valuesJson) {
        return new Response("http://google.com", 200, "", new ArrayList<>(), null);
    }
}
