package com.aca.travelsafe.Retrofit;

import com.aca.travelsafe.database.GcmMapping;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;

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
public interface GcmAPI {

    static final String prefix = "gcm";

    @GET(prefix  + "/token/{userId}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<GcmMapping>> retrieve(@Path("userId") String userId);

    @POST(prefix)
    Observable<GcmMapping> submit(@Body GcmMapping gcmMapping);

}
