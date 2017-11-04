package hu.ektf.iot.openbiomapsapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormControl;

@EViewGroup(R.layout.list_item_edit_text)
public class TextControlInputView extends BaseFormControlItemView {

    @ViewById
    TextView label;

    @ViewById
    EditText input;

    public TextControlInputView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bind(FormControl control) {
        label.setText(control.getShortName());
        input.setTag(R.id.tag_form_control, control);
    }
}
