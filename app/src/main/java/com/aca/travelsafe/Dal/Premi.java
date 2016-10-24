package com.aca.travelsafe.Dal;

import com.aca.travelsafe.Holder.PremiHolder;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.ExchangeRate;
import com.aca.travelsafe.database.Product;
import com.aca.travelsafe.database.Product_Table;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProductPlanAdd;
import com.aca.travelsafe.database.SubProductPlanAdd_Table;
import com.aca.travelsafe.database.SubProductPlanBDA;
import com.aca.travelsafe.database.SubProductPlanBDA_Table;
import com.aca.travelsafe.database.SubProductPlanBasic;
import com.aca.travelsafe.database.SubProductPlanBasic_Table;
import com.aca.travelsafe.database.SubProduct_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by Marsel on 28/4/2016.
 */
public class Premi {
    private PremiHolder holder;

    public Premi() {
        holder = new PremiHolder();
    }

    public PremiHolder loadPremi() {
        try {
            SppaMain sppaMain = SppaMain.get();

            if (sppaMain == null) {
                return null;
            }

            PremiHolder holder = new PremiHolder();
            holder.premiBasic = Double.parseDouble(sppaMain.PremiumAmount);
            holder.premiAdd = Double.parseDouble(sppaMain.PremiumAdditionalAmount);
            holder.premiBDA = Double.parseDouble(sppaMain.PremiumBdaAmount);
            holder.premiAllocation = Double.parseDouble(sppaMain.AllocationAmount);
            holder.premiLoading = Double.parseDouble(sppaMain.PremiumLoadingAmount);
            holder.charge = Double.parseDouble(sppaMain.ChargeAmount);
            holder.stamp = Double.parseDouble(sppaMain.StampAmount);
            holder.currency = sppaMain.CurrencyCode;
            holder.potongan = Double.parseDouble(sppaMain.DiscAmount);
            holder.exchangeRate = Double.parseDouble(sppaMain.ExchangeRate);
            return holder;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    public PremiHolder countPremi() {
        try {
            SppaMain sppaMain = SppaMain.get();

            if (sppaMain == null)
                return null;

            String subProductCode = sppaMain.SubProductCode;
            String productCode = sppaMain.ProductCode;
            String zoneId = sppaMain.ZoneId;
            String planCode = sppaMain.PlanCode;
            int dayDiff = Scalar.getDaysPeriode();

            Product product = Product.get(productCode);
            holder.currency = product.CurrencyCode;


            SubProduct subProduct = SubProduct.get(subProductCode, productCode);

            int maxDay = Integer.parseInt(subProduct.MaxDay);
            int maxDayBDA = Integer.parseInt(subProduct.MaxDayBda);
            int maxAgeFrom = Integer.parseInt(subProduct.MaxAgeFrom);
            int maxAgeTo = Integer.parseInt(subProduct.MaxAgeTo);
            double loadingPct = Double.parseDouble(subProduct.LoadingPct);
            double stamp = Double.parseDouble(subProduct.StampAmount);
            double policyCharge = Double.parseDouble(subProduct.Charges);

            countBasicPremi(subProductCode, planCode, zoneId, dayDiff);
            countAddPremi(subProductCode, planCode, zoneId, dayDiff, maxDay);
            countBDAPremi(subProductCode, productCode, planCode, zoneId, dayDiff, maxDayBDA);
            countAddPremiAge(maxAgeFrom, maxAgeTo);
            countLoadingPremi(loadingPct);
            countPolicyCharge(stamp, policyCharge);
            fillExchangeRate();

            return holder;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fillExchangeRate() {
        ExchangeRate exchangeRate = new Select().from(ExchangeRate.class).querySingle();
        holder.exchangeRate = exchangeRate.ExchRate;
    }

    private void countPolicyCharge(double stamp, double policyCharge) {
        try {
            holder.charge = policyCharge;
            holder.stamp = stamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void countLoadingPremi(double loadingPct) {
        try {
            double premiTotal, allocationAmount;
            premiTotal = holder.premiAdd + holder.premiBDA + holder.premiBasic;
            if (holder.loadingUmur) {
                holder.premiLoading = premiTotal * loadingPct / 100;
                holder.premiAllocation = holder.premiAllocation + (holder.premiAllocation * loadingPct / 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void countBasicPremi(String subProductCode, String planCode, String zoneId, int dayDiff) {
        try {
            double premiumAmount, allocationAmount;

            dayDiff = dayDiff > 31 ? 31 : dayDiff;

            SubProductPlanBasic subProductPlanBasic = SubProductPlanBasic.get(subProductCode, planCode, zoneId, dayDiff);

            if (subProductPlanBasic != null) {
                premiumAmount = Double.parseDouble(subProductPlanBasic.PremiumAmount);
                allocationAmount = Double.parseDouble(subProductPlanBasic.AllocationAmount) ;
            } else {
                premiumAmount = 0.0;
                allocationAmount = 0.0;
            }

            holder.premiBasic = premiumAmount;
            holder.premiAllocation += allocationAmount;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void countAddPremi(String subProductCode, String planCode, String zoneId, int dayDiff, int maxDay) {
        try {

            if (dayDiff <= maxDay)
                return;

            int week = (int) Math.ceil((dayDiff - maxDay) / 7.0);

            SubProductPlanAdd subProductPlanAdd = SubProductPlanAdd.get(subProductCode, planCode, zoneId);

            double premiumAmount;
            double allocationAmount;
            double premiAdd;

            /*BERUBAH */

            if (subProductPlanAdd != null) {
                premiumAmount = Double.parseDouble(subProductPlanAdd.PremiumAmount);
                allocationAmount = Double.parseDouble(subProductPlanAdd.AllocationAmount) * week;
                premiAdd = premiumAmount * week;
            } else {
                premiumAmount = 0.0;
                allocationAmount = 0.0;
                premiAdd = 0.0;
            }

            holder.premiAdd = premiAdd;
            holder.premiAllocation += allocationAmount;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void countBDAPremi(String subProductCode, String productCode, String planCode, String zoneId, int dayDiff, int maxDayBDA) {
        try {
            if (dayDiff <= maxDayBDA)
                return;

            int week = (int) Math.ceil((dayDiff - maxDayBDA) / 7.0);

            SubProductPlanBDA subProductPlanBDA = SubProductPlanBDA.get(subProductCode, planCode, zoneId);

            double premiumAmount;
            double premi;

            if (subProductPlanBDA != null) {
                premiumAmount = Double.parseDouble(subProductPlanBDA.PremiumAmount);
                premi = premiumAmount * week;
            } else {
                premiumAmount = 0.0;
                premi = 0.0;
            }

            holder.premiBDA = premi;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void countAddPremiAge(int maxAgeFrom, int maxAgeTo) {
        try {

            SppaInsured sppaInsured = new Select().from(SppaInsured.class).querySingle();

            if (sppaInsured == null)
                return;

            int age = Integer.parseInt(sppaInsured.Age);

            if (age >= maxAgeFrom && age <= maxAgeTo) {
                holder.loadingUmur = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
