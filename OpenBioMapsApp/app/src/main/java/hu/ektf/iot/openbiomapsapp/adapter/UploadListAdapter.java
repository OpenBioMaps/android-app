package hu.ektf.iot.openbiomapsapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.object.NoteRecord;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
    ArrayList<NoteRecord> mDataset;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvComment;
        public TextView tvDate;
        public ImageView ivSounds, ivImages, ivStatus;
        public TextView tvCoord;
        public TextView tvNumOfImages, tvNumOfSounds;
        public ViewHolder(View v) {
            super(v);
            tvComment = (TextView) v.findViewById(R.id.tvNote);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            ivSounds = (ImageView) v.findViewById(R.id.ivSounds);
            ivImages = (ImageView) v.findViewById(R.id.ivImages);
            ivStatus = (ImageView) v.findViewById(R.id.ivStatus);
            tvCoord = (TextView) v.findViewById(R.id.tvLocation);
            tvNumOfImages = (TextView) v.findViewById(R.id.tvNumOfImages);
            tvNumOfSounds = (TextView) v.findViewById(R.id.tvNumOfSounds);
        }
    }

    public UploadListAdapter(ArrayList<NoteRecord> listObjects) {
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
        NoteRecord listObject = mDataset.get(position);
        if(!listObject.getDate().isEmpty()) holder.tvDate.setText(listObject.getDate());
        if(!listObject.getComment().isEmpty()) holder.tvComment.setText(listObject.getComment());
        if(!listObject.getLocationString().isEmpty()) holder.tvCoord.setText(listObject.getLocationString());
        if(listObject.getResponse() == 0) {
            holder.ivStatus.setImageResource(R.drawable.fail);
        }
        if(listObject.getImagesList() != null) holder.tvNumOfImages.setText(String.valueOf(listObject.getImagesList().size()));
        if(listObject.getSoundsList() != null) holder.tvNumOfSounds.setText(String.valueOf(listObject.getSoundsList().size()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}