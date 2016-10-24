package com.aca.travelsafe.Retrofit;

import com.aca.travelsafe.database.City;
import com.aca.travelsafe.database.Country;
import com.aca.travelsafe.database.ExchangeRate;
import com.aca.travelsafe.database.ExchangeRateBDA;
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
import java.util.Objects;

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
public interface UserAPI {

    /**User **/

    @POST("user/login")
    Observable<Result> userLogin(@Body User user);


    @GET("user/main/{userEmail}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<User> userMain(@Path("userEmail") String userEmail);

    @GET("user/detail/{userEmail}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<UserDetail> userDetail(@Path("userEmail") String userEmail);

    @POST("user/signUp/main")
    Observable<Result> userMain(@Body User user);

    @POST("user/signUp/detail")
    Observable<Result> userDetail(@Body UserDetail detail);

    @PUT("user/update/main")
    Observable<Result> updateMain(@Body User user);

    @PUT("user/update/detail")
    Observable<Result> updateDetail(@Body UserDetail detail);

    @GET("user/email/forgot/{userEmail}")
    Observable<Result> forgotPassword(@Path("userEmail") String userEmail);

    @GET("user/activation/{userEmail}")
    Observable<Void> activation(@Path("userEmail") String userEmail);

}
