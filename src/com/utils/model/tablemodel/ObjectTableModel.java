/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.model.tablemodel;

import com.utils.CrepzInfo;
import java.lang.reflect.Field;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author rudieri
 * @param <E>
 */
public class ObjectTableModel<E extends Filtravel> implements TableModel {

    private ArrayList<TableModelListener> tableModelListeners;
    private ArrayList<E> itens;
    private ArrayList<E> itensFiltrados;
    private final Class<E> modelo;
    private String filtro;
    private ColunaTabela[] colunas;
    private ArrayList<ObjectTableModelListener> listeners;
    private boolean gerarColunas;

    public ObjectTableModel(Class<E> classeModelo) {
        this(classeModelo, true);
    }
    public ObjectTableModel(Class<E> classeModelo, boolean gerarColunas) {
        tableModelListeners = new ArrayList<TableModelListener>(2);
        listeners = new ArrayList<ObjectTableModelListener>(1);
        itens = new ArrayList<E>(10);
        itensFiltrados = new ArrayList<E>(10);
        this.gerarColunas = gerarColunas;
        this.modelo = classeModelo;
        if (gerarColunas) {
            inicializaColunas();
        }
    }

    private void inicializaColunas() {
        Field[] campos = modelo.getDeclaredFields();
        ArrayList<ColunaTabela> colunaTabelas = new ArrayList<ColunaTabela>();
        ArrayList<Field> caminho = new ArrayList<Field>();
        inicializaColunas(colunaTabelas, campos, caminho);
        colunas = new ColunaTabela[colunaTabelas.size()];
        colunas = colunaTabelas.toArray(colunas);

    }

    private void inicializaColunas(ArrayList<ColunaTabela> colunas, Field[] campos, ArrayList<Field> caminho) {
        for (Field campo : campos) {
            campo.setAccessible(true);
            CrepzInfo annotation = campo.getAnnotation(CrepzInfo.class);
            if (annotation != null && annotation.mostrarNaTabela()) {
                if (annotation.temFilhos()) {
                    Class<?> tipoCampoFilho = campo.getType();
                    caminho.add(campo);
                    inicializaColunas(colunas, tipoCampoFilho.getDeclaredFields(), caminho);
                    caminho.remove(campo);
                } else {
                    caminho.add(campo);
                    Field[] fields = new Field[caminho.size()];
                    ColunaTabela colunaTabela = new ColunaTabela(caminho.toArray(fields));
                    colunas.add(colunaTabela);
                    caminho.remove(caminho.size() - 1);
                }
            }
        }
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
        return gerarColunas ? colunas.length : 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return gerarColunas ? ObjetoTabelaUtils.getNomeColuna(modelo, colunas[columnIndex].getField()) : modelo.getName();
//        return ((ObjetoTabela) modelo.getClass().getAnnotations()[columnIndex]).nomeColuna();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return gerarColunas ? Object.class : modelo;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    @Deprecated
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (filtro == null) {
            return gerarColunas ? colunas[columnIndex].getValor(itens.get(rowIndex)) : itens.get(rowIndex);
        } else {
            if (itensFiltrados.isEmpty()) {
                return null;
            }
            return gerarColunas ? colunas[columnIndex].getValor(itensFiltrados.get(rowIndex)) : itensFiltrados.get(rowIndex);
        }
    }

    /**
     *
     * @param aValue
     * @param rowIndex
     * @param columnIndex
     * @deprecated Será usado apenas internamente, substituído por
     * {@link ObjectTableModel}
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

    public void insertItem(E item, int index) {
        itens.add(index, item);
        if (filtro == null) {
            for (int i = 0; i < tableModelListeners.size(); i++) {
                TableModelEvent event = new TableModelEvent(this, index, itens.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
                tableModelListeners.get(i).tableChanged(event);
            }
        } else {
            atualizarFiltro();
        }
        dispararSizeChanged();

    }

    public void setItens(ArrayList<E> itens) {
        clear();
        if (itens.isEmpty()) {
            return ;
        }
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
            listeners.get(i).sizeChanged(this, getRowCount());
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
        for (int i = 0; i < tableModelListeners.size(); i++) {
            TableModelEvent event = new TableModelEvent(this);//, 0, itensFiltrados.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
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
            this.filtro = text.toLowerCase();
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

    public void moveRow(int linhaInicial, int linhaFinal, int novaPosicao) {
        ArrayList<E> dados = new ArrayList<E>(linhaFinal - linhaInicial + 1);
        for (int i = linhaInicial; i < linhaFinal; i++) {
            dados.add(itens.remove(i));
        }
        itens.addAll(novaPosicao, dados);
        if (filtro == null) {
            for (int i = 0; i < tableModelListeners.size(); i++) {
                TableModelEvent event = new TableModelEvent(this, 0, itens.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
                tableModelListeners.get(i).tableChanged(event);
            }
        } else {
            atualizarFiltro();
        }
        dispararSizeChanged();
    }

    /**
     * Use por conta e risco...
     *
     * @return
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public ArrayList<E> getItens() {
        return itens;
    }

    public void removeItem(int... linhas) {
        for (int i = linhas.length; i >= 0; i--) {
            itens.remove(linhas[i]);
        }
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
}
