package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Table(database = DBMaster.class)
public class Zone extends BaseModel{

    @Column
    @PrimaryKey
    public String
            ZoneId;

    @Column
    public String
            ZoneName,
            As400Code,
            IsActive;

}
