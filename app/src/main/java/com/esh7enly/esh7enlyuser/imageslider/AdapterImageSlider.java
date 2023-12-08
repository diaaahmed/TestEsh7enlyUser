package com.esh7enly.esh7enlyuser.imageslider;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.esh7enly.domain.entity.userservices.Image;
import com.esh7enly.esh7enlyuser.R;
import com.esh7enly.esh7enlyuser.util.Utils;

import java.util.ArrayList;

public class AdapterImageSlider extends PagerAdapter {

    OnClickListener mListener;
    private Activity act;
    private ArrayList<Image> bannerImages = new ArrayList<>();

    private interface OnItemClickListener {
        void onItemClick(View view, Image obj);
    }

    // constructor
    public AdapterImageSlider(Activity activity, ArrayList<Image> items) {
        this.act = activity;
        this.bannerImages = items;
    }


    @Override
    public int getCount() {
        return this.bannerImages.size();
    }

    public Image getItem(int pos) {
        return bannerImages.get(pos);
    }

    public void setItems(ArrayList<Image> items) {
        this.bannerImages = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Image o = bannerImages.get(position);
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_slider_image, container, false);

        ImageView image = (ImageView) v.findViewById(R.id.image);
        RelativeLayout lyt_parent = (RelativeLayout) v.findViewById(R.id.lyt_parent);

        Utils.displayImageOriginal(act, image, o.getPath());

//        lyt_parent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                mListener.onClick(v, position, position);
//            }
//        });

        ((ViewPager) container).addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}
