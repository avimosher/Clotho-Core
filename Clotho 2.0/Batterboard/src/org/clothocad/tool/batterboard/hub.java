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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.Plate;
import org.clothocore.api.core.Collector;
import org.clothocore.api.plugin.ClothoTool;



/**
 *
 * @author J. Christopher Anderson
 */
public class hub implements ClothoTool {

    public void launch() {
        if(!Collector.isConnected()) {
            return;
        }
        new plateChooser();
    }

    public void launch(ObjBase o) {
        if(!Collector.isConnected()) {
            return;
        }
        Plate aplate = (Plate) o;
        if(aplate==null) {
            return;
        }
        frame aframe = new frame(aplate);
        guis.add(new WeakReference(aframe));
    }

    public void close() {
        for(WeakReference w: guis) {
            if(w.get()!=null) {
                frame aframe = (frame) w.get();
                aframe.dispose();
            }
        }
    }

    public void init() {
    }

/*-----------------
     variables
 -----------------*/
    static ArrayList<WeakReference> guis = new ArrayList<WeakReference>();
}
