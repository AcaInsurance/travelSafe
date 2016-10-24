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
public class SubProductPlanBDA extends BaseModel {
    @Column
    @PrimaryKey
    public String
            SubPlanBdaId;

    @Column
    public String
            PlanCode,
            SubProductCode,
            PremiumAmount,
            ZoneId,
            IsActive;

    public static SubProductPlanBDA get (String subProductCode, String planCode, String zoneId) {
        SubProductPlanBDA subProductPlanBDA = null;

        subProductPlanBDA = new Select().from(SubProductPlanBDA.class)
                .where(SubProductPlanBDA_Table.SubProductCode.eq(subProductCode))
                .and(SubProductPlanBDA_Table.PlanCode.eq(planCode))
                .and(SubProductPlanBDA_Table.ZoneId.eq(zoneId))
                .and(SubProductPlanBDA_Table.IsActive.is(String.valueOf(true)))
                .querySingle();

        return subProductPlanBDA;
    }

}
