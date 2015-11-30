package hu.ektf.iot.openbiomapsapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.object.Note;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {
    ArrayList<Note> mDataset;
    private AdapterView.OnItemClickListener itemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvComment;
        public TextView tvDate;
        public ImageView ivSounds, ivImages, ivStatus;
        public TextView tvCoord;
        public TextView tvNumOfImages, tvNumOfSounds;
        public AdapterView.OnItemClickListener itemClickListener;

        public ViewHolder(View v, AdapterView.OnItemClickListener itemClickListener) {
            super(v);
            this.itemClickListener = itemClickListener;
            tvComment = (TextView) v.findViewById(R.id.tvNote);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            ivSounds = (ImageView) v.findViewById(R.id.ivSounds);
            ivImages = (ImageView) v.findViewById(R.id.ivImages);
            ivStatus = (ImageView) v.findViewById(R.id.ivStatus);
            tvCoord = (TextView) v.findViewById(R.id.tvLocation);
            tvNumOfImages = (TextView) v.findViewById(R.id.tvNumOfImages);
            tvNumOfSounds = (TextView) v.findViewById(R.id.tvNumOfSounds);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(null, v, getPosition(), v.getId());
            }
        }
    }

    public UploadListAdapter(ArrayList<Note> listObjects) {
        mDataset = listObjects;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public UploadListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_upload, parent, false);
        ViewHolder vh = new ViewHolder(v, itemClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note listObject = mDataset.get(position);
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

