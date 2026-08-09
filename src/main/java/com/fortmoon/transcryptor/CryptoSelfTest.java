/*
 * CryptoSelfTest.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

package com.fortmoon.transcryptor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Standalone verification of the {@link Crypto} engine — no test framework required.
 *
 * <p>Run with {@code java CryptoSelfTest}; exits 0 if every check passes, 1 otherwise.
 * A JUnit 5 version is tracked by issue #16.
 *
 * @author Christopher Steel
 */
public final class CryptoSelfTest {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        byte[] data = new byte[8192];
        new SecureRandom().nextBytes(data);
        char[] password = "correct horse battery staple".toCharArray();

        byte[] blob = Crypto.encrypt(data, password.clone());
        byte[] recovered = Crypto.decrypt(blob, password.clone());

        check("round-trip recovers the original bytes", Arrays.equals(data, recovered));
        check("ciphertext is not the plaintext",
                !Arrays.equals(data, Arrays.copyOfRange(blob, blob.length - data.length, blob.length)));
        check("blob carries the TCR1 magic header", blob[0] == 'T' && blob[3] == '1');
        check("wrong password is rejected", rejects(blob, "wrong password".toCharArray()));

        byte[] tamperedCiphertext = blob.clone();
        tamperedCiphertext[tamperedCiphertext.length - 1] ^= 0x01;
        check("tampered ciphertext is rejected", rejects(tamperedCiphertext, password.clone()));

        byte[] tamperedHeader = blob.clone();
        tamperedHeader[7] ^= 0x01; // flip a byte inside the iterations field (authenticated as AAD)
        check("tampered header is rejected", rejects(tamperedHeader, password.clone()));

        check("empty input round-trips",
                Arrays.equals(new byte[0], Crypto.decrypt(Crypto.encrypt(new byte[0], password.clone()), password.clone())));

        // File round-trip
        Path dir = Files.createTempDirectory("tc-selftest");
        Path in = dir.resolve("plain.bin");
        Path enc = dir.resolve("plain" + CryptoFiles.ENCRYPTED_EXTENSION);
        Path dec = dir.resolve("plain.out");
        try {
            Files.write(in, data);
            CryptoFiles.encryptFile(in, enc, password.clone());
            CryptoFiles.decryptFile(enc, dec, password.clone());
            check("file round-trip recovers the original", Arrays.equals(Files.readAllBytes(in), Files.readAllBytes(dec)));
        } finally {
            Files.deleteIfExists(in);
            Files.deleteIfExists(enc);
            Files.deleteIfExists(dec);
            Files.deleteIfExists(dir);
        }

        System.out.println(failures == 0 ? "\nALL CHECKS PASSED" : "\n" + failures + " CHECK(S) FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }

    private static boolean rejects(byte[] blob, char[] password) {
        try {
            Crypto.decrypt(blob, password);
            return false;
        } catch (GeneralSecurityException expected) {
            return true;
        }
    }

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "  [PASS] " : "  [FAIL] ") + name);
        if (!ok) {
            failures++;
        }
    }
}
