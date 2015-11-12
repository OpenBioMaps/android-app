package hu.ektf.iot.openbiomapsapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.object.ListObject;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
    ArrayList<ListObject> mDataset;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNote;
        public TextView tvDate;
        public ImageView ivSounds, ivImages, ivStatus;
        public TextView tvCoord;
        public ViewHolder(View v) {
            super(v);
            tvNote = (TextView) v.findViewById(R.id.tvNote);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            ivSounds = (ImageView) v.findViewById(R.id.ivSounds);
            ivImages = (ImageView) v.findViewById(R.id.ivImages);
            ivStatus = (ImageView) v.findViewById(R.id.ivStatus);
            tvCoord = (TextView) v.findViewById(R.id.tvLocation);
        }
    }

    public UploadListAdapter(ArrayList<ListObject> listObjects) {
        mDataset = listObjects;
    }

    @Override
    public UploadListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_upload, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListObject listObject = mDataset.get(position);
        holder.tvDate.setText(listObject.getDate());
        holder.tvNote.setText(listObject.getNote());
        holder.tvCoord.setText("Koordináták: (21.12, 11.21)");
        if(listObject.getStatus() == 0) {
            holder.ivStatus.setImageResource(R.drawable.fail);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}