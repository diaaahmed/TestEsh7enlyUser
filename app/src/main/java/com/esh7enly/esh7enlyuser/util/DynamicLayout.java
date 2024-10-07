package com.esh7enly.esh7enlyuser.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.esh7enly.domain.entity.SpinnerModel;
import com.esh7enly.esh7enlyuser.R;
import com.esh7enly.esh7enlyuser.click.DynamicOnClickListener;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DynamicLayout extends AppCompatActivity {

     Context mContext;

    public DynamicLayout(Context context) {
        this.mContext = context;
    }

    public void addRadioButtons(
            LinearLayout linearLayout, String internalId, ArrayList<SpinnerModel> values) {

        //RadioButtons are always added inside a RadioGroup
        RadioGroup radioGroup = new RadioGroup(mContext);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setTag(internalId);
        linearLayout.addView(radioGroup);
        for (int i = 0; i < values.size(); i++) {
            RadioButton radioButton = new RadioButton(mContext);
            radioButton.setText(values.get(i).getaName());
            radioGroup.addView(radioButton);
            setRadioButtonAttributes(radioButton);
        }
        //addLineSeperator(linearLayout);
    }

    public void addTextViews(LinearLayout linearLayout, String text) {
        //Adding a LinearLayout with HORIZONTAL orientation
        LinearLayout textLinearLayout = new LinearLayout(mContext);
        textLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        linearLayout.addView(textLinearLayout);

        TextView textView = new TextView(mContext);
        textView.setText(text);
//        textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
        textView.setGravity(Gravity.START);
        setTextViewAttributes(textView);
        textLinearLayout.addView(textView);

        //addLineSeperator(linearLayout);
    }



    public void addTextViews(LinearLayout linearLayout, String text, int color, float size) {
        //Adding a LinearLayout with HORIZONTAL orientation
        LinearLayout textLinearLayout = new LinearLayout(mContext);
        textLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        linearLayout.addView(textLinearLayout);

        TextView textView = new TextView(mContext);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(convertDpToPixel(size));
        textView.setGravity(Gravity.START);
        setTextViewAttributes(textView);
        textLinearLayout.addView(textView);

        //addLineSeperator(linearLayout);
    }

    public void addTextViews(LinearLayout linearLayout, String text, int color) {
        //Adding a LinearLayout with HORIZONTAL orientation
        LinearLayout textLinearLayout = new LinearLayout(mContext);
        textLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        linearLayout.addView(textLinearLayout);

        TextView textView = new TextView(mContext);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setGravity(Gravity.START);
        setTextViewAttributes(textView,"");
        textLinearLayout.addView(textView);

        //addLineSeperator(linearLayout);
    }

    public void addSpinners(LinearLayout linearLayout, String viewId,
                            int check, String selectedValueId,
                            final List<SpinnerModel> list,
                            final DynamicOnClickListener dynamicOnClickListener) {
        final int[] chk = {check};
        //Adding a LinearLayout with HORIZONTAL orientation
        LinearLayout spinnerLinearLayout = new LinearLayout(mContext);
        spinnerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        linearLayout.addView(spinnerLinearLayout);
        SpinnerHelper spinnerHelper = new SpinnerHelper(mContext, list, true);
        Spinner spinner = new Spinner(mContext);
        //spinner.setId(viewId);
        spinner.setTag(viewId);
        spinnerHelper.setAdapter(spinner);

        if (!selectedValueId.equals("")) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().trim().equals(selectedValueId)) {
                    spinner.setSelection(i);
                }
            }

        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // changing the color of selected item
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#36abe5"));

                if (++chk[0] > 1) {
                    String valueId = list.get(position).getId();
                    dynamicOnClickListener.onItemSelected(valueId);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerLinearLayout.addView(spinner);
        setSpinnerAttributes(spinner);
    }

    public void addCheckBoxes(LinearLayout linearLayout) {

        LinearLayout checkBoxLayout = new LinearLayout(mContext);
        checkBoxLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(checkBoxLayout);

        for (int i = 1; i <= 3; i++) {
            CheckBox checkBox = new CheckBox(mContext);
            checkBox.setText("CheckBox " + String.valueOf(i));
            setCheckBoxAttributes(checkBox);
            checkBoxLayout.addView(checkBox);
        }
        addLineSeperator(linearLayout);
    }

    public void addEditTexts(LinearLayout linearLayout, boolean isEnabled, int viewId, String hint, int type, int lines, Button button, final boolean isRequired,
                             final DynamicOnClickListener dynamicOnClickListener) {

        LinearLayout editTextLayout = new LinearLayout(mContext);
        editTextLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(editTextLayout);

        final EditText editText = new EditText(mContext);
        if (!isEnabled) Utils.disableEditText(editText);

        editText.setBackgroundColor(ContextCompat.getColor(mContext, R.color.instaPrimary));
        editText.setHint(hint); // hint
        editText.setId(viewId); // id
        editText.setInputType(type); //type
        editText.setLines(lines); //Num of lines
        editText.setSingleLine(false);
        setEditTextAttributes(editText);
        editTextLayout.addView(editText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRequired && editText.getText().toString().isEmpty()) {
                    editText.setError("Rrequired");
                } else {
                    dynamicOnClickListener.onItemSelected(editText.getText().toString());
                }

            }
        });
        //addLineSeperator(linearLayout);
    }

    public void addEditTexts(
            LinearLayout linearLayout, boolean isEnabled, String viewId, String hint,
                             int type, int lines,
            final DynamicOnClickListener dynamicOnClickListener) {

        LinearLayout editTextLayout = new LinearLayout(mContext);
        editTextLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(editTextLayout);

        EditText editText = new EditText(mContext);
        if (!isEnabled) Utils.disableEditText(editText);
        editText.setHint(hint); // hint
        //editText.setId(viewId); // id
        editText.setTag(viewId); // tag
        if (type == 2) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //type
        } else {
            editText.setInputType(type); //type

        }
        editText.setLines(lines); //Num of lines
        editText.setSingleLine(false);
        setEditTextAttributes(editText); //  attr
        editTextLayout.addView(editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dynamicOnClickListener.onItemSelected(s.toString());

            }
        });
        //addLineSeperator(linearLayout);
    }

    public void addViews(LinearLayout linearLayout, int width, int height, int margin) {

        View view = new View(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, convertDpToPixel(height));
        params.setMargins(0, convertDpToPixel(margin), 0, convertDpToPixel(margin));
        view.setLayoutParams(params);
        linearLayout.addView(view);
    }

    public void addViews(LinearLayout linearLayout, int width, float height, int margin, int color) {
                /*
                <View
                    android:layout_width="match_parent"
                    android:layout_height="0.5dp"
                    android:layout_marginStart="20dp"
                    android:background="#cccccc" />
                     */
        View view = new View(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, convertDpToPixel(height));
        params.setMargins(0, convertDpToPixel(margin), 0, convertDpToPixel(margin));
        view.setLayoutParams(params);
        view.setBackgroundColor(color);
        linearLayout.addView(view);
    }

    public void setEditTextAttributes(EditText editText) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(convertDpToPixel(0), convertDpToPixel(0), 0, 0);
        editText.setGravity(Gravity.TOP | Gravity.START);
        editText.setPadding(convertDpToPixel(8), convertDpToPixel(8), convertDpToPixel(8), convertDpToPixel(8));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setBackground(mContext.getDrawable(R.drawable.edit_text_bg_stroke));
        }else{
            editText.setPadding(convertDpToPixel(16), convertDpToPixel(16), convertDpToPixel(16), convertDpToPixel(16));
            editText.setHeight(45);
            editText.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            editText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.edit_text_bg_stroke));
        }

        editText.setTextColor(Color.BLACK);
        editText.setLayoutParams(params);
    }

    public void setCheckBoxAttributes(CheckBox checkBox) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(convertDpToPixel(16),
                convertDpToPixel(16),
                convertDpToPixel(16),
                0
        );

        checkBox.setLayoutParams(params);

        //mContext is used to place the checkbox on the right side of the textview
        //By default, the checkbox is placed at the left side
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple,
                typedValue, true);

        checkBox.setButtonDrawable(null);
        checkBox.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                typedValue.resourceId, 0);
    }

    public void setTextViewAttributes(TextView textView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(convertDpToPixel(0), convertDpToPixel(0), 0, 0);

        //textView.setTextColor(Color.BLACK);
        textView.setTextColor(Color.WHITE);
        //textView.setTextSize(convertDpToPixel(9));
        textView.setLayoutParams(params);
    }

    public void setTextViewAttributes(TextView textView,String newFun) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(convertDpToPixel(0), convertDpToPixel(0), 0, 0);

        //textView.setTextColor(Color.BLACK);
        //textView.setTextSize(convertDpToPixel(9));
        textView.setLayoutParams(params);
    }

    public void setSpinnerAttributes(Spinner spinner) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                convertDpToPixel(40));

        params.setMargins(convertDpToPixel(0), convertDpToPixel(0), 0, 0);
        spinner.setPadding(convertDpToPixel(8), convertDpToPixel(8), convertDpToPixel(8), convertDpToPixel(8));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            spinner.setBackground(mContext.getDrawable(R.drawable.spinner_background));
        }else{
            spinner.setBackground(ContextCompat.getDrawable(mContext, R.drawable.spinner_background));
        }

        spinner.setLayoutParams(params);
    }

    private void setRadioButtonAttributes(RadioButton radioButton) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(convertDpToPixel(16),
                convertDpToPixel(16),
                0, 0
        );

        radioButton.setLayoutParams(params);
    }

    //mContext function to convert DPs to pixels
    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public void addLineSeperator(LinearLayout linearLayout) {
        LinearLayout lineLayout = new LinearLayout(mContext);
        lineLayout.setBackgroundColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(0, convertDpToPixel(10), 0, convertDpToPixel(10));
        lineLayout.setLayoutParams(params);
        linearLayout.addView(lineLayout);
    }


}
