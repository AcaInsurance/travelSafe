package com.aca.travelsafe.Binding;

import android.app.Activity;
import android.widget.Spinner;

import com.aca.travelsafe.PojoModel.SpinnerItem;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marsel on 26/4/2016.
 */
public class CityBind {

    public static void bind(Activity activity, Spinner spinner) {
        try {
            List<com.aca.travelsafe.database.City> list = new Select()
                    .from(com.aca.travelsafe.database.City.class)
                    .queryList();
            List<SpinnerItem> arrList = new ArrayList<>();
            SpinnerItem spinnerItem;

            for (com.aca.travelsafe.database.City c : list) {
                spinnerItem = new SpinnerItem(c.CityId, c.CityName);
                arrList.add(spinnerItem);
            }

            SpinnerBinding.bindWithKey(activity, spinner, arrList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getID(Spinner spinner) {
        return SpinnerBinding.getKey(spinner);
    }


    public static void select(Spinner spinner, String id) {
        SpinnerBinding.selectWithKey(spinner, id);
    }


}
