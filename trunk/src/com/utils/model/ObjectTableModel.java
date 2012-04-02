/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author rudieri
 */
public class ObjectTableModel<E extends Filtravel> implements TableModel {

    private ArrayList<TableModelListener> tableModelListeners;
    private ArrayList<E> itens;
    private ArrayList<E> itensFiltrados;
    private final Class<E> modelo;
    private String filtro;
    private ArrayList<ObjectTableModelListener> listeners;

    public ObjectTableModel(Class<E> modelo) {
        tableModelListeners = new ArrayList<TableModelListener>(2);
        listeners = new ArrayList<ObjectTableModelListener>(1);
        itens = new ArrayList<E>(10);
        itensFiltrados = new ArrayList<E>(10);
        this.modelo = modelo;
    }

    @Override
    public int getRowCount() {
        if (filtro == null) {
            return itens.size();
        } else {
            return itensFiltrados.size();
        }
    }

    @Override
    public int getColumnCount() {
        Field[] campos = modelo.getDeclaredFields();
        int contaAnotacao = 0;
        for (int i = 0; i < campos.length; i++) {
            Field campo = campos[i];
            final ObjetoTabela annotation = campo.getAnnotation(ObjetoTabela.class);
            if (annotation != null && annotation.visivel()) {
                contaAnotacao++;
            }

        }
        return contaAnotacao;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return ObjetoTabeaUtils.getNomeColuna(modelo, columnIndex);
//        return ((ObjetoTabela) modelo.getClass().getAnnotations()[columnIndex]).nomeColuna();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    @Deprecated
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (filtro == null) {
            return ObjetoTabeaUtils.getValueAt(itens.get(rowIndex), columnIndex);
        } else {
            if (itensFiltrados.isEmpty()) {
                return null;
            }
            return ObjetoTabeaUtils.getValueAt(itensFiltrados.get(rowIndex), columnIndex);
        }
    }

    /**

     * @deprecated Será usado apenas internamente, substituído por {@link ObjectTableModel}
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // do nothing
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        tableModelListeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        tableModelListeners.remove(l);
    }

    public void addObjectTableModelListener(ObjectTableModelListener listener) {
        listeners.add(listener);
    }

    public void removeObjectTableModelListener(ObjectTableModelListener listener) {
        listeners.remove(listener);
    }

    public void setValueAt(E item, int linha) {
        itens.set(linha, item);
        for (int i = 0; i < tableModelListeners.size(); i++) {
            TableModelEvent event = new TableModelEvent(this);
            tableModelListeners.get(i).tableChanged(event);

        }
    }

    public void addItem(E item) {
        itens.add(item);
        if (filtro == null) {

            for (int i = 0; i < tableModelListeners.size(); i++) {
                TableModelEvent event = new TableModelEvent(this, itens.size() - 1, itens.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                tableModelListeners.get(i).tableChanged(event);

            }
        } else {
            atualizarFiltro();
        }
        dispararSizeChanged();

    }

    public void setItens(ArrayList<E> itens) {
//        this.itens.clear();
//        this.itensFiltrados.clear();
        clear();
        this.itens.addAll(itens);
        if (filtro == null) {

            for (int i = 0; i < tableModelListeners.size(); i++) {
                TableModelEvent event = new TableModelEvent(this, 0, itens.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                tableModelListeners.get(i).tableChanged(event);

            }
        } else {
            atualizarFiltro();
        }
        dispararSizeChanged();
    }

    private void dispararSizeChanged() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).sizeChanged(getRowCount());
        }
    }

    public void atualizarItem(E item, int row) {
        atualizarItem(item, row, row);
    }

    public void atualizarItem(E item, int row, int convertedRow) {
        itens.set(row, item);
        if (filtro != null) {
            int indexOf = itensFiltrados.indexOf(item);
            if (indexOf != -1) {
                itensFiltrados.set(indexOf, item);
            }
        }
        System.out.println("Item changed: " + item + ", tempo total: " + item.getTextoParaPesquisa());
        for (int i = 0; i < tableModelListeners.size(); i++) {
            TableModelEvent event = new TableModelEvent(this, convertedRow, convertedRow, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
            tableModelListeners.get(i).tableChanged(event);

        }
    }

    public E getItem(int row) {
        if (filtro == null) {
            return itens.get(row);

        } else {
            return itensFiltrados.get(row);
        }
    }

    public void clear() {
        itens.clear();
        itensFiltrados.clear();
        for (int i = 0; i < tableModelListeners.size(); i++) {
            TableModelEvent event = new TableModelEvent(this);
            tableModelListeners.get(i).tableChanged(event);

        }
        dispararSizeChanged();
    }

    private void atualizarFiltro() {
        if (!itensFiltrados.isEmpty()) {
            for (int i = 0; i < tableModelListeners.size(); i++) {
                TableModelEvent event = new TableModelEvent(this, -1, itensFiltrados.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
                tableModelListeners.get(i).tableChanged(event);
            }
        }
        itensFiltrados.clear();
        System.out.println("Filtro: " + filtro);
        String[] tokenFiltro = filtro.split(" ");
        for (int i = 0; i < itens.size(); i++) {
            E item = itens.get(i);
            boolean todos = true;
            for (int j = 0; todos && j < tokenFiltro.length; j++) {
                todos &= item.getTextoParaPesquisa().contains(tokenFiltro[j]);
            }
            if (todos) {
                itensFiltrados.add(item);
            }
        }
        System.out.println("Itens Filtrados: " + itensFiltrados.size());
        for (int i = 0; i < tableModelListeners.size(); i++) {
            TableModelEvent event = new TableModelEvent(this, 0, itensFiltrados.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
            if (!itensFiltrados.isEmpty()) {
//             event = new TableModelEvent(this, 0, 0, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
                tableModelListeners.get(i).tableChanged(event);
            }

        }
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String text) {
        if (text.isEmpty()) {
            filtro = null;
            for (int i = 0; i < tableModelListeners.size(); i++) {
                TableModelEvent event = new TableModelEvent(this, 0, itens.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                if (!itensFiltrados.isEmpty()) {
//             event = new TableModelEvent(this, 0, 0, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
                    tableModelListeners.get(i).tableChanged(event);
                }

            }
            itensFiltrados.clear();
        } else {
            this.filtro = text;
            atualizarFiltro();
        }
        dispararSizeChanged();
    }

    public int indexOf(E item) {
        int i = 0;
        boolean achei = false;
        if (filtro == null) {
            for (; !achei && i < itens.size(); i++) {
                E e = itens.get(i);
                achei |= e.equals(item);
            }
        } else {
            for (; !achei && i < itensFiltrados.size(); i++) {
                E e = itensFiltrados.get(i);
                achei |= e.equals(item);
            }
        }
//        System.out.println("Achei? " + achei + ", index: " + i);
        // diminui 1 por que o for soma uma para mais sempre
        return achei ? i - 1 : -1;
    }

    public boolean contains(E item) {
        return indexOf(item) != -1;
    }
}
