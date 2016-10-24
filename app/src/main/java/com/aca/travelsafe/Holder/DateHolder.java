package com.aca.travelsafe.Holder;

import com.aca.travelsafe.Util.UtilDate;

import org.joda.time.LocalDate;

/**
 * Created by Marsel on 9/6/2016.
 */
public class DateHolder {
    public int year, month, day;
    public String tanggal;
    public LocalDate date;

    public DateHolder() {
        LocalDate date = UtilDate.getDate();

        year = date.getYear();
        month = date.getMonthOfYear();
        day = date.getDayOfMonth();
    }

    public void splitDate(String tanggal) {
        LocalDate date = UtilDate.toDate(tanggal);
        year = date.getYear();
        month = date.getMonthOfYear();
        day = date.getDayOfMonth();
    }
}
