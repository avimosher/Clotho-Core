/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clothocad.viewer.partviewertc;

import java.awt.BorderLayout;
import java.lang.ref.WeakReference;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.clothocad.viewer.commonviewtc.HubTopComponent;
import org.clothocad.viewer.partviewertc.panels.PartImagePanel;
import org.clothocad.viewer.partviewertc.panels.PartInfoPanel;
import org.clothocad.viewer.partviewertc.panels.VisualScene;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.ObjType;
import org.clothocore.api.data.Part;
import org.clothocore.api.plugin.ClothoViewer;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 *
 * @author jcanderson
 */
public class Connect implements ClothoViewer {

    @Override
    public void launch(ObjBase o) {
        if(!o.getType().equals(ObjType.PART)) {
            return;
        }
        final Part apart = (Part) o;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PartInfoPanel pip = new PartInfoPanel(apart);
                HubTopComponent.show(pip, "Part: " + apart.getName());
                ImageWindowTopComponent iwtc = new ImageWindowTopComponent(pip, apart);

                VisualScene scene = new VisualScene(apart);
                JComponent myView = scene.createView();
                scene.getPart().isDropListenedBy(myView);

                iwtc.getScroller().setViewportView(myView);
                iwtc.setName("Part: " + apart.getName());
                iwtc.open();
                iwtc.requestActive();
                iwtc.validate();
                iwtc.repaint();
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
