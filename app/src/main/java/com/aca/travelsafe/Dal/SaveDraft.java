package com.aca.travelsafe.Dal;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import com.aca.travelsafe.FillInsuredActivity;
import com.aca.travelsafe.Interface.SyncListener;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.UtilDate;
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
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

/**
 * Created by Marsel on 16/5/2016.
 */
public class SaveDraft {
    private int counter, finishCounter;

    private String sppaNo;
    private Activity activity;
    private SwipeRefreshLayout swrLayout;
    private SyncListener listener;

    public SaveDraft(String sppaNo, FillInsuredActivity activity, SwipeRefreshLayout swrLayout) {
        this.sppaNo = sppaNo;
        this.listener = activity;
        this.activity = activity;
        this.swrLayout = swrLayout;
        counter = 0;
        finishCounter = 0;

    }

    private void clearSppa(){
        Delete.table(SppaMain.class);
        Delete.table(SppaInsured.class);
        Delete.table(SppaBeneficiary.class);
        Delete.table(SppaDestination.class);
        Delete.table(SppaDomestic.class);
        Delete.table(SppaFlight.class);
        Delete.table(SppaFamily.class);
    }

    public void saveAll() {
        clearSppa();

//        saveSppaMain();
        saveSppaDestination();
        saveSppaDomestic();

        syncData();
    }

    private void syncData() {
        Observable.zip(
                getListObservable(),
                new FuncN<Object>() {
                    @Override
                    public Object call(Object... args) {
                        try {
                            List<SppaBeneficiary> sppaBeneficiaryList = (List<SppaBeneficiary>) args[0];
                            List<SppaFamily> sppaFamilyList = (List<SppaFamily>) args[1];
                            List<SppaFlight> sppaFlightList = (List<SppaFlight>) args[2];
                            List<SppaInsured> sppaInsuredList = (List<SppaInsured>) args[3];
                            List<SppaMain> sppaMain = (List<SppaMain>) args[4];


                            saveMain(sppaMain);
                            saveBene(sppaBeneficiaryList);
                            saveFam(sppaFamilyList);
                            saveFlight(sppaFlightList);
                            saveInsured(sppaInsuredList);

                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }


                    private void saveMain(List<SppaMain> sppaMainList) {
                        if (sppaMainList == null || sppaMainList.size() == 0)
                            return;


                        for (SppaMain s : sppaMainList) {
                            s.SppaDate = UtilDate.parseUTC(s.SppaDate).toString(UtilDate.ISO_DATE);
                            s.EffectiveDate = UtilDate.parseUTC(s.EffectiveDate).toString(UtilDate.ISO_DATE);
                            s.ExpireDate = UtilDate.parseUTC(s.ExpireDate).toString(UtilDate.ISO_DATE);

                            s.save();
                        }


                    }


                    private void saveInsured(List<SppaInsured> sppaInsuredList) {
                        if (sppaInsuredList == null || sppaInsuredList.size() == 0)
                            return;

                        for (SppaInsured s : sppaInsuredList) {
                            s.DateOFBirth = UtilDate.parseUTC(s.DateOFBirth).toString(UtilDate.ISO_DATE);
                            s.save();
                        }
                    }

                    private void saveFlight(List<SppaFlight> sppaFlightList) {
                        if (sppaFlightList == null || sppaFlightList.size() == 0)
                            return;

                        for (SppaFlight s : sppaFlightList) {
                            s.FlightDate = UtilDate.parseUTC(s.FlightDate).toString(UtilDate.ISO_DATE);
                            s.save();
                        }


                    }

                    private void saveFam(List<SppaFamily> sppaFamilyList) {
                        if (sppaFamilyList == null || sppaFamilyList.size() == 0)
                            return;

                        for (SppaFamily s : sppaFamilyList) {
                            s.save();
                        }
                    }

                    private void saveBene(List<SppaBeneficiary> sppaBeneficiaryList) {
                        if (sppaBeneficiaryList == null || sppaBeneficiaryList.size() == 0)
                            return;

                        for (SppaBeneficiary s : sppaBeneficiaryList) {
                            s.save();
                        }

                    }
                }
        ).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer() {
            @Override
            public void onCompleted() {
          /*      synchronized (swrLayout) {
                    swrLayout.notify();
                }*/
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                onFailed();
            }

            @Override
            public void onNext(Object o) {
                if ((Boolean)o) {
                    onFinish();
                }
                else {
                    onFailed();
                }

            }
        });
    }

    private Iterable getListObservable () {
        List<Observable> observableList = new ArrayList<>();

        observableList.add(saveSppaBene());
        observableList.add(saveSppaFamily());
        observableList.add(saveSppaFlight());
        observableList.add(saveSppaInsured());
        observableList.add(saveSppaMain());

        return observableList;
    }



    private void reset(){
        finishCounter = 0;
        counter = 0;
        listener.syncFailed("");
    }

    private void onFinish() {
        listener.syncSucceed(true);
/*
        finishCounter++;

        if (finishCounter == counter) {
            reset();

            synchronized (swrLayout) {
                swrLayout.notify();
            }
//            editSPPA();
        }*/
    }

    private void editSPPA() {
        Intent intent = new Intent(activity, FillInsuredActivity.class);
        activity.startActivity(intent);
    }

    private void onFailed () {
        reset();
    }


    public Observable<List<SppaMain>> saveSppaMain() {
        SppaMainDraft sppaMainDraft;
        SppaMain sppaMain;

        try {
            sppaMainDraft = new Select().from(SppaMainDraft.class)
                    .where(SppaMainDraft_Table.SppaNo.eq(sppaNo))
                    .querySingle();

            if (sppaMainDraft == null)
                return null;

            sppaMain = new SppaMain();
            sppaMain.SppaNo = sppaMainDraft.SppaNo;
            counter++;

            return TravelService.createSPPAService(null)
                    .sppaMain(sppaMainDraft.SppaNo)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread());

//

//            sppaMain.SppaDate = sppaMainDraft.SppaDate;
//            sppaMain.SppaStatus = sppaMainDraft.SppaStatus;
//            sppaMain.ProductCode = sppaMainDraft.ProductCode;
//            sppaMain.SubProductCode = sppaMainDraft.SubProductCode;
//            sppaMain.PlanCode = sppaMainDraft.PlanCode;
//            sppaMain.ZoneId = sppaMainDraft.ZoneId;
//            sppaMain.Name = sppaMainDraft.Name;
//            sppaMain.CurrencyCode = sppaMainDraft.CurrencyCode;
//            sppaMain.PremiumAmount = sppaMainDraft.PremiumAmount;
//            sppaMain.PremiumAdditionalAmount = sppaMainDraft.PremiumAdditionalAmount;
//            sppaMain.PremiumLoadingAmount = sppaMainDraft.PremiumLoadingAmount;
//            sppaMain.PremiumBdaAmount = sppaMainDraft.PremiumBdaAmount;
//            sppaMain.ChargeAmount = sppaMainDraft.ChargeAmount;
//            sppaMain.StampAmount = sppaMainDraft.StampAmount;
//            sppaMain.EndorsmentAmount = sppaMainDraft.EndorsmentAmount;
//            sppaMain.TotalPremiumAmount = sppaMainDraft.TotalPremiumAmount;
//            sppaMain.EffectiveDate = sppaMainDraft.EffectiveDate;
//            sppaMain.ExpireDate = sppaMainDraft.ExpireDate;
//            sppaMain.PolicyNo = sppaMainDraft.PolicyNo;
//            sppaMain.AgentCode = sppaMainDraft.AgentCode;
//            sppaMain.AgentUserCode = sppaMainDraft.AgentUserCode;
//            sppaMain.SppaSubmitDate = sppaMainDraft.SppaSubmitDate;
//            sppaMain.SppaSubmitBy = sppaMainDraft.SppaSubmitBy;
//            sppaMain.BranchCode = sppaMainDraft.BranchCode;
//            sppaMain.SubBranchCode = sppaMainDraft.SubBranchCode;
//            sppaMain.TypingBranch = sppaMainDraft.TypingBranch;
//            sppaMain.AgeUse = sppaMainDraft.AgeUse;
//            sppaMain.TotalDayTravel = sppaMainDraft.TotalDayTravel;
//            sppaMain.BasicDayTravel = sppaMainDraft.BasicDayTravel;
//            sppaMain.AddDay = sppaMainDraft.AddDay;
//            sppaMain.AddWeek = sppaMainDraft.AddWeek;
//            sppaMain.AddWeekFactor = sppaMainDraft.AddWeekFactor;
//            sppaMain.BdaDayMax = sppaMainDraft.BdaDayMax;
//            sppaMain.BdaDay = sppaMainDraft.BdaDay;
//            sppaMain.BdaWeek = sppaMainDraft.BdaWeek;
//            sppaMain.BdaWeekFactor = sppaMainDraft.BdaWeekFactor;
//            sppaMain.AgeLoadingFactor = sppaMainDraft.AgeLoadingFactor;
//            sppaMain.NoOfPersonFactor = sppaMainDraft.NoOfPersonFactor;
//            sppaMain.ExchangeRate = sppaMainDraft.ExchangeRate;
//            sppaMain.IsEndors = sppaMainDraft.IsEndors;
//            sppaMain.EndorsementTypeId = sppaMainDraft.EndorsementTypeId;
//            sppaMain.EndorsBy = sppaMainDraft.EndorsBy;
//            sppaMain.EndorsDate = sppaMainDraft.EndorsDate;
//            sppaMain.IsFromEndorsment = sppaMainDraft.IsFromEndorsment;
//            sppaMain.IsTransferAS400 = sppaMainDraft.IsTransferAS400;
//            sppaMain.TransferDate = sppaMainDraft.TransferDate;
//            sppaMain.CommisionRate = sppaMainDraft.CommisionRate;
//            sppaMain.Commision = sppaMainDraft.Commision;
//            sppaMain.AreaCodeAs400 = sppaMainDraft.AreaCodeAs400;
//            sppaMain.CoverageCodeAs400 = sppaMainDraft.CoverageCodeAs400;
//            sppaMain.DurationCodeAs400 = sppaMainDraft.DurationCodeAs400;
//            sppaMain.AllocationAmount = sppaMainDraft.AllocationAmount;
//            sppaMain.CreateBy = sppaMainDraft.CreateBy;
//            sppaMain.CreateDate = sppaMainDraft.CreateDate;
//            sppaMain.ModifyBy = sppaMainDraft.ModifyBy;
//            sppaMain.ModifyDate = sppaMainDraft.ModifyDate;
//            sppaMain.IsActive = sppaMainDraft.IsActive;
//            sppaMain.save();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveSppaDomestic() {
        List<SppaDomesticDraft> sppaDomesticDraft = null;
        SppaDomestic sppaDomestic;

        try {
            sppaDomesticDraft = new Select().from(SppaDomesticDraft.class)
                    .where(SppaDomesticDraft_Table.SppaNo.eq(sppaNo))
                    .queryList();

            if (sppaDomesticDraft == null || sppaDomesticDraft.size() == 0)
                return;

            for (SppaDomesticDraft s : sppaDomesticDraft) {
                sppaDomestic = new SppaDomestic();

                sppaDomestic.SppaNo = s.SppaNo;
                sppaDomestic.CityId = s.CityId;
                sppaDomestic.CreateBy = s.CreateBy;
                sppaDomestic.CreateDate = s.CreateDate;
                sppaDomestic.ModifyBy = s.ModifyBy;
                sppaDomestic.ModifyDate = s.ModifyDate;
                sppaDomestic.IsActive = s.IsActive;

                sppaDomestic.save();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSppaDestination() {
        List<SppaDestinationDraft> sppaDestinationDraftList = null;
        SppaDestination sppaDestination;

        try {
            sppaDestinationDraftList = new Select().from(SppaDestinationDraft.class)
                    .where(SppaDestinationDraft_Table.SppaNo.eq(sppaNo))
                    .queryList();

            if (sppaDestinationDraftList == null || sppaDestinationDraftList.size() == 0)
                return;

            for (SppaDestinationDraft s : sppaDestinationDraftList) {
                sppaDestination = new SppaDestination();


                sppaDestination.SppaNo = s.SppaNo;
                sppaDestination.CountryId = s.CountryId;
                sppaDestination.CreateBy = s.CreateBy;
                sppaDestination.CreateDate = s.CreateDate;
                sppaDestination.ModifyBy = s.ModifyBy;
                sppaDestination.ModifyDate = s.ModifyDate;
                sppaDestination.IsActive = s.IsActive;

                sppaDestination.save();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Observable<List<SppaInsured>> saveSppaInsured() {
        counter++;

        return
                TravelService.createSPPAService(null)
                        .sppaInsured(sppaNo)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread());
        /*
        TravelService.createSPPAService(null)
                .sppaInsured(sppaNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<List<SppaInsured>>() {
                    @Override
                    public void onCompleted() {
                        onFinish();

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onFailed();
                    }

                    @Override
                    public void onNext(List<SppaInsured> sppaInsureds) {
                        if (sppaInsureds == null || sppaInsureds.size() == 0)
                            return;

                        for (SppaInsured s : sppaInsureds) {
                            s.DateOFBirth = UtilDate.parseUTC(s.DateOFBirth).toString(UtilDate.ISO_DATE);
                            s.save();
                        }

                    }
                });*/
    }


    public Observable<List<SppaBeneficiary>> saveSppaBene() {
        counter++;

        return
                TravelService.createSPPAService(null)
                        .sppaBeneficiary(sppaNo)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread());
/*
        TravelService.createSPPAService(null)
                .sppaBeneficiary(sppaNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<List<SppaBeneficiary>>() {
                    @Override
                    public void onCompleted() {
                        onFinish();

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onFailed();
                    }

                    @Override
                    public void onNext(List<SppaBeneficiary> sppaBeneficiaries) {
                        if (sppaBeneficiaries == null || sppaBeneficiaries.size() == 0)
                            return;

                        for (SppaBeneficiary s : sppaBeneficiaries) {
                            s.save();
                        }

                    }
                });*/
    }


    public Observable<List<SppaFamily>> saveSppaFamily() {
        counter++;

        return    TravelService.createSPPAService(null)
                .sppaFamily(sppaNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
        /*
        TravelService.createSPPAService(null)
                .sppaFamily(sppaNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<List<SppaFamily>>() {
                    @Override
                    public void onCompleted() {
                        onFinish();

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onFailed();
                    }

                    @Override
                    public void onNext(List<SppaFamily> sppaFamilies) {
                        if (sppaFamilies == null || sppaFamilies.size() == 0)
                            return;

                        for (SppaFamily s : sppaFamilies) {
                            s.save();
                        }

                    }
                });*/
    }


    public Observable<List<SppaFlight>> saveSppaFlight() {
        counter++;
        return   TravelService.createSPPAService(null)
                .sppaFlight(sppaNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
                /*
        TravelService.createSPPAService(null)
                .sppaFlight(sppaNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<List<SppaFlight>>() {
                    @Override
                    public void onCompleted() {
                        onFinish();

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onFailed();
                    }

                    @Override
                    public void onNext(List<SppaFlight> sppaFlights) {
                        if (sppaFlights == null || sppaFlights.size() == 0)
                            return;

                        for (SppaFlight s : sppaFlights) {
                            s.FlightDate = UtilDate.parseUTC(s.FlightDate).toString(UtilDate.ISO_DATE);
                            s.save();
                        }

                    }
                });*/
    }

}
