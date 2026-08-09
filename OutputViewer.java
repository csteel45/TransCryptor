/*
 * OutputViewer.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

/**
 * An {@link OutputStream} that mirrors {@code System.out}/{@code System.err} into a
 * {@link JTextArea}.
 *
 * <p>Bytes are buffered per line and decoded as UTF-8 (fixing the old byte&rarr;char
 * cast that corrupted non-ASCII output), the text area is updated on the Swing Event
 * Dispatch Thread, and the retained document is capped so long runs don't grow without
 * bound.
 *
 * @author Christopher Steel
 */
public class OutputViewer extends OutputStream {

    /** Maximum characters retained in the text area; older text is trimmed. */
    private static final int MAX_CHARS = 200_000;

    private JTextArea textArea;
    private final ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream(128);

    public OutputViewer(JTextArea area) {
        setTextArea(area);
    }

    public void clear() {
        SwingUtilities.invokeLater(() -> {
            try {
                Document doc = textArea.getDocument();
                doc.remove(0, doc.getLength());
            } catch (Exception e) {
                System.err.println("OutputViewer.clear exception: " + e.getMessage());
            }
        });
    }

    @Override
    public synchronized void write(int b) {
        lineBuffer.write(b);
        if (b == '\n') {
            flushLine();
        }
    }

    @Override
    public synchronized void write(byte[] b, int offset, int length) {
        for (int i = 0; i < length; i++) {
            write(b[offset + i]);
        }
    }

    @Override
    public synchronized void flush() {
        flushLine();
    }

    private void flushLine() {
        if (lineBuffer.size() == 0) {
            return;
        }
        final String text = new String(lineBuffer.toByteArray(), StandardCharsets.UTF_8);
        lineBuffer.reset();
        SwingUtilities.invokeLater(() -> {
            textArea.append(text);
            Document doc = textArea.getDocument();
            int overflow = doc.getLength() - MAX_CHARS;
            if (overflow > 0) {
                try {
                    doc.remove(0, overflow);
                } catch (Exception ignore) {
                    // trimming is best-effort
                }
            }
        });
    }

    public final void setTextArea(JTextArea area) {
        this.textArea = area;
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setForeground(java.awt.Color.blue);
    }
}
