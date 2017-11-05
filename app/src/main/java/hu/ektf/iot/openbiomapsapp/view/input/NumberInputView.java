package hu.ektf.iot.openbiomapsapp.view.input;

import android.content.Context;
import android.support.annotation.NonNull;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import timber.log.Timber;

@EViewGroup(R.layout.input_number)
public class NumberInputView extends TextInputView {
    private final static int DEFAULT_VALUE = 0;

    public NumberInputView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bind(FormControl control) {
        super.bind(control);

        setValue(DEFAULT_VALUE);
    }

    @Click
    void subtractClicked() {
        setValue(getValue() - 1);
    }

    @Click
    void addClicked() {
        setValue(getValue() + 1);
    }

    private int getValue() {
        try {
            return Integer.parseInt(input.getText().toString());
        } catch (NumberFormatException ex) {
            Timber.e("Could not parse number input", ex);
        }

        return 0;
    }

    private void setValue(int value) {
        input.setText(String.valueOf(value));
    }
}
