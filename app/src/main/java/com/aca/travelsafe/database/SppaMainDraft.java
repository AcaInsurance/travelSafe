package com.aca.travelsafe.database;

import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by Marsel on 6/4/2016.
 */
@Parcel(analyze = {SppaMainDraft.class})
@Table(database = DBMaster.class)
public class SppaMainDraft extends BaseModel {

    @Column
    @PrimaryKey (autoincrement = true)
    public int id;

    @Column
    public String
            SppaNo
            ,SppaDate
            ,SppaStatus
            ,ProductCode
            ,SubProductCode
            ,PlanCode
            ,ZoneId
            ,Name
            ,CurrencyCode
            ,PremiumAmount
            ,PremiumAdditionalAmount
            ,PremiumLoadingAmount
            ,PremiumBdaAmount
            ,ChargeAmount
            ,StampAmount
            ,DiscRate
            ,DiscAmount
            ,TotalPremiumAmount
            ,PromoCode
            ,EffectiveDate
            ,ExpireDate
            ,PolicyNo
            ,AgentCode
            ,AgentUserCode
            ,SppaSubmitDate
            ,SppaSubmitBy
            ,BranchCode
            ,SubBranchCode
            ,TypingBranch
            ,AgeUse
            ,TotalDayTravel
            ,BasicDayTravel
            ,AddDay
            ,AddWeek
            ,AddWeekFactor
            ,BdaDayMax
            ,BdaDay
            ,BdaWeek
            ,BdaWeekFactor
            ,AgeLoadingFactor
            ,NoOfPersonFactor
            ,ExchangeRate
            ,IsEndors
            ,EndorsementTypeId
            ,EndorsBy
            ,EndorsDate
            ,IsFromEndorsment
            ,KdPaymentStatus
            ,PaymentDate
            ,IsTransferAS400
            ,TransferDate
            ,CommisionRate
            ,Commision
            ,AreaCodeAs400
            ,CoverageCodeAs400
            ,DurationCodeAs400
            ,AllocationAmount
            ,IsMobile
            ,CreateBy
            ,CreateDate
            ,ModifyBy
            ,ModifyDate
            ,IsActive
            ,EndorsmentAmount;


    public SppaMainDraft() {

        SppaStatus = var.OPEN;
        SppaDate = UtilDate.getDate().toString(UtilDate.ISO_DATE);
        PolicyNo = null;
        AgentCode = "";
        AgentUserCode = "";
        SppaSubmitDate = null;
        SppaSubmitBy = null;
        BranchCode = "98";
        SubBranchCode = "98";
        TypingBranch = "98";
        AgeUse = null;

        AddDay = null;
        AddWeek = null;
        AddWeekFactor = null;
        BdaDayMax = null;
        BdaDay = null;
        BdaWeek = null;
        BdaWeekFactor = null;
        AgeLoadingFactor = null;
        NoOfPersonFactor = null;


        IsEndors = "0";
        EndorsementTypeId = null;
        EndorsBy = null;
        EndorsDate = null;
        IsFromEndorsment = "0";
        IsTransferAS400 = "0";
        TransferDate = null;
        CommisionRate = "0";
        Commision = "0";
        AreaCodeAs400 = "0";
        IsMobile = String.valueOf(true);

        CreateBy = Login.getUserID();
        CreateDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        ModifyBy = Login.getUserID();
        ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
        IsActive = String.valueOf(true);

    }

    public static SppaMainDraft get () {
        SppaMainDraft sppaMain = null;
        sppaMain = new Select().from(SppaMainDraft.class).querySingle();

        return sppaMain;
    }

}