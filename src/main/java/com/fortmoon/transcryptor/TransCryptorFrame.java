/*
 * TransCryptorFrame.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

package com.fortmoon.transcryptor;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.Font;

/**
 * GUI utility for decrypting and then encrypting directories of files.
 *
 * @author Christopher Steel
 * @version $Revision: 1.2 $
 * @date $Date: 2000/08/16 05:25:40 $
 */
public class TransCryptorFrame extends javax.swing.JFrame {
   private javax.swing.JMenuBar jMenuBar;
   private javax.swing.JMenu fileMenu;
   private javax.swing.JMenuItem openMenuItem;
   private javax.swing.JMenuItem exitMenuItem;
   private javax.swing.JMenu transCryptMenu;
   private javax.swing.JMenuItem undoMenuItem;
   private javax.swing.JMenuItem transCryptMenuItem;
   private javax.swing.JMenuItem decryptMenuItem;
   private javax.swing.JMenuItem cleanMenuItem;
   private javax.swing.JMenu helpMenu;
   private javax.swing.JMenuItem gettingStartedMenuItem;
   private javax.swing.JMenuItem aboutMenuItem;
   private javax.swing.JPanel mainPanel;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JPanel jPanel6;
   private javax.swing.JButton jButton2;
   private javax.swing.JButton jButton1;
   private javax.swing.JPanel jPanel5;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JTextArea outputTextArea;
   private javax.swing.JPanel jPanel7;
   private javax.swing.JButton jButton3;
   private javax.swing.JPanel progressPanel;
   private javax.swing.JLabel progressLabel;
   private javax.swing.JProgressBar progressBar;
   private java.io.File selectedFile;
   private static final java.util.logging.Logger LOG =
         java.util.logging.Logger.getLogger(TransCryptorFrame.class.getName());

   /** Creates new form TransCryptorFrame */
   public TransCryptorFrame() {
      initComponents ();
      pack ();
      OutputViewer ov = new OutputViewer(outputTextArea);
      System.setOut( new PrintStream(ov));
   }

   /**
    * This method is called from within the constructor to
    * initialize the form.
    */
   private void initComponents () {
      jMenuBar = new javax.swing.JMenuBar ();
      fileMenu = new javax.swing.JMenu ();
      openMenuItem = new javax.swing.JMenuItem ();
      exitMenuItem = new javax.swing.JMenuItem ();
      transCryptMenu = new javax.swing.JMenu ();
      undoMenuItem = new javax.swing.JMenuItem ();
      transCryptMenuItem = new javax.swing.JMenuItem ();
      decryptMenuItem = new javax.swing.JMenuItem ();
      cleanMenuItem = new javax.swing.JMenuItem ();
      helpMenu = new javax.swing.JMenu ();
      gettingStartedMenuItem = new javax.swing.JMenuItem ();
      aboutMenuItem = new javax.swing.JMenuItem ();
      mainPanel = new javax.swing.JPanel ();
      jPanel4 = new javax.swing.JPanel ();
      jPanel6 = new javax.swing.JPanel ();
      jButton2 = new javax.swing.JButton ();
      jButton1 = new javax.swing.JButton ();
      jPanel5 = new javax.swing.JPanel ();
      jScrollPane1 = new javax.swing.JScrollPane ();
      outputTextArea = new javax.swing.JTextArea ();
      jPanel7 = new javax.swing.JPanel ();
      jButton3 = new javax.swing.JButton ();
      progressPanel = new javax.swing.JPanel ();
      progressLabel = new javax.swing.JLabel ();
      progressBar = new javax.swing.JProgressBar ();
      jMenuBar.setName ("menuBar");

      fileMenu.setLabel ("File");
      fileMenu.setName ("File");

      openMenuItem.setText ("Open");

      fileMenu.add (openMenuItem);
      exitMenuItem.setText ("Exit");
      exitMenuItem.addActionListener (new java.awt.event.ActionListener () {
                                         public void actionPerformed (java.awt.event.ActionEvent evt) {
                                            exitMenuItemActionPerformed (evt);
                                         }
                                      }
                                     );

      fileMenu.add (exitMenuItem);
      jMenuBar.add (fileMenu);
      transCryptMenu.setText ("TransCrypt");

      undoMenuItem.setLabel ("Undo");

      transCryptMenu.add (undoMenuItem);
      transCryptMenuItem.setText ("TransCrypt");

      transCryptMenu.add (transCryptMenuItem);
      decryptMenuItem.setLabel ("Decrypt");

      transCryptMenu.add (decryptMenuItem);
      cleanMenuItem.setText ("Clean");

      transCryptMenu.add (cleanMenuItem);
      jMenuBar.add (transCryptMenu);
      helpMenu.setToolTipText ("Select for help");
      helpMenu.setLabel ("Help");
      helpMenu.setHorizontalAlignment (javax.swing.SwingConstants.RIGHT);

      gettingStartedMenuItem.setActionCommand ("gettingStarted");
      gettingStartedMenuItem.setText ("Getting Started");

      helpMenu.add (gettingStartedMenuItem);
      aboutMenuItem.setText ("About");

      helpMenu.add (aboutMenuItem);
      jMenuBar.add (helpMenu);

      setTitle ("TransCryptor");
      addWindowListener (new java.awt.event.WindowAdapter () {
                            public void windowClosing (java.awt.event.WindowEvent evt) {
                               exitForm (evt);
                            }
                         }
                        );

      mainPanel.setLayout (new javax.swing.BoxLayout (mainPanel, 1));
      mainPanel.setPreferredSize (new java.awt.Dimension(500, 520));
      mainPanel.setBorder (new javax.swing.border.EtchedBorder());
      jButton2.setText ("Encrypt File...");
      jButton2.addActionListener (e -> runCrypt(true));

      jPanel6.add (jButton2);

      jPanel4.add (jPanel6);

      jButton1.setText ("Decrypt File...");
      jButton1.addActionListener (e -> runCrypt(false));
      jPanel4.add (jButton1);
      mainPanel.add (jPanel4);

      jPanel5.setLayout (new java.awt.BorderLayout ());
      jPanel5.setBorder (new javax.swing.border.EtchedBorder());

      jScrollPane1.setBorder (new javax.swing.border.LineBorder(java.awt.Color.black));
      jScrollPane1.setViewportBorder (new javax.swing.border.BevelBorder(1));
      jScrollPane1.setBackground (java.awt.Color.white);


      jScrollPane1.setViewportView (outputTextArea);

      jPanel5.add (jScrollPane1, java.awt.BorderLayout.CENTER);
      jButton3.setText ("Choose File...");
      jButton3.addActionListener (e -> chooseFile());
      jPanel7.add(jButton3);
      jPanel5.add(jPanel7, java.awt.BorderLayout.NORTH);
      mainPanel.add(jPanel5);


      getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

      progressPanel.setLayout(new javax.swing.BoxLayout (progressPanel, 0));
      progressPanel.setBorder(new javax.swing.border.EtchedBorder());

      progressLabel.setPreferredSize(new java.awt.Dimension(70, 28));
      progressLabel.setMinimumSize(new java.awt.Dimension(44, 28));
      progressLabel.setText("Progress");
      progressLabel.setHorizontalAlignment (javax.swing.SwingConstants.CENTER);

      progressPanel.add(progressLabel);

      progressBar.setPreferredSize (new java.awt.Dimension(158, 28));
      progressBar.setBorder(new javax.swing.border.BevelBorder(1));
      progressBar.setMaximumSize (new java.awt.Dimension(32767, 28));
      progressPanel.add (progressBar);

      getContentPane().add(progressPanel, java.awt.BorderLayout.SOUTH);

      openMenuItem.addActionListener (e -> chooseFile());
      transCryptMenuItem.addActionListener (e -> runCrypt(true));
      decryptMenuItem.addActionListener (e -> runCrypt(false));
      cleanMenuItem.addActionListener (e -> outputTextArea.setText(""));
      undoMenuItem.setEnabled (false);
      aboutMenuItem.addActionListener (e -> showAbout());
      gettingStartedMenuItem.addActionListener (e -> showGettingStarted());

      setJMenuBar(jMenuBar);
   }

   private void exitMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
      System.exit(0);
   }
   /** Exit the Application */
   private void exitForm(java.awt.event.WindowEvent evt) {
      System.exit (0);
   }

   private void chooseFile() {
      JFileChooser chooser = new JFileChooser(
            selectedFile != null ? selectedFile.getParentFile() : new java.io.File("."));
      chooser.setDialogTitle("Choose a file");
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
         selectedFile = chooser.getSelectedFile();
         System.out.println("Selected: " + selectedFile.getAbsolutePath());
      }
   }

   private char[] promptPassphrase(String title) {
      JPasswordField field = new JPasswordField(24);
      int choice = JOptionPane.showConfirmDialog(
            this, field, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      return choice == JOptionPane.OK_OPTION ? field.getPassword() : null;
   }

   /** Runs an encrypt or decrypt of the selected file off the EDT, with progress and error reporting. */
   private void runCrypt(final boolean encrypt) {
      if (selectedFile == null) {
         JOptionPane.showMessageDialog(this, "Choose a file first.",
               "TransCryptor", JOptionPane.INFORMATION_MESSAGE);
         return;
      }
      final char[] passphrase = promptPassphrase(encrypt ? "Passphrase to encrypt" : "Passphrase to decrypt");
      if (passphrase == null) {
         return;
      }
      final java.nio.file.Path in = selectedFile.toPath();
      final java.nio.file.Path out = encrypt
            ? in.resolveSibling(selectedFile.getName() + CryptoFiles.ENCRYPTED_EXTENSION)
            : defaultDecryptTarget(in);
      setBusy(true);
      new SwingWorker<Void, Void>() {
         private Exception failure;

         @Override protected Void doInBackground() {
            try {
               if (encrypt) {
                  CryptoFiles.encryptFile(in, out, passphrase);
               } else {
                  CryptoFiles.decryptFile(in, out, passphrase);
               }
            } catch (Exception e) {
               failure = e;
            } finally {
               java.util.Arrays.fill(passphrase, '\0');
            }
            return null;
         }

         @Override protected void done() {
            setBusy(false);
            if (failure == null) {
               System.out.println((encrypt ? "Encrypted -> " : "Decrypted -> ") + out);
            } else {
               LOG.log(java.util.logging.Level.WARNING, "TransCrypt operation failed", failure);
               String message = failure instanceof javax.crypto.AEADBadTagException
                     ? "Wrong passphrase, or the file has been tampered with."
                     : String.valueOf(failure.getMessage());
               JOptionPane.showMessageDialog(TransCryptorFrame.this, message,
                     "TransCryptor", JOptionPane.ERROR_MESSAGE);
            }
         }
      }.execute();
   }

   private static java.nio.file.Path defaultDecryptTarget(java.nio.file.Path in) {
      String s = in.toString();
      if (s.endsWith(CryptoFiles.ENCRYPTED_EXTENSION)) {
         return java.nio.file.Paths.get(s.substring(0, s.length() - CryptoFiles.ENCRYPTED_EXTENSION.length()));
      }
      return java.nio.file.Paths.get(s + ".dec");
   }

   private void setBusy(boolean busy) {
      progressBar.setIndeterminate(busy);
      progressLabel.setText(busy ? "Working..." : "Progress");
      jButton1.setEnabled(!busy);
      jButton2.setEnabled(!busy);
   }

   private void showAbout() {
      JOptionPane.showMessageDialog(this,
            "TransCryptor - file encryption (AES-256-GCM + PBKDF2).\nMIT licensed.",
            "About TransCryptor", JOptionPane.INFORMATION_MESSAGE);
   }

   private void showGettingStarted() {
      JOptionPane.showMessageDialog(this,
            "1. Choose File... to pick a file.\n"
          + "2. Encrypt File... creates a .tcr file; Decrypt File... restores it.\n"
          + "You will be prompted for a passphrase.",
            "Getting Started", JOptionPane.INFORMATION_MESSAGE);
   }
   /**
   * @param args the command line arguments
   */
   public static void main (String args[]) {
      javax.swing.SwingUtilities.invokeLater(() -> {
         TransCryptorFrame frame = new TransCryptorFrame();
         frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
         frame.setVisible(true);
      });
   }

}