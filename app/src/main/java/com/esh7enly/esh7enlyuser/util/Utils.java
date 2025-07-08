package com.esh7enly.esh7enlyuser.util;


import android.content.Context;

import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.esh7enly.esh7enlyuser.BuildConfig;
import com.esh7enly.esh7enlyuser.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.Locale;

public class Utils {

    public Utils() {


    }

    public static String replaceArabicNumbers(String original) {

        return original.replaceAll("١", "1")
                .replaceAll("٢", "2")
                .replaceAll("٣", "3")
                .replaceAll("٤", "4")
                .replaceAll("٥", "5")
                .replaceAll("٦", "6")
                .replaceAll("٧", "7")
                .replaceAll("٨", "8")
                .replaceAll("٩", "9")
                .replaceAll("٠", "0");
    }

    public static void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }


    public static void log(String log) {
        if (BuildConfig.DEBUG) {
            Log.d("===== HWDTechs", "=====================================");
            Log.d("HWDTechs", log);
            Log.d("===== HWDTechs", "=====================================");
        }
    }

    //endregion

    // For Price Decimal Format
    public static String format(Double value) {
        if (value == null) {
            value = 0.0;
        }
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#.##");
        return df.format(value);

    }


    public static void displayImageOriginalFromCache(
            Context ctx, ImageView img, String url, boolean isWifi
    ) {
        try {

            Glide.with(ctx).load(BuildConfig.IMAGE_URL + url)
                    //.crossFade()
                    .placeholder(R.drawable.new_logo_trans)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(new RequestOptions()
                            //.onlyRetrieveFromCache(!isWifi)
                            .dontAnimate()
                            .centerInside())
                    .into(img);


        } catch (Exception e) {
        }
    }

    public static void displayImageOriginal(Context ctx, ImageView img, String url) {
        try {
            Glide.with(ctx).load(BuildConfig.IMAGE_URL + url)
                    //.crossFade()
                    .placeholder(R.drawable.new_logo_trans)
                    //.override(100, 100)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img);
        } catch (Exception e) {
        }
    }

}

