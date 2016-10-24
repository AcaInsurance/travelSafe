package com.aca.travelsafe.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aca.travelsafe.R;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

/**
 * Created by Marsel on 28/6/2016.
 */
public class RegistrationIntentService extends IntentService {


    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    private final static int MAX_ATTEMPTS = 5;
    private final static int BACKOFF_MILLI_SECONDS = 2000;

    public RegistrationIntentService() {
        super(TAG);
    }

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String FCM_TOKEN = "FCMToken";


    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken();
            Log.d(TAG, "FCM Registration Token: " + token);


            sharedPreferences.edit().putString(FCM_TOKEN, token).apply();

            // pass along this data
            sendRegistrationToServer(token);

        } catch (Exception e) {
            e.printStackTrace();

            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();

        }
    }

    private void sendRegistrationToServer(String token) {
        // send network request

        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
    }

    /*
    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]

            Random random = new Random();
            long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = null;

            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                token = instanceID.getToken(
                        getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                if (null != token && !token.isEmpty()) {
                    break;
                }

                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    break;
                }
                // increase backoff exponentially
                backoff *= 2;

                Log.i(TAG, "GCM Registration Token: " + token);

            }


            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]

            Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.



    }*/

    /**
     * Persist registration to third-party servers.
     * <p>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     *              <p>
     *              <p>
     *              Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]
}
