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
import javax.swing.SwingUtilities;
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
import org.clothocore.api.data.Container;
import org.clothocore.api.data.OligoSample;
import org.clothocore.api.data.Plasmid;
import org.clothocore.api.data.PlasmidSample;
import org.clothocore.api.data.Sample;
import org.clothocore.api.data.Strain;
import org.clothocore.api.data.StrainSample;

/**
 *
 * @authorInputString J. Christopher Anderson
 */
public class addSample implements Interpreter {

    public addSample(int rows, int columns) {
        String[] titles = new String[columns];
        _numCols = titles.length;
        _numRows = hub.numrows;
        _data = new String[_numRows][_numCols];
        for (int i = 0; i < _numCols; i++) {
            titles[i] = "----------";
        }
        for (int i = 0; i < _numRows; i++) {
            for (int j = 0; j < _numCols; j++) {
                _data[i][j] = "";
            }
        }
        _mySheet = new spreadsheet(_data, titles, this);
        _mySheet.setTitle("Add Samples");
        _mySheet.setTitleArea("Add samples from Excel by Copy and Paste<br>You must supply A properly formatted name as well as select from the appropriate combo boxes.<br>Other fields will be set to defaults below.");

        if (Collector.isConnected()) {
            loadingthread = new Thread() {

                @Override
                public void run() {

                    //Get the current user
                    final Person user = Collector.getCurrentUser();

                    //Put in the authorInputString chooser (field 2)
                    _persons = Collector.getAllLinksOf(ObjType.PERSON);
                    final Object[] authorChoices = _persons.toArray();
                    final Object[] types = {"Oligo", "Plasmid", "Strain"};
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {

                            @Override
                            public void run() {
                                _mySheet.putComboField1("Author", authorChoices, user.getObjLink());
                                _mySheet.putComboField2("Type", types, "Oligo");
                                _mySheet.putTextField1("Volume (uL)", "40.0        ");
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
        _data = (String[][]) data;
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

        //Get the authorInputString defaults
        ObjLink authorLink = (ObjLink) _mySheet.getComboField1();
        _defaultAuthor = Collector.getPerson(authorLink.uuid);
        Double volume = 40.0;
        String type = (String) _mySheet.getComboField2();

        try {
            volume = Double.parseDouble(_mySheet.getTextField1());
        } catch (NumberFormatException e) {
            volume = 40.0;
        }
        for (int i = 0; i < _numCols; i++) {
            for (int j = 0; j < _numRows; j++) {
                try {
                    String name = (String) data[i][j];
                    name = name.trim().replaceAll("\\s", ""); //remove whitespace and newline characters, neither of which are part of a legal Sample name
                    if (name.equals("")) {
                        continue;
                    }
                    //the if block below overrides the default type
                    if (name.indexOf(":") > -1) {
                        String potentialType = name.substring(0, name.indexOf(":"));
                        if (potentialType.equalsIgnoreCase("Oligo")) {
                            type = "Oligo";
                            name = name.substring(name.indexOf(":") + 1);
                        } else if (potentialType.equalsIgnoreCase("Plasmid")) {
                            type = "Plasmid";
                            name = name.substring(name.indexOf(":") + 1);
                        } else if (potentialType.equalsIgnoreCase("Strain")) {
                            type = "Strain";
                            name = name.substring(name.indexOf(":") + 1);
                        }
                    }
                    Sample newSample = null;
                    if (type.equals("Oligo")) {
                        Oligo anOligo = Oligo.retrieveByName(name);
//                        System.out.println("oligo name: "+anOligo.getName());
                        if (anOligo.getName().equals(name)) {
                            Container aContainer = new Container();
                            newSample = OligoSample.generateOligoSample(anOligo, aContainer, volume, _defaultAuthor);
                            aContainer.EVT_SampleToContainer(newSample);
                        } else {
                            System.out.println("Error saving " + name + " as an oligo sample");
                            continue;
                        }
                    } else if (type.equals("Plasmid")) {
                        String strainName = "";
                        String origName = name;
                        if (name.indexOf("_") > 0) {
                            strainName = name.substring(name.lastIndexOf("_") + 1);
                            name = name.substring(0, name.lastIndexOf("_"));
                        }
                        //plasmid:pSB1AC3-BBa_R0040_DH5a
                        Plasmid aPlasmid = Plasmid.retrieveByName(name);
                        Strain aStrain = Strain.retrieveByName(strainName);
//                        System.out.println("making an plasmid sample with plasmid:" + name + " and strain:" + strainName);
                        if (aPlasmid.getName().contains(name) && aStrain.getName().equals(strainName)) {
                            Container aContainer = new Container();
                            newSample = PlasmidSample.generatePlasmidSample(aPlasmid, aStrain, aContainer, volume, _defaultAuthor);
                            aContainer.EVT_SampleToContainer(newSample);
                        } else {
                            System.out.println("Error saving " + origName + " as a plasmid sample");
                            continue;
                        }
                    } else if (type.equals("Strain")) {
//                        System.out.println("making a strain" + name);
                        Strain aStrain = Strain.retrieveByName(name);
                        if (aStrain.getName().equals(name)) {
                            Container aContainer = new Container();
                            newSample = StrainSample.generateStrainSample(aStrain, aContainer, volume, _defaultAuthor);
                            aContainer.EVT_SampleToContainer(newSample);
                        } else {
                            System.out.println("Error saving " + name + " as a strain sample");
                            continue;
                        }
                    }
                    if (newSample != null) {
                        System.out.println("adding a new sample");
                        _outCollection.addObject(newSample);
                        newSample = null;
                    }

                } catch (Exception e) {
                    continue;
                }
            }

        }
        _mySheet.refreshData(_data);

    }

    /*-----------------
    variables
    -----------------*/
    private String[][] _data;
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
