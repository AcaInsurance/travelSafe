package com.aca.travelsafe.Util;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Marsel on 8/13/2015.
 */
public class UtilMath {
    private static NumberFormat nf = NumberFormat.getCurrencyInstance();
    private static DecimalFormat formatter = new DecimalFormat("#,###,###.##");

    public static String toString (Double value) {
        return formatter.format(value);
    }

    private static double toDouble (@NonNull String angka) throws ParseException {
        return nf.parse(angka).doubleValue();
    }

    private static double toInt (@NonNull String angka) throws ParseException {
        return nf.parse(angka).intValue();
    }

    private static double toLong (@NonNull String angka) throws ParseException {
        return nf.parse(angka).longValue();
    }

    private static String toString (@NonNull double angka) throws ParseException {
        return nf.format(angka);
    }
    private static String toString (@NonNull int angka) throws ParseException {
        return nf.format(angka);
    }
    private static String toString (@NonNull long angka) throws ParseException {
        return nf.format(angka);
    }

}
