package hu.ektf.iot.openbiomapsapp.view.input;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageButton;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.util.DateUtil;
import timber.log.Timber;

@EViewGroup(R.layout.input_date_time)
public class DateTimeInputView extends TextInputView {
    private Calendar calendar = Calendar.getInstance();

    @ViewById
    ImageButton pickDateButton;

    @ViewById
    ImageButton pickTimeButton;

    public DateTimeInputView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void bind(FormControl control) {
        super.bind(control);

        setValue(new Date());
    }

    @Click
    void pickTimeButtonClicked() {
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

    @Click
    void pickDateButtonClicked() {
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

    protected Date getValue() {
        try {
            return DateUtil.parseDateTime(input.getText().toString());
        } catch (ParseException ex) {
            Timber.e(ex, "Unable to parse dateTime input");
        }

        return new Date();
    }

    protected void setValue(Date value) {
        input.setText(DateUtil.formatDateTime(value));
    }
}
