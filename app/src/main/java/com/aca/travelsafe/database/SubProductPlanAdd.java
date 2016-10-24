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
public class SubProductPlanAdd extends BaseModel {
        @Column
        @PrimaryKey
        public String
                SubPlanAddId;


        @Column
        public String
                 PlanCode;
        @Column
        public String SubProductCode;
        @Column
        public String PremiumAmount;
        @Column
        public String AllocationAmount;
        @Column
        public String ZoneId;
        @Column
        public String DurationCodeAs400;
        @Column
        public String IsActive;

        public static SubProductPlanAdd get (String subProductCode, String planCode, String zoneId) {
            SubProductPlanAdd subProductPlanAdd = null;
            subProductPlanAdd = new Select().from(SubProductPlanAdd.class)
                    .where(SubProductPlanAdd_Table.SubProductCode.eq(subProductCode))
                    .and(SubProductPlanAdd_Table.PlanCode.eq(planCode))
                    .and(SubProductPlanAdd_Table.ZoneId.eq(zoneId))
                    .and(SubProductPlanAdd_Table.IsActive.is(String.valueOf(true)))
                    .querySingle();

            return subProductPlanAdd;

        }
}
