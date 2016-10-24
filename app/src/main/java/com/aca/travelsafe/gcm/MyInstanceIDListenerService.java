package com.aca.travelsafe.gcm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GcmMapping;
import com.aca.travelsafe.database.Login;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.Setvar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Marsel on 28/6/2016.
 */
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    public static final String FCM_TOKEN = "FCMToken";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]

    @Override
    public void onTokenRefresh() {

        try {

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

//            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//            sharedPreferences.edit().putString(FCM_TOKEN, refreshedToken)
//                    .apply();

            Log.d(TAG, "Refreshed token: " + refreshedToken);

            if (TextUtils.isEmpty(refreshedToken))
                refreshedToken = "testoken";

            saveRegistrationToken(refreshedToken);
            sendRegistrationToken();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveRegistrationToken(String token) {
        GcmMapping gcmMapping;

        try {
            gcmMapping = new Select().from(GcmMapping.class).querySingle();

            if (gcmMapping == null) {
                gcmMapping = new GcmMapping();
            }

            gcmMapping.UserId = Login.getUserID();
            gcmMapping.RegisteredToken = token;
            gcmMapping.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToken() {
        try {
            GcmMapping gcmMapping = new Select().from(GcmMapping.class).querySingle();

            TravelService.createGCMService(null)
                    .submit(gcmMapping)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(registrationTokenObserver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Observer<? super GcmMapping> registrationTokenObserver() {
        return new Observer<GcmMapping>() {
            @Override
            public void onCompleted() {

            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                doOnError();
            }

            @Override
            public void onNext(GcmMapping gcmMapping) {
                try {
                    if (gcmMapping == null)  {
                        doOnError();
                        return;
                    }


                    Delete.table(GcmMapping.class);
                    gcmMapping.save();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void doOnError(){
                Toast.makeText(getBaseContext(), R.string.messasge_failed_register_token, Toast.LENGTH_SHORT).show();
            }
        };
    }



    // [END refresh_token]
}
