package com.esh7enly.esh7enlyuser.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;


import androidx.core.content.ContextCompat;

import com.esh7enly.esh7enlyuser.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Islam
 */
public class TimeDialogs {

    private static int mYear, mMonth, mDay, mHour, mMinute;
    private static SimpleDateFormat dateFormatter;
    private static Calendar c;

    public static String pattern1 = "yyyy-MM-DD";

    /**
     * Native Date calender
     *
     * @param context
     * @param editText
     */
    public static void openDateCalender(Context context, final EditText editText) {

        c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        Date d = new Date(mYear, mMonth, mDay);
        dateFormatter = new SimpleDateFormat(pattern1);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DialogTheme, (view, year, monthOfYear, dayOfMonth) -> {

                    String date_select = year + "-" + (monthOfYear + 1)  + "-" + dayOfMonth;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
                    Date date1 = null;
                    try {
                        date1 = format.parse(date_select);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String date = format.format(date1);

                    String strDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    editText.setText(date);
                }, mYear, mMonth, mDay);
        int positiveColor = ContextCompat.getColor(context, R.color.colorPrimary);
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(positiveColor);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor);
    }

}
