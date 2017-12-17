package hu.ektf.iot.openbiomapsapp.view.adapter;

import android.view.ViewGroup;

import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.view.FormDataView;
import hu.ektf.iot.openbiomapsapp.view.FormDataViewImpl;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.BindableRecycleViewAdapter;

public class FormDataAdapter extends BindableRecycleViewAdapter<FormData, FormDataView> {

    @Override
    protected FormDataView onCreateItemView(ViewGroup parent, int viewType) {
        return FormDataViewImpl.build(parent.getContext());
    }
}
