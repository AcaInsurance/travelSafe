package com.aca.travelsafe.database;

import android.text.TextUtils;

import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.runtime.transaction.process.DeleteModelListTransaction;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.text.Format;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {SppaInsured.class})
@Table(database = DBMaster.class)
public class SppaInsured extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public int id;

    @Column
    public String SppaNo;
    @Column
    public String InsuredName;
    @Column
    public String DateOFBirth;
    @Column
    public String Age;
    @Column
    public String Address;
    @Column
    public String CityId;
    @Column
    public String PhoneNo;
    @Column
    public String MobileNo;
    @Column
    public String Email;
    @Column
    public String CitizenshipId;
    @Column
    public String PassportNo;
    @Column
    public String IdNo;
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


    public SppaInsured() {
        CreateBy = Login.getUserID();
        CreateDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        ModifyBy = Login.getUserID();
        ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        IsActive = String.valueOf(true);

    }

    public SppaInsured get () {
        SppaInsured sppaInsured = null;
        sppaInsured = new Select().from(SppaInsured.class).querySingle();
        return sppaInsured;
    }

    public String getAddress(String address)  {
        if (TextUtils.isEmpty(address))
            return "";

        return address.replace("_","");
    }

    public String setAddress(String address) {
        try {
            int splitterLength = var.MAX_ADDRESS;

            String tempAddress = "";
            String formattedAddress = "";
            String splitter =  "_";
            String temp = "";

            for (int i = 0 ; i < 3 ; i ++) {
                temp = address.substring(
                        tempAddress.length(),
                        tempAddress.length() + splitterLength > address.length()
                        ? address.length()
                        : tempAddress.length() +  splitterLength);

                tempAddress += temp;
                formattedAddress = formattedAddress + temp;

                if (i != 2) formattedAddress += splitter;
            }

            return formattedAddress;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
