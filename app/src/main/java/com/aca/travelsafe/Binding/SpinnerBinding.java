package com.aca.travelsafe.Binding;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aca.travelsafe.PojoModel.SpinnerItem;
import com.aca.travelsafe.R;

import java.util.List;

/**
 * Created by Marsel on 26/4/2016.
 */
public class SpinnerBinding {
    public static String getKey (Spinner spinner) {
        SpinnerItem spinnerItem = (SpinnerItem) spinner.getSelectedItem();
        return  spinnerItem.getKey();
    }

    public static void selectWithKey(android.widget.Spinner spinner, String key) {
        ArrayAdapter<SpinnerItem> adapter = (ArrayAdapter<SpinnerItem>) spinner.getAdapter();

        SpinnerItem spinnerItem;
        for (int i = 0; i < adapter.getCount(); i++) {
            spinnerItem = adapter.getItem(i);

            if(spinnerItem.getKey().equals(key)) {
                spinner.setSelection(i);
            }
        }
    }


    public static void select(android.widget.Spinner spinner, String item) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(item);
        spinner.setSelection(position);
    }

    public static void bindWithKey(Activity activity, android.widget.Spinner spinner, List<SpinnerItem> arrList) {
        try {
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(activity, R.layout.simple_spinner_item, arrList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void bindWithKey(Activity activity, android.widget.Spinner spinner, List<SpinnerItem> arrList, boolean whiteTextColor) {
        try {
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(activity, whiteTextColor ? R.layout.simple_spinner_item_white_text : android.R.layout.simple_spinner_item, arrList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bind(Activity activity, android.widget.Spinner spinner, List<String> arrList) {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, arrList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
