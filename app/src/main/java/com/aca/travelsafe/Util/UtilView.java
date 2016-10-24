package com.aca.travelsafe.Util;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by marsel on 23/5/2016.
 */
public class UtilView {

    private static final String message_empty = "Cannot leave the field Blank";

    public static void setErrorNull (TextInputLayout viewLabel){
        viewLabel.setErrorEnabled(false);
        viewLabel.setError(null);
    }


    public static void setError (TextInputLayout viewLabel, String errorMessage){
        viewLabel.setErrorEnabled(!errorMessage.isEmpty() ? true :false);
        viewLabel.setError(errorMessage.isEmpty() ? null : errorMessage);
    }



    public static boolean isEmpty (EditText view, TextInputLayout viewLabel){
        if (view.getVisibility() == view.GONE)
            return false;

        if (TextUtils.isEmpty(view.getText().toString())) {
            viewLabel.setErrorEnabled(true);
            viewLabel.setError(message_empty);
        }
        else {
            viewLabel.setErrorEnabled(false);
            viewLabel.setError(null);
        }

        return view.getText().toString().isEmpty();
    }


    public static boolean isEmpty (EditText view){
        if (view.getVisibility() == view.GONE)
            return false;

        if(view.getText().toString().isEmpty()) {
            view.setError(message_empty);
        }
        else {
            view.setError(null);
        }

        return view.getText().toString().isEmpty();
    }

    public static boolean isEmpty (Button view){
        if (view.getVisibility() == view.GONE)
            return false;

        if(view.getText().toString().isEmpty()) {
            view.setError(message_empty);
        }
        else {
            view.setError(null);
        }

        return view.getText().toString().isEmpty();
    }


    public static TextWatcher inputTextWatcher(final TextInputLayout view) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    view.setError(null);
                    view.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }


    public static TextWatcher inputTextWatcher(final Button view) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0) {
                    view.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

}
