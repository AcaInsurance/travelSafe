package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {AATemplate.class})
@Table(database = DBMaster.class)
public class AATemplate extends BaseModel {

    @Column
    @PrimaryKey
    public String CountryId;

    @Column
    public String
                CountryName;

    public AATemplate() {
    }
}
