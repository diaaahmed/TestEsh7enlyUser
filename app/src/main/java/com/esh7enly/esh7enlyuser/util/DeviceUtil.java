package com.esh7enly.esh7enlyuser.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class DeviceUtil extends AppCompatActivity {

    private static String TAG = "DeviceUtil";
    public Context _context;

    public static String getImeiWithPermission(Context context) {

        String imei = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean result = PermissionHelper.checkSinglePermission(context,
                    Manifest.permission.READ_PHONE_STATE, 100);
            if (result) {
                imei = DeviceUtil.getDeviceIMEI(Objects.requireNonNull(context));
                return imei;
            } else {
                return imei;
            }
        } else {
            imei = DeviceUtil.getDeviceIMEI(Objects.requireNonNull(context));
            return imei;
        }

    }

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceIMEI(Context context) {

        String IMEI = null;
        String IMEI1 = null;
        String IMEI2 = null;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (null != tm) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                IMEI1 = tm.getDeviceId(1);
                IMEI2 = tm.getDeviceId(2);

                IMEI = IMEI1 + "\n" + IMEI2;

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                IMEI1 = tm.getImei(1);
                IMEI2 = tm.getImei(2);

                IMEI = IMEI1 + "\n" + IMEI2;
            } else {

                IMEI = tm.getDeviceId();


            }

        }

        if (null == IMEI || 0 == IMEI.length()) {

            IMEI = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return IMEI;
    }




}
