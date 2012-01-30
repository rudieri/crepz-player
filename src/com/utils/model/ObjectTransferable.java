/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class ObjectTransferable<E> implements Transferable {

    DataFlavor[] dataFlavors;
    private final E obj;

    public ObjectTransferable(E obj, String nome) {
        this.obj = obj;
        dataFlavors = new DataFlavor[2];
        dataFlavors[0] = new DataFlavor(obj.getClass(), nome);
        try {
            dataFlavors[1] = new DataFlavor("text/plain");
//            dataFlavors[2] = new DataFlavor("text/html");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ObjectTransferable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return dataFlavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        boolean suporta = false;
        for (int i = 0; !suporta && i < dataFlavors.length; i++) {
            DataFlavor dataFlavor = dataFlavors[i];
            suporta |= dataFlavor.equals(flavor);
        }
        return suporta;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(dataFlavors[0])) {
            return obj;
        } else if (flavor.equals(dataFlavors[1])) {
            return obj.toString();
        } else {
            return "No flavor found.";
        }
    }

    public E getObject(){
        return obj;
    }
}
