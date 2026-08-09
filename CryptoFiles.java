/*
 * CryptoFiles.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * File-level convenience wrappers over {@link Crypto}.
 *
 * <p>These read the whole file into memory, which is fine for typical documents;
 * chunked/streaming encryption and crash-safe atomic writes are tracked separately
 * (see issues #12 and the large-file follow-up).
 *
 * @author Christopher Steel
 */
public final class CryptoFiles {

    /** Default extension for TransCryptor-encrypted files. */
    public static final String ENCRYPTED_EXTENSION = ".tcr";

    private CryptoFiles() {
    }

    /** Encrypts {@code in} under {@code password}, writing the encrypted blob to {@code out}. */
    public static void encryptFile(Path in, Path out, char[] password)
            throws IOException, GeneralSecurityException {
        byte[] plaintext = Files.readAllBytes(in);
        try {
            byte[] blob = Crypto.encrypt(plaintext, password);
            Files.write(out, blob);
        } finally {
            Arrays.fill(plaintext, (byte) 0);
        }
    }

    /** Decrypts {@code in} (a blob from {@link #encryptFile}) under {@code password}, writing plaintext to {@code out}. */
    public static void decryptFile(Path in, Path out, char[] password)
            throws IOException, GeneralSecurityException {
        byte[] blob = Files.readAllBytes(in);
        byte[] plaintext = Crypto.decrypt(blob, password);
        try {
            Files.write(out, plaintext);
        } finally {
            Arrays.fill(plaintext, (byte) 0);
        }
    }
}
