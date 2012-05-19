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

package org.clothocad.viewer.plateeditortc.guis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.Plate;
import org.clothocore.api.dnd.ObjBaseObserver;
import org.clothocore.api.dnd.RefreshEvent;
import org.clothocore.util.basic.ObjBasePopup;

/**
 *
 * @author jcanderson_Home
 */
public class HeaderPanel extends JPanel {

    public HeaderPanel(Plate aplate) {
        _plate = aplate;
        fname = new JTextField();
        add(fname);
        fname.setText(aplate.getName());

        fbar = new JTextField();
        add(fbar);
        fbar.setText(aplate.getBarcode());

        JButton cancelbtn = new JButton("Cancel Changes");
        add(cancelbtn);
        cancelbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fbar.setText(_plate.getBarcode());
                    fname.setText(_plate.getName());
                } catch(Exception err) {
                }
            }
        });

        JButton btn = new JButton("Save Changes");
        add(btn);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = fname.getText();
                    _plate.changeName(name);
                    String bar = fbar.getText();
                    _plate.setBarcode(bar);
                } catch(Exception err) {
                    err.printStackTrace();
                }
            }
        });

        _obo = new plateObserver();
        _plate.isRepresentedBy(_obo, this);
        ObjBasePopup obp = new ObjBasePopup(this, aplate);
    }



    /* GETTERS
     * */



    /* SETTERS
     * */

    private class plateObserver implements ObjBaseObserver {
        @Override
        public void update(ObjBase obj, RefreshEvent evt) {
            if(evt.isCondition(RefreshEvent.Condition.NAME_CHANGED)) {
                fbar.setText(_plate.getBarcode());
                fname.setText(_plate.getName());
            }
        }
    }


    ///////////////////////////////////////////////////////////////////
    ////                      private variables                    ////
        JTextField fname;
        JTextField fbar;
        Plate _plate;
        plateObserver _obo;
}
