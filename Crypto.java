/*
 * Crypto.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Authenticated, password-based encryption for TransCryptor.
 *
 * <p>Each call derives a fresh AES-256 key from the password with PBKDF2-HMAC-SHA256
 * over a random salt, then encrypts with AES-256-GCM under a random 96-bit IV. The
 * {@link CryptoFormat} header (salt, IV, and KDF parameters) is prepended to the
 * ciphertext and authenticated as additional data, so tampering with either the
 * header or the ciphertext — or using the wrong password — makes decryption fail
 * closed with a {@link javax.crypto.AEADBadTagException}.
 *
 * @author Christopher Steel
 */
public final class Crypto {

    private static final String CIPHER = "AES/GCM/NoPadding";
    private static final String KDF = "PBKDF2WithHmacSHA256";
    private static final SecureRandom RNG = new SecureRandom();

    private Crypto() {
    }

    /**
     * Encrypts {@code plaintext} under {@code password}.
     *
     * @return a self-describing blob: {@link CryptoFormat} header followed by the AES-GCM ciphertext
     */
    public static byte[] encrypt(byte[] plaintext, char[] password) throws GeneralSecurityException {
        byte[] salt = new byte[CryptoFormat.SALT_LENGTH];
        byte[] iv = new byte[CryptoFormat.IV_LENGTH];
        RNG.nextBytes(salt);
        RNG.nextBytes(iv);

        CryptoFormat header = new CryptoFormat(CryptoFormat.DEFAULT_ITERATIONS, salt, iv);
        byte[] headerBytes = header.toBytes();
        byte[] keyBytes = deriveKey(password, salt, header.iterations);
        try {
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(CryptoFormat.GCM_TAG_BITS, iv));
            cipher.updateAAD(headerBytes);
            byte[] ciphertext = cipher.doFinal(plaintext);

            byte[] out = Arrays.copyOf(headerBytes, headerBytes.length + ciphertext.length);
            System.arraycopy(ciphertext, 0, out, headerBytes.length, ciphertext.length);
            return out;
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
        }
    }

    /**
     * Decrypts a blob produced by {@link #encrypt}.
     *
     * @throws javax.crypto.AEADBadTagException if the password is wrong or the data was modified
     * @throws IllegalArgumentException         if the header is not a recognized TransCryptor format
     */
    public static byte[] decrypt(byte[] blob, char[] password) throws GeneralSecurityException {
        CryptoFormat.Parsed parsed = CryptoFormat.parse(blob);
        byte[] headerBytes = Arrays.copyOfRange(blob, 0, parsed.ciphertextOffset);
        byte[] ciphertext = Arrays.copyOfRange(blob, parsed.ciphertextOffset, blob.length);
        byte[] keyBytes = deriveKey(password, parsed.header.salt, parsed.header.iterations);
        try {
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(CryptoFormat.GCM_TAG_BITS, parsed.header.iv));
            cipher.updateAAD(headerBytes);
            return cipher.doFinal(ciphertext);
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
        }
    }

    private static byte[] deriveKey(char[] password, byte[] salt, int iterations) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, CryptoFormat.KEY_BITS);
        try {
            return SecretKeyFactory.getInstance(KDF).generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
