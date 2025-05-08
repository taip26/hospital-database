package gourd.hospitaldatabase;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static String sha256(String text) {
        try {
            var messageDigest = MessageDigest.getInstance("SHA-256");
            var hash = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));

            return String.format("%064x", new BigInteger(1, hash));
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean verifyPassword(String password, String hashedPassword) {
        String hashedInput = sha256(password);
        if (hashedInput == null) {
            return false;
        }
        return hashedInput.equals(hashedPassword); // Placeholder, replace with actual verification logic
    }
}
