/*
 * Main.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

package com.fortmoon.transcryptor;

/**
 * Single entry point for TransCryptor.
 *
 * <p>With no arguments it launches the Swing UI; with arguments it runs the
 * command-line interface. This replaces the earlier separate {@code main} methods.
 *
 * @author Christopher Steel
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            TransCryptorFrame.main(args); // launches the Swing UI on the EDT
        } else {
            int code = TransCryptorCli.run(args);
            if (code != 0) {
                System.exit(code);
            }
        }
    }
}
