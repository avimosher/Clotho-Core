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
package org.clothocad.tool.bulltrowell.interpreters;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.clothocore.api.core.Collector;
import org.clothocore.api.data.Collection;
import org.clothocore.api.data.Feature;
import org.clothocore.api.data.Format;
import org.clothocore.api.data.ObjLink;
import org.clothocore.api.data.ObjType;
import org.clothocore.api.data.Oligo;
import org.clothocore.api.data.Person;
import org.clothocad.tool.bulltrowell.view.hub;
import org.clothocad.tool.bulltrowell.view.spreadsheet;
import org.clothocore.api.data.Plate;
import org.clothocore.api.data.PlateType;

/**
 *
 * @authorInputString Jenhan Tao
 */
public class addPlate implements Interpreter {

    public addPlate() {
        String[] titles = {"Name", "Author", "Type", "Rows", "Columns", "Fixed"};
        _numCols = titles.length;
        _numRows = hub.numrows;
        _data = new Object[_numRows][_numCols];
        for (int i = 0; i < _numRows; i++) {
            for (int j = 0; j < _numCols - 1; j++) {
                _data[i][j] = "";
            }
//            _data[i][_numCols - 1] = new JCheckBox();
        }
        _mySheet = new spreadsheet(_data, titles, this);
        _mySheet.setTitle("Add Plates");
        _mySheet.setTitleArea("Add plates from Excel by Copy and Paste<br>You must supply Name, Type, Rows, Columns, Fixed, and Location.<br>Other fields will be set to defaults below.");
        _mySheet.setDataModel(_data, new DefaultTableModel(_data, titles) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        });
        if (Collector.isConnected()) {
            loadingthread = new Thread() {

                @Override
                public void run() {
                    /*
                    //Put in the Collection chooser (field 1)
                    _collections = Collector.getAllLinksOf(ObjType.COLLECTION);
                    final Object[] collChoices =  _collections.toArray();
                    SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                    _mySheet.putComboField1("Collection", collChoices, collChoices[0].toString());
                    }
                    });
                     */


                    //Get the current user
                    final Person user = Collector.getCurrentUser();

                    //Put in the authorInputString chooser (field 2)
                    _persons = Collector.getAllLinksOf(ObjType.PERSON);
                    final Object[] authorChoices = _persons.toArray();
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            @Override
                            public void run() {
                                _mySheet.putComboField2("Author", authorChoices, user.getObjLink());
                            }
                        });
                    } catch (InterruptedException ex) {
                        Logger.getLogger(addFeature.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(addFeature.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            loadingthread.start();
        }
    }

    @Override
    public void receiveData(Object[][] data) {
        _data = (Object[][]) data;
        //Create a new Collection to store everything
        if (_outCollection == null) {
            _outCollection = new Collection();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    _outCollection.launchDefaultViewer();
                }
            });
        }

        //If it's still loading wait for it to finish loading
        try {
            loadingthread.join();
        } catch (InterruptedException ex) {
        }

        //Get the formatInputString and authorInputString defaults
        ObjLink link = (ObjLink) _mySheet.getComboField2();
        _defaultAuthor = Collector.getPerson(link.uuid);

        _data = (Object[][]) data;

        for (int i = 0; i < _data.length; i++) {
            try {
//        String[] titles = {"Name", "Author", "Type", "Rows", "Columns",  "Fixed"};

                //Retrieve the data in the current line
                String name = (String) _data[i][0];
                String type = (String) _data[i][2];
                String rows = (String) _data[i][3];
                String columns = (String) _data[i][4];
                Boolean isFixed = (Boolean) _data[i][5];
                //If any of the 3 required fields weren't entered, abort this line
                if (rows.equals("") || type.equals("") || columns.equals("") || name.equals("")) {
                    continue;
                }
                int rowNumber = Integer.parseInt(rows);
                int columnNumber = Integer.parseInt(columns);


                //Retrieve any additional lines entered
                String authorInputString = (String) _data[i][1];

                //If an authorInputString was typed in, search for that Person
                Person plateAuthor;
                if (authorInputString.equals("")) {
                    plateAuthor = _defaultAuthor;
                } else {
                    plateAuthor = Person.retrieveByName(authorInputString);
                    if (plateAuthor == null) {
                        continue;
                    }
                }
                PlateType plateType = new PlateType(type, rowNumber, columnNumber, isFixed, "");
                Plate newPlate = new Plate(name, plateType, plateAuthor);
                if (newPlate == null) {
                    continue;
                }

                //Notify user that the Oligo was created
                _mySheet.appendLogMessage(newPlate.getName() + "\n");
                System.out.println("I created Plate " + newPlate.getName());
                _outCollection.addObject(newPlate);

                //Clear the data in the data table
                for (int col = 0; col < _numCols; col++) {
                    _data[i][col] = "";
                }
            } catch (Exception e) {
                continue;
            }
        }
        _mySheet.refreshData(_data);

    }

    /*-----------------
    variables
    -----------------*/
    private Object[][] _data;
    private spreadsheet _mySheet;
    private ArrayList<ObjLink> _persons;
    private ArrayList<ObjLink> _collections;
    private ArrayList<ObjLink> _formats;
    private Person _defaultAuthor;
    private Collection _outCollection;
    private Format _defaultFormat;
    private int _numCols;
    private int _numRows;
    private String messageText = "";
    private Thread loadingthread;
}
