package com.aca.travelsafe.Retrofit;

import com.aca.travelsafe.Util.DBFlowExclusionStrategy;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilService;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

public class TravelService {

    private TravelService() {
    }

    public static Retrofit.Builder createService(final RequestBody body)  {
        Gson gson= new GsonBuilder()
                .setLenient()
                .setExclusionStrategies(new ExclusionStrategy[]{new DBFlowExclusionStrategy()})
                .setDateFormat(UtilDate.UTC_DATE_TIME)
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(UtilService.wsURL);

        if (body == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Request newReq = request.newBuilder()
                                    //                            .addHeader("Authorization", format("token %s", githubToken))
                                    //                            .post(body)
                                    .build();
                            return chain.proceed(newReq);
                        }
                    })
                    .addNetworkInterceptor(new StethoInterceptor())
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            builder.client(client);
        }
        return builder;
    }

    public static UserAPI createUserService(final RequestBody body) {
        Retrofit.Builder builder = createService(body);
        return builder.build().create(UserAPI.class);
    }

    public static MasterAPI createMasterService(final RequestBody body) {
        Retrofit.Builder builder = createService(body);
        return builder.build().create(MasterAPI.class);
    }

    public static SppaAPI createSPPAService(final RequestBody body) {
        Retrofit.Builder builder = createService(body);
        return builder.build().create(SppaAPI.class);
    }

    public static GcmAPI createGCMService(final RequestBody body) {
        Retrofit.Builder builder = createService(body);
        return builder.build().create(GcmAPI.class);
    }
    public static PromoAPI createPromoService(final RequestBody body) {
        Retrofit.Builder builder = createService(body);
        return builder.build().create(PromoAPI.class);
    }
}
