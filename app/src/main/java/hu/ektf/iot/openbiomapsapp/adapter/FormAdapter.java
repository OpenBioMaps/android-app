package hu.ektf.iot.openbiomapsapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.object.FormControl;

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.BaseViewHolder> {

    private static final int VIEW_TYPE_CHECKBOX = 0;
    private static final int VIEW_TYPE_EDITTEXT = 1;

    private List<FormControl> data = new ArrayList<>();

    public void setControls(List<FormControl> controls) {
        data.clear();
        if (controls != null) {
            data.addAll(controls);
        }
        notifyDataSetChanged();
    }

    @Override
    public FormAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (VIEW_TYPE_CHECKBOX == viewType) {
            return new CheckBoxViewHolder(parent);
        } else if (VIEW_TYPE_EDITTEXT == viewType) {
            return new EditTextViewHolder(parent);
        } else {
            throw new IllegalArgumentException("Unknown view type: " + String.valueOf(viewType));
        }
    }

    @Override
    public void onBindViewHolder(FormAdapter.BaseViewHolder holder, int position) {
        FormControl control = data.get(position);
        holder.bind(control);
    }

    @Override
    public int getItemViewType(int position) {
        FormControl.Type type = data.get(position).getType();
        switch (type) {
            case BOOLEAN:
                return VIEW_TYPE_CHECKBOX;
            default:
                return VIEW_TYPE_EDITTEXT;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View v) {
            super(v);
        }

        public abstract void bind(FormControl control);
    }

    public static class EditTextViewHolder extends BaseViewHolder {
        public TextView label;
        public EditText input;

        public EditTextViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_edit_text, parent, false));
            this.label = (TextView) itemView.findViewById(R.id.id);
            this.input = (EditText) itemView.findViewById(R.id.input);
        }

        @Override
        public void bind(FormControl control) {
            label.setText(control.getShortName());
        }
    }

    public static class CheckBoxViewHolder extends BaseViewHolder {
        public CheckBox input;

        public CheckBoxViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_check_box, parent, false));
            this.input = (CheckBox) itemView.findViewById(R.id.input);
        }

        @Override
        public void bind(FormControl control) {
            input.setText(control.getShortName());
        }
    }
}
