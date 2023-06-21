package me.dimitri.project_manager.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    /*
    It was late when I did this, I followed some tutorial od md_5 hashing so yeah... I think it works pretty well,
    and it's a simple yet powerful security measurement.

    It basically works like this:

    New Registered User - > password - > gets hashed - > gets stored
    Same User Logging In - > typed password - > gets hashed - > compare the new hash with hash in db
     */

    public static String hashString(String input) {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hexString.toString();
    }

    public static boolean verifyHash(String input, String hash) {
        String hashedInput = hashString(input);
        return hashedInput.equals(hash);
    }
}
