/*
 * CryptoTest.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

package com.fortmoon.transcryptor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.AEADBadTagException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CryptoTest {

    private static char[] pw() {
        return "correct horse battery staple".toCharArray();
    }

    private static byte[] random(int n) {
        byte[] b = new byte[n];
        new SecureRandom().nextBytes(b);
        return b;
    }

    @Test
    void roundTripRecoversOriginal() throws Exception {
        byte[] data = random(8192);
        assertArrayEquals(data, Crypto.decrypt(Crypto.encrypt(data, pw()), pw()));
    }

    @Test
    void blobCarriesMagicAndHidesPlaintext() throws Exception {
        byte[] data = random(256);
        byte[] blob = Crypto.encrypt(data, pw());
        assertEquals('T', blob[0]);
        assertEquals('1', blob[3]);
        assertFalse(Arrays.equals(data, Arrays.copyOfRange(blob, blob.length - data.length, blob.length)));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        byte[] blob = Crypto.encrypt("secret".getBytes(), pw());
        assertThrows(AEADBadTagException.class, () -> Crypto.decrypt(blob, "wrong".toCharArray()));
    }

    @Test
    void tamperedCiphertextIsRejected() throws Exception {
        byte[] blob = Crypto.encrypt("secret".getBytes(), pw());
        blob[blob.length - 1] ^= 0x01;
        assertThrows(AEADBadTagException.class, () -> Crypto.decrypt(blob, pw()));
    }

    @Test
    void tamperedHeaderIsRejected() throws Exception {
        byte[] blob = Crypto.encrypt("secret".getBytes(), pw());
        blob[7] ^= 0x01; // within the authenticated iterations field
        assertThrows(AEADBadTagException.class, () -> Crypto.decrypt(blob, pw()));
    }

    @Test
    void emptyInputRoundTrips() throws Exception {
        assertArrayEquals(new byte[0], Crypto.decrypt(Crypto.encrypt(new byte[0], pw()), pw()));
    }

    @Test
    void nonTransCryptorInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> Crypto.decrypt("not a tcr file".getBytes(), pw()));
    }

    @Test
    void fileRoundTrip(@TempDir Path dir) throws Exception {
        byte[] data = random(4096);
        Path in = dir.resolve("p.bin");
        Path enc = dir.resolve("p.tcr");
        Path dec = dir.resolve("p.out");
        Files.write(in, data);
        CryptoFiles.encryptFile(in, enc, pw());
        CryptoFiles.decryptFile(enc, dec, pw());
        assertArrayEquals(Files.readAllBytes(in), Files.readAllBytes(dec));
    }

    @Test
    void inPlaceEncryptDecryptRoundTrips(@TempDir Path dir) throws Exception {
        byte[] data = random(2048);
        Path f = dir.resolve("secret.bin");
        Files.write(f, data);
        CryptoFiles.encryptFile(f, f, pw()); // encrypt in place
        assertFalse(Arrays.equals(data, Files.readAllBytes(f)));
        CryptoFiles.decryptFile(f, f, pw()); // decrypt in place
        assertArrayEquals(data, Files.readAllBytes(f));
    }
}
