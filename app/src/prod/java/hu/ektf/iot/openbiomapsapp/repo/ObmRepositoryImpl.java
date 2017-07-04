package hu.ektf.iot.openbiomapsapp.repo;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.object.Form;
import hu.ektf.iot.openbiomapsapp.object.FormControl;
import rx.Observable;

public class ObmRepositoryImpl implements ObmRepository {
    @Override
    public Observable<List<Form>> loadFormList() {
        return Observable.empty();
    }

    @Override
    public Observable<List<FormControl>> loadForm(int formId) {
        return Observable.empty();
    }
}
