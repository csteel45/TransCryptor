/*
 * TransCryptorCli.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

import java.io.Console;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.AEADBadTagException;

/**
 * Headless command-line front end for the encryption engine.
 *
 * <pre>
 *   transcryptor encrypt &lt;in&gt; [out]
 *   transcryptor decrypt &lt;in&gt; [out]
 * </pre>
 *
 * <p>The passphrase is read interactively from the console. When no console is
 * attached (scripts, CI), it falls back to the {@code TRANSCRYPTOR_PASSWORD}
 * environment variable — never a command-line flag, which would leak into shell
 * history and process listings.
 *
 * <p>Unifying this with the GUI launcher is tracked by issue #6.
 *
 * @author Christopher Steel
 */
public final class TransCryptorCli {

    private TransCryptorCli() {
    }

    public static void main(String[] args) {
        int code = run(args);
        if (code != 0) {
            System.exit(code);
        }
    }

    /** Runs one command and returns a process exit code (0 ok, 1 error, 2 usage). */
    static int run(String[] args) {
        if (args.length < 2) {
            usage();
            return 2;
        }
        String command = args[0];
        Path in = Paths.get(args[1]);
        try {
            switch (command) {
                case "encrypt": {
                    Path out = args.length >= 3 ? Paths.get(args[2])
                            : Paths.get(args[1] + CryptoFiles.ENCRYPTED_EXTENSION);
                    char[] password = readPassword("Password: ");
                    try {
                        CryptoFiles.encryptFile(in, out, password);
                    } finally {
                        Arrays.fill(password, '\0');
                    }
                    System.out.println("Encrypted -> " + out);
                    return 0;
                }
                case "decrypt": {
                    Path out = args.length >= 3 ? Paths.get(args[2]) : defaultDecryptOutput(in);
                    char[] password = readPassword("Password: ");
                    try {
                        CryptoFiles.decryptFile(in, out, password);
                    } finally {
                        Arrays.fill(password, '\0');
                    }
                    System.out.println("Decrypted -> " + out);
                    return 0;
                }
                default:
                    usage();
                    return 2;
            }
        } catch (AEADBadTagException e) {
            System.err.println("error: wrong password, or the file has been tampered with");
            return 1;
        } catch (NoSuchFileException e) {
            System.err.println("error: file not found: " + e.getFile());
            return 1;
        } catch (GeneralSecurityException | IOException | RuntimeException e) {
            System.err.println("error: " + e.getMessage());
            return 1;
        }
    }

    private static Path defaultDecryptOutput(Path in) {
        String s = in.toString();
        if (s.endsWith(CryptoFiles.ENCRYPTED_EXTENSION)) {
            return Paths.get(s.substring(0, s.length() - CryptoFiles.ENCRYPTED_EXTENSION.length()));
        }
        return Paths.get(s + ".dec");
    }

    private static char[] readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            return console.readPassword(prompt);
        }
        String fromEnv = System.getenv("TRANSCRYPTOR_PASSWORD");
        if (fromEnv != null && !fromEnv.isEmpty()) {
            return fromEnv.toCharArray();
        }
        throw new IllegalStateException(
                "no console available; set TRANSCRYPTOR_PASSWORD to supply the passphrase non-interactively");
    }

    private static void usage() {
        System.err.println("Usage: transcryptor <encrypt|decrypt> <in> [out]");
        System.err.println("  The passphrase is read from the console, or from $TRANSCRYPTOR_PASSWORD");
        System.err.println("  when no console is attached.");
    }
}
