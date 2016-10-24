package com.aca.travelsafe.Util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.aca.travelsafe.BuildConfig;
import com.aca.travelsafe.database.VersionModel;
import com.aca.travelsafe.R;

import org.joda.time.LocalDateTime;

import java.util.List;

/**
 * Created by Marsel on 6/4/2016.
 */
public class UtilVersion {

    public static boolean verifyVersion(Context context, List<VersionModel> versions) {
        try {
            VersionModel v = versions.get(0);

            LocalDateTime dateTime = UtilDate.parseUTC(v.DateTimeUpdate);
            boolean forceUpgrade = Boolean.parseBoolean(v.ForceUpgrade);
            int newVersion = Integer.parseInt(v.Version);

            int currentVersion = BuildConfig.VERSION_CODE;
            LocalDateTime now = UtilDate.getDateTime();

            if (Boolean.parseBoolean(v.Maintenance)) {
                popupMaintenance(context);
                return false;
            }

            if (currentVersion < newVersion) {
                if (now.isAfter(dateTime)) {
                    if (forceUpgrade) {
                        popupUpdate(context);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void popupUpdate(final Context context) {
        try {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(context.getString(R.string.new_update));
            builder.setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String appPackageName = context.getPackageName();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    ((Activity) context).finish();
                }
            });
            builder.setCancelable(false);
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void popupMaintenance(final Context context) {
        try {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(context.getString(R.string.maintenance));
            builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ((Activity) context).finish();
                }
            });
            builder.setCancelable(false);
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
