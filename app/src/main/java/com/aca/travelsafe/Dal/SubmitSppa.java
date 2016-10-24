package com.aca.travelsafe.Dal;

import android.support.v4.app.Fragment;

import com.aca.travelsafe.Fragment.FillConfirmationFragment;
import com.aca.travelsafe.Interface.SyncListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.SppaBeneficiary;
import com.aca.travelsafe.database.SppaDestination;
import com.aca.travelsafe.database.SppaDomestic;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaFamily_Table;
import com.aca.travelsafe.database.SppaFlight;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.SppaMain;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

/**
 * Created by Marsel on 11/5/2016.
 */
public class SubmitSppa {
    private Fragment context;
    private SyncListener listener;
    private Subscription subscription;


    public SubmitSppa(FillConfirmationFragment context) {
        this.context = context;
        this.listener = context;
    }


    public Subscription sendSppaMain() {
        try {
            final SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();

            subscription = TravelService
                    .createSPPAService(null)
                    .sppaMainAdd(sppaMain)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<List<Result>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            onErrorSubmit();
                        }

                        @Override
                        public void onNext(List<Result> resultList) {
                            if (resultList != null) {
                                String message = resultList.get(0).message;
                                String sppaNo = resultList.get(0).detail;


                                if (message.equalsIgnoreCase(var.TRUE)) {
                                    saveSppaNo(sppaNo);
                                    sendSppaDetail(sppaNo);
                                } else {
                                    onFailedSubmit();
                                }
                            }
                        }
                    });

            return subscription;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void saveSppaNo(String sppaNo) {
        try {
            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            sppaMain.SppaNo = sppaNo;
            sppaMain.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSppaDetail (String sppaNo) {
        Observable.zip(
                getListObservable(sppaNo),
                new FuncN<Boolean>() {
                    @Override
                    public Boolean call(Object... args) {
                        List<Result> resultList;
                        for (Object o: args) {
                            resultList = (List<Result>) o;
                            if (!Result.getMessage(resultList).equalsIgnoreCase(var.TRUE)) {
                                return false;
                            }
                            return true;
                        }
                        return false;
                    }
                }
        )
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Boolean bool) {
                if (bool){
                    onCompleteSubmit();
                }
                else {
                    onFailedSubmit();
                }
            }
        });
    }

    private Iterable<Observable<List<Result>>> getListObservable(String sppaNo) {
        List<Observable<List<Result>>> observableList = new ArrayList<>();

        Observable<List<Result>> insured = sendSppaInsured(sppaNo);
        Observable<List<Result>> beneficiary = sendSppaBeneficiary(sppaNo);
        Observable<List<Result>> destination = sendSppaDestination(sppaNo);
        Observable<List<Result>> domestic = sendSppaDomestic(sppaNo);
        Observable<List<Result>> family = sendSppaFamily(sppaNo);
        Observable<List<Result>> flight = sendSppaFlight(sppaNo);

        if (insured != null) observableList.add(insured);
        if (beneficiary != null) observableList.add(beneficiary);
        if (destination != null) observableList.add(destination);
        if (domestic != null) observableList.add(domestic);
        if (family != null) observableList.add(family);
        if (flight != null) observableList.add(flight);

        Iterable<Observable<List<Result>>> observables = observableList;
        return observables;
    }


    public Observable<List<Result>> sendSppaInsured(String sppaNo) {
        try {
            SppaInsured sppaInsured = new Select().from(SppaInsured.class).querySingle();
            if (sppaInsured == null)
                return null;

            sppaInsured.SppaNo = sppaNo;
            sppaInsured.save();

            return TravelService
                    .createSPPAService(null)
                    .sppaInsuredAdd(sppaInsured)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Observable<List<Result>> sendSppaFlight(String sppaNo) {
        try {
            List<SppaFlight> sppaFlightList = new Select().from(SppaFlight.class).queryList();

            if (sppaFlightList.size() == 0)
                return null;

            for (SppaFlight sppa: sppaFlightList) {
                sppa.SppaNo = sppaNo;
                sppa.save();
            }
            sppaFlightList = new Select().from(SppaFlight.class).queryList();

            return TravelService
                    .createSPPAService(null)
                    .sppaFlightAdd(sppaFlightList)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread());


        }
         catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Observable<List<Result>> sendSppaBeneficiary(String sppaNo) {
        try {
            List<SppaBeneficiary> sppaBeneficiaryList = new Select().from(SppaBeneficiary.class).queryList();

            if (sppaBeneficiaryList.size() == 0) {
                return null;
            }

            for (SppaBeneficiary sppa: sppaBeneficiaryList) {
                sppa.SppaNo = sppaNo;
                sppa.save();
            }
            sppaBeneficiaryList = new Select().from(SppaBeneficiary.class).queryList();

            return TravelService
                    .createSPPAService(null)
                    .sppaBeneficiaryAdd(sppaBeneficiaryList)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    public Observable<List<Result>> sendSppaDestination(String sppaNo) {
        try {
            List<SppaDestination> sppaDestinationList = new Select().from(SppaDestination.class).queryList();

            if (sppaDestinationList.size() == 0) {
                return null;
            }

            for (SppaDestination sppa: sppaDestinationList) {
                sppa.SppaNo = sppaNo;
                sppa.save();
            }
            sppaDestinationList = new Select().from(SppaDestination.class).queryList();


            return   TravelService
                    .createSPPAService(null)
                    .sppaDestinationAdd(sppaDestinationList)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Observable<List<Result>> sendSppaDomestic(String sppaNo) {
        try {
            List<SppaDomestic>  sppaDomesticList = new Select().from(SppaDomestic.class).queryList();

            if (sppaDomesticList.size() == 0) {
                return null;
            }

            for (SppaDomestic sppa: sppaDomesticList) {
                sppa.SppaNo = sppaNo;
                sppa.save();
            }
            sppaDomesticList = new Select().from(SppaDomestic.class).queryList();

            return TravelService
                    .createSPPAService(null)
                    .sppaDomesticAdd(sppaDomesticList)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public Observable<List<Result>> sendSppaFamily(String sppaNo) {
        try {
            List<SppaFamily>  sppaFamilyList = new Select().from(SppaFamily.class)
//                    .orderBy(SppaFamily_Table.SequenceNo, true)
                    .queryList();

            if (sppaFamilyList.size() == 0) {
                return null;
            }

            int index = 2;
            for (SppaFamily sppa: sppaFamilyList) {
                sppa.SppaNo = sppaNo;
                if (!sppa.FamilyCode.equalsIgnoreCase(var.TertanggungUtama)) {
                    sppa.SequenceNo = String.format("%03d", index);
                    index++;
                }
                sppa.save();
            }
            sppaFamilyList = new Select().from(SppaFamily.class)
                    .orderBy(SppaFamily_Table.SequenceNo, true)
                    .queryList();
            return TravelService
                    .createSPPAService(null)
                    .sppaFamilyAdd(sppaFamilyList)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void onErrorSubmit() {
        listener.syncFailed(context.getString(R.string.message_failed_submit_sppa));
    }

    public void onFailedSubmit() {
        listener.syncSucceed(false);
    }

    public void onCompleteSubmit () {
        try {
            listener.syncSucceed(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
