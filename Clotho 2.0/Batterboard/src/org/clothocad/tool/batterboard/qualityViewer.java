/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clothocad.tool.batterboard;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JPanel;

import org.clothocore.api.data.Container;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.data.PlasmidSample;
import org.clothocore.api.data.Plate;
import org.clothocore.api.data.PlateType;
import org.clothocore.api.data.Sample;
import org.clothocore.api.data.Format;
import org.clothocore.api.data.NucSeq;

import org.clothocore.api.dnd.RefreshEvent;
import org.clothocore.util.basic.ObjBasePopup;

import net.iharder.dnd.FileDrop;

import org.clothocore.api.dnd.ObjBaseObserver;

import org.clothocad.tool.batterboard.sequence.fileDropHandler;


/**
 *
 * @author jcanderson
 */
public class qualityViewer extends JPanel implements ObjBaseObserver {

    public qualityViewer(Plate aplate, popupPanel pp, frame f) {
        setLayout(null);
        _plate = aplate;
        _frame = f;
        _popup = pp;
        _plate.isRepresentedBy(this, this);

        ObjBasePopup app = new ObjBasePopup(this, _plate);

        PlateType pt = _plate.getPlateType();
        plateImage = pt.getPlateImage();

        squareWells = pt.isSquareWells();
        if(squareWells) {
            wellWidth = pt.getSquareWellWidth();
            wellHeight = pt.getSquareWellHeight();
        } else {
            diameter = pt.getWellDiameter();
        }

        numrows = pt.getNumRows();
        numcols = pt.getNumCols();
        xpos = pt.getWellPositionX();
        ypos = pt.getWellPositionY();
        wellComponents = new well[numrows][numcols];

        new  FileDrop( this, new FileDrop.Listener() {
                    @Override
                    public void  filesDropped( java.io.File[] file ) {
                        zipDropFiles(file[0]);
                    }});

        //Calculate initial centers of all the wells
        int width = plateImage.getWidth();
        int height = plateImage.getHeight();

        Dimension dims = new Dimension(plateImage.getWidth(), plateImage.getHeight());
        setPreferredSize(dims);
    }

    @Override
    public void paintComponent(Graphics g) {
        g2 = (Graphics2D) g.create();
        regularcomp = g2.getComposite();

        g2.drawImage(plateImage, 0, 0, this);

        if(!initiated) {
            _frame.plateLoadingThread = new Thread() {
                @Override
                public void run() {
                    createWellShapes();
                    createContainerLinks();
                    repaint();
                }
            };
            _frame.plateLoadingThread.start();
            return;
        }

        for(int i=0; i< numrows; i++) {
            for(int j=0; j<numcols; j++) {
                g2.setColor(Color.red);
                paintUnselected(i, j);
                paintSelected();
            }
        }
    }

    protected void createWellShapes() {
        for(int i=0; i< numrows; i++) {
            for(int j=0; j<numcols; j++) {
                wellComponents[i][j] = new well();
                if(squareWells) {
                    wellComponents[i][j]._shape = new Rectangle2D.Double(xpos[i][j], ypos[i][j], wellWidth, wellHeight);
                } else {
                    wellComponents[i][j]._shape = new Ellipse2D.Double(xpos[i][j], ypos[i][j], diameter, diameter);
                    System.out.println("xpos["+i+"]["+j+"= "+xpos[i][j]);
                    System.out.println("ypos["+i+"]["+j+"= "+ypos[i][j]);
                }
                final int ii=i;
                final int jj=j;
                new  FileDrop( wellComponents[i][j], new FileDrop.Listener() {
                    @Override
                    public void  filesDropped( java.io.File[] files ) {
                        dropFiles(ii, jj, files);
                    }});
                xclick.put(wellComponents[i][j]._shape, i);
                yclick.put(wellComponents[i][j]._shape, j);
                regions.add(wellComponents[i][j]._shape);
            }
        }
        initiated = true;
    }

    protected void createContainerLinks() {
        System.out.println("\n\n\n******Creating container links");
        for(int i=0; i< numrows; i++) {
            for(int j=0; j<numcols; j++) {
                Container acon = _plate.getContainerAt(i, j);
                if(acon==null) {
                    continue;
                }
                Sample asam = acon.getSample();
                if(asam==null) {
                    acon.isRepresentedBy(this, wellComponents[i][j]);
                    ObjBasePopup app = new ObjBasePopup(wellComponents[i][j], acon);
                    add(wellComponents[i][j]);
                    wellComponents[i][j].setBounds(wellComponents[i][j]._shape.getBounds());
                    wellComponents[i][j].isSample = false;
                    continue;
                }
                asam.isRepresentedBy(this, wellComponents[i][j]);
                ObjBasePopup app = new ObjBasePopup(wellComponents[i][j], asam);
                add(wellComponents[i][j]);
                wellComponents[i][j].setBounds(wellComponents[i][j]._shape.getBounds());
                wellComponents[i][j].isSample = true;
                wellComponents[i][j]._sample = asam;
            }
        }
    }

    /*public void refreshData(RefreshEvent evt) {
        initiated = false;
        repaint();
    }*/


    //Added; not sure if correct
    @Override
    public void update(ObjBase obj, RefreshEvent evt) {
        initiated = false;
        repaint();
    }

    /**
     * Method for painting the selected well
     * @param ashape the circle or rectanlge of the well
     * @param g2 the Graphics2D for painting it
     */
    protected void paintSelected() {
        g2.setComposite(alphacomp);
        g2.setColor(Color.green);
        for(well acom: selected) {
            g2.draw(acom._shape);

        }
    }
    /**
     * this is a complete hack to figure out the last container
     * that has been "selected" by the user. There must be some better
     * way to do this really.
     * @return Container which is the last selected one.
     */
    public Container getLastSelectedContainer()
    {
        System.out.println("Comes into getlastselectedContainer");
        Container lastContainer=null;
        well last = selected.get(selected.size()-1);
        Shape s =last._shape;
        Rectangle2D r= s.getBounds2D();
        double minDistance = 100.0;
        System.out.println("Center = "+r.getCenterX()+","+r.getCenterY());
        for (int i= 0; i< numrows; i++)
        {
            for (int j=0; j < numcols ; j++) {
                System.out.println(xpos[i][j]+","+ypos[i][j]);
                double distance =Point.distance(xpos[i][j], ypos[i][j],r.getCenterX(),r.getCenterY());
                if (distance < minDistance ) {
                  lastContainer = _plate.getContainerAt(i, j);
                  minDistance =distance;
                  System.out.println("found: "+i+ " "+j);
                }
           }
        }
        if (lastContainer ==null)
            System.err.println("didnt find last container!");
        return lastContainer;

    }
    /**
     * Method for painting an unselected well
     * @param ashape the circle or rectangle of the well
     * @param g2 the Graphics2D for painting it
     */
    protected void paintUnselected(int i, int j) {
        Component acom = wellComponents[i][j];
        Container acon = _plate.getContainerAt(i, j);
        if(acon==null) {
            return;
        }
        Sample asam = acon.getSample();
        if(asam==null) {
            return;
        }

        try {
            PlasmidSample ps = (PlasmidSample) asam;
            short quality = ps.getQuality();

            g2.setComposite(alphacomp);
            g2.setColor(getColorForQuality(quality));
            acom.paint(g2);


        } catch (java.lang.Exception e) {
            return;
        }


    }

    private static Color getColorForQuality(short qual) {
        switch(qual) {
            case 0:
                return Color.red;
            case 1:
                return Color.blue;
            case 2:
                return Color.yellow;
            case 3:
                return Color.yellow;
            case 4:
                return Color.green;
            case 5:
                return Color.green;
            default:
                return Color.gray;
        }
    }

    private void dropFiles(int row, int col, java.io.File[] files) {
        if(_plate!=null) {
            if(_plate.getContainerAt(row, col)!=null) {
                Sample asam = _plate.getContainerAt(row, col).getSample();
                PlasmidSample p;
                try{
                    p=(PlasmidSample)asam;
                    String author=p.getAuthor().getGivenName();
                    String sampleName=p.getName();
                    Format f = p.getPlasmid().getFormat();
                    NucSeq ns = f.generateSequencingRegion(p.getPlasmid());
                    fileDropHandler.drop(files, ns, author, sampleName);
                }
                catch(ClassCastException e){
                    System.out.println("sample is not a plasmid");
                }
                
            }
        }

    }
    private void zipDropFiles(java.io.File file) {
        System.out.println("in zipDropFiles");
        if(_plate!=null){
            int r =_plate.getNumRows();
            int c=_plate.getNumCols();
            ArrayList<NucSeq> seqs=new ArrayList<NucSeq>();
            ArrayList<String> names=new ArrayList<String>();
            /*for(int i=0; i<r; i++){
                for(int j=0; j<c; j++){
                    Sample asam = _plate.getContainerAt(i, j).getSample();
                    PlasmidSample p;
                    try{
                        
                        p=(PlasmidSample)asam;
                        Format f = p.getPlasmid().getFormat();
                        NucSeq ns = f.generateSequencingRegion(p.getPlasmid());
                        seqs.add(ns);
                        System.out.println("added seq");
                    }
                    catch(ClassCastException e){
                        System.out.println("Sample is not a plasmid");
                    }
                }
            }*/
            fileDropHandler.zipDrop(file, seqs);
        }
        

    }



    protected class well extends Component {

        public well() {
            final well myself = this;
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if(e.getClickCount()==1 && e.getModifiers() == 16) {
                        if(isSelected) {
                            selected.remove(myself);
                            isSelected = false;
                        } else {
                            selected.add(myself);
                            isSelected = true;
                        }
                        pview.repaint();
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    if(isSample) {
                        _popup.initpopup(_sample, _shape.getBounds().x, _shape.getBounds().y, _shape.getBounds().width);
                    }
                }

                public void mouseExited(MouseEvent e) {
                    if(isSample) {
                        _popup.clearPopup();
                    }
                }
            }  );
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.fill(_shape);
        }

        //Variables//
        public Shape _shape;
        public boolean isSample = false;
        public Sample _sample;
        public boolean isSelected = false;
    }
/**-----------------
    variables
-----------------*/
    protected qualityViewer pview = this;
    protected Plate _plate;
    private popupPanel _popup;

    protected well[][] wellComponents;
    protected ArrayList<Shape> regions = new ArrayList<Shape>();
    protected ArrayList<well> selected = new ArrayList<well>();
    protected boolean initiated;

    protected boolean squareWells;
    protected int diameter;
    protected int wellWidth;
    protected int wellHeight;

    protected Graphics2D g2;
    protected int numrows;
    protected int numcols;

    protected Composite regularcomp;
    protected Composite alphacomp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,  0.4f);

    protected Hashtable<Shape, Integer> xclick = new Hashtable<Shape, Integer>();
    protected Hashtable<Shape, Integer> yclick = new Hashtable<Shape, Integer>();

    protected static int[][] xpos;
    protected static int[][] ypos;
    
    protected static BufferedImage plateImage;

    protected enum directions { LEFT, RIGHT, UP, DOWN };
    private frame _frame;

}
