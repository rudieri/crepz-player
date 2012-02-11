/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import java.lang.reflect.Method;

/**
 *
 * @author rudieri
 */
public class Acao {
    private Object alvo;
    private Method acao;
    private final boolean contemParametros;

    public Acao(Object alvo, Method acao) {
        this(alvo, acao, false);
    }
    public Acao(Object alvo, Method acao, boolean contemParametros) {
        this.alvo = alvo;
        this.acao = acao;
        this.contemParametros = contemParametros;
    }

    public Method getAcao() {
        return acao;
    }

    public Object getAlvo() {
        return alvo;
    }

    public boolean isContemParametros() {
        return contemParametros;
    }

}
