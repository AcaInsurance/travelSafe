package com.aca.travelsafe.database;

import android.util.Log;

import java.util.List;

/**
 * Created by Marsel on 18/4/2016.
 */
public class Result {
    public String message,
                  detail;

    public Result(String message, String detail) {
         this.message = message;
         this.detail = detail;
    }

    public static String getMessage (List<Result> resultList) {
        if (resultList.isEmpty()) {
            return "";
        }

        return resultList.get(0).message;
    }

    public static String getDetail (List<Result> resultList) {
        if (resultList.isEmpty()) {
            return "";
        }
        return resultList.get(0).detail;
    }

    public static void log (List<Result>  resultList)  {
        int i = 0 ;
        for (Result result: resultList) {

            Log.d("Result " + ++i + " message", result.message);
            Log.d("Result " + ++i + " detail", result.detail);

        }
    }

    public static void log (Result... results)  {
        int i = 0 ;
        for (Result result: results) {

            ++i;
            Log.d("Result " + i + " message", result.message);
            Log.d("Result " + i + " detail", result.detail);

        }
    }


    public void log(){
        Log.d("Result message", message);
        Log.d("Result detail", detail);
    }


}
