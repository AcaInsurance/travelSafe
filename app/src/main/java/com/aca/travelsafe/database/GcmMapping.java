package com.aca.travelsafe.database;

import com.aca.travelsafe.Util.UtilDate;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.Date;


/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {GcmMapping.class})
@Table(database = DBMaster.class)
public class GcmMapping extends BaseModel {
    Date defaultDate = UtilDate.getDateTime().toDate();

    @Column
    @PrimaryKey
    public String UserId;

    @Column
    public String
            RegisteredToken,
            IsActive,
            IsLogin;

    @Column
    public Date
            CreateDate,
            ModifyDate;


    public GcmMapping() {
        CreateDate = defaultDate;
        ModifyDate = defaultDate;
        IsActive = String.valueOf(true);
    }
}
