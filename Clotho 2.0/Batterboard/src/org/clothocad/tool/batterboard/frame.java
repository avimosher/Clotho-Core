/*
Copyright (c) 2009 The Regents of the University of California.
All rights reserved.
Permission is hereby granted, without written agreement and without
license or royalty fees, to use, copy, modify, and distribute this
software and its documentation for any purpose, provided that the above
copyright notice and the following two paragraphs appear in all copies
of this software.

IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.

THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
ENHANCEMENTS, OR MODIFICATIONS..
 */
package org.clothocad.tool.batterboard;

import java.io.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.*;
import org.clothocore.api.data.Plate;
import org.clothocore.util.misc.StackLayout;
import org.clothocore.util.misc.CommandHelp;

/**
 *
 * @author J. Christopher Anderson
 */
public class frame extends JFrame implements ActionListener {

    public frame(Plate aplate) {
        super("Batterboard");
        _plate = aplate;
        initComponents();
        importExport = new ImportExport(_plate);
        //headboard.refreshData(null); FIXME
        headboard.update(null, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("editWell")) {
            System.out.println("edit selected well..");

            JFrame testFrame = new ContainerEditFrame(this._plate, this.qualityview.getLastSelectedContainer());
            hub.guis.add(new WeakReference(testFrame));
            testFrame.setVisible(true);
        }

    }

    private void initComponents() {
        setResizable(false);
        menuBar = new javax.swing.JMenuBar();

        //Put in file menu items
        fileMenu = new javax.swing.JMenu();
        fileMenu.setText("File");
        JMenuItem jMenuItem1 = new JMenuItem();
        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.out.println("jMenuItem1");
            }
        });
        fileMenu.add(jMenuItem1);
        menuBar.add(fileMenu);

        //Put in edit menu items
        editMenu = new javax.swing.JMenu();
        editMenu.setText("Edit");
        JMenuItem volumeItem = new JMenuItem();
        volumeItem.setText("Edit Volumes");
        volumeItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.out.println("edit volumes");
            }
        });
        editMenu.add(volumeItem);

        //put in a selection to edit highlighted container
        JMenuItem editContainerItem = new JMenuItem();
        editContainerItem.setText("Edit selected Well..");
        editContainerItem.setActionCommand("editWell");
        editContainerItem.addActionListener(this);
        editMenu.add(editContainerItem);

        //put selections for import export
        JMenuItem importItem = new JMenuItem();
        JMenuItem exportItem = new JMenuItem();

        importItem.setText("Import from Excel..");
        importItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {


                int returnVal = fileChooser.showOpenDialog(fileMenu);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    importExport.importFromExcel(file);
                    System.out.println("path = " + file.toString() + " \nImport excel file = " + file.getName());
                } else {
                    System.out.println("Import file canceled by user");
                }

            }
        });

        exportItem.setText("Export to Excel..");
        exportItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String DATE_FORMAT_NOW = "yyyyMMdd_HHmmss";
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                String dateTime = sdf.format(cal.getTime());
                String filename = "exportdata" + "_" + _plate.getName() + "_" + dateTime + ".xls";
                System.out.println("EXPORT is chosen..filename = " + filename);
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Choose destination folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : " + chooser.getSelectedFile());

                    filename = chooser.getSelectedFile() + "\\" + filename;
                    importExport.exportToExcel(filename);
                } else {
                    System.out.println("export cancelled by user");
                }


            }
        });
        editMenu.add(exportItem);
        editMenu.add(importItem);
        //Put in selection menu items
        selectionMenu = new javax.swing.JMenu();
        selectionMenu.setText("Selection");
        JMenuItem deleteItem = new JMenuItem();
        deleteItem.setText("Delete samples");
        deleteItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.out.println("Delete the samples selected");
            }
        });
        selectionMenu.add(deleteItem);

        //Put in help menu items
        helpMenu = new javax.swing.JMenu();
        helpMenu.setText("Help");
        JMenuItem commandsItem = new JMenuItem();
        commandsItem.setText("Command List");
        commandsItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //Need to bring help back
                //new CommandHelp(new File("plugins/Tools/Batterboard/commands.csv"), "Batterboard", "clothobugs@googlegroups.com", "http://www.clothohelp.org");
            }
        });
        helpMenu.add(commandsItem);

        menuBar.add(editMenu);
        menuBar.add(selectionMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);


        getContentPane().setLayout(new BorderLayout());

        JPanel stackingpanel = new JPanel();
        stackingpanel.setLayout(new StackLayout());

        popupPanel pp = new popupPanel();
        qualityview = new qualityViewer(_plate, pp, this);
        stackingpanel.add(qualityview, StackLayout.TOP);
        stackingpanel.add(pp, StackLayout.TOP);

        getContentPane().add(stackingpanel, BorderLayout.CENTER);

        headboard = new headBoard(_plate, this);
        headboard.setPreferredSize(new Dimension(qualityview.getWidth(), 150));
        headboard.setBackground(navyblue);
        getContentPane().add(headboard, BorderLayout.NORTH);

        JPanel baseboard = new JPanel();
        baseboard.setPreferredSize(new Dimension(qualityview.getWidth(), 15));
        baseboard.setBackground(navyblue);
        getContentPane().add(baseboard, BorderLayout.SOUTH);

        JPanel leftboard = new JPanel();
        leftboard.setPreferredSize(new Dimension(10, qualityview.getHeight() + 160));
        leftboard.setBackground(navyblue);
        getContentPane().add(leftboard, BorderLayout.EAST);

        JPanel rightboard = new JPanel();
        rightboard.setPreferredSize(new Dimension(10, qualityview.getHeight() + 160));
        rightboard.setBackground(navyblue);
        getContentPane().add(rightboard, BorderLayout.WEST);

        pack();
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /*-----------------
    variables
    -----------------*/
    private qualityViewer qualityview;
    Plate _plate;
    static Color navyblue = new Color(35, 48, 64);
    private headBoard headboard;
    Thread plateLoadingThread;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu selectionMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar menuBar;
    private ImportExport importExport;
    private JFileChooser fileChooser = new JFileChooser();
}
