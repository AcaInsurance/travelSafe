package com.aca.travelsafe.Retrofit;

import com.aca.travelsafe.database.MasterPromo;
import com.aca.travelsafe.database.Promo;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Marsel on 6/4/2016.
 */
public interface PromoAPI {
//
//    @POST("promo/use")
//    Observable<Promo> use(@Body String  promoCode);

    @GET("promo/all")
    Observable<List<MasterPromo>> getAll();

    @GET("promo/popup")
    Observable<MasterPromo> getPopup();

    @GET("promo/{promoCode}")
    Observable<Promo> get(@Path("promoCode") String promoCode);

    @GET("promo/use/{promoCode}")
    Observable<Promo> use(@Path("promoCode") String promoCode);

    @GET("promo/remove/{promoCode}")
    Observable<Result> remove(@Path("promoCode") String promoCode);

}
