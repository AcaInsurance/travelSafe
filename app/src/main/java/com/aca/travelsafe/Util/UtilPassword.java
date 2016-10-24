package com.aca.travelsafe.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marsel on 21/7/2016.
 */
public class UtilPassword {

    public static boolean isValidPassword(String pass){
        Pattern pattern;
        Matcher matcher;

       /*
        ^                 # start-of-string
        (?=.*[0-9])       # a digit must occur at least once
        (?=.*[a-z])       # a lower case letter must occur at least once
        (?=.*[A-Z])       # an upper case letter must occur at least once
        (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
        (?=\\S+$)         # no whitespace allowed in the entire string
        .{4,}             # anything, at least six places though
        $                 # end-of-string
        */
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(pass);

        return matcher.matches();
    }
}
