/*
 * OutputViewer.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.Font;

/**
 * GUI viewer for <code>System.out</code> and <code>System.err</code> messages.
 *
 * @author Christopher Steel
 * @version $Revision: 1.2 $
 * @date $Date: 2000/08/16 05:25:40 $
 */
public class OutputViewer extends OutputStream {
   private JTextArea textArea;
   private StringBuffer strBuf;

   private OutputViewer() {
   }

   public OutputViewer(JTextArea area) {
      super();
      strBuf = new StringBuffer(80);
      setTextArea(area);
   }

   public void clear() {
      try {
         Document doc = textArea.getDocument();
         doc.remove(0, doc.getLength());
      }
      catch(Exception e) {
         System.err.println("OutputViewer.clear exception: "+ e.getMessage());
         e.printStackTrace();
      }
   }

   public void write(int b) {
      strBuf.append((char)b);
      if(b == '\n') {
         textArea.append(strBuf.toString());
         strBuf.setLength(0);
      }
   }

   public void write(byte b[], int offset, int length) {
      for(int i = 0; i < length; ++i)
         write(b[offset + i]);
   }

   public void setTextArea(JTextArea area) {
      textArea = area;
      textArea.setEditable(false);
      textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
      textArea.setForeground(java.awt.Color.blue);
   }

}


