package hu.ektf.iot.openbiomapsapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.R;

/**
 * Created by Pádi on 2015. 11. 05..
 */
public class ImagePagerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> imagesList;

    public ImagePagerAdapter(Context context, ArrayList<String> imagesList) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imagesList = imagesList;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String imagePath = imagesList.get(position);
        File imageFile = new File(imagePath);

        View itemView = mLayoutInflater.inflate(R.layout.pager_item_image, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        Picasso.with(mContext)
                .load(imageFile)
                .placeholder(R.drawable.transparent_black)
                .fit()
                .centerInside()
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}