package com.esh7enly.esh7enlyuser.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import androidx.core.content.FileProvider;

import com.esh7enly.esh7enlyuser.Config;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class Screenshot {

    public static String takeScreenshot(Activity activity, View view) {

        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            String path = "/Downloads/" + "ScreenShoots/";

            File file;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ///storage/emulated/0/Android/data/com.tal2a.merchant/files/Downloads/Files
                file = new File (activity.getExternalFilesDir(null) + path);
            } else {
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path);
            }

            // have the object build the directory structure, if needed.
            if (!file.exists()) {
                if (!file.mkdirs()) {

                }
            }

            // create bitmap screen capture
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageFile = new File(file.getAbsolutePath()+ "/" + now + ".jpeg");

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //Setting screenshot in imageview
            String filePath = imageFile.getPath();

            Bitmap ssbitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //iv.setImageBitmap(ssbitmap);

            //sharePath = filePath;
            //share(sharePath);

            return filePath;

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            //Log.e(TransactionDetailsFragment.class.getSimpleName(), "share Error => "+e.getMessage());
            e.printStackTrace();
            return "";
        }

    }

    public static void share(Activity activity, String sharePath) {

        Utils.log(sharePath);
        File file = new File(sharePath);
        Uri uri = FileProvider.getUriForFile(activity,
                activity.getPackageName() + Config.AUTHORITYFILE, file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(intent);
       // startActivity(Intent.createChooser(share,"Share content"));

    }

}
