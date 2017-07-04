package hu.ektf.iot.openbiomapsapp.repo;

import java.util.ArrayList;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.object.Form;
import hu.ektf.iot.openbiomapsapp.object.FormControl;
import rx.Observable;

public class ObmRepositoryImpl implements ObmRepository {

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
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setShortName("Here comes a point");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setShortName("Here comes a point");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.TEXT);
        control.setShortName("Test Text");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.AUTOCOMPLETE);
        control.setShortName("Autocomplete control");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.BOOLEAN);
        control.setShortName("Checkity checkbox");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.DATE);
        control.setShortName("Here comes a date");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.NUMERIC);
        control.setShortName("Gimmi number");
        controls.add(control);
        control = new FormControl();
        control.setType(FormControl.Type.POINT);
        control.setShortName("Here comes a point");
        controls.add(control);

        return Observable.just(controls);
    }
}
