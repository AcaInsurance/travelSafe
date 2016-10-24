package com.aca.travelsafe.Binding;

import android.app.Activity;
import android.widget.Spinner;

import com.aca.travelsafe.PojoModel.SpinnerItem;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SppaMain_Table;
import com.aca.travelsafe.database.SubProductPlan;
import com.aca.travelsafe.database.SubProductPlan_Table;
import com.aca.travelsafe.database.SubProduct_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marsel on 26/4/2016.
 */
public class PlanBind {

    /*  public static void bind(Activity activity, Spinner spinner) {
          try {
              SppaMain sppaMain = new Select(SppaMain_Table.SubProductCode)
                      .from(SppaMain.class)
                      .querySingle();

              String subProductCode = sppaMain.SubProductCode;


              List<SubProductPlan> list = new Select(SubProductPlan_Table.Description)
                      .from(SubProductPlan.class)
                      .where(SubProductPlan_Table.SubProductCode.eq(subProductCode))
                      .queryList();

              List<String> countryList = new ArrayList<>();
              for (SubProductPlan sf : list) {
                  countryList.add(sf.Description);
              }

              SpinnerBinding.bind(activity, spinner, countryList);

          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  */
    public static void bind(Activity activity, Spinner spinner) {
        try {
            SppaMain sppaMain = new Select(SppaMain_Table.SubProductCode)
                    .from(SppaMain.class)
                    .querySingle();

            String subProductCode = sppaMain.SubProductCode;


            List<SubProductPlan> list = new Select(
                    SubProductPlan_Table.PlanCode,
                    SubProductPlan_Table.Description)
                    .from(SubProductPlan.class)
                    .where(SubProductPlan_Table.SubProductCode.eq(subProductCode))
                    .and(SubProduct_Table.IsActive.eq(String.valueOf(true)))
                    .orderBy(SubProductPlan_Table.OrderNo, true)
                    .queryList();

            List<SpinnerItem> arrList = new ArrayList<>();
            SpinnerItem spinnerItem;
            for (SubProductPlan sf : list) {
                spinnerItem = new SpinnerItem(sf.PlanCode, sf.Description);
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


/*
    public static void select (Spinner spinner, String id) {
    *//*    String desc =  new Select().from(SubProductPlan.class)
                .where(SubProductPlan_Table.PlanCode.eq(id))
                .querySingle()
                .SubProductCode;
*//*
        SpinnerBinding.selectWithKey(spinner, id);
    }

    public static String getID (Spinner spinner){
        SpinnerItem spinnerItem = (SpinnerItem) spinner.getSelectedItem();
        return  spinnerItem.getKey();

//        return new Select()
//                .from(SubProductPlan.class)
//                .where(SubProductPlan_Table.PlanCode.eq(spinnerItem.getKey()))
//                .querySingle()
//                .PlanCode;
    }
    */


}
