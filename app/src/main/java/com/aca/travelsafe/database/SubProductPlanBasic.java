package com.aca.travelsafe.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.property.DoubleProperty;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Table(database = DBMaster.class)
public class SubProductPlanBasic extends BaseModel {

    @Column
    @PrimaryKey
    public String
            SubPlanBasicId;

    @Column
    public String
             PlanCode
            ,SubProductCode
            ,PremiumAmount
            ,AllocationAmount
            ,ZoneId
            ,DurationCodeAs400
            ,IsActive;

    @Column public int
             DayFrom
            ,DayTo;

    /*
    *Daydiff maksimal 31
    * */
    public static SubProductPlanBasic get (String subProductCode, String planCode, String zoneId, int dayDiff) {
        dayDiff = dayDiff > 31 ? 31 : dayDiff;

        SubProductPlanBasic subProductPlanBasic = null;
        subProductPlanBasic = new Select().from(SubProductPlanBasic.class)
                .where(SubProductPlanBasic_Table.SubProductCode.eq(subProductCode))
                .and(SubProductPlanBasic_Table.PlanCode.eq(planCode))
                .and(SubProductPlanBasic_Table.ZoneId.eq(zoneId))
                .and(SubProductPlanBasic_Table.DayFrom.lessThanOrEq(dayDiff))
                .and(SubProductPlanBasic_Table.DayTo.greaterThanOrEq(dayDiff))
                .and(SubProductPlanBasic_Table.IsActive.is(String.valueOf(true)))
                .querySingle();

        return subProductPlanBasic;
    }

}
