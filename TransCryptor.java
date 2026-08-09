/*
 * TransCryptor.java
 *
 * Copyright (c) 1998-2026 Chris Steel
 * SPDX-License-Identifier: MIT
 * See the LICENSE file in the project root for the full license text.
 */

import java.io.*;
import java.util.*;
import java.security.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * @author Christopher Steel
 * @version $Revision: 1.2 $
 * @date $Date: 2000/04/02 02:26:00 $
 * @since   JDK1.1
 */
public class TransCryptor {
   private Vector fileList;
   boolean done = false;
   private static final JFrame frame = new JFrame();

//       static {
//          try {
//             javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//          }
//          catch(Exception e) {
//          }
//       }

   public TransCryptor() {
      try {
         fileList = new Vector(100);
      }
      catch(Exception e) {
         System.out.println("TransCryptor() exception: " + e);
         e.printStackTrace();
         System.exit(-1);
      }
   }

   public void go() {
      JFileChooser chooser = new JFileChooser(".");
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      chooser.setMultiSelectionEnabled(false);
      chooser.setApproveButtonText("OK");
      chooser.setApproveButtonMnemonic('O');
      chooser.setDialogTitle("TransCryptor");
      int returnVal = chooser.showDialog(frame, null);
      if(returnVal != JFileChooser.APPROVE_OPTION) {
         System.out.println("Goodbye.");
         System.exit(0);
      }
      File dir = chooser.getSelectedFile();
      System.out.println("You chose to open this file: " + dir.getName());
      ProcessDirectory(dir);
      ProgressMonitor meter = new ProgressMonitor(frame,
                                                  "Percent Complete:", null, 0, fileList.size());
      meter.setMillisToDecideToPopup(0);
      meter.setMillisToPopup(0);

      for(int i = 0; i < fileList.size(); i++) {
         System.out.println("Go: " + ((File)(fileList.elementAt(i))).getName());
         processFile((File)fileList.elementAt(i));
         meter.setProgress(i);
      }
   }

   public void ProcessDirectory(File dir) {
      System.out.println("Entering directory: " + dir.getAbsolutePath());
      File currentFile;

      try {
         String[] files = dir.list();
         for(int i = 0; i < files.length; i++) {
            currentFile = new File(dir.getAbsolutePath() + "/" + files[i]);
            if(currentFile.isDirectory()) {
               System.out.println("Processing directory: " + currentFile.getName());
               ProcessDirectory(new File(currentFile.getAbsolutePath()));
            }
            else {
               fileList.add(currentFile);
            }
         }
      }
      catch(Exception e) {
         System.out.println("TransCryptor.ProcessDirectory exception: " + e.getMessage());
         e.printStackTrace();
         System.exit(-1);
      }
   }

   public void processFile(File currentFile) {
      try {
         System.out.println("Processing file: " + currentFile.getAbsolutePath());
         ProgressMonitorInputStream pmis =
         new ProgressMonitorInputStream(
                                       frame,
                                       "TransCrypting " + currentFile,
                                       new FileInputStream(currentFile));
         pmis.getProgressMonitor().setMillisToPopup(0);
         pmis.getProgressMonitor().setMillisToDecideToPopup(0);
         InputStream in = new BufferedInputStream(pmis);


         byte[] buf = new byte[64];
         while(in.read(buf) != -1)
            Thread.sleep(10);
      }
      catch(Exception e) {
         System.out.println("ProcessFile exception: " + e.getMessage());
         e.printStackTrace();
      }
   }

   public static void main(String args[]) {
      TransCryptor correct = null;
      boolean done = false;
      File file = new File("C:\\devel\\shack");

      if(! file.isDirectory()) {
         System.out.println("Top directory is not a directory!");
         System.exit(1);
      }

      try {
         correct = new TransCryptor();
         correct.go();
      }
      catch(Exception e) {
         System.out.println("Caught exception: " + e);
         e.printStackTrace();
      }
      System.out.println("\nProgram complete.\n\n");
      System.exit(0);
   }

}