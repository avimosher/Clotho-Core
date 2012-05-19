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

package org.clothocad.viewer.partviewertc.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import org.clothocore.api.data.Annotation;
import org.clothocore.api.data.Feature;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.Part;
import org.clothocore.api.dnd.ObjBaseObserver;
import org.clothocore.api.dnd.RefreshEvent;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jcanderson_Home
 */
public class VisualScene extends GraphScene {

    public VisualScene(Part apart) {
        _scene = this;
        _part = apart;
        layer = new LayerWidget (_scene);
        _scene.addChild(layer);

        _scene.getActions().addAction (ActionFactory.createZoomAction ());
        _scene.getActions().addAction (ActionFactory.createPanAction ());
        WidgetAction hoverAction = ActionFactory.createHoverAction (new MyHoverProvider ());
        WidgetAction popupMenuAction = ActionFactory.createPopupMenuAction ((PopupMenuProvider) new DeckPosPopup ());
        _scene.getActions().addAction (hoverAction);

        //Draw the part
        drawPart();

        //Set up updates to the part and it's nucseq
        _obo = new ObjBaseObserver() {
            @Override
            public void update(ObjBase obj, RefreshEvent evt) {
                drawPart();
            }
        };
        _part.isObservedBy(_obo);
        _part.getSeq().isObservedBy(_obo);
    }

    /**
     * Controller method for all redrawing of the part diagram
     */
    private void drawPart() {
        putInFeatures();

        _scene.validate();
        _scene.repaint();
    }

    private void putInFeatures() {
        //Draw the features
        HashSet<Annotation> annots = _part.getSeq().getAnnotations();
        for(Annotation annot : annots) {
            int start = annot.getStart();
            int end = annot.getEnd();

            Feature afeat = annot.getFeature();
            ComponentWidget widget = new ComponentWidget(_scene, new cdsComponent(afeat));

            //If double click on feature launch preferred viewer
            MyAction action = new MyAction (afeat);
            widget.getActions().addAction(action);

            double dpos = Math.floor(start * calcPxPerBp() * getZoomFactor());
            int pos = (int) dpos;

            widget.setPreferredLocation (new Point (pos, 50));
            layer.addChild (widget);
        }
    }

    private class cdsComponent extends JComponent {
        //VARIABLES//
        private Feature afeat;

        public cdsComponent(Feature afeature) {
            afeat = afeature;
            setPreferredSize(new Dimension(calculateWidth(),calculateHeight()));
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            if(afeat!=null) {
                g2d.setColor(afeat.getForwardColor());
                Rectangle rect = new Rectangle(0,0, calculateWidth(),calculateHeight());
                g2d.fill(rect);
            }
        }

        private int calculateWidth() {
            return (int) Math.floor(afeat.getSeq().seqLength() * calcPxPerBp() * getZoomFactor());
        }

        private int calculateHeight() {
            return (int) Math.floor(35.0*getZoomFactor());
        }
    }

    public class MyAction extends WidgetAction.Adapter {
        private Feature _feat;
        public MyAction(Feature afeat) {
            _feat = afeat;
        }
        @Override
        public State mouseClicked (Widget widget, WidgetMouseEvent event) {
            if(event.getClickCount()==2) {
                _feat.launchDefaultViewer();
            }
            return State.REJECTED;
        }

    }

    private class MyHoverProvider implements TwoStateHoverProvider {
        @Override
        public void unsetHovering(Widget widget) {
            if (widget != null) {
                widget.setBackground (Color.WHITE);
                widget.setForeground (Color.BLACK);
            }
        }

        @Override
        public void setHovering(Widget widget) {
            if (widget != null) {
                widget.setBackground (new Color (52, 124, 150));
                widget.setForeground (Color.WHITE);
            }
        }

    }

    private double calcPxPerBp() {
        double length = _part.getSeq().getSeq().length();
        return 500.0/length;
    }

    private class DeckPosPopup implements PopupMenuProvider {
        //VARIABLES//
        @Override
        public JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
            JPopupMenu menu = new JPopupMenu ();
            return menu;
        }
    }

    @Override
    protected Widget attachNodeWidget(Object node) {
        return null;
    }

    @Override
    protected Widget attachEdgeWidget(Object edge) {
        return null;
    }

    @Override
    protected void attachEdgeSourceAnchor(Object edge, Object oldSourceNode, Object newSourceNode) {

    }

    @Override
    protected void attachEdgeTargetAnchor(Object edge, Object oldTargetNode, Object newTargetNode) {

    }

    public Part getPart() {
        return _part;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables               ////
    private Part _part;   //Bundle of plates dropped into the Collection
    private ObjBaseObserver _obo;  //Listener for changes to plate list
    private LayerWidget layer;
    private Scene _scene;

    private static final int offset = 20;
}