package hu.ektf.iot.openbiomapsapp.view.input;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.BindableRecycleViewAdapter;

public abstract class BaseFormInputView extends FrameLayout implements BindableRecycleViewAdapter.Bindable<FormControl> {

    public BaseFormInputView(@NonNull Context context) {
        super(context);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
