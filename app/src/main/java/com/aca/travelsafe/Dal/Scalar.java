package com.aca.travelsafe.Dal;

import com.aca.travelsafe.Holder.PremiHolder;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.Setvar_Table;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProductPlanAdd;
import com.aca.travelsafe.database.SubProductPlanBasic;
import com.aca.travelsafe.database.SubProduct_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by Marsel on 26/4/2016.
 */
public class Scalar {

    public static String getSetVar(String paramCode) {
        Setvar setvar;

        try {
            setvar = new Select().from(Setvar.class)
                    .where(Setvar_Table.ParameterCode.eq(paramCode))
                    .querySingle();

            return setvar.Value;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getExpiredDate() {
        try {
            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            String expDate = sppaMain.ExpireDate;

            return UtilDate.format(expDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getBeginDate() {
        try {
            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            String expDate = sppaMain.EffectiveDate;

            return UtilDate.format(expDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getAge(String expiredDate, String dateOfBirth) {
        try {
            LocalDate now = UtilDate.toDate(expiredDate);
            LocalDate dob = UtilDate.toDate(dateOfBirth);
            int age = UtilDate.yearDiffInPeriode(dob, now);
            return age;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getDaysPeriode() {
        try {
            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            if (sppaMain == null)
                return 0;

            LocalDate effDate = LocalDate.parse(sppaMain.EffectiveDate, ISODateTimeFormat.date());
            LocalDate expDate = LocalDate.parse(sppaMain.ExpireDate, ISODateTimeFormat.date());

            int dayDiff = UtilDate.dayDiff(effDate, expDate) + 1;

            return dayDiff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String getDurationCode(PremiHolder premiHolder) {
        SppaMain sppaMain;
        String durationCode = null;
        try {
            sppaMain = SppaMain.get();

            String subProductCode = sppaMain.SubProductCode;
            String planCode = sppaMain.PlanCode;
            String zoneId = sppaMain.ZoneId;

            if (premiHolder.premiAdd != 0.0) {
                SubProductPlanAdd subProductPlanAdd = SubProductPlanAdd.get(subProductCode, planCode, zoneId);
                durationCode = subProductPlanAdd.DurationCodeAs400;
            } else {
                SubProductPlanBasic subProductPlanBasic = SubProductPlanBasic.get(subProductCode, planCode, zoneId, Scalar.getDaysPeriode());
                durationCode = subProductPlanBasic.DurationCodeAs400;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return durationCode;
    }


    /***
     * @return true = maxday
     * false = -1
     **/
    public static int getMaxDayTravel(String subProductCode) {
        SubProduct subProduct = new Select().from(SubProduct.class)
                .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                .querySingle();

        if (subProduct != null)
            return Integer.parseInt(subProduct.MaxDayTravel);

        return -1;
    }

    public static String getSubProductCode() {
        SppaMain sppaMain;
        try {
            sppaMain = new Select().from(SppaMain.class).querySingle();

            if (sppaMain == null) {
                return "";
            }

            return sppaMain.SubProductCode;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
