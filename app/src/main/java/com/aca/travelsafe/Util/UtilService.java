package com.aca.travelsafe.Util;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.aca.travelsafe.R;

import java.io.IOException;

/**0
 * Created by Marsel on 8/13/2015.
 */
public class UtilService {
    private static final String ipServer = "http://172.16.88.31/";
//    private static final String ipServer = "http://182.23.65.68/";

    private static final String urlSendPolis = ipServer + "newTravelsafeMigrasi/";
//    private static final String urlSendPolis = "http://www.travelsafe-acains.com/agency/";

    private static final String urlTest =  ipServer + "waTravelsafe/api/";
    private static final String urlPaymentTest = ipServer + "waTravelPayment/api/";

/*
    private static final String urlPaymentTest = "http://10.0.3.2/waTravelPayment/api/";
    private static final String urlTest = "http://10.0.3.2/waTravelsafe/api/";
    private static final String urlTest = "http://10.0.3.2/waTravelsafe/api/";
*/


    public static final String wsURL = urlTest;
    public static final String wsPaymentURL = urlPaymentTest;
    public static final String sendPolisURL = urlSendPolis;

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isOnline(Context context) {
        /*if (!isNetworkAvailable(context))
            return false;*/

        return isNetworkAvailable(context);
/*
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;*/
    }

    public static void showSnackbar(View view) {
        Snackbar.make(view, R.string.message_no_connection, Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackbar(View view, View.OnClickListener listener) {
        Snackbar.make(view, R.string.message_no_connection, Snackbar.LENGTH_SHORT)
                .setAction(R.string.retry, listener)
                .show();
    }

}
