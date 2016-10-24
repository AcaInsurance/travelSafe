package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.lang.reflect.GenericArrayType;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {GeneralSetting.class})
@Table(database = DBMaster.class)
public class GeneralSetting extends BaseModel {

    @Column
    @PrimaryKey (autoincrement = true)
    public int id;
    @Column
    public String ParameterCode;
    @Column
    public String ParameterValue;

    public GeneralSetting() {
    }

    public static void insert(String parameterCode, String parameterValue) {
        GeneralSetting generalSetting = new Select().from(GeneralSetting.class)
                .where(GeneralSetting_Table.ParameterCode.eq(parameterCode))
                .querySingle();

        if (generalSetting == null)
            generalSetting = new GeneralSetting();
        generalSetting.ParameterCode = parameterCode;
        generalSetting.ParameterValue = parameterValue;
        generalSetting.save();
    }


    public static void delete(String parameterCode) {
        GeneralSetting generalSetting = new Select().from(GeneralSetting.class)
                .where(GeneralSetting_Table.ParameterCode.eq(parameterCode))
                .querySingle();

        if (generalSetting == null)
            return;

        generalSetting.delete();
    }

    public static void deleteAll () {
        Delete.table(GeneralSetting.class);
    }

    public static String getParameterValue (String parameterCode) {
        GeneralSetting generalSetting = new Select().from(GeneralSetting.class)
                .where(GeneralSetting_Table.ParameterCode.eq(parameterCode))
                .querySingle();

        if (generalSetting == null)
            return "";

        return generalSetting.ParameterValue;
    }
}
