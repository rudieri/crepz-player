/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente.condicao;

import com.musica.Musica;
import com.utils.campo.Campo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class ValorCondicao {
    private Object valor;
    private Campo campo;

    public ValorCondicao(String valor) {
        this.valor = valor;
    }
    public ValorCondicao(Byte valor) {
        this.valor = valor;
    }
    public ValorCondicao(Short valor) {
        this.valor = valor;
    }
    public ValorCondicao(Integer valor) {
        this.valor = valor;
    }
    public ValorCondicao(Long valor) {
        this.valor = valor;
    }

    public ValorCondicao(Campo campo) {
            this.campo = campo;
    }
    
    public Object getValor(Musica musica){
        if (valor != null) {
            return valor;
        }else{
            try {
                return campo.getField().get(musica);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ValorCondicao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ValorCondicao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
            
    }
    
    public TipoValorCondicao getTipoValorCondicao(){
        if (valor == null) {
            return TipoValorCondicao.CAMPO;
        }else if(valor instanceof String){
            return TipoValorCondicao.STRING;
        }else if(valor instanceof Byte){
            return TipoValorCondicao.BYTE;
        }else if(valor instanceof Short){
            return TipoValorCondicao.SHORT;
        }else if(valor instanceof Long){
            return TipoValorCondicao.LONG;
        }else{
            return TipoValorCondicao.INTEGER;
        }
    }

    @Override
    public String toString() {
        if (valor == null) {
            return campo.toString();
        }else{
            return valor.toString();
        }
    }
    public String toBD() {
        if (valor == null) {
            return campo.getField().getName();
        }else{
            return valor.toString();
        }
    }

    public Campo getCampo() {
        return campo;
    }
    
    
    
    
}
