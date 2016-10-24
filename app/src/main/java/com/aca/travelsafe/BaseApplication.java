package com.aca.travelsafe;

import android.app.Application;

import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.Utility;
import com.facebook.stetho.Stetho;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;

import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by Marsel on 14/3/2016.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        LeakCanary.install(this);
        FlowManager.init(this);
        Stetho.initializeWithDefaults(this);
        EasyImage.configuration(getApplicationContext())
                .setImagesFolderName(getString(R.string.app_name))
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);

        Utility.setLocale(getApplicationContext());
        UtilDate.setAutoDateTime(this);
    }
}
