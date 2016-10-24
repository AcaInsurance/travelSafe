package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Marsel on 3/11/2015.
 */
@Database(name = DBMaster.NAME, version = DBMaster.VERSION)
 public class DBMaster {

    public static final String NAME = "Travel";
    public static final int VERSION = 1;
}