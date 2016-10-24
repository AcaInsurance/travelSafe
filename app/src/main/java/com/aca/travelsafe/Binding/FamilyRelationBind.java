package com.aca.travelsafe.Binding;

import android.app.Activity;
import android.widget.Spinner;

import com.aca.travelsafe.PojoModel.SpinnerItem;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.City_Table;
import com.aca.travelsafe.database.FamilyRelation;
import com.aca.travelsafe.database.FamilyRelation_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marsel on 26/4/2016.
 */
public class FamilyRelationBind {

    public static void bind(Activity activity, Spinner spinner) {
        try {
            List<com.aca.travelsafe.database.FamilyRelation> list = new Select()
                    .from(com.aca.travelsafe.database.FamilyRelation.class)
                    .where(FamilyRelation_Table.IsActive.is(String.valueOf(true)))
                        .and(FamilyRelation_Table.FamilyCode.notEq(var.TertanggungUtama))
                    .queryList();
            List<SpinnerItem> arrList = new ArrayList<>();
            SpinnerItem spinnerItem ;

            for (com.aca.travelsafe.database.FamilyRelation f : list) {
                spinnerItem = new SpinnerItem(f.FamilyCode , f.Description);
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


}
