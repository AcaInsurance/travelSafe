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
@Parcel(analyze = {SppaFlight.class})
@Table(database = DBMaster.class)
public class SppaFlight extends BaseModel {

    @Column
    @PrimaryKey (autoincrement = true)
    public int id;

    @Column
    public String
            SppaFlightId,
            SppaNo,
            FlightDate,
            FlightNo,
            FlightFrom,
            FlightTo,
            CreateBy,
            CreateDate,
            ModifyBy,
            ModifyDate,
            IsActive;



    public SppaFlight() {
        CreateBy = Login.getUserID();
        CreateDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        ModifyBy = Login.getUserID();
        ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        IsActive = String.valueOf(true);
    }
}
