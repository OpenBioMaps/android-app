package hu.ektf.iot.openbiomapsapp.repo;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.object.Form;
import hu.ektf.iot.openbiomapsapp.object.FormControl;
import rx.Observable;

public interface ObmRepository {

    Observable<List<Form>> loadFormList();
    Observable<List<FormControl>> loadForm(int formId);
}
