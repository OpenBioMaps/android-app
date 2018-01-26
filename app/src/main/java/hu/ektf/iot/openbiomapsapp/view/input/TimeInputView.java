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
public class TimeInputView extends DateTimeInputView {

    public TimeInputView(@NonNull Context context) {
        super(context);
    }

    @AfterViews
    void init() {
        pickDateButton.setVisibility(View.GONE);
    }

    @Override
    protected Date getValue() {
        try {
            return DateUtil.parseTime(input.getText().toString());
        } catch (ParseException ex) {
            Timber.e(ex, "Unable to parse time input");
        }

        return new Date();
    }

    @Override
    protected void setValue(Date value) {
        input.setText(DateUtil.formatTime(value));
    }
}
