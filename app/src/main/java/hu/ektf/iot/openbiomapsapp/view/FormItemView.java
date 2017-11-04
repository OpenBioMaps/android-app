package hu.ektf.iot.openbiomapsapp.view;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.recyclerview.BindableRecycleViewAdapter;

@EViewGroup(R.layout.list_item_form)
public class FormItemView extends LinearLayout implements BindableRecycleViewAdapter.Bindable<Form> {

    @ViewById
    TextView id;

    @ViewById
    TextView name;

    @DimensionPixelSizeRes(R.dimen.list_item_min_height)
    int height;

    @DimensionPixelSizeRes(R.dimen.list_item_padding)
    int padding;

    public FormItemView(Context context) {
        super(context);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);
    }

    @AfterViews
    void init() {
        setMinimumHeight(height);
        setPadding(padding, padding, padding, padding);
    }

    @Override
    public void bind(Form form) {
        id.setText(String.valueOf(form.getId()));
        name.setText(form.getVisibility());
    }
}
