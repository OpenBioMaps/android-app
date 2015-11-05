package hu.ektf.iot.openbiomapsapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.List;
import hu.ektf.iot.openbiomapsapp.R;

/**
 * Created by Pádi on 2015. 11. 05..
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private int imageSize = 600;
    private List<String> dataset;
    private AdapterView.OnItemClickListener itemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public AdapterView.OnItemClickListener itemClickListener;

        public ViewHolder(View v, AdapterView.OnItemClickListener itemClickListener) {
            super(v);
            this.itemClickListener = itemClickListener;
            this.imageView = (ImageView) v.findViewById(R.id.image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(null, v, getPosition(), v.getId());
            }
        }
    }

    public ImageListAdapter(List<String> dataset) {
        this.dataset = dataset;
    }

    public void setImageSize(int size) {
        this.imageSize = size;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_image, parent, false);
        ViewHolder vh = new ViewHolder(v, itemClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String image = dataset.get(position);
        Context ctx = holder.imageView.getContext();
        Picasso.with(ctx)
                .setLoggingEnabled(true);
        Picasso.with(ctx)
                .load(image)
                .placeholder(R.drawable.transparent_black)
                .resize(imageSize, imageSize)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
