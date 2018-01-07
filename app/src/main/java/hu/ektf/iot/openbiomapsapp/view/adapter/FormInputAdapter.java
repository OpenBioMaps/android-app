package hu.ektf.iot.openbiomapsapp.view.adapter;

import android.view.ViewGroup;

import hu.ektf.iot.openbiomapsapp.model.FormControl;
import hu.ektf.iot.openbiomapsapp.view.input.BaseFormInputView;
import hu.ektf.iot.openbiomapsapp.view.input.CheckBoxInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.input.DateInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.input.DateTimeInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.input.NumberInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.input.PointInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.input.TextInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.input.TimeInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.input.UnknownInputViewImpl;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.BindableRecycleViewAdapter;

public class FormInputAdapter extends BindableRecycleViewAdapter<FormControl, BaseFormInputView> {
    private static final int VIEW_TYPE_UNKNOWN = -1;
    private static final int VIEW_TYPE_CHECKBOX = 0;
    private static final int VIEW_TYPE_DATE = 1;
    private static final int VIEW_TYPE_DATE_TIME = 2;
    private static final int VIEW_TYPE_NUMBER = 3;
    private static final int VIEW_TYPE_POINT = 4;
    private static final int VIEW_TYPE_TEXT = 5;
    private static final int VIEW_TYPE_TIME = 6;

    @Override
    protected BaseFormInputView onCreateItemView(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_CHECKBOX:
                return CheckBoxInputViewImpl.build(parent.getContext());
            case VIEW_TYPE_DATE:
                return DateInputViewImpl.build(parent.getContext());
            case VIEW_TYPE_DATE_TIME:
                return DateTimeInputViewImpl.build(parent.getContext());
            case VIEW_TYPE_NUMBER:
                return NumberInputViewImpl.build(parent.getContext());
            case VIEW_TYPE_POINT:
                return PointInputViewImpl.build(parent.getContext());
            case VIEW_TYPE_TEXT:
                return TextInputViewImpl.build(parent.getContext());
            case VIEW_TYPE_TIME:
                return TimeInputViewImpl.build(parent.getContext());
            default:
                return UnknownInputViewImpl.build(parent.getContext());
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
            case DATE:
                return VIEW_TYPE_DATE;
            case DATE_TIME:
                return VIEW_TYPE_DATE_TIME;
            case NUMERIC:
                return VIEW_TYPE_NUMBER;
            case POINT:
                return VIEW_TYPE_POINT;
            case TIME:
                return VIEW_TYPE_TIME;
            default:
                return VIEW_TYPE_TEXT;
        }
    }
}
