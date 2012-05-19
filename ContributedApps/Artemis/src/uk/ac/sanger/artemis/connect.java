package uk.ac.sanger.artemis;

import java.awt.Font;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
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
import uk.ac.sanger.artemis.components.ArtemisMain;
import uk.ac.sanger.artemis.components.EntryEdit;
import uk.ac.sanger.artemis.io.ClothoEntry;
import uk.ac.sanger.artemis.io.EntryInformation;
import uk.ac.sanger.artemis.io.EntryInformationException;
import uk.ac.sanger.artemis.io.GenbankDocumentEntry;
import uk.ac.sanger.artemis.io.PartialSequence;
import uk.ac.sanger.artemis.io.Sequence;
import uk.ac.sanger.artemis.io.SimpleDocumentEntry;
import uk.ac.sanger.artemis.io.SimpleEntryInformation;
import uk.ac.sanger.artemis.sequence.Bases;
import uk.ac.sanger.artemis.sequence.NoSequenceException;
import uk.ac.sanger.artemis.util.Document;
import uk.ac.sanger.artemis.util.FileDocument;
import uk.ac.sanger.artemis.util.OutOfRangeException;

/**
 *
 * @author J. Christopher Anderson
 */
public class connect implements ClothoTool {

    public void launchEntry(Entry entry)
    {
        final Bases bases = entry.getBases();
        final EntryGroup entry_group = new SimpleEntryGroup(bases);
        entry_group.add(entry);
        final EntryEdit entry_edit = new EntryEdit(entry_group);
        entry_edit.openTab();    
    }
    
    @Override
    public void launch() {
        try {
            EntryInformation entry_information=new SimpleEntryInformation();
            Document document=new FileDocument(new File("/Users/avir/Documents/constructs/ordered_sequences/pclipf.gb"));
            uk.ac.sanger.artemis.io.Entry embl_entry=new GenbankDocumentEntry(entry_information,document,null);
            Entry entry=new Entry(embl_entry);
            launchEntry(entry);
            //ArtemisMain main_window=new ArtemisMain(null);
            //main_window.setVisible(true);
    /*        if (!Collector.isConnected()) {
                JOptionPane.showMessageDialog(null, "Database connection required to launch this tool",
                        "Not connected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Preferences _prefs = Preferences.userNodeForPackage(j5wrapper.class);
            String uuid = _prefs.get("launchCollection", "");

            Collection launchcoll=null;// = Collector.getCollection(uuid);
            if(launchcoll==null) {
                launchcoll = Collector.getCurrentUser().getHerCollection();
            }
            if(launchcoll==null) {
                launchcoll=new Collection();
            }

            j5wrapper c = new j5wrapper(launchcoll);
            c.openTab();
            //guis.add(new WeakReference(c));*/
        } catch (OutOfRangeException ex) {
            Logger.getLogger(connect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSequenceException ex) {
            Logger.getLogger(connect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(connect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EntryInformationException ex) {
            Logger.getLogger(connect.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            ClothoEntry clothoEntry=new ClothoEntry(part);
            Entry entry=new Entry(clothoEntry);
            launchEntry(entry);
//            j5wrapper c = new j5wrapper(Collector.getCurrentUser().getHerCollection());
//            c.addPart(part);
//            guis.add(new WeakReference(c));
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