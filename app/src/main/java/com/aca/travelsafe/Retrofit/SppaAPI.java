package com.aca.travelsafe.Retrofit;

import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.SppaBeneficiary;
import com.aca.travelsafe.database.SppaDestination;
import com.aca.travelsafe.database.SppaDestinationDraft;
import com.aca.travelsafe.database.SppaDomestic;
import com.aca.travelsafe.database.SppaDomesticDraft;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaFlight;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SppaMainDraft;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Marsel on 6/4/2016.
 */
public interface SppaAPI {

    @GET("sppaBeneficiary/{sppaNo}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaBeneficiary>> sppaBeneficiary(@Path("sppaNo") String sppaNo);

    @GET("sppaDestination/{sppaNo}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaDestination>> sppaDestination(@Path("sppaNo") String sppaNo);

    @GET("sppaDomestic/{sppaNo}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaDomestic>> sppaDomestic(@Path("sppaNo") String sppaNo);

    @GET("sppaFamily/{sppaNo}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaFamily>> sppaFamily(@Path("sppaNo") String sppaNo );

    @GET("sppaFlight/{sppaNo}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaFlight>> sppaFlight(@Path("sppaNo") String sppaNo );

    @GET("sppaInsured/{sppaNo}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaInsured>> sppaInsured(@Path("sppaNo") String sppaNo);

    @GET("sppaMain/{sppaNo}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaMain>> sppaMain(@Path("sppaNo") String sppaNo);

    //sppa draft

    @GET("sppaMain/draft/{email}")
//    @Headers({
//            "Content-Type: application/json;charset=utf-8",
//            "Accept: application/json"
//    })
    Observable<List<SppaMainDraft>> sppaMainDraft(@Path("email") String email);

    @POST("sppaDestination/draft")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaDestinationDraft>> sppaDestinationDraft(@Body List<String> sppaNoList);

    @POST("sppaDomestic/draft")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaDomesticDraft>> sppaDomesticDraft(@Body List<String> sppaNoList);


    //sppa purchased

    @GET("sppaMain/purchased/{email}")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<SppaMainDraft>> sppaMainPurchased(@Path("email") String email);


    /**POST***/


    @POST("sppaBeneficiary")
    @Headers({
            "Content-Type: application/json;charset=utf-8",
            "Accept: application/json"
    })
    Observable<List<Result>> sppaBeneficiaryAdd(@Body List<SppaBeneficiary> sppaBeneficiary);

    @POST("sppaDestination")
    Observable<List<Result>> sppaDestinationAdd(@Body List<SppaDestination> sppaDestination);

    @POST("sppaDomestic")
    Observable<List<Result>> sppaDomesticAdd(@Body List<SppaDomestic> sppaDomestic);

    @POST("sppaFamily")
    Observable<List<Result>> sppaFamilyAdd(@Body List<SppaFamily> sppaFamily);

    @POST("sppaFlight")
    Observable<List<Result>> sppaFlightAdd(@Body List<SppaFlight> sppaFlight);

    @POST("sppaInsured")
    Observable<List<Result>> sppaInsuredAdd(@Body SppaInsured sppaInsured);

    @POST("sppaMain/submit")
    Observable<List<Result>> sppaMainAdd(@Body SppaMain sppaMain);

    /**REMOVE ***/

    @POST("sppaMain/submit")
    Observable<List<Result>> sppaMainRemove(@Body SppaMainDraft sppaMain);

}
