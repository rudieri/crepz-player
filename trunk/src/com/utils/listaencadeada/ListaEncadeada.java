/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.listaencadeada;

import java.util.Iterator;

/**
 *
 * @author rudieri
 */
public class ListaEncadeada<E> implements Iterable<E>, Iterator<E> {

    private No<E> inicio;
    private No<E> fim;
    private No<E> iteratorNo;
    private int tamanho = 0;
    private int iteratorIndex = 0;

    public void adicionar(E valor) {
        if (inicio == null) {
            inicio = new No<E>(valor);
            fim = inicio;
        } else {
            fim.proximo = new No(valor);
            fim.proximo.anterior = fim;
            fim = fim.proximo;
        }
        tamanho++;
        imprimir();
    }

    public E getValorA(int indice) {
        if (indice >= tamanho || indice < 0) {
            throw new IllegalStateException("O índice deve estar entre [0 e TAMANHO], índice: " + indice + " tamanho: " + tamanho);
        }
        iteratorIndex = indice;
        if (indice < tamanho >> 2) {
            int i = 0;
            No<E> base = inicio;
            while (i++ != indice) {
                base = base.proximo;
            }
            iteratorNo = base;
            return base.valor;
        } else {
            int i = tamanho - 1;
            No<E> base = fim;
            while (i-- != indice) {
                base = base.anterior;
            }
            iteratorNo = base;
            return base.valor;

        }

    }

    public int getPosicaoAtualIterador() {
        return iteratorNo == null ? -1 : iteratorIndex;
    }

    public E getValorAtualIterador() {
        return iteratorNo == null ? null : iteratorNo.valor;
    }

    public void inserir(E valor, int indice) {
        if (indice >= tamanho || indice < 0) {
            if (tamanho == 0) {
                adicionar(valor);
                return;
            }
            throw new IllegalStateException("O índice deve estar entre [0 e TAMANHO], índice: " + indice + " tamanho: " + tamanho);
        }
        No novoNo = new No(valor);
        if (indice < tamanho >> 2) {
            No ondeInserir = inicio;
            int i = 0;
            while (i++ != indice) {
                ondeInserir = ondeInserir.proximo;
            }
            if (ondeInserir.anterior == null) {
                inicio = novoNo;
                inicio.proximo = ondeInserir;
                ondeInserir.anterior = inicio;
            } else {
                ondeInserir.anterior.proximo = novoNo;
                novoNo.anterior = ondeInserir.anterior;
                ondeInserir.anterior = novoNo;
                novoNo.proximo = ondeInserir;
            }
        } else {
            No ondeInserir = fim;
            int i = tamanho - 1;
            while (i-- != indice) {
                ondeInserir = ondeInserir.anterior;
            }
            if (ondeInserir.proximo == null) {
                fim = novoNo;
                fim.anterior = ondeInserir;
                ondeInserir.proximo = fim;
            } else {
                if (ondeInserir.anterior != null) {
                    ondeInserir.anterior.proximo = novoNo;
                }
                novoNo.anterior = ondeInserir.anterior;
                ondeInserir.anterior = novoNo;
                novoNo.proximo = ondeInserir;
            }
        }
        tamanho++;
        imprimir();
    }

    public boolean removerPrimeiro(E valor) {
        return removerPrimeiro(inicio, valor);
    }

    private boolean removerPrimeiro(No pai, E valor) {
        if (pai.valor.equals(valor)) {
            if (pai == inicio) {
                inicio = pai.proximo;
            } else {
                pai.anterior.proximo = pai.proximo;
            }
            if (pai == fim) {
                fim = pai.anterior;
            } else {
                pai.proximo.anterior = pai.anterior;
            }
            return true;
        } else {
            if (pai.proximo == null) {
                return false;
            }
            return removerPrimeiro(pai.proximo, valor);
        }
    }

    private boolean removerUltimo(No pai, E valor) {
        if (pai.valor.equals(valor)) {
            if (pai == inicio) {
                inicio = pai.proximo;
            } else {
                pai.anterior.proximo = pai.proximo;
            }
            if (pai == fim) {
                fim = pai.anterior;
            } else {
                pai.proximo.anterior = pai.anterior;
            }
            return true;
        } else {
            if (pai.anterior == null) {
                return false;
            }
            return removerUltimo(pai.anterior, valor);
        }
    }

    public boolean removerUltimo(E valor) {
        return removerUltimo(fim, valor);
    }

    public void imprimir() {
        System.out.println("- - - - - - - - - - - - - - - ");
        System.out.println("Conteudo: ");
        imprimir(inicio);
    }

    private void imprimir(No<E> pai) {
        if (pai.proximo != null) {
            System.out.println(pai + " >> " + pai.proximo + " << " + pai.proximo.anterior);
            imprimir(pai.proximo);
        } else {
            if (pai.anterior != null) {
                System.out.println(pai + " << " + pai.anterior);
            } else {
                System.out.println(pai);
            }
        }
    }

    public int tamanho() {
        return tamanho;
    }

    @Override
    public Iterator<E> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return iteratorIndex < tamanho;
    }

    @Override
    public E next() {
        if (iteratorNo == null) {
            iteratorNo = inicio;
        } else {
            iteratorNo = iteratorNo.proximo;
        }
        iteratorIndex++;
        return iteratorNo.valor;
    }

    public E prev() {
        if (iteratorNo == null) {
            iteratorIndex = tamanho - 1;
            iteratorNo = fim;
        } else {
            iteratorNo = iteratorNo.anterior;
        }
        iteratorIndex--;
        return iteratorNo.valor;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remoção por iterador não implementada.");
//        if (iteratorNo.anterior!=null) {
//            if (iteratorNo.proximo!=null) {
//                iteratorNo.anterior.proximo=iteratorNo.proximo;
//                iteratorNo.proximo.anterior=iteratorNo.anterior;
//                iteratorNo=iteratorNo.anterior;
//                iteratorIndex--;
//                tamanho--;
//            }
//        }
    }

    private class No<E> {

        public No(E valor) {
            this.valor = valor;
        }
        private No anterior;
        private No proximo;
        private E valor;

        @Override
        public String toString() {
            return valor.toString();
        }
    }
}
