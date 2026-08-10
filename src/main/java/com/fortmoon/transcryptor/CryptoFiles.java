/*
 * CryptoFiles.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

package com.fortmoon.transcryptor;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * File-level convenience wrappers over {@link Crypto}.
 *
 * <p>Output is written crash-safely: to a temporary file in the destination directory,
 * then moved into place atomically. A failure mid-write therefore never leaves a
 * truncated result or corrupts an existing file (including in-place encryption where the
 * output path equals the input path). These read the whole file into memory, which is
 * fine for typical documents; chunked/streaming encryption is a possible follow-up.
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
            writeAtomic(out, Crypto.encrypt(plaintext, password));
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
            writeAtomic(out, plaintext);
        } finally {
            Arrays.fill(plaintext, (byte) 0);
        }
    }

    /**
     * Writes {@code data} to {@code target} crash-safely: a temp file in the same
     * directory is written first, then moved into place (atomically where supported).
     */
    private static void writeAtomic(Path target, byte[] data) throws IOException {
        Path dir = target.toAbsolutePath().getParent();
        Path tmp = Files.createTempFile(dir, ".transcryptor-", ".tmp");
        try {
            Files.write(tmp, data);
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
