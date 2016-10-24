package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Table(database = DBMaster.class)
public class VersionModel extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public int _id;

    @Column
    public String
            Version,
            DateTimeUpdate,
            ForceUpgrade,
            Maintenance;

}
