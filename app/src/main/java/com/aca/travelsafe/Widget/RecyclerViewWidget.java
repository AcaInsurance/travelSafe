package com.aca.travelsafe.Widget;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Marsel on 21/3/2016.
 */
public class RecyclerViewWidget {
    private static LinearLayoutManager linearLayoutManager;


    public static void create  (Activity activity, RecyclerView recyclerView) {
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
    }

    public static void createNoDivider  (Activity activity, RecyclerView recyclerView) {
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    public static LinearLayoutManager getLinearLayoutManager(){
        return linearLayoutManager;
    }


}
