package hu.ektf.iot.openbiomapsapp.view.input;

import android.app.DatePickerDialog;
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
public class DateInputView extends TextInputView {
    private Calendar calendar = Calendar.getInstance();

    public DateInputView(@NonNull Context context) {
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

        new DatePickerDialog(getContext(),
                (datePicker, year, month, day) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    Date newDate = calendar.getTime();
                    setValue(newDate);
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private Date getValue() {
        try {
            return DateUtil.parseDate(input.getText().toString());
        } catch (ParseException ex) {
            Timber.e(ex, "Unable to parse date input");
        }

        return new Date();
    }

    private void setValue(Date value) {
        input.setText(DateUtil.formatDate(value));
    }
}
