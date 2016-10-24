package com.aca.travelsafe.database;

import android.text.TextUtils;

import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Widget.DateTypeConverter;
import com.aca.travelsafe.Widget.MyDate;
import com.facebook.stetho.common.StringUtil;
import com.raizlabs.android.dbflow.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.LocalDate;
import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {SppaFamily.class})
@Table(database = DBMaster.class)
public class SppaFamily extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public int id;

    @Column
    public String SppaFamilyId;
    @Column
    public String SppaNo;
    @Column
    public String SequenceNo;
    @Column
    public String Name;
    @Column
    public String FamilyCode;
    @Column
    public Date DOB;
    @Column
    public String Age;
    @Column
    public String PassportNo;
    @Column
    public String CitizenshipId;
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


    public SppaFamily() {
        CreateBy = Login.getUserID();
        CreateDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        ModifyBy = Login.getUserID();
        ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        IsActive = String.valueOf(true);

    }
}
