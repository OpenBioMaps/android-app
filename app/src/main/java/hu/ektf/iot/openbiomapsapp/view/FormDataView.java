package hu.ektf.iot.openbiomapsapp.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormData;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.BindableRecycleViewAdapter;

@EViewGroup(R.layout.list_item_form_data)
public class FormDataView extends LinearLayout implements BindableRecycleViewAdapter.Bindable<FormData> {

    @ViewById
    TextView tvDate;

    @ViewById
    TextView tvNote;

    @ViewById
    TextView tvNumOfFiles;

    @ViewById
    ImageView ivStatus;

    @DimensionPixelSizeRes(R.dimen.list_item_padding)
    int padding;

    public FormDataView(Context context) {
        super(context);

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
    }

    @AfterViews
    void init() {
        setPadding(padding, padding, padding, padding);
    }

    @Override
    public void bind(FormData item) {
        tvDate.setText(item.getDateString());
        tvNumOfFiles.setText(String.valueOf(item.getFiles().size()));

        switch (item.getState()) {
            case UPLOADED:
                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(R.drawable.ic_cloud_done);
                break;
            case UPLOADING:
                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(R.drawable.ic_cloud_upload);
                break;
            case UPLOAD_ERROR:
                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setImageResource(R.drawable.ic_cloud_error);
                break;
            default:
                ivStatus.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
