package com.aca.travelsafe.database;

import com.aca.travelsafe.Util.var;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 6/4/2016.
 */

@Table(database = DBMaster.class)
public class Setvar extends BaseModel {
    @Column
    @PrimaryKey
    public String
            ParameterCode;


    @Column
    public String
            ParameterName,
            Value,
            IsVisible,
            Description,
            IsActive;

    public Setvar() {
        IsVisible = var.TRUE;
        IsActive = var.TRUE;
    }

    public static String getParameterName(String parameterCode) {
        Setvar setvar = new Select().from(Setvar.class)
                .where(Setvar_Table.ParameterCode.eq(parameterCode))
                .querySingle();

        if (setvar == null)
            return "";

        return setvar.ParameterName;
    }

    public static String getValue(String parameterCode) {
        Setvar setvar = new Select().from(Setvar.class)
                .where(Setvar_Table.ParameterCode.eq(parameterCode))
                .querySingle();

        if (setvar == null)
            return "";

        return setvar.Value;
    }
}
