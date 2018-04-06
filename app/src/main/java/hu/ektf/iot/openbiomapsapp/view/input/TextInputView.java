package hu.ektf.iot.openbiomapsapp.view.input;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormControl;

@EViewGroup(R.layout.input_text)
public class TextInputView extends BaseFormInputView {

    @ViewById
    TextView label;

    @ViewById
    EditText input;

    public TextInputView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bind(FormControl control) {
        label.setText(control.getShortName());
        input.setText(control.getValue() != null ? control.getValue().toString() : "");
        input.setTag(R.id.tag_form_control, control);
    }
}
