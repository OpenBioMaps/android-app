package hu.ektf.iot.openbiomapsapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.Form;

public class FormListAdapter extends RecyclerView.Adapter<FormListAdapter.ViewHolder> {

    private List<Form> data = new ArrayList<>();

    public void setForms(List<Form> forms) {
        data.clear();
        if (forms != null) {
            data.addAll(forms);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_form, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Form form = data.get(position);

        holder.id.setText(String.valueOf(form.getId()));
        holder.name.setText(form.getVisibility());
    }

    public Form getItemAt(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView name;

        public ViewHolder(View v) {
            super(v);
            this.id = (TextView) v.findViewById(R.id.id);
            this.name = (TextView) v.findViewById(R.id.name);
        }
    }
}
