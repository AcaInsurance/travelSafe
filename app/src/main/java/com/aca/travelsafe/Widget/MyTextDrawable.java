package com.aca.travelsafe.Widget;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.aca.travelsafe.R;
import com.amulyakhare.textdrawable.TextDrawable;

/**
 * Created by marsel on 11/4/2016.
 */
public class MyTextDrawable {
    public static Drawable create(Activity activity, String text) {
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize((int) activity.getResources().getDimension(R.dimen.font_text_drawable))
                .endConfig()
                .buildRound(text, activity.getResources().getColor(R.color.colorPrimary));
        return drawable;
    }

}
