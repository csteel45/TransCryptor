/*
 * CryptoFormat.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

package com.fortmoon.transcryptor;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Binary container format for TransCryptor-encrypted files (version 1).
 *
 * <p>Layout (all integers big-endian):
 * <pre>
 *   magic      4 bytes  = 'T','C','R','1'
 *   version    1 byte   = 1
 *   kdfId      1 byte   = 1  (PBKDF2WithHmacSHA256)
 *   iterations 4 bytes  (int)
 *   saltLen    1 byte,  salt (saltLen bytes)
 *   ivLen      1 byte,  iv   (ivLen bytes)
 *   ciphertext remaining bytes (AES-GCM output, including the 128-bit tag)
 * </pre>
 *
 * <p>The serialized header — everything before the ciphertext — is authenticated
 * as AES-GCM additional authenticated data (AAD), so the KDF and IV parameters
 * cannot be altered without causing decryption to fail.
 *
 * @author Christopher Steel
 */
public final class CryptoFormat {

    public static final byte[] MAGIC = {'T', 'C', 'R', '1'};
    public static final byte VERSION = 1;
    public static final byte KDF_PBKDF2_HMAC_SHA256 = 1;

    public static final int SALT_LENGTH = 16;        // 128-bit salt
    public static final int IV_LENGTH = 12;          // 96-bit GCM nonce
    public static final int GCM_TAG_BITS = 128;
    public static final int KEY_BITS = 256;          // AES-256
    public static final int DEFAULT_ITERATIONS = 210_000;

    public final int iterations;
    public final byte[] salt;
    public final byte[] iv;

    public CryptoFormat(int iterations, byte[] salt, byte[] iv) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("iterations must be positive");
        }
        this.iterations = iterations;
        this.salt = salt.clone();
        this.iv = iv.clone();
    }

    /** Serializes this header — the bytes that are prepended to the ciphertext and authenticated as AAD. */
    public byte[] toBytes() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bos);
            out.write(MAGIC);
            out.writeByte(VERSION);
            out.writeByte(KDF_PBKDF2_HMAC_SHA256);
            out.writeInt(iterations);
            out.writeByte(salt.length);
            out.write(salt);
            out.writeByte(iv.length);
            out.write(iv);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("unable to serialize header", e);
        }
    }

    /** A parsed header together with the offset at which the ciphertext begins. */
    public static final class Parsed {
        public final CryptoFormat header;
        public final int ciphertextOffset;

        Parsed(CryptoFormat header, int ciphertextOffset) {
            this.header = header;
            this.ciphertextOffset = ciphertextOffset;
        }
    }

    /**
     * Parses a header from the front of {@code blob}.
     *
     * @throws IllegalArgumentException if the magic, version, or KDF id is unrecognized,
     *                                  or the header is truncated
     */
    public static Parsed parse(byte[] blob) {
        try {
            if (blob.length < MAGIC.length
                    || blob[0] != 'T' || blob[1] != 'C' || blob[2] != 'R' || blob[3] != '1') {
                throw new IllegalArgumentException("not a TransCryptor file (bad magic)");
            }
            int p = MAGIC.length;
            byte version = blob[p++];
            if (version != VERSION) {
                throw new IllegalArgumentException("unsupported format version: " + version);
            }
            byte kdf = blob[p++];
            if (kdf != KDF_PBKDF2_HMAC_SHA256) {
                throw new IllegalArgumentException("unsupported KDF id: " + kdf);
            }
            int iterations = ((blob[p] & 0xff) << 24) | ((blob[p + 1] & 0xff) << 16)
                    | ((blob[p + 2] & 0xff) << 8) | (blob[p + 3] & 0xff);
            p += 4;
            int saltLen = blob[p++] & 0xff;
            byte[] salt = Arrays.copyOfRange(blob, p, p + saltLen);
            p += saltLen;
            int ivLen = blob[p++] & 0xff;
            byte[] iv = Arrays.copyOfRange(blob, p, p + ivLen);
            p += ivLen;
            return new Parsed(new CryptoFormat(iterations, salt, iv), p);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("truncated or corrupt header", e);
        }
    }
}
