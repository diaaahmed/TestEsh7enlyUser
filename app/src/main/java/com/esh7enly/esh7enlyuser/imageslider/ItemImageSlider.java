package com.esh7enly.esh7enlyuser.imageslider;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;


import com.esh7enly.domain.entity.userservices.*;
import com.esh7enly.esh7enlyuser.R;

import java.util.ArrayList;

public class ItemImageSlider {

    private Activity activity;
    private ViewPager pager;
    private LinearLayout layoutDots;
    
    private AdapterImageSlider adapterImageSlider;
    private ArrayList<Image> bannerImages = new ArrayList<>();

    public ItemImageSlider(Activity activity, ViewPager pager, LinearLayout layoutDots, AdapterImageSlider adapterImageSlider,
                           ArrayList<Image> bannerImages) {

        this.activity = activity;
        this.pager = pager;
        this.layoutDots = layoutDots;
        this.adapterImageSlider = adapterImageSlider;
        this.bannerImages = bannerImages;


    }

    /**
     * setup Image slider
     */
    public void initImageSlider() {

        adapterImageSlider = new AdapterImageSlider(activity,this.bannerImages);

//        adapterImageSlider = new AdapterImageSlider(activity, this.bannerImages, new OnClickListener() {
//            @Override
//            public void onClick(View view, Object obj, int pos) {
//                int position = (int) obj;
//                //activity.startActivity(ViewPagerActivity.getStartIntent(activity, bannerImages, position));
//            }
//        });

        adapterImageSlider.setItems(this.bannerImages);
        pager.setAdapter(adapterImageSlider);

        // displaying selected image first
        pager.setCurrentItem(0);
        addBottomDots(layoutDots, adapterImageSlider.getCount(), 0);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                addBottomDots(layoutDots, adapterImageSlider.getCount(), pos);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(activity);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(ContextCompat.getColor(activity, R.color.overlay_dark_10), PorterDuff.Mode.SRC_ATOP);
            layout_dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current].setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
    }

}
