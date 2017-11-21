package hu.ektf.iot.openbiomapsapp.view.adapter;

import android.view.ViewGroup;

import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.BindableRecycleViewAdapter;
import hu.ektf.iot.openbiomapsapp.view.FormItemView;
import hu.ektf.iot.openbiomapsapp.view.FormItemViewImpl;

public class FormAdapter extends BindableRecycleViewAdapter<Form, FormItemView> {

    @Override
    protected FormItemView onCreateItemView(ViewGroup parent, int viewType) {
        return FormItemViewImpl.build(parent.getContext());
    }
}
