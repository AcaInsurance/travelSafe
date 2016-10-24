package com.aca.travelsafe.Dal;

import com.aca.travelsafe.Holder.PolicyHolder;
import com.aca.travelsafe.Holder.PremiHolder;
import com.aca.travelsafe.Retrofit.TravelPolisService;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.City;
import com.aca.travelsafe.database.City_Table;
import com.aca.travelsafe.database.Country;
import com.aca.travelsafe.database.Country_Table;
import com.aca.travelsafe.database.ExchangeRate;
import com.aca.travelsafe.database.Product;
import com.aca.travelsafe.database.Product_Table;
import com.aca.travelsafe.database.SppaBeneficiary;
import com.aca.travelsafe.database.SppaDestination;
import com.aca.travelsafe.database.SppaDestinationDraft;
import com.aca.travelsafe.database.SppaDestinationDraft_Table;
import com.aca.travelsafe.database.SppaDomestic;
import com.aca.travelsafe.database.SppaDomesticDraft;
import com.aca.travelsafe.database.SppaDomesticDraft_Table;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaFlight;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SppaMainDraft;
import com.aca.travelsafe.database.SppaMainDraft_Table;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProductPlan;
import com.aca.travelsafe.database.SubProductPlanAdd;
import com.aca.travelsafe.database.SubProductPlanBasic;
import com.aca.travelsafe.database.SubProductPlan_Table;
import com.aca.travelsafe.database.SubProduct_Table;
import com.aca.travelsafe.database.Zone;
import com.aca.travelsafe.database.Zone_Adapter;
import com.aca.travelsafe.database.Zone_Table;
import com.raizlabs.android.dbflow.config.NaturalOrderComparator;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Marsel on 29/4/2016.
 */
public class Policy {
    private PolicyHolder holder;

    public Policy() {
        holder = new PolicyHolder();
    }

    public SppaMain fillSppaMain (PremiHolder premiHolder) {
        SppaMain sppaMainHolder = null;
        SppaMain sppaMain;
        SubProductPlan subProductPlan;
        String durationCode;
        String planCode;

        try {
            sppaMainHolder = new SppaMain();
            sppaMain = SppaMain.get();

            planCode = sppaMain.PlanCode;
            durationCode = Scalar.getDurationCode(premiHolder);
            subProductPlan = SubProductPlan.get(planCode);

            sppaMainHolder.ExchangeRate = getRate(premiHolder.currency);
            sppaMainHolder.CoverageCodeAs400 = subProductPlan.CoverageCodeAs400;
            sppaMainHolder.DurationCodeAs400 = durationCode;
            sppaMainHolder.AreaCodeAs400 = getAreaAs400Code(sppaMain.ZoneId);


/*
            sppaMainHolder.PolicyNo = null;
            sppaMainHolder.AgentCode = "";
            sppaMainHolder.AgentUserCode = "";
            sppaMainHolder.SppaSubmitDate = null;
            sppaMainHolder.SppaSubmitBy = null;
            sppaMainHolder.BranchCode = "98";
            sppaMainHolder.SubBranchCode = "98";
            sppaMainHolder.TypingBranch = "98";
            sppaMainHolder.AgeUse = null;

            sppaMainHolder.AddDay = null;
            sppaMainHolder.AddWeek = null;
            sppaMainHolder.AddWeekFactor = null;
            sppaMainHolder.BdaDayMax = null;
            sppaMainHolder.BdaDay = null;
            sppaMainHolder.BdaWeek = null;
            sppaMainHolder.BdaWeekFactor = null;
            sppaMainHolder.AgeLoadingFactor = null;
            sppaMainHolder.NoOfPersonFactor = null;


            sppaMainHolder.ExchangeRate = getRate(premiHolder.currency);
            sppaMainHolder.IsEndors = "0";
            sppaMainHolder.EndorsementTypeId = null;
            sppaMainHolder.EndorsBy = null;
            sppaMainHolder.EndorsDate = null;
            sppaMainHolder.IsFromEndorsment = "0";
            sppaMainHolder.IsTransferAS400 = "0";
            sppaMainHolder.TransferDate = null;
            sppaMainHolder.CommisionRate = "0";
            sppaMainHolder.Commision = "0";
            sppaMainHolder.AreaCodeAs400 = "0";
            sppaMainHolder.CoverageCodeAs400 = subProductPlan.CoverageCodeAs400;
            sppaMainHolder.DurationCodeAs400 = durationCode;*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  sppaMainHolder;
    }


    public PolicyHolder fillPolicy () {
        try {

            SppaMain sppaMain = SppaMain.get();
            if (sppaMain == null)
                return null;

            String subProductCode = sppaMain.SubProductCode;
            String productCode = sppaMain.ProductCode;
            String planCode = sppaMain.PlanCode;

            LocalDate effDate = LocalDate.parse (sppaMain.EffectiveDate, ISODateTimeFormat.date());
            LocalDate expDate = LocalDate.parse(sppaMain.ExpireDate, ISODateTimeFormat.date());

            int dayDiff = Scalar.getDaysPeriode();

            String periode =
                    effDate.toString(UtilDate.displayDateWithMonth())
                            + "\n"
                            + expDate.toString(UtilDate.displayDateWithMonth());


            SubProduct subProduct = SubProduct.get(subProductCode, productCode);
            Product product = Product.get(productCode);
            SubProductPlan subProductPlan = SubProductPlan.get(planCode);
            String destination = getDestination() + getDomestic();
            String zone = getZone();

            holder.coverage = product.Description;
            holder.type = subProduct.Description;
            holder.plan = subProductPlan.Description;
            holder.destination = destination;
            holder.zone = zone;
            holder.periode = periode;
            holder.days = String.valueOf(dayDiff);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return holder;
    }


    private String getDestination() {
        try {
            List<SppaDestination> sppaDestinationList = new Select().from(SppaDestination.class).queryList();
            StringBuilder sb = new StringBuilder();

            String countryName;

            for (SppaDestination sd: sppaDestinationList) {
                countryName = new Select().from(Country.class)
                        .where(Country_Table.CountryId.eq(sd.CountryId))
                        .querySingle()
                        .CountryName;

                sb.append(countryName);
                sb.append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private String getDomestic() {
        try {
            List<SppaDomestic> sppaDomesticList = new Select().from(SppaDomestic.class).queryList();
            StringBuilder sb = new StringBuilder();

            String cityName;

            for (SppaDomestic sd: sppaDomesticList) {
                cityName = new Select().from(City.class)
                        .where(City_Table.CityId.eq(sd.CityId))
                        .querySingle()
                        .CityName;

                sb.append(cityName);
                sb.append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getZone () {
        SppaMain sppaMain;
        Zone zone;
        try {
            sppaMain = new Select().from(SppaMain.class).querySingle();
            zone = new Select().from(Zone.class)
                    .where(Zone_Table.ZoneId.eq(sppaMain.ZoneId))
                    .querySingle();

            return zone.ZoneName.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
    public static String getDestinationDraft(String sppaNo) {
        StringBuilder dest = new StringBuilder();

        try {
            List<SppaDestinationDraft> destList = new Select().from(SppaDestinationDraft.class)
                    .where(SppaDestinationDraft_Table.SppaNo.eq(sppaNo))
                    .queryList();

            List<SppaDomesticDraft> domList = new Select().from(SppaDomesticDraft.class)
                    .where(SppaDomesticDraft_Table.SppaNo.in(sppaNo))
                    .queryList();

            dest.append("");

            if (destList != null && destList.size() != 0) {
                for (SppaDestinationDraft s : destList) {
                    Country country = new Select().from(Country.class).where(Country_Table.CountryId.eq(s.CountryId)).querySingle();

                    dest.append(country.CountryName);
                    dest.append("\n");
                }
            }

            if (domList != null && domList.size() != 0) {
                for (SppaDomesticDraft s : domList) {
                    City city = new Select().from(City.class).where(City_Table.CityId.eq(s.CityId)).querySingle();

                    dest.append(city.CityName);
                    dest.append("\n");
                }
            }

            if (dest.toString().isEmpty()) {
                SppaMainDraft sppaMain = new Select().from(SppaMainDraft.class)
                        .where(SppaMainDraft_Table.SppaNo.eq(sppaNo))
                        .querySingle();

                Zone zone = new Select().from(Zone.class)
                        .where(Zone_Table.ZoneId.eq(sppaMain.ZoneId))
                        .querySingle();

                dest.append(
                        (zone.ZoneName.charAt(0)+ "").toUpperCase()+
                        zone.ZoneName.toLowerCase().substring(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dest.toString();
    }



    public static void inActiveSppa(String sppaNo) {
        SppaMainDraft sppaMainDraft;

        try {
            sppaMainDraft = new Select().from(SppaMainDraft.class)
                    .where(SppaMainDraft_Table.SppaNo.eq(sppaNo))
                    .querySingle();

            if (sppaMainDraft == null)
                return;

            sppaMainDraft.IsActive = String.valueOf(false);
            sppaMainDraft.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCoverage(SppaMainDraft sppaMainDraft) {
        String coverage = "";

        try {
            Product product = Product.get(sppaMainDraft.ProductCode);
            SubProduct subProduct = SubProduct.get(sppaMainDraft.SubProductCode, sppaMainDraft.ProductCode);

            coverage = product.Description + " - " + subProduct.Description;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return coverage;
    }


    public static boolean isPaid () {
        SppaMain sppaMain = null;

        try {
            sppaMain = new Select().from(SppaMain.class).querySingle();

            if (sppaMain == null)
                return false;

            if (!sppaMain.SppaStatus.equalsIgnoreCase(var.OPEN)) {
                return true;
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    public static boolean isCancelAndVoid () {
        SppaMain sppaMain = null;

        try {
            sppaMain = new Select().from(SppaMain.class).querySingle();

            if (sppaMain == null)
                return false;

            if (sppaMain.SppaStatus.equalsIgnoreCase(var.CANCEL) ||
                sppaMain.SppaStatus.equalsIgnoreCase(var.VOID)) {
                return true;
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private String getRate(String currency) {
        if (currency.equals(var.IDR))
            return "1.00";

        ExchangeRate exchangeRate = new Select().from(ExchangeRate.class).querySingle();
        return exchangeRate.ExchRate.toString();
    }

    private String getAreaAs400Code(String zoneId) {
        Zone zone = new Select().from(Zone.class)
                .where(Zone_Table.ZoneId.eq(zoneId)).and(Zone_Table.IsActive.eq(String.valueOf(true)))
                .querySingle();

        if (zone != null)
            return zone.As400Code;

        return "";
    }


    public static  void sendEmail(String sppaNo, String userEmail) {
        TravelPolisService.sendPolis(null)
                .send(sppaNo, userEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                    }


                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        aVoid.toString();

                    }
                });
    }


}
