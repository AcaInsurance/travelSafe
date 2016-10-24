package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {MasterPromo.class})
@Table(database = DBMaster.class)
public class MasterPromo extends BaseModel {

    @Column
    @PrimaryKey
    public String PromoCode;
    @Column
    public String PromoName;
    @Column
    public String PromoImage;
    @Column
    public Date StartDate;
    @Column
    public Date EndDate;
    @Column
    public String TotalUsage;
    @Column
    public String IsPercentage;
    @Column
    public Double PromoAmount;
    @Column
    public boolean IsPopup;
    @Column
    public String RemainingUsage;
    @Column
    public String CreateBy;
    @Column
    public String CreateDate;
    @Column
    public String ModifyBy;
    @Column
    public String ModifyDate;
    @Column
    public String IsActive;


    public MasterPromo() {
    }
}
