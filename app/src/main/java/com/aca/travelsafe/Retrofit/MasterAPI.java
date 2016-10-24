package com.aca.travelsafe.Retrofit;

import com.aca.travelsafe.database.City;
import com.aca.travelsafe.database.Country;
import com.aca.travelsafe.database.ExchangeRate;
import com.aca.travelsafe.database.ExchangeRateBDA;
import com.aca.travelsafe.database.FamilyRelation;
import com.aca.travelsafe.database.Product;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.StandardField;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProductPlan;
import com.aca.travelsafe.database.SubProductPlanAdd;
import com.aca.travelsafe.database.SubProductPlanBDA;
import com.aca.travelsafe.database.SubProductPlanBasic;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.aca.travelsafe.database.VersionFunction;
import com.aca.travelsafe.database.VersionModel;
import com.aca.travelsafe.database.Zone;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Marsel on 6/4/2016.
 */
public interface MasterAPI {
    @GET("city")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<City>> City();

    @GET("country")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<Country>> Country();

    @GET("ExchangeRate")
    Observable<List<ExchangeRate>> ExchangeRate();

    @GET("ExchangeRateBDA")
    Observable<List<ExchangeRateBDA>> ExchangeRateBDA();

    @GET("FamilyRelation")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<FamilyRelation>> FamilyRelation();

    @GET("Product")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<Product>> Product();

    @GET("Setvar")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<Setvar>> Setvar();

    @GET("StandardField")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<StandardField>> StandardField();

    @GET("SubProduct")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SubProduct>> SubProduct();

    @GET("SubProductPlan")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SubProductPlan>> SubProductPlan();

    @GET("SubProductPlanAdd")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SubProductPlanAdd>> SubProductPlanAdd();

    @GET("SubProductPlanBasic")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SubProductPlanBasic>> SubProductPlanBasic();

    @GET("SubProductPlanBDA")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SubProductPlanBDA>> SubProductPlanBDA();

    @GET("VersionFunction")
    Observable<List<VersionFunction>> VersionFunction();

    @GET("VersionMobile")
    Observable<List<VersionModel>> VersionMobile();

    @GET("Zone")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<Zone>> Zone();



}
