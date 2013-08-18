package com.config;

import com.musica.Musica;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author rudieri
 */
public class Configuracao<E> {

    private E valor;
    private final ArrayList<ConfiguracaoListener> listeners = new ArrayList<ConfiguracaoListener>(3);

    public Configuracao() {
    }

    public Configuracao(E valorPadrao) {
        this.valor = valorPadrao;
    }

    public E getValor() {
        return valor;
    }

    public void setValor(E valor, boolean gravarAgora) {
        setValor(valor);
        if (gravarAgora) {
            Configuracoes.gravar();
        }
    }

    public void setValor(E valor) {
        this.valor = valor;
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).configuracaoModificada(this);
        }
    }

    protected String converteValorParaGravavel() {
        if (valor == null) {
            return null;
        } else if (valor instanceof ArrayList) {
            return ((ArrayList) valor).toString().replaceAll("[\\[\\]]", "");
        } else if (valor instanceof Musica) {
            return String.valueOf(((Musica) valor).getId());
        } else if (valor instanceof Enum) {
            return ((Enum) valor).name();
        } else if (valor instanceof Rectangle) {
            Rectangle rec = (Rectangle) valor;
            return "[" + rec.x + "," + rec.y + "," + rec.width + "," + rec.height + "]";
        } else if (valor instanceof String || valor instanceof Long || valor instanceof Double || valor instanceof Boolean) {
            return valor.toString();
        } else {
            throw new UnsupportedOperationException("Classe " + valor.getClass().toString() + " não tratada.");
        }
    }

    public void addConfiguracaoListener(ConfiguracaoListener configuracaoListener) {
        listeners.add(configuracaoListener);
    }
}
