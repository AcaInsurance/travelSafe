package com.aca.travelsafe.Widget;

import com.raizlabs.android.dbflow.converter.TypeConverter;


import java.util.Date;

/**
 * Created by Marsel on 17/6/2016.
 */
public class DateTypeConverter extends TypeConverter<String, Date> {

    @Override
    public String getDBValue(Date model) {
        return "";
    }

    @Override
    public Date getModelValue(String data) {
        return new Date();
    }
}
