package com.aca.travelsafe.Util;

import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.aca.travelsafe.Holder.DateHolder;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Marsel on 4/21/2015.
 */
public class UtilDate {
    public final static String BASIC_DATE = "dd-MM-yyyy";
    public final static String BASIC_MON_DATE = "dd MMM yyyy";
    public final static String SIMPLE_DATE = "dd MMM";

    public final static String ISO_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public final static String ISO_DATE = "yyyy-MM-dd";
    public final static String UTC_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";

    public final static String BASIC_TIME = "HH:mm";


    public static void setAutoDateTime(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.AUTO_TIME, 1);

    }

    public static DateTimeFormatter displayDate () {
        DateTimeFormatter oldF = DateTimeFormat.forPattern(BASIC_DATE);
        return oldF;
    }

    public static DateTimeFormatter displayDateWithMonth () {
        DateTimeFormatter oldF = DateTimeFormat.forPattern(BASIC_MON_DATE);
        return oldF;
    }


    public static int dayDiff(@NonNull LocalDate fromDate, @NonNull LocalDate toDate)
    {
        try {
            return Days.daysBetween(fromDate, toDate).getDays();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int monthDiff(@NonNull LocalDate firstDate, @NonNull LocalDate secondDate) {
        /**
         * secondate - firstdate
         * */
        return Months.monthsBetween(firstDate.withDayOfMonth(1), secondDate.withDayOfMonth(1)).getMonths();
    }

    public static int monthDiffInPeriode(@NonNull LocalDate firstDate, @NonNull LocalDate secondDate) {
        return Months.monthsBetween(firstDate, secondDate).getMonths();
    }

    public static int yearDiff(@NonNull LocalDate firstDate, @NonNull LocalDate secondDate) {
        return Years.yearsBetween(firstDate.withDayOfYear(1), secondDate.withDayOfYear(1)).getYears();
    }
    public static int yearDiffInPeriode(@NonNull LocalDate firstDate, @NonNull LocalDate secondDate) {
        return Years.yearsBetween(firstDate, secondDate).getYears();
    }


    public static Calendar addDay (LocalDate date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.toDate());
        calendar.add(Calendar.DAY_OF_YEAR, day);

        return calendar;
    }
    public static Calendar addMonth (LocalDate date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.toDate());
        calendar.add(Calendar.MONTH, month);

        return calendar;
    }

    public static Calendar addYear (LocalDate date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.toDate());
        calendar.add(Calendar.YEAR, year);

        return calendar;
    }

    public static LocalDate getDate() {
        LocalDate localDate  = LocalDate.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+7")));
        return localDate;
    }

    public static LocalDate getDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    public static LocalTime getTime() {
        LocalTime localTime = LocalTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+7")));
        return localTime;
    }

    public static LocalTime getTime(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime();
    }


    public static LocalDateTime getDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+7")));
        return localDateTime;
    }



/*
    year = date.getYear();
    month = date.getMonthOfYear() - 1;
    day = date.getDayOfMonth();
*/

    public static LocalTime  mergeTime (@NonNull int hour, @NonNull int minute) {
        try {
            LocalTime localTime = getTime();
            localTime =  localTime.withHourOfDay(hour).withMinuteOfHour(minute);

            return localTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static LocalDateTime toDateTime (@NonNull String tanggal, @NonNull String pattern){
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern)
                    .withLocale(Locale.UK);

            LocalDateTime date = formatter.parseLocalDateTime(tanggal);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**Default Pattern 01 Jan 2000**/
    public static LocalDate toDate (@NonNull String tanggal){
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(BASIC_MON_DATE); // dd-MON-yyyy;

            formatter.parseLocalDate(tanggal);
            LocalDate date = formatter.parseLocalDate(tanggal);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    public static LocalDate toDate (@NonNull Date date){
        try {
            return date == null ? null : LocalDate.fromDateFields(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static LocalDate toDate (@NonNull String tanggal, @NonNull String pattern){
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern)
                    .withLocale(Locale.UK);

            LocalDate date = formatter.parseLocalDate(tanggal);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
        time.getHourOfDay()
        time.getMinuteOfHour()
     */

    public static LocalTime toTime (@NonNull String waktu){
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(BASIC_TIME) // HH:mm
                    .withLocale(Locale.UK);

            LocalTime time = formatter.parseLocalTime(waktu);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalTime toTime (@NonNull String waktu, @NonNull String pattern){
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern)
                    .withLocale(Locale.UK);

            LocalTime time = formatter.parseLocalTime(waktu);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toString (Date date) {
        return date == null ? "" : LocalDate.fromDateFields(date).toString(BASIC_MON_DATE);
    }

    public static LocalDateTime parseUTC(@NonNull String tanggal) {
    	//DateTime dt = new DateTime(tanggal, DateTimeZone.UTC);
        DateTime dt = new DateTime(tanggal);
        LocalDateTime localDateTime = dt.toLocalDateTime();
        return localDateTime;
    }

    public static String format (@NonNull String tanggal, @NonNull String oldFormat, @NonNull String newFormat) {
        DateTimeFormatter oldF = DateTimeFormat.forPattern(oldFormat);
        DateTimeFormatter newF = DateTimeFormat.forPattern(newFormat);


        LocalDateTime oldDate = null;
        String newDate = "";

        try {
            oldDate = oldF.parseLocalDateTime(tanggal);
            newDate = oldDate.toString(newF);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return newDate;
    }

    public static String format (@NonNull String tanggal, @NonNull String newFormat) {
        DateTimeFormatter oldF = DateTimeFormat.forPattern(BASIC_MON_DATE);
        DateTimeFormatter newF = DateTimeFormat.forPattern(newFormat);

        LocalDateTime oldDate = null;
        String newDate = "";

        try {
            oldDate = oldF.parseLocalDateTime(tanggal);
            newDate = oldDate.toString(newF);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return newDate;
    }


    public static LocalDateTime formatJson (String tanggal) {
        Calendar c = Calendar.getInstance();

        tanggal = tanggal.replace("/Date(", "").replace(")/", "");
        Long timeInMillis = Long.valueOf(tanggal);
        c.setTimeInMillis(timeInMillis);

        return new LocalDateTime(timeInMillis);
    }

    public static String dateDbToDate (String tanggal) {
        return format(tanggal, ISO_DATE, BASIC_MON_DATE);
    }

    public static String toIsoDate(String tanggal) {
        return format(tanggal, BASIC_MON_DATE, ISO_DATE);
    }

    public static String dateToDateTimeDB (String tanggal) {
        return format(tanggal, BASIC_MON_DATE, ISO_DATE_TIME);
    }

    public static String dateTimeDBToDate (String tanggal) {
        return format(tanggal, ISO_DATE_TIME, BASIC_MON_DATE);
    }


    /**
     *
     * @param tanggal
     *                If Empty, use today's date
     * @return Dateholder (year, month, day)
     * */
    public static DateHolder splitDate(String tanggal) {
        DateHolder dateHolder = new DateHolder();
        LocalDate date = tanggal.isEmpty() ?  getDate() : toDate(tanggal);
        dateHolder.year = date.getYear();
        dateHolder.month = date.getMonthOfYear();
        dateHolder.day = date.getDayOfMonth();

        return dateHolder;
    }



}
