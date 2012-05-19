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

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.clothocore.api.data.PlasmidSample;
import org.clothocore.api.data.Sample;



/**
 *
 * @author J. Christopher Anderson
 */
public class popupPanel extends JPanel {
    public popupPanel() {

        setLayout(null);
        
        pop = new JPanel();
        pop.setLayout(null);
        pop.setBackground(Color.white);


        nameLabel = new JLabel();
        authorLabel = new JLabel();
        volumeLabel = new JLabel();
        nameLabel.setBounds(3,2, popwidth-4,19);
        authorLabel.setBounds(3,21, popwidth-4,19);
        volumeLabel.setBounds(3,40, popwidth-4,19);
        pop.add(nameLabel);
        pop.add(authorLabel);
        pop.add(volumeLabel);

        add(pop);
        pop.setVisible(false);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    public void initpopup(Sample asample, int x, int y, int spacing) {

        int panelwidth = getBounds().width;
        int panelheight = getBounds().height;

        if(x > panelwidth/2) {
            x = x - popwidth;
        } else {
            x = x + spacing;
        }

        if(y > panelheight/2) {
            y = y - popheight;
        } else {
            y = y + spacing;
        }

        switch(asample.getSampleType()) {
            case PLASMID_SAMPLE:
                PlasmidSample ps = (PlasmidSample) asample;
                nameLabel.setText(ps.getPlasmid().getName());
                break;
            default:
                nameLabel.setText(asample.getName());
                break;
        }
        
        authorLabel.setText("Author: " + asample.getAuthor().getName());
        volumeLabel.setText("Volume: " + asample.getVolume() + " uL");

        pop.setBounds(x, y, popwidth,popheight);
        pop.setVisible(true);
    }

    public void clearPopup() {
        pop.setVisible(false);
    }

    /* SETTERS
     * */

    /* PUTTERS
     * */

    /* GETTERS
     * */

/*-----------------
     variables
 -----------------*/
    private JPanel pop;
    private static final int popwidth = 130;
    private static final int popheight = 61;
    JLabel nameLabel;
    JLabel authorLabel;
    JLabel volumeLabel;
}
