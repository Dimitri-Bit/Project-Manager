package me.dimitri.project_manager.util;

import java.util.regex.Pattern;

public class StringValidationUtil {

    /*
    Just a class to make validating emails, names, passwords etc. a bit easier.
     */

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Allows for cirilica
    public static boolean isValidName(String name) {
        String nameRegex = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(nameRegex);
        return pattern.matcher(name).matches();
    }

    public static boolean isValidLength(String string) {
        if (string != null) {
            return string.length() >= 3 && string.length() <= 35;
        }
        return false;
    }

    public static boolean isValidPassword(String password) {
        int minLength = 8, maxLength = 30, minLowercase = 1, minUppercase = 1, minDigits = 1, minSpecial = 1;

        if (password.length() < minLength) {
            return false;
        }

        if (password.length() > maxLength) {
            return false;
        }

        int lowercaseCount = 0;
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                lowercaseCount++;
            }
        }
        if (lowercaseCount < minLowercase) {
            return false;
        }

        int uppercaseCount = 0;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                uppercaseCount++;
            }
        }
        if (uppercaseCount < minUppercase) {
            return false;
        }

        int digitCount = 0;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digitCount++;
            }
        }
        if (digitCount < minDigits) {
            return false;
        }

        String specialChars = "!@#$%^&*()-_=+[]{}\\|;:'\",.<>/?";
        int specialCount = 0;
        for (char c : password.toCharArray()) {
            if (specialChars.indexOf(c) != -1) {
                specialCount++;
            }
        }
        return specialCount >= minSpecial;
    }
}
