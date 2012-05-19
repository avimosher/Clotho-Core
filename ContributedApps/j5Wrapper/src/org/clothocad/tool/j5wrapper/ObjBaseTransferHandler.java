/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clothocad.tool.j5wrapper;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.TransferHandler;
import org.clothocore.api.data.ObjBase;
import org.clothocore.api.dnd.TransferableObject;
import org.openide.util.Exceptions;

/**
 * copied shamelessly from jenhan
 */
public class ObjBaseTransferHandler extends TransferHandler {
public ObjBaseTransferHandler(ObjBaseDropTarget target) {
    _target=target;
}
    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        try {
            ObjBase o= (ObjBase) support.getTransferable().getTransferData(TransferableObject.objBaseFlavor);
            _target.handleDroppedObject(o);
        } catch (UnsupportedFlavorException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return super.importData(support);
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        try {
            if (support.getTransferable().getTransferData(TransferableObject.objBaseFlavor) != null) {
                return true;
            }
        } catch (UnsupportedFlavorException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
        return false;
    }
    
    private ObjBaseDropTarget _target;
}
