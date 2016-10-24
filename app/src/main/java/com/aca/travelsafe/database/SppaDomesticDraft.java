package com.aca.travelsafe.database;

import com.aca.travelsafe.Util.UtilDate;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {SppaDomesticDraft.class})
@Table(database = DBMaster.class)
public class SppaDomesticDraft extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public int id;


    @Column
    public String SppaNo,
            CityId,
            CreateBy,
            CreateDate,
            ModifyBy,
            ModifyDate,
            IsActive;


    public SppaDomesticDraft() {
        CreateBy = Login.getUserID();
        CreateDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        ModifyBy = Login.getUserID();
        ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        IsActive = String.valueOf(true);

    }

}
