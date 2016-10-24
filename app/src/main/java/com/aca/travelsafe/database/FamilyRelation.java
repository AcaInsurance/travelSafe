package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {FamilyRelation.class})
@Table(database = DBMaster.class)
public class FamilyRelation extends BaseModel {

    @Column
    @PrimaryKey
    public String FamilyCode;

    @Column
    public String
                Description
                ,MaxAge
                ,OrderNo
                ,BenefitPercentage
                ,As400Code
                ,IsActive;


    public FamilyRelation() {
    }
}
