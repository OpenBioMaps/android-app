package hu.ektf.iot.openbiomapsapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormControl;

@EViewGroup(R.layout.list_item_unknown_input)
public class UnknownControlItemView extends BaseFormControlItemView {

    @ViewById
    TextView text;

    public UnknownControlItemView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bind(FormControl item) {
        text.setText(text.getContext().getString(R.string.unknown_input_type, item.getColumn()));
    }
}
