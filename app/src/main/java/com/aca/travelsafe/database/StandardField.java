package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by Marsel on 6/4/2016.
 */
@Table(database = DBMaster.class)
public class StandardField extends BaseModel {
    @Column
    @PrimaryKey
    public String
            FieldCode,
            FieldCodeDt;

    @Column
    public String
                FieldNameDt,
                Value,
                Description,
                IsActive;

    public static List<StandardField> getList(String fieldCode) {
        List<StandardField> standardField = new Select()
                .from(StandardField.class)
                .where(StandardField_Table.FieldCode.eq(fieldCode))
                .and(StandardField_Table.IsActive.eq(String.valueOf(true)))
                .queryList();

        return standardField;
    }

}
