package com.aca.travelsafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.UtilVersion;
import com.aca.travelsafe.database.City;
import com.aca.travelsafe.database.Country;
import com.aca.travelsafe.database.FamilyRelation;
import com.aca.travelsafe.database.Product;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.StandardField;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProductPlan;
import com.aca.travelsafe.database.SubProductPlanAdd;
import com.aca.travelsafe.database.SubProductPlanBDA;
import com.aca.travelsafe.database.SubProductPlanBasic;
import com.aca.travelsafe.database.VersionFunction;
import com.aca.travelsafe.database.VersionModel;
import com.aca.travelsafe.database.Zone;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func6;
import rx.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {
    @Bind(R.id.rootView)
    RelativeLayout rootView;
    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;

    private Subscription subscription1, subscription2;
    private int countSubs;
    private int maxCountSubs;
    private VersionFunction vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);


     /*   Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        this.finish();*/
        loadData();
    }


    private void initVar() {
        countSubs = 0;
        maxCountSubs = 2;
    }

    @Override
    protected void onDestroy() {
        if (subscription1 != null)
            subscription1.unsubscribe();

        if (subscription2 != null)
            subscription2.unsubscribe();


        super.onDestroy();
    }

    private void loadData() {
//        pbLoading.setVisibility(View.VISIBLE);
        getVersionMobile();
    }

    private void getVersionMobile() {
        initVar();
        TravelService.createMasterService(null)
                .VersionMobile()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<List<VersionModel>>() {

                            @Override
                            public void onCompleted() {

                            }


                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                retry();
                            }

                            @Override
                            public void onNext(List<VersionModel> versions) {
                                Log.d("SplashActivity", "onNext version " + versions.size());
                                if (UtilVersion.verifyVersion(SplashActivity.this, versions)) {
                                    getVersionFunction();
                                }
                            }
                        });

    }


    private void getVersionFunction() {
        TravelService.createMasterService(null)
                .VersionFunction()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VersionFunction>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        retry();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<VersionFunction> VersionFunctions) {
                        boolean update = false;

                        try {
                            vm = VersionFunctions.get(0);
                            int count = new Select().from(VersionFunction.class).query().getCount();
                            if (count == 0) {
                                update = true;

                            } else {
                                VersionFunction vers = new Select().from(VersionFunction.class).querySingle();

                                if (!vm.Version.equals(vers.Version)) {
                                    Delete.table(VersionFunction.class);
                                    update = true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (update)
                                initializing();
                            else {
                                countSubs = maxCountSubs;
                                autoLogin();
                            }
                        }
                    }
                });
    }


    private void initializing() {
        Log.d("SplashActivity", "initializing ");

        subscription1 = Observable.zip(
                TravelService.createMasterService(null).City().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).Country().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).Product().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).Setvar().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).Zone().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).FamilyRelation().subscribeOn(Schedulers.newThread()),
                new Func6<List<City>, List<Country>, List<Product>, List<Setvar>, List<Zone>, List<FamilyRelation>, Object>() {
                    @Override
                    public Object call(List<City> cities, List<Country> countries, List<Product> products, List<Setvar> setvars, List<Zone> zones, List<FamilyRelation> familyRelations) {
                        insertCity(cities);
                        insertCountry(countries);
                        insertProduct(products);
                        insertSetVar(setvars);
                        insertZone(zones);
                        insertFamilyRelation(familyRelations);

                        runSubsriber2();
                        return null;
                    }
                }
        ).subscribe(
                new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        countSubs++;
                        autoLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        retry();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });


    }

    private void runSubsriber2() {


        subscription2 = Observable.zip(
                TravelService.createMasterService(null).StandardField().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).SubProduct().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).SubProductPlan().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).SubProductPlanAdd().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).SubProductPlanBasic().subscribeOn(Schedulers.newThread()),
                TravelService.createMasterService(null).SubProductPlanBDA().subscribeOn(Schedulers.newThread()),
                new Func6<List<StandardField>, List<SubProduct>, List<SubProductPlan>, List<SubProductPlanAdd>, List<SubProductPlanBasic>, List<SubProductPlanBDA>, Object>() {
                    @Override
                    public Object call(List<StandardField> standardFields, List<SubProduct> subProducts,
                                       List<SubProductPlan> subProductPlen, List<SubProductPlanAdd> subProductPlanAdds,
                                       List<SubProductPlanBasic> subProductPlanBasics, List<SubProductPlanBDA> subProductPlanBDAs) {
                        insertStandardField(standardFields);
                        insertSubProcuct(subProducts);
                        insertSubProductPlan(subProductPlen);
                        insertSubProductPlanAdd(subProductPlanAdds);
                        insertSubProductPlanBasic(subProductPlanBasics);
                        insertSubProductPlanBDA(subProductPlanBDAs);

                        return null;
                    }
                }
        ).subscribe(
                new Observer<Object>() {

                    @Override
                    public void onCompleted() {
                        countSubs++;
                        vm.save();

                        autoLogin();

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        retry();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                }

        );

    }

    private void autoLogin() {
        if (countSubs == maxCountSubs) {
            startActivity(new Intent(this, DashboardActivity.class));
            this.finish();
        }

    }

    private void retry() {
        Snackbar.make(rootView, R.string.message_failed_load_data, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getVersionMobile();
                    }
                })
                .show();
    }


    private void insertFamilyRelation(List<FamilyRelation> familyRelations) {
        Delete.table(FamilyRelation.class);

        for (FamilyRelation fr : familyRelations) {
            fr.save();
        }
    }


    private void insertZone(List<Zone> zones) {
        Delete.table(Zone.class);

        for (Zone zone : zones) {
            zone.save();
        }
    }

    private void insertSetVar(List<Setvar> setvars) {
        Delete.table(Setvar.class);

        for (Setvar setvar : setvars) {
            setvar.save();
        }
    }

    private void insertProduct(List<Product> products) {
        Delete.table(Product.class);

        for (Product product : products) {
            product.save();
        }
    }

    private void insertCountry(List<Country> countries) {
        Delete.table(Country.class);

        for (Country country : countries) {
            country.save();
        }
    }

    private void insertCity(List<City> cities) {
        Delete.table(Country.class);

        for (City city : cities) {
            city.save();
        }
    }


    private void insertSubProductPlanBDA(List<SubProductPlanBDA> subProductPlanBDAs) {
        Delete.table(SubProductPlanBDA.class);

        for (SubProductPlanBDA spp : subProductPlanBDAs) {
            spp.save();
        }
    }


    private void insertSubProductPlanBasic(List<SubProductPlanBasic> subProductPlanBasics) {
        Delete.table(SubProductPlanBasic.class);

        for (SubProductPlanBasic spp : subProductPlanBasics) {
            spp.save();
        }
    }

    private void insertSubProductPlanAdd(List<SubProductPlanAdd> subProductPlanAdds) {
        Delete.table(SubProductPlanAdd.class);

        for (SubProductPlanAdd spp : subProductPlanAdds) {
            spp.save();
        }

    }

    private void insertSubProductPlan(List<SubProductPlan> subProductPlen) {
        Delete.table(SubProductPlan.class);

        for (SubProductPlan spp : subProductPlen) {
            spp.save();
        }
    }

    private void insertSubProcuct(List<SubProduct> subProducts) {
        Delete.table(SubProduct.class);

        for (SubProduct sp : subProducts) {
            sp.save();
        }
    }


    private void insertStandardField(List<StandardField> standardFields) {
        Delete.table(StandardField.class);

        for (StandardField standardField : standardFields) {
            standardField.save();
        }
    }


}
