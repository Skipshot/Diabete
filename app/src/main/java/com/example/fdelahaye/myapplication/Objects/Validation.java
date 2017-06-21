package com.example.fdelahaye.myapplication.Objects;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * Created by fdelahaye on 08/06/2017.
 */

public abstract class Validation  implements TextWatcher {

    private final TextView textView;

    // Regular Expression
    // you can change the expression based on your need
    private static final String DECIMAL_REGEX = "^\\d{1,}\\.\\d{1,}$";
    private static final String NUMBER_REGEX = "^[0-9]*$";
    private static final String TIME_REGEX = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";
    /*private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{3}-\\d{7}";*/

    // Error Messages
    private static final String REQUIRED_MSG = "Champs requis";
    private static final String DECIMAL_MSG = "Champ incorrect (ex : 1.0)";
    private static final String NUMBER_MSG = "Champ incorrect (ex : 123)";
    private static final String TIME_MSG = "Champ incorrect (ex : 01:00)";
    /*private static final String EMAIL_MSG = "invalid email";
    private static final String PHONE_MSG = "###-#######";*/

    public Validation(TextView textView) {
        this.textView = textView;
    }

    public abstract void validate();

    @Override
    final public void afterTextChanged(Editable s) {
        validate();
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }


    // call this method when you need to check email validation
    /*public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
    }*/

    // call this method when you need to check decimal number validation
    public static boolean isDecimalNumber(EditText editText, boolean required) {
        return isValid(editText, DECIMAL_REGEX , DECIMAL_MSG, required);
    }

    // call this method when you need to check number validation
    public static boolean isNumber(EditText editText, boolean required) {
        return isValid(editText, NUMBER_REGEX , NUMBER_MSG, required);
    }

    // call this method when you need to check time validation
    public static boolean isTime(EditText editText, boolean required) {
        return isValid(editText, TIME_REGEX , TIME_MSG, required);
    }

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        };

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }
}
