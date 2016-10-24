package com.aca.travelsafe.Dal;

import android.text.TextUtils;

import com.aca.travelsafe.Holder.PolicyHolder;
import com.aca.travelsafe.Holder.PremiHolder;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.database.Login;
import com.aca.travelsafe.database.Promo;
import com.aca.travelsafe.database.SppaBeneficiary;
import com.aca.travelsafe.database.SppaDestination;
import com.aca.travelsafe.database.SppaDomestic;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaFlight;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.SppaMain;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by Marsel on 12/5/2016.
 */
public class SaveSppa {

    public SaveSppa() {
    }


    public static void clearSppa() {
        Delete.table(SppaMain.class);
        Delete.table(SppaInsured.class);
        Delete.table(SppaBeneficiary.class);
        Delete.table(SppaDestination.class);
        Delete.table(SppaDomestic.class);
        Delete.table(SppaFlight.class);
        Delete.table(SppaFamily.class);
    }

    public void saveSPPAMain(PremiHolder premiHolder, PolicyHolder policyHolder) {
        SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();

        Policy policy = new Policy();
        SppaMain sppaMainHolder = policy.fillSppaMain(premiHolder);
        if (sppaMain == null)
            sppaMain = new SppaMain();

        sppaMain.CurrencyCode = premiHolder.currency;
        sppaMain.PremiumAmount = String.valueOf(premiHolder.premiBasic);
        sppaMain.PremiumAdditionalAmount = String.valueOf(premiHolder.premiAdd);
        sppaMain.PremiumLoadingAmount = String.valueOf(premiHolder.premiLoading);
        sppaMain.PremiumBdaAmount = String.valueOf(premiHolder.premiBDA);
        sppaMain.ChargeAmount = String.valueOf(premiHolder.charge);
        sppaMain.StampAmount = String.valueOf(premiHolder.stamp);
        sppaMain.EndorsmentAmount = String.valueOf(0.0);
        sppaMain.TotalPremiumAmount = String.valueOf(premiHolder.getTotal());
        sppaMain.AllocationAmount = String.valueOf(premiHolder.premiAllocation);
        sppaMain.DiscAmount = String.valueOf(premiHolder.getDiskonAmount());
//        sppaMain.PromoCode = promo == null ? null : promo.PromoCode;

        sppaMain.TotalDayTravel = String.valueOf(policyHolder.days);
        sppaMain.BasicDayTravel = String.valueOf(policyHolder.days);

        sppaMain.ExchangeRate = sppaMainHolder.ExchangeRate;
        sppaMain.IsEndors = sppaMainHolder.IsEndors;
        sppaMain.IsTransferAS400 = sppaMainHolder.IsTransferAS400;
        sppaMain.TransferDate = sppaMainHolder.TransferDate;
        sppaMain.CommisionRate = sppaMainHolder.CommisionRate;
        sppaMain.Commision = sppaMainHolder.Commision;
        sppaMain.AreaCodeAs400 = sppaMainHolder.AreaCodeAs400;
        sppaMain.CoverageCodeAs400 = sppaMainHolder.CoverageCodeAs400;
        sppaMain.DurationCodeAs400 = sppaMainHolder.DurationCodeAs400;

        sppaMain.CreateBy = TextUtils.isEmpty(sppaMain.CreateBy)
                ? Login.getUserID()
                : sppaMain.CreateBy;

        sppaMain.ModifyBy = Login.getUserID();
        sppaMain.ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);

        sppaMain.save();
    }


    public void saveSPPAInsured() {
        SppaInsured sppaInsured = new Select().from(SppaInsured.class).querySingle();


        sppaInsured.Email = Login.getUserID();
        sppaInsured.CreateBy = TextUtils.isEmpty(sppaInsured.CreateBy)
                ? Login.getUserID()
                : sppaInsured.CreateBy;

        sppaInsured.ModifyBy = Login.getUserID();
        sppaInsured.ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);

        sppaInsured.save();
    }


    public void saveSPPABeneficiary() {
        List<SppaBeneficiary> sppaBeneficiaryList = new Select().from(SppaBeneficiary.class).queryList();

        for (SppaBeneficiary s : sppaBeneficiaryList) {

            s.CreateBy = TextUtils.isEmpty(s.CreateBy)
                    ? Login.getUserID()
                    : s.CreateBy;
            s.ModifyBy = Login.getUserID();
            s.ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);

            s.save();
        }
    }

    public void saveSPPADestination() {
        List<SppaDestination> sppaDestinationList = new Select().from(SppaDestination.class).queryList();

        for (SppaDestination s : sppaDestinationList) {

            s.CreateBy = TextUtils.isEmpty(s.CreateBy)
                    ? Login.getUserID()
                    : s.CreateBy;
            s.ModifyBy = Login.getUserID();
            s.ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);

            s.save();
        }
    }

    public void saveSPPADomestic() {
        List<SppaDomestic> sppaDomesticList = new Select().from(SppaDomestic.class).queryList();

        for (SppaDomestic s : sppaDomesticList) {

            s.CreateBy = TextUtils.isEmpty(s.CreateBy)
                    ? Login.getUserID()
                    : s.CreateBy;
            s.ModifyBy = Login.getUserID();
            s.ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);

            s.save();
        }
    }

    public void saveSPPAFamily() {
        List<SppaFamily> sppaFamilyList = new Select().from(SppaFamily.class).queryList();

        for (SppaFamily s : sppaFamilyList) {
            
            s.CreateBy = TextUtils.isEmpty(s.CreateBy)
                    ? Login.getUserID()
                    : s.CreateBy;
            s.ModifyBy = Login.getUserID();
            s.ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);

            s.save();
        }
    }

    public void saveSPPAFlight() {
        List<SppaFlight> sppaFlightList = new Select().from(SppaFlight.class).queryList();

        for (SppaFlight s : sppaFlightList) {

            s.CreateBy = TextUtils.isEmpty(s.CreateBy)
                    ? Login.getUserID()
                    : s.CreateBy;
            s.ModifyBy = Login.getUserID();
            s.ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);

            s.save();
        }
    }
}
