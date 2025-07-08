package com.esh7enly.esh7enlyuser.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

   // public static final int REQUEST_CODE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkSinglePermission(final Context context, String permission, int REQUEST_CODE)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    showPermissionsAlert(context,permission,REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, REQUEST_CODE);
                }
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private static void showPermissionsAlert(final Context context, final String permission, final int REQUEST_CODE) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage("External storage permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, REQUEST_CODE);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

}






