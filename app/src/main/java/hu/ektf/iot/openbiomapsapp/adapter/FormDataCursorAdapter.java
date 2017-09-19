package hu.ektf.iot.openbiomapsapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.database.FormDataCreator;
import hu.ektf.iot.openbiomapsapp.model.FormData;

public class FormDataCursorAdapter extends CursorRecyclerViewAdapter<FormDataCursorAdapter.ViewHolder> {

    private AdapterView.OnItemClickListener itemClickListener;
    private AdapterView.OnItemLongClickListener longClickListener;

    public FormDataCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView tvComment;
        public TextView tvDate;
        public ImageView ivFiles, ivStatus;
        public TextView tvNumOfFiles;

        public AdapterView.OnItemClickListener itemClickListener;
        public AdapterView.OnItemLongClickListener longClickListener;

        public ViewHolder(View v, AdapterView.OnItemClickListener itemClickListener, AdapterView.OnItemLongClickListener longClickListener) {
            super(v);
            this.itemClickListener = itemClickListener;
            this.longClickListener = longClickListener;

            tvComment = (TextView) v.findViewById(R.id.tvNote);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            ivFiles = (ImageView) v.findViewById(R.id.ivFiles);
            ivStatus = (ImageView) v.findViewById(R.id.ivStatus);
            tvNumOfFiles = (TextView) v.findViewById(R.id.tvNumOfFiles);

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

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_note, parent, false);
        ViewHolder vh = new ViewHolder(itemView, itemClickListener, longClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        FormData formData = FormDataCreator.getFormDataFromCursor(cursor);
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
}
