package hu.ektf.iot.openbiomapsapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import hu.ektf.iot.openbiomapsapp.adapter.ImagePagerAdapter;

/**
 * Created by Pádi on 2015. 11. 05..
 */
public class ImagePagerActivity extends Activity {
    public static final String ARG_IMAGES = "ARG_IMAGES";
    public static final String ARG_POS = "ARG_POS";

    private ImagePagerAdapter mImagePagerAdapter;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_pager);

        ArrayList<String> measurementImages = getIntent().getStringArrayListExtra(ARG_IMAGES);
        position = getIntent().getIntExtra(ARG_POS, 0);
        mImagePagerAdapter = new ImagePagerAdapter(this, measurementImages);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mImagePagerAdapter);
        mViewPager.setCurrentItem(position);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
    }
}