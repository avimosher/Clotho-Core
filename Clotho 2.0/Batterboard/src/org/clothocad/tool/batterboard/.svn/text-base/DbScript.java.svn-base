/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clothocad.tool.batterboard;

import org.clothocore.api.data.*;
import java.util.*;
import java.util.UUID;
import java.io.*;
import org.clothocore.api.data.Attachment.AttachmentType;
import org.clothocore.api.data.PlateType.PlateTypeDatum;
/**
 *
 * @author rishi
 */
public class DbScript {

    public static enum picTypes {PCR_96, RACK_99};

    public static void setupDB(File filename, picTypes typ)

    {
        // insert the attachment images
        String name = (typ == picTypes.PCR_96)?"PCR_96":"RACK_9X9";
        Attachment attach = new Attachment(filename,name,AttachmentType.PNG);
        attach.saveDefault();
        

        // insert plate types
        HashMap<String, Object> objMap = new HashMap();
        fillup(objMap,typ,attach.getUUID());
        String uuid = UUID.randomUUID().toString();
        PlateType ptype = (PlateType)PlateType.importFromHashMap(uuid,objMap);
//        ptype._datum._isChanged = true;
        ptype.setDatumChangeStatus(true);
        ptype.saveDefault();

      
    }

    public static void setupExistingRack(PlateType p)
    {
        p._pTypeDatum.wellPosX = PlateType.parseIntString("46,89,133,176,221,265,308,353,396,440,484,528.44,88,133,176,222,266,309,352,397,440,484,529.44,88,132,176,221,265,308,353,397,441,484,529.44,88,132,176,221,265,309,353,397,441,485,530.44,87,132,176,220,264,309,353,397,441,485,530.43,87,131,176,220,264,308,353,398,442,486,530.42,87,131,176,220,264,309,353,398,442,486,531.42,87,131,175,220,264,309,353,399,442,487,532.42,87,131,175,220,264,309,353,399,442,487,532");
        p._pTypeDatum.wellPosY = PlateType.parseIntString("35,35,33,33,34,34,33,34,33,33,33,34.79,79,79,78,79,79,78,77,78,78,78,78.124,124,123,124,124,124,123,123,123,123,123,123.169,169,168,168,168,168,168,168,168,168,168,168.214,214,213,213,213,213,213,213,213,213,213,212.258,258,258,257,258,257,258,257,257,258,258,258.304,304,302,303,302,302,302,302,302,303,302,303.348,348,348,347,347,347,347,347,348,348,348,348.393,393,393,392,392,392,392,392,393,393,393,393");
//        p._datum._isChanged=true;
        p.setDatumChangeStatus(true);
        p.saveDefault();
    }
    private static void fillup(HashMap<String,Object> objMap,picTypes typ,String uuid)
    {
       String numRows = (typ==picTypes.PCR_96)? "8" : "9";
       String numCols = (typ==picTypes.PCR_96)? "12" : "9";
       String wellPosX = (typ==picTypes.PCR_96)? "46,89,133,176,221,265,308,353,396,440,484,528.44,88,133,176,222,266,309,352,397,440,484,529.44,88,132,176,221,265,308,353,397,441,484,529.44,88,132,176,221,265,309,353,397,441,485,530.44,87,132,176,220,264,309,353,397,441,485,530.43,87,131,176,220,264,308,353,398,442,486,530.42,87,131,176,220,264,309,353,398,442,486,531.42,87,131,175,220,264,309,353,399,442,487,532" : "46,89,133,176,221,265,308,353,396,440,484,528.44,88,133,176,222,266,309,352,397,440,484,529.44,88,132,176,221,265,308,353,397,441,484,529.44,88,132,176,221,265,309,353,397,441,485,530.44,87,132,176,220,264,309,353,397,441,485,530.43,87,131,176,220,264,308,353,398,442,486,530.42,87,131,176,220,264,309,353,398,442,486,531.42,87,131,175,220,264,309,353,399,442,487,532.42,87,131,175,220,264,309,353,399,442,487,532";
       String wellPosY = (typ==picTypes.PCR_96)? "35,35,33,33,34,34,33,34,33,33,33,34.79,79,79,78,79,79,78,77,78,78,78,78.124,124,123,124,124,124,123,123,123,123,123,123.169,169,168,168,168,168,168,168,168,168,168,168.214,214,213,213,213,213,213,213,213,213,213,212.258,258,258,257,258,257,258,257,257,258,258,258.304,304,302,303,302,302,302,302,302,303,302,303.348,348,348,347,347,347,347,347,348,348,348,348" : "35,35,33,33,34,34,33,34,33,33,33,34.79,79,79,78,79,79,78,77,78,78,78,78.124,124,123,124,124,124,123,123,123,123,123,123.169,169,168,168,168,168,168,168,168,168,168,168.214,214,213,213,213,213,213,213,213,213,213,212.258,258,258,257,258,257,258,257,257,258,258,258.304,304,302,303,302,302,302,302,302,303,302,303.348,348,348,347,347,347,347,347,348,348,348,348.393,393,393,392,392,392,392,392,393,393,393,393";
       String name = (typ==picTypes.PCR_96)? "PCR_96":"RACK_9X9";

       String isContainerFixed = "true";
       String  wellDiameter = "27";
       String squareWells = "false";
       String squareWellHeight = "27";
       String squareWellWidth = "27";
       String mmWidth = "124.28";
       String mmHeight = "83.97";
       String mmWellWidth ="4";
       String mmWellHeight ="4";
       String mmWellDiameter = "5.46";
       String mmXOffset = "10.1";
       String mmYOffset ="7.76";
       String mmXWellSpacing ="9";
       String mmYWellSpacing ="9";
       String attachmentUUID = uuid;

       objMap.put("_numRows", numRows);
       objMap.put("_numCols", numCols);
       objMap.put("wellPosX", wellPosX);
       objMap.put("wellPosY",wellPosY);
       objMap.put("name", name);
       objMap.put("_isContainerFixed",isContainerFixed);
       objMap.put("name", name);
       objMap.put("wellDiameter", wellDiameter);
       objMap.put("squareWells", squareWells);
       objMap.put("squareWellHeight",squareWellHeight);
       objMap.put("squareWellWidth",squareWellWidth);
       objMap.put("mmWidth",mmWidth);
       objMap.put("mmHeight",mmHeight);
       objMap.put("mmWellWidth",mmWellWidth);
       objMap.put("mmWellHeight",mmWellHeight);
       objMap.put("mmWellDiameter",mmWellDiameter);
       objMap.put("mmXOffset",mmXOffset);
       objMap.put("mmYOffset",mmYOffset);
       objMap.put("mmXWellSpacing",mmXWellSpacing);
       objMap.put("mmYWellSpacing",mmYWellSpacing);
       objMap.put("attachmentUUID", attachmentUUID);
       //populate the hashmap with given values;
    


    }

}