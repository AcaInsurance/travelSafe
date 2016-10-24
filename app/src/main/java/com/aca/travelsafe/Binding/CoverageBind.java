package com.aca.travelsafe.Binding;

import android.app.Activity;
import android.widget.Spinner;

import com.aca.travelsafe.PojoModel.SpinnerItem;
import com.aca.travelsafe.database.Product;
import com.aca.travelsafe.database.Product_Table;
import com.aca.travelsafe.database.Zone_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marsel on 26/4/2016.
 */
public class CoverageBind {
    public static void bind(Activity activity, Spinner spinner) {
        try {
            List<Product> list = new Select()
                    .from(Product.class)
                    .queryList();
            List<SpinnerItem> arrList = new ArrayList<>();
            SpinnerItem spinnerItem;

            for (Product product : list) {
                spinnerItem = new SpinnerItem(product.ProductCode, product.Description);
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
