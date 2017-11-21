package hu.ektf.iot.openbiomapsapp.view.input;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormControl;

@EViewGroup(R.layout.input_unknown)
public class UnknownInputView extends BaseFormInputView {

    @ViewById
    TextView text;

    public UnknownInputView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bind(FormControl item) {
        text.setText(text.getContext().getString(R.string.unknown_input_type, item.getColumn()));
    }
}
