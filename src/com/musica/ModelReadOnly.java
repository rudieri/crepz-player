/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.musica;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author manchini
 */
public class ModelReadOnly extends DefaultTableModel {

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }



}
