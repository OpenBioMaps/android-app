package hu.ektf.iot.openbiomapsapp.adapter;

import android.view.ViewGroup;

import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.recyclerview.BindableRecycleViewAdapter;
import hu.ektf.iot.openbiomapsapp.view.BaseFormControlItemView;
import hu.ektf.iot.openbiomapsapp.view.CheckBoxControlItemViewImpl;
import hu.ektf.iot.openbiomapsapp.view.TextControlInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.UnknownControlItemViewImpl;

public class FormControlAdapter extends BindableRecycleViewAdapter<FormControl, BaseFormControlItemView> {
    private static final int VIEW_TYPE_UNKNOWN = -1;
    private static final int VIEW_TYPE_CHECKBOX = 0;
    private static final int VIEW_TYPE_EDIT_TEXT = 1;

    @Override
    protected BaseFormControlItemView onCreateItemView(ViewGroup parent, int viewType) {
        if (VIEW_TYPE_CHECKBOX == viewType) {
            return CheckBoxControlItemViewImpl.build(parent.getContext());
        } else if (VIEW_TYPE_EDIT_TEXT == viewType) {
            return TextControlInputViewImpl.build(parent.getContext());
        } else {
            return UnknownControlItemViewImpl.build(parent.getContext());
        }
    }

    @Override
    public int getItemViewType(int position) {
        FormControl.Type type = getItemAtPosition(position).getType();

        if (type == null) {
            return VIEW_TYPE_UNKNOWN;
        }

        switch (type) {
            case BOOLEAN:
                return VIEW_TYPE_CHECKBOX;
            default:
                return VIEW_TYPE_EDIT_TEXT;
        }
    }
}
