package com.aca.travelsafe.database;

import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;
import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {ExchangeRate.class})
@Table(database = DBMaster.class)
public class ExchangeRate extends BaseModel
{
    @Column
    @PrimaryKey (autoincrement = true)
    public int
            ExchId;

    @Column
    public String
                CurrencyCode;
    @Column
    public Date AsOfDate;
    @Column
    public Double ExchRate;
    @Column
    public String IsActive;

    public ExchangeRate() {
        CurrencyCode = "USD";
        AsOfDate = UtilDate.getDateTime().toDate();
        IsActive = var.TRUE;
    }
}
