package com.aca.travelsafe.Binding;

import com.aca.travelsafe.Holder.DateHolder;
import com.aca.travelsafe.Util.UtilDate;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.LocalDate;

import java.util.Calendar;

/**
 * Created by Marsel on 26/4/2016.
 */
public  class PickerBind {

    public static DatePickerDialog bind(DatePickerDialog.OnDateSetListener listener) {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthOfYear();
        int day = date.getDayOfMonth();

        DatePickerDialog dpp= DatePickerDialog.newInstance(
                listener,
                year,
                --month,
                day
        );
        return dpp;

    }

    public static void setMax (DatePickerDialog dpp, String maxDate)
    {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(UtilDate.toDate(maxDate).toDate());
            dpp.setMaxDate(calendar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setMin (DatePickerDialog dpp, String minDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(UtilDate.toDate(minDate).toDate());
        dpp.setMinDate(calendar);
    }

}
