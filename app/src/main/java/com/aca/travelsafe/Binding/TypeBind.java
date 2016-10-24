package com.aca.travelsafe.Binding;

import android.app.Activity;
import android.widget.Spinner;

import com.aca.travelsafe.PojoModel.SpinnerItem;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProduct_Table;
import com.aca.travelsafe.database.Zone_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marsel on 26/4/2016.
 */
public class TypeBind {
    public static void bind(Activity activity, Spinner spinner, String productCode) {
        try {
            List<SubProduct> list = new Select()
                    .from(SubProduct.class)
                    .where(SubProduct_Table.ProductCode.eq(productCode)).and(SubProduct_Table.IsMobile.eq(var.TRUE))
                    .queryList();

            List<SpinnerItem> arrList = new ArrayList<>();
            SpinnerItem spinnerItem;

            for (SubProduct sp : list) {
                spinnerItem = new SpinnerItem(sp.SubProductCode, sp.Description);
                arrList.add(spinnerItem);
            }

            SpinnerBinding.bindWithKey(activity, spinner, arrList, true);

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
