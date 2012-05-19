/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clothocad.tool.batterboard;

import org.apache.poi.hssf.usermodel.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.clothocore.api.data.*;
/**
 *ImportExport Class. Used to Import plate date from MS-EXCEL worksheets, and
 * Export Plate Data to excel worksheet(s).
 * @author Rishi Ganguly
 */
public class ImportExport {

    public ImportExport(Plate plate)
    {
        myPlate =plate;
    }

    //used for testing only
    public ImportExport()
    {
       this.myPlate = null;
    }

    /**
     * Export data from a plate to an excel file.
     * @param filename Name of the excel file
     */
    public void exportToExcel(String filename)
    {
        try
        {
        /** open up the excel file to write data on it**********************/
        File f = new File(filename);
        f.createNewFile();
        HSSFWorkbook wb   = new HSSFWorkbook();
        FileOutputStream fileOut = new FileOutputStream(f);
        HSSFSheet sheet = wb.createSheet();

        /** write the headers ***********************************/

        HSSFRow myrow = sheet.createRow(0);
       // String[] headers = {"Row","Col","Container Name","BarCode","Sample Name","Sample Type",	"Quality","Concentration","Volume","Plasmid","Strain","Author","Enzyme1","Enzyme2","Frozen-Stock"};
        for (int i= 0 ; i < headerNames.length; i++)
        {
            HSSFCell cell   = myrow.createCell(i);
            cell.setCellValue(new HSSFRichTextString(headerNames[i]));
        }
        /** read data out of the plate*************************************/
        short rowCount =1;
        for( int row = 0 ; row < myPlate.getNumRows();row++)
        {
            for (int col = 0; col < myPlate.getNumCols(); col ++)
            {
                String containerName,barCode,sampleType,sampleName,quality,conc,vol,plasmid,strain,author,enzyme1,enzyme2;
                Container container =  myPlate.getContainerAt(row, col);
                if (container !=null) {
                   containerName = (container.getName()==null || container.getName().length()==0)? "?":container.getName();
                   barCode = (container.getBarcode() == null || container.getBarcode().length()==0) ? "?" : container.getBarcode();

                   PlasmidSample ps = (PlasmidSample) container.getSample();
                   if (ps!=null) {
                       sampleType = ""+ps.getSampleType().name();
                       sampleName = (ps.getName()==null || ps.getName().length()==0)? "?":ps.getName();
                       quality = ""+ps.getQuality();
                       conc = ""+ps.getConcentration();
                       vol = ""+ps.getVolume();
                       plasmid = ps.getPlasmid().getName();
                       strain = ps.getSourceStrain().getName();
                       char ro = (char) ('A' +  row);
                       author = ps.getAuthor().getName();

                       //if this is a gel sample we need to extract the two enzymes (enzyme1 and 2) from search tag table
                       ArrayList<String> tags = ps.getSearchTags();
                       boolean isGelSample = false;
                       enzyme1="?"; enzyme2="?";
                       for (int i = 0; i < tags.size(); i++)
                       {
                           if (tags.get(i).startsWith("enzyme1"))
                           {
                               isGelSample = true;
                               enzyme1 = tags.get(i).substring(8);
                           }
                           else if (tags.get(i).startsWith("enzyme2"))
                           {
                               isGelSample = true;
                               enzyme2 = tags.get(i).substring(8);
                           }

                       }
                       //if this is a frozen-stock put in appropriate entry..
                       String frozen_stock ="NO";
                       for (int g = 0; g < tags.size(); g++)
                       {
                           if (tags.get(g).startsWith("frozen-stock"))
                           {
                               frozen_stock = "YES";
                               break;
                           }
                       }

                       writeRowToExcel(sheet,rowCount,Character.toString(ro),""+(col+1),containerName,barCode,sampleName,sampleType,quality,conc,vol,plasmid,strain,author,enzyme1,enzyme2,frozen_stock);
                       rowCount++;
                   }
                    
                }
            }
        }
        wb.write(fileOut);
        fileOut.close();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,"Error!\n"+e.getMessage());
        }
        JOptionPane.showMessageDialog(null,"Export completed to file : "+filename);
    }

    private void writeRowToExcel(HSSFSheet sheet,short rowCount,String row,String col, String containerName,String barCode,String sampleName,String sampleType, String quality, String conc, String vol, String plasmid,String strain,String author,String enzyme1,String enzyme2,String frozen_stock)
    {
       // System.out.println("EXPORT OUTPUT:");
       // System.out.println(row + col + "container name :"+ containerName + "barcode : "+ barCode+ "sampleName : "+ sampleName +" sampleType : "+ sampleType + "quality : "+ quality + "conc :" + conc + "vol : "+ vol +"plasmid : "+ plasmid + "strain : " +strain);
       String msg = row + col + "container name :"+ containerName + "barcode : "+ barCode+ "sampleName : "+ sampleName +" sampleType : "+ sampleType + "quality : "+ quality + "conc :" + conc + "vol : "+ vol +"plasmid : "+ plasmid + "strain : " +strain;
       String [] args = {row,col, containerName,barCode,sampleName,sampleType,quality,conc,vol,plasmid,strain,author,enzyme1,enzyme2,frozen_stock};
      // JOptionPane.showMessageDialog(null, msg);

        HSSFRow myrow = sheet.createRow(rowCount);
        for (int i= 0 ; i < args.length; i++)
        {
            HSSFCell cell   = myrow.createCell(i);
            cell.setCellValue(new HSSFRichTextString(args[i])); 
        }

    }


    /**
     * Import data for a plate from an excel file, and populates plate
     * object with it.
     * @param filename
     */
    public void importFromExcel(File filename)
    {
        FileInputStream fis =null;
        try
        {
        fis =new FileInputStream(filename);

        //create workbook
        HSSFWorkbook workbook = new HSSFWorkbook(fis);
        HSSFSheet sheet = workbook.getSheetAt(0);

        Iterator rows =sheet.rowIterator();
        int rowCount =0;
        while (rows.hasNext()){
            HSSFRow row = (HSSFRow)rows.next();
            Iterator cells =row.cellIterator();
            List data =new ArrayList();
            int cellCount=0;
            while (cells.hasNext()){
                HSSFCell cell =(HSSFCell) cells.next();
                data.add(cell);
                cellCount++;
            }
            if (rowCount==0)//if it is the first row, check for headings.
               checkHeader(data);
            else
               makeSample(data);
            rowCount++;
        }
        }
        catch(Exception e) {

           System.err.println( e.getMessage());
           e.printStackTrace();
           JOptionPane.showMessageDialog( null, "OOPS! there were error(s) while importing: check the red colored messages!", "Error", JOptionPane.ERROR_MESSAGE );
            this.errorFlag=true;
        }

        JOptionPane.showMessageDialog(null, "Import from file completed");
    }
    /**
     * Checks the header of the imported excel file, if it
     * matches the standards set
     * @param data: list of headers
     * @throws IllegalArgumentException
     */
    private void checkHeader(List data) throws IllegalArgumentException
    {
        //first check number of headers in the excel file.
        if (data.size() != headerNames.length) {
           this.errorFlag = true;
           throw new IllegalArgumentException("Error Reading Header in excel file.Please Check if ALL headers are present");
        }

        //next, check if the header names match convention.
        for (int i =0; i <data.size();i++)
        {
            HSSFCell cell = (HSSFCell) data.get(i);
            String header = cell.getStringCellValue();
            if (!(headerNames[i].equals(header)))
                throw new IllegalArgumentException("Error Reading Header in excel File. Please Check header #"+i+" Expected:"+headerNames[i]+", but found :"+header);
        }
    }
    /**
     * Makes a sample object and puts it in the corresponding Well of the plate
     * @param data list of properties extracted from a row in
     * excel file for making a sample object
     * @throws IllegalArgumentException
     */
    private void makeSample(List data) throws IllegalArgumentException
    {
       if (data.size()!=headerNames.length)
       {
           return;
       }

        HSSFCell plasmid = (HSSFCell)data.get(header.PLASMID.ordinal());
        HSSFCell strain = (HSSFCell)data.get(header.STRAIN.ordinal());
        HSSFCell person = (HSSFCell)data.get(header.AUTHOR.ordinal());
        HSSFCell enz1 = (HSSFCell)data.get(header.ENZYME1.ordinal());
        HSSFCell enz2 = (HSSFCell)data.get(header.ENZYME2.ordinal());
        HSSFCell frozen_stock = (HSSFCell)data.get(header.FROZEN_STOCK.ordinal());

        validatePlasmid(plasmid.getStringCellValue());
        validateStrain(strain.getStringCellValue());

        //retrieve plasmid
        Plasmid myPlasmid = Plasmid.retrieveByName(plasmid.getStringCellValue());
        if (myPlasmid ==null)
        {
            this.errorFlag =true;
            throw new IllegalArgumentException("ImportExport: ERROR, plasmid "+plasmid.getStringCellValue()+" cannot be found in DB!");
        }

        //retrieve strain
        Strain myStrain = Strain.retrieveByName(strain.getStringCellValue());
        if (myStrain ==null)
        {
           this.errorFlag =true;
           throw new IllegalArgumentException("ImportExport: ERROR, strain "+strain.getStringCellValue()+" cannot be found in DB!");

        }

        //retrieve author
        Person myPerson = Person.retrieveByName(person.getStringCellValue());
        if (myPerson == null)
        {
            this.errorFlag =true;
            throw new IllegalArgumentException("ImportExport:ERROR, Person :"+person.getStringCellValue()+" cannot be found in DB!");

        }

        //retrieve container given the row and col.
        HSSFCell rowCell =(HSSFCell)data.get(header.ROW.ordinal());
        rowCell.setCellType(Cell.CELL_TYPE_STRING);
        int row = rowCell.getStringCellValue().charAt(0) - 65;
        HSSFCell colCell = (HSSFCell)data.get(header.COL.ordinal());
        colCell.setCellType(Cell.CELL_TYPE_STRING);
        int col = Integer.parseInt(colCell.getStringCellValue()) -1;
        Container myContainer = this.myPlate.getContainerAt(row, col);
        if (myContainer ==null)
        {
           this.errorFlag = true;
           throw new IllegalArgumentException("ImportExport: ERROR, cannot retrieve container for row = "+row+" column = "+col);

        }

        //retrieve volume
        HSSFCell volCell = (HSSFCell)data.get(header.VOL.ordinal());
        volCell.setCellType(Cell.CELL_TYPE_STRING);
        double vol = Double.parseDouble(volCell.getStringCellValue());

        //retrieve quality, concentration
        HSSFCell qcell = (HSSFCell)data.get(header.QUALITY.ordinal());
        qcell.setCellType(Cell.CELL_TYPE_STRING);
        int quality =Integer.parseInt(qcell.getStringCellValue());

        HSSFCell ccell = (HSSFCell)data.get(header.CONC.ordinal());
        ccell.setCellType(Cell.CELL_TYPE_STRING);
        double conc =Double.parseDouble(ccell.getStringCellValue());

        //if this is a gel sample, then retrieve enzyme1 and enzyme2
        String enzyme1 = enz1.getStringCellValue();
        String enzyme2 = enz2.getStringCellValue();

        //retrieve container name plasmid name and barcode
        HSSFCell containerNameCell = (HSSFCell)data.get(header.CONTAINERNAME.ordinal());
        String newContainerName = containerNameCell.getStringCellValue();
        HSSFCell sampleNameCell = (HSSFCell)data.get(header.SAMPLENAME.ordinal());
        HSSFCell barcodeCell = (HSSFCell)data.get(header.BARCODE.ordinal());
        String newBarcode = barcodeCell.getStringCellValue();

        /** Create a Plasmid sample. For now I am assuming that sample type is only plasmid sample
         * this needs to be changed in subsequent revisions..
         */
       // Person person =this.myPlate.getAuthor();
        PlasmidSample pls = (PlasmidSample)myContainer.getSample();
        if (pls!=null)
        {
           int opt = JOptionPane.showConfirmDialog(null,"Overwrite Row = "+rowCell.getStringCellValue()+" Col = "+col+" ?");
            if (opt!=JOptionPane.OK_OPTION)
                return;

        }
        else
            pls =PlasmidSample.generatePlasmidSample(myPlasmid, myStrain, myContainer, vol, myPerson);
        pls.changeName(sampleNameCell.getStringCellValue());
        pls.changeVolume(vol);
        pls.changeQuality((short)quality);
        pls.changeConcentration(conc);
        //put in appropriate tags if this is a gel Sample;
        // note that BOTH enzyme1 and enzyme2 should be specified in order for this
        // to be a legitimate gel sample!
        if (!(enzyme1.equals("?") || enzyme2.equals("?")))
        {
            pls.addSearchTag("gel-sample");
            pls.addSearchTag("enzyme1 "+enzyme1);
            pls.addSearchTag("enzyme2 "+enzyme2);
        }

        //if this is a frozen stock, put in appropriate search tags to notify that!
        if (frozen_stock.getStringCellValue().equalsIgnoreCase("yes"))
        {
            pls.addSearchTag("frozen-stock");
        }
        pls.saveDefault();


        boolean change =false;
        if (!(newContainerName.equals("?")))
        {
            myContainer.changeName(newContainerName);
            change =true;
        }
        if (!(newBarcode.equals("?")))
        {
            myContainer.changeBarcode(newBarcode);
            change =true;
        }
        if (change)
            myContainer.saveDefault();

    }

    /**
     * validates the plasmid name is in the database
     * @param plasmid name of Plasmid in the excel table
     */
    private void validatePlasmid(String plasmid)
    {

    }

    /**
     * validates the strain name is in the database
     * @param strain name of strain in the excel table
     */
    private void validateStrain(String strain)
    {


    }
    

private Plate myPlate;
private boolean errorFlag=false;
public static enum header {ROW,COL,CONTAINERNAME,BARCODE,SAMPLENAME,SAMPLETYPE,QUALITY,CONC,VOL,PLASMID,STRAIN,AUTHOR,ENZYME1,ENZYME2,FROZEN_STOCK};
public static String[] headerNames = {"Row","Col","Container Name","BarCode","Sample Name","Sample Type","Quality","Concentration","Volume","Plasmid","Strain","Author","Enzyme1","Enzyme2","Frozen-Stock"};


}

