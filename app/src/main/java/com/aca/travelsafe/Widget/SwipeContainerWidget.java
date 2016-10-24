package com.aca.travelsafe.Widget;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by Marsel on 21/3/2016.
 */
public class SwipeContainerWidget {
    private static SwipeRefreshLayout swipeContainer;

    public static SwipeRefreshLayout create (SwipeRefreshLayout swipeContainerRefreshLayout){
        /*swipeContainer = swipeContainerRefreshLayout;
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light);
*/

        swipeContainerRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        return swipeContainerRefreshLayout;
    }
}
