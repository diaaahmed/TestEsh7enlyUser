package com.esh7enly.esh7enlyuser.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.esh7enly.domain.entity.SpinnerModel;
import com.esh7enly.esh7enlyuser.R;


import java.util.List;

public class SpinnerHelper {

    private static final String TAG = "SpinnerAdapter";

    private ArrayAdapter<String> adapter;
    private Context context;
    private boolean hasHint = false;
    private List<SpinnerModel> list;
    private int position;


    public SpinnerHelper(Context context, List<SpinnerModel> list, boolean hasHint) {
        this.context = context;
        this.list = list;
        this.hasHint = hasHint;
    }

    public void setAdapter(Spinner spinner) {

        this.adapter = new ArrayAdapter<String>(this.context, android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint((CharSequence) getItem(getCount()));
                    ((TextView) v.findViewById(android.R.id.text1)).setTextColor(ViewCompat.MEASURED_STATE_MASK);
                    ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(SpinnerHelper.this.context.getResources().getColor(R.color.colorAccent));
                    ((TextView) v.findViewById(android.R.id.text1)).setHighlightColor(ViewCompat.MEASURED_STATE_MASK);
                }
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(ViewCompat.MEASURED_STATE_MASK);
                ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(ViewCompat.MEASURED_STATE_MASK);
                ((TextView) v.findViewById(android.R.id.text1)).setHighlightColor(ViewCompat.MEASURED_STATE_MASK);
                ((TextView) v.findViewById(android.R.id.text1)).setGravity(Gravity.CENTER);

                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v.findViewById(android.R.id.text1)).setTextColor(ViewCompat.MEASURED_STATE_MASK);
                ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(ViewCompat.MEASURED_STATE_MASK);
                ((TextView) v.findViewById(android.R.id.text1)).setHighlightColor(ViewCompat.MEASURED_STATE_MASK);
                ((TextView) v.findViewById(android.R.id.text1)).setGravity(Gravity.CENTER);
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };

        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i < this.list.size(); i++) {
            this.adapter.add(((SpinnerModel) this.list.get(i)).getaName());
        }

        spinner.setAdapter(this.adapter);
        spinner.setSelection(this.adapter.getCount());
        spinner.setOnItemSelectedListener(new onItemSelected());
    }

    class onItemSelected implements OnItemSelectedListener {
        onItemSelected() {
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            SpinnerHelper.this.position = Integer.parseInt(((SpinnerModel) SpinnerHelper.this.list.get(i)).getId());
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
    
    public void setPosition(int position) {
    }

    public void notifyDataChange() {
        this.adapter.notifyDataSetChanged();
    }

    public void removeItem(String item) {
        this.adapter.remove(item);
    }

    public int getPosition() {
        return this.position;
    }
}
