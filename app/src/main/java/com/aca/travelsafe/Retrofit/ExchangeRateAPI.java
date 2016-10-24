package com.aca.travelsafe.Retrofit;

import com.aca.travelsafe.database.City;
import com.aca.travelsafe.database.Country;
import com.aca.travelsafe.database.ExchangeRate;
import com.aca.travelsafe.database.ExchangeRateBDA;
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

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import rx.Observable;

/**
 * Created by Marsel on 6/4/2016.
 */
public interface ExchangeRateAPI {
    @GET("exchangeRate")
    Observable<Double> get();
}
