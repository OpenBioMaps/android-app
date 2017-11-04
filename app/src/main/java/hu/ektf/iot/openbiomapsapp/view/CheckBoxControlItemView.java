package hu.ektf.iot.openbiomapsapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.CheckBox;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormControl;

@EViewGroup(R.layout.list_item_check_box)
public class CheckBoxControlItemView extends BaseFormControlItemView {

    @ViewById
    CheckBox input;

    public CheckBoxControlItemView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bind(FormControl control) {
        input.setText(control.getShortName());
        input.setTag(R.id.tag_form_control, control);
    }
}
