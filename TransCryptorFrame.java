/*
 * TransCryptorFrame.java
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
      jButton2.setText ("jButton1");

      jPanel6.add (jButton2);

      jPanel4.add (jPanel6);

      jButton1.setText ("jButton1");
      jPanel4.add (jButton1);
      mainPanel.add (jPanel4);

      jPanel5.setLayout (new java.awt.BorderLayout ());
      jPanel5.setBorder (new javax.swing.border.EtchedBorder());

      jScrollPane1.setBorder (new javax.swing.border.LineBorder(java.awt.Color.black));
      jScrollPane1.setViewportBorder (new javax.swing.border.BevelBorder(1));
      jScrollPane1.setBackground (java.awt.Color.white);


      jScrollPane1.setViewportView (outputTextArea);

      jPanel5.add (jScrollPane1, java.awt.BorderLayout.CENTER);
      jButton3.setText ("jButton1");
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
      setJMenuBar(jMenuBar);
   }

   private void exitMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
      System.exit(0);
   }
   /** Exit the Application */
   private void exitForm(java.awt.event.WindowEvent evt) {
      System.exit (0);
   }
   /**
   * @param args the command line arguments
   */
   public static void main (String args[]) {
      new TransCryptorFrame().show();
   }

}