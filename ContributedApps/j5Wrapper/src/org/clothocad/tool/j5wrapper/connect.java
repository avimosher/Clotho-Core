package org.clothocad.tool.j5wrapper;

import java.awt.Window;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.clothocore.api.core.Collector;
import org.clothocore.api.data.Collection;
import org.clothocore.api.data.Feature;
import org.clothocore.api.data.NucSeq;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.Oligo;
import org.clothocore.api.data.Part;
import org.clothocore.api.data.Plasmid;
import org.clothocore.api.data.Vector;
import org.clothocore.api.plugin.ClothoTool;

/**
 *
 * @author J. Christopher Anderson
 */
public class connect implements ClothoTool {

    @Override
    public void launch() {
        if (!Collector.isConnected()) {
            JOptionPane.showMessageDialog(null, "Database connection required to launch this tool",
                    "Not connected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Preferences _prefs = Preferences.userNodeForPackage(j5wrapper.class);
        String uuid = _prefs.get("launchCollection", "");

        Collection launchcoll=null;// = Collector.getCollection(uuid);
        if(launchcoll==null) {
//            Collection prexistingSeq = Collection.retrieveByName("Lev Shaket's collection");
//            Collector.getCurrentUser().setHerCollection(prexistingSeq);
//            Collector.getCurrentUser().setDatumChangeStatus(true);
//            Collector.getCurrentUser().save(Collector.getDefaultConnection());
            launchcoll = Collector.getCurrentUser().getHerCollection();
        }
        if(launchcoll==null) {
            launchcoll=new Collection();
        }

        j5wrapper c = new j5wrapper(launchcoll);
        c.openTab();
        guis.add(new WeakReference(c));
    }

    @Override
    public void launch(ObjBase o) {
        if (!Collector.isConnected()) {
            JOptionPane.showMessageDialog(null, "Database connection required to launch this tool",
                    "Not connected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Part part=null;
            NucSeq template = null;
            switch(o.getType()) {
                case PART:
                    template = ((Part) o).getSeq();
                    part=(Part)o;
                    break;
                case VECTOR:
                    template = ((Vector) o).getSeq();
                    break;
                case PLASMID:
                    template = ((Plasmid) o).getSeq();
                    break;
                case NUCSEQ:
                    template = (NucSeq) o;
                    break;
                case FEATURE:
                    template = ((Feature) o).getSeq();
                    break;
                case OLIGO:
                    template = ((Oligo) o).getSeq();
                    break;
                default:
                    return;
            }
            j5wrapper c = new j5wrapper(Collector.getCurrentUser().getHerCollection());
            c.addPart(part);
            guis.add(new WeakReference(c));
        } catch(Exception e) {
        }
    }

    @Override
    public void close() {
        for(WeakReference<Window> wrw: guis) {
            Window w = wrw.get();
            if(w!=null) {
                w.dispose();
            }
        }
    }

    @Override
    public void init() {
    }

/*-----------------
     variables
 -----------------*/
    private ArrayList<WeakReference<Window>> guis = new ArrayList<WeakReference<Window>>();
}