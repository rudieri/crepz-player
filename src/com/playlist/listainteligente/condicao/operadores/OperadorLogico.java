/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente.condicao.operadores;

import com.musica.Musica;
import com.playlist.listainteligente.condicao.Condicao;

/**
 *
 * @author rudieri
 */
public enum OperadorLogico implements Operador<Condicao>{
    E("E", "E"),
    OU("Ou", "Ou")
    ;

    private String nome;
    private String representacao;

    private OperadorLogico(String nome, String representacao) {
        this.nome = nome;
        this.representacao = representacao;
    }
    
    

    @Override
    public boolean resolverOperacao(Condicao valor1, Condicao valor2, Musica musica) {
        if (this==E) {
            return valor1.resolver(musica) && valor2.resolver(musica);
        }else{
            return valor1.resolver(musica) || valor2.resolver(musica);
        }
    }

    @Override
    public String getRepresentacao() {
        return representacao;
    }

    @Override
    public String toString() {
        return nome;
    }
    
    

  
   
    
  
}
