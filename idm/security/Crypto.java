package edu.uci.ics.binghal.service.idm.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public final class Crypto {
    private static SecureRandom secRand = new SecureRandom();
    public static final int ITERATIONS = 10000;
    public static final int KEY_LENGTH = 512;

    // PBKDF2 -- Password-based-key-derivation-function
    // HMAC -- Key-hashed Message Authentication code, used in conjunction with any cryptographic hash function.
    // SHA512 -- 512 bit member function of the SHA-2 cryptographic hash functions family designed by the NSA
    private static final String hashFunction = "PBKDF2WithHmacSHA512";

    private Crypto() { }

    public static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) {
        try {
            // Create a SecretKeyFactory
            SecretKeyFactory skf = SecretKeyFactory.getInstance(hashFunction);
            // Create a PBEKeySpec
            // PBEKeySpec is a user-chosen password that can be used with password-based encryption (PBE).
            // Iterations -- the number of times we want the PBKDF2 to execute it's underlying algorithm. The higher the
            //               the number of iterations, the safer the hashed password is.

            // Generate the secret key from the PBE spec.
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            // Retrieve the encoded password from the key and save into a byte[]
            SecretKey key = skf.generateSecret(spec);

            // Return the hashed pass
            byte[] hashedPass = key.getEncoded();

            return hashedPass;
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
        Salt must be generated using a Cryptographically Secure Pseudo-Random Number Generator (CSPRNG). CSPRNGs are
        very different from ordinary pseudo-random number generators, like C's rand() function. CSPRNGs are designed to
        be cryptographically secure, meaning they provide a high level of randomness and are completely unpredictable.
        We do not want our salts to be predictable, so we must use a CSPRNG. Java has such a CSPRNG: SecureRandom.
     */
    public static byte[] genSalt() {
        byte[] salt = new byte[4];
        secRand.nextBytes(salt);
        salt[0] = (byte)( ~(Byte.toUnsignedInt(salt[0]) >>> 2) );
        return salt;
    }

    public static String getHashedPass(byte[] hashedPassword) {
        StringBuffer buf = new StringBuffer();
        for (byte b : hashedPassword) {
            buf.append(format(Integer.toHexString(Byte.toUnsignedInt(b))));
        }
        return buf.toString();
    }

    private static String format(String binS) {
        int length = 2 - binS.length();
        char[] padArray = new char[length];
        Arrays.fill(padArray, '0');
        String padString = new String(padArray);
        return padString + binS;
    }
}
