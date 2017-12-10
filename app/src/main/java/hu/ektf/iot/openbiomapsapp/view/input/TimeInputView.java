package hu.ektf.iot.openbiomapsapp.view.input;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.util.DateUtil;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import timber.log.Timber;

@EViewGroup(R.layout.input_text)
public class TimeInputView extends TextInputView {
    private Calendar calendar = Calendar.getInstance();

    public TimeInputView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bind(FormControl control) {
        super.bind(control);

        setValue(new Date());
        input.setKeyListener(null);
    }

    @Click
    void inputClicked() {
        Date date = getValue();
        calendar.setTime(date);

        new TimePickerDialog(getContext(),
                (timePicker, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    Date newDate = calendar.getTime();
                    setValue(newDate);
                }, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true)
                .show();
    }

    private Date getValue() {
        try {
            return DateUtil.parseTime(input.getText().toString());
        } catch (ParseException ex) {
            Timber.e(ex, "Unable to parse time input");
        }

        return new Date();
    }

    private void setValue(Date value) {
        input.setText(DateUtil.formatTime(value));
    }
}
