package com.utils;
//+-------+---+    +-------+---+    +-------+---+             +-------+---+
//|Vazio  | *----> |  X1   | *----> | X2    | *----> ... ---> |  Xn   | *---->nil

import com.graficos.Dado;

//+-------+---+    +-------+---+    +-------+---+             +-------+---+
//  Inicio                                                       FIM
public class Fila {

    private static class Celula {
        Dado item;
        Celula prox;
    }
    private Celula inicio, fim;
    private int qtd;

    public Fila() { //Cria uma Pilha vazia
        this.inicio = new Celula();
        this.fim = this.inicio;
        this.fim.prox = null;
        this.qtd = 0;
    }

    public Dado inserir(Dado x) {
        this.fim.prox = new Celula();
        if (this.fim.prox == null) {
            return null;
        }
        this.fim = this.fim.prox;
        this.fim.item = x;
        this.fim.prox = null;
        this.qtd++;
        return x;
    }

    public Dado retirar() {
        Dado item = null;
        if (!this.vazia()) {
            this.inicio = this.inicio.prox;
            if(inicio==null){
                return null;
            }
            item = this.inicio.item;
            this.qtd--;
        }
        return item;
    }

    public Dado retirar(Dado retirar) {
        Celula atual = inicio.prox;
        Celula ant = inicio;
        while(atual!=null && atual.item != null){
            if(atual.item.imagem.equals(retirar.imagem)){
                if(ant!=null){
                    ant.prox = atual.prox;
                }else{
                    inicio = atual.prox;
                }
                return atual.item;
            }
            ant = atual;
            atual = atual.prox;
        }

        System.out.println("Crepz, dado não encontrado"+retirar.imagem);
        return null;


    }

    public boolean vazia() {
        return (this.inicio == this.fim);
    }

    public int qtd() {
        return (this.qtd);
    }

    public boolean mostrar() { // Mostra do topo para a base
        Fila pilhaAux = new Fila();
        Dado elemento = null;

        System.out.print("Fila=[ ");
        while ((elemento =  this.retirar()) != null) {
            pilhaAux.inserir(elemento);
            System.out.print(elemento.imagem + " ");
        }
        System.out.println("]");

        while ((elemento =  pilhaAux.retirar()) != null) {
            this.inserir(elemento);
        }
        return (true);
    }

   

  

    public static Fila agrupar(Fila fila1, Fila fila2){
        Fila retorno = new Fila();

        while(!fila1.vazia()){
            retorno.inserir(fila1.retirar());
        }

        while(!fila2.vazia()){
            retorno.inserir(fila2.retirar());
        }

        return retorno;
    }
}