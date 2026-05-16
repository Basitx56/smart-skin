package util;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.regex.Pattern;

public class PasswordUtil {
    public static String hash(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    public static boolean verify(String plain, String hashed) {
        return BCrypt.verifyer().verify(plain.toCharArray(), hashed).verified;
    }

    public static boolean isStrong(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$";
        return password != null && Pattern.compile(regex).matcher(password).matches();
    }
}
