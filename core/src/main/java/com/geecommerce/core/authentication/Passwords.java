package com.geecommerce.core.authentication;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Passwords {
    private static final char[] RANDOM_LCASE_LETTERS = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static final char[] RANDOM_UCASE_LETTERS = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private static final char[] RANDOM_NUMBERS = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
    private static final char[] RANDOM_SPECIAL_CHARS = new char[] { '!', '%', '/', '#', '+', '_', '-' };

    private static final List<char[]> randomCharArrays = new ArrayList<>();
    static {
        randomCharArrays.add(RANDOM_LCASE_LETTERS);
        randomCharArrays.add(RANDOM_UCASE_LETTERS);
        randomCharArrays.add(RANDOM_NUMBERS);
        randomCharArrays.add(RANDOM_SPECIAL_CHARS);
    }

    public static final boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

        return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
    }

    public static final byte[] getEncryptedPassword(String password, byte[] salt)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithm = "PBKDF2WithHmacSHA1";

        int derivedKeyLength = 160;
        int iterations = 20000;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        return f.generateSecret(spec).getEncoded();
    }

    public static final byte[] getRandomSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        return salt;
    }

    public static final byte[] merge(byte[] one, byte[] two) {
        byte[] combined = new byte[one.length + two.length];

        System.arraycopy(one, 0, combined, 0, one.length);
        System.arraycopy(two, 0, combined, one.length, two.length);

        return combined;
    }

    public static String random() {
        return random(10);
    }

    public static String random(int length) {
        StringBuilder randomPassword = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int idx = i < randomCharArrays.size() ? i : (int) (Math.random() * randomCharArrays.size());
            char[] chars = randomCharArrays.get(idx);

            int idx2 = (int) (Math.random() * chars.length);
            randomPassword.append(chars[idx2]);
        }

        return randomPassword.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] merchant_sugar = "%f&h!./fghzu46uzf-'+".getBytes();
        byte[] salt = getRandomSalt();
        byte[] secret = merge(salt, merchant_sugar);

        byte[] password = getEncryptedPassword("mypassword", secret);

        boolean auth = authenticate("mypasswordx", password, secret);

        System.out.println("AUTH:: " + auth);
    }
}