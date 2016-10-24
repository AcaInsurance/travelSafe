package com.aca.travelsafe.Widget;

import com.aca.travelsafe.Util.UtilDate;

import org.joda.time.LocalDate;

import java.util.Date;

/**
 * Created by Marsel on 17/6/2016.
 */
public class MyDate extends Date {
    @Override
    public String toString() {
        return LocalDate.fromDateFields(this).toString(UtilDate.BASIC_MON_DATE);
    }
}
