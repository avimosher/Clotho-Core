/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clothocad.tool.batterboard.sequence;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.clothocore.api.data.NucSeq;
import org.clothocore.api.data.PlasmidSample;
import org.clothocore.api.data.Sample;
import org.clothocore.api.data.ObjType;


import org.clothocad.algorithm.seqanalyzer.sequencing.ABITrace;
import org.clothocad.algorithm.seqanalyzer.sequencing.seqAnalysis;
//import net.iharder.dnd.FileDrop;

/**
 *
 * @author jcanderson
 */
public class fileDropHandler {

    public static void drop(File[] files, NucSeq dropSample, String author, String sName) {
        System.out.println("drop!");
        

        //Determine if an abi file was dropped
        ArrayList<ABITrace> traces = new ArrayList<ABITrace>();
        for(File afile: files) {
            ABITrace abi = getABIFrom(afile);
            if(abi!=null) {
                traces.add(abi);
                System.out.println("Dropper added a trace of length " + abi.getTraceLength());
            }
            else{
                System.out.println("OOPS");
            }
        }

        
        
        String in=dropSample.getSeq();
        seqAnalysis analysis=new seqAnalysis(traces, in, author, sName, true);
        
    }

    public static void zipDrop(File zip, ArrayList<NucSeq> samps){
        //Determine if a zip file was dropped
        ArrayList<byte[]> zippedFiles = new ArrayList<byte[]>();
        ArrayList<String> zipNames=new ArrayList<String>();
        try {
            System.out.println("ZIPPPPPP");
            FileInputStream fis = new FileInputStream(zip);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze;
            int off=0;
            while ((ze = zis.getNextEntry()) != null) {
                if(!ze.getName().contains(".ab1")){
                    continue;
                }
                System.out.println(ze.getName());
                byte[] bytes = new byte[1024];
                byte[] data;
                String fileName=ze.getName();
                zipNames.add(fileName);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int b;
                System.out.println(ze.getCompressedSize());
                while ((b = zis.read(bytes, 0, bytes.length)) >= 0)
                {
                    //System.out.println(b);
                    baos.write(b);
                    off+=bytes.length;
                }
                baos.close();
                data = baos.toByteArray();
                zippedFiles.add(data);
                zis.closeEntry();
                off=0;
            }
            zis.close();
            
        } catch (IOException ex) {
            Logger.getLogger(fileDropHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<ABITrace> parsed=extractFromZip(zippedFiles);
        System.out.println("checking extraction");
        for(ABITrace ab: parsed){
            System.out.println(ab.getFileName());
        }

    }

    private static ArrayList<ABITrace> extractFromZip(ArrayList<byte[]> zippedFiles) {
        System.out.println("extracting!");
        ArrayList<ABITrace> ret=new ArrayList<ABITrace>();
        for(byte[] i: zippedFiles){
            for(int j=0; j<i.length; j++){
                System.out.print(i[j]);
            }
            System.out.println();
            ret.add(getABIFrom(i));
        }
        return ret;
    }
    public static final void copyInputStream(InputStream in, OutputStream out)
        throws IOException
        {
            byte[] buffer = new byte[1024];
            int len;

            while((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

            in.close();
            out.close();
        }
    private static ABITrace getABIFrom(File afile) {
            try {
                ABITrace trace = new ABITrace(afile);
                return trace;
            } catch (IOException ex) {
                Logger.getLogger(fileDropHandler.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
    }
    private static ABITrace getABIFrom(byte[] bytes) {

            ABITrace trace = new ABITrace(bytes);
            return trace;

    }

    public static void main(String[] args){
        File f=new File("//Users//benjaminbubenheim//Downloads//071410_iGEM.zip");
        zipDrop(f, null);
    }



}

