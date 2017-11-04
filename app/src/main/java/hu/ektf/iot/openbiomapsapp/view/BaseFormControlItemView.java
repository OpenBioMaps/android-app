package hu.ektf.iot.openbiomapsapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.recyclerview.BindableRecycleViewAdapter;

public abstract class BaseFormControlItemView extends FrameLayout implements BindableRecycleViewAdapter.Bindable<FormControl> {

    public BaseFormControlItemView(@NonNull Context context) {
        super(context);
    }
}
