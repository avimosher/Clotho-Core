/*
 * 
Copyright (c) 2010 The Regents of the University of California.
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

package org.clothocad.viewer.plasmideditortc.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.clothocore.api.data.Plasmid;

/**
 *
 * @author jcanderson_Home
 */
public class ConstructionEditor extends JPanel {

    public ConstructionEditor(Plasmid aplas) {
        _plas= aplas;
        setLayout(new BorderLayout());
        setBackground(new Color(240,240,240));

        Box topPanel = new Box(BoxLayout.X_AXIS);
        add(topPanel, BorderLayout.NORTH);

        JButton addPCR = new JButton("Add PCR");
        topPanel.add(addPCR);

        JButton addDigest = new JButton("Add Digest");
        topPanel.add(addDigest);

        JButton addLig = new JButton("Add Ligation");
        topPanel.add(addLig);

        JTextArea midArea = new JTextArea();
        midArea.setLineWrap(true);
        midArea.setWrapStyleWord(true);
        midArea.setEditable(false);
        midArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(midArea, BorderLayout.CENTER);

        System.out.println(_plas.getConstructionFileAsString());
        
        //Construct acon = ConstructionParser.parseOut(_plas.getConstructionFileAsString());
        //midArea.setText(acon.getXML());

    }




    /* GETTERS
     * */



    /* SETTERS
     * */



    ///////////////////////////////////////////////////////////////////
    ////                      private variables                    ////
    private Plasmid _plas;
}
