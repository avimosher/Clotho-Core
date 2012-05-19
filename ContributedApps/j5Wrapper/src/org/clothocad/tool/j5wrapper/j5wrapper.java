package org.clothocad.tool.j5wrapper;

import java.io.FileNotFoundException;
import org.clothocore.api.data.ObjBase;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.apache.ws.commons.util.Base64.DecodingException;
import org.xml.sax.ContentHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.csv.CSVParser;
import org.apache.xmlrpc.XmlRpcException;
import org.clothocore.api.core.Collector;
import org.clothocore.api.data.Feature;
import org.clothocore.api.data.Format;
import org.clothocore.api.data.NucSeq;
import org.clothocore.api.data.ObjType;
import org.clothocore.api.data.Oligo;
import org.clothocore.api.data.Part;
import org.clothocore.api.data.Plasmid;
import org.clothocore.api.data.Vector;
import org.clothocore.util.basic.ImageSource;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.apache.xmlrpc.serializer.BaseXmlWriterFactory;
import org.apache.xmlrpc.serializer.XmlRpcWriter;
import org.xml.sax.SAXException;
import org.apache.ws.commons.util.Base64;
import org.clothocore.api.data.Collection;
import org.clothocore.util.chooser.ClothoOpenChooser;

/**
 *
 * @author Avi Robinson-Mosher
 */
public class j5wrapper extends javax.swing.JFrame implements ObjBaseDropTarget {

    private Collection _Collection;
    
    public j5wrapper(Collection collection) {
        super("j5 Wrapper");
        _Collection=collection;
        setIconImage(ImageSource.getTinyLogo());
        initComponents();
    }


    private void initComponents() {
        j5password="";
        j5username="";
        j5plasmidname="";
        _fileOpenerInstantiated=false;
        getContentPane().setBackground(navyblue);
        setLayout(new BorderLayout());
        Box headerPanel = new Box(BoxLayout.X_AXIS);
        headerPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(headerPanel, BorderLayout.NORTH);

            whiteLabel usernamelabel = new whiteLabel("Username:");
            headerPanel.add(usernamelabel);
            headerPanel.add(Box.createHorizontalStrut(2));

            final JTextField usernamefield = new JTextField();
            usernamefield.setPreferredSize(new Dimension(75,22));
            usernamefield.addFocusListener(new FocusAdapter(){
                @Override
                public void focusLost(FocusEvent e) {
                    if(!Collector.isConnected()) {
                        return;
                    }
                    String text = usernamefield.getText().trim();
                    if(text==null || text.equals("")) {
                        return;
                    }
                    j5username=text;
                }
            });
            headerPanel.add(usernamefield);
            headerPanel.add(Box.createHorizontalStrut(2));

            whiteLabel passwordlabel = new whiteLabel("Password:");
            headerPanel.add(passwordlabel);
            headerPanel.add(Box.createHorizontalStrut(2));

            final JPasswordField passwordfield = new JPasswordField();
            passwordfield.setPreferredSize(new Dimension(75,22));
            passwordfield.addFocusListener(new FocusAdapter(){
                @Override
                public void focusLost(FocusEvent e) {
                    if(!Collector.isConnected()) {
                        return;
                    }
                    String text = new String(passwordfield.getPassword());
                    if(text==null || text.equals("")) {
                        return;
                    }
                    j5password=text;
                }
            });
            headerPanel.add(passwordfield);
            headerPanel.add(Box.createHorizontalStrut(2));
            
            whiteLabel plasmidLabel = new whiteLabel("Plasmid name:");
            headerPanel.add(plasmidLabel);
            headerPanel.add(Box.createHorizontalStrut(2));
            
            final JTextField plasmidfield = new JTextField();
            plasmidfield.setPreferredSize(new Dimension(75,22));
            plasmidfield.addFocusListener(new FocusAdapter(){
                @Override
                public void focusLost(FocusEvent e) {
                    if(!Collector.isConnected()) {
                        return;
                    }
                    String text = plasmidfield.getText().trim();
                    if(text==null || text.equals("")) {
                        return;
                    }
                    j5plasmidname=text;
                }
            });
            headerPanel.add(plasmidfield);

        JPanel dominantPanel = new JPanel();
        dominantPanel.setOpaque(false);
        GridLayout gl = new GridLayout(1,2);
        gl.setHgap(5);
        dominantPanel.setLayout(gl);
        add(dominantPanel, BorderLayout.CENTER);
        
        Box leftpanel = new Box(BoxLayout.Y_AXIS);
        leftpanel.setBackground(Color.BLUE);
        dominantPanel.add(leftpanel);

        
        partsTable=new JTable();
        partsTable.setModel(new javax.swing.table.DefaultTableModel(
            new String [] {
                "UUID", "Part", "Start", "End"
            },
                0
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            @Override
            public int getRowCount(){
                return dataVector.size();
            }
            
            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        partsTable.setDragEnabled(true);
        partsTable.setEnabled(true);
        partsTable.setFillsViewportHeight(true);
        partsTable.setName("partsTable"); // NOI18N
        partsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        partsTable.removeColumn(partsTable.getColumnModel().getColumn(0));
        partsScrollPane=new javax.swing.JScrollPane();
        partsScrollPane.setViewportView(partsTable);

        //templateArea = new nucSeqEditor(templateSeq, this);
        partsScrollPane.setPreferredSize(new Dimension(250,180));
        partsScrollPane.setTransferHandler(new ObjBaseTransferHandler(this));
        leftpanel.add(partsScrollPane);

        JButton goButton = new JButton("Request design from j5");
        goButton.setPreferredSize(new Dimension(100,23));
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    runCalc();
                } catch (SAXException ex) {
                    Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
                }
            } });
        leftpanel.add(goButton);

        JButton oligoButton = new JButton("Add j5 oligos to collection");
        oligoButton.setPreferredSize(new Dimension(100,23));
        oligoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOligosFromFile();
            } });
        leftpanel.add(oligoButton);

        pack();
        //setVisible(true);
    }

    private String decodeAndPrint(String encoded)
    {
        byte[] decodedDesign=null;
        try {
            decodedDesign = Base64.decode(encoded);
        } catch (DecodingException ex) {
            Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        String decodedDesignString=new String(decodedDesign);
        System.out.println(decodedDesignString);
        return decodedDesignString;
    }
    
    private void addOligosFromFile() {
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Oligo File", "csv");
        if (!_fileOpenerInstantiated) {
            _fileOpener = new ClothoOpenChooser("Load j5 Oligos");
            _fileOpener.getFileChooser().addChoosableFileFilter(csvFilter);
            _fileOpener.getFileChooser().setFileFilter(_fileOpener.getFileChooser().getAcceptAllFileFilter());
            //_fileOpener.getFileChooser().setCurrentDirectory(_filePath);
            _fileOpener.setTitle("Open a j5 oligo file...");
            _fileOpenerInstantiated = true;
        }
        _fileOpener.open_Window();

        if (_fileOpener.fileSelected) {
            FileReader inFile=null;
            try {
                inFile = new FileReader(_fileOpener.getFile());
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            addCsvToCollection(inFile);
        }

    }
    
    private void runCalc() throws SAXException {
        XmlRpcClient client=new XmlRpcClient();
        XmlRpcClientConfigImpl config=new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL("http://j5.jbei.org/bin/j5_xml_rpc.pl"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        client.setConfig(config);

        HashMap struct=new HashMap(2);
        
        struct.put("username",j5username);
        struct.put("password",j5password);
                
        ArrayList params=new ArrayList();
        params.add(struct);
        ByteArrayOutputStream output=new ByteArrayOutputStream();
        client.setXmlWriterFactory(new BaseXmlWriterFactory());
        try {
            XmlRpcClientRequestImpl pRequest=new XmlRpcClientRequestImpl(config,"CreateNewSessionId",params);
            ContentHandler h = client.getXmlWriterFactory().getXmlWriter(config, output);
            XmlRpcWriter xw = new XmlRpcWriter(config, h, client.getTypeFactory());
            xw.write(pRequest);
            //output.close();
            //pStream = null;
            //client.getXmlWriterFactory().getXmlWriter(config,output);
            
        } catch (Exception ex) {
            Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(output.toString());
        String j5_session_id="";
        try {
            HashMap login = (HashMap)client.execute("CreateNewSessionId",params);
            System.out.println("login succeeded");
            j5_session_id=(String)login.get("j5_session_id");
            ArrayList filesParams=new ArrayList();
            filesParams.add(login);
            HashMap serverFiles=(HashMap)client.execute("GetLastUpdatedUserFiles",filesParams);
            
            decodeAndPrint(serverFiles.get("encoded_eugene_rules_list_file").toString());
            decodeAndPrint(serverFiles.get("encoded_j5_parameters_file").toString());
                        
                
            System.out.println(serverFiles.toString());
        } catch (XmlRpcException ex) {
            Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // build oligo file
        ArrayList<Oligo> inlist = (ArrayList<Oligo>) _Collection.getAll(ObjType.OLIGO);
        //ArrayList<Oligo> allOligos;
        //_data = new String [allOligos.size()][5];
        String oligoFile="Oligo Name,Length,Tm,Tm (3' only),Sequence\n";
        Iterator iterator=inlist.iterator();
        while(iterator.hasNext()){
            Oligo oligo=(Oligo)iterator.next();
            oligoFile=oligoFile.concat(oligo.getName()+","+oligo.getSeq().getSeq().length()+","+oligo.getSeq().meltingTemp()+","+oligo.getSeq().meltingTemp()+","+oligo.getSeq()+"\n");
        }
        
        
        // for a design query:
        // *parts_list_file: name "parts" relevant for this assembly.  Based on segments of sequences named in sequences_list_file
        // Part Name,Part Source (Sequence Display ID),Reverse Compliment?,Start (bp),End (bp)
        // Part name is the new declared name, Part Source is the name of the sequence it comes from
        // *target_part_order_list: places the parts defined in "parts_list_file" in order for the resulting sequence
        // (>Bin) or Part Name,Direction,Forced Assembly Strategy?,Forced Relative Overhang Position?,Direct Synthesis Firewall?
        // *sequences_list_file: names of the sequence files in zipped_sequences_file.  I believe GenBank, FASTA, and jbei-seq are allowed.
        // Sequence File Name,Format
        // *zipped_sequences_file: a file containing the sequences, probably in genbank format (is fasta okay?), zipped up together
        // j5_parameters_file: the j5 parameters.  Reusable.
        // eugene_rules_list_file: unimportant.  Reusable.
        // master_plasmids_file: plasmids that I've made (output of script goes here).  Just for seeing if we can steal sequence from them, or possibly record
        // Plasmid Name,Alias,Contents,Length,Sequence
        // "contents" is what it's made from; everything else more or less self-explanatory
        // master_oligos_list: this one we have under control.  Except I need something to get Tms.
        // Oligo Name,Length,Tm,Tm (3' only),Sequence
        // master_direct_syntheses_list: yep.
        // Direct Synthesis Name,Alias,Contents,Length,Sequence
        
        HashMap designAssemblyMap=new HashMap(23);
        ArrayList<String> sequenceFilenames=new ArrayList<String>();
        designAssemblyMap.put("j5_session_id",j5_session_id);
        designAssemblyMap.put("reuse_j5_parameters_file","TRUE");
        designAssemblyMap.put("encoded_j5_parameters_file","");
        designAssemblyMap.put("reuse_zipped_sequences_file","FALSE");
        designAssemblyMap.put("encoded_zipped_sequences_file",Base64.encode(buildZippedSequencesFile(sequenceFilenames)));
        designAssemblyMap.put("reuse_sequences_list_file","FALSE");
        designAssemblyMap.put("encoded_sequences_list_file",Base64.encode(buildSequenceFileNames(sequenceFilenames)));
        designAssemblyMap.put("reuse_parts_list_file","FALSE");
        designAssemblyMap.put("encoded_parts_list_file",Base64.encode(buildPartsListFile()));
        designAssemblyMap.put("reuse_target_part_order_list_file","FALSE");
        designAssemblyMap.put("encoded_target_part_order_list_file",Base64.encode(buildTargetPartOrderListFile()));
        designAssemblyMap.put("reuse_eugene_rules_list_file","TRUE");
        designAssemblyMap.put("encoded_eugene_rules_list_file","");
        designAssemblyMap.put("reuse_master_plasmids_file","TRUE");
        designAssemblyMap.put("encoded_master_plasmids_file","");
        designAssemblyMap.put("master_plasmids_list_filename","j5_plasmids_0.csv");
        designAssemblyMap.put("reuse_master_oligos_file","FALSE");
        designAssemblyMap.put("encoded_master_oligos_file",Base64.encode(oligoFile.getBytes()));
        designAssemblyMap.put("master_oligos_list_filename","j5_oligos_0.csv");
        designAssemblyMap.put("reuse_master_direct_syntheses_file","TRUE");
        designAssemblyMap.put("encoded_master_direct_syntheses_file","");
        designAssemblyMap.put("master_direct_syntheses_list_filename","j5_directsyntheses_0.csv");
        designAssemblyMap.put("assembly_method","SLIC/Gibson/CPEC");
        ArrayList designAssemblyParams=new ArrayList();
        designAssemblyParams.add(designAssemblyMap);
        String decodedDesignString="";
        try {
            HashMap designResult=(HashMap) client.execute("DesignAssembly",designAssemblyParams);
            try {
                String encoded_output_file=designResult.get("encoded_output_file").toString();
                byte[] decodedDesign=Base64.decode(encoded_output_file);
                decodedDesignString=new String(decodedDesign);

                System.out.println(decodedDesignString);
                ZipInputStream zipInput=new ZipInputStream(new ByteArrayInputStream(decodedDesignString.getBytes()));
                ZipEntry zipEntry;
                String constructionDirections="";
                Part newPart=null;
                try {
                    String entryString=null;
                    while((zipEntry=zipInput.getNextEntry())!=null){
                        entryString=zipEntry.toString();
                        System.out.println(entryString);
                        if("j5_oligos_0.csv".equals(entryString)){
                            addCsvToCollection(new InputStreamReader(zipInput));
                        }
                        else if(entryString.equals("masterplasmidlist.csv")){
                            newPart=addNewPlasmidToCollection(new InputStreamReader(zipInput));
                        }
                        else if(entryString.startsWith("pj5_")){
                            constructionDirections=buildPlasmidConstructionString(entryString,new InputStreamReader(zipInput));
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(newPart!=null){
                    newPart.changeShortDescription(constructionDirections);
                }
                
                
                //designAssemblyMap.put(prodArea, prodArea)
                //productSeq.changeSeq(p.getResult());
                //productSeq.changeSeq(p.getResult());
            } catch (DecodingException ex) {
                Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (XmlRpcException ex) {
            Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addCsvToCollection(Reader csvReader){
        try {
            // certainly a more efficient way to do this:
//            ArrayList<Oligo> allOligos = (ArrayList<Oligo>)_Collection.getAll(ObjType.OLIGO);
//            HashSet<String> oligoNames=new HashSet<String>();
//            Iterator<Oligo> oligoIterator=allOligos.iterator();
//            while(oligoIterator.hasNext()){
//                Oligo oligo=oligoIterator.next();
//                oligoNames.add(oligo.getName());
//            }

            // add oligos to collection
            // parse entryData as csv
            BufferedReader reader=new BufferedReader(csvReader);
            CSVParser csvParser=new CSVParser(reader);
            String[] parsedHeaders=csvParser.getLine();
            String[] line;
            while((line=csvParser.getLine())!=null){
                String name=line[0];
                if(Oligo.retrieveByName(name)!=null){
                    continue;
                }
                Oligo newOligo=new Oligo(name,name,_Collection.getAuthor(),line[4]);
                _Collection.addObject(newOligo);
            }
            _Collection.saveDefault();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private Part addNewPlasmidToCollection(Reader csvReader){
        Part newPart=null;
        try {
            // parse entryData as csv
            BufferedReader reader=new BufferedReader(csvReader);
            CSVParser csvParser=new CSVParser(reader);
            String[] parsedHeaders=csvParser.getLine();
            String[] line;
            String name=null,sequence=null;
            while((line=csvParser.getLine())!=null){
                name=line[0];
                sequence=line[4];
            }
            newPart=Part.generateBasic(j5plasmidname,j5plasmidname,sequence,Format.retrieveByName("FreeForm"),_Collection.getAuthor());
            _Collection.addObject(newPart);
            _Collection.saveDefault();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return newPart;
    }

    private byte[] buildSequenceFileNames(ArrayList<String> sequenceFileNames) {
        // *sequences_list_file: names of the sequence files in zipped_sequences_file.  I believe GenBank, FASTA, and jbei-seq are allowed.
        // Sequence File Name,Format
        String csv="Sequence File Name,Format\n";
        Iterator<String> filenames=sequenceFileNames.iterator();
        while(filenames.hasNext()){
            String filename=filenames.next();
            csv=csv+filename+",FASTA\n";
        }
        return csv.getBytes();
    }

    private byte[] buildZippedSequencesFile(ArrayList<String> sequenceFileNames) {
        // *zipped_sequences_file: a file containing the sequences, probably in genbank format (is fasta okay?), zipped up together
        ArrayList<Part> parts = (ArrayList<Part>) _Collection.getAll(ObjType.PART);
        ByteArrayOutputStream zippedBytes=new ByteArrayOutputStream();
        ZipOutputStream zipOutput=new ZipOutputStream(zippedBytes);
        Iterator<Part> pi=parts.iterator();
        while(pi.hasNext()){
            Part part=pi.next();
            String name=part.getName();
            name=name.replaceAll(" ", "_"); // fasta won't handle whitespace names
            String filename=name+".fasta";
            sequenceFileNames.add(filename);
            ZipEntry entry=new ZipEntry(filename);
            NucSeq seq=part.getSeq();
            String seqString=seq.getSeq();
            try {
                zipOutput.putNextEntry(entry);
                zipOutput.write((">"+name+"\n").getBytes());
                zipOutput.write(seqString.getBytes());
            } catch (IOException ex) {
                Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            zipOutput.close();
        } catch (IOException ex) {
            Logger.getLogger(j5wrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        String string=new String(zippedBytes.toByteArray());
        return zippedBytes.toByteArray();
    }

    private byte[] buildPartsListFile() {
        // *parts_list_file: name "parts" relevant for this assembly.  Based on segments of sequences named in sequences_list_file
        // Part Name,Part Source (Sequence Display ID),Reverse Compliment?,Start (bp),End (bp)
        // Part name is the new declared name, Part Source is the name of the sequence it comes from
        String csv="Part Name,Part Source (Sequence Display ID),Reverse Compliment?,Start (bp),End (bp)\n";
        //_Collection.get
        for(int i=0;i<partsTable.getModel().getRowCount();i++){
            String uuid=(String)partsTable.getModel().getValueAt(i, 0);
            Part part=(Part)Collector.get(ObjType.PART, uuid);
            String name=part.getName().replaceAll(" ", "_");
            csv+=name+","+name+",FALSE,1,"+part.getSeq().seqLength()+"\n";
        }
        //csv+="pET 30 a vector,pET_30_a,FALSE,1,5422\n";
        //csv+="caffeine insert,caffeine_operon,FALSE,1,2301\n";
        return csv.getBytes();
    }

    private byte[] buildTargetPartOrderListFile() {
        // *target_part_order_list: places the parts defined in "parts_list_file" in order for the resulting sequence
        // (>Bin) or Part Name,Direction,Forced Assembly Strategy?,Forced Relative Overhang Position?,Direct Synthesis Firewall?
        String csv="(>Bin) or Part Name,Direction,Forced Assembly Strategy?,Forced Relative Overhang Position?,Direct Synthesis Firewall?\n";
        for(int i=0;i<partsTable.getModel().getRowCount();i++){
            String uuid=(String)partsTable.getModel().getValueAt(i, 0);
            Part part=(Part)Collector.get(ObjType.PART, uuid);
            String name=part.getName().replaceAll(" ", "_");
            csv+=name+",,,\n";
        }
        return csv.getBytes();
    }
    
    public void openTab() {
        final JComponent guiContentPane=(JComponent)getContentPane();
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                TopComponent tc=new TopComponent();
                tc.setLayout(new BorderLayout());
                JScrollPane sp=new JScrollPane(guiContentPane);
                tc.add(sp,BorderLayout.NORTH);
                tc.setName("j5wrapper");
                tc.open();
                tc.requestActive();
            }
        });
    }

    @Override
    public void handleDroppedObject(ObjBase o) {

        String seq = "";
        Part part=null;
        if (o.getType().equals(ObjType.PART)) {
            part=(Part)o;
            seq = ((Part) o).getSeq().toString();
        } else if (o.getType().equals(ObjType.OLIGO)) {
            seq = ((Oligo) o).getSeq().toString();
        } else if (o.getType().equals(ObjType.FEATURE)) {
            seq = ((Feature) o).getSeq().toString();
        } else if (o.getType().equals(ObjType.VECTOR)) {
            seq = ((Vector) o).getSeq().toString();
        } else if (o.getType().equals(ObjType.PLASMID)) {
            seq = ((Plasmid) o).getSeq().toString();
        } else if (o.getType().equals(ObjType.NUCSEQ)) {
            seq = ((NucSeq) o).getSeq().toString();
        } else {
            JOptionPane.showMessageDialog(null, "Sequence View doesn't support drag and drop with Clotho " + o.getType(), "Sequence View: Drag and Drop", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] options = {"Insert", "Replace", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, "A Clotho object has been dropped.\nWhat do you want to do with it?", "SequenceView: Drag and Drop", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, "insert");

        if (option == 0) {
            addPart(part);
            //JTextPane ta = _sequenceArea;
            //String currentText = ta.getText();
            //ta.setText(currentText.substring(0, ta.getCaretPosition()) + seq + currentText.substring(ta.getCaretPosition()));
            //((SequenceViewGUI) _sequenceview).getOutputTextArea().setText("Inserted the sequence of Clotho " + o.getType() + ": " + o.getName());
            //sequenceChanged();
        }/* else if (option == 1) {
            JTextPane ta = _sequenceArea;
            ta.setText(seq);
            ((SequenceViewGUI) _sequenceview).getOutputTextArea().setText("Loaded Clotho " + o.getType() + ": " + o.getName());
            sequenceChanged();
        }*/
    }

    public void addPart(Part part) {
        String seq = part.getSeq().toString();
        String name = part.getName();
        String uuid = part.getUUID();

        int lastRow=partsTable.getModel().getRowCount();
        ((DefaultTableModel)partsTable.getModel()).setRowCount(lastRow+1);
        partsTable.getModel().setValueAt(uuid, lastRow, 0);
        partsTable.getModel().setValueAt(name, lastRow, 1);
        partsTable.getModel().setValueAt(1, lastRow, 2);
        partsTable.getModel().setValueAt(seq.length(), lastRow, 3);
        
    }

    public enum PlasmidConstructionSection{
        None,PCRs,Assemblies
    }
    
    private String buildPlasmidConstructionString(String filename,Reader csvReader) {
        String directions="";
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter("/tmp/"+filename));
            // add oligos to collection
            // parse entryData as csv
            BufferedReader reader=new BufferedReader(csvReader);
            CSVParser csvParser=new CSVParser(reader);
            String[] line;
            PlasmidConstructionSection section=PlasmidConstructionSection.None;
            while((line=csvParser.getLine())!=null){
                bw.write(line[0]);
                for(int i=1;i<line.length;i++){
                    bw.write(",");
                    bw.write(line[i]);
                }
                bw.newLine();
                
                if(line.length==0){
                    section=PlasmidConstructionSection.None;
                    continue;
                }
                String name=line[0];
                if(name.startsWith("ID Number")){
                    continue;
                }
                if(name.startsWith("PCR Reactions")){
                    section=PlasmidConstructionSection.PCRs;
                    directions+="PCR Reactions\n";
                }
                else if(name.startsWith("Assembly Pieces")){
                    section=PlasmidConstructionSection.Assemblies;
                    directions+="Assembly Pieces\n";
                }
                else if(section==PlasmidConstructionSection.PCRs){
                    directions+=name;
                    for(int i=1;i<15;i++){
                        directions+=","+line[i];
                    }
                    directions+="\n";
                }
                else if(section==PlasmidConstructionSection.Assemblies){
                    directions+=name;
                    //for(int i=1;i<11;i++){
                    //    directions+=","+line[i];
                    //}
                    //directions+="\n";                    
                }
            }
            bw.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return directions;
    }

    private class whiteLabel extends JLabel {
        public whiteLabel(String name) {
            super(name);
            init();
        }
        private void init() {
            setForeground(Color.WHITE);
            setFont(arialItal12);
        }
    }
/*-----------------
     variables
 -----------------*/
    private static final Color navyblue = new Color(35, 48, 64);
    private static final Font arialItal12 = new Font("Arial", Font.ITALIC, 12);

    public javax.swing.JTable partsTable;
    private javax.swing.JScrollPane partsScrollPane;

    String j5username;
    String j5password;
    String j5plasmidname;
    private ClothoOpenChooser _fileOpener;
    boolean _fileOpenerInstantiated;

}
