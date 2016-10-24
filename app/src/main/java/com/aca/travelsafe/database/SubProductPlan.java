package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Table(database = DBMaster.class)
public class SubProductPlan extends BaseModel {
    @Column
    @PrimaryKey
    public String
            PlanCode;

    @Column
    public String
                SubProductCode
                ,Description
                ,OrderNo
                ,Benefit
                ,BenefitImage
                ,CoverageCodeAs400
                ,IsActive;

    public static SubProductPlan get (String planCode) {
        SubProductPlan subProductPlan = null;
        subProductPlan = new Select().from(SubProductPlan.class)
                .where(SubProductPlan_Table.PlanCode.eq(planCode))
                .querySingle();

        return subProductPlan;
    }
}
