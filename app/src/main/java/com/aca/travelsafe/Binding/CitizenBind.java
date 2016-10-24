package com.aca.travelsafe.Binding;

import android.app.Activity;
import android.widget.Spinner;

import com.aca.travelsafe.PojoModel.SpinnerItem;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.StandardField;
import com.aca.travelsafe.database.StandardField_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marsel on 26/4/2016.
 */
public class CitizenBind {

    public static void bind(Activity activity, Spinner spinner) {
        try {
            List<StandardField> list = new Select()
                    .from(StandardField.class)
                    .where(StandardField_Table.FieldCode.eq(var.Citizenship))
                    .queryList();
            List<SpinnerItem> arrList = new ArrayList<>();
            SpinnerItem spinnerItem ;

            for (StandardField sf : list) {
                spinnerItem = new SpinnerItem(sf.FieldCodeDt, sf.FieldNameDt);
                arrList.add(spinnerItem);
            }

            SpinnerBinding.bindWithKey(activity, spinner, arrList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getID (Spinner spinner){
       return SpinnerBinding.getKey(spinner);
    }


    public static void select (Spinner spinner, String id) {
        SpinnerBinding.selectWithKey(spinner, id);
    }

    /*
    public static String getID (Spinner spinner){
        return new Select()
                .from(StandardField.class)
                .where(StandardField_Table.FieldNameDt.eq(spinner.getSelectedItem().toString()))
                .querySingle()
                .FieldCodeDt;
    }



    public static void select (Spinner spinner, String id) {
        String desc =  new Select().from(StandardField.class)
                .where(StandardField_Table.FieldCodeDt.eq(id))
                .querySingle()
                .FieldNameDt;

        SpinnerBinding.select(spinner, desc);
    }*/




}
