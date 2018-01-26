package hu.ektf.iot.openbiomapsapp.view.input;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;

import java.text.ParseException;
import java.util.Date;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.util.DateUtil;
import timber.log.Timber;

@EViewGroup(R.layout.input_date_time)
public class DateInputView extends DateTimeInputView {

    public DateInputView(@NonNull Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        pickTimeButton.setVisibility(View.GONE);
    }

    @Override
    protected Date getValue() {
        try {
            return DateUtil.parseDate(input.getText().toString());
        } catch (ParseException ex) {
            Timber.e(ex, "Unable to parse date input");
        }

        return new Date();
    }

    @Override
    protected void setValue(Date value) {
        input.setText(DateUtil.formatDate(value));
    }
}
