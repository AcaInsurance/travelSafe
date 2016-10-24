package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import retrofit2.http.Url;

/**
 * Created by Marsel on 6/4/2016.
 */

@Table(database = DBMaster.class)
public class SubProduct extends BaseModel {
    @Column
    @PrimaryKey
    public String
            SubProductCode;

    @Column
    public String
            Description
            ,ProductCode
            ,OrderNo
            ,IsConventional
            ,Url
            ,ScheduleName
            ,AttachBenefit
            ,AttachClaim
            ,AttachOthers
            ,WsUrl
            ,IdOrFa
            ,AnnOrReg
            ,IsNeedFlight
            ,MaxAgeFrom
            ,MaxAgeTo
            ,MinAge
            ,MaxDay
            ,MaxDayBda
            ,LoadingPct
            ,Charges
            ,BdaAmount
            ,StampAmount
            ,MaxDayTravel
            ,IsConvertToIdr
            ,MinMember
            ,IsMobile
            ,IsActive;

    public static SubProduct get (String subProductCode, String productCode) {
        SubProduct subProduct = null;
        subProduct = new Select().from(SubProduct.class)
                .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                .and(SubProduct_Table.ProductCode.eq(productCode))
                .and(SubProduct_Table.IsActive.eq(String.valueOf(true)))
                .querySingle();

        return subProduct;
    }


    public static SubProduct get ( ) {
        SubProduct subProduct = null;
        SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
        if (sppaMain == null) return null;
        String subProductCode = sppaMain.SubProductCode;
        String productCode = sppaMain.ProductCode;
        subProduct = new Select().from(SubProduct.class)
                .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                .and(SubProduct_Table.ProductCode.eq(productCode))
                .and(SubProduct_Table.IsActive.eq(String.valueOf(true)))
                .querySingle();

        return subProduct;
    }

}
