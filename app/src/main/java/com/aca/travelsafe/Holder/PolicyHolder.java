package com.aca.travelsafe.Holder;

import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.database.ExchangeRate;

/**
 * Created by Marsel on 29/4/2016.
 */
public class PolicyHolder {
    public String coverage,
            type,
            plan,
            periode,
            days,
            destination,
            zone,
            ExchangeRate,
            IsEndors,
            IsTransferAS400,
            TransferDate,
            CommisionRate,
            Commision,
            AreaCodeAs400,
            CoverageCodeAs400,
            DurationCodeAs400,
            AllocationAmount     ;

    public PolicyHolder() {
        coverage = "";
        type = "";
        plan = "";
        periode = "";
        days = "";
        destination = "";
        zone = "";

        IsEndors = "0";
        IsTransferAS400= "0";
        TransferDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        CommisionRate= "0";
        Commision= "0";
        AreaCodeAs400= "0";
        CoverageCodeAs400= "0";
        DurationCodeAs400= "0";
        AllocationAmount     = "0";
    }
}
