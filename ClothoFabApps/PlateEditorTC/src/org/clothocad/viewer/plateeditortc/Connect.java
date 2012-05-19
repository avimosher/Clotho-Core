/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clothocad.viewer.plateeditortc;

import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import org.clothocad.viewer.plateeditortc.guis.HeaderPanel;
import org.clothocad.viewer.plateeditortc.guis.PlatePanel;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.ObjType;
import org.clothocore.api.data.Plate;
import org.clothocore.api.plugin.ClothoViewer;

/**
 *
 * @author jcanderson
 */
public class Connect implements ClothoViewer {

    @Override
    public void launch(ObjBase o) {
        if(!o.getType().equals(ObjType.PLATE)) {
            return;
        }
        final Plate aplate = (Plate) o;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PlateTopComponent ptc = new PlateTopComponent();
                ptc.setName("Plate: " + aplate.getName());
                PlatePanel pp = new PlatePanel(aplate);
                ptc.add(pp, BorderLayout.CENTER);

                HeaderPanel hp = new HeaderPanel(aplate);
                ptc.add(hp, BorderLayout.NORTH);

                ptc.open();
                ptc.requestActive();
            }
        });

    }

    @Override
    public void close() {
    }

    @Override
    public void init() {
    }

}
