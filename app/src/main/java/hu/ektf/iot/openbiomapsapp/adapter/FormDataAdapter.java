package hu.ektf.iot.openbiomapsapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.FormData;

public class FormDataAdapter extends RecyclerView.Adapter<FormDataAdapter.ViewHolder> {
    private List<FormData> dataList = new ArrayList<>();

    private AdapterView.OnItemClickListener itemClickListener;
    private AdapterView.OnItemLongClickListener longClickListener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public FormData getItem(int position) {
        return dataList.get(position);
    }

    // TODO Use DiffUtil
    public void swapItems(List<FormData> newList) {
        dataList.clear();

        if (newList != null) {
            dataList.addAll(newList);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_note, parent, false);
        return new ViewHolder(itemView, itemClickListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        FormData formData = getItem(position);
        viewHolder.tvDate.setText(formData.getDateString());
        viewHolder.tvNumOfFiles.setText(String.valueOf(formData.getFiles().size()));

        switch (formData.getState()) {
            case UPLOADED:
                viewHolder.ivStatus.setVisibility(View.VISIBLE);
                viewHolder.ivStatus.setImageResource(R.drawable.state_success);
                break;
            case UPLOADING:
                viewHolder.ivStatus.setVisibility(View.VISIBLE);
                viewHolder.ivStatus.setImageResource(R.drawable.state_uploading);
                break;
            case UPLOAD_ERROR:
                viewHolder.ivStatus.setVisibility(View.VISIBLE);
                viewHolder.ivStatus.setImageResource(R.drawable.state_fail);
                break;
            default:
                viewHolder.ivStatus.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvComment;
        TextView tvDate;
        ImageView ivFiles, ivStatus;
        TextView tvNumOfFiles;

        AdapterView.OnItemClickListener itemClickListener;
        AdapterView.OnItemLongClickListener longClickListener;

        ViewHolder(View v, AdapterView.OnItemClickListener itemClickListener, AdapterView.OnItemLongClickListener longClickListener) {
            super(v);
            this.itemClickListener = itemClickListener;
            this.longClickListener = longClickListener;

            tvComment = v.findViewById(R.id.tvNote);
            tvDate = v.findViewById(R.id.tvDate);
            ivFiles = v.findViewById(R.id.ivFiles);
            ivStatus = v.findViewById(R.id.ivStatus);
            tvNumOfFiles = v.findViewById(R.id.tvNumOfFiles);

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(null, v, getPosition(), v.getId());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(null, v, getPosition(), v.getId());
                return true;
            }
            return false;
        }
    }
}
