/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.model;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author manchini
 */
public class ModelReadOnly implements TableModel {

    private final ArrayList<TableModelListener> tableModelListeners;
    private final ArrayList<Object[]> dados;
    private final ArrayList<String> colunas;
//    private final ArrayList<Class> classesColunas;

    public ModelReadOnly() {
        this.tableModelListeners = new ArrayList<TableModelListener>(2);
        dados = new ArrayList<Object[]>(0);
        colunas = new ArrayList<String>();
//        dispararEvento(TableModelEvent.);
//        classesColunas = new ArrayList<Class>();
    }

    public ModelReadOnly(String[] colunas, Object[][] dados) {
        this.tableModelListeners = new ArrayList<TableModelListener>(2);
        this.dados = new ArrayList<Object[]>(dados.length);
        this.dados.addAll(Arrays.asList(dados));
        this.colunas = new ArrayList<String>(colunas.length);
        this.colunas.addAll(Arrays.asList(colunas));
//        dispararEvento();
//        classesColunas = new ArrayList<Class>();
    }

    public void addColumn(String nomeColuna) {
        colunas.add(nomeColuna);
        dispararEvento(TableModelEvent.INSERT);
    }

    public void setRowCount(int i) {
        if (i >= dados.size()) {
            return;
        }
        int sizeAC = dados.size();
        dados.subList(i, sizeAC).clear();
        dispararEvento(i, sizeAC, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
    }

    public void addRow(Object[] object) {
        dados.add(object);
        dispararEvento(dados.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
    }

    public void removeRow(int selectedRow) {
        dados.remove(selectedRow);
        dispararEvento(selectedRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
    }

    public void moveRow(int start, int end, int to) {
        if (start == to) {
            return;
        } else if (to > start) {
            to += 1;
        }
        end += 1;

        ArrayList<Object[]> subList = new ArrayList<Object[]>(dados.subList(start, end));
        for (int i = start; i < end; i++) {
            dados.set(i, null);
        }
        dados.addAll(to, subList);
        dados.removeAll(Arrays.asList(new Object[]{null}));
//        subList.clear(); // Erro aqui
        dispararEvento(TableModelEvent.UPDATE);
    }

    public void insertRow(int posicao, Object[] object) {
        dados.add(posicao, object);
        dispararEvento(posicao, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public int getRowCount() {
        return dados.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return colunas.get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return dados.get(rowIndex)[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        dados.get(rowIndex)[columnIndex] = aValue;
        dispararEvento(rowIndex, columnIndex, TableModelEvent.UPDATE);
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        if (!tableModelListeners.contains(l)) {
            tableModelListeners.add(l);
        }
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        if (tableModelListeners.contains(l)) {
            tableModelListeners.remove(l);
        }
    }

    private void dispararEvento(int tipoEvento) {
        dispararEvento(-1, -1, tipoEvento);
    }

    private void dispararEvento(int linha, int coluna, int tipoEvento) {
        dispararEvento(linha, linha, coluna, tipoEvento);
    }

    private void dispararEvento(int linhaInicial, int linhaFinal, int coluna, int tipoEvento) {
        TableModelEvent event;
        if (linhaInicial == -1) {
            event = new TableModelEvent(this);
        } else {
            event = new TableModelEvent(this, linhaInicial, linhaFinal, coluna, tipoEvento);
        }
        for (int i = 0; i < tableModelListeners.size(); i++) {
            tableModelListeners.get(i).tableChanged(event);

        }
    }

}
