/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clothocad.viewer.plateeditortc.guis;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.clothocore.api.data.Container;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.Plate;
import org.clothocore.api.data.Sample;
import org.clothocore.api.dnd.ObjBaseObserver;
import org.clothocore.api.dnd.RefreshEvent;

/**
 *
 * @author jcanderson
 */
public class PlatePanel extends JPanel {

    public PlatePanel(Plate aplate) {
        setLayout(new GridLayout(aplate.getNumRows(),aplate.getNumCols() ));
        po = new PlateObserver();
        _plate = aplate;

        JLabel loadingLabel = new JLabel("Plate loading, please wait...");
        add(loadingLabel);

        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                preLoad();
                return null;
            }

            @Override
            public void done() {
                putInWells();
            }
        }.execute();
    }

    private class PlateObserver implements ObjBaseObserver {
        @Override
        public void update(ObjBase obj, RefreshEvent evt) {
            if(evt.getCondition().equals(RefreshEvent.Condition.CONTAINER_TO_PLATE)) {
                putInWells();
            }
        }

    }

    private void preLoad() {
        for(int row = 0; row<_plate.getNumRows(); row++) {
            for(int col = 0; col<_plate.getNumCols(); col++) {
                Container acon = _plate.getContainerAt(row, col);
                if(acon!=null) {
                    Sample asam = acon.getSample();
                }
            }
        }
    }

    private void putInWells() {
        removeAll();
        validate();
        repaint();

        //Draw the wells
        for(int row = 0; row<_plate.getNumRows(); row++) {
            for(int col = 0; col<_plate.getNumCols(); col++) {
                Container acon = _plate.getContainerAt(row, col);
                WellPanel awell = new WellPanel(acon);
                add(awell);
            }
        }

        validate();
        repaint();


    }

    private PlateObserver po;
    private Plate _plate;

}
