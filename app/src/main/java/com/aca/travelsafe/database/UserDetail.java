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
@Parcel(analyze = {UserDetail.class})
@Table(database = DBMaster.class)
public class UserDetail extends BaseModel{

    @Column
    @PrimaryKey(autoincrement = true)
    public int id;

    @Column
    public String
            UserId,
            DOB,
            Name,
            MobilePhoneNo,
            IdentityNo,
            PassportNo,
            Country,
            Address,
            City,
            Phone,
            Email,
            CreateBy,
            CreateDate,
            ModifyBy,
            ModifyDate,
            IsActive;

    public UserDetail () {
        CreateBy = "";
        CreateDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        ModifyBy = "";
        ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        IsActive = String.valueOf(true);
    }


}
