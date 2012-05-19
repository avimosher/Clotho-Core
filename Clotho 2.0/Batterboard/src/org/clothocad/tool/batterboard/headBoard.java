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

/*
 * headBoard.java
 *
 * Created on Jul 7, 2010, 11:08:29 AM
 */

package org.clothocad.tool.batterboard;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.ObjLink;
import org.clothocore.api.data.ObjType;
import org.clothocore.api.data.Plate;
import org.clothocore.api.core.Collector;
//import org.clothocore.api.dnd.
import org.clothocore.api.dnd.RefreshEvent;
import org.clothocore.util.elements.AutoComboBox;


import org.clothocore.api.dnd.ObjBaseObserver;


/**
 *
 * @author J. Christopher Anderson
 */
public class headBoard extends javax.swing.JPanel implements ObjBaseObserver {

    /** Creates new form headBoard */
    public headBoard(Plate aplate, frame f) {
        _plate = aplate;
        _frame = f;
        initComponents();
        init2();
        //refreshData(null); FIXME
        update (null, null);
    }

    private void initComponents() {

        titleField = new javax.swing.JTextField();
        ArrayList<String> texty = new ArrayList<String>();
        authorBox = new AutoComboBox(texty);
        platetypeBox = new AutoComboBox(texty);
        
        new Thread() {
            public void run() {
                putAuthors();
                putPlateTypes();
            }
        }.start();

        datecreated = new javax.swing.JLabel();
        datemodified = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        authorLabel = new javax.swing.JLabel();
        revertButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        barcodeField = new javax.swing.JTextField();
        barcodeLabel = new javax.swing.JLabel();

        titleField.setText("Plate being loaded...");

        datecreated.setText("jLabel1");

        datemodified.setText("jLabel2");

        typeLabel.setText("Plate Type");

        authorLabel.setText("Author");

        revertButton.setText("Revert");

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        barcodeLabel.setText("Barcode");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleField, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)


                    .addGroup(layout.createSequentialGroup()
                        .addComponent(authorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(authorBox, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(datecreated, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))

                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(platetypeBox, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(datemodified, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))

                    .addGroup(layout.createSequentialGroup()
                        .addComponent(barcodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(barcodeField, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(revertButton, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(authorLabel)
                        .addComponent(authorBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(datecreated))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(platetypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeLabel)
                    .addComponent(datemodified))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(revertButton)
                            .addComponent(saveButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(barcodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(barcodeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
        );
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        _plate.changeName(titleField.getText());
        _plate.setBarcode(barcodeField.getText());

        /** this part of code isnt working.. FIXIT
        ObjLink list = (ObjLink) authorBox.getSelectedItem();
        _plate.changeAuthor(Collector.getPerson(list.uuid));

        list = (ObjLink) platetypeBox.getSelectedItem();
        _plate.changePlateType(Collector.getPlateType(list.uuid));
        **/
        _plate.saveDefault();
        //refreshData(null); //FIX
        update (null, null);
    }

    private void putAuthors() {
        authorList = Collector.getAllLinksOf(ObjType.PERSON);
        SwingUtilities.invokeLater(new Runnable() {
        @Override
            public void run() {
                authorBox.setDataList(authorList);
                authorBox.setSelectedItem(_plate.getAuthor().getName());
            }
        });
    }

    private void putPlateTypes() {
        platetypeList = Collector.getAllLinksOf(ObjType.PLATE_TYPE);
        SwingUtilities.invokeLater(new Runnable() {
        @Override
            public void run() {
                platetypeBox.setDataList(platetypeList);
                platetypeBox.setSelectedItem(_plate.getPlateType().getName());
            }
        });

    }

    private void init2() {
        setBackground(frame.navyblue);

        datecreated.setText("");
        datemodified.setText("");

        titleField.setOpaque(false);
        titleField.setBorder(null);
        titleField.setFont(new Font("Arial", Font.PLAIN, 18));
        titleField.setForeground(Color.white);

        barcodeField.setOpaque(false);
        barcodeField.setBorder(null);
        barcodeField.setFont(new Font("Arial", Font.PLAIN, 12));
        barcodeField.setForeground(Color.white);

        datecreated.setForeground(Color.white);
        datemodified.setForeground(Color.white);
        typeLabel.setForeground(Color.white);
        authorLabel.setForeground(Color.white);
        barcodeLabel.setForeground(Color.white);

    }

    /*public void refreshData(RefreshEvent evt) {

        SwingUtilities.invokeLater(new Runnable() {
        @Override
            public void run() {
                if(_plate.getDateCreated()!=null) {
                   datecreated.setText(_plate.getDateCreated().toString());
                }
                if(_plate.getLastModified()!=null) {
                    datemodified.setText(_plate.getLastModified().toString());
                }
                barcodeField.setText(_plate.getBarcode());

                try {
                    //Wait for full Plate loading to finish
                    _frame.plateLoadingThread.join();
                } catch (InterruptedException ex) {
                }

                titleField.setText(_plate.getName());
            }
        });


    }*/

    //Added; not sure if this is correct
    @Override
    public void update(ObjBase obj, RefreshEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
        @Override
            public void run() {
                if(_plate.getDateCreated()!=null) {
                   datecreated.setText(_plate.getDateCreated().toString());
                }
                if(_plate.getLastModified()!=null) {
                    datemodified.setText(_plate.getLastModified().toString());
                }
                barcodeField.setText(_plate.getBarcode());

                try {
                    //Wait for full Plate loading to finish
                    _frame.plateLoadingThread.join();
                } catch (InterruptedException ex) {
                }
                catch (Exception e)
                {
                    e.getMessage();
                }

                titleField.setText(_plate.getName());
            }
        });
    }



/**-----------------
     variables
 -----------------*/
    // Variables declaration - do not modify
    private AutoComboBox authorBox;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JTextField barcodeField;
    private javax.swing.JLabel barcodeLabel;
    private javax.swing.JLabel datecreated;
    private javax.swing.JLabel datemodified;
    private AutoComboBox platetypeBox;
    private javax.swing.JButton revertButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration

    private Plate _plate;
    private ArrayList<ObjLink> authorList;
    private ArrayList<ObjLink> platetypeList;
    private frame _frame;


}
