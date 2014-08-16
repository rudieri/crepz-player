/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.model.comboboxmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.event.ListDataListener;

/**
 *
 * @param <E>
 * @author rudieri
 */
public class ComboBoxModelEditavel<E> implements javax.swing.ComboBoxModel<Object> {

    private ArrayList<E> itens;
    private ArrayList<ListDataListener> listDataListeners;
    private E selectedItem;
    private String textoNenhumItemSelecionado;

    public ComboBoxModelEditavel(ArrayList<E> itens) {
        this.itens = itens;
        listDataListeners = new ArrayList<ListDataListener>(2);

    }

    public ComboBoxModelEditavel() {
        this(new ArrayList<E>(3));
    }

    public void addItem(E item) {
        itens.add(item);
    }

    public void addAll(Collection<E> collection) {
        itens.addAll(collection);
    }

    public void addAll(E[] vetor) {
        itens.addAll(Arrays.asList(vetor));
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selectedItem = (E) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem == null ? textoNenhumItemSelecionado : selectedItem;
    }

    @Override
    public int getSize() {
        return itens.size();
    }

    @Override
    public Object getElementAt(int index) {
        return itens.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        if (!listDataListeners.contains(l)) {
            listDataListeners.add(l);
        }
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        int indexOf = listDataListeners.indexOf(l);
        if (indexOf != -1) {
            listDataListeners.remove(indexOf);
        }
    }

    public void setTextoNenhumItemSelecionado(String textoNenhumItemSelecionado) {
        this.textoNenhumItemSelecionado = textoNenhumItemSelecionado;
    }

    public String getTextoNenhumItemSelecionado() {
        return textoNenhumItemSelecionado;
    }
    
}
