/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clothocad.viewer.partviewertc.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JPanel;
import org.clothocore.api.data.Annotation;
import org.clothocore.api.data.Feature;
import org.clothocore.api.data.Part;

/**
 *
 * @author jcanderson
 */
public class PartImagePanel extends JPanel {

    public PartImagePanel(Part apart) {
        _part = apart;
    }

    private void drawBasicPart() {
        Rectangle rect = new Rectangle(100,100,200,25);
        g2d.fill(rect);
    }

    private void drawCompositePart() {
        ArrayList<Part> composition = _part.getCompositeParts();
        int size = composition.size();
        int width = 500/size;
        int offset = 20;
        System.out.println("width " + width + "size " + size);

        //Draw a square for each part
        g2d.setColor(Color.red);
        for(int i=0; i<composition.size(); i++) {
            Rectangle rect = new Rectangle(offset+i*width,100,width-2,35);
            g2d.fill(rect);
        }


    }

    @Override
    public void paintComponent(Graphics g) {
        System.out.println("Painting window");
        g2d = (Graphics2D) g;

        //Scale things to fit window
        int seqlength = _part.getSeq().seqLength();
        pxperbp = totalwidth/seqlength;

        //Draw the features
        HashSet<Annotation> annots = _part.getSeq().getAnnotations();
        for(Annotation annot : annots) {
            int start = annot.getStart();
            int end = annot.getEnd();
            g2d.setColor(annot.getColor());
            Feature afeat = annot.getFeature();
            if(afeat!=null) {
                Rectangle rect = new Rectangle(offset+start+5,60,end-start-10,35);
                g2d.draw(rect);
                continue;
            }
            Rectangle rect = new Rectangle(offset+start+5,60,end-start-10,35);
            g2d.fill(rect);
        }


        if(_part.getPartType().equals(Part.partType.Basic)) {
            drawBasicPart();
        } else {
            drawCompositePart();
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables               ////
    private Part _part;
    private Graphics2D g2d;
    private double pxperbp;
    int offset = 20;
    int totalwidth = 600;
}
