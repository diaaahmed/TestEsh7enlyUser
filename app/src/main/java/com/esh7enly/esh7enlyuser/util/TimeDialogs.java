package com.esh7enly.esh7enlyuser.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;


import com.esh7enly.esh7enlyuser.R;

import java.text.DateFormat;
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
    private static String pickerDate, time;
    private static SimpleDateFormat dateFormatter;
    private static Calendar c;
    private static Calendar dateTime = Calendar.getInstance();

    public static String pattern1 = "yyyy-MM-DD";
    public static String pattern4 = "hh:mm:ss";

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
                R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

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
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();


    }

    /**
     * Native Date calender and Time Calender
     *
     * @param context
     * @param editText
     */
    public static void openDateTimeCalender(final Context context, final EditText editText) {

        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        dateFormatter = new SimpleDateFormat(pattern1);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String strDate = dateFormatter.format(c.getTime());
                openTimeCalender(context, editText, strDate);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();


    }

    /**
     * Launch Time Picker Dialog after date picker
     *
     * @param context
     * @param editText
     * @param pickerDate
     */
    private static void openTimeCalender(Context context, final EditText editText, final String pickerDate) {

        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        dateFormatter = new SimpleDateFormat(pattern4);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                String strTime = dateFormatter.format(c.getTime());
                editText.setText(pickerDate + " " + strTime);
            }
        }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    public static String getFirstDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(cal.getTime());
    }

    public static String getCurrantDateOfMonth() {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(dateTime.getTime());
    }

}
