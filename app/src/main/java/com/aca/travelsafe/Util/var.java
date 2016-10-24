package com.aca.travelsafe.Util;


import com.aca.travelsafe.FillInsuredActivity;

/**
 * Created by Marsel on 26/1/2016.
 */
public class var {
    public static final int pembagiJam = 60;
    public static final int maxSleep = 10 * 1000;
    public static final int maxRow = 20;
    public static final int fontSizeTextDrawable = 20;


    public static final int TRANSITION_NONE = -1;
    public static final int TRANSITION_SLIDE = 0;
    public static final int TRANSITION_FADE = 1;
    public static final int TRANSITION_MASK = 2;
    public static final int INVALID_FLAG = -1;

    public static final int BACKSTACK = 0;
    public static final int NO_BACKSTACK = 1;

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String Citizenship = "Citizenship";
    public static final String Country = "Country";

    public static final String userCode = "MBL";
    public static final String roleID = "4";


    public static final String id = "ID";


    public static final String ANNFA = "ANNFA";
    public static final String ANNID = "ANNID";
    public static final String REGFA = "REGFA";
    public static final String REGID = "REGID";
    public static final String TRIP = "TRIP";
    public static final String TRIPA = "TRIPA";

    public static final String INT = "INT";
    public static final String DOM = "DOM";

    public static final String IDR = "IDR";
    public static final String USD = "USD";

    public static final String OPEN = "OPEN";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String CANCEL = "CANCEL";
    public static final String VOID = "VOID";


    public static final String Annual = "AnnReg^001";
    public static final String Regular = "AnnReg^002";

    public static final String Individu = "IdFa^001";
    public static final String Family = "IdFa^002";

    public static final String TertanggungUtama = "TU";


    public static final String ASIA = "1";

    public static final int MAX_ADDRESS_ALL = 90;
    public static final int MAX_ADDRESS = 30;

    public static final String SPPA_NO = "SPPA_NO";

    public static final int REQUEST_CODE_CHOOSE_COUNTRY = 100;
    public static final int REQUEST_CODE_ADD_FLIGHT_DETAIL = 200;
    public static final int REQUEST_CODE_EDIT_SPPA = 300;

    public static final int ACTIVITY_VIEW = android.R.id.content;
    public static final String SEQUENCE_NUMBER_TU = "001";


    /*Payment status*/
    public static final String SUKSES_BAYAR = "PaymentStatus^001";
    public static final String GAGAL_BAYAR = "PaymentStatus^002";
    public static final String SUKSES_TRANSFER = "PaymentStatus^003";

    /*Standard Field HD*/
    public static final String StatusPolis = "StatusPolis";

    /*set var*/
    public static final String AgeMultiplier = "AgeMultiplier";
    public static final String AgentCodeMobile = "AgentCodeMobile";
    public static final String BrCodeMobile = "BrCodeMobile";
    public static final String CountryIdUmroh = "CountryIdUmroh";
    public static final String DelaySendEmail = "DelaySendEmail";
    public static final String EmailAdmin = "EmailAdmin";
    public static final String FinishPayURL = "FinishPayURL";
    public static final String FinalPayURLBCA = "FinalPayURLBCA";
    public static final String ImgPtgDom = "ImgPtgDom";
    public static final String ImgPtgInt = "ImgPtgInt";
    public static final String KlikAcaUrl = "KlikAcaUrl";
    public static final String MaxPassLength = "MaxPassLength";
    public static final String MinPassLength = "MinPassLength";
    public static final String PromoCode = "Promo";
    public static final String PromoImage = "PromoImage";
    public static final String UrlBenefitImage = "UrlBenefitImage";
    public static final String UrlHalPtgImage = "UrlHalPtgImage";
    public static final String UrlPromoImage = "UrlPromoImage";
    public static final String ZoneAsia = "ZoneAsia";

    /*General Setting*/
    public static final String GENERAL_SETTING_SIGN_UP_TRIGGER = "SIGN_UP_TRIGGER";
    public static final String GENERAL_SETTING_CUSTOMER_FRAGMENT_USED = "CUSTOMER_FRAGMENT_USED";
    public static final String GENERAL_SETTING_IS_POLIS_ACTIVITY = "GENERAL_SETTING_IS_POLIS_ACTIVITY";
    public static final String GENERAL_SETTING_EDIT_CONFIRMATION = "GENERAL_SETTING_EDIT_CONFIRMATION";

    /*Activity*/
    public static final String SIGN_UP_TRIGGER_ACTIVITY = FillInsuredActivity.class.getName();
    public static final String CUSTOMER_INDIVIDU_FRAGMENT_USED_ACTIVITY = FillInsuredActivity.class.getName();


}
